package org.morgade.staff;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author x4rb
 */
@Repository
public class StaffRepositoryStatic implements StaffRepository {
    private final List<String> staffList;
    
    public StaffRepositoryStatic() {
        staffList = Arrays.asList(
            "Jack", "Lena", "Pedro", "Anastasia"
        );
    }
    
    
    @Override
    public List<String> findAll() {
        return staffList;
    }

}
