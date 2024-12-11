package com.example.khuvote.university;

import com.example.khuvote.university.type.CollegeType;
import com.example.khuvote.university.type.DepartmentType;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AffiliationInfoParser {
    private static final Pattern INFO_PATTERN = Pattern.compile("(.+?)\\[(.+?)\\]\\((.+?) (.+?)\\)");
    private final String MemberIsNotUndergraduateMessage = "학부생이 아닙니다.";

    public AffiliationInfo parseAffiliationInfo(String googleName) {
        Matcher matcher = INFO_PATTERN.matcher(googleName);

        if (matcher.matches()) {
            CollegeType collegeType = CollegeType.find(matcher.group(3));
            DepartmentType departmentType = DepartmentType.find(matcher.group(4));

            if (collegeType == null) {
                throw new RuntimeException(MemberIsNotUndergraduateMessage);
            }

            return new AffiliationInfo(collegeType, departmentType);
        } else {
            throw new RuntimeException(MemberIsNotUndergraduateMessage);
        }
    }

    public record AffiliationInfo(CollegeType collegeType, DepartmentType departmentType) {}
}
