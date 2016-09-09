package org.morgade.cqrs;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author x4rb
 */
public interface CommandReceiver {
    
    default List<? extends Event> dispatch(Command command) {
        Method method = HandlerManager.findHandler(this, CommandHandler.class, command, command.getClass());
        if (method==null) {
            return Collections.EMPTY_LIST;
        } else {
            return (List<? extends Event>) HandlerManager.invokeHandler(method, this, command);
        }
    }
    
}
