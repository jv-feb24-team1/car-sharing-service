DELETE FROM rentals;
DELETE FROM cars;
DELETE FROM users_roles;
DELETE FROM roles;
DELETE FROM users;

INSERT INTO users (id, email, first_name, last_name, password)
VALUES (1, 'user1@example.com', 'John', 'Doe', 'password1'),
       (2, 'user2@example.com', 'Jane', 'Smith', 'password2'),
       (3, 'user3@example.com', 'Alice', 'Johnson', 'password3'),
       (4, 'user4@example.com', 'Bob', 'Dork', 'password4');

INSERT INTO roles (id, type)
VALUES (1, 'MANAGER'),
       (2, 'CUSTOMER');

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 2),
       (2, 2),
       (3, 2),
       (4, 1);

INSERT INTO cars (id, model, brand, type, inventory, daily_fee, is_deleted)
VALUES (1, 'Model X', 'Tesla', 'SUV', 2, 24.99, false),
       (2, 'Model Y', 'Tesla', 'SEDAN', 2, 14.99, false),
       (3, 'Model S', 'Tesla', 'SEDAN', 1, 19.99, false);

INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, active)
VALUES (1, '2024-05-26', '2024-05-31', null, 1, 3, true),
       (2, '2024-05-28', '2024-06-01', '2024-06-01', 1, 1, false),
       (3, '2024-05-30', '2024-06-02', '2024-06-02', 2, 2, false),
       (4, '2024-06-02', '2024-06-08', null, 1, 1, true),
       (5, '2024-06-04', '2024-06-11', null, 1, 2, true),
       (6, '2024-06-04', '2024-06-09', null, 2, 2, true);
