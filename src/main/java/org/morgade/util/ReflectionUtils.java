package org.morgade.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 *
 * @author x4rb
 */
public class ReflectionUtils {
    /**
     * 
     * @param <T>
     * @param <P>
     * @param target
     * @param annotation
     * @param parameter
     * @param parameterClass
     * @return 
     */
    public static <T, P> Method findSingleParameterAnnotatedMethod(T target, Class annotation, P parameter, Class<? extends P> parameterClass) {
        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(target.getClass(), annotation);
        List<Method> matches = methods.stream()
                                      .filter((m) -> m.getParameterCount() == 1 &&
                                                     m.getParameterTypes()[0].equals(parameterClass))
                                      .collect(Collectors.toList());
        
        if (matches.isEmpty()) {
            return null;
        } else {
            return matches.iterator().next();
        }
        
    }
    
    /**
     * 
     * @param <R>
     * @param method
     * @param target
     * @param param
     * @return 
     */
    public static <R> R invokeSingleParameterMethod(Method method, Object target, Object param) {
        try {
            return (R) method.invoke(target, param);
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) ex.getTargetException();
            } else {
                throw new RuntimeException(ex.getTargetException());
            }
        }
    }
    
}
