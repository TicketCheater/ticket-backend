package com.ticketcheater.webservice.fixture;

import com.ticketcheater.webservice.entity.Place;

public class PlaceFixture {

    public static Place get(Long id) {
        Place place = new Place();
        place.setId(id);
        return place;
    }

    public static Place get(String name) {
        Place place = new Place();
        place.setName(name);
        return place;
    }

}
