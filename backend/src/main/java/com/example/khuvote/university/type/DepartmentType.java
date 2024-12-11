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
public enum DepartmentType {
    UNKNOWN(null),

    COMPUTER_SCIENCE_AND_ENGINEERING("컴퓨터공학부"),
    ARTIFICIAL_INTELLIGENCE("인공지능학과"),
    SOFTWARE_CONVERGENCE("소프트웨어융합학과"),
    ;

    private String name;

    public static Map<String, DepartmentType> departmentTypeMap =
            Collections.unmodifiableMap(
                    Stream.of(DepartmentType.values())
                            .collect(Collectors.toMap(DepartmentType::getName, Function.identity()))
            );

    public static DepartmentType find(String name) {
        return Optional.ofNullable(departmentTypeMap.get(name)).orElse(UNKNOWN);
    }
}
