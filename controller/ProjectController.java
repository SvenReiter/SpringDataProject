package org.example.controller;

import org.example.model.Department;
import org.example.model.Project;
import org.example.model.WorksOn;
import org.example.repository.DepartmentRepository;
import org.example.repository.EmployeeRepository;
import org.example.repository.ProjectRepository;
import org.example.repository.WorksOnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@RestController
@RequestMapping("/project") // NOT @RequestMapping(name="/project") -> the significant parameter is not called "name"!!!
public class ProjectController {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorksOnRepository worksOnRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public ProjectController(ProjectRepository projectRepository, EmployeeRepository employeeRepository,
                             WorksOnRepository worksOnRepository, DepartmentRepository departmentRepository) {
        this.projectRepository= projectRepository;
        this.employeeRepository= employeeRepository;
        this.worksOnRepository= worksOnRepository;
        this.departmentRepository= departmentRepository;
    }

    /**
     * @param dId - departmentsId of Project[pId]
     * @param pId - Projects id
     * @param location
     * @return HttpStatus.OK                    "<location> added to Department[<dId>] and Project[<pId>]"
     *         HttpStatus.NOT_FOUND             "Project[<id>] not found!"
     *         HttpStatus.NOT_FOUND             "Project[<id>] not found!" */
    @PostMapping("setLocation/{dId}/{pId}/{location}")
    private ResponseEntity<String> setLocation(@PathVariable("dId")Long dId,
                                               @PathVariable("pId")Long pId,
                                               @PathVariable("location")String location) {
        for(Department d: departmentRepository.findAll()) {
            if(d.getId().compareTo(dId)== 0) {
                for(Project p: d.getControls()) {
                    if(p.getId().compareTo(pId)== 0) {
                        p.setLocation(location);
                        projectRepository.flush();

                        d.getLocations().clear();
                        for(Project project: d.getControls()) {
                            d.addLocation(project.getLocation());
                        }
                        departmentRepository.flush();
                        return ResponseEntity.status(HttpStatus.OK).body(location +
                                " added to Department["+dId+"] and Project["+pId+"]");
                    }
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project["+pId+"] not found!");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department["+dId+"] not found!");
    }

    /**
     * @param project - Project to persist
     * @return HttpStatus.OK                    "<project>" */
    @PostMapping
    private ResponseEntity<Project> save(@RequestBody Project project) {
        return ResponseEntity.status(HttpStatus.OK).body(projectRepository.save(project));
    }

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
     * @return  HttpStatus.NOT_FOUND             "Project[<id>] not found!"
     *          HttpStatus.OK                    "Project[<id>] deleted in Project-table.
     *                                           Reference/s to Project[<id>] found/deleted in/from
     *                                              -employeeWorksOnProject[n]
     *                                              -controls[m] */
    @PostMapping("removeCascade/{id}")
    private ResponseEntity<String> removeCascade(@PathVariable("id")Long id) {
        Project project= null;
        for(Project p: projectRepository.findAll()) {
            if(p.getId().compareTo(id)== 0) {
                project= p;
            }
        }
        if(project== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project[" + id + "] not found!");
        }
        StringBuilder result= new StringBuilder(
                "Project["+id+"] deleted in Project-table.\nReference/s to Project["+id+"] found/deleted in/from\n\t");

        for(WorksOn w: worksOnRepository.findAll()) {
            if(w.getProject_id().compareTo(id)== 0) {
                result.append("-employeeWorksOnProject["+w.getId()+"]\n\t");
                worksOnRepository.delete(w);
                worksOnRepository.flush();
            }
        }
        for(Department d: departmentRepository.findAll()) {
            if(d.getControls().contains(project)) {
                result.append("-controls["+d.getId()+"]\n\t");
                d.getControls().remove(project);
                departmentRepository.save(d);

                d.getLocations().clear();
                for(Project p: d.getControls()) {
                    d.addLocation(p.getLocation());
                }
                departmentRepository.flush();
            }
        }
        projectRepository.deleteById(id);
        projectRepository.flush();

        return ResponseEntity.status(HttpStatus.OK).body(result.toString());
    }

    /**
     * @param id - Project's id
     * @return HttpStatus.OK                    "Project[<id>] removed!"
     *         HttpStatus.NOT_FOUND             "Project[<id>] not found!"
     *         HttpStatus.CONFLICT              "Project[<id>] still references Employee/s!" */
    @PostMapping("remove/{id}")
    private ResponseEntity<String> remove(@PathVariable("id")Long id) {
        boolean projectFound= false;
        for(Project p: projectRepository.findAll()) {
            if(p.getId().compareTo(id)== 0) {
                projectFound= true;
                break;
            }
        }
        if(!projectFound) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project[" + id + "] not found!");
        }
        for(WorksOn wo: worksOnRepository.findAll()) {
            if(wo.getProject_id().compareTo(id)== 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Project["+id+"] still references Employee/s!");
            }
        }
        projectRepository.deleteById(id);
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

    /**
     * @param pId - Project's id
     * @return HttpStatus.OK                    <Department> responsable for Project[<id>]
     *         HttpStatus.NOT_FOUND             "Project[<id>] not found"
     *         HttpStatus.NOT_FOUND             "No Department found referencing to Project[<id>]" */
    @GetMapping("getDepartment/{pId}")
    private ResponseEntity<String> getDepartmentOf(@PathVariable("pId")Long pId) {
        boolean projectAvailable= false;
        for(Project p: projectRepository.findAll()) {
            if(p.getId()== pId) {
                projectAvailable= true;
                break;
            }
        }
        if(!projectAvailable) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project[" + pId + "] not found");
        }
        for(Department d: departmentRepository.findAll()) {
            for(Project p: d.getControls()) {
                if(p.getId().equals(pId)) {
                    return ResponseEntity.status(HttpStatus.OK).body(d.toString());
                }
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Department found referencing to Project["+pId+"]");
    }
}
