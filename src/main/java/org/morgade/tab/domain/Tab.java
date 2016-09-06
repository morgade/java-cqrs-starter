package org.morgade.tab.domain;

import static java.util.Arrays.asList;
import java.util.LinkedList;
import java.util.List;
import static java.util.stream.Collectors.summingDouble;
import static java.util.stream.Collectors.toList;
import org.morgade.cqrs.Aggregate;
import org.morgade.cqrs.CommandHandler;
import org.morgade.cqrs.Event;
import org.morgade.cqrs.EventHandler;
import org.morgade.tab.command.CloseTab;
import org.morgade.tab.command.MarkDrinksServed;
import org.morgade.tab.command.OpenTab;
import org.morgade.tab.command.PlaceOrder;
import org.morgade.tab.event.DrinksOrdered;
import org.morgade.tab.event.DrinksServed;
import org.morgade.tab.event.FoodOrdered;
import org.morgade.tab.event.TabClosed;
import org.morgade.tab.event.TabOpened;
import org.morgade.tab.exception.DrinksNotOutstandingException;
import org.morgade.tab.exception.TabNotOpenException;
import org.morgade.tab.vo.OrderedItem;

/**
 *
 * @author x4rb
 */
public class Tab implements Aggregate {

    private boolean open;
    private float servedItemsValue = 0;
    private final List<OrderedItem> outstandingDrinks = new LinkedList<>();
    private final List<OrderedItem> outstandingFood = new LinkedList<>();
    private final List<OrderedItem> preparedFood = new LinkedList<>();

    /**
     * OpenTab command handler
     * @param openTab
     * @return 
     */
    @CommandHandler
    public List<? extends Event> handle(OpenTab openTab) {
        return asList(
                new TabOpened(openTab.id, openTab.tableNumber, openTab.waiter)
        );
    }

    /**
     * PlaceOrder command handler
     * @param placeOrder
     * @return 
     */
    @CommandHandler
    public List<? extends Event> handle(PlaceOrder placeOrder) {
        if (!open) {
            throw new TabNotOpenException();
        }

        List<Event> events = new LinkedList<>();
        // Drinks
        List<OrderedItem> drinks = placeOrder.items.stream().filter((i) -> i.isDrink).collect(toList());
        if (!drinks.isEmpty()) {
            events.add(new DrinksOrdered(placeOrder.id, drinks));
        }

        // Food
        List<OrderedItem> foods = placeOrder.items.stream().filter((i) -> !i.isDrink).collect(toList());
        if (!foods.isEmpty()) {
            events.add(new FoodOrdered(placeOrder.id, foods));
        }

        return events;
    }

    /**
     * MarkDrinksServed command handler
     * @param markDrinksServed
     * @return 
     */
    @CommandHandler
    public List<? extends Event> handle(MarkDrinksServed markDrinksServed) {
        if (!isDrinksOutstanding(markDrinksServed.menuNumbers)) {
            throw new DrinksNotOutstandingException();
        }
        
        return asList(new DrinksServed(markDrinksServed.id, markDrinksServed.menuNumbers));
    }

    /**
     * CloseTab command handler
     * @param closeTab
     * @return 
     */
    @CommandHandler
    public List<? extends Event> handle(CloseTab closeTab) {
        return asList(new TabClosed(
                closeTab.id, 
                closeTab.amountPaid, 
                servedItemsValue, 
                closeTab.amountPaid - servedItemsValue)
        );
    }

    /**
     * TabOpened event handler
     * @param tabOpened 
     */
    @EventHandler
    public void apply(TabOpened tabOpened) {
        open = true;
    }

    /**
     * DrinksOrdered event handler
     * @param drinksOrdered 
     */
    @EventHandler
    public void apply(DrinksOrdered drinksOrdered) {
        outstandingDrinks.addAll(drinksOrdered.items);
    }

    /**
     * DrinksServed event handler
     * @param drinksServed 
     */
    @EventHandler
    public void apply(DrinksServed drinksServed) {
        List<OrderedItem> servedItems = outstandingDrinks.stream()
                .filter( (oi) ->  drinksServed.menuNumbers.contains(oi.menuNumber) )
                .collect(toList());
        
        servedItemsValue += servedItems.stream()
                                    .collect(summingDouble((oi)->oi.price));
        outstandingDrinks.removeAll(servedItems);
        
    }

    private boolean isDrinksOutstanding(List<Integer> menuNumbers) {
        List<Integer> outstandingDrinksNumbers = outstandingDrinks.stream()
                .map((oi) -> oi.menuNumber)
                .collect(toList());
        return menuNumbers.stream().allMatch( (n) -> outstandingDrinksNumbers.contains(n) );
    }
}
