package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Employee manager;

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

    public List<Project> addProject(Project p) {
        controls.add(p);
        return controls;
    }

    public List<Employee> addEmployeeAsWorksFor(Employee e) {
        worksFor.add(e);
        return worksFor;
    }
}
