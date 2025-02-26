CREATE TABLE practitioners
(
    id             SERIAL PRIMARY KEY,
    first_name     VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE medical_services
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE practitioner_medical_services
(
    practitioner_id    INT REFERENCES practitioners (id) ON DELETE CASCADE,
    medical_service_id INT REFERENCES medical_services (id) ON DELETE CASCADE,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (practitioner_id, medical_service_id)
);

CREATE TABLE patients
(
    id         SERIAL PRIMARY KEY,
    first_name     VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE time_slots
(
    id              SERIAL PRIMARY KEY,
    practitioner_id INT REFERENCES practitioners (id) ON DELETE CASCADE,
    start_time      TIMESTAMP NOT NULL,
    end_time        TIMESTAMP NOT NULL,
    is_available    BOOLEAN   DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE appointments
(
    id                 SERIAL PRIMARY KEY,
    patient_id         INT REFERENCES patients (id) ON DELETE SET NULL,
    time_slot_id       INT REFERENCES time_slots (id) ON DELETE CASCADE,
    medical_service_id INT REFERENCES medical_services (id) ON DELETE CASCADE,
    booked_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status             VARCHAR(20) CHECK (status IN ('BOOKED', 'CANCELED')),
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE waiting_list
(
    id              SERIAL PRIMARY KEY,
    patient_id      INT REFERENCES patients (id) ON DELETE CASCADE,
    practitioner_id INT REFERENCES practitioners (id) ON DELETE CASCADE,
    added_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);