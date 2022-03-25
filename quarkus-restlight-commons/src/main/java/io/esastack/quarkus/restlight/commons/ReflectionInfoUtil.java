package io.esastack.quarkus.restlight.commons;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public final class ReflectionInfoUtil {

    private static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
    private static ObjectMapper DEFAULT_OBJECT_MAPPER = initDefaultMapper();
    private static final JavaType REFLECTION_CONFIG_TYPE = DEFAULT_OBJECT_MAPPER
            .getTypeFactory()
            .constructType(new TypeReference<List<ReflectedClassInfo>>() {
            }.getType());

    private ReflectionInfoUtil() {
    }

    public static List<ReflectedClassInfo> loadReflections(String reflectionsFrom,
                                                           Class<?> classInJar) throws IOException {
        List<ReflectedClassInfo> reflectedInfos = loadReflectionsByJson(classInJar,
                "META-INF/native-image/io.esastack/" + reflectionsFrom + "/reflection-config.json");
        reflectedInfos.addAll(loadReflectionsBySpis(classInJar, SpiUtil.getAllSpiPaths(classInJar)));

        return reflectedInfos;
    }

    private static List<ReflectedClassInfo> loadReflectionsBySpis(Class<?> classInJar, List<String> spiPaths)
            throws IOException {
        checkNotNull(classInJar, "classInJar");
        checkNotNull(spiPaths, "spiPaths");

        String jarPath = classInJar.getProtectionDomain().getCodeSource().getLocation().getPath();
        try (JarFile jar = new JarFile(jarPath)) {
            List<ReflectedClassInfo> reflectionConfig = new ArrayList<>(12);

            for (String spiPath : spiPaths) {
                ZipEntry entry = jar.getEntry(spiPath);
                if (entry == null) {
                    throw new IllegalStateException(spiPath + " is not exist in jar(" + jarPath + ")!");
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(entry)))) {
                    String className = reader.readLine();
                    while (className != null) {
                        ReflectedClassInfo classInfo = new ReflectedClassInfo(className, null);
                        reflectionConfig.add(classInfo);
                        className = reader.readLine();
                    }
                }
            }
            return reflectionConfig;
        }
    }

    private static List<ReflectedClassInfo> loadReflectionsByJson(Class<?> classInJar, String reflectionConfigPath)
            throws IOException {
        checkNotNull(classInJar, "classInJar");
        checkNotNull(reflectionConfigPath, "reflectionConfigPath");

        String jarPath = classInJar.getProtectionDomain().getCodeSource().getLocation().getPath();
        try (JarFile jar = new JarFile(jarPath)) {
            ZipEntry entry = jar.getEntry(reflectionConfigPath);
            if (entry == null) {
                throw new IllegalStateException(reflectionConfigPath + " is not exist in jar(" + jarPath + ")!");
            }

            try (InputStream reflectionStream = jar.getInputStream(entry)) {
                return DEFAULT_OBJECT_MAPPER.readValue(reflectionStream.readAllBytes(),
                        REFLECTION_CONFIG_TYPE);
            }
        }
    }

    private static ObjectMapper initDefaultMapper() {
        if (DEFAULT_OBJECT_MAPPER == null) {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(new SimpleDateFormat(yyyyMMddHHmmss));
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            DEFAULT_OBJECT_MAPPER = objectMapper;
        }
        return DEFAULT_OBJECT_MAPPER;
    }

    private static void checkNotNull(Object obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
    }
}
