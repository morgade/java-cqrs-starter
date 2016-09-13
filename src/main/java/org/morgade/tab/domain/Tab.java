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
import org.morgade.tab.command.MarkFoodPrepared;
import org.morgade.tab.command.MarkFoodServed;
import org.morgade.tab.command.OpenTab;
import org.morgade.tab.command.PlaceOrder;
import org.morgade.tab.event.DrinksOrdered;
import org.morgade.tab.event.DrinksServed;
import org.morgade.tab.event.FoodOrdered;
import org.morgade.tab.event.FoodPrepared;
import org.morgade.tab.event.FoodServed;
import org.morgade.tab.event.TabClosed;
import org.morgade.tab.event.TabOpened;
import org.morgade.tab.exception.DrinksNotOutstandingException;
import org.morgade.tab.exception.FoodNotOutstandingException;
import org.morgade.tab.exception.FoodNotPreparedException;
import org.morgade.tab.exception.MustPayEnoughException;
import org.morgade.tab.exception.TabHasUnservedItemsException;
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
     * MarkFoodPrepared command handler
     * @param markFoodPrepared
     * @return 
     */
    @CommandHandler
    public List<? extends Event> handle(MarkFoodPrepared markFoodPrepared) {
        if (!isFoodOutstanding(markFoodPrepared.menuNumbers)) {
            throw new FoodNotOutstandingException();
        }
        
        return asList(new FoodPrepared(markFoodPrepared.id, markFoodPrepared.menuNumbers));
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
     * MarkFoodServed command handler
     * @param markFoodServed
     * @return 
     */
    @CommandHandler
    public List<? extends Event> handle(MarkFoodServed markFoodServed) {
        if (!isFoodPrepared(markFoodServed.menuNumbers)) {
            throw new FoodNotPreparedException();
        }
        
        return asList(new FoodServed(markFoodServed.id, markFoodServed.menuNumbers));
    }

    /**
     * CloseTab command handler
     * @param closeTab
     * @return 
     */
    @CommandHandler
    public List<? extends Event> handle(CloseTab closeTab) {
        if (!open) {
            throw new TabNotOpenException();
        } else if (closeTab.amountPaid < servedItemsValue) {
            throw new MustPayEnoughException();
        } else if (!outstandingDrinks.isEmpty() || !outstandingFood.isEmpty() || !preparedFood.isEmpty()) {
            throw new TabHasUnservedItemsException();
        } else {
            return asList(new TabClosed(
                    closeTab.id, 
                    closeTab.amountPaid, 
                    servedItemsValue, 
                    closeTab.amountPaid - servedItemsValue)
            );
        }
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
     * FoodOrdered event handler
     * @param foodOrdered 
     */
    @EventHandler
    public void apply(FoodOrdered foodOrdered) {
        outstandingFood.addAll(foodOrdered.items);
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

    /**
     * FoodPrepared event handler
     * @param foodPrepared 
     */
    @EventHandler
    public void apply(FoodPrepared foodPrepared) {
        foodPrepared.menuNumbers.forEach(n -> {
            preparedFood.add(
                outstandingFood.stream()
                        .filter(item -> item.menuNumber == n)
                        .findFirst().get()
            );
                    
            outstandingFood.removeIf(item -> item.menuNumber == n);
        });
    }

    /**
     * FoodServed event handler
     * @param foodServed 
     */
    @EventHandler
    public void apply(FoodServed foodServed) {
        List<OrderedItem> servedItems = preparedFood.stream()
                .filter( (oi) ->  foodServed.menuNumbers.contains(oi.menuNumber) )
                .collect(toList());
        
        servedItemsValue += servedItems.stream()
                                .collect(summingDouble((oi)->oi.price));
        
        preparedFood.removeAll(servedItems);
    }

    /**
     * TabClosed event handler
     * @param tabClosed 
     */
    @EventHandler
    public void apply(TabClosed tabClosed) {
        open = false;
    }
    
    
    private boolean isDrinksOutstanding(List<Integer> menuNumbers) {
        List<Integer> outstandingDrinksNumbers = outstandingDrinks.stream()
                .map((oi) -> oi.menuNumber)
                .collect(toList());
        return menuNumbers.stream().allMatch( (n) -> outstandingDrinksNumbers.contains(n) );
    }
    
    private boolean isFoodOutstanding(List<Integer> menuNumbers) {
        List<Integer> outstandingFoodNumbers = outstandingFood.stream()
                .map((oi) -> oi.menuNumber)
                .collect(toList());
        return menuNumbers.stream().allMatch( (n) -> outstandingFoodNumbers.contains(n) );
    }
    
    private boolean isFoodPrepared(List<Integer> menuNumbers) {
        return !preparedFood.isEmpty() 
                && preparedFood.stream()
                    .allMatch(f->menuNumbers.contains(f.menuNumber));
    }
}
