create or replace PACKAGE licenta_location_pkg AS
    PROCEDURE insert_location_procedure (
        location_country IN licenta_location.country%TYPE,
        location_state IN licenta_location.state%TYPE,
        location_city IN licenta_location.city%TYPE,
        location_zipcode IN licenta_location.zipcode%TYPE);
    PROCEDURE verify_location (
        location_country IN licenta_location.country%TYPE,
        location_state IN licenta_location.state%TYPE,
        location_city IN licenta_location.city%TYPE,
        location_zipcode IN licenta_location.zipcode%TYPE,
        location_id OUT licenta_location.location_id%TYPE
    );
END licenta_location_pkg;


create or replace PACKAGE BODY licenta_location_pkg IS
    PROCEDURE insert_location_procedure (
        location_country IN licenta_location.country%TYPE,
        location_state IN licenta_location.state%TYPE,
        location_city IN licenta_location.city%TYPE,
        location_zipcode IN licenta_location.zipcode%TYPE)
	IS
	BEGIN
		INSERT INTO licenta_location values (null, location_country, location_state, location_city, location_zipcode);
		COMMIT;
	END insert_location_procedure;
    
    PROCEDURE verify_location (
        location_country IN licenta_location.country%TYPE,
        location_state IN licenta_location.state%TYPE,
        location_city IN licenta_location.city%TYPE,
        location_zipcode IN licenta_location.zipcode%TYPE,
        location_id OUT licenta_location.location_id%TYPE) 
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
    END verify_location;
    
END licenta_location_pkg;