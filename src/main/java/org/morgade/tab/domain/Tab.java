package org.morgade.tab.domain;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.morgade.cqrs.Aggregate;
import org.morgade.cqrs.CommandHandler;
import org.morgade.cqrs.Event;
import org.morgade.cqrs.EventHandler;
import org.morgade.tab.command.OpenTab;
import org.morgade.tab.command.PlaceOrder;
import org.morgade.tab.event.DrinksOrdered;
import org.morgade.tab.event.FoodOrdered;
import org.morgade.tab.event.TabOpened;
import org.morgade.tab.exception.TabNotOpenException;
import org.morgade.tab.vo.OrderedItem;

/**
 *
 * @author x4rb
 */
public class Tab implements Aggregate {
    private boolean open;
    
    @CommandHandler
    public List<? extends Event> handle(OpenTab c) {
        return Arrays.asList(
            new TabOpened(c.id, c.tableNumber, c.waiter)
        );
    }
    
    @CommandHandler
    public List<? extends Event> handle(PlaceOrder c) {
        if (!open) {
            throw new TabNotOpenException();
        }
        
        List<Event> events = new LinkedList<>();
        // Drinks
        List<OrderedItem> drinks = c.items.stream().filter((i)-> i.isDrink).collect(toList());
        if (!drinks.isEmpty()) {
            events.add(new DrinksOrdered(c.id, drinks));
        }
        
        // Food
        List<OrderedItem> foods = c.items.stream().filter((i)-> !i.isDrink).collect(toList());
        if (!foods.isEmpty()) {
            events.add(new FoodOrdered(c.id, foods));
        }
        
        return events;
    }
    
    @EventHandler
    public void apply(TabOpened e) {
        open = true;
    }
    
}
