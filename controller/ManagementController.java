package org.example.controller;

import org.example.model.Department;
import org.example.model.Employee;
import org.example.model.Management;
import org.example.repository.DepartmentRepository;
import org.example.repository.EmployeeRepository;
import org.example.repository.ManagementRepository;
import org.example.service.ManagementService;
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
    ManagementService service;

    /**
     * @param dId   Departments ID to query the management of
     * @return HttpStatus.NOT_FOUND             ""
     * @return HttpStatus.OK                    "<Employee[]> */
    @GetMapping("get/{dId}")
    private ResponseEntity<List<Employee>>get(@PathVariable("dId")Long dId) {
        return service.get(dId);
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
    private ResponseEntity<String> bind(@PathVariable("eId")Long eId, @PathVariable("dId")Long dId,
                                        @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate) {
        return service.bind(eId, dId, startDate);
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
    private ResponseEntity<String> change(@PathVariable("eId")Long eId, @PathVariable("dId")Long dId,
                                          @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate) {
        return service.change(eId, dId, startDate);
    }
}
