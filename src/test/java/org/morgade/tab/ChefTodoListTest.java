/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.morgade.tab;

import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.morgade.cqrs.EventReceiverTest;
import org.morgade.tab.event.DrinksOrdered;
import org.morgade.tab.event.DrinksServed;
import org.morgade.tab.event.FoodOrdered;
import org.morgade.tab.event.FoodPrepared;
import org.morgade.tab.event.FoodServed;
import org.morgade.tab.event.TabOpened;
import org.morgade.tab.projection.chef.ChefTodoListImpl;
import org.morgade.tab.projection.chef.ChefTodoListGroup;
import org.morgade.tab.projection.tabs.TabInvoice;
import org.morgade.tab.projection.tabs.TabStatus;
import org.morgade.tab.projection.tabs.TabsImpl;
import org.morgade.tab.vo.OrderedItem;

/**
 *
 * @author x4rb
 */
public class ChefTodoListTest extends EventReceiverTest<ChefTodoListImpl> {
    private UUID[] ids;
    private Integer[] tableNumbers;
    private String waiter;
    private OrderedItem drink1;
    private OrderedItem drink2;
    private OrderedItem food1;
    private OrderedItem food2;
    
    public ChefTodoListTest() {
        super(ChefTodoListImpl.class);
    }
    
    @Before
    public void setup() {
        ids = new UUID[]{ UUID.randomUUID(), UUID.randomUUID() };
        tableNumbers = new Integer[]{ 1, 2 };
        waiter = "John";
        drink1 = new OrderedItem(1, "Coke", 1.0f, true);
        drink2 = new OrderedItem(2, "Beer", 2.0f, true);
        food1 = new OrderedItem(3, "Fish", 3.0f, false);
        food2 = new OrderedItem(4, "Chips", 1.0f, false);
    }
    
    @Test
    public void testTabIdForTable() {
        ChefTodoListImpl todo = given(
            new TabOpened(ids[0], tableNumbers[0], waiter),
            new TabOpened(ids[1], tableNumbers[0], waiter),
            new DrinksOrdered(ids[0], asList(drink1, drink2)),
            new FoodOrdered(ids[0], asList(food1, food2)),
            new FoodOrdered(ids[1], asList(food2))
        );
        
        List<ChefTodoListGroup> list = todo.getTodoList();
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(ids[0], list.iterator().next().tab);
        Assert.assertEquals(2, list.iterator().next().items.size());
        Assert.assertEquals(food1.menuNumber, list.iterator().next().items.iterator().next().menuNumber);
    }
    
}
