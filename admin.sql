-- 1. Asegurar que el rol ADMIN existe
INSERT INTO roles (name) VALUES ('ADMIN') ON CONFLICT (name) DO NOTHING;

-- 2. Actualizar usuario existente o insertarlo
INSERT INTO users (
    email,
    password,
    role_id,
    status,
    verified,
    created_at
)
VALUES (
           'U202216001@upc.edu.pe',
           '$2a$10$ZN9qU5ZWAzLhXhHTKX1qN.C8U.8I.qv1d8qQ1lJfVp3cL6m1n2o3p',  -- contraseña: admin123
           (SELECT id FROM roles WHERE name = 'ADMIN'),
           'ACTIVE',
           true,
           NOW()
       )
    ON CONFLICT (email) DO UPDATE SET
    password = EXCLUDED.password,
                               role_id = EXCLUDED.role_id,
                               status = 'ACTIVE',
                               verified = true;

-- 3. Insertar perfil de Teacher si no existe
INSERT INTO teachers (first_name, last_name, user_id)
SELECT 'Admin', 'Soporte', id
FROM users
WHERE email = 'U202216001@upc.edu.pe'
    ON CONFLICT (user_id) DO NOTHING;

-- 4. Verificar
SELECT id, email, status, role_id FROM users WHERE email = 'U202216001@upc.edu.pe';