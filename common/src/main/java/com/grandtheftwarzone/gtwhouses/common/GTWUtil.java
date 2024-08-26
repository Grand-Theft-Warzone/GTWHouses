package com.grandtheftwarzone.gtwhouses.common;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GTWUtil {
    public static Class<?> getGenericType(Class<?> clazz) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length > 0) {
                    return (Class<?>) actualTypeArguments[0];
                }
            }
        }
        return null;
    }
}
