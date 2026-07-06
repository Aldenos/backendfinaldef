package com.upc.learntrack.course.service.impl;

import com.upc.learntrack.assessment.repository.ActivityResultRepository;
import com.upc.learntrack.assessment.model.ActivityResult;
import com.upc.learntrack.course.dto.*;
import com.upc.learntrack.course.exception.GroupNotFoundException;
import com.upc.learntrack.course.exception.LearningCollectionNotFoundException;
import com.upc.learntrack.course.exception.StudentNotFoundException;
import com.upc.learntrack.course.exception.TeacherNotFoundException;
import com.upc.learntrack.course.mapper.GroupMapper;
import com.upc.learntrack.course.mapper.StudentMapper;
import com.upc.learntrack.course.model.*;
import com.upc.learntrack.course.repository.*;
import com.upc.learntrack.course.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;
    private final LearningCollectionRepository learningCollectionRepository;
    private final GroupMapper groupMapper;
    private final StudentMapper studentMapper;
    private final GroupTopicPriorityRepository priorityRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ActivityResultRepository activityResultRepository;
    private final GroupJoinCodeRepository groupJoinCodeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GroupDto> findAll() {
        return groupRepository.findAll().stream()
                .map(groupMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GroupDto findById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado con ID: " + id));
        return groupMapper.toDto(group);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupDto> findAllMyGroups(String teacherEmail) {
        Teacher teacher = teacherRepository.findByUserEmail(teacherEmail)
                .orElseThrow(() -> new TeacherNotFoundException("Profesor no encontrado con correo: " + teacherEmail));
        return groupRepository.findAllByTeacherId(teacher.getId()).stream()
                .map(groupMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GroupDto save(GroupDto dto, String teacherEmail) {
        if (dto.getCollectionId() == null) {
            throw new IllegalArgumentException("El grupo debe pertenecer a una colección.");
        }
        return saveInCollection(dto.getCollectionId(), dto, teacherEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> findStudentsByGroupId(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado con ID: " + groupId));
        return group.getEnrollments().stream()
                .map(Enrollment::getStudent)
                .filter(Objects::nonNull)
                .map(studentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void setTopicPriority(Long groupId, Long topicId, boolean priority) {
        GroupTopicId id = new GroupTopicId(groupId, topicId);
        GroupTopicPriority gtp = priorityRepository.findById(id)
                .orElse(new GroupTopicPriority());
        gtp.setId(id);
        gtp.setPriority(priority);
        priorityRepository.save(gtp);
    }

    @Override
    @Transactional
    public List<StudentDto> enrollStudents(String groupCode, List<String> studentEmails, String teacherEmail) {
        Group group = groupRepository.findByCode(groupCode)
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado con código: " + groupCode));
        if (!group.getTeacher().getUser().getEmail().equals(teacherEmail)) {
            throw new SecurityException("No tienes permisos para inscribir estudiantes en este grupo.");
        }
        List<StudentDto> enrolledStudents = new ArrayList<>();
        for (String email : studentEmails) {
            Student student = studentRepository.findByUserEmail(email)
                    .orElseThrow(() -> new StudentNotFoundException("Estudiante no encontrado con email: " + email));
            if (!enrollmentRepository.existsByStudentIdAndGroupId(student.getId(), group.getId())) {
                Enrollment enrollment = new Enrollment();
                enrollment.setGroup(group);
                enrollment.setStudent(student);
                enrollmentRepository.save(enrollment);
                enrolledStudents.add(studentMapper.toDto(student));
            }
        }
        return enrolledStudents;
    }

    // ========== NUEVOS MÉTODOS ==========

    @Override
    @Transactional(readOnly = true)
    public GroupDetailsDto findByCode(String code) {
        Group group = groupRepository.findByCode(code)
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado con código: " + code));
        GroupDetailsDto dto = new GroupDetailsDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setCode(group.getCode());
        dto.setCreatedAt(group.getCreatedAt());
        dto.setStudentsCount(group.getEnrollments().size());
        dto.setActivitiesCount(0); // Se puede calcular después si se necesita
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentSimpleDto> findStudentsByGroupCode(String code) {
        Group group = groupRepository.findByCode(code)
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado con código: " + code));
        return group.getEnrollments().stream()
                .map(Enrollment::getStudent)
                .map(s -> {
                    StudentSimpleDto dto = new StudentSimpleDto();
                    dto.setId(s.getId());
                    dto.setName(s.getFirstName() + " " + s.getLastName());
                    dto.setEmail(s.getUser().getEmail());
                    // Por defecto (se pueden calcular con activityResultRepository)
                    dto.setAvgScore(0);
                    dto.setCompletedActivities(0);
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentSimpleDto enrollStudent(String groupCode, String studentEmail, String teacherEmail) {
        Group group = groupRepository.findByCode(groupCode)
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado"));
        if (!group.getTeacher().getUser().getEmail().equals(teacherEmail)) {
            throw new SecurityException("No tienes permiso para inscribir en este grupo");
        }
        Student student = studentRepository.findByUserEmail(studentEmail)
                .orElseThrow(() -> new StudentNotFoundException("Estudiante no encontrado"));
        if (enrollmentRepository.existsByStudentIdAndGroupId(student.getId(), group.getId())) {
            throw new IllegalArgumentException("El estudiante ya está inscrito");
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setGroup(group);
        enrollment.setStudent(student);
        enrollmentRepository.save(enrollment);

        StudentSimpleDto dto = new StudentSimpleDto();
        dto.setId(student.getId());
        dto.setName(student.getFirstName() + " " + student.getLastName());
        dto.setEmail(student.getUser().getEmail());
        dto.setAvgScore(0);
        dto.setCompletedActivities(0);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentStatDto> getStudentStats(String groupCode) {
        Group group = groupRepository.findByCode(groupCode)
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado"));
        List<Student> students = group.getEnrollments().stream()
                .map(Enrollment::getStudent)
                .toList();
        List<StudentStatDto> stats = new ArrayList<>();
        for (Student student : students) {
            List<ActivityResult> results = activityResultRepository.findAllByStudentId(student.getId());
            double avg = results.stream().mapToDouble(r -> r.getScore().doubleValue()).average().orElse(0.0);
            StudentStatDto dto = new StudentStatDto();
            dto.setName(student.getFirstName() + " " + student.getLastName());
            dto.setEmail(student.getUser().getEmail());
            dto.setScore((int) Math.round(avg));
            dto.setCompleted(results.size());
            stats.add(dto);
        }
        return stats;
    }

    @Override
    @Transactional
    public GroupDto update(Long id, GroupDto dto, String teacherEmail) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado"));

        if (!group.getTeacher().getUser().getEmail().equals(teacherEmail)) {
            throw new SecurityException("No tienes permiso para modificar este grupo");
        }

        // Validar código duplicado (excepto sí mismo)
        if (!group.getCode().equals(dto.getCode()) && groupRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("El código '" + dto.getCode() + "' ya está en uso.");
        }

        // Validar nombre duplicado para el mismo profesor (excepto sí mismo)
        if (!group.getName().equals(dto.getName()) &&
                groupRepository.existsByTeacherIdAndName(group.getTeacher().getId(), dto.getName())) {
            throw new IllegalArgumentException("Ya existe un grupo con el nombre '" + dto.getName() + "'.");
        }

        group.setName(dto.getName());
        group.setCode(dto.getCode());
        return groupMapper.toDto(groupRepository.save(group));
    }

    @Override
    @Transactional
    public void removeStudentFromGroup(String groupCode, String studentEmail, String teacherEmail) {
        Group group = groupRepository.findByCode(groupCode)
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado"));

        if (!group.getTeacher().getUser().getEmail().equals(teacherEmail)) {
            throw new SecurityException("No tienes permiso para modificar este grupo");
        }

        Student student = studentRepository.findByUserEmail(studentEmail)
                .orElseThrow(() -> new StudentNotFoundException("Estudiante no encontrado"));

        // Buscar la matrícula
        Enrollment enrollment = enrollmentRepository.findAllByStudentId(student.getId()).stream()
                .filter(e -> e.getGroup().getId().equals(group.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El estudiante no pertenece a este grupo"));

        enrollmentRepository.delete(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupDto> findGroupsByCollection(Long collectionId, String teacherEmail) {
        Teacher teacher = teacherRepository.findByUserEmail(teacherEmail)
                .orElseThrow(() -> new TeacherNotFoundException("Profesor no encontrado con correo: " + teacherEmail));
        return groupRepository.findAllByTeacherIdAndLearningCollectionId(teacher.getId(), collectionId).stream()
                .map(groupMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GroupDto saveInCollection(Long collectionId, GroupDto dto, String teacherEmail) {
        if (groupRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("El código de grupo '" + dto.getCode() + "' ya está en uso.");
        }
        Teacher teacher = teacherRepository.findByUserEmail(teacherEmail)
                .orElseThrow(() -> new TeacherNotFoundException("Profesor no encontrado con correo: " + teacherEmail));
        LearningCollection collection = learningCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new LearningCollectionNotFoundException("Colección no encontrada con ID: " + collectionId));
        if (!collection.getTeacher().getId().equals(teacher.getId())) {
            throw new SecurityException("No tienes permiso para crear grupos en esta colección.");
        }
        if (groupRepository.existsByTeacherIdAndName(teacher.getId(), dto.getName())) {
            throw new IllegalArgumentException("Ya existe un grupo con el nombre '" + dto.getName() + "'.");
        }
        Group group = groupMapper.toEntity(dto);
        group.setTeacher(teacher);
        group.setLearningCollection(collection);
        return groupMapper.toDto(groupRepository.save(group));
    }

    @Override
    @Transactional
    public void deleteGroup(Long id, String teacherEmail) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado con ID: " + id));
        if (!group.getTeacher().getUser().getEmail().equals(teacherEmail)) {
            throw new SecurityException("No tienes permiso para eliminar este grupo.");
        }
        groupRepository.delete(group);
    }

    @Override
    @Transactional
    public GroupJoinCodeDto generateJoinCode(String groupCode, String teacherEmail) {
        Group group = groupRepository.findByCode(groupCode)
                .orElseThrow(() -> new GroupNotFoundException("Grupo no encontrado con código: " + groupCode));
        if (!group.getTeacher().getUser().getEmail().equals(teacherEmail)) {
            throw new SecurityException("No tienes permisos para generar un código en este grupo.");
        }

        String code;
        do {
            code = generateRandomCode();
        } while (groupJoinCodeRepository.existsByCode(code));

        GroupJoinCode joinCode = new GroupJoinCode();
        joinCode.setCode(code);
        joinCode.setGroup(group);
        joinCode.setExpiresAt(java.time.LocalDateTime.now().plusMinutes(10));
        groupJoinCodeRepository.save(joinCode);

        GroupJoinCodeDto dto = new GroupJoinCodeDto();
        dto.setCode(joinCode.getCode());
        dto.setGroupCode(group.getCode());
        dto.setExpiresAt(joinCode.getExpiresAt());
        return dto;
    }

    private String generateRandomCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        java.util.Random random = new java.util.Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    @Transactional
    public StudentSimpleDto joinGroupByTemporaryCode(String code, String studentEmail) {
        GroupJoinCode joinCode = groupJoinCodeRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Código inválido o no existe."));

        if (joinCode.isExpired()) {
            throw new IllegalStateException("El código ha expirado. Solicita uno nuevo a tu docente.");
        }

        Student student = studentRepository.findByUserEmail(studentEmail)
                .orElseThrow(() -> new StudentNotFoundException("No se encontró el perfil de estudiante."));

        Group group = joinCode.getGroup();

        if (!enrollmentRepository.existsByStudentIdAndGroupId(student.getId(), group.getId())) {
            Enrollment enrollment = new Enrollment();
            enrollment.setGroup(group);
            enrollment.setStudent(student);
            enrollmentRepository.save(enrollment);
        }

        StudentSimpleDto dto = new StudentSimpleDto();
        dto.setId(student.getId());
        dto.setName(student.getFirstName() + " " + student.getLastName());
        dto.setEmail(studentEmail);
        return dto;
    }

    @Override
    @Transactional
    public void setTopicAssigned(Long groupId, Long topicId, boolean assigned) {
        GroupTopicId id = new GroupTopicId(groupId, topicId);
        GroupTopicPriority gtp = priorityRepository.findById(id)
                .orElse(new GroupTopicPriority());
        gtp.setId(id);
        gtp.setAssigned(assigned);
        priorityRepository.save(gtp);
    }
}