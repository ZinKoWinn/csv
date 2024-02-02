package io.github.zinkowinn.csv.utils;

import io.github.zinkowinn.csv.exceptions.IllegalCastException;
import io.github.zinkowinn.csv.exceptions.InstantiationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A utility class for performing reflection-related operations, such as finding fields
 * and retrieving values from objects using reflection and getter methods.
 *
 * @author Zin Ko Winn
 * @since 1.0.0
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * Finds a field within a class hierarchy based on the provided field name.
     *
     * @param object    The object (or class name) to start searching from.
     * @param fieldName The name of the field to find. Supports nested fields using dot notation.
     * @return The Field object representing the found field, or null if not found.
     */
    public static Field findFieldByName(Object object, String fieldName) {
        if (!fieldName.isEmpty()) {
            try {
                Class<?> clazz = (Class<?>) object;
                String[] fieldNames = fieldName.split("\\.");
                Field field = null;

                for (String fName : fieldNames) {
                    field = clazz.getDeclaredField(fName);
                    clazz = field.getType();
                }

                return field;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Retrieves the value of a nested field from an object using reflection and getter methods.
     *
     * @param item      The object from which to retrieve the field value.
     * @param fieldName The name of the nested field to retrieve. Supports dot notation for nested fields.
     * @return The value of the nested field, or null if any exception occurs during retrieval.
     */
    public static Object getValueByFieldName(Object item, String fieldName) {
        try {
            Class<?> itemClass = item.getClass();
            Object fieldValue = item;

            String[] fieldNames = fieldName.split("\\.");

            for (String nestedFieldName : fieldNames) {
                Method getter = findGetterMethod(itemClass, nestedFieldName);
                if (getter == null) {
                    throw new NoSuchMethodException("Getter method not found for field: " + nestedFieldName);
                }
                fieldValue = getter.invoke(fieldValue);
                itemClass = getter.getReturnType();
            }

            return fieldValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Recursively finds a field within a class hierarchy based on the provided field name.
     *
     * @param clazz     The class to start searching from.
     * @param fieldName The name of the field to find.
     * @return The Field object representing the found field, or null if not found.
     */
    private static Method findGetterMethod(Class<?> clazz, String fieldName) {
        String getterName = "get" + capitalize(fieldName);
        try {
            return clazz.getMethod(getterName);
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null) {
                return findGetterMethod(clazz.getSuperclass(), fieldName);
            }
            return null;
        }
    }


    private static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return getField(clazz.getSuperclass(), fieldName);
            }
            return null;
        }
    }

    /**
     * Retrieves the names of fields declared in a given class.
     *
     * @param data The class for which to retrieve field names.
     * @return A List of field names declared in the class.
     */
    public static List<Field> getFields(Class<?> data) {
        return Arrays.stream(data.getDeclaredFields()).collect(Collectors.toList());
    }

    /**
     * Retrieves fields from a class based on their names.
     *
     * @param clazz      The class to retrieve fields from.
     * @param fieldNames An array of field names to retrieve.
     * @return A list of fields with the specified names that exist in the class.
     */
    public static List<Field> getFieldsByNamesFromClass(Class<?> clazz, String[] fieldNames) {
        return Arrays.stream(fieldNames)
                .map(fieldName -> getFieldIfExists(clazz, fieldName))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Attempts to retrieve a field from a class if it exists.
     *
     * @param clazz     The class to retrieve the field from.
     * @param fieldName The name of the field to retrieve.
     * @return The Field object if it exists in the class; otherwise, null.
     */
    private static Field getFieldIfExists(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * Creates a new instance of a given class using its default constructor.
     *
     * @param <T>  The type of the object to be created.
     * @param type The Class object representing the type to instantiate.
     * @return A new instance of the specified class.
     * @throws InstantiationException If an error occurs while creating the instance.
     */

    public static <T> T newInstanceOf(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (Exception ex) {
            throw new InstantiationException("Cannot create a new instance of " + type.getName(), ex);
        }
    }


    /**
     * Sets the value of a specified field in an object instance.
     *
     * @param fieldName The name of the field to set.
     * @param o         The new value to assign to the field.
     * @param instance  The instance containing the field to be updated.
     * @throws IllegalCastException If a casting issue occurs while setting the field.
     */
    public static void setFieldData(String fieldName, Object o, Object instance) {
        Field field = getFieldIfExists(instance.getClass(), fieldName);
        if (field != null) {
            try {
                field.setAccessible(true);
                field.set(instance, o);
            } catch (IllegalAccessException e) {
                throw new IllegalCastException("Unexpected cast type {" + o + "} of field" + field.getName());
            }
        }
    }

    /**
     * Capitalizes the first character of a string.
     *
     * @param s The input string.
     * @return The input string with its first character capitalized.
     */
    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

}
