package io.esastack.quarkus.restlight.jaxrs.deployment;

import io.esastack.restlight.jaxrs.resolver.reqentity.FixedRequestEntityResolverFactoryImpl;
import io.esastack.restlight.jaxrs.spi.FlexibleRequestEntityResolverProvider;
import io.esastack.restlight.jaxrs.spi.FlexibleResponseEntityResolverProvider;
import io.esastack.restlight.jaxrs.spi.JaxrsExtensionsHandlerFactory;
import io.esastack.restlight.jaxrs.spi.JaxrsMappingLocatorFactory;
import io.esastack.restlight.jaxrs.spi.JaxrsResponseAdapterFactory;
import io.esastack.restlight.jaxrs.spi.JaxrsRouteMethodLocatorFactory;
import io.esastack.restlight.jaxrs.spi.RouteTrackingFilterFactory;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

import java.util.LinkedList;
import java.util.List;

class QuarkusRestlightJaxrsProcessor {

    private static final String FEATURE = "quarkus-restlight-jaxrs";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    List<ReflectiveClassBuildItem> reflections() {
        List<ReflectiveClassBuildItem> reflections = new LinkedList<>();
        reflections.add(new ReflectiveClassBuildItem(false, false,
                FixedRequestEntityResolverFactoryImpl.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                FlexibleRequestEntityResolverProvider.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                FlexibleResponseEntityResolverProvider.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                JaxrsExtensionsHandlerFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                JaxrsMappingLocatorFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                JaxrsResponseAdapterFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                JaxrsRouteMethodLocatorFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                RouteTrackingFilterFactory.class));

        return reflections;
    }
}
