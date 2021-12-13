package io.esastack.quarkus.restlight.jaxrs.deployment;

import io.esastack.restlight.jaxrs.resolver.param.AsyncResponseParamResolver;
import io.esastack.restlight.jaxrs.resolver.param.CookieValueParamResolver;
import io.esastack.restlight.jaxrs.resolver.param.DefaultValueParamResolver;
import io.esastack.restlight.jaxrs.resolver.param.FormParamResolver;
import io.esastack.restlight.jaxrs.resolver.param.HttpHeadersParamResolver;
import io.esastack.restlight.jaxrs.resolver.param.MatrixVariableParamResolver;
import io.esastack.restlight.jaxrs.resolver.param.PathParamResolver;
import io.esastack.restlight.jaxrs.resolver.param.QueryParamResolver;
import io.esastack.restlight.jaxrs.resolver.param.RequestHeaderParamResolver;
import io.esastack.restlight.jaxrs.resolver.param.RequestParamResolver;
import io.esastack.restlight.jaxrs.resolver.param.SecurityContextParamResolver;
import io.esastack.restlight.jaxrs.resolver.param.UriInfoParamResolver;
import io.esastack.restlight.jaxrs.resolver.reqentity.FixedRequestEntityResolverFactoryImpl;
import io.esastack.restlight.jaxrs.resolver.rspentity.FixedResponseEntityResolverFactory;
import io.esastack.restlight.jaxrs.spi.AsyncResponseTransferFactory;
import io.esastack.restlight.jaxrs.spi.BeanParamResolverProvider;
import io.esastack.restlight.jaxrs.spi.FlexibleRequestEntityResolverProvider;
import io.esastack.restlight.jaxrs.spi.FlexibleResponseEntityResolverProvider;
import io.esastack.restlight.jaxrs.spi.JaxrsExtensionsHandlerFactory;
import io.esastack.restlight.jaxrs.spi.JaxrsHandlerFactoryProvider;
import io.esastack.restlight.jaxrs.spi.JaxrsMappingLocatorFactory;
import io.esastack.restlight.jaxrs.spi.JaxrsResolvableParamPredicate;
import io.esastack.restlight.jaxrs.spi.JaxrsResponseAdapterFactory;
import io.esastack.restlight.jaxrs.spi.JaxrsRouteMethodLocatorFactory;
import io.esastack.restlight.jaxrs.spi.RouteTrackingFilterFactory;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.pkg.builditem.UberJarMergedResourceBuildItem;

import java.util.LinkedList;
import java.util.List;

class QuarkusRestlightJaxrsProcessor {

    private static final String FEATURE = "quarkus-restlight-jaxrs";

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
                "/io.esastack.restlight.core.resolver.ResponseEntityResolverAdviceFactory"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/io.esastack.restlight.core.resolver.ResponseEntityResolverFactory"));

        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/io.esastack.restlight.core.spi.ParamResolverProvider"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/io.esastack.restlight.core.spi.RequestEntityResolverProvider"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/io.esastack.restlight.core.spi.ResponseEntityResolverProvider"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/io.esastack.restlight.core.spi.RouteFilterFactory"));


        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/internal/io.esastack.restlight.core.method.ResolvableParamPredicate"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/internal/io.esastack.restlight.core.spi.ExtensionsHandlerFactory"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/internal/io.esastack.restlight.core.spi.FutureTransferFactory"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/internal/io.esastack.restlight.core.spi.HandlerFactoryProvider"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/internal/io.esastack.restlight.core.spi.MappingLocatorFactory"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/internal/io.esastack.restlight.core.spi.RouteMethodLocatorFactory"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/internal/io.esastack.restlight.spring.spi.ControllerLocator"));
        mergedResources.add(new UberJarMergedResourceBuildItem("META-INF/esa" +
                "/internal/io.esastack.restlight.spring.spi.ExtensionLocator"));

        return mergedResources;
    }

    @BuildStep
    List<NativeImageResourceBuildItem> nativeImageResourceBuildItems() {
        List<NativeImageResourceBuildItem> resources = new LinkedList<>();
        resources.add(new NativeImageResourceBuildItem(
                "META-INF/native-image/io.esastack/restlight-jaxrs-provider/resource-config.json"));
        return resources;
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
        reflections.add(new ReflectiveClassBuildItem(false, false,
                AsyncResponseTransferFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                CookieValueParamResolver.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                DefaultValueParamResolver.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                FormParamResolver.class));

        reflections.add(new ReflectiveClassBuildItem(false, false,
                RequestHeaderParamResolver.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                MatrixVariableParamResolver.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                PathParamResolver.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                QueryParamResolver.class));

        reflections.add(new ReflectiveClassBuildItem(false, false,
                HttpHeadersParamResolver.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                SecurityContextParamResolver.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                RequestParamResolver.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                UriInfoParamResolver.class));

        reflections.add(new ReflectiveClassBuildItem(false, false,
                AsyncResponseParamResolver.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                JaxrsHandlerFactoryProvider.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                JaxrsResolvableParamPredicate.class));

        reflections.add(new ReflectiveClassBuildItem(false, false,
                FixedResponseEntityResolverFactory.class));
        reflections.add(new ReflectiveClassBuildItem(false, false,
                BeanParamResolverProvider.class));

        return reflections;
    }
}
