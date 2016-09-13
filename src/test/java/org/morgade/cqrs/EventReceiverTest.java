package org.morgade.cqrs;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;

/**
 * Provides infrastructure for a set of tests on a given event receiver.
 *
 * @author x4rb
 * @param <R>
 */
public abstract class EventReceiverTest<R extends EventSubscriber> {
    private final Class<R> projectionClass;
    private R instance;

    public EventReceiverTest(Class<R> sutClass) {
        this.projectionClass = sutClass;
    }

    @Before
    public void projectionSetup() {
        try {
            instance = projectionClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected R given(Event ... events) {
        return applyEvents(instance, Arrays.asList(events));
    }
    
    protected R applyEvents(R receiver, List<? extends Event> events) {
        events.forEach(e->receiver.dispatch(e));
        return receiver;
    }

}
