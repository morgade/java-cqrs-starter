package org.morgade.tab;

import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.UUID;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.morgade.cqrs.AggregateServiceImpl;
import org.morgade.cqrs.EventStore;
import org.morgade.cqrs.EventSubscriber;
import org.morgade.cqrs.store.DelegatingEventStore;
import org.morgade.cqrs.store.InMemoryEventStore;
import org.morgade.tab.command.CloseTab;
import org.morgade.tab.command.MarkDrinksServed;
import org.morgade.tab.command.MarkFoodPrepared;
import org.morgade.tab.command.MarkFoodServed;
import org.morgade.tab.command.OpenTab;
import org.morgade.tab.command.PlaceOrder;
import org.morgade.tab.domain.Tab;
import org.morgade.tab.projection.chef.ChefTodoList;
import org.morgade.tab.projection.chef.ChefTodoListGroup;
import org.morgade.tab.projection.chef.ChefTodoListImpl;
import org.morgade.tab.projection.chef.ChefTodoListItem;
import org.morgade.tab.projection.tabs.TabInvoice;
import org.morgade.tab.projection.tabs.TabItem;
import org.morgade.tab.projection.tabs.TabStatus;
import org.morgade.tab.projection.tabs.Tabs;
import org.morgade.tab.projection.tabs.TabsImpl;
import org.morgade.tab.vo.OrderedItem;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 *
 * @author x4rb
 */
public class ServiceIntegrationTest {
    private final UUID tabId = UUID.randomUUID();
    private final int tableNumber = 3;
    private final String waiter = "Jimmy";
    private final OrderedItem coke = new OrderedItem(1, "Coke", 1.0f, true);
    private final OrderedItem beer = new OrderedItem(2, "Beer", 2.0f, true);
    private final OrderedItem chips = new OrderedItem(3, "Chips", 1.5f, false);

    private ChefTodoList chefTodoList;
    private Tabs tabs;
    private EventStore eventStore;
    private AggregateServiceImpl<Tab> tabService;
    
    @Before
    public void setup() {
        chefTodoList = new ChefTodoListImpl();
        tabs = new TabsImpl();
        eventStore = new DelegatingEventStore(new InMemoryEventStore(), (EventSubscriber)chefTodoList, (EventSubscriber)tabs);
        tabService = new AggregateServiceImpl<>(eventStore, Tab.class);
    }
    
    /***
     * Integration test example
     */
    @Test
    public void tabHappyPathTest() {
        assertTrue(chefTodoList.getTodoList().isEmpty());
        assertTrue(tabs.activeTableNumbers().isEmpty());
        
        //***************
        // Opening tab
        tabService.handle(new OpenTab(tabId, tableNumber, waiter));
        
        assertTrue(tabs.activeTableNumbers().equals(asList(tableNumber)));
        assertReflectionEquals(
            tabs.invoiceForTable(tableNumber), 
            new TabInvoice(tabId, tableNumber, Collections.EMPTY_LIST, 0f, false));
        assertReflectionEquals(
            tabs.tabForTable(tableNumber), 
            new TabStatus(tabId, tableNumber, Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST));
        
        //******************************
        // Placing order
        tabService.handle(new PlaceOrder(tabId, asList(coke, chips)));
        
        assertReflectionEquals("TabInvoice data not as expected",
                tabs.invoiceForTable(tableNumber),
                new TabInvoice(tabId, tableNumber, Collections.EMPTY_LIST, 0f, true));
        assertReflectionEquals("TabStatus data not as expected",
                tabs.tabForTable(tableNumber),
                new TabStatus(tabId, 
                        tableNumber, 
                        asList(new TabItem(coke.menuNumber, coke.description, coke.price)), 
                        asList(new TabItem(chips.menuNumber, chips.description, chips.price)), 
                        Collections.EMPTY_LIST));
        
        assertReflectionEquals("ChefTodoList data not as expected",
                chefTodoList.getTodoList(),
                asList(new ChefTodoListGroup(
                            tabId, 
                            asList(new ChefTodoListItem(chips.menuNumber, chips.description))
                       )
                )
        );
        
        //**********************************
        // Serving drink
        tabService.handle(new MarkDrinksServed(tabId, asList(coke.menuNumber)));
        
        assertReflectionEquals("TabInvoice data not as expected",
                tabs.invoiceForTable(tableNumber),
                new TabInvoice(tabId, 
                        tableNumber, 
                        asList(new TabItem(coke.menuNumber, coke.description, coke.price)), 
                        coke.price, 
                        true
                )
            );
        assertReflectionEquals("TabStatus data not as expected",
                tabs.tabForTable(tableNumber),
                new TabStatus(tabId, 
                        tableNumber, 
                        Collections.EMPTY_LIST, 
                        asList(new TabItem(chips.menuNumber, chips.description, chips.price)), 
                        asList(new TabItem(coke.menuNumber, coke.description, coke.price))));
        
        //**********************************
        // Preparing food
        tabService.handle(new MarkFoodPrepared(tabId, asList(chips.menuNumber)));
        
        assertReflectionEquals("TabStatus data not as expected",
                tabs.tabForTable(tableNumber),
                new TabStatus(tabId, 
                        tableNumber, 
                        asList(new TabItem(chips.menuNumber, chips.description, chips.price)), 
                        Collections.EMPTY_LIST, 
                        asList(new TabItem(coke.menuNumber, coke.description, coke.price))));
        assertTrue(chefTodoList.getTodoList().isEmpty());
        
        //**********************************
        // Serving food
        tabService.handle(new MarkFoodServed(tabId, asList(chips.menuNumber)));
        
        assertReflectionEquals("TabStatus data not as expected",
                tabs.tabForTable(tableNumber),
                new TabStatus(tabId, 
                        tableNumber, 
                        Collections.EMPTY_LIST, 
                        Collections.EMPTY_LIST, 
                        asList(new TabItem(coke.menuNumber, coke.description, coke.price),
                               new TabItem(chips.menuNumber, chips.description, chips.price))));
        assertReflectionEquals("TabInvoice data not as expected",
                tabs.invoiceForTable(tableNumber),
                new TabInvoice(tabId, 
                        tableNumber, 
                        asList(new TabItem(coke.menuNumber, coke.description, coke.price),
                               new TabItem(chips.menuNumber, chips.description, chips.price)), 
                        coke.price+chips.price, 
                        false
                )
            );
        
        //**************************
        // Closing tab
        tabService.handle(new CloseTab(tabId, 12));
        
        assertTrue(tabs.activeTableNumbers().isEmpty());
    }
}
