package com.upc.learntrack.iam.service;

import com.upc.learntrack.iam.dto.UserDto;
import java.util.List;

public interface UserService {
    List<UserDto> findAll();
    UserDto findById(Long id);
    UserDto findByEmail(String email);
    UserDto save(UserDto userDto);
    UserDto update(Long id, UserDto userDto);
    void delete(Long id);
}