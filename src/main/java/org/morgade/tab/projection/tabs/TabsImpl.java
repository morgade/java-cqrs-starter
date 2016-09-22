package org.morgade.tab.projection.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.util.Optional;
import java.util.function.Function;
import org.morgade.cqrs.EventHandler;
import org.morgade.cqrs.EventSubscriber;
import org.morgade.tab.event.DrinksOrdered;
import org.morgade.tab.event.DrinksServed;
import org.morgade.tab.event.FoodOrdered;
import org.morgade.tab.event.FoodPrepared;
import org.morgade.tab.event.FoodServed;
import org.morgade.tab.event.TabClosed;
import org.morgade.tab.event.TabOpened;

/**
 *
 * @author x4rb
 */
public class TabsImpl implements EventSubscriber, Tabs {

    private final Map<UUID, Tab> todoByTab = new HashMap<>();

    @Override
    public List<Integer> activeTableNumbers() {
        synchronized (todoByTab) {
            return todoByTab.values().stream()
                    .map(t -> t.tableNumber)
                    .sorted()
                    .collect(toList());
        }
    }

    @Override
    public Map<Integer, List<TabItem>> todoListForWaiter(String waiter) {
        synchronized (todoByTab) {
            return todoByTab.values().stream()
                    .filter(t -> t.waiter.equals(waiter) && !t.toServe.isEmpty())
                    .collect(
                        toMap(
                            t -> t.tableNumber, 
                            t -> new ArrayList(t.toServe)
                        )
                    );
        }
    }
    
    @Override
    public UUID tabIdForTable(int table) {
        synchronized (todoByTab) {
            return todoByTab.entrySet().stream()
                .filter(entry -> entry.getValue().tableNumber == table)
                .map(entry -> entry.getKey())
                .findFirst()
                .get();
        }
    }
    
    @Override
    public TabStatus tabForTable(int table) {
        synchronized (todoByTab) {
            return todoByTab.entrySet().stream()
                .filter(entry -> entry.getValue().tableNumber == table)
                .map( entry -> new TabStatus(
                        entry.getKey(), 
                        entry.getValue().tableNumber,
                        entry.getValue().toServe,
                        entry.getValue().inPreparation,
                        entry.getValue().served
                    )
                )
                .findFirst()
                .orElse(null);
        }
    }
    
    @Override
    public TabInvoice invoiceForTable(int table) {
        Optional<Map.Entry<UUID, Tab>> tab = todoByTab.entrySet().stream()
                .filter( entry -> entry.getValue().tableNumber==table )
                .findFirst();
        
        return tab.map( 
            t -> new TabInvoice(
                t.getKey(), 
                t.getValue().tableNumber,
                t.getValue().served, 
                (float)t.getValue().served.stream().mapToDouble(i->i.price).sum(), 
                !t.getValue().inPreparation.isEmpty() || !t.getValue().toServe.isEmpty()
            )
        ).get();
    }
    
    /**
     * TabOpened event handler
     * @param e 
     */
    @EventHandler
    public void handle(TabOpened e) {
        synchronized (todoByTab) {
            todoByTab.put(e.id, new Tab(e.tableNumber, e.waiter));
        }
    }
    
    /**
     * DrinksOrdered event handler
     * @param e 
     */
    @EventHandler
    public void handle(DrinksOrdered e) {
        List<TabItem> tabItems = e.items.stream().map( 
                oi -> new TabItem(oi.menuNumber, oi.description, oi.price)
        ).collect(toList());
        
        addItems(e.id, tabItems, tab -> tab.toServe);
    }
    
    /**
     * FoodOrdered event handler
     * @param e 
     */
    @EventHandler
    public void handle(FoodOrdered e) {
        List<TabItem> tabItems = e.items.stream().map( 
                oi -> new TabItem(oi.menuNumber, oi.description, oi.price)
        ).collect(toList());
        
        addItems(e.id, tabItems, tab -> tab.inPreparation);
    }
    
    /**
     * FoodPrepared event handler
     * @param e 
     */
    @EventHandler
    public void handle(FoodPrepared e) {
        moveItems(e.id, e.menuNumbers, t -> t.inPreparation, t -> t.toServe);
    }
    
    /**
     * FoodServed event handler
     * @param e 
     */
    @EventHandler
    public void handle(FoodServed e)    {
        moveItems(e.id, e.menuNumbers, t -> t.toServe, t -> t.served);
    }
    
    /**
     * DrinksServed event handler
     * @param e 
     */
    @EventHandler
    public void handle(DrinksServed e)    {
        moveItems(e.id, e.menuNumbers, t -> t.toServe, t -> t.served);
    }
    
    /**
     * TabClosed event handler
     * @param e 
     */
    @EventHandler
    public void handle(TabClosed e) {
        synchronized (todoByTab) {
            todoByTab.remove(e.id);
        }
    }
    
    private void addItems(UUID id, List<TabItem> items, Function<Tab, List<TabItem>> to) {
        Tab tab = getTab(id);
        synchronized (tab) {
            to.apply(tab).addAll(items);
        }
    }
    
    private void moveItems(UUID id, List<Integer> items, Function<Tab, List<TabItem>> from, Function<Tab, List<TabItem>> to) {
        Tab tab = getTab(id);
        synchronized (tab) {
            List<TabItem> fromList = from.apply(tab);
            List<TabItem> toList = to.apply(tab);
            items.forEach(number -> {
                fromList.stream()
                    .filter(f -> f.menuNumber==number)
                    .findFirst()
                    .ifPresent(servItem -> {
                        toList.add(servItem);
                        fromList.remove(servItem);
                    });
            });
        }
    }
    
    private Tab getTab(UUID id) {
        synchronized (todoByTab) {
            return todoByTab.get(id);
        }
    }
}

