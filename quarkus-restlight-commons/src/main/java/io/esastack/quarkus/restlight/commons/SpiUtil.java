package io.esastack.quarkus.restlight.commons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class SpiUtil {

    private static final String ESA_SPI_DIR_PATH = "META-INF/esa/";
    private static final String ESA_INTERNAL_SPI_DIR_PATH = "META-INF/esa/internal/";
    private static final String PACKAGE_NAME_OF_RESTLIGHT_SPRING = "io.esastack.restlight.spring.spi";

    private SpiUtil() {
    }

    public static List<String> getAllSpiPaths(Class<?> classInJar) throws IOException {
        checkNotNull(classInJar, "classInJar");
        final JarFile jar = new JarFile(classInJar.getProtectionDomain().getCodeSource().getLocation().getPath());
        final Enumeration<JarEntry> entries = jar.entries(); //gives all entries in jar
        final List<String> spiPaths = new ArrayList<>(12);
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            final String name = entry.getName();
            if ((name.startsWith(ESA_SPI_DIR_PATH) || name.startsWith(ESA_INTERNAL_SPI_DIR_PATH))
                    && !(ESA_SPI_DIR_PATH.equals(name) || ESA_INTERNAL_SPI_DIR_PATH.equals(name))
                    && !(name.contains(PACKAGE_NAME_OF_RESTLIGHT_SPRING))
            ) { //filter according to the path
                spiPaths.add(name);
            }
        }
        jar.close();
        return spiPaths;
    }

    private static void checkNotNull(Object obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
    }
}
