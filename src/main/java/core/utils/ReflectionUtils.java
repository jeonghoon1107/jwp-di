package core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionUtils {
    public static <A extends Annotation> A getAnnotationInMethod(Method method, Class<A> annotation) {
        return method.getAnnotation(annotation);
    }

    public static <A extends Annotation> Method[] getMethodsWithAnnotation(Class<?> clazz, Class<A> annotation) {
        Method[] methods = clazz.getDeclaredMethods();
        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(annotation))
                .toArray(Method[]::new);
    }
}
