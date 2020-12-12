CREATE TABLE users (
    person_id      NUMBER(5) NOT NULL,
    first_name     VARCHAR2(20),
    last_name      VARCHAR2(20),
    email_id       VARCHAR2(100) NOT NULL,
    password       VARCHAR2(100) NOT NULL,
    birth_date     DATE,
    phone_number   VARCHAR2(15),
    photo          VARCHAR2(20)
);

ALTER TABLE users
    ADD CONSTRAINT email_person_ck CHECK ( REGEXP_LIKE ( email,
                                                         '[a-z0-9._%-]+@[a-z0-9._%-]+\.[a-z]{2,4}' ) );

ALTER TABLE users ADD CONSTRAINT licenta_person_pk PRIMARY KEY ( person_id );

ALTER TABLE users ADD CONSTRAINT person_email_id_un UNIQUE ( email_id );

CREATE SEQUENCE users_person_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER users_person_id_trg BEFORE
    INSERT ON users
    FOR EACH ROW
    WHEN ( new.person_id IS NULL )
BEGIN
    :new.person_id := users_person_id_seq.nextval;
END;
/

CREATE TABLE house (
    house_id         NUMBER(7) NOT NULL,
    cost_per_night   NUMBER(8, 2),
    person_id        NUMBER(5) NOT NULL,
    location_id      NUMBER(7),
    main_photo       VARCHAR2(20),
    description      VARCHAR2(1000),
    house_name       VARCHAR2(40)
);

ALTER TABLE house ADD CONSTRAINT licenta_house_pk PRIMARY KEY ( house_id );

ALTER TABLE house
    ADD CONSTRAINT house_location_fk FOREIGN KEY ( location_id )
        REFERENCES location ( location_id );

ALTER TABLE house
    ADD CONSTRAINT house_person_fk FOREIGN KEY ( person_id )
        REFERENCES users ( person_id );

CREATE SEQUENCE house_house_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER house_house_id_trg BEFORE
    INSERT ON house
    FOR EACH ROW
    WHEN ( new.house_id IS NULL )
BEGIN
    :new.house_id := house_house_id_seq.nextval;
END;
/

CREATE TABLE booking (
    booking_id              NUMBER NOT NULL,
    booking_starting_date   DATE,
    booking_ending_date     DATE,
    booking_status          CHAR(1),
    booking_house_id        NUMBER(7) NOT NULL,
    booking_person_id       NUMBER(5) NOT NULL
);

ALTER TABLE booking ADD CONSTRAINT licenta_booking_pk PRIMARY KEY ( booking_id );

ALTER TABLE booking
    ADD CONSTRAINT booking_house_fk FOREIGN KEY ( booking_house_id )
        REFERENCES house ( house_id );

ALTER TABLE booking
    ADD CONSTRAINT booking_person_fk FOREIGN KEY ( booking_person_id )
        REFERENCES users ( person_id );

CREATE SEQUENCE booking_booking_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER booking_booking_id_trg BEFORE
    INSERT ON booking
    FOR EACH ROW
    WHEN ( new.booking_id IS NULL )
BEGIN
    :new.booking_id := booking_booking_id_seq.nextval;
END;
/

CREATE TABLE reviews (
    review_id          NUMBER NOT NULL,
    review_comments    VARCHAR2(500),
    review_date        DATE,
    person_person_id   NUMBER(5) NOT NULL,
    house_house_id     NUMBER(7) NOT NULL,
    rating             NUMBER(2)
);

ALTER TABLE reviews ADD CONSTRAINT licenta_review_pk PRIMARY KEY ( review_id );

ALTER TABLE reviews
    ADD CONSTRAINT review_house_fk FOREIGN KEY ( house_house_id )
        REFERENCES house ( house_id );

ALTER TABLE reviews
    ADD CONSTRAINT review_person_fk FOREIGN KEY ( person_person_id )
        REFERENCES users ( person_id );
/

CREATE TABLE location (
    location_id   NUMBER(5) NOT NULL,
    country       VARCHAR2(40),
    city          VARCHAR2(40),
    street_name   VARCHAR2(40)
);

ALTER TABLE location ADD CONSTRAINT licenta_location_pk PRIMARY KEY ( location_id );

CREATE SEQUENCE location_location_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER location_location_id_trg BEFORE
    INSERT ON location
    FOR EACH ROW
    WHEN ( new.location_id IS NULL )
BEGIN
    :new.location_id := location_location_id_seq.nextval;
END;
/

CREATE TABLE house_facilities (
    house_id           NUMBER(7) NOT NULL,
    facility_id   NUMBER NOT NULL
);

ALTER TABLE house_facilities ADD CONSTRAINT house_facilitiesv1_pk PRIMARY KEY ( licenta_house_house_id,
                                                                                licenta_facilities_facility_id );


ALTER TABLE house_facilities
    ADD CONSTRAINT house_facilities_fk FOREIGN KEY ( facility_id )
        REFERENCES facilities ( facility_id );


ALTER TABLE house_facilities
    ADD CONSTRAINT house_house_fk FOREIGN KEY ( house_id )
        REFERENCES house ( house_id );
/

CREATE TABLE facilities (
    facility_id     NUMBER NOT NULL,
    facility_name   VARCHAR2(50)
);

ALTER TABLE facilities ADD CONSTRAINT licenta_facilities_pk PRIMARY KEY ( facility_id );

CREATE SEQUENCE facilities_facility_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER facilities_facility_id_trg BEFORE
    INSERT ON facilities
    FOR EACH ROW
    WHEN ( new.facility_id IS NULL )
BEGIN
    :new.facility_id := facilities_facility_id_seq.nextval;
END;
/


