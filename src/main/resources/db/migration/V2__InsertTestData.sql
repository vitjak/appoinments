INSERT INTO medical_services (name)
VALUES ('Kardiologija'),
       ('Ortopedija'),
       ('Dermatologija'),
       ('Nevrologija'),
       ('Pediatrija'),
       ('Oftalmologija'),
       ('Pulmologija'),
       ('Urologija');

INSERT INTO practitioners (first_name, last_name, specialization)
VALUES ('Marjan', 'Novak', 'Kardiologija'),
       ('Ana', 'Škufca', 'Ortopedija'),
       ('David', 'Zupan', 'Dermatologija'),
       ('Bojana', 'Horvat', 'Nevrologija'),
       ('Gregor', 'Dejak', 'Pediatrija');

INSERT INTO practitioner_medical_services (practitioner_id, medical_service_id)
VALUES (1, 1),
       (1, 7),
       (2, 2),
       (2, 8),
       (3, 3),
       (3, 6),
       (4, 4),
       (4, 1),
       (5, 5),
       (5, 3);

INSERT INTO patients (first_name, last_name, email)
VALUES ('Maja', 'Vidmar', 'maja.vidmar@gmail.com'),
       ('Peter', 'Potočnik', 'peter.potocnik@gmail.com'),
       ('Miha', 'Kralj', 'miha.kralj@gmail.com'),
       ('Marko', 'Petek', 'marko.petek@gmail.com'),
       ('Barbara', 'Rozman', 'barbara.rozman@gmail.com');

INSERT INTO time_slots (practitioner_id, start_time, end_time, is_available)
VALUES

(1, '2025-02-25 09:00:00', '2025-02-25 09:30:00', true),
(1, '2025-02-25 10:00:00', '2025-02-25 10:30:00', false),
(1, '2025-02-25 11:00:00', '2025-02-25 11:30:00', true),
(1, '2025-02-25 12:00:00', '2025-02-25 12:30:00', true),
(1, '2025-02-25 13:00:00', '2025-02-25 13:30:00', false),

(2, '2025-02-25 09:00:00', '2025-02-25 09:30:00', true),
(2, '2025-02-25 10:00:00', '2025-02-25 10:30:00', false),
(2, '2025-02-25 11:00:00', '2025-02-25 11:30:00', true),
(2, '2025-02-25 12:00:00', '2025-02-25 12:30:00', true),
(2, '2025-02-25 13:00:00', '2025-02-25 13:30:00', true),

(3, '2025-02-25 09:00:00', '2025-02-25 09:30:00', true),
(3, '2025-02-25 10:00:00', '2025-02-25 10:30:00', false),
(3, '2025-02-25 11:00:00', '2025-02-25 11:30:00', true),
(3, '2025-02-25 12:00:00', '2025-02-25 12:30:00', false),
(3, '2025-02-25 13:00:00', '2025-02-25 13:30:00', true),

(4, '2025-02-25 09:00:00', '2025-02-25 09:30:00', true),
(4, '2025-02-25 10:00:00', '2025-02-25 10:30:00', true),
(4, '2025-02-25 11:00:00', '2025-02-25 11:30:00', true),
(4, '2025-02-25 12:00:00', '2025-02-25 12:30:00', true),
(4, '2025-02-25 13:00:00', '2025-02-25 13:30:00', true),

(5, '2025-02-25 09:00:00', '2025-02-25 09:30:00', true),
(5, '2025-02-25 10:00:00', '2025-02-25 10:30:00', true),
(5, '2025-02-25 11:00:00', '2025-02-25 11:30:00', true),
(5, '2025-02-25 12:00:00', '2025-02-25 12:30:00', true),
(5, '2025-02-25 13:00:00', '2025-02-25 13:30:00', true);


INSERT INTO appointments (patient_id, time_slot_id, medical_service_id, booked_at, status)
VALUES (1, 2, 1, '2025-02-21 09:15:00', 'BOOKED'),
       (2, 5, 2, '2025-02-21 10:45:00', 'BOOKED'),
       (3, 7, 3, '2025-02-22 11:30:00', 'BOOKED'),
       (4, 12, 4, '2025-02-23 12:20:00', 'BOOKED'),
       (5, 14, 5, '2025-02-24 13:10:00', 'BOOKED');

INSERT INTO waiting_list (patient_id, practitioner_id, added_at)
VALUES (1, 2, '2025-02-21 09:35:10'),
       (3, 4, '2025-02-22 10:28:42'),
       (2, 1, '2025-02-23 13:15:00'),
       (5, 3, '2025-02-24 14:45:30');