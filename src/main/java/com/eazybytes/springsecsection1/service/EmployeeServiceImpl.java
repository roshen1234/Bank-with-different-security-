package com.eazybytes.springsecsection1.service;

import com.eazybytes.springsecsection1.doa.EmployeeDaoImpl;
import com.eazybytes.springsecsection1.doa.EmployeeRepository;
import com.eazybytes.springsecsection1.entity.Employee;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    EmployeeDaoImpl employeeDao;
    EmployeeRepository employeeRepository;
    public EmployeeServiceImpl(EmployeeDaoImpl employeeDao,EmployeeRepository employeeRepository)
    {

        this.employeeDao=employeeDao;
        this.employeeRepository=employeeRepository;
    }

    @Override
    public List<Employee> findAll() {

        //return employeeDao.findAll();
        return employeeRepository.findAll();
    }

    @Override
    public Employee findById(int id) {

        //return employeeDao.findById(id);
        Optional<Employee>emp=employeeRepository.findById(id);
        if(emp.isPresent())
        {
           return emp.get();
        }

        return null;

    }

    @Override
    @Transactional
    public Employee save(Employee employee) {

        //return employeeDao.save(employee);
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void deleteById(int id) {

        //employeeDao.deleteById(id);
        employeeRepository.deleteById(id);
    }
}
