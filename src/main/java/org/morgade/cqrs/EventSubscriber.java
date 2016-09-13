package org.morgade.cqrs;

import java.lang.reflect.Method;

/**
 *
 * @author x4rb
 */
public interface EventSubscriber {
    
    default void dispatch(Event event) {
        Method method = HandlerManager.findHandler(this, EventHandler.class, event, event.getClass());
        if (method!=null) {
            HandlerManager.invokeHandler(method, this, event);
        }
    }
    
    default void dispatch(EventStream events) {
        events.forEach(this::dispatch);
    }
}
