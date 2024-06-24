package org.example.service;

import org.example.model.Employee;
import org.example.model.Project;
import org.example.model.WorksOn;
import org.example.repository.EmployeeRepository;
import org.example.repository.ProjectRepository;
import org.example.repository.WorksOnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class WorksOnService {
    @Autowired
    private WorksOnRepository repository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public ResponseEntity<String> makeEmployeeWorkOnProject(Long eId, Long pId) {
        Employee employee= null;
        for(Employee e: employeeRepository.findAll()) {
            if(e.getId().compareTo(eId)== 0) { employee= e; break; }
        }
        if(employee== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+eId+"] not found!");
        }

        Project project= null;
        for(Project p: projectRepository.findAll()) {
            if(p.getId().compareTo(pId)== 0) { project= p; break; }
        }
        if(project== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project["+pId+"] not found!");
        }

        for(WorksOn wo: repository.findAll()) {
            if(wo.getProject_id().compareTo(pId)== 0 && wo.getEmployee_id().compareTo(eId)== 0) {
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(
                        "Employee["+eId+"] is already working on Project["+pId+"]!");
            }
        }

        WorksOn worksOnEntity= new WorksOn(employee.getId(), project.getId());
        repository.save(worksOnEntity);
        return ResponseEntity.status(HttpStatus.OK).body("Employee["+eId+"] works Project["+pId+"]!");
    }

    public ResponseEntity<String>incrHrs(Long eId, Long pId, Integer hrs) {
        for(WorksOn wo: repository.findAll()) {
            if(wo.getEmployee_id().compareTo(eId)== 0 && wo.getProject_id().compareTo(pId)== 0) {
                wo.incrementHours(hrs);
                repository.flush();
                employeeRepository.flush();
                projectRepository.flush();
                return ResponseEntity.status(HttpStatus.OK).body(
                        "Employee["+eId+"] worked "+wo.getHours()+"hrs on Project["+pId+"] so far");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                "A Employee["+eId+"]:Project["+pId+"] matching dataset was not found!");
    }
}
