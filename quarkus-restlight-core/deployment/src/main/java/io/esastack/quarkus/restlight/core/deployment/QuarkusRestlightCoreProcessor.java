/*
 * Copyright 2021 OPPO ESA Stack Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.esastack.quarkus.restlight.core.deployment;

import com.fasterxml.jackson.databind.ext.Java7HandlersImpl;
import com.fasterxml.jackson.databind.ext.Java7SupportImpl;
import esa.commons.Platforms;
import io.esastack.commons.net.buffer.BufferUtil;
import io.esastack.commons.net.netty.buffer.NettyBufferAllocatorImpl;
import io.esastack.commons.net.netty.buffer.NettyBufferProvider;
import io.esastack.commons.net.netty.buffer.UnpooledNettyBufferAllocator;
import io.esastack.commons.net.netty.http.NettyCookieProvider;
import io.esastack.httpserver.transport.NioTransport;
import io.esastack.quarkus.restlight.commons.SpiUtil;
import io.esastack.restlight.core.Restlight;
import io.esastack.restlight.core.method.DefaultResolvableParamPredicate;
import io.esastack.restlight.core.resolver.param.HttpRequestParamResolverFactory;
import io.esastack.restlight.core.resolver.param.HttpResponseParamResolverFactory;
import io.esastack.restlight.core.resolver.rspentity.ByteArrayEntityResolverFactory;
import io.esastack.restlight.core.resolver.rspentity.ByteBufEntityResolverFactory;
import io.esastack.restlight.core.resolver.rspentity.CharSequenceEntityResolverFactory;
import io.esastack.restlight.core.resolver.rspentity.PrimitiveEntityResolverFactory;
import io.esastack.restlight.core.serialize.JacksonSerializer;
import io.esastack.restlight.core.spi.impl.CompletableFutureTransferFactory;
import io.esastack.restlight.core.spi.impl.DefaultFutureTransferFactory;
import io.esastack.restlight.core.spi.impl.HandlerFactoryProviderImpl;
import io.esastack.restlight.core.spi.impl.HandlerLocatorResolverFactory;
import io.esastack.restlight.core.spi.impl.HandlerMethodResolverFactory;
import io.esastack.restlight.core.spi.impl.HandlersParamResolverProvider;
import io.esastack.restlight.core.spi.impl.JacksonDefaultSerializerFactory;
import io.esastack.restlight.core.spi.impl.ListenableFutureTransferFactory;
import io.esastack.restlight.core.spi.impl.QueryBeanParamResolverProvider;
import io.esastack.restlight.core.spi.impl.RequestBeanParamResolverProvider;
import io.esastack.restlight.core.spi.impl.ResponseEntityWriterFilterFactory;
import io.esastack.restlight.server.Restlite;
import io.esastack.restlight.server.spi.impl.RouteFailureExceptionHandlerFactory;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.deployment.pkg.builditem.UberJarMergedResourceBuildItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;

class QuarkusRestlightCoreProcessor {

    private static final String FEATURE = "quarkus-restlight-core";
    private static final String ESA_SPI_DIR_PATH = "META-INF/esa";
    private static final String ESA_INTERNAL_SPI_DIR_PATH = "META-INF/esa/internal";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    List<UberJarMergedResourceBuildItem> mergedResources() throws IOException {
        List<UberJarMergedResourceBuildItem> mergedResources = new LinkedList<>();
        List<String> spiPaths = SpiUtil.getAllSpiPaths(Restlight.class);
        for (String spiPath : spiPaths) {
            mergedResources.add(new UberJarMergedResourceBuildItem(spiPath));
        }
        return mergedResources;
    }

    @BuildStep
    List<NativeImageResourceBuildItem> nativeImageResourceBuildItems() {
        List<NativeImageResourceBuildItem> resources = new LinkedList<>();
        resources.add(new NativeImageResourceBuildItem(
                "META-INF/native-image/io.esastack/commons-net-netty/resource-config.json"));

        resources.add(new NativeImageResourceBuildItem(
                "META-INF/native-image/io.esastack/restlight-common/resource-config.json"));

        resources.add(new NativeImageResourceBuildItem(
                "META-INF/native-image/io.esastack/restlight-core/resource-config.json"));

        resources.add(new NativeImageResourceBuildItem(
                "META-INF/native-image/io.esastack/restlight-server/resource-config.json"));

        return resources;
    }

    @BuildStep
    List<ReflectiveClassBuildItem> reflections() throws IOException {
        List<ReflectiveClassBuildItem> reflections = new LinkedList<>();

        // reflection-configs from commons-net-netty.
        reflections.add(new ReflectiveClassBuildItem(false, false, NettyBufferProvider.class));
        reflections.add(new ReflectiveClassBuildItem(false, false, UnpooledNettyBufferAllocator.class));
        reflections.add(new ReflectiveClassBuildItem(false, false, NettyCookieProvider.class));

        // reflection-configs from esa-httpserver.
        reflections.add(new ReflectiveClassBuildItem(true, false,
                "io.esastack.httpserver.impl.Http1Handler"));
        reflections.add(new ReflectiveClassBuildItem(true, true,
                "io.esastack.httpserver.impl.HttpServerChannelInitializr"));
        reflections.add(new ReflectiveClassBuildItem(true, true,
                "io.esastack.httpserver.impl.OnChannelActiveHandler"));
        reflections.add(new ReflectiveClassBuildItem(true, true,
                "io.esastack.httpserver.impl.RequestDecoder"));

        // reflection-configs from restlight-server.
        final JarFile restlightServerJar = new JarFile(Restlite.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        InputStream reflectionStream = restlightServerJar.getInputStream(restlightServerJar.getEntry("META-INF/native-image/io.esastack/restlight-server/reflection-config.json"));
        new JacksonSerializer().deserialize(reflectionStream.readAllBytes(), );

        reflections.add(new ReflectiveClassBuildItem(false, false, RouteFailureExceptionHandlerFactory.class));

        // reflection-configs from restlight-core.
        final File jarFile = new File(Restlight.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        final JarFile jar = new JarFile(jarFile);

        reflections.add(new ReflectiveClassBuildItem(false, false, Java7HandlersImpl.class));
        reflections.add(new ReflectiveClassBuildItem(false, false, Java7SupportImpl.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                DefaultResolvableParamPredicate.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                HttpRequestParamResolverFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                HttpResponseParamResolverFactory.class));

        reflections.add(new ReflectiveClassBuildItem(false, false,
                ByteArrayEntityResolverFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                ByteBufEntityResolverFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                CharSequenceEntityResolverFactory.class));

        reflections.add(new ReflectiveClassBuildItem(false, false,
                PrimitiveEntityResolverFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                CompletableFutureTransferFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                DefaultFutureTransferFactory.class));

        reflections.add(new ReflectiveClassBuildItem(false, false,
                HandlerFactoryProviderImpl.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                HandlerLocatorResolverFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                HandlerMethodResolverFactory.class));

        reflections.add(new ReflectiveClassBuildItem(false, false,
                HandlersParamResolverProvider.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                JacksonDefaultSerializerFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                ListenableFutureTransferFactory.class));

        reflections.add(new ReflectiveClassBuildItem(false, false,
                QueryBeanParamResolverProvider.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                RequestBeanParamResolverProvider.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                ResponseEntityWriterFilterFactory.class));

        return reflections;
    }

    @BuildStep
    List<RuntimeInitializedClassBuildItem> runtimeInitializedClass() {
        List<RuntimeInitializedClassBuildItem> runtimeInitializedClasses = new LinkedList<>();

        // runtime-initialized build item from esa-commons.
        runtimeInitializedClasses.add(new RuntimeInitializedClassBuildItem(
                "esa.commons.concurrent.StripedBuffer"));
        runtimeInitializedClasses.add(new RuntimeInitializedClassBuildItem(Platforms.class.getName()));

        // runtime-initialized build item from commons-net-core.
        runtimeInitializedClasses.add(new RuntimeInitializedClassBuildItem(BufferUtil.class.getName()));

        // runtime-initialized build item from commons-net-netty.
        runtimeInitializedClasses.add(new RuntimeInitializedClassBuildItem(NettyBufferAllocatorImpl.class.getName()));

        // runtime-initialized build item from esa-httpserver.
        runtimeInitializedClasses.add(new RuntimeInitializedClassBuildItem(
                "io.esastack.httpserver.impl.H2cDetector"));
        runtimeInitializedClasses.add(new RuntimeInitializedClassBuildItem(NioTransport.class.getName()));

        return runtimeInitializedClasses;
    }
}
