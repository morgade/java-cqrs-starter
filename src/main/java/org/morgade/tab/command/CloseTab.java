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
public class CloseTab implements Command {
    public UUID id;
    public float amountPaid;

    public CloseTab(UUID id, float amountPaid) {
        this.id = id;
        this.amountPaid = amountPaid;
    }

    @Override
    public UUID getId() {
        return id;
    }
    
}
