package org.example.model;

import jakarta.persistence.*;
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
//@AllArgsConstructor
public class Employee {

    private static final long serialVersionUID = 1L;

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
    @Column(name= "ssn")
    private String ssn;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "supervisor")
    private List<Employee> supervision;

    public Employee(Long id, String firstName, String lastName, String address, LocalDate birthday, char sex,
                    float salary, String ssn, List<Employee> supervision) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthday = birthday;
        this.sex = sex;
        this.salary = salary;
        this.ssn= ssn;
        this.supervision = supervision;
    }
}
