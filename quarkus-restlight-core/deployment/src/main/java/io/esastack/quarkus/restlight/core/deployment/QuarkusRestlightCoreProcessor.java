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

import esa.commons.Platforms;
import esa.commons.logging.Logger;
import esa.commons.logging.LoggerFactory;
import io.esastack.commons.net.buffer.BufferUtil;
import io.esastack.commons.net.netty.buffer.NettyBufferAllocatorImpl;
import io.esastack.commons.net.netty.buffer.UnpooledNettyBufferAllocator;
import io.esastack.httpserver.HttpServer;
import io.esastack.httpserver.transport.NioTransport;
import io.esastack.quarkus.restlight.commons.ReflectedClassInfo;
import io.esastack.quarkus.restlight.commons.ReflectionInfoUtil;
import io.esastack.quarkus.restlight.commons.SpiUtil;
import io.esastack.restlight.core.Restlight;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.deployment.pkg.builditem.UberJarMergedResourceBuildItem;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class QuarkusRestlightCoreProcessor {

    private static final String FEATURE = "quarkus-restlight-core";
    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusRestlightCoreProcessor.class);

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    List<UberJarMergedResourceBuildItem> mergedResources() throws IOException {
        List<UberJarMergedResourceBuildItem> mergedResources = new LinkedList<>();
        Set<String> spiPathSet = new HashSet<>();
        spiPathSet.addAll(SpiUtil.getAllSpiPaths(Restlight.class));
        spiPathSet.addAll(SpiUtil.getAllSpiPaths(UnpooledNettyBufferAllocator.class));
        for (String spiPath : spiPathSet) {
            LOGGER.info("Add mergedResources:" + spiPath);
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
    List<ReflectiveClassBuildItem> reflections() throws IOException, ClassNotFoundException {
        Set<String> classNameSet = new HashSet<>();

        // reflection-configs from commons-net-netty.
        for (ReflectedClassInfo classInfo : ReflectionInfoUtil.loadReflections("commons-net-netty",
                UnpooledNettyBufferAllocator.class)) {
            classNameSet.add(classInfo.getName());
        }

        // reflection-configs from esa-httpserver.
        for (ReflectedClassInfo classInfo : ReflectionInfoUtil.loadReflections("httpserver",
                HttpServer.class)) {
            classNameSet.add(classInfo.getName());
        }

        // reflection-configs from restlight-core.
        for (ReflectedClassInfo classInfo : ReflectionInfoUtil.loadReflections("restlight-core",
                Restlight.class)) {
            classNameSet.add(classInfo.getName());
        }

        List<ReflectiveClassBuildItem> reflections = new LinkedList<>();
        for (String className : classNameSet) {
            LOGGER.info("Load refection(" + className + ") when build quarkus-restlight-core");
            reflections.add(new ReflectiveClassBuildItem(true, true, Class.forName(className)));
        }
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
