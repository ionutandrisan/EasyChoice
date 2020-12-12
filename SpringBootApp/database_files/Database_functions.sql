CREATE OR REPLACE FUNCTION usf_email_exists (email IN licenta_person.email_id%TYPE)
RETURN varchar2 IS 
    v_exists number;
BEGIN 
    SELECT count(*) INTO v_exists FROM licenta_person where email_id = email; 
    if v_exists = 0 THEN
        RETURN 'false';
    ELSE
        RETURN 'true'; 
    END IF;
END;
/

CREATE OR REPLACE FUNCTION usf_user_can_login (email IN licenta_person.email_id%TYPE, pass IN licenta_person.password%TYPE)
RETURN varchar2 IS 
    v_exists number;
BEGIN 
    SELECT count(*) INTO v_exists FROM licenta_person WHERE email_id = email AND pass = password; 
    if v_exists = 0 THEN
        RETURN 'false';
    ELSE
        RETURN 'true'; 
    END IF;
END;
/

CREATE OR REPLACE FUNCTION verify_location_fct (
        location_country IN licenta_location.country%TYPE,
        location_state IN licenta_location.state%TYPE,
        location_city IN licenta_location.city%TYPE,
        location_zipcode IN licenta_location.zipcode%TYPE) 
        RETURN licenta_location.location_id%TYPE
    IS
        v_loc_country licenta_location.country%TYPE;
        v_loc_state licenta_location.state%TYPE;
        v_loc_city licenta_location.city%TYPE;
        v_loc_zipcode licenta_location.zipcode%TYPE;
        v_loc_id licenta_location.location_id%TYPE;
    BEGIN
        SELECT country, state, city, location_id INTO v_loc_country, v_loc_state, v_loc_city, v_loc_id
            FROM licenta_location WHERE zipcode = location_zipcode;
        IF v_loc_country = location_country and v_loc_state = location_state and v_loc_city = location_city THEN
            RETURN v_loc_id;
        ELSE
            RETURN -1;
        END IF;
END verify_location_fct;
/

CREATE OR REPLACE FUNCTION usf_house_exists (in_house_id IN licenta_house.house_id%TYPE)
RETURN varchar2 IS 
    v_exists number;
BEGIN 
    SELECT count(*) INTO v_exists FROM licenta_house where house_id = in_house_id; 
    if v_exists = 0 THEN
        RETURN 'false';
    ELSE
        RETURN 'true'; 
    END IF;
END;
/

create or replace FUNCTION verify_booking_availability (
    in_start_date IN licenta_booking.booking_starting_date%TYPE,
    in_end_date IN licenta_booking.booking_ending_date%TYPE,
    in_house_id IN licenta_house.house_id%TYPE
    )
    RETURN varchar2 IS
        v_nr_booking number;
        v_nr_of_days number;
    BEGIN
        SELECT count(*) INTO v_nr_booking FROM licenta_booking 
            WHERE in_start_date BETWEEN booking_starting_date AND booking_ending_date
            OR in_end_date BETWEEN booking_starting_date AND booking_ending_date
            AND in_house_id = house_id;
        IF v_nr_booking > 0 THEN RETURN 'false';
        END IF;
        SELECT count(*) INTO v_nr_booking FROM licenta_booking 
            WHERE in_start_date < booking_starting_date
            AND in_end_date > booking_ending_date
            AND in_house_id = house_id;
        IF v_nr_booking > 0 THEN RETURN 'false';
        END IF;
        SELECT  in_end_date - in_start_date into v_nr_of_days from dual;
        RETURN 'true ' || v_nr_of_days;
END verify_booking_availability;
/

CREATE OR REPLACE FUNCTION get_all_facilities
    RETURN sys_refcursor
IS
    fac_cursor sys_refcursor;
BEGIN
    OPEN fac_cursor FOR
        SELECT facility_name FROM licenta_facilities;
    RETURN fac_cursor;
END get_all_facilities;
/
    
CREATE OR REPLACE FUNCTION get_house_reviews(in_house_id licenta_house.house_id%TYPE)
    RETURN SYS_REFCURSOR
IS
    c_reviews SYS_REFCURSOR;
BEGIN
    OPEN c_reviews FOR
        SELECT r.review_date, r.review_comments, r.rating, p.last_name, p.photo, p.person_id FROM licenta_review r
            JOIN licenta_person p ON r.person_id = p.person_id
            WHERE r.house_id = in_house_id;    
    RETURN c_reviews;
END get_house_reviews;
/

CREATE OR REPLACE FUNCTION get_house_facilities (in_house_id licenta_house.house_id%TYPE)
    RETURN sys_refcursor
IS
    fac_cursor sys_refcursor;
BEGIN
    OPEN fac_cursor FOR
        SELECT facility_name FROM licenta_facilities 
            JOIN licenta_house_facilities ON facility_id = licenta_facilities_facility_id
            WHERE licenta_house_house_id = in_house_id;
    RETURN fac_cursor;
END get_house_facilities;
/

CREATE OR REPLACE FUNCTION book_house(
    check_in_date licenta_booking.booking_starting_date%TYPE,
    check_out_date licenta_booking.booking_ending_date%TYPE,
    in_house_id licenta_booking.house_id%TYPE,
    in_email licenta_person.email_id%TYPE
    )
    RETURN varchar2
IS
    v_is_available varchar2(6);
    v_person_id licenta_person.person_id%TYPE;
    v_booking_id licenta_booking.booking_id%TYPE;
BEGIN
    v_is_available := verify_booking_availability(check_in_date, check_out_date);
    if v_is_available <> 'false' THEN
        SELECT person_id INTO v_person_id FROM licenta_person WHERE in_email = email_id;
        INSERT INTO licenta_booking (booking_starting_date, booking_ending_date, house_id, person_id)
            VALUES (check_in_date, check_out_date, in_house_id, v_person_id)
            RETURNING booking_id INTO v_booking_id;
        RETURN v_booking_id;
    ELSE
        RETURN 'false';
    END IF;
END book_house;
/

CREATE OR REPLACE FUNCTION get_details_for_email_send (
        in_house_id licenta_house.house_id%TYPE,
        in_email_id licenta_person.email_id%TYPE
    )
    RETURN sys_refcursor
IS
    house_cursor sys_refcursor;
BEGIN
    OPEN house_cursor FOR
        SELECT c.first_name, c.last_name, h.house_name, o.email
                FROM licenta_house h JOIN licenta_person o ON h.house_id = in_house_id
                JOIN licenta_person c ON in_email_id = c.email_id
                WHERE rownum <= 3;
    RETURN house_cursor;
END get_three_houses;
/



