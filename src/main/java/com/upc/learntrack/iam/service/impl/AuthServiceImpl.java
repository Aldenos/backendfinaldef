package com.upc.learntrack.iam.service.impl;

import com.upc.learntrack.course.model.Student;
import com.upc.learntrack.course.model.Teacher;
import com.upc.learntrack.course.repository.StudentRepository;
import com.upc.learntrack.course.repository.TeacherRepository;
import com.upc.learntrack.iam.dto.LoginRequestDto;
import com.upc.learntrack.iam.dto.RegisterRequestDto;
import com.upc.learntrack.iam.exception.RoleNotFoundException;
import com.upc.learntrack.iam.exception.UserNotFoundException;
import com.upc.learntrack.iam.model.Role;
import com.upc.learntrack.iam.model.User;
import com.upc.learntrack.iam.model.UserStatus;
import com.upc.learntrack.iam.repository.RoleRepository;
import com.upc.learntrack.iam.repository.UserRepository;
import com.upc.learntrack.iam.service.AuthService;
import com.upc.learntrack.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public String register(RegisterRequestDto request) {
        if (!request.getEmail().toLowerCase().endsWith("@upc.edu.pe")) {
            throw new IllegalArgumentException("No se pudo crear la cuenta. Ingrese un correo institucional válido (@upc.edu.pe).");
        }

        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new RoleNotFoundException("El rol '" + request.getRoleName() + "' no existe."));

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Error: El email ya está en uso.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setVerified(false);
        user.setStatus(UserStatus.PENDING);
        user.setVerificationCode(null);
        user.setVerificationAttempts(0);

        User savedUser = userRepository.save(user);

        // Crear perfil Teacher o Student según rol
        if (role.getName().equalsIgnoreCase("DOCENTE")) {
            Teacher teacher = new Teacher();
            teacher.setFirstName(request.getFirstName());
            teacher.setLastName(request.getLastName());
            teacher.setUser(savedUser);
            teacherRepository.save(teacher);
        } else if (role.getName().equalsIgnoreCase("ESTUDIANTE")) {
            Student student = new Student();
            student.setFirstName(request.getFirstName());
            student.setLastName(request.getLastName());
            student.setUser(savedUser);
            studentRepository.save(student);
        }

        // NO devolvemos token, solo mensaje de éxito (puedes devolver un string)
        return "Usuario registrado correctamente. Espera la aprobación del administrador.";
    }

    @Override
    public String login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (user.getStatus() == UserStatus.PENDING) {
            throw new IllegalStateException("Tu cuenta está pendiente de verificación. Revisa tu correo.");
        }
        if (user.getStatus() == UserStatus.REJECTED) {
            throw new IllegalStateException("Tu cuenta ha sido rechazada. Contacta con soporte.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        return jwtService.generateToken(user.getEmail());
    }
}