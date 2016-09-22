package org.morgade.tab.projection.chef;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import org.morgade.cqrs.EventHandler;
import org.morgade.cqrs.EventSubscriber;
import org.morgade.tab.event.FoodOrdered;
import org.morgade.tab.event.FoodPrepared;

/**
 *
 * @author x4rb
 */
public class ChefTodoListImpl implements EventSubscriber, ChefTodoList {
    private final List<ChefTodoListGroup> todoList = new LinkedList<>();

    /**
     * Returns todo list copy
     * @return 
     */
    @Override
    public List<ChefTodoListGroup> getTodoList() {
        synchronized (todoList) {
            return (List<ChefTodoListGroup>) 
                todoList.stream()
                    .map(
                        (tlg) -> new ChefTodoListGroup(tlg.tab, new ArrayList(tlg.items))
                    )
                    .collect(toList());
        }
    }
    
    /**
     * FoodOrdered event handler
     * @param foodOrdered 
     */
    @EventHandler
    public void handle(FoodOrdered foodOrdered) {
        ChefTodoListGroup group = new ChefTodoListGroup(
                foodOrdered.id, 
                foodOrdered.items.stream()
                    .map(oi -> new ChefTodoListItem(oi.menuNumber, oi.description))
                    .collect(toList())
        );

        synchronized (todoList) {
            todoList.add(group);
        }
    }
    
    /**
     * FoodPrepared event handler
     * @param foodPrepared 
     */
    @EventHandler
    public void handle(FoodPrepared foodPrepared) {
        synchronized (todoList) {
            Optional<ChefTodoListGroup> group = todoList.stream()
                    .filter(g -> g.tab.equals(foodPrepared.id) )
                    .findFirst();

            group.ifPresent(g -> {
                g.items.removeIf(item -> foodPrepared.menuNumbers.contains(item.menuNumber));

                if (g.items.isEmpty()) {
                    todoList.remove(g);
                }
            });
        }
    }
}
