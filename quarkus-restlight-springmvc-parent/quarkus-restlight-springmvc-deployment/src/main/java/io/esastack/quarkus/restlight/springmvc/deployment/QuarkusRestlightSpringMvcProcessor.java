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

import java.util.LinkedList;
import java.util.List;

class QuarkusRestlightSpringMvcProcessor {

    private static final String FEATURE = "quarkus-restlight-springmvc";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
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
