SET REFERENTIAL_INTEGRITY FALSE;

DROP TABLE IF EXISTS users_roles;
DROP TABLE IF EXISTS rentals;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;

SET REFERENTIAL_INTEGRITY TRUE;

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       is_deleted BIT NOT NULL DEFAULT FALSE


);

CREATE TABLE roles (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       type VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users_roles (
                             user_id BIGINT NOT NULL,
                             role_id BIGINT NOT NULL,
                             FOREIGN KEY (user_id) REFERENCES users(id),
                             FOREIGN KEY (role_id) REFERENCES roles(id)
);

INSERT INTO users (email, first_name, last_name, password) VALUES
                                                               ('customer@example.com', 'Customer', 'User', '$2a$10$OtAaqYntWR2UF0gUatPjeOcNrfqu43TeiazwKWsF5BXmomhf782A6'),
                                                               ('manager@example.com', 'Manager', 'User', '$2a$10$3NQB4Ktd9I9lJJQjSKaOm.wu6qfIM8KQiclfOTqCUaQiscPw0nK7i');

INSERT INTO roles (type) VALUES
                             ('MANAGER'),
                             ('CUSTOMER');

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'customer@example.com' AND r.type = 'CUSTOMER';

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'manager@example.com' AND r.type = 'MANAGER';
