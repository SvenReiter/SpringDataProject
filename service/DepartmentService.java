package org.example.service;

import org.example.model.Department;
import org.example.model.Employee;
import org.example.model.Project;
import org.example.repository.DepartmentRepository;
import org.example.repository.EmployeeRepository;
import org.example.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {
    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ProjectRepository projectRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository= departmentRepository;
    }

    public ResponseEntity<String>getDepartmentOfProject(Long pId) {
        for(Department d: departmentRepository.findAll()) {
            for(Project p: d.getControls()) {
                if(p.getId().compareTo(pId)== 0) {
                    return ResponseEntity.status(HttpStatus.OK).body(d.toString());
                }
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No relation of Project["+pId+"] to any <Department> found!");
    }

    public ResponseEntity<String> remove(Long id) {
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

    public ResponseEntity<Integer> getNrOfEmployees(Long dId) {
        for(Department d: departmentRepository.findAll()) {
            if(d.getId().compareTo(dId)== 0) {
                return ResponseEntity.status(HttpStatus.OK).body(d.getWorksFor().size());
            }
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(0);
    }

    public ResponseEntity<Department> save(Department department) {
        return ResponseEntity.status(HttpStatus.OK).body(departmentRepository.save(department));
    }

    public ResponseEntity<String> addProject(String dId, String pId) {
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

    public ResponseEntity<String> addEmployee(String dId, String eId) {
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
