/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.morgade.tab.command;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.morgade.cqrs.Command;

/**
 *
 * @author x4rb
 */
public class MarkDrinksServed implements Command {
    public UUID id;
    public List<Integer> menuNumbers;

    public MarkDrinksServed(UUID id, List<Integer> menuNumbers) {
        this.id = id;
        this.menuNumbers = Collections.unmodifiableList(menuNumbers);
    }

    @Override
    public UUID getId() {
        return id;
    }
    
}
