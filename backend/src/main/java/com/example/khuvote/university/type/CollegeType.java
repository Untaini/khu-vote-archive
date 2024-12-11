package com.example.khuvote.university.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CollegeType {
    UNKNOWN(null, null),

    ENGINEERING("공과대학", CampusType.GLOBAL),
    ELECTRONICS_AND_INFORMATION("전자정보대학", CampusType.GLOBAL),
    SOFTWARE("소프트웨어융합대학", CampusType.GLOBAL),
    APPLIED_SCIENCES("응용과학대학", CampusType.GLOBAL),
    LIFE_SCIENCES("생명과학대학", CampusType.GLOBAL),
    INTERNATIONAL_STUDIES("국제대학", CampusType.GLOBAL),
    FOREIGN_LANGUAGE_AND_LITERATURE("외국어대학", CampusType.GLOBAL),
    ART_AND_DESIGN("예술·디자인대학", CampusType.GLOBAL),
    PHYSICAL_EDUCATION("체육대학", CampusType.GLOBAL),
    ;

    private String name;
    private CampusType campusType;

    public static Map<String, CollegeType> collegeTypeMap =
            Collections.unmodifiableMap(
                    Stream.of(CollegeType.values())
                            .collect(Collectors.toMap(CollegeType::getName, Function.identity()))
            );

    public static CollegeType find(String name) {
        return Optional.ofNullable(collegeTypeMap.get(name)).orElse(UNKNOWN);
    }
}
