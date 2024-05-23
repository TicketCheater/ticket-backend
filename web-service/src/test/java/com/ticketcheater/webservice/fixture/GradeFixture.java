package com.ticketcheater.webservice.fixture;

import com.ticketcheater.webservice.entity.Grade;
import com.ticketcheater.webservice.entity.Place;

public class GradeFixture {

    public static Grade get(Long gradeId) {
        Grade grade = new Grade();
        grade.setId(gradeId);
        return grade;
    }

    public static Grade get(Place place, String name) {
        Grade grade = new Grade();
        grade.setPlace(place);
        grade.setName(name);
        return grade;
    }

}
