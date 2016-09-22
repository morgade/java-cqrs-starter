package org.morgade;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import org.morgade.cqrs.AggregateService;
import org.morgade.cqrs.AggregateServiceImpl;
import org.morgade.cqrs.EventSubscriber;
import org.morgade.cqrs.EventStore;
import org.morgade.cqrs.store.DelegatingEventStore;
import org.morgade.tab.domain.Tab;
import org.morgade.tab.infrastructure.DynamoDBEventStore;
import org.morgade.tab.infrastructure.ProjectionsLoader;
import org.morgade.tab.projection.chef.ChefTodoList;
import org.morgade.tab.projection.chef.ChefTodoListImpl;
import org.morgade.tab.projection.tabs.Tabs;
import org.morgade.tab.projection.tabs.TabsImpl;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${amazon.dynamodb.endpoint}")
    private String amazonDynamoDBEndpoint;
    
    @Value("${amazon.aws.accesskey}")
    private String amazonAWSAccessKey;

    @Value("${amazon.aws.secretkey}")
    private String amazonAWSSecretKey;
    
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
//        return new DelegatingEventStore(new InMemoryEventStore(),  
//            (EventSubscriber)getTabs(),
//            (EventSubscriber)getChefTodoList()
//        );
        getProjectionsLoader().load((TabsImpl)getTabs(), (ChefTodoListImpl)getChefTodoList());
        return new DelegatingEventStore(dynamoDBEventStore(),  
            (EventSubscriber)getTabs(),
            (EventSubscriber)getChefTodoList()
        );
    }
    
    @Bean
    public AggregateService getTabAggregateService() {
        LoggerFactory.getLogger(Launcher.class).info("Starting tab aggregate service");
        return new AggregateServiceImpl<>(getEventStore(), Tab.class);
    }
    
    @Bean
    public ProjectionsLoader getProjectionsLoader() {
        return new ProjectionsLoader();
    }
    
    @Bean
    public DynamoDBEventStore dynamoDBEventStore() {
        return new DynamoDBEventStore();
    }
    
    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(amazonAWSCredentials());
        amazonDynamoDB.setEndpoint(amazonDynamoDBEndpoint);
        return amazonDynamoDB;
    }
    
    @Bean
    public AWSCredentials amazonAWSCredentials() {
        return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
    }
}    
