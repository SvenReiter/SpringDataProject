package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    /* example requests

        POST: http://localhost:8080/projectPOST: http://localhost:8080/employee
            Body/raw/JSON:
                {
                    "firstName": "Hans",
                    "lastName": "Dieter",
                    "address": "60433 Frankfurt",
                    "sex": "M",
                    "birthday": "2000-01-02"
                }
            Body/raw/JSON:
                {
                    "name": "ein rieeeesiges Projekt",
                    "employees": [
                            {
                                "firstName": "Wilma",
                                "lastName": "Müller",
                                "address": "35641 Oberquembach",
                                "sex": "F",
                                "birthday": "1999-05-02"
                            }
                    ]
                }
        POST: http://localhost:8080/project
            Body/raw/JSON:
                {
                    "name": "ein Projekt bislang ohne Mitarbeiter",
                    "employees": []
                }
        POST: http://localhost:8080/project
            Body/raw/JSON:
                {
                    "name": "ein Projekt welches initial noch nicht bearbeitet wird"
                }
     */
    /*
        POST: http://localhost:8080/employee
            Body/raw/JSON:
                {
                    "firstName": "Hans",
                    "lastName": "Dieter",
                    "address": "60433 Frankfurt",
                    "sex": "M",
                    "birthday": "2000-01-02"
                }
        -- wobei Employee mit id=1 existieren muss --
        POST: http://localhost:8080/project/1
            Body/raw/JSON:
                {
                    "name": "ein Projekt mit einem Mitarbeiter"
                }
     */

    //HINT flush()ing the repositories was missing!
}