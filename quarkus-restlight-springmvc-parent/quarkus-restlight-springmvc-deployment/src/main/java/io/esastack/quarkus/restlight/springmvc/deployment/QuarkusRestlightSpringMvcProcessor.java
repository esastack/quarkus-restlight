package io.esastack.quarkus.restlight.springmvc.deployment;

import io.esastack.restlight.springmvc.spi.SpringMvcExceptionResolverFactoryProvider;
import io.esastack.restlight.springmvc.spi.core.SpringMvcExceptionHandlerFactory;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
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
    List<ReflectiveClassBuildItem> reflections() {
        List<ReflectiveClassBuildItem> reflections = new LinkedList<>();
        reflections.add(new ReflectiveClassBuildItem(false, false,
                SpringMvcExceptionResolverFactoryProvider.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                SpringMvcExceptionHandlerFactory.class));

        return reflections;
    }

}
