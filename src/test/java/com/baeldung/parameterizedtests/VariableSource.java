package com.baeldung.parameterizedtests;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.support.AnnotationConsumer;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.stream.Stream;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(VariableArgumentsProvider.class)
public @interface VariableSource {

    /**
     * The name of the static variable
     */
    String value();
}

class VariableArgumentsProvider
        implements ArgumentsProvider, AnnotationConsumer<VariableSource> {

    private String variableName;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return context.getTestClass()
                .map(this::getField)
                .map(this::getValue)
                .orElseThrow(() ->
                        new IllegalArgumentException("Failed to load test arguments"));
    }

    @Override
    public void accept(VariableSource variableSource) {
        variableName = variableSource.value();
    }

    private Field getField(Class<?> clazz) {
        try {
            return clazz.getDeclaredField(variableName);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Stream<Arguments> getValue(Field field) {
        Object value = null;
        try {
            value = field.get(null);
        } catch (Exception ignored) {}

        return value == null ? null : (Stream<Arguments>) value;
    }
}