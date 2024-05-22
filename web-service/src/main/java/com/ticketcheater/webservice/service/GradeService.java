package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.dto.GradeDTO;
import com.ticketcheater.webservice.entity.Grade;
import com.ticketcheater.webservice.entity.Place;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.repository.GradeRepository;
import com.ticketcheater.webservice.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final PlaceRepository placeRepository;
    private final GradeRepository gradeRepository;

    @Transactional
    public GradeDTO createGrade(Long placeId, String name) {
        gradeRepository.findByPlaceIdAndName(placeId, name).ifPresent(it -> {
            throw new WebApplicationException(ErrorCode.GRADE_ALREADY_EXISTS, String.format("grade is %s", name));
        });

        Place place = findPlaceById(placeId);
        Grade grade = gradeRepository.save(Grade.of(place, name));

        return GradeDTO.toDTO(grade);
    }

    @Transactional
    public GradeDTO updateGrade(Long gradeId, Long placeId, String name) {
        Grade grade = gradeRepository.findByIdAndDeletedAtIsNull(gradeId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.GRADE_NOT_FOUND, String.format("grade is %s", gradeId))
        );

        grade.setPlace(findPlaceById(placeId));
        grade.setName(name);

        gradeRepository.saveAndFlush(grade);

        return GradeDTO.toDTO(grade);
    }

    @Transactional
    public void deleteGrade(Long gradeId) {
        Grade grade = gradeRepository.findByIdAndDeletedAtIsNull(gradeId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.GRADE_NOT_FOUND, String.format("grade is %s", gradeId))
        );

        grade.setDeletedAt(new Timestamp(System.currentTimeMillis()));

        gradeRepository.saveAndFlush(grade);
    }

    @Transactional
    public void restoreGrade(Long gradeId) {
        Grade grade = gradeRepository.findByIdAndDeletedAtIsNotNull(gradeId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.GRADE_ALREADY_EXISTS, String.format("grade is %s", gradeId))
        );

        grade.setDeletedAt(null);

        gradeRepository.saveAndFlush(grade);
    }

    private Place findPlaceById(Long placeId) {
        return placeRepository.findByIdAndDeletedAtIsNull(placeId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.PLACE_NOT_FOUND, String.format("place is %s", placeId)));
    }

}
