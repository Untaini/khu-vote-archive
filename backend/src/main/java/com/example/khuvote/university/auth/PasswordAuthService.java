package com.example.khuvote.university.auth;

import com.example.khuvote.university.dto.PasswordAuthDTO;
import com.example.khuvote.university.type.CampusType;
import com.example.khuvote.university.type.CollegeType;
import com.example.khuvote.university.type.DepartmentType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PasswordAuthService {
    Map<String, CampusType> campusPasswordMap = new HashMap<>();
    Map<String, CollegeType> collegePasswordMap = new HashMap<>();
    Map<String, DepartmentType> departmentPasswordMap = new HashMap<>();

    public PasswordAuthService() {
        campusPasswordMap.put("4LLBtVCn", CampusType.GLOBAL);
        collegePasswordMap.put("rYrJTuPF", CollegeType.SOFTWARE);
        departmentPasswordMap.put("5OJTuDPp", DepartmentType.COMPUTER_SCIENCE_AND_ENGINEERING);
    }

    public Boolean verify(String password) {
        return campusPasswordMap.containsKey(password)
                || collegePasswordMap.containsKey(password)
                || departmentPasswordMap.containsKey(password);
    }

    public PasswordAuthDTO.Response getAffiliation(PasswordAuthDTO.Request request) {
        String password = request.password();

        if (!verify(password)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        final String affiliation;
        if (campusPasswordMap.containsKey(password)) {
            affiliation = campusPasswordMap.get(password).getName();
        } else if (collegePasswordMap.containsKey(password)) {
            affiliation = collegePasswordMap.get(password).getName();
        } else if (departmentPasswordMap.containsKey(password)) {
            affiliation = departmentPasswordMap.get(password).getName();
        } else {
            throw new RuntimeException("버그가 발생했습니다.");
        }

        return PasswordAuthDTO.Response.builder()
                .affiliation(affiliation)
                .build();
    }
}
