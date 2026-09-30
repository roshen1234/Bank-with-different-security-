package com.eazybytes.springsecsection1.doa;

import com.eazybytes.springsecsection1.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//We use this
@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Integer> {

}
