package org.morgade.menu;

import org.morgade.menu.vo.MenuItem;
import java.util.List;

/**
 *
 * @author x4rb
 */
public interface MenuRepository {
    public List<MenuItem> findAll();
    public MenuItem get(int id);
}
