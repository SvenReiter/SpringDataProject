package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name= "department")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name= "manages",
            joinColumns = {
                    @JoinColumn(name= "department_id", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name= "employee_id", referencedColumnName = "id")
            })
    private List<Management> management;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name= "works_for",
            joinColumns = {
                    @JoinColumn(name= "department_id", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name= "employee_id", referencedColumnName = "id")
            })
    private List<Employee> worksFor;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name= "controls",
            inverseJoinColumns = {
                    @JoinColumn(name= "project_id", referencedColumnName = "id")
            })
    private List<Project> controls;

    private Set<String> locations;

    public List<Project> addProject(Project p) {
        controls.add(p);
        if(locations== null)
            locations= new HashSet<>();
        if(p.getLocation()!= null)
            locations.add(p.getLocation());
        return controls;
    }

    public List<Employee> addEmployeeAsWorksFor(Employee e) {
        worksFor.add(e);
        return worksFor;
    }

    public void addLocation(String location) {
        this.locations.add(location);
    }

    public String toString() {
        String result= "{\n";
        result+= "\t\"id\": "+id+",\n";
        result+= "\t\"name\": "+name+",\n";
//        result+= "\t\"manager\": "+manager.getFirstName()+" "+manager.getLastName()+",\n}";
        return result;
    }
}
