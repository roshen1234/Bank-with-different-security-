package com.eazybytes.springsecsection1.doa;

import com.eazybytes.springsecsection1.entity.Employee;

import java.util.List;

//not used now as we use EmployeeRepository
public interface EmployeeDOA {

    public List<Employee> findAll();
    public Employee findById(int id);
    public Employee save(Employee employee);
    public void deleteById(int id);

}

