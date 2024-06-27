package org.example.controller;

import org.example.model.Employee;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    EmployeeService service;

    @PostMapping
    private ResponseEntity<Employee> save(@RequestBody Employee employee) {
        return service.save(employee);
    }

    @PostMapping("change/{id}")
    private ResponseEntity<String> change(@RequestBody Employee employee, @PathVariable("id")Long id) {
        return service.change(employee, id);
    }

    @PostMapping("removeCascade/{id}")
    private ResponseEntity<String> removeEmployeeCascade(@PathVariable("id")Long id) {
        return service.removeEmployeeCascade(id);
    }
    @PostMapping("remove/{id}")
    private ResponseEntity<String> removeEmployee(@PathVariable("id")Long id) {
        return service.removeEmployee(id);
    }

    @GetMapping("/get/{id}")
    private ResponseEntity<String> getEmployee(@PathVariable("id")Long id) {
        return service.getEmployee(id);
    }

    @GetMapping("getSupervision")
    private ResponseEntity<String>getSupervision() {
        return service.getSupervision();
    }

    @PostMapping("setSupervisor/{sId}/{eId}")
    private ResponseEntity<String> setSupervision(@PathVariable("sId")Long sId, @PathVariable("eId")Long eId) {
        return service.setSupervision(sId, eId);
    }

    @GetMapping("/bySex/{sex}")
    private ResponseEntity<List<String>> getBySex(@PathVariable("sex")char sex) {
        return service.getBySex(sex);
    }

    @GetMapping("/byBirthdayBefore/{date}")
    private ResponseEntity<List<String>> getByBirthdayBefore(@PathVariable("date")LocalDate date) {
        return service.getByBirthdayBefore(date);
    }

    @GetMapping("/bySalaryLessThan/{salary}")
    private ResponseEntity<List<String>> getSalaryLessThan(@PathVariable("salary")float salary) {
        return service.getSalaryLessThan(salary);
    }

    @GetMapping("/maxSalary")
    private ResponseEntity<Employee> getEmployeeByMaxSalary() {
        return service.getEmployeeByMaxSalary();
    }
}
