package com.kdnth.campaignkeep.dndclass;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DndClassServiceImpl implements DndClassService {

    private final DndClassRepository dndClassRepository;

    public DndClassServiceImpl(DndClassRepository dndClassRepository) {
        this.dndClassRepository = dndClassRepository;
    }

    @Override
    public List<DndClassResponse> getClasses() {
        return dndClassRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public DndClassResponse getClassById(Long classId) {
        DndClass dndClass = dndClassRepository.findById(classId)
                .orElseThrow(() -> new NoSuchElementException("Class not found"));
        return toResponse(dndClass);
    }

    private DndClassResponse toResponse(DndClass dndClass) {
        return new DndClassResponse(
                dndClass.getId(),
                dndClass.getName(),
                dndClass.getHitDice(),
                dndClass.getDescription(),
                dndClass.getIndex()
        );
    }
}
