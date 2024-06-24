package org.example.controller;

import org.example.model.Employee;
import org.example.model.Project;
import org.example.model.WorksOn;
import org.example.repository.EmployeeRepository;
import org.example.repository.ProjectRepository;
import org.example.repository.WorksOnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/worksOn")
public class WorksOnController {
    @Autowired
    private WorksOnRepository repository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    //todo removeBonds, removeBondsCascade

    /**
     * @param eId employees id
     * @param pId projects id
     * @return HttpStatus.NOT_FOUND         "Employee[<eId>] not found"
     *         HttpStatus.NOT_FOUND         "Project[<pId>] not found"
     *         HttpStatus.ALREADY_REPORTED  "Employee[<eId>] is already working on Project[<pId>]"
     *         HttpStatus.OK                "Employee[<eId>] works Project[<pId>]!" */
    @PostMapping("bind/{eId}/{pId}")
    private ResponseEntity<String>makeEmployeeWorkOnProject(@PathVariable("eId")String eId,
                                                            @PathVariable("pId")String pId) {
        Employee employee= null;
        for(Employee e: employeeRepository.findAll()) {
            if(e.getId().compareTo(Long.parseLong(eId))== 0) { employee= e; break; }
        }
        if(employee== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+eId+"] not found!");
        }

        Project project= null;
        for(Project p: projectRepository.findAll()) {
            if(p.getId().compareTo(Long.parseLong(pId))== 0) { project= p; break; }
        }
        if(project== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project["+pId+"] not found!");
        }

        for(WorksOn wo: repository.findAll()) {
            if(wo.getProject_id().compareTo(Long.parseLong(pId))== 0 &&
               wo.getEmployee_id().compareTo(Long.parseLong(eId))== 0) {
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(
                        "Employee["+eId+"] is already working on Project["+pId+"]!");
            }
        }

        WorksOn worksOnEntity= new WorksOn(employee.getId(), project.getId());
        repository.save(worksOnEntity);
        return ResponseEntity.status(HttpStatus.OK).body("Employee["+eId+"] works Project["+pId+"]!");
    }

    /**
     * @param eId
     * @param pId
     * @param hrs
     * @return HttpStatus.NOT_FOUND         "Employee[<eId>] not found"
     *         HttpStatus.OK                "Employee[<eId>] worked <wo.getHours()>hrs on Project[<pId>] so far" */
    @PostMapping("incrementHours/{eId}/{pId}/{hrs}")
    private ResponseEntity<String>incrHrs(@PathVariable("eId")String eId, @PathVariable("pId")String pId,
                                          @PathVariable("hrs")String hrs) {
        for(WorksOn wo: repository.findAll()) {
            if(wo.getEmployee_id().compareTo(Long.parseLong(eId))== 0 &&
               wo.getProject_id().compareTo(Long.parseLong(pId))== 0) {
               wo.incrementHours(Integer.parseInt(hrs));
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
