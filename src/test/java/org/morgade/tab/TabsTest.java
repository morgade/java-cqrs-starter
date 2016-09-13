/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.morgade.tab;

import java.util.Arrays;
import static java.util.Arrays.asList;
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
import org.morgade.tab.projection.tabs.TabInvoice;
import org.morgade.tab.projection.tabs.TabStatus;
import org.morgade.tab.projection.tabs.TabsImpl;
import org.morgade.tab.vo.OrderedItem;

/**
 *
 * @author x4rb
 */
public class TabsTest extends EventReceiverTest<TabsImpl> {
    private UUID[] ids;
    private Integer[] tableNumbers;
    private String waiter;
    private OrderedItem drink1;
    private OrderedItem drink2;
    private OrderedItem food1;
    private OrderedItem food2;
    
    public TabsTest() {
        super(TabsImpl.class);
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
        TabsImpl tabs = given(
            new TabOpened(ids[0], tableNumbers[0], waiter),
            new DrinksOrdered(ids[0], asList(drink1, drink2))
        );
        
        Assert.assertEquals(ids[0], tabs.tabIdForTable(tableNumbers[0]));
    }
    
    @Test
    public void testActiveTableNumbers() {
        TabsImpl tabs = given(
            new TabOpened(ids[0], tableNumbers[0], waiter),
            new TabOpened(ids[1], tableNumbers[1], waiter),
            new DrinksOrdered(ids[0], asList(drink2)),
            new DrinksOrdered(ids[1], asList(drink1))
        );
        
        Assert.assertEquals(Arrays.asList(tableNumbers), tabs.activeTableNumbers());
    }
    
    @Test
    public void testInvoiceForTable() {
        TabsImpl tabs = given(
            new TabOpened(ids[0], tableNumbers[0], waiter),
            new TabOpened(ids[1], tableNumbers[1], waiter),
            new DrinksOrdered(ids[0], asList(drink1)),
            new DrinksOrdered(ids[1], asList(drink1)),
            new FoodOrdered(ids[0], asList(food1)),
            new FoodOrdered(ids[1], asList(food2)),
            new DrinksServed(ids[0], asList(drink1.menuNumber)),
            new FoodPrepared(ids[0], asList(food1.menuNumber)),
            new FoodServed(ids[0], asList(food1.menuNumber))
        );
        
        TabInvoice invoice = tabs.invoiceForTable(tableNumbers[0]);
        Assert.assertEquals(ids[0], invoice.tabId);
        Assert.assertEquals(2, invoice.items.size());
        Assert.assertEquals(drink1.menuNumber, invoice.items.iterator().next().menuNumber);
        Assert.assertEquals(drink1.price + food1.price, invoice.total, 0);
        Assert.assertFalse(invoice.hasUnservedItems);
    }
        
    @Test
    public void testTabForTable() {
        TabsImpl tabs = given(
            new TabOpened(ids[0], tableNumbers[0], waiter),
            new DrinksOrdered(ids[0], asList(drink1, drink2)),
            new DrinksServed(ids[0], asList(drink1.menuNumber))
        );
        
        TabStatus status = tabs.tabForTable(tableNumbers[0]);
        Assert.assertEquals(ids[0], status.tabId);
        Assert.assertEquals(1, status.served.size());
        Assert.assertEquals(1, status.toServe.size());
        Assert.assertEquals(drink1.menuNumber, status.served.iterator().next().menuNumber);
        Assert.assertEquals(drink2.menuNumber, status.toServe.iterator().next().menuNumber);
    }
}
