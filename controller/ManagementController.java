package org.example.controller;

import org.example.model.Department;
import org.example.model.Employee;
import org.example.model.Management;
import org.example.repository.DepartmentRepository;
import org.example.repository.EmployeeRepository;
import org.example.repository.ManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/management")
public class ManagementController {
    @Autowired
    ManagementRepository managesRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    /**
     * @param dId   Departments ID to query the management of
     * @return HttpStatus.NOT_FOUND             ""
     * @return HttpStatus.OK                    "<Employee[]> */
    @GetMapping("get/{dId}")
    ResponseEntity<List<Employee>>get(@PathVariable("dId")Long dId) {
        List<Employee> result= new ArrayList<>();
        for(Department d: departmentRepository.findAll()) {
            if(d.getId().compareTo(dId)== 0) {
                for(Management m: d.getManagement()) {
                    for(Employee e: employeeRepository.findAll()) {
                        if(e.getId().compareTo(m.getEmployee_id())== 0) {
                            result.add(e);
                        }
                    }
                }
                return ResponseEntity.status(HttpStatus.OK).body(result);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    /**
     * @param dId
     * @param eId
     * @param startDate  dd-MM-yyyy
     * @return HttpStatus.NOT_FOUND             "Employee[<eId>] not found!"
     * @return HttpStatus.NOT_FOUND             "Department[<dId>] not found!"
     * @return HttpStatus.EXPECTATION_FAILED    "Department[<dId>]'s manager already appointed,
     *                                           you might change the manager!"
     * @return HttpStatus.OK                    "<Employee[<eId>].name> is Department <Department[dId].name>s manager
     *                                           from <Department[dId].startDate> on!"                                */
    @PostMapping("bind/{eId}/{dId}/{startDate}")
    private ResponseEntity<String> bind(@PathVariable("eId")String eId, @PathVariable("dId")String dId,
                                        @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate) {
        Employee employee= null;
        for(Employee e: employeeRepository.findAll()) {
            if(e.getId().compareTo(Long.parseLong(eId))== 0) { employee= e; break; }
        }
        if(employee== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+eId+"] not found!");
        }

        Department department = null;
        for(Department d: departmentRepository.findAll()) {
            if(d.getId().compareTo(Long.parseLong(dId))== 0) { department= d; break; }
        }
        if(department== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department["+dId+"] not found!");
        }

        if(department.getManagement()!= null && !department.getManagement().isEmpty()) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                    "Department["+dId+"]'s manager already appointed, you might change the manager!");
        }
        Management m= new Management();
        m.setEmployee_id(Long.parseLong(eId));
        m.setDepartment_id(Long.parseLong(dId));
        m.setStartDate(startDate);
        managesRepository.save(m);
        managesRepository.flush();

        if(department.getManagement().isEmpty()) {
            department.getManagement().add(m);
        }else {
            department.getManagement().set(0,m);
        }
        departmentRepository.save(department);
        departmentRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).body(employee.getFirstName()+" "+employee.getLastName()+
                " is Department "+department.getName()+"s manager from "+
                startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))+" on!");

    }

    /**
     * @param dId
     * @param eId
     * @param startDate  dd-MM-yyyy
     * @return HttpStatus.NOT_FOUND             "Employee[<eId>] not found!"
     * @return HttpStatus.NOT_FOUND             "Department[<dId>] not found!"
     * @return HttpStatus.EXPECTATION_FAILED    "Department[<dId>]'s manager is not appointed,
     *                                           you might bind a manager!"
     * @return HttpStatus.OK                    "<Employee[<eId>].name> is Department <Department[dId].name>s manager
     *                                           from <Department[dId].startDate> on!"                                */
    @PostMapping("change/{eId}/{dId}/{startDate}")
    private ResponseEntity<String> change(@PathVariable("eId")String eId, @PathVariable("dId")String dId,
                                          @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate) {
        Employee employee= null;
        for(Employee e: employeeRepository.findAll()) {
            if(e.getId().compareTo(Long.parseLong(eId))== 0) { employee= e; break; }
        }
        if(employee== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+eId+"] not found!");
        }

        Department department = null;
        for(Department d: departmentRepository.findAll()) {
            if(d.getId().compareTo(Long.parseLong(dId))== 0) { department= d; break; }
        }
        if(department== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department["+dId+"] not found!");
        }

        if(department.getManagement()== null) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                    "Department["+dId+"]'s manager is not appointed, you might bind a manager!");
        }

        for(Management m: managesRepository.findAll()) {
            if(m.getDepartment_id().compareTo(department.getId())== 0) {
                m.setEmployee_id(Long.parseLong(eId));
                m.setDepartment_id(Long.parseLong(dId));
                m.setStartDate(startDate);
                managesRepository.save(m);
                managesRepository.flush();

                department.getManagement().set(0,m);
                departmentRepository.save(department);
                departmentRepository.flush();
                return ResponseEntity.status(HttpStatus.OK).body(employee.getFirstName()+" "+employee.getLastName()+
                        " is Department "+department.getName()+"s manager from "+
                        startDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))+" on!");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department["+dId+"] not found!");
    }
}
