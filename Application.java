package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    /*
        Adds String ssn -property to <Employee>;

        Adds String location to <Project> and List<String> locations to <Department>
        Makes Set<String> locations in <Department>
        Implements ProjectController.setLocation(Long, Long, String)
        Edits Department.addProject(Project)
        Fixes ProjectController.removeCascade(Long) to sync <Department.locations> referencing to its current projects
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}