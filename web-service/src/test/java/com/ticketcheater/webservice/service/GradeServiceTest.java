package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.entity.Grade;
import com.ticketcheater.webservice.entity.Place;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.fixture.GradeFixture;
import com.ticketcheater.webservice.fixture.PlaceFixture;
import com.ticketcheater.webservice.repository.GradeRepository;
import com.ticketcheater.webservice.repository.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Business Logic - 등급")
@SpringBootTest
class GradeServiceTest {

    @Autowired
    GradeService sut;

    @MockBean
    GradeRepository gradeRepository;

    @MockBean
    PlaceRepository placeRepository;

    @DisplayName("등급 생성 정상 동작")
    @Test
    void givenGrade_whenCreate_thenCreatesGrade() {
        Place place = PlaceFixture.get("광주기아챔피언스필드");
        Grade grade = GradeFixture.get(place, "VIP");

        when(placeRepository.findByIdAndDeletedAtIsNull(place.getId())).thenReturn(Optional.of(place));
        when(gradeRepository.save(any())).thenReturn(grade);

        assertDoesNotThrow(() -> sut.createGrade(place.getId(), "VIP"));
    }

    @DisplayName("없는 장소의 등급 생성 시 오류 발생")
    @Test
    void givenNonExistentPlace_whenCreate_thenThrowsError() {
        Place place = PlaceFixture.get("광주기아챔피언스필드");
        Grade grade = GradeFixture.get(place, "VIP");

        when(placeRepository.findByIdAndDeletedAtIsNull(place.getId())).thenReturn(Optional.empty());
        when(gradeRepository.findByPlaceAndName(place, "VIP")).thenReturn(Optional.empty());
        when(gradeRepository.save(any())).thenReturn(grade);

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.createGrade(place.getId(), "VIP")
        );

        assertEquals(ErrorCode.PLACE_NOT_FOUND, exception.getCode());
    }

    @DisplayName("이미 존재하는 등급 생성 시 오류 발생")
    @Test
    void givenExistentGrade_whenCreate_thenThrowsError() {
        Place place = PlaceFixture.get("광주기아챔피언스필드");
        Grade grade = GradeFixture.get(place, "VIP");

        when(placeRepository.findByIdAndDeletedAtIsNull(place.getId())).thenReturn(Optional.of(place));
        when(gradeRepository.findByPlaceAndName(place, "VIP")).thenReturn(Optional.of(grade));

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.createGrade(place.getId(), "VIP")
        );

        assertEquals(ErrorCode.GRADE_ALREADY_EXISTS, exception.getCode());
    }

    @DisplayName("등급 수정 정상 동작")
    @Test
    void givenGrade_whenUpdate_thenUpdatesGrade() {
        Place place = PlaceFixture.get("광주기아챔피언스필드");
        Grade grade = GradeFixture.get(place, "VIP");

        when(gradeRepository.findByIdAndDeletedAtIsNull(grade.getId())).thenReturn(Optional.of(grade));
        when(placeRepository.findByIdAndDeletedAtIsNull(place.getId())).thenReturn(Optional.of(place));
        when(gradeRepository.saveAndFlush(any())).thenReturn(grade);

        assertDoesNotThrow(() -> sut.updateGrade(grade.getId(), place.getId(), "Normal"));
    }

    @DisplayName("없는 장소로 수정 시 오류 발생")
    @Test
    void givenNonExistentPlace_whenUpdate_thenThrowsError() {
        Long gradeId = 1L;
        Place place = PlaceFixture.get("광주기아챔피언스필드");
        Grade grade = GradeFixture.get(place, "VIP");

        when(placeRepository.findByIdAndDeletedAtIsNull(place.getId())).thenReturn(Optional.empty());
        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.of(grade));

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.updateGrade(gradeId, place.getId(), "VVIP")
        );

        assertEquals(ErrorCode.PLACE_NOT_FOUND, exception.getCode());
    }

    @DisplayName("없는 등급 수정 시 오류 발생")
    @Test
    void givenNonExistentGrade_whenUpdate_thenThrowsError() {
        Long gradeId = 1L;
        Place place = PlaceFixture.get("광주기아챔피언스필드");

        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.updateGrade(gradeId, place.getId(), "VVIP")
        );

        assertEquals(ErrorCode.GRADE_NOT_FOUND, exception.getCode());
    }

    @DisplayName("등급 삭제 정상 동작")
    @Test
    void givenGrade_whenDelete_thenDeletesGrade() {
        Place place = PlaceFixture.get("광주기아챔피언스필드");
        Grade grade = GradeFixture.get(place, "VIP");

        when(gradeRepository.findByIdAndDeletedAtIsNull(grade.getId())).thenReturn(Optional.of(grade));
        when(gradeRepository.saveAndFlush(any())).thenReturn(grade);

        assertDoesNotThrow(() -> sut.deleteGrade(grade.getId()));
    }

    @DisplayName("없는 등급 삭제 시 오류 발생")
    @Test
    void givenNonExistentGrade_whenDelete_thenThrowsError() {
        Long gradeId = 1L;

        when(gradeRepository.findByIdAndDeletedAtIsNull(gradeId)).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.deleteGrade(gradeId)
        );

        assertEquals(ErrorCode.GRADE_NOT_FOUND, exception.getCode());
    }

    @DisplayName("등급 복구 정상 동작")
    @Test
    void givenGrade_whenRestore_thenRestoresGrade() {
        Place place = PlaceFixture.get("광주기아챔피언스필드");
        Grade grade = GradeFixture.get(place, "VIP");

        when(gradeRepository.findByIdAndDeletedAtIsNotNull(grade.getId())).thenReturn(Optional.of(grade));
        when(gradeRepository.saveAndFlush(any())).thenReturn(grade);

        assertDoesNotThrow(() -> sut.restoreGrade(grade.getId()));
    }

    @DisplayName("존재하는 등급 복구 시 오류 발생")
    @Test
    void givenExistentGrade_whenRestore_thenThrowsError() {
        Long gradeId = 1L;

        when(gradeRepository.findByIdAndDeletedAtIsNotNull(gradeId)).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class, () -> sut.restoreGrade(gradeId)
        );

        assertEquals(ErrorCode.GRADE_ALREADY_EXISTS, exception.getCode());
    }

}
