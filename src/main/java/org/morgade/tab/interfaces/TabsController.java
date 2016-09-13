package org.morgade.tab.interfaces;

import java.util.Arrays;
import static java.util.Collections.singletonMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import org.morgade.cqrs.AggregateService;
import org.morgade.menu.MenuRepository;
import org.morgade.menu.vo.MenuItem;
import org.morgade.staff.StaffRepository;
import org.morgade.tab.command.CloseTab;
import org.morgade.tab.command.MarkDrinksServed;
import org.morgade.tab.command.MarkFoodPrepared;
import org.morgade.tab.command.MarkFoodServed;
import org.morgade.tab.command.OpenTab;
import org.morgade.tab.command.PlaceOrder;
import org.morgade.tab.projection.chef.ChefTodoList;
import org.morgade.tab.projection.chef.ChefTodoListGroup;
import org.morgade.tab.projection.tabs.TabInvoice;
import org.morgade.tab.projection.tabs.TabItem;
import org.morgade.tab.projection.tabs.TabStatus;
import org.morgade.tab.projection.tabs.Tabs;
import org.morgade.tab.vo.OrderedItem;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author x4rb
 */
@RestController
public class TabsController {
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private AggregateService tabService;
    @Autowired
    private Tabs tabs;
    @Autowired
    private ChefTodoList chefTodoList;
    
    @RequestMapping("/menu")
    public List<MenuItem> menu() {
        return menuRepository.findAll();
    }
    
    @RequestMapping("/staff")
    public List<String> staff() {
        return staffRepository.findAll();
    }
    
    @RequestMapping("/tab/open")
    public Map<String, Object> tabOpen(@RequestParam("tableNumber") int tableNumber, @RequestParam("waiter") String waiter) {
        UUID id = UUID.randomUUID();
        tabService.handle(new OpenTab(id, tableNumber, waiter));
        return success("id", id.toString());
    }
    
    @RequestMapping("/tab/{id}/place")
    public Map<String, Object> placeOrder(@PathVariable("id") String id, @RequestParam("item") Integer[] menuNumbers) {
        List<OrderedItem> items = Arrays.stream(menuNumbers)
            .map(n -> new OrderedItem(n, 
                    menuRepository.get(n).getDescription(), 
                    menuRepository.get(n).getPrice(), 
                    menuRepository.get(n).isDrink()
                )
            )
            .collect(toList());

        tabService.handle(new PlaceOrder(UUID.fromString(id), items));
        return success();
    }
    
    @RequestMapping("/tab/{id}/markDrinksServed")
    public Map<String, Object> markDrinksServed(@PathVariable("id") String id, @RequestParam("item") Integer[] menuNumbers) {
        tabService.handle(new MarkDrinksServed(UUID.fromString(id), Arrays.asList(menuNumbers)));
        return success();
    }
    
    @RequestMapping("/tab/{id}/markFoodPrepared")
    public Map<String, Object> markFoodPrepared(@PathVariable("id") String id, @RequestParam("item") Integer[] menuNumbers) {
        tabService.handle(new MarkFoodPrepared(UUID.fromString(id), Arrays.asList(menuNumbers)));
        return success();
    }
    
    @RequestMapping("/tab/{id}/markFoodServed")
    public Map<String, Object> markFoodServed(@PathVariable("id") String id, @RequestParam("item") Integer[] menuNumbers) {
        tabService.handle(new MarkFoodServed(UUID.fromString(id), Arrays.asList(menuNumbers)));
        return success();
    }
    
    @RequestMapping("/tab/{id}/close")
    public Map<String, Object> close(@PathVariable("id") String id, @RequestParam("amountPaid") float amountPaid) {
        tabService.handle(new CloseTab(UUID.fromString(id), amountPaid));
        return success();
    }
    
    @RequestMapping("/tab/table-numbers")
    public List<Integer> tableNumbers() {
        return tabs.activeTableNumbers();
    }
    
    @RequestMapping("/table/{number}/status")
    public TabStatus status(@PathVariable("number") Integer number) {
        return tabs.tabForTable( number );
    }
    
    @RequestMapping("/table/{number}/invoice")
    public TabInvoice invoice(@PathVariable("number") Integer number) {
        return tabs.invoiceForTable(number);
    }
    
    @RequestMapping("/staff/{name}")
    public Map<Integer, List<TabItem>> invoice(@PathVariable("name") String name) {
        return tabs.todoListForWaiter(name);
    }
    
    @RequestMapping("/chef")
    public List<ChefTodoListGroup> chef() {
        return chefTodoList.getTodoList();
    }
    
    @ExceptionHandler
    public Map<String, Object> fail(Exception exception) {
        LoggerFactory.getLogger(TabsController.class).error("Controller exception",exception);
        
        return new HashMap<String, Object>(){{
            put("succeded", Boolean.FALSE);
            put("type", exception.getClass().getName());
            put("message", exception.getMessage());
        }};
    }

    private Map<String, Object> success() {
        return singletonMap("succeded", Boolean.TRUE);
    }
    
    private Map<String, Object> success(String feedbackKey, Object feedbackValue) {
        return new HashMap<String, Object>(){{
            put("succeded", Boolean.TRUE);
            put(feedbackKey, feedbackValue);
        }};
    }

}
