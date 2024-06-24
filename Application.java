package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    /*
        Customizes a few Controller-arguments from String to Long;

     */

    /*  Employee:
            POST    /employee/                                                  Body/JSON Employee.json
            POST    /employee/change/{id}                                       Body/JSON Employee.json
            POST    /employee/remove/{id}
            GET     /employee/get/{id}
            GET     /employee/getSupervision
            POST    /employee/setSupervisor/{sId}/{eId}
            GET     /employee/bySex/{sex}
            GET     /employee/byBirthdayBefore/{date}                           //1990-01-21
            GET     /employee/bySalaryLessThan/{salary}
            GET     /employee/maxSalary
        Department:
            GET     /department/getDepartmentOfProject/{pId}
            GET     /department/nrOfEmployees/{dId}
            POST    /department/                                                Body/JSON Department.json
            POST    /department/addProject/{dId}/{pId}
            POST    /department/addEmployeeWorkingFor/{dId}/{eId}
            POST    /department/remove/{id}
        Project:
            POST    /project/setLocation/{dId}/{pId}/{location}
            POST    /project/                                                   Body/JSON Project.json
            POST    /project/change/{pId}                                       Body/JSON Project.json
            POST    /project/removeCascade/{id}
            POST    /project/remove/{id}
            GET     /project/get/{id}
            GET     /project/getDepartment/{pId}
        Management:
            GET     /management/get/{dId}
            POST    /management/bind/{eId}/{dId}/{startDate}
            POST    /management/change/{eId}/{dId}/{startDate}
        WorksOn:
            POST    /worksOn/bind/{eId}/{pId}
            POST    /worksOn/incrementHours/{eId}/{pId}/{hrs}   */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}