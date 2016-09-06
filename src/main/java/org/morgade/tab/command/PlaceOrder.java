/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.morgade.tab.command;

import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.Validate;
import org.morgade.cqrs.Command;
import org.morgade.tab.vo.OrderedItem;

/**
 *
 * @author x4rb
 */
public class PlaceOrder implements Command  {
    public final UUID id;
    public final List<OrderedItem> items;

    public PlaceOrder(UUID id, List<OrderedItem> items) {
        this.id = id;
        this.items = items;
    }

    @Override
    public UUID getId() {
        return id;
    }
    
}
