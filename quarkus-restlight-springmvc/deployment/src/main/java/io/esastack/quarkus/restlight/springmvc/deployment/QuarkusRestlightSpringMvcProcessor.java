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
package io.esastack.quarkus.restlight.springmvc.deployment;

import io.esastack.restlight.springmvc.reqentity.FixedRequestEntityResolverFactoryImpl;
import io.esastack.restlight.springmvc.resolver.param.CookieValueParamResolver;
import io.esastack.restlight.springmvc.resolver.param.MatrixVariableParamResolver;
import io.esastack.restlight.springmvc.resolver.param.PathVariableParamResolver;
import io.esastack.restlight.springmvc.resolver.param.RequestAttributeParamResolver;
import io.esastack.restlight.springmvc.resolver.param.RequestHeaderParamResolver;
import io.esastack.restlight.springmvc.resolver.param.RequestParamResolver;
import io.esastack.restlight.springmvc.resolver.rspentity.FixedResponseEntityResolverFactory;
import io.esastack.restlight.springmvc.resolver.rspentity.ResponseStatusEntityResolverFactory;
import io.esastack.restlight.springmvc.spi.FlexibleRequestEntityResolverProvider;
import io.esastack.restlight.springmvc.spi.FlexibleResponseEntityResolverProvider;
import io.esastack.restlight.springmvc.spi.SpringMvcExceptionResolverFactoryProvider;
import io.esastack.restlight.springmvc.spi.SpringMvcMappingLocatorFactory;
import io.esastack.restlight.springmvc.spi.SpringMvcRouteMethodLocatorFactory;
import io.esastack.restlight.springmvc.spi.core.SpringMvcExceptionHandlerFactory;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.pkg.builditem.UberJarMergedResourceBuildItem;

import java.util.LinkedList;
import java.util.List;

class QuarkusRestlightSpringMvcProcessor {

    private static final String FEATURE = "quarkus-restlight-springmvc";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    List<UberJarMergedResourceBuildItem> mergedResources() {
        List<UberJarMergedResourceBuildItem> mergedResources = new LinkedList<>();
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/io.esastack.restlight.core.resolver.ParamResolverFactory"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/io.esastack.restlight.core.resolver.RequestEntityResolverFactory"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/io.esastack.restlight.core.resolver.ResponseEntityResolverFactory"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/io.esastack.restlight.core.spi.ExceptionHandlerFactory"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/io.esastack.restlight.core.spi.RequestEntityResolverProvider"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/io.esastack.restlight.core.spi.ResponseEntityResolverProvider"));

        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/internal/io.esastack.restlight.core.spi.ExceptionResolverFactoryProvider"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/internal/io.esastack.restlight.core.spi.MappingLocatorFactory"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/internal/io.esastack.restlight.core.spi.RouteMethodLocatorFactory"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/internal/io.esastack.restlight.spring.spi.AdviceLocator"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/internal/io.esastack.restlight.spring.spi.ControllerLocator"));

        return mergedResources;
    }

    @BuildStep
    List<NativeImageResourceBuildItem> nativeImageResourceBuildItems() {
        List<NativeImageResourceBuildItem> resources = new LinkedList<>();
        resources.add(new NativeImageResourceBuildItem(
                "META-INF/native-image/io.esastack/restlight-springmvc-provider/resource-config.json"));
        return resources;
    }

    @BuildStep
    List<ReflectiveClassBuildItem> reflections() {
        List<ReflectiveClassBuildItem> reflections = new LinkedList<>();
        reflections.add(new ReflectiveClassBuildItem(false, false,
                SpringMvcMappingLocatorFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                SpringMvcRouteMethodLocatorFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                CookieValueParamResolver.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                MatrixVariableParamResolver.class));

        reflections.add(new ReflectiveClassBuildItem(false, false,
                PathVariableParamResolver.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                RequestAttributeParamResolver.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                RequestHeaderParamResolver.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                RequestParamResolver.class));

        reflections.add(new ReflectiveClassBuildItem(false, false,
                FixedRequestEntityResolverFactoryImpl.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                FixedResponseEntityResolverFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                ResponseStatusEntityResolverFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                FlexibleRequestEntityResolverProvider.class));

        reflections.add(new ReflectiveClassBuildItem(false, false,
                FlexibleResponseEntityResolverProvider.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                SpringMvcExceptionResolverFactoryProvider.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                SpringMvcExceptionHandlerFactory.class));

        return reflections;
    }

}
