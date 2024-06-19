package org.example.repository;

import org.example.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    //fetch Employees ordered by salary
    List<Employee> findTopByOrderBySalaryDesc();

    //fetch every female/male employee
    List<Employee> findBySex(char sex);

    //fetch employees younger than <timestamp>
    List<Employee> findByBirthdayBefore(LocalDate date);

    //fetch employees having salary less than
    List<Employee> findBySalaryLessThan(float maxSalary);
}
