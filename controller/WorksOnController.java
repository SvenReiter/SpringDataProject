package org.example.controller;

import org.example.service.WorksOnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/worksOn")
public class WorksOnController {
    @Autowired
    WorksOnService service;

    //todo removeBonds, removeBondsCascade

    /**
     * @param eId employees id
     * @param pId projects id
     * @return HttpStatus.NOT_FOUND         "Employee[<eId>] not found"
     *         HttpStatus.NOT_FOUND         "Project[<pId>] not found"
     *         HttpStatus.ALREADY_REPORTED  "Employee[<eId>] is already working on Project[<pId>]"
     *         HttpStatus.OK                "Employee[<eId>] works Project[<pId>]!" */
    @PostMapping("bind/{eId}/{pId}")
    private ResponseEntity<String>makeEmployeeWorkOnProject(@PathVariable("eId")Long eId,
                                                            @PathVariable("pId")Long pId) {
        return service.makeEmployeeWorkOnProject(eId, pId);
    }

    /**
     * @param eId
     * @param pId
     * @param hrs
     * @return HttpStatus.NOT_FOUND         "Employee[<eId>] not found"
     *         HttpStatus.OK                "Employee[<eId>] worked <wo.getHours()>hrs on Project[<pId>] so far" */
    @PostMapping("incrementHours/{eId}/{pId}/{hrs}")
    private ResponseEntity<String>incrHrs(@PathVariable("eId")Long eId, @PathVariable("pId")Long pId,
                                          @PathVariable("hrs")Integer hrs) {
        return service.incrHrs(eId, pId, hrs);
    }
}
