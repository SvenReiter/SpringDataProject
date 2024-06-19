package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name= "employeeWorksOnProject")  // as alias for "works on"
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorksOn {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private Long employee_id;
    private Long project_id;

    private Integer hours;

    public WorksOn(Long eId, Long pId) {
        employee_id= eId;
        project_id= pId;
    }

    public void incrementHours(int incr) {
        hours=(hours==null)?0:hours;
        hours+= incr;
    }
}
