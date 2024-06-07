package org.example.controller;

import org.example.model.Employee;
import org.example.model.Project;
import org.example.repository.EmployeeRepository;
import org.example.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/project") //  @RequestMapping("/project") NOT @RequestMapping(name="/project") -> the significant parameter is not called "name"!!!
public class ProjectController {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public ProjectController(ProjectRepository projectRepository, EmployeeRepository employeeRepository) {
        this.projectRepository= projectRepository;
        this.employeeRepository= employeeRepository;
    }

    @PostMapping
    private ResponseEntity<Project> save(@RequestBody Project project) {
        return ResponseEntity.status(HttpStatus.OK).body(projectRepository.save(project));
    }

    @PostMapping("{eId}")
    private ResponseEntity<String> saveProjectWithReferencingEmployee(@RequestBody Project project, @PathVariable("eId")String eId) {
        Employee employee= null;
        for(Employee ee: employeeRepository.findAll()) {
            if(ee.getId()== Long.parseLong(eId)) {
                employee= ee;
            }
        }
        if(employee== null) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Employee["+eId+"] not found!");
        }
        if (project.getEmployees() == null) project.setEmployees(new ArrayList<>());
        project.addEmployee(employee);
        projectRepository.save(project);
        if (employee.getProjects() == null) employee.setProjects(new ArrayList<>());
        employee.addProject(project);
        if(project.getEmployees().contains(employee) || employee.getProjects().contains(project)) {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(
                    "Project["+project.getId()+"] already referenced to Employee[" + eId + "]!");
        }
        projectRepository.flush();  //updates hibernate- <projectRepository> and its corresponding DB context
        employeeRepository.flush(); //updates hibernate- <employeeRepository> and its corresponding DB context
        return ResponseEntity.status(HttpStatus.OK).body(
                "Project \""+project.getName()+"\" with Employee["+eId+"] referencing on created!");
    }

    //todo ändern und löschen von Datensätzen

    @PostMapping("remove/{id}")
    private ResponseEntity<String> removeProject(@PathVariable("id")String id) {
        Project project= projectRepository.getReferenceById(Long.parseLong(id));
        if(!project.getEmployees().isEmpty())
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Project["+id+"] still references Employee/s!");
        projectRepository.deleteById(Long.parseLong(id));
        return ResponseEntity.status(HttpStatus.OK).body("Project["+id+"] removed");
    }

    @PostMapping("removeCascade/{id}")
    private ResponseEntity<String> removeCascadeProject(@PathVariable("id")String id) {
        if(projectRepository.findById(Long.parseLong(id)).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project["+id+"] not found");
        Project project= projectRepository.getReferenceById(Long.parseLong(id));
        StringBuilder sb= new StringBuilder();
        if(!project.getEmployees().isEmpty()) {
            sb.append(" and also the workingOn-reference/s of Employee/s[");
            for(int i=0; i<project.getEmployees().size()-1; ++i) {
                project.getEmployees().get(i).getProjects().remove(project);
                sb.append(project.getEmployees().get(i).getFirstName()+" "+
                          project.getEmployees().get(i).getLastName()+", ");
            }
            project.getEmployees().get(project.getEmployees().size()-1).getProjects().remove(project);
            sb.append(project.getEmployees().get(project.getEmployees().size()-1).getFirstName()+" "+
                      project.getEmployees().get(project.getEmployees().size()-1).getLastName()+"]");
        }
        project.setEmployees(null);
        projectRepository.deleteById(Long.parseLong(id));
        return ResponseEntity.status(HttpStatus.OK).body("Project["+id+"] removed"+ sb);
    }

    /**
     * @param pId
     * @param eId
     * @pre Project[pId] and Employee[eId] exist
     * @post Project[pId].employees{...,Employee[eId]}; Employee[eId].projects{...,Project[pId]};
     */
    @PostMapping("/ref/{pId}/{eId}")
    private ResponseEntity<String> referenceProjectAndEmployee(@PathVariable("pId")String pId, @PathVariable("eId")String eId) {
        Project p= null;
        for(Project i: projectRepository.findAll()) {
            if(i.getId().compareTo(Long.parseLong(pId))== 0) {
                p= i;
            }
        }
        if(p== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project[" + pId + "] not found!");
        }
        Employee e= null;
        for(Employee i: employeeRepository.findAll()) {
            if(i.getId().compareTo(Long.parseLong(eId))== 0) {
                e= i;
            }
        }
        if(e== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee[" + eId + "] not found!");
        }
        if(p.getEmployees() == null) p.setEmployees(new ArrayList<>());
        if(e.getProjects() == null) e.setProjects(new ArrayList<>());
        if(p.getEmployees().contains(e) && e.getProjects().contains(p)) {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(
                    "Employee[" + eId + "] and Project["+pId+"] already referenced to each other!");
        }
        p.addEmployee(e);
        e.addProject(p);
        projectRepository.flush();  //updates hibernate- <projectRepository> and its corresponding DB context
        employeeRepository.flush(); //updates hibernate- <employeeRepository> and its corresponding DB context
        return ResponseEntity.status(HttpStatus.OK).body("Referenced Project["+pId+"] and Employee["+eId+"]");
    }

    @GetMapping("/get/{id}")
    private ResponseEntity<String> getProject(@PathVariable("id")String id) {
        Project result= null;
        for(Project p: projectRepository.findAll()) {
            if(p.getId()== Long.parseLong(id)) {
                result= p;
                return ResponseEntity.status(HttpStatus.OK).body(result.toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project["+id+"] not found!");
    }
}
