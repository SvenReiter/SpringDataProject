package org.example.service;

import org.example.model.Department;
import org.example.model.Employee;
import org.example.model.Management;
import org.example.model.WorksOn;
import org.example.repository.DepartmentRepository;
import org.example.repository.EmployeeRepository;
import org.example.repository.ManagementRepository;
import org.example.repository.WorksOnRepository;
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
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorksOnRepository worksOnRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ManagementRepository managementRepository;

    /**
     * @param employee - json representation
     * @return HttpStatus.OK                    "Employee{employee}" */
    public ResponseEntity<Employee> save(Employee employee) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeRepository.save(employee));
    }

    /**
     * @param employee - new Employee-context
     * @param id - Employee's id
     * @return HttpStatus.OK                    "Employee[<id>] got changed!"
     *         HttpStatus.NOT_FOUND             "Employee[<id>] not found!" */
    public ResponseEntity<String> change(Employee employee, Long id) {
        for(Employee e: employeeRepository.findAll()) {
            if(e.getId().compareTo(id)== 0) {
                e.setBirthday(employee.getBirthday());
                e.setAddress(employee.getAddress());
                e.setSex(employee.getSex());
                e.setFirstName(employee.getFirstName());
                e.setLastName(employee.getLastName());
                e.setSalary(employee.getSalary());
                e.setSupervision(employee.getSupervision());
                employeeRepository.flush();
                return ResponseEntity.status(HttpStatus.OK).body("Employee["+id+"] got changed!");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+id+"] not found!");
    }

    /**
     * @param id - to remove Employee's ID
     * @return HttpStatus.NOT_FOUND             "Employee[<id>] not found!"
     * @return HttpStatus.OK                    "Employee[<id>] cascaded removed!"*/
    public ResponseEntity<String> removeEmployeeCascade(Long id) {
        Employee employee= null;
        for(Employee e: employeeRepository.findAll()) {
            if(e.getId().compareTo(id)== 0) {
                employee= e;
            }
        }
        if(employee== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+id+"] not found!");
        }
        for(Management m: managementRepository.findAll()) {
            if(m.getEmployee_id().compareTo(id)== 0) {
                for(Department d: departmentRepository.findAll()) {
                    if(d.getId().compareTo(m.getDepartment_id())== 0) {
                        List<Management> newManagement= d.getManagement();
                        newManagement.remove(m);
                        d.setManagement(newManagement);
                        departmentRepository.save(d);
                        departmentRepository.flush();
                    }
                }
                managementRepository.delete(m);
                managementRepository.flush();
            }
        }
        if(!employee.getSupervision().isEmpty()) {
            employee.setSupervision(new ArrayList<>());
            employeeRepository.save(employee);
            employeeRepository.flush();
        }
        for(WorksOn wo: worksOnRepository.findAll()) {
            if(wo.getEmployee_id().compareTo(id)== 0) {
                worksOnRepository.delete(wo);
                worksOnRepository.flush();
            }
        }
        for(Department d: departmentRepository.findAll()) {
            for(Employee e: d.getWorksFor()) {
                if(e.getId().compareTo(id)== 0) {
                    List<Employee>employees= d.getWorksFor();
                    employees.remove(employee);
                    d.setWorksFor(employees);
                    departmentRepository.save(d);
                    departmentRepository.flush();
                }
            }
        }
        employeeRepository.deleteById(id);
        employeeRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).body("Employee["+id+"] cascaded removed!");
    }

    /**
     * @param id - to remove Employee's ID
     * @return HttpStatus.NOT_FOUND             "Employee[<id>] not found!"
     * @return HttpStatus.CONFLICT              "Employee[<id>] still manages Department[<id>]!"
     * @return HttpStatus.CONFLICT              "Employee[<id>] still supervises other Employees!"
     * @return HttpStatus.CONFLICT              "Employee[<id>] still works on Project[<id>]!"
     * @return HttpStatus.CONFLICT              "Employee[<id>] still works for Department[<id>]/<name>!"
     * @return HttpStatus.OK                    "Employee[<id>] removed!"*/
    public ResponseEntity<String> removeEmployee(Long id) {
        Employee employee= null;
        for(Employee e: employeeRepository.findAll()) {
            if(e.getId().compareTo(id)== 0) {
                employee= e;
            }
        }
        if(employee== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+id+"] not found!");
        }
        for(Management m: managementRepository.findAll()) {
            if(m.getEmployee_id().compareTo(id)== 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        "Employee["+id+"] still manages Department["+m.getDepartment_id()+"]!");
            }
        }
        if(!employee.getSupervision().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Employee["+id+"] still supervises other Employees");
        }
        for(WorksOn wo: worksOnRepository.findAll()) {
            if(wo.getEmployee_id().compareTo(id)== 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        "Employee["+id+"] still works on Project["+wo.getProject_id()+"]");
            }
        }
        for(Department d: departmentRepository.findAll()) {
            for(Employee e: d.getWorksFor()) {
                if(e.getId().compareTo(id)== 0) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(
                            "Employee["+id+"] still works for Department["+d.getId()+"]/"+d.getName());
                }
            }
        }
        employeeRepository.deleteById(id);
        employeeRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).body("Employee["+id+"] removed!");
    }

    /**
     * @param id - demanded Employee's ID
     * @return HttpStatus.OK                    "Employee[<id>]"
     *         HttpStatus.NOT_FOUND             "Employee[<id>] not found!" */
    public ResponseEntity<String> getEmployee(Long id) {
        Employee result= null;
        for(Employee e: employeeRepository.findAll()) {
            if(e.getId()== id) {
                result= e;
                return ResponseEntity.status(HttpStatus.OK).body(result.toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+id+"] not found!");
    }

    /**
     * @return HttpStatus.OK                    List of "Employee.fullName supervises Employee.fullName" */
    public ResponseEntity<String>getSupervision() {
        StringBuilder result= new StringBuilder();
        for(Employee e: employeeRepository.findAll()) {
            if(!e.getSupervision().isEmpty()) {
                for(Employee es: e.getSupervision()) {
                    result.append(  e.getFirstName()+" "+e.getLastName()+" supervises "+
                            es.getFirstName()+" "+es.getLastName()+"\n");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(result.toString());
    }

    /**
     * @param sId - the supervisors Employee's ID
     * @param eId - Employee's ID to designate supervisor to
     * @return HttpStatus.NOT_FOUND             "Employee[<sId>] not found!"
     *         HttpStatus.NOT_FOUND             "Employee[<eId>] not found!"
     *         HttpStatus.OK                    "Supervisor set!" */
    public ResponseEntity<String> setSupervision(Long sId, Long eId) {
        Employee employee= null;
        for(Employee e: employeeRepository.findAll()) {
            if(e.getId().compareTo(sId)== 0) {
                employee= e;
                break;
            }
        }
        if(employee== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+sId+"] not found!");
        }
        Employee supervisor= null;
        for(Employee s: employeeRepository.findAll()) {
            if(s.getId().compareTo(eId)== 0) {
                supervisor= s;
                break;
            }
        }
        if(supervisor== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee["+eId+"] not found!");
        }
        employee.getSupervision().add(supervisor);
        employeeRepository.flush();
        return ResponseEntity.status(HttpStatus.OK).body("Supervisor set!");
    }

    /**
     * @param sex - f/F: female, m/M: male
     * @return HttpStatus.OK                    List of <full Names> of <Employees having matched sex to <sex> */
    public ResponseEntity<List<String>> getBySex(char sex) {
        List<String> result= new ArrayList<>();
        for(Employee e: employeeRepository.findBySex(sex)) {
            result.add(e.getFirstName()+" "+e.getLastName());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * @param date - yyyy-MM-dd
     * @return HttpStatus.OK                    List of <fullNames> of <Employee>s older than <date> */
    public ResponseEntity<List<String>> getByBirthdayBefore(LocalDate date) {
        List<String> result= new ArrayList<>();
        for(Employee e: employeeRepository.findByBirthdayBefore(date)) {
            result.add(e.getFirstName()+" "+e.getLastName());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * @param salary - salary to compare with
     * @return  HttpStatus.OK           List of <fullNames> of <Employee>s getting less salary than given <salary> */
    public ResponseEntity<List<String>> getSalaryLessThan(float salary) {
        List<String> result = new ArrayList<>();
        for (Employee e : employeeRepository.findBySalaryLessThan(salary)) {
            result.add(e.getFirstName() + " " + e.getLastName());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * @return HttpStatus.OK                    <Employee> having the highest salary */
    public ResponseEntity<Employee> getEmployeeByMaxSalary() {
        return ResponseEntity.status(HttpStatus.OK).body(employeeRepository.findTopByOrderBySalaryDesc().get(0));
    }
}
