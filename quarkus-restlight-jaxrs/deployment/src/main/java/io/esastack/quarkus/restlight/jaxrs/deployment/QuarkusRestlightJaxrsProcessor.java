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
package io.esastack.quarkus.restlight.jaxrs.deployment;

import esa.commons.logging.Logger;
import esa.commons.logging.LoggerFactory;
import io.esastack.quarkus.restlight.commons.ReflectedClassInfo;
import io.esastack.quarkus.restlight.commons.ReflectionInfoUtil;
import io.esastack.quarkus.restlight.commons.SpiUtil;
import io.esastack.restlight.jaxrs.impl.JaxrsContextUtils;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.pkg.builditem.UberJarMergedResourceBuildItem;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

class QuarkusRestlightJaxrsProcessor {

    private static final String FEATURE = "quarkus-restlight-jaxrs";
    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusRestlightJaxrsProcessor.class);

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    List<UberJarMergedResourceBuildItem> mergedResources() throws IOException {
        List<UberJarMergedResourceBuildItem> mergedResources = new LinkedList<>();
        List<String> spiPaths = SpiUtil.getAllSpiPaths(JaxrsContextUtils.class);
        for (String spiPath : spiPaths) {
            LOGGER.info("Add mergedResources:" + spiPath);
            mergedResources.add(new UberJarMergedResourceBuildItem(spiPath));
        }

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
    List<ReflectiveClassBuildItem> reflections() throws ClassNotFoundException, IOException {
        List<ReflectiveClassBuildItem> reflections = new LinkedList<>();
        List<ReflectedClassInfo> reflectedInfos = ReflectionInfoUtil.getReflectionConfig(
                JaxrsContextUtils.class, "META-INF/native-image/io.esastack/" +
                        "restlight-jaxrs-provider/reflection-config.json");
        for (ReflectedClassInfo reflectedInfo : reflectedInfos) {
            String className = reflectedInfo.getName();
            LOGGER.info("Load reflection-info(" + className + ") from restlight-jaxrs-provider.");
            reflections.add(new ReflectiveClassBuildItem(false,
                    false,
                    Class.forName(className)));
        }

        return reflections;
    }
}
