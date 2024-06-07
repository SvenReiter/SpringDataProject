package org.example.controller;

import org.example.model.Employee;
import org.example.model.Project;
import org.example.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    private ResponseEntity<Employee> save(@RequestBody Employee employee) {
        return ResponseEntity.status(HttpStatus.OK).body(repository.save(employee));
    }

    @PostMapping("remove/{id}")
    private ResponseEntity<String> removeEmployee(@PathVariable("id")String id) {
        Employee employee= repository.getReferenceById(Long.parseLong(id));
        if(!employee.getProjects().isEmpty())
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Employee["+id+"] still references Project/s!");
        repository.deleteById(Long.parseLong(id));
        return ResponseEntity.status(HttpStatus.OK).body("Employee["+id+"] removed");
    }

    /**
     * @param id
     * @post if Employee[id] is still referencing to/ gets referenced from Project/s,
     *       concerning Projects get removed as well.
     */
    @PostMapping("removeCascade/{id}")
    private ResponseEntity<String> removeCascadeEmployee(@PathVariable("id")String id) {
        if(repository.findById(Long.parseLong(id)).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+id+"] not found");
        Employee employee= repository.getReferenceById(Long.parseLong(id));
        StringBuilder sb= new StringBuilder();
        if(!employee.getProjects().isEmpty()) {
            sb.append(" and also its reference/s Project/s[");
            for(int i=0; i<employee.getProjects().size()-1; ++i) {
                employee.getProjects().get(i).getEmployees().remove(employee);
                sb.append(employee.getProjects().get(i).getName()+" ,");
            }
            employee.getProjects().get(employee.getProjects().size()-1).getEmployees().remove(employee);
            sb.append(employee.getProjects().get(employee.getProjects().size()-1).getName()+"]");
        }
        employee.setProjects(null);
        repository.deleteById(Long.parseLong(id));
        return ResponseEntity.status(HttpStatus.OK).body("Employee["+id+"] removed"+ sb);
    }

    @GetMapping("/get/{id}")
    private ResponseEntity<String> getEmployee(@PathVariable("id")String id) {
    Employee result= null;
        for(Employee e: repository.findAll()) {
            if(e.getId()== Long.parseLong(id)) {
                result= e;
                return ResponseEntity.status(HttpStatus.OK).body(result.toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+id+"] not found!");
    }

    @GetMapping("/bySex/{sex}")
    private ResponseEntity<List<String>> getBySex(@PathVariable("sex")char sex) {
        List<String> result= new ArrayList<>();
        for(Employee e: repository.findBySex(sex)) {
            result.add(e.getFirstName()+" "+e.getLastName());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
}

    @GetMapping("/byBirthdayBefore/{date}")
    private ResponseEntity<List<String>> getByBirthdayBefore(@PathVariable("date")LocalDate date) {
        List<String> result= new ArrayList<>();
            for(Employee e: repository.findByBirthdayBefore(date)) {
            result.add(e.getFirstName()+" "+e.getLastName());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
}

    @GetMapping("/bySalaryLessThan/{salary}")
    private ResponseEntity<List<String>> getSalaryLessThan(@PathVariable("salary")float salary) {
        List<String> result = new ArrayList<>();
        for (Employee e : repository.findBySalaryLessThan(salary)) {
            result.add(e.getFirstName() + " " + e.getLastName());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/maxSalary")
    private ResponseEntity<Employee> getEmployeeByMaxSalary() {
        return ResponseEntity.status(HttpStatus.OK).body(repository.findTopByOrderBySalaryDesc().get(0));
    }
}
