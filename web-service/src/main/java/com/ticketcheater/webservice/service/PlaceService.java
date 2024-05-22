package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.dto.PlaceDTO;
import com.ticketcheater.webservice.entity.Place;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    @Transactional
    public PlaceDTO createPlace(String name) {
        placeRepository.findByName(name).ifPresent(it -> {
            throw new WebApplicationException(ErrorCode.PLACE_ALREADY_EXISTS, String.format("place with name %s already exists", name));
        });

        if(isInvalidPlace(name)) {
            throw new WebApplicationException(ErrorCode.INVALID_PLACE, String.format("team with name %s is not valid", name));
        }

        Place place = placeRepository.save(Place.of(name));

        log.info("create place method executed successfully for place: place id={}", place.getId());

        return PlaceDTO.toDTO(place);
    }

    @Transactional
    public PlaceDTO updatePlace(Long placeId, String name) {
        Place place = placeRepository.findByIdAndDeletedAtIsNull(placeId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.PLACE_NOT_FOUND, String.format("place id %d not found", placeId))
        );

        if(isInvalidPlace(name)) {
            throw new WebApplicationException(ErrorCode.INVALID_PLACE, String.format("team with name %s is not valid", name));
        }

        place.setName(name);
        placeRepository.saveAndFlush(place);

        log.info("update place method executed successfully for place: place id={}", placeId);

        return PlaceDTO.toDTO(place);
    }

    @Transactional
    public void deletePlace(Long placeId) {
        Place place = placeRepository.findByIdAndDeletedAtIsNull(placeId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.PLACE_NOT_FOUND, String.format("place id %d not found", placeId))
        );

        place.setDeletedAt(new Timestamp(System.currentTimeMillis()));

        placeRepository.saveAndFlush(place);

        log.info("delete place method executed successfully for place: place id={}", placeId);
    }

    @Transactional
    public void restorePlace(Long placeId) {
        Place place = placeRepository.findByIdAndDeletedAtIsNotNull(placeId).orElseThrow(
                () -> new WebApplicationException(ErrorCode.PLACE_ALREADY_EXISTS, String.format("place id %d already exists", placeId))
        );

        place.setDeletedAt(null);

        placeRepository.saveAndFlush(place);

        log.info("restore place method executed successfully for place: place id={}", placeId);
    }

    private boolean isInvalidPlace(String name) {
        return !Pattern.matches("^[가-힇]*$", name);
    }

}
