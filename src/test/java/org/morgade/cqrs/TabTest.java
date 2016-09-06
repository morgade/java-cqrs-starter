/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.morgade.cqrs;

import static java.util.Arrays.asList;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.morgade.tab.command.CloseTab;
import org.morgade.tab.command.MarkDrinksServed;
import org.morgade.tab.domain.Tab;
import org.morgade.tab.command.OpenTab;
import org.morgade.tab.command.PlaceOrder;
import org.morgade.tab.event.DrinksOrdered;
import org.morgade.tab.event.DrinksServed;
import org.morgade.tab.event.TabClosed;
import org.morgade.tab.event.TabOpened;
import org.morgade.tab.exception.DrinksNotOutstandingException;
import org.morgade.tab.exception.TabNotOpenException;
import org.morgade.tab.vo.OrderedItem;

/**
 *
 * @author x4rb
 */
public class TabTest extends BDDTest<Tab> {

    private UUID testId;
    private int testTable;
    private String testWaiter;
    private OrderedItem testDrink1;
    private OrderedItem testDrink2;
    private OrderedItem testFood1;
    private OrderedItem testFood2;

    public TabTest() {
        super(Tab.class);
    }

    @Before
    public void setup() {
        testId = UUID.randomUUID();
        testTable = 42;
        testWaiter = "Derek";
        testDrink1 = new OrderedItem(4, "Sprite", 1.5f, true);
        testDrink2 = new OrderedItem(5, "Beer", 3f, true);
    }

    @Test
    public void canOpenANewTabTest() {
        test(given(),
                when(new OpenTab(testId, testTable, testWaiter)),
                then(new TabOpened(testId, testTable, testWaiter)));
    }

    @Test
    public void canNotOrderWithUnopenedTab() {
        test(given(),
                when(new PlaceOrder(testId, asList(testDrink1))),
                thenFailWith(TabNotOpenException.class)
        );
    }

    @Test
    public void canPlaceDrinksOrder() {
        test(given(new TabOpened(testId, testTable, testWaiter)),
                when(new PlaceOrder(testId, asList(testDrink1, testDrink2))),
                then(new DrinksOrdered(testId, asList(testDrink1, testDrink2)))
        );
    }

    @Test
    public void orderedDrinksCanBeServedOrder() {
        test(
            given(
                new TabOpened(testId, testTable, testWaiter),
                new DrinksOrdered(testId, asList(testDrink1, testDrink2))
            ),
            when(
                new MarkDrinksServed(testId, asList(testDrink1.menuNumber, testDrink2.menuNumber))
            ),
            then(
                new DrinksServed(testId, asList(testDrink1.menuNumber, testDrink2.menuNumber))
            )
        );
    }

    @Test
    public void canNotServeAnUnorderedDrink() {
        test(given(
                new TabOpened(testId, testTable, testWaiter),
                new DrinksOrdered(testId, asList(testDrink1))
            ),
            when(
                new MarkDrinksServed(testId, asList(testDrink1.menuNumber, testDrink2.menuNumber))
            ),
            thenFailWith(
                DrinksNotOutstandingException.class
            )
        );
    }
    
    @Test
    public void canNotServeAnOrderedDrinkTwice() {
        test(given(
                new TabOpened(testId, testTable, testWaiter),
                new DrinksOrdered(testId, asList(testDrink1)),
                new DrinksServed(testId, asList(testDrink1.menuNumber))
            ),
            when(
                new MarkDrinksServed(testId, asList(testDrink1.menuNumber))
            ),
            thenFailWith(
                DrinksNotOutstandingException.class
            )
        );
    }
    
    @Test
    public void CanCloseTabWithTip() {
        test(given(
                new TabOpened(testId, testTable, testWaiter),
                new DrinksOrdered(testId, asList(testDrink1, testDrink2)),
                new DrinksServed(testId, asList(testDrink1.menuNumber, testDrink2.menuNumber))
            ),
            when(
                new CloseTab(testId, testDrink1.price + testDrink2.price + 0.50F)
            ),
            then(
                new TabClosed(
                    testId, 
                    testDrink1.price + testDrink2.price + 0.50F, 
                    testDrink1.price + testDrink2.price, 0.50F
                )
            )
        );
}
}
