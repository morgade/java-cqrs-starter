package org.morgade.tab.infrastructure;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import java.util.List;
import java.util.Map;
import org.morgade.tab.projection.chef.ChefTodoListImpl;
import org.morgade.tab.projection.tabs.TabsImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author x4rb
 */
public class ProjectionsLoader {
    @Autowired
    private AmazonDynamoDB dynamoDB;
    
    public void load(TabsImpl tabs, ChefTodoListImpl chefTodoListImpl) {
        List<Map<String, AttributeValue>> m = dynamoDB.scan(new ScanRequest("tab_evt")).getItems();
        
        m.stream()
            .map(DynamoDBEventMapper::from)
            .forEachOrdered( evt -> {
                tabs.dispatch(evt);
                chefTodoListImpl.dispatch(evt);
            });
    }
    
}
