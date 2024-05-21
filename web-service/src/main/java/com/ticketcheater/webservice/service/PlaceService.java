package com.ticketcheater.webservice.service;

import com.ticketcheater.webservice.dto.PlaceDTO;
import com.ticketcheater.webservice.entity.Place;
import com.ticketcheater.webservice.exception.ErrorCode;
import com.ticketcheater.webservice.exception.WebApplicationException;
import com.ticketcheater.webservice.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    @Transactional
    public PlaceDTO createPlace(String name) {
        placeRepository.findByName(name).ifPresent(it -> {
            throw new WebApplicationException(ErrorCode.PLACE_ALREADY_EXISTS, String.format("place is %s", name));
        });

        if(isInvalidPlace(name)) {
            throw new WebApplicationException(ErrorCode.INVALID_PLACE, String.format("team is %s", name));
        }

        Place place = placeRepository.save(Place.of(name));

        return PlaceDTO.toDTO(place);
    }

    @Transactional
    public PlaceDTO updatePlace(Long placeId, String name) {
        Place place = placeRepository.findByIdAndDeletedAtIsNull(placeId)
                .orElseThrow(() -> new WebApplicationException(ErrorCode.PLACE_NOT_FOUND));

        if(isInvalidPlace(name)) {
            throw new WebApplicationException(ErrorCode.INVALID_PLACE, String.format("team is %s", name));
        }

        place.setName(name);
        placeRepository.saveAndFlush(place);

        return PlaceDTO.toDTO(place);
    }

    @Transactional
    public void deletePlace(Long placeId) {
        Place place = placeRepository.findByIdAndDeletedAtIsNull(placeId)
                .orElseThrow(() -> new WebApplicationException(ErrorCode.PLACE_NOT_FOUND));

        place.setDeletedAt(new Timestamp(System.currentTimeMillis()));
        placeRepository.saveAndFlush(place);
    }

    @Transactional
    public void restorePlace(Long placeId) {
        Place place = placeRepository.findByIdAndDeletedAtIsNotNull(placeId)
                .orElseThrow(() -> new WebApplicationException(ErrorCode.PLACE_ALREADY_EXISTS));

        place.setDeletedAt(null);
        placeRepository.saveAndFlush(place);
    }

    private boolean isInvalidPlace(String name) {
        return !Pattern.matches("^[가-힇]*$", name);
    }

}
