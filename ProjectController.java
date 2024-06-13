package org.example.controller;

import org.example.model.Project;
import org.example.model.WorksOn;
import org.example.repository.EmployeeRepository;
import org.example.repository.ProjectRepository;
import org.example.repository.WorksOnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project") //  @RequestMapping("/project") NOT @RequestMapping(name="/project") -> the significant parameter is not called "name"!!!
public class ProjectController {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorksOnRepository worksOnRepository;

    public ProjectController(ProjectRepository projectRepository, EmployeeRepository employeeRepository,
                             WorksOnRepository worksOnRepository) {
        this.projectRepository= projectRepository;
        this.employeeRepository= employeeRepository;
        this.worksOnRepository= worksOnRepository;
    }

    /**
     * @param project - Project to persist
     * @return HttpStatus.OK                    "Project[<id>]" */
    @PostMapping
    private ResponseEntity<Project> save(@RequestBody Project project) {
        return ResponseEntity.status(HttpStatus.OK).body(projectRepository.save(project));
    }

    //todo ändern und löschen von Datensätzen

    /**
     * @param id - Project's id
     * @return HttpStatus.OK                    "Project[<id>]"
     *         HttpStatus.NOT_FOUND             "Project[<id>] not found!" */
    @PostMapping("change/{pId}")
    private ResponseEntity<String> change(@RequestBody Project project, @PathVariable("id")Long id) {
        for(Project p: projectRepository.findAll()) {
            if(p.getId().compareTo(id)== 0) {
                p.setName(project.getName());
                projectRepository.flush();
                return ResponseEntity.status(HttpStatus.OK).body(p.toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project["+id+"] not found!");
    }

    /**
     * @param id - Project's id
     * @return HttpStatus.OK                    "Project[<id>] removed!"
     *         HttpStatus.NOT_FOUND             "Project[<id>] not found!"
     *         HttpStatus.CONFLICT              "Project[<id>] still references Employee/s!" */
    @PostMapping("remove/{id}")
    private ResponseEntity<String> removeProject(@PathVariable("id")String id) {
        Project project= projectRepository.getReferenceById(Long.parseLong(id));
        boolean projectFound= false;
        for(Project p: projectRepository.findAll()) {
            if(p.getId().compareTo(Long.parseLong(id))== 0) {
                projectFound= true;
                break;
            }
        }
        if(!projectFound) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project[" + id + "] not found!");
        }
        for(WorksOn wo: worksOnRepository.findAll()) {
            if(wo.getProject_id().compareTo(Long.parseLong(id))== 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Project["+id+"] still references Employee/s!");
            }
        }
        projectRepository.deleteById(Long.parseLong(id));
        return ResponseEntity.status(HttpStatus.OK).body("Project["+id+"] removed");
    }

    /**
     * @param id - Project's id
     * @return HttpStatus.OK                    "Project[<id>]"
     *         HttpStatus.NOT_FOUND             "Project[<id>] not found!" */
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
