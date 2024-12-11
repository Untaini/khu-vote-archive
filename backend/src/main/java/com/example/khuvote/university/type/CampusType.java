package com.example.khuvote.university.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CampusType {
    SEOUL("서울캠퍼스"),
    GLOBAL("국제캠퍼스"),
    HUMANITAS("후마니타스칼리지");

    private String name;
}
