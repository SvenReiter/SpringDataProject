package org.example.controller;

import org.example.model.*;
import org.example.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    DepartmentService service;

    /**
     * @param id - Department's id
     * @return HttpStatus.OK                    "Department[<id>] removed!"
     *         HttpStatus.NOT_FOUND             "Department[<id>] not found!"
     *         HttpStatus.CONFLICT              "Department[<id>] still references Employee/s!"
     *         HttpStatus.CONFLICT              "Department[<id>] still references a Manager!" */
    @PostMapping("remove/{id}")
    private ResponseEntity<String> remove(@PathVariable("id")Long id) {
        return service.remove(id);
    }

    /**
     * @param pId
     * @return HttpStatus.OK                    "Department[<id>]"
     *         HttpStatus.NOT_FOUND             "No relation of Project[<pId>] to any <Department> found!" */
    @GetMapping("getDepartmentOfProject/{pId}")
    private ResponseEntity<String>getDepartmentOfProject(@PathVariable("pId")Long pId) {
        return service.getDepartmentOfProject(pId);
    }

    /**
     * @param dId
     * @return HttpStatus.OK                    <Nr of employees>
     * @return HttpStatus.EXPECTATION_FAILED    0 */
    @GetMapping("nrOfEmployees/{dId}")
    private ResponseEntity<Integer> getNrOfEmployees(@PathVariable("dId")Long dId) {
        return service.getNrOfEmployees(dId);
    }

    /**
     * @param department
     * @return HttpStatus.OK                    <department> */
    @PostMapping
    private ResponseEntity<Department> save(@RequestBody Department department) {
        return service.save(department);
    }

    /**
     * @param dId
     * @param pId
     * @return HttpStatus.NOT_FOUND    "Department[<dId>] not found!"
     * @return HttpStatus.NOT_FOUND    "Project[<pId>] not found!"
     * @return HttpStatus.OK           "Project <Project.name> added to Department <Department.name>!" */
    @PostMapping("addProject/{dId}/{pId}")
    private ResponseEntity<String> addProject(@PathVariable("dId")String dId, @PathVariable("pId")String pId) {
        return service.addProject(dId, pId);
    }

    /**
     * @param dId
     * @param eId
     * @return HttpStatus.NOT_FOUND    "Department[<dId>] not found!"
     * @return HttpStatus.NOT_FOUND    "Employee[<eId>] not found!"
     * @return HttpStatus.OK           "Employee <Employee.fullName> works for Department <Department.name>!" */
    @PostMapping("addEmployeeWorkingFor/{dId}/{eId}")
    private ResponseEntity<String> addEmployee(@PathVariable("dId")String dId, @PathVariable("eId")String eId) {
        return service.addEmployee(dId, eId);
    }
}
