package org.morgade.tab;

import static java.util.Arrays.asList;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.morgade.cqrs.AggregateTest;
import org.morgade.tab.command.CloseTab;
import org.morgade.tab.command.MarkDrinksServed;
import org.morgade.tab.domain.Tab;
import org.morgade.tab.command.OpenTab;
import org.morgade.tab.command.PlaceOrder;
import org.morgade.tab.event.DrinksOrdered;
import org.morgade.tab.event.DrinksServed;
import org.morgade.tab.event.FoodOrdered;
import org.morgade.tab.event.FoodPrepared;
import org.morgade.tab.event.TabClosed;
import org.morgade.tab.event.TabOpened;
import org.morgade.tab.exception.DrinksNotOutstandingException;
import org.morgade.tab.exception.MustPayEnoughException;
import org.morgade.tab.exception.TabAlreadyOpenException;
import org.morgade.tab.exception.TabHasUnservedItemsException;
import org.morgade.tab.exception.TabNotOpenException;
import org.morgade.tab.vo.OrderedItem;

/**
 *
 * @author x4rb
 */
public class TabTest extends AggregateTest<Tab> {

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
        testFood1 = new OrderedItem(6, "Chips", 3f, false);
        testFood2 = new OrderedItem(7, "Peanuts", 2f, false);
    }

    @Test
    public void canOpenANewTabTest() {
        test(
            given(),
            when(new OpenTab(testId, testTable, testWaiter)),
            then(new TabOpened(testId, testTable, testWaiter))
        );
    }

    @Test
    public void canNotOpenTabAlreadyOpenTest() {
        test(
            given(new TabOpened(testId, testTable, testWaiter)),
            when(new OpenTab(testId, testTable, testWaiter)),
            thenFailWith(TabAlreadyOpenException.class)
        );
    }

    @Test
    public void canNotOrderWithUnopenedTab() {
        test(
            given(),
            when(new PlaceOrder(testId, asList(testDrink1))),
            thenFailWith(TabNotOpenException.class)
        );
    }

    @Test
    public void canPlaceDrinksOrder() {
        test(
            given(new TabOpened(testId, testTable, testWaiter)),
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
        test(
            given(
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
        test(
            given(
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
    public void canCloseTabWithTip() {
        test(
            given(
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
    
    @Test
    public void mustPayEnoughToCloseTab() {
        test(
            given(
                new TabOpened(testId, testTable, testWaiter),
                new DrinksOrdered(testId, asList(testDrink2)),
                new DrinksServed(testId, asList(testDrink2.menuNumber))
            ),
            when(
                new CloseTab(testId, testDrink2.price - 0.50F)
            ),
            thenFailWith(
                MustPayEnoughException.class
            )
        );
    }
    
    @Test
    public void canNotCloseTabTwice() {
        test(
            given(
                new TabOpened(testId, testTable, testWaiter),
                new DrinksOrdered(testId, asList(testDrink2)),
                new DrinksServed(testId, asList(testDrink2.menuNumber)),
                new TabClosed(testId, testDrink2.price + 0.50f, testDrink2.price, 0.50f)
            ),
            when(
                new CloseTab(testId, testDrink2.price)
            ),
            thenFailWith(
                TabNotOpenException.class
            )
        );
    }
    
    @Test
    public void canNotCloseTabWithUnservedDrinksItems() {
        test(
            given(
                new TabOpened(testId, testTable, testWaiter),
                new DrinksOrdered(testId, asList(testDrink2))
            ),
            when(
                new CloseTab(testId, testDrink2.price)
            ),
            thenFailWith(
                TabHasUnservedItemsException.class
            )
        );
    }
    
    @Test
    public void CanNotCloseTabWithUnpreparedFoodItems() {
        test(
            given(
                new TabOpened(testId, testTable, testWaiter),
                new FoodOrdered(testId, asList(testFood1))
            ),
            when(
                new CloseTab(testId, testFood1.price)
            ),
            thenFailWith(
                TabHasUnservedItemsException.class
            )
        );
    }
    
    @Test
    public void canNotCloseTabWithUnservedFoodItems() {
        test(
            given(
                new TabOpened(testId, testTable, testWaiter),
                new FoodOrdered(testId, asList(testFood1)),
                new FoodPrepared(testId, asList(testFood1.menuNumber))
            ),
            when(
                new CloseTab(testId, testFood1.price)
            ),
            thenFailWith(
                TabHasUnservedItemsException.class
            )
        );
    }
}
