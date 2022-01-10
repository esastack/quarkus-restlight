package io.esastack.quarkus.restlight.commons;

import java.util.List;

public final class ReflectedClassInfo {

    private String name;
    private List<MethodInfo> methods;

    public ReflectedClassInfo() {
    }

    public ReflectedClassInfo(String name, List<MethodInfo> methods) {
        this.name = name;
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MethodInfo> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodInfo> methods) {
        this.methods = methods;
    }

    public static class MethodInfo {
        private String name;
        private List<String> parameterTypes;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getParameterTypes() {
            return parameterTypes;
        }

        public void setParameterTypes(List<String> parameterTypes) {
            this.parameterTypes = parameterTypes;
        }
    }
}
