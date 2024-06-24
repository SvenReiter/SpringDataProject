package org.example.service;

import org.example.model.Employee;
import org.example.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository repository;

    public ResponseEntity<Employee> save(Employee employee) {
        return ResponseEntity.status(HttpStatus.OK).body(repository.save(employee));
    }

    public ResponseEntity<String> change(Employee employee, Long id) {
        for(Employee e: repository.findAll()) {
            if(e.getId().compareTo(id)== 0) {
                e.setBirthday(employee.getBirthday());
                e.setAddress(employee.getAddress());
                e.setSex(employee.getSex());
                e.setFirstName(employee.getFirstName());
                e.setLastName(employee.getLastName());
                e.setSalary(employee.getSalary());
                e.setSupervision(employee.getSupervision());
                repository.flush();
                return ResponseEntity.status(HttpStatus.OK).body("Employee["+id+"] got changed!");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+id+"] not found!");
    }

    //todo remove, removeCascade

    public ResponseEntity<String> removeEmployee(Long id) {
        //todo check what happens when <Employee> having supervisor gets removed
        Employee employee= repository.getReferenceById(id);
        repository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Employee["+id+"] removed!");
    }

    public ResponseEntity<String> getEmployee(Long id) {
        Employee result= null;
        for(Employee e: repository.findAll()) {
            if(e.getId()== id) {
                result= e;
                return ResponseEntity.status(HttpStatus.OK).body(result.toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+id+"] not found!");
    }

    public ResponseEntity<String>getSupervision() {
        StringBuilder result= new StringBuilder();
        for(Employee e: repository.findAll()) {
            if(!e.getSupervision().isEmpty()) {
                for(Employee es: e.getSupervision()) {
                    result.append(  e.getFirstName()+" "+e.getLastName()+" supervises "+
                            es.getFirstName()+" "+es.getLastName()+"\n");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(result.toString());
    }

    public ResponseEntity<String> setSupervision(Long sId, Long eId) {
        Employee employee= null;
        for(Employee e: repository.findAll()) {
            if(e.getId().compareTo(sId)== 0) {
                employee= e;
                break;
            }
        }
        if(employee== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+sId+"] not found!");
        }
        Employee supervisor= null;
        for(Employee s: repository.findAll()) {
            if(s.getId().compareTo(eId)== 0) {
                supervisor= s;
                break;
            }
        }
        if(supervisor== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+eId+"] not found!");
        }
        employee.getSupervision().add(supervisor);
        repository.flush();
        return ResponseEntity.status(HttpStatus.OK).body("Supervisor set!");
    }

    public ResponseEntity<List<String>> getBySex(char sex) {
        List<String> result= new ArrayList<>();
        for(Employee e: repository.findBySex(sex)) {
            result.add(e.getFirstName()+" "+e.getLastName());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public ResponseEntity<List<String>> getByBirthdayBefore(LocalDate date) {
        List<String> result= new ArrayList<>();
        for(Employee e: repository.findByBirthdayBefore(date)) {
            result.add(e.getFirstName()+" "+e.getLastName());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public ResponseEntity<List<String>> getSalaryLessThan(float salary) {
        List<String> result = new ArrayList<>();
        for (Employee e : repository.findBySalaryLessThan(salary)) {
            result.add(e.getFirstName() + " " + e.getLastName());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public ResponseEntity<Employee> getEmployeeByMaxSalary() {
        return ResponseEntity.status(HttpStatus.OK).body(repository.findTopByOrderBySalaryDesc().get(0));
    }
}
