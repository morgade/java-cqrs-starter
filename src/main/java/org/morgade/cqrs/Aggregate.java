package org.morgade.cqrs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import org.morgade.util.ReflectionUtils;

/**
 *
 * @author x4rb
 */
public interface Aggregate {
    
    default List<? extends Event> dispatch(Command command) {
        Method method = ReflectionUtils.findSingleParameterAnnotatedMethod(this, CommandHandler.class, command, command.getClass());
        if (method==null) {
            return Collections.EMPTY_LIST;
        } else {
            return (List<? extends Event>) ReflectionUtils.invokeSingleParameterMethod(method, this, command);
        }
    }
    
    default void dispatch(Event event) {
        Method method = ReflectionUtils.findSingleParameterAnnotatedMethod(this, EventHandler.class, event, event.getClass());
        if (method!=null) {
            ReflectionUtils.invokeSingleParameterMethod(method, this, event);
        }
    }
    
    default void dispatch(List<? extends Event> events) {
        events.forEach(this::dispatch);
    }
    
}
