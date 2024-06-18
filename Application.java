package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    /*
        Removes setManager from DepartmentController()
        Creates/Implements model.Management(), controller.ManagementController and repository.ManagementRepository
        project/removeProjectCascade is working
            [removes <Project>s specific dataset from employee_works_on_project-, project-, controls-table]
        ProjectController.change(Project, Long) implemented/works
        ProjectController.getDepartmentOf(Long pId)
            for retrieving Department by requesting the ProjectController
        EmployeeController.getSupervision
            provides full list of employee.fullName supervises employee.fullName
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}