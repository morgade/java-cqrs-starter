package org.morgade.tab;

import org.morgade.cqrs.store.InMemoryEventStore;
import static java.util.Arrays.asList;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.morgade.cqrs.AggregateService;
import org.morgade.cqrs.EventStore;
import org.morgade.cqrs.store.DelegatingEventStore;
import org.morgade.tab.command.MarkDrinksServed;
import org.morgade.tab.command.MarkFoodServed;
import org.morgade.tab.command.OpenTab;
import org.morgade.tab.command.PlaceOrder;
import org.morgade.tab.domain.Tab;
import org.morgade.tab.projection.chef.ChefTodoList;
import org.morgade.tab.vo.OrderedItem;

/**
 *
 * @author x4rb
 */
public class ServiceIntegrationTest {
    @Test
    public void chefTodoListTest() {
        UUID tabId = UUID.randomUUID();
        int tableNumber = 3;
        String waiter = "Jimmy";
        OrderedItem coke = new OrderedItem(1, "Coke", 1.0f, true);
        OrderedItem beer = new OrderedItem(2, "Beer", 2.0f, true);
        OrderedItem chips = new OrderedItem(3, "Chips", 1.5f, false);
        
        ChefTodoList chefTodoList = new ChefTodoList();
        EventStore eventStore = new DelegatingEventStore(new InMemoryEventStore(), chefTodoList);
        
        AggregateService<Tab> tabService = new AggregateService<>(eventStore, Tab.class);
        tabService.handle(new OpenTab(tabId, tableNumber, waiter));
        tabService.handle(new PlaceOrder(tabId, asList(coke, beer, chips)));
        
        // Verify todo list
        Assert.assertTrue(chefTodoList.getTodoList().size()==1);
        Assert.assertTrue(chefTodoList.getTodoList().iterator().next().items.size()==1);
        Assert.assertTrue(chefTodoList.getTodoList().iterator().next().items.iterator().next().menuNumber==3);
        
        tabService.handle(new MarkDrinksServed(tabId, asList(1,2)));
        tabService.handle(new MarkFoodServed(tabId, asList(3)));
        
        // Verify todo list
        Assert.assertTrue(chefTodoList.getTodoList().isEmpty());
    }
}
