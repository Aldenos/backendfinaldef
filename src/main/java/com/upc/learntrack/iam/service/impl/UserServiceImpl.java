package com.upc.learntrack.iam.service.impl;

import com.upc.learntrack.iam.dto.UserDto;
import com.upc.learntrack.iam.exception.RoleNotFoundException;
import com.upc.learntrack.iam.exception.UserNotFoundException;
import com.upc.learntrack.iam.mapper.UserMapper;
import com.upc.learntrack.iam.model.Role;
import com.upc.learntrack.iam.model.User;
import com.upc.learntrack.iam.repository.RoleRepository;
import com.upc.learntrack.iam.repository.UserRepository;
import com.upc.learntrack.iam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con email: " + email));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto save(UserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        Role role = roleRepository.findByName(dto.getRoleName())
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado con nombre: " + dto.getRoleName()));
        User user = userMapper.toEntity(dto);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));

        Role role = roleRepository.findByName(dto.getRoleName())
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado con nombre: " + dto.getRoleName()));

        existingUser.setEmail(dto.getEmail());
        existingUser.setRole(role);
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return userMapper.toDto(userRepository.save(existingUser));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }
}