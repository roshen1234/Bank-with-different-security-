package com.eazybytes.springsecsection1.doa;

import com.eazybytes.springsecsection1.entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeDaoImpl implements  EmployeeDOA{

    EntityManager entityManager;

    public EmployeeDaoImpl(EntityManager entityManager)
    {
        this.entityManager=entityManager;
    }

    @Override
    public List<Employee> findAll() {
        TypedQuery<Employee> emp=entityManager.createQuery("FROM Employee",Employee.class);
        return emp.getResultList();
    }

    @Override
    public Employee findById(int id) {
        Employee emp=entityManager.find(Employee.class,id);
        return emp;
    }

    @Override
    public Employee save(Employee employee) {

        if(employee.id==null)
         {

            entityManager.persist(employee);
         }
         else
         {
             entityManager.merge(employee);
         }

         return employee;

    }

    @Override
    public void deleteById(int id) {
        Employee emp=entityManager.find(Employee.class,id);
        entityManager.remove(emp);
    }
}
