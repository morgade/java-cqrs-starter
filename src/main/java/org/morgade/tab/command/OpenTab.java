/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.morgade.tab.command;

import java.util.UUID;
import org.morgade.cqrs.Command;

/**
 *
 * @author x4rb
 */
public class OpenTab implements Command {
    public UUID id;
    public int tableNumber;
    public String waiter;

    public OpenTab(UUID id, int tableNumber, String waiter) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.waiter = waiter;
    }

    @Override
    public UUID getId() {
        return id;
    }
    
}
