package org.example.controller;

import org.example.model.Project;
import org.example.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project") // NOT @RequestMapping(name="/project") -> the significant parameter is not called "name"!!!
public class ProjectController {

    @Autowired
    private ProjectService service;

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
        return service.setLocation(dId, pId, location);
    }

    /**
     * @param project - Project to persist
     * @return HttpStatus.OK                    "<project>" */
    @PostMapping
    private ResponseEntity<Project> save(@RequestBody Project project) {
        return service.save(project);
    }

    /**
     * @param id - Project's id
     * @return HttpStatus.OK                    "Project[<id>]"
     *         HttpStatus.NOT_FOUND             "Project[<id>] not found!" */
    @PostMapping("change/{pId}")
    private ResponseEntity<String> change(@RequestBody Project project, @PathVariable("id")Long id) {
        return service.change(project, id);
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
        return service.removeCascade(id);
    }

    /**
     * @param id - Project's id
     * @return HttpStatus.OK                    "Project[<id>] removed!"
     *         HttpStatus.NOT_FOUND             "Project[<id>] not found!"
     *         HttpStatus.CONFLICT              "Project[<id>] still references Employee/s!" */
    @PostMapping("remove/{id}")
    private ResponseEntity<String> remove(@PathVariable("id")Long id) {
        return service.remove(id);
    }

    /**
     * @param id - Project's id
     * @return HttpStatus.OK                    "Project[<id>]"
     *         HttpStatus.NOT_FOUND             "Project[<id>] not found!" */
    @GetMapping("/get/{id}")
    private ResponseEntity<String> getProject(@PathVariable("id")Long id) {
        return service.getProject(id);
    }

    /**
     * @param pId - Project's id
     * @return HttpStatus.OK                    <Department> responsable for Project[<id>]
     *         HttpStatus.NOT_FOUND             "Project[<id>] not found"
     *         HttpStatus.NOT_FOUND             "No Department found referencing to Project[<id>]" */
    @GetMapping("getDepartment/{pId}")
    private ResponseEntity<String> getDepartmentOf(@PathVariable("pId")Long pId) {
        return service.getDepartmentOf(pId);
    }
}
