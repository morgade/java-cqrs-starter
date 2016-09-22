package org.morgade.tab.infrastructure;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import org.morgade.cqrs.Event;
import org.morgade.tab.event.DrinksOrdered;
import org.morgade.tab.event.DrinksServed;
import org.morgade.tab.event.FoodOrdered;
import org.morgade.tab.event.FoodPrepared;
import org.morgade.tab.event.FoodServed;
import org.morgade.tab.event.TabClosed;
import org.morgade.tab.event.TabOpened;
import org.morgade.tab.vo.OrderedItem;

/**
 *
 * @author x4rb
 */
class DynamoDBEventMapper {
    static Map<String, AttributeValue> from(Event evt) {
        Map<String, AttributeValue> m = new HashMap<>();
        m.put("id", new AttributeValue().withS(evt.getId().toString()));
        m.put("idx", new AttributeValue().withN(Long.toString(System.currentTimeMillis())));
        m.put("type", new AttributeValue().withS(evt.getClass().getSimpleName()));
                
        switch (evt.getClass().getSimpleName()) {
            case "DrinksOrdered":
                m.put("items", new AttributeValue().withL( fromOrderedItems( ((DrinksOrdered)evt).items ) ) );
                break;
            case "DrinksServed":
                m.put("menuNumbers", new AttributeValue().withL(
                        ((DrinksServed)evt).menuNumbers.stream()
                            .map(n->new AttributeValue().withN(Integer.toString(n)))
                            .collect(toList()))
                );
                break;
            case "FoodOrdered":
                m.put("items", new AttributeValue().withL( fromOrderedItems( ((FoodOrdered)evt).items ) ) );
                break;
            case "FoodServed":
                m.put("menuNumbers", new AttributeValue().withL(
                        ((FoodServed)evt).menuNumbers.stream()
                            .map(n->new AttributeValue().withN(Integer.toString(n)))
                            .collect(toList()))
                );
                break;
            case "TabClosed":
                m.put("amountPaid", new AttributeValue().withN(Float.toString(((TabClosed)evt).amountPaid ) ) );
                m.put("orderValue", new AttributeValue().withN(Float.toString(((TabClosed)evt).orderValue ) ) );
                m.put("tipValue", new AttributeValue().withN(Float.toString(((TabClosed)evt).tipValue ) ) );
                break;
            case "TabOpened":
                m.put("tableNumber", new AttributeValue().withN(Float.toString(((TabOpened)evt).tableNumber ) ) );
                m.put("waiter", new AttributeValue().withS(((TabOpened)evt).waiter ) );
                break;
        }
        
        return m;
    }
    
    static Event from(Map<String, AttributeValue> item) {
        String evtType = item.get("type").getS();
        switch (evtType) {
            case "DrinksOrdered":
                return new DrinksOrdered(
                                UUID.fromString(item.get("id").getS()), 
                                fromAttributeValues(item.get("items").getL()));
            case "DrinksServed":
                return new DrinksServed(
                                UUID.fromString(item.get("id").getS()), 
                                item.get("menuNumbers").getL().stream().map(i->parseInt(i.getN())).collect(toList()));
            case "FoodOrdered":
                return new FoodOrdered(
                                UUID.fromString(item.get("id").getS()), 
                                fromAttributeValues(item.get("items").getL()));
            case "FoodPrepared":
                return new FoodPrepared(
                                UUID.fromString(item.get("id").getS()), 
                                item.get("menuNumbers").getL().stream().map(i->parseInt(i.getN())).collect(toList()));
            case "FoodServed":
                return new FoodServed(
                                UUID.fromString(item.get("id").getS()), 
                                item.get("menuNumbers").getL().stream().map(i->parseInt(i.getN())).collect(toList()));
            case "TabClosed":
                return new TabClosed(
                                UUID.fromString(item.get("id").getS()), 
                                parseFloat(item.get("amountPaid").getN()), 
                                parseFloat(item.get("orderValue").getN()), 
                                parseFloat(item.get("tipValue").getN()));
            case "TabOpened":
                return new TabOpened(
                                UUID.fromString(item.get("id").getS()), 
                                parseInt(item.get("tableNumber").getN()), 
                                item.get("waiter").getN());
            default:
                throw new IllegalStateException(format("Unrecognized event: %s", evtType));
        }
    }
    
    static List<OrderedItem> fromAttributeValues(List<AttributeValue> items) {
        return items.stream()
            .map(
                ai -> new OrderedItem(
                    new Float(ai.getM().get("menuNumber").getN()).intValue(), 
                    ai.getM().get("description").getS(), 
                    parseFloat(ai.getM().get("price").getN()),
                    ai.getM().get("isDrink").getBOOL()
                )
            )
            .collect(toList());
    }
    
    static List<AttributeValue> fromOrderedItems(List<OrderedItem> items) {
        return items.stream().map( it -> {
                Map<String, AttributeValue> mIt = new HashMap<>();
                mIt.put("description", new AttributeValue().withS(it.description));
                mIt.put("menuNumber", new AttributeValue().withN(Float.toString(it.menuNumber)));
                mIt.put("price", new AttributeValue().withN(Float.toString(it.price)));
                mIt.put("isDrink", new AttributeValue().withBOOL(it.isDrink));
                return new AttributeValue().withM(mIt);
        })
        .collect(toList());
    }

}
