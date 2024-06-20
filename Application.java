package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    /*
        Employee:
            POST    /employee/
            POST    /employee/change/{id}
            POST    /employee/remove/{id}
            GET     /employee/get/{id}
            GET     /employee/getSupervision
            POST    /employee/setSupervisor/{sId}/{eId}
            GET     /employee/bySex/{sex}
            GET     /employee/byBirthdayBefore/{date}
            GET     /employee/bySalaryLessThan/{salary}
            GET     /employee/maxSalary
        Department:
            GET     /department/getDepartmentOfProject/{pId}
            GET     /department/nrOfEmployees/{dId}
            POST    /department/
            POST    /department/addProject/{dId}/{pId}
            POST    /department/addEmployeeWorkingFor/{dId}/{eId}
         Project:
            POST    /project/setLocation/{dId}/{pId}/{location}
            POST    /project/
            POST    /project/change/{pId}
            POST    /project/removeCascade/{id}
            POST    /project/remove/{id}
            GET     /project/get/{id}
            GET     /project/getDepartment/{pId}
         Management:
            POST    /management/bind/{eId}/{dId}/{startDate}
            POST    /management/change/{eId}/{dId}/{startDate}
         WorksOn:
            POST    /worksOn/bind/{eId}/{pId}
            POST    /worksOn/incrementHours/{eId}/{pId}/{hrs}
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}