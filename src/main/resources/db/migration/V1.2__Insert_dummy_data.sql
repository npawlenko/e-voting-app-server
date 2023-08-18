INSERT INTO roles (name) VALUES
('USER'),
('ADMIN');

INSERT INTO users (first_name, last_name, email, password, role_id) VALUES
('John', 'Doe', 'john@example.com', '$2a$10$KxSOssT7XUg.micE/hOl1.s940DQalPORQ43p6nbzyqVkMPDitjLi', 1),
('Jane', 'Smith', 'jane@example.com', '$2a$10$BORQy.KMbhYAmu88qgHYrOXjMG81vhhhpv55GD15/UFyS1xa8u9We', 2);

INSERT INTO polls (question, expires_at, creator_id) VALUES
('Favorite Color?', '2023-12-31 23:59:59', 1),
('Best Programming Language?', '2023-12-31 23:59:59', 2);

INSERT INTO poll_answers (answer, poll_id) VALUES
('Red', 1),
('Blue', 1),
('Java', 2),
('Python', 2);

INSERT INTO votes (answer_id, poll_id, voter_id) VALUES
(1, 1, 2),
(3, 2, 1),
(3, 2, 2);
