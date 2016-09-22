package org.morgade.tab.infrastructure;

import static org.morgade.tab.infrastructure.DynamoDBEventMapper.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import java.util.Collections;
import static java.util.Collections.singletonMap;
import java.util.List;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import org.morgade.cqrs.Event;
import org.morgade.cqrs.EventStore;
import org.morgade.cqrs.EventStream;
import org.morgade.cqrs.EventStreamImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author x4rb
 */
public class DynamoDBEventStore implements EventStore{
    @Autowired
    private AmazonDynamoDB dynamoDB;
    
    @Override
    public EventStream loadEventStream(UUID id) {
        QueryResult queryResult = dynamoDB.query(new QueryRequest("tab_evt")
                .withKeyConditionExpression("#id = :id")
                .withExpressionAttributeNames(singletonMap("#id", "id"))
                .withExpressionAttributeValues(singletonMap(":id", new AttributeValue().withS(id.toString())))
        );
        
        List<Event> evts = queryResult.getItems().stream()
                .map(DynamoDBEventMapper::from)
                .collect(toList());
        
        if (evts.isEmpty()) {
            return new EventStreamImpl(1, Collections.EMPTY_LIST);
        } else {
            return new EventStreamImpl(1, 
                queryResult.getItems().stream()
                    .map(DynamoDBEventMapper::from)
                    .collect(toList())
            );
        }
    }

    @Override
    public void store(UUID id, long version, List<? extends Event> newEvents) {
        List<WriteRequest> evtData = newEvents.stream().map(
                evt -> new WriteRequest(new PutRequest(from(evt)))
        ).collect(toList());
        
        dynamoDB.batchWriteItem(singletonMap("tab_evt", evtData));
    }
    
        
}
