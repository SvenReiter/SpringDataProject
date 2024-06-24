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

    /**
     *
     * @param employee - json representation
     * @return HttpStatus.OK                    "Employee{employee}" */
    @PostMapping
    private ResponseEntity<Employee> save(@RequestBody Employee employee) {
        return service.save(employee);
    }

    /**
     * @param employee - new Employee-context
     * @param id - Employee's id
     * @return HttpStatus.OK                    "Employee[<id>] got changed!"
     *         HttpStatus.NOT_FOUND             "Employee[<id>] not found!" */
    @PostMapping("change/{id}")
    private ResponseEntity<String> change(@RequestBody Employee employee, @PathVariable("id")Long id) {
        return service.change(employee, id);
    }

    //todo remove, removeCascade

    /**
     * @param id - to remove Employee's ID
     * @return HttpStatus.OK                    "Employee[<id>] removed!" */
    @PostMapping("remove/{id}")
    private ResponseEntity<String> removeEmployee(@PathVariable("id")Long id) {
        return service.removeEmployee(id);
    }

    /**
     * @param id - demanded Employee's ID
     * @return HttpStatus.OK                    "Employee[<id>]"
     *         HttpStatus.NOT_FOUND             "Employee[<id>] not found!" */
    @GetMapping("/get/{id}")
    private ResponseEntity<String> getEmployee(@PathVariable("id")Long id) {
        return service.getEmployee(id);
    }

    /**
     * @return HttpStatus.OK                    List of "Employee.fullName supervises Employee.fullName" */
    @GetMapping("getSupervision")
    private ResponseEntity<String>getSupervision() {
        return service.getSupervision();
    }

    /**
     * @param sId - the supervisors Employee's ID
     * @param eId - Employee's ID to designate supervisor to
     * @return HttpStatus.NOT_FOUND             "Employee[<eId>] not found!"
     *         HttpStatus.NOT_FOUND             "Employee[<eId>] not found!"
     *         HttpStatus.OK                    "Supervisor set!" */
    @PostMapping("setSupervisor/{sId}/{eId}")
    private ResponseEntity<String> setSupervision(@PathVariable("sId")Long sId, @PathVariable("eId")Long eId) {
        return service.setSupervision(sId, eId);
    }

    /**
     * @param sex - f/F: female, m/M: male
     * @return  List of <full Names> of <Employees having matched sex to <sex> */
    @GetMapping("/bySex/{sex}")
    private ResponseEntity<List<String>> getBySex(@PathVariable("sex")char sex) {
        return service.getBySex(sex);
    }

    /**
     * @param date - yyyy-MM-dd
     * @return  List of <fullNames> of <Employee>s older than <date> */
    @GetMapping("/byBirthdayBefore/{date}")
    private ResponseEntity<List<String>> getByBirthdayBefore(@PathVariable("date")LocalDate date) {
        return service.getByBirthdayBefore(date);
    }

    /**
     * @param salary - salary to compare with
     * @return  List of <fullNames> of <Employee>s getting less salary than given <salary> */
    @GetMapping("/bySalaryLessThan/{salary}")
    private ResponseEntity<List<String>> getSalaryLessThan(@PathVariable("salary")float salary) {
        return service.getSalaryLessThan(salary);
    }

    /**
     * @return HttpStatus.OK                    <Employee> having the highest salary */
    @GetMapping("/maxSalary")
    private ResponseEntity<Employee> getEmployeeByMaxSalary() {
        return service.getEmployeeByMaxSalary();
    }
}
