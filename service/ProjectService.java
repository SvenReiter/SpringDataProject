package org.example.service;

import org.example.model.Department;
import org.example.model.Project;
import org.example.model.WorksOn;
import org.example.repository.DepartmentRepository;
import org.example.repository.ProjectRepository;
import org.example.repository.WorksOnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private WorksOnRepository worksOnRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public ResponseEntity<String> setLocation(Long dId, Long pId, String location) {
        for(Department d: departmentRepository.findAll()) {
            if(d.getId().compareTo(dId)== 0) {
                for(Project p: d.getControls()) {
                    if(p.getId().compareTo(pId)== 0) {
                        p.setLocation(location);
                        projectRepository.flush();

                        d.getLocations().clear();
                        for(Project project: d.getControls()) {
                            d.addLocation(project.getLocation());
                        }
                        departmentRepository.flush();
                        return ResponseEntity.status(HttpStatus.OK).body(location +
                                " added to Department["+dId+"] and Project["+pId+"]");
                    }
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project["+pId+"] not found!");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department["+dId+"] not found!");
    }

    public ResponseEntity<Project> save(Project project) {
        return ResponseEntity.status(HttpStatus.OK).body(projectRepository.save(project));
    }

    public ResponseEntity<String> change(Project project, Long id) {
        for(Project p: projectRepository.findAll()) {
            if(p.getId().compareTo(id)== 0) {
                p.setName(project.getName());
                projectRepository.flush();
                return ResponseEntity.status(HttpStatus.OK).body(p.toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project["+id+"] not found!");
    }

    public ResponseEntity<String> removeCascade(Long id) {
        Project project= null;
        for(Project p: projectRepository.findAll()) {
            if(p.getId().compareTo(id)== 0) {
                project= p;
            }
        }
        if(project== null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project[" + id + "] not found!");
        }
        StringBuilder result= new StringBuilder(
                "Project["+id+"] deleted in Project-table.\nReference/s to Project["+id+"] found/deleted in/from\n\t");

        for(WorksOn w: worksOnRepository.findAll()) {
            if(w.getProject_id().compareTo(id)== 0) {
                result.append("-employeeWorksOnProject["+w.getId()+"]\n\t");
                worksOnRepository.delete(w);
                worksOnRepository.flush();
            }
        }
        for(Department d: departmentRepository.findAll()) {
            if(d.getControls().contains(project)) {
                result.append("-controls["+d.getId()+"]\n\t");
                d.getControls().remove(project);
                departmentRepository.save(d);

                d.getLocations().clear();
                for(Project p: d.getControls()) {
                    d.addLocation(p.getLocation());
                }
                departmentRepository.flush();
            }
        }
        projectRepository.deleteById(id);
        projectRepository.flush();

        return ResponseEntity.status(HttpStatus.OK).body(result.toString());
    }

    public ResponseEntity<String> remove(Long id) {
        boolean projectFound= false;
        for(Project p: projectRepository.findAll()) {
            if(p.getId().compareTo(id)== 0) {
                projectFound= true;
                break;
            }
        }
        if(!projectFound) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project[" + id + "] not found!");
        }
        for(WorksOn wo: worksOnRepository.findAll()) {
            if(wo.getProject_id().compareTo(id)== 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Project["+id+"] still references Employee/s!");
            }
        }
        projectRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Project["+id+"] removed");
    }

    public ResponseEntity<String> getProject(Long id) {
        Project result= null;
        for(Project p: projectRepository.findAll()) {
            if(p.getId()== id) {
                result= p;
                return ResponseEntity.status(HttpStatus.OK).body(result.toString());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project["+id+"] not found!");
    }

    public ResponseEntity<String> getDepartmentOf(Long pId) {
        boolean projectAvailable= false;
        for(Project p: projectRepository.findAll()) {
            if(p.getId()== pId) {
                projectAvailable= true;
                break;
            }
        }
        if(!projectAvailable) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project[" + pId + "] not found");
        }
        for(Department d: departmentRepository.findAll()) {
            for(Project p: d.getControls()) {
                if(p.getId().equals(pId)) {
                    return ResponseEntity.status(HttpStatus.OK).body(d.toString());
                }
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Department found referencing to Project["+pId+"]");
    }
}
