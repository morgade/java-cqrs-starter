package org.morgade.cqrs;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author x4rb
 * @param <A>
 */
public class AggregateServiceImpl<A extends Aggregate> implements AggregateService {
    private final EventStore eventStore;
    private final Class<A> aggregateType;

    public AggregateServiceImpl(EventStore eventStore, Class<A> aggregateType) {
        this.eventStore = eventStore;
        this.aggregateType = aggregateType;
    }

    @Override
    public void handle(Command command) {
        final EventStream eventStream = eventStore.loadEventStream(command.getId());
        
        // Instantiate base aggregate
        final A aggregate = instantiate(aggregateType);
        // Apply event stream
        aggregate.dispatch(eventStream);
        
        // Dispatch command
        List<? extends Event> newEvents = aggregate.dispatch(command);

        // Store newEvents
        eventStore.store(command.getId(), eventStream.getVersion(), newEvents);
    }
    
    @Override
    public <P extends EventSubscriber> P load(Class<P> projectionClass, UUID id) {
        final EventSubscriber projection = instantiate(projectionClass);
        final EventStream eventStream = eventStore.loadEventStream(id);
        projection.dispatch(eventStream);
        return (P) projection;
    }
    
    private <O> O instantiate(Class<O> targetClass) {
        try {
            return targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
