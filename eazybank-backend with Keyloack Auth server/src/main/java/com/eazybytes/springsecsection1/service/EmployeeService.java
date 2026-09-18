package com.eazybytes.springsecsection1.service;

import com.eazybytes.springsecsection1.entity.Employee;

import java.util.List;

public interface EmployeeService {
    public List<Employee> findAll();
    public Employee findById(int id);
    public Employee save(Employee employee);
    public void deleteById(int id);
}
