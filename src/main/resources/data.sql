-- =================================================================================
-- LearnTrack - Datos de Prueba Limpios
-- Modelo:
-- Colección = curso
-- Grupo = salón/sección dentro de una colección
-- Tema = contenido global de una colección
-- =================================================================================

-- 1. ROLES
INSERT INTO roles (id, name) VALUES
    (1, 'DOCENTE'),
    (2, 'ESTUDIANTE')
ON CONFLICT (id) DO NOTHING;


-- 2. USUARIOS
-- Contraseña de prueba: "password123" hasheada con BCrypt
INSERT INTO users (id, email, password, role_id, verified, status, created_at) VALUES
    (1, 'profesor@upc.edu.pe', '$2a$10$7v52m9GkO1x985q0/3yY8.A2Tz3mK.7T1G0Xf7J0j9h9Z2P3oYQOW', 1, true, 'ACTIVE', CURRENT_TIMESTAMP),
    (2, 'alumno1@upc.edu.pe', '$2a$10$7v52m9GkO1x985q0/3yY8.A2Tz3mK.7T1G0Xf7J0j9h9Z2P3oYQOW', 2, true, 'ACTIVE', CURRENT_TIMESTAMP),
    (3, 'alumno2@upc.edu.pe', '$2a$10$7v52m9GkO1x985q0/3yY8.A2Tz3mK.7T1G0Xf7J0j9h9Z2P3oYQOW', 2, true, 'ACTIVE', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;


-- 3. PERFILES DOCENTE / ESTUDIANTE
INSERT INTO teachers (id, user_id, first_name, last_name) VALUES
    (1, 1, 'Carlos', 'Gómez')
ON CONFLICT (id) DO NOTHING;

INSERT INTO students (id, user_id, first_name, last_name) VALUES
    (1, 2, 'Ana', 'García'),
    (2, 3, 'Pedro', 'López')
ON CONFLICT (id) DO NOTHING;


-- 4. COLECCIONES / CURSOS
INSERT INTO collections (id, code, name, description, teacher_id, created_at) VALUES
    (1, 'JAVA-ADV', 'Java Avanzado', 'Spring Boot y Microservicios', 1, CURRENT_TIMESTAMP),
    (2, 'ALG-2026', 'Algoritmos', 'Estructuras de datos y complejidad', 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;


-- 5. GRUPOS / SALONES DENTRO DE COLECCIONES
INSERT INTO groups (id, code, name, teacher_id, collection_id, created_at) VALUES
    (1, 'SW51', 'Sección SW51', 1, 1, CURRENT_TIMESTAMP),
    (2, 'SW52', 'Sección SW52', 1, 1, CURRENT_TIMESTAMP),
    (3, 'CC71', 'Sección CC71', 1, 2, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;


-- 6. MATRÍCULAS DE ESTUDIANTES EN GRUPOS
INSERT INTO enrollments (id, student_id, group_id, enrolled_at) VALUES
    (1, 1, 1, CURRENT_TIMESTAMP),
    (2, 2, 1, CURRENT_TIMESTAMP),
    (3, 1, 3, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;


-- 7. TEMAS GLOBALES POR COLECCIÓN
INSERT INTO topics (id, name, order_idx, collection_id) VALUES
    (1, 'Fundamentos de Spring', 1, 1),
    (2, 'Spring Data JPA', 2, 1),
    (3, 'Búsqueda Binaria', 1, 2),
    (4, 'Grafos', 2, 2)
ON CONFLICT (id) DO NOTHING;


-- 8. CONFIGURACIÓN DE TEMAS POR GRUPO
-- assigned = indica si el tema está asignado/visible para ese grupo
-- priority = indica si el tema es prioritario para ese grupo
INSERT INTO group_topic_priorities (group_id, topic_id, priority, assigned) VALUES
    (1, 1, true, true),
    (1, 2, false, true),

    (2, 1, false, true),
    (2, 2, true, false),

    (3, 3, true, true),
    (3, 4, false, true)
ON CONFLICT (group_id, topic_id) DO NOTHING;


-- 9. CÓDIGOS TEMPORALES PARA UNIRSE A GRUPOS
-- Estos códigos expiran en 10 minutos.
INSERT INTO group_join_codes (id, code, group_id, expires_at, created_at) VALUES
    (1, 'A7K92', 1, CURRENT_TIMESTAMP + INTERVAL '10 minutes', CURRENT_TIMESTAMP),
    (2, 'B8Q21', 3, CURRENT_TIMESTAMP + INTERVAL '10 minutes', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;


-- 10. VINCULACIÓN COLECCIÓN-GRUPO
-- Se mantiene por compatibilidad con el backend actual.
-- La relación principal nueva debe ser groups.collection_id.
INSERT INTO collection_groups (collection_id, group_id, linked_at) VALUES
    (1, 1, CURRENT_TIMESTAMP),
    (1, 2, CURRENT_TIMESTAMP),
    (2, 3, CURRENT_TIMESTAMP)
ON CONFLICT (collection_id, group_id) DO NOTHING;


-- 11. SUBTEMAS
INSERT INTO subtopics (id, name, topic_id) VALUES
    (1, 'Inyección de dependencias', 1),
    (2, 'Ciclo de vida de beans', 1),
    (3, 'Consultas derivadas', 2),
    (4, 'Implementación iterativa', 3)
ON CONFLICT (id) DO NOTHING;


-- 12. ACTIVIDADES
INSERT INTO activities (
    id,
    title,
    description,
    type,
    status,
    generated_by_ai,
    created_by_email,
    personal,
    topic_id,
    created_at
) VALUES
    (1, 'Quiz Spring Core', 'Preguntas sobre IoC y DI', 'QUIZ', 'PUBLISHED', false, 'profesor@upc.edu.pe', false, 1, CURRENT_TIMESTAMP),
    (2, 'Quiz Búsqueda Binaria', 'Evaluación de algoritmos', 'QUIZ', 'PUBLISHED', false, 'profesor@upc.edu.pe', false, 3, CURRENT_TIMESTAMP),
    (3, 'Actividad personal de Ana', 'Práctica extra', 'QUIZ', 'DRAFT', false, 'alumno1@upc.edu.pe', true, 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;


-- 13. PREGUNTAS
INSERT INTO questions (id, statement, explanation, order_idx, activity_id, subtopic_id) VALUES
    (1, '¿Qué es la inyección de dependencias?', 'Es un patrón donde los objetos reciben sus dependencias', 0, 1, 1),
    (2, '¿Cuál es la anotación principal de Spring Boot?', '@SpringBootApplication combina @Configuration, @EnableAutoConfiguration y @ComponentScan', 1, 1, NULL)
ON CONFLICT (id) DO NOTHING;


-- 14. OPCIONES DE PREGUNTAS
INSERT INTO question_options (id, text, correct, question_id) VALUES
    (1, 'Un patrón de diseño', true, 1),
    (2, 'Un framework', false, 1),
    (3, 'Un lenguaje', false, 1),
    (4, 'Una base de datos', false, 1),

    (5, '@SpringBootApplication', true, 2),
    (6, '@Autowired', false, 2),
    (7, '@Component', false, 2),
    (8, '@Service', false, 2)
ON CONFLICT (id) DO NOTHING;


-- 15. ASIGNACIONES DE ACTIVIDADES A GRUPOS
INSERT INTO assignments (id, due_date, activity_id, group_id) VALUES
    (1, CURRENT_TIMESTAMP + INTERVAL '7 days', 1, 1),
    (2, NULL, 2, 3)
ON CONFLICT (id) DO NOTHING;


-- 16. RESULTADOS DE ACTIVIDADES
INSERT INTO activity_results (
    id,
    activity_id,
    student_id,
    score,
    total_questions,
    correct_answers,
    time_spent_seconds,
    status,
    completed_at,
    created_at
) VALUES
    (1, 1, 1, 95.00, 2, 2, 300, 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 2, 40.00, 2, 1, 400, 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 2, 1, 90.00, 2, 2, 200, 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;


-- 17. RESPUESTAS DETALLADAS DE ESTUDIANTES
INSERT INTO student_answers (id, is_correct, answered_at, question_id, result_id, selected_option_id) VALUES
    (1, true, CURRENT_TIMESTAMP, 1, 1, 1),
    (2, true, CURRENT_TIMESTAMP, 2, 1, 5),

    (3, false, CURRENT_TIMESTAMP, 1, 2, 2),
    (4, true, CURRENT_TIMESTAMP, 2, 2, 5),

    (5, true, CURRENT_TIMESTAMP, 1, 3, 1),
    (6, true, CURRENT_TIMESTAMP, 2, 3, 5)
ON CONFLICT (id) DO NOTHING;


-- 18. RUTAS DE APRENDIZAJE
INSERT INTO learning_paths (
    id,
    student_id,
    collection_id,
    current_percentage,
    target_percentage,
    status,
    created_at,
    updated_at
) VALUES
    (1, 1, 1, 90.00, 70.00, 'IN_PROGRESS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 2, 1, 30.00, 70.00, 'IN_PROGRESS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;


-- 19. NODOS DE RUTA
INSERT INTO path_nodes (
    id,
    order_idx,
    completed,
    mastery_score,
    learning_path_id,
    topic_id
) VALUES
    (1, 0, true, 95.00, 1, 1),
    (2, 1, false, 0.00, 1, 2),
    (3, 0, false, 40.00, 2, 1),
    (4, 1, false, 0.00, 2, 2)
ON CONFLICT (id) DO NOTHING;


-- 20. BRECHAS CONCEPTUALES
INSERT INTO conceptual_gaps (id, learning_path_id, topic_id, description, resolved) VALUES
    (1, 2, 1, 'Inyección de dependencias mal comprendida', false),
    (2, 2, 2, 'Confusión entre Lazy y Eager loading', false)
ON CONFLICT (id) DO NOTHING;


-- 21. FLASHCARD SETS
INSERT INTO flashcard_sets (id, title, topic_id, created_at) VALUES
    (1, 'Spring Core Essentials', 1, CURRENT_TIMESTAMP),
    (2, 'JPA Básico', 2, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;


-- 22. FLASHCARDS
INSERT INTO flashcards (id, front, back, flashcard_set_id) VALUES
    (1, '¿Qué es IoC?', 'Inversión de control, el framework gestiona los objetos', 1),
    (2, '¿Qué hace @Autowired?', 'Inyecta automáticamente una dependencia', 1),
    (3, '¿Qué es un repositorio JPA?', 'Interfaz que proporciona métodos CRUD', 2)
ON CONFLICT (id) DO NOTHING;


-- 23. PROGRESO DE FLASHCARDS
INSERT INTO student_flashcard_progress (
    id,
    recalled,
    flashcard_id,
    student_id,
    last_reviewed_at
) VALUES
    (1, true, 1, 1, CURRENT_TIMESTAMP),
    (2, false, 2, 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;


-- 24. REPORTES PDF
INSERT INTO pdf_reports (
    id,
    file_url,
    sent_to,
    group_id,
    collection_id,
    created_at,
    pdf_content
) VALUES
    (1, 'generated_1712345678', 'profesor@upc.edu.pe', 1, 1, CURRENT_TIMESTAMP, E'\\x255044462d312e34')
ON CONFLICT (id) DO NOTHING;


-- 25. ESTADÍSTICAS SNAPSHOT
INSERT INTO statistics_snapshots (
    id,
    mastery_percentage,
    students_count,
    generated_at,
    group_id,
    topic_id
) VALUES
    (1, 67.50, 2, CURRENT_TIMESTAMP, 1, 1),
    (2, 90.00, 1, CURRENT_TIMESTAMP, 3, 3)
ON CONFLICT (id) DO NOTHING;


-- 26. SINCRONIZACIÓN DE SECUENCIAS
SELECT setval(pg_get_serial_sequence('roles', 'id'), COALESCE(MAX(id), 1)) FROM roles;
SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE(MAX(id), 1)) FROM users;
SELECT setval(pg_get_serial_sequence('teachers', 'id'), COALESCE(MAX(id), 1)) FROM teachers;
SELECT setval(pg_get_serial_sequence('students', 'id'), COALESCE(MAX(id), 1)) FROM students;
SELECT setval(pg_get_serial_sequence('collections', 'id'), COALESCE(MAX(id), 1)) FROM collections;
SELECT setval(pg_get_serial_sequence('groups', 'id'), COALESCE(MAX(id), 1)) FROM groups;
SELECT setval(pg_get_serial_sequence('enrollments', 'id'), COALESCE(MAX(id), 1)) FROM enrollments;
SELECT setval(pg_get_serial_sequence('topics', 'id'), COALESCE(MAX(id), 1)) FROM topics;
SELECT setval(pg_get_serial_sequence('subtopics', 'id'), COALESCE(MAX(id), 1)) FROM subtopics;
SELECT setval(pg_get_serial_sequence('activities', 'id'), COALESCE(MAX(id), 1)) FROM activities;
SELECT setval(pg_get_serial_sequence('questions', 'id'), COALESCE(MAX(id), 1)) FROM questions;
SELECT setval(pg_get_serial_sequence('question_options', 'id'), COALESCE(MAX(id), 1)) FROM question_options;
SELECT setval(pg_get_serial_sequence('assignments', 'id'), COALESCE(MAX(id), 1)) FROM assignments;
SELECT setval(pg_get_serial_sequence('activity_results', 'id'), COALESCE(MAX(id), 1)) FROM activity_results;
SELECT setval(pg_get_serial_sequence('student_answers', 'id'), COALESCE(MAX(id), 1)) FROM student_answers;
SELECT setval(pg_get_serial_sequence('learning_paths', 'id'), COALESCE(MAX(id), 1)) FROM learning_paths;
SELECT setval(pg_get_serial_sequence('path_nodes', 'id'), COALESCE(MAX(id), 1)) FROM path_nodes;
SELECT setval(pg_get_serial_sequence('conceptual_gaps', 'id'), COALESCE(MAX(id), 1)) FROM conceptual_gaps;
SELECT setval(pg_get_serial_sequence('flashcard_sets', 'id'), COALESCE(MAX(id), 1)) FROM flashcard_sets;
SELECT setval(pg_get_serial_sequence('flashcards', 'id'), COALESCE(MAX(id), 1)) FROM flashcards;
SELECT setval(pg_get_serial_sequence('student_flashcard_progress', 'id'), COALESCE(MAX(id), 1)) FROM student_flashcard_progress;
SELECT setval(pg_get_serial_sequence('pdf_reports', 'id'), COALESCE(MAX(id), 1)) FROM pdf_reports;
SELECT setval(pg_get_serial_sequence('statistics_snapshots', 'id'), COALESCE(MAX(id), 1)) FROM statistics_snapshots;
SELECT setval(pg_get_serial_sequence('group_join_codes', 'id'), COALESCE(MAX(id), 1)) FROM group_join_codes;
