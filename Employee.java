package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name= "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(name= "first_name")
    private String firstName;
    @Column(name= "last_name")
    private String lastName;
    @Column(name= "address")
    private String address;
    @Column(name= "birthday")
    private LocalDate birthday;
    @Column(name= "sex")
    private char sex;
    @Column(name= "salary")
    private float salary;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id")
    private List<Employee> supervision;

    @ManyToMany(mappedBy= "employees", fetch= FetchType.LAZY)
    private List<Project> projects;

    public void addProject(Project project) {
        projects.add(project);
    }

    public String toString() {
        StringBuilder result= new StringBuilder("{id:"+id+", first_name:"+firstName+", last_name:"+lastName+", address:"
                                                +address+", birthday:"+birthday+", sex:"+sex+",supervision: {");
        if(supervision!= null && !supervision.isEmpty()) {
            for (int i=0; i<supervision.size()-1; ++i) {
                result.append(supervision.get(i).getFirstName() + " " + supervision.get(i).getLastName() + ", ");
            }
            result.append(supervision.get(supervision.size()-1).getFirstName()+" "+
                          supervision.get(supervision.size()-1).getLastName());
        }
        result.append("}, projects: {");
        if(projects!= null) {
            for(int i=0; i< projects.size()-1; ++i) {
                result.append(projects.get(i).getName()+ ", ");
            }
            result.append(projects.get(projects.size()-1).getName());
        }
        result.append("}}");
        return result.toString();
    }
}
