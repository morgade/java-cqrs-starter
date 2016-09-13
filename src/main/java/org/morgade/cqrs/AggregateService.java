package org.morgade.cqrs;

import java.util.UUID;

/**
 *
 * @author x4rb
 */
public interface AggregateService {
    void handle(Command command);
    <P extends EventSubscriber> P load(Class<P> projectionClass, UUID id);
}
