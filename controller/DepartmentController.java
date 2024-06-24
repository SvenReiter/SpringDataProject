package org.example.controller;

import org.example.model.*;
import org.example.repository.DepartmentRepository;
import org.example.repository.EmployeeRepository;
import org.example.repository.ManagementRepository;
import org.example.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/department")
public class DepartmentController {
    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ManagementRepository managementRepository;

    public DepartmentController(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository,
                                ProjectRepository projectRepository, ManagementRepository managementRepository) {
        this.departmentRepository= departmentRepository;
        this.employeeRepository= employeeRepository;
        this.projectRepository= projectRepository;
        this.managementRepository= managementRepository;
    }

    //todo remove, removeCascade

    /**
     * @param id - Department's id
     * @return HttpStatus.OK                    "Department[<id>] removed!"
     *         HttpStatus.NOT_FOUND             "Department[<id>] not found!"
     *         HttpStatus.CONFLICT              "Department[<id>] still references Employee/s!"
     *         HttpStatus.CONFLICT              "Department[<id>] still references a Manager!" */
    @PostMapping("remove/{id}")
    private ResponseEntity<String> remove(@PathVariable("id")Long id) {
        Department departmentToRemove= null;
        for(Department d: departmentRepository.findAll()) {
            if(d.getId().compareTo(id)== 0) {
                departmentToRemove= d;
                break;
            }
        }
        if(departmentToRemove== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department[" + id + "] not found!");
        }
        if(!departmentToRemove.getControls().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Department["+id+"] still references Project/s!");
        }
        if(departmentToRemove.getManagement()!= null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Department["+id+"] still references a Manager!");
        }

        departmentRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Department["+id+"] removed");
    }

    @GetMapping("getDepartmentOfProject/{pId}")
    private ResponseEntity<String>getDepartmentOfProject(@PathVariable("pId")Long pId) {
        for(Department d: departmentRepository.findAll()) {
            for(Project p: d.getControls()) {
                if(p.getId().compareTo(pId)== 0) {
                    return ResponseEntity.status(HttpStatus.OK).body(d.toString());
                }
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No relation of Project["+pId+"] to any <Department> found!");
    }

    /**
     * @param dId
     * @return HttpStatus.OK                    <Nr of employees>
     * @return HttpStatus.EXPECTATION_FAILED    0 */
    @GetMapping("nrOfEmployees/{dId}")
    private ResponseEntity<Integer> getNrOfEmployees(@PathVariable("dId")Long dId) {
        for(Department d: departmentRepository.findAll()) {
            if(d.getId().compareTo(dId)== 0) {
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
     * @return HttpStatus.NOT_FOUND    "Department[<dId>] not found!"
     * @return HttpStatus.NOT_FOUND    "Project[<pId>] not found!"
     * @return HttpStatus.OK           "Project <Project.name> added to Department <Department.name>!" */
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department["+dId+"] not found!");
        }

        Project p= null;
        for(Project i: projectRepository.findAll()) {
            if(i.getId().compareTo(Long.parseLong(pId))== 0) {
                p= i;
                break;
            }
        }
        if(p== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project["+pId+"] not found!");
        }

        for(Project pOfD: d.getControls()) {
            if(pOfD.getId().compareTo(p.getId())== 0) {
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(
                        "Project["+pId+"] already added to Department["+dId+"]!");
            }
        }
        d.setControls(d.addProject(p));
        departmentRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).body("Project "+p.getName()+" added to Department "+d.getName()+"!");
    }

    /**
     * @param dId
     * @param eId
     * @return HttpStatus.NOT_FOUND    "Department[<dId>] not found!"
     * @return HttpStatus.NOT_FOUND    "Employee[<eId>] not found!"
     * @return HttpStatus.OK           "Employee <Employee.fullName> works for Department <Department.name>!" */
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department["+dId+"] not found!");
        }

        Employee e= null;
        for(Employee i: employeeRepository.findAll()) {
            if(i.getId().compareTo(Long.parseLong(eId))== 0) {
                e= i;
                break;
            }
        }
        if(e== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+eId+"] not found!");
        }

        d.setWorksFor(d.addEmployeeAsWorksFor(e));
        departmentRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).body("Employee "+e.getFirstName()+" "+e.getLastName()+" works for Department "+d.getName()+"!");
    }
}
