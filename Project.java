package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Table(name= "project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(fetch= FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name= "works_on",
        joinColumns = {
            @JoinColumn(name= "project_id", referencedColumnName = "id")
        },
        inverseJoinColumns = {
            @JoinColumn(name= "employee_id", referencedColumnName = "id")
        })
    private List<Employee> employees;

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public String toString() {
        StringBuilder result= new StringBuilder("{id:"+id+", name:"+name+",employees: {");
        if(employees!= null && !employees.isEmpty()) {
            for (int i=0; i<employees.size()-1; ++i) {
                result.append(employees.get(i).getFirstName() + " " + employees.get(i).getLastName() + ", ");
            }
            result.append(employees.get(employees.size()-1).getFirstName() + " " + employees.get(employees.size()-1).getLastName());
        }
        result.append("}}");
        return result.toString();
    }
}
