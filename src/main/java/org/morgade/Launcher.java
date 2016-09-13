package org.morgade;

import org.morgade.cqrs.AggregateService;
import org.morgade.cqrs.AggregateServiceImpl;
import org.morgade.cqrs.EventSubscriber;
import org.morgade.cqrs.EventStore;
import org.morgade.cqrs.store.DelegatingEventStore;
import org.morgade.cqrs.store.InMemoryEventStore;
import org.morgade.tab.domain.Tab;
import org.morgade.tab.projection.chef.ChefTodoList;
import org.morgade.tab.projection.chef.ChefTodoListImpl;
import org.morgade.tab.projection.tabs.Tabs;
import org.morgade.tab.projection.tabs.TabsImpl;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author x4rb
 */
@SpringBootApplication
public class Launcher {
    public static void main(String[] args) {
        SpringApplication.run(Launcher.class, args);
    }
    
    @Bean
    public Tabs getTabs() {
        return new TabsImpl();
    }
    
    @Bean
    public ChefTodoList getChefTodoList() {
        return new ChefTodoListImpl();
    }
    
    @Bean
    public EventStore getEventStore() {
        return new DelegatingEventStore(new InMemoryEventStore(),  
            (EventSubscriber)getTabs(),
            (EventSubscriber)getChefTodoList()
        );
    }
    
    @Bean
    public AggregateService getTabAggregateService() {
        LoggerFactory.getLogger(Launcher.class).info("Starting tab aggregate service");
        return new AggregateServiceImpl<>(getEventStore(), Tab.class);
    }
}    
