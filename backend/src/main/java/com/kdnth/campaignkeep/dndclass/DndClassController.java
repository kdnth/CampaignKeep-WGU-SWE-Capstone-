package com.kdnth.campaignkeep.dndclass;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class DndClassController {

    private final DndClassService dndClassService;

    public DndClassController(DndClassService dndClassService) {
        this.dndClassService = dndClassService;
    }

    @GetMapping
    public List<DndClassResponse> getClasses() {
        return dndClassService.getClasses();
    }

    @GetMapping("/{classId}")
    public DndClassResponse getClassById(@PathVariable Long classId) {
        return dndClassService.getClassById(classId);
    }
}
