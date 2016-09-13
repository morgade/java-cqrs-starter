package org.morgade.menu;

import java.util.ArrayList;
import org.morgade.menu.vo.MenuItem;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toMap;
import org.springframework.stereotype.Repository;

/**
 *
 * @author x4rb
 */
@Repository
public class MenuRepositoryStatic implements MenuRepository {
    private final Map<Integer, MenuItem> menuMap;
    
    public MenuRepositoryStatic() {
        menuMap = Arrays.asList(
            new MenuItem(1, "Coke", 1.50f, true),
            new MenuItem(2, "Green Tea", 1.90f, true),
            new MenuItem(3, "Freshly Ground Coffee", 2.00f, true),
            new MenuItem(4, "Czech Pilsner", 3.50f, true),
            new MenuItem(5, "Yeti Stout", 4.50f, true),
            new MenuItem(10, "Mushroom & Bacon Pasta", 6.00f),
            new MenuItem(11, "Chili Con Carne", 7.50f),
            new MenuItem(12, "Borsch With Smetana", 4.50f),
            new MenuItem(13, "Lamb Skewers with Tatziki", 8.00f),
            new MenuItem(14, "Beef Stroganoff", 8.50f)
        )
        .stream()
        .collect(toMap(mi->mi.getMenuNumber(), mi->mi));
    }
    
    
    @Override
    public List<MenuItem> findAll() {
        return new ArrayList(menuMap.values());
    }

    @Override
    public MenuItem get(int id) {
        return menuMap.get(id);
    }

}
