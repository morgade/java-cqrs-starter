package org.morgade.tab.projection.tabs;

import java.util.LinkedList;
import java.util.List;
import org.morgade.cqrs.EventSubscriber;

/**
 *
 * @author x4rb
 */
public class TableTodo implements EventSubscriber {
    public int tableNumber;
    public String waiter;
    public final List<TableTodoItem> toServe = new LinkedList<>();
    public final List<TableTodoItem> inPreparation = new LinkedList<>();

    
}
