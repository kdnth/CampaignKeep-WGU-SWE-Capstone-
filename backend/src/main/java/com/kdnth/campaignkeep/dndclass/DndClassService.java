package com.kdnth.campaignkeep.dndclass;

import java.util.List;

public interface DndClassService {

    List<DndClassResponse> getClasses();

    DndClassResponse getClassById(Long classId);
}
