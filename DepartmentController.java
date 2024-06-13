package org.example.controller;

import org.example.model.Department;
import org.example.model.Employee;
import org.example.model.Project;
import org.example.repository.DepartmentRepository;
import org.example.repository.EmployeeRepository;
import org.example.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/department")
public class DepartmentController {
    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ProjectRepository projectRepository;

    public DepartmentController(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository,
                                ProjectRepository projectRepository) {
        this.departmentRepository= departmentRepository;
        this.employeeRepository= employeeRepository;
        this.projectRepository= projectRepository;
    }

    /**
     * @param dId
     * @return HttpStatus.OK                    <Nr of employees>
     * @return HttpStatus.EXPECTATION_FAILED    0
     */
    @GetMapping("nrOfEmployees/{dId}")
    private ResponseEntity<Integer> getNrOfEmployees(@PathVariable("dId")String dId) {
        for(Department d: departmentRepository.findAll()) {
            if(d.getId().compareTo(Long.parseLong(dId))== 0) {
                return ResponseEntity.status(HttpStatus.OK).body(d.getWorksFor().size());
            }
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(0);
    }

    /**
     * @param department
     * @return HttpStatus.OK                    <department> */
    @PostMapping
    private ResponseEntity<Department> save(@RequestBody Department department) {
        return ResponseEntity.status(HttpStatus.OK).body(departmentRepository.save(department));
    }

    /**
     * @param dId
     * @param pId
     * @return HttpStatus.EXPECTATION_FAILED    "Department[<dId>] not found!"
     * @return HttpStatus.EXPECTATION_FAILED    "Project[<pId>] not found!"
     * @return HttpStatus.OK                    "Project <Project.name> added to Department <Department.name>!" */
    @PostMapping("addProject/{dId}/{pId}")
    private ResponseEntity<String> addProject(@PathVariable("dId")String dId, @PathVariable("pId")String pId) {
        Department d= null;
        for(Department i: departmentRepository.findAll()) {
            if(i.getId().compareTo(Long.parseLong(dId))== 0) {
                d= i;
                break;
            }
        }
        if(d== null) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Department["+dId+"] not found!");
        }

        Project p= null;
        for(Project i: projectRepository.findAll()) {
            if(i.getId().compareTo(Long.parseLong(pId))== 0) {
                p= i;
                break;
            }
        }
        if(p== null) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Project["+pId+"] not found!");
        }

        d.setControls(d.addProject(p));
        departmentRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).body("Project "+p.getName()+" added to Department "+d.getName()+"!");
    }

    /**
     * @param dId
     * @param eId
     * @return HttpStatus.EXPECTATION_FAILED    "Department[<dId>] not found!"
     * @return HttpStatus.EXPECTATION_FAILED    "Employee[<eId>] not found!"
     * @return HttpStatus.OK                    "Employee <Employee.fullName> works for Department <Department.name>!" */
    @PostMapping("addEmployeeWorkingFor/{dId}/{eId}")
    private ResponseEntity<String> addEmployee(@PathVariable("dId")String dId, @PathVariable("eId")String eId) {
        Department d= null;
        for(Department i: departmentRepository.findAll()) {
            if(i.getId().compareTo(Long.parseLong(dId))== 0) {
                d= i;
                break;
            }
        }
        if(d== null) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Department["+dId+"] not found!");
        }

        Employee e= null;
        for(Employee i: employeeRepository.findAll()) {
            if(i.getId().compareTo(Long.parseLong(eId))== 0) {
                e= i;
                break;
            }
        }
        if(e== null) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Employee["+eId+"] not found!");
        }

        d.setWorksFor(d.addEmployeeAsWorksFor(e));
        departmentRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).body("Employee "+e.getFirstName()+" "+e.getLastName()+" works for Department "+d.getName()+"!");
    }
}
