package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.entity.Place;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.fixture.PlaceFixture;
import com.ticketcheater.webservice.repository.PlaceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("비즈니스 로직 - 장소")
@SpringBootTest
class PlaceServiceTest {

    @Autowired
    PlaceService sut;

    @MockBean
    PlaceRepository placeRepository;

    @DisplayName("장소 생성 정상 동작")
    @Test
    void givenPlace_whenCreate_thenCreatesTeam() {
        String name = "광주기아챔피언스필드";
        Place place = PlaceFixture.get(name);

        when(placeRepository.save(any())).thenReturn(place);
        when(placeRepository.findByName(name)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> sut.createPlace(name));
    }

    @DisplayName("이미 존재하는 장소 이름으로 생성 시 오류 발생")
    @Test
    void givenExistentPlace_whenCreate_thenThrowsError() {
        String name = "광주기아챔피언스필드";
        Place place = PlaceFixture.get(name);

        when(placeRepository.findByName(name)).thenReturn(Optional.of(place));

        WebApplicationException exception = Assertions.assertThrows(
                WebApplicationException.class, () -> sut.createPlace(name)
        );

        Assertions.assertEquals(ErrorCode.PLACE_ALREADY_EXISTS, exception.getCode());
    }

    @DisplayName("부적절한 장소 이름으로 생성 시 오류 발생")
    @Test
    void givenInvalidPlace_whenCreate_thenThrowsError() {
        String name = "InvalidPlace";

        WebApplicationException exception = Assertions.assertThrows(
                WebApplicationException.class, () -> sut.createPlace(name)
        );

        Assertions.assertEquals(ErrorCode.INVALID_PLACE, exception.getCode());
    }

    @DisplayName("장소 수정 정상 동작")
    @Test
    void givenPlaceIdAndName_whenUpdate_thenUpdatesTeam() {
        Long placeId = 1L;
        String name = "광주기아챔피언스필드";
        Place place = PlaceFixture.get(name);

        when(placeRepository.findByIdAndDeletedAtIsNull(placeId)).thenReturn(Optional.of(place));
        when(placeRepository.saveAndFlush(any())).thenReturn(place);

        Assertions.assertDoesNotThrow(() -> sut.updatePlace(placeId, name));
    }

    @DisplayName("없는 장소 수정 시 오류 발생")
    @Test
    void givenNonExistentPlace_whenUpdate_thenThrowsError() {
        Long placeId = 1L;
        String name = "광주기아챔피언스필드";

        when(placeRepository.findByIdAndDeletedAtIsNull(placeId)).thenReturn(Optional.empty());

        WebApplicationException exception = Assertions.assertThrows(
                WebApplicationException.class, () -> sut.updatePlace(placeId, name)
        );

        Assertions.assertEquals(ErrorCode.PLACE_NOT_FOUND, exception.getCode());
    }

    @DisplayName("부적절한 장소 이름으로 수정 시 오류 발생")
    @Test
    void givenInvalidPlaceName_whenUpdate_thenThrowsError() {
        Long placeId = 1L;
        String name = "InvalidPlace";

        Place place = PlaceFixture.get(name);

        when(placeRepository.findByIdAndDeletedAtIsNull(placeId)).thenReturn(Optional.of(place));

        WebApplicationException exception = Assertions.assertThrows(
                WebApplicationException.class, () -> sut.updatePlace(placeId, name)
        );

        Assertions.assertEquals(ErrorCode.INVALID_PLACE, exception.getCode());
    }

    @DisplayName("장소 삭제 정상 동작")
    @Test
    void givenPlaceId_whenDelete_thenDeletesTeam() {
        Long placeId = 1L;
        Place place = PlaceFixture.get("광주기아챔피언스필드");

        when(placeRepository.findByIdAndDeletedAtIsNull(placeId)).thenReturn(Optional.of(place));
        when(placeRepository.saveAndFlush(any())).thenReturn(place);

        Assertions.assertDoesNotThrow(() -> sut.deletePlace(placeId));
    }

    @DisplayName("없는 장소 삭제 시 오류 발생")
    @Test
    void givenNonExistentPlace_whenDelete_thenThrowsError() {
        Long placeId = 1L;

        when(placeRepository.findByIdAndDeletedAtIsNull(placeId)).thenReturn(Optional.empty());

        WebApplicationException exception = Assertions.assertThrows(
                WebApplicationException.class, () -> sut.deletePlace(placeId)
        );

        Assertions.assertEquals(ErrorCode.PLACE_NOT_FOUND, exception.getCode());
    }

    @DisplayName("장소 복구 정상 동작")
    @Test
    void givenDeletedPlaceId_whenRestore_thenRestoresTeam() {
        Long placeId = 1L;
        Place place = PlaceFixture.get("광주기아챔피언스필드");
        place.setDeletedAt(new Timestamp(System.currentTimeMillis()));

        when(placeRepository.findByIdAndDeletedAtIsNotNull(placeId)).thenReturn(Optional.of(place));
        when(placeRepository.saveAndFlush(any())).thenReturn(place);

        Assertions.assertDoesNotThrow(() -> sut.restorePlace(placeId));
    }

    @DisplayName("있는 장소 복구 시 오류 발생")
    @Test
    void givenExistentPlace_whenRestore_thenThrowsError() {
        Long placeId = 1L;

        when(placeRepository.findByIdAndDeletedAtIsNotNull(placeId)).thenReturn(Optional.empty());

        WebApplicationException exception = Assertions.assertThrows(
                WebApplicationException.class, () -> sut.restorePlace(placeId)
        );

        Assertions.assertEquals(ErrorCode.PLACE_ALREADY_EXISTS, exception.getCode());
    }

}
