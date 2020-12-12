create or replace PACKAGE licenta_house_pkg AS
    PROCEDURE insert_house_procedure (
        in_country IN licenta_location.country%TYPE,
        in_city IN licenta_location.city%TYPE,
        in_street_name IN licenta_location.street_name%TYPE,
        in_cost_per_night IN licenta_house.cost_per_night%TYPE,
        main_img IN licenta_house.main_image%TYPE,
        in_email IN licenta_person.email_id%TYPE,
        in_house_name IN licenta_house.house_name%TYPE,
        in_house_description IN licenta_house.description%TYPE,
		out_house_id OUT licenta_house.house_id%TYPE
        );

    PROCEDURE get_cheapest_houses (
        house_info_cursor OUT SYS_REFCURSOR);

    PROCEDURE get_most_appreciated_houses (
        house_info_cursor OUT SYS_REFCURSOR);

    PROCEDURE get_houses_by_location (
        in_city IN licenta_location.city%TYPE,
        house_info_cursor OUT SYS_REFCURSOR
        );
        
    PROCEDURE get_a_house (
        in_house_id IN licenta_house.house_id%TYPE,
        house_info_cursor OUT SYS_REFCURSOR
        );

END licenta_house_pkg;

create or replace PACKAGE BODY licenta_house_pkg IS
    PROCEDURE insert_house_procedure (
        in_country IN licenta_location.country%TYPE,
        in_city IN licenta_location.city%TYPE,
        in_street_name IN licenta_location.street_name%TYPE,
        in_cost_per_night IN licenta_house.cost_per_night%TYPE,
        main_img IN licenta_house.main_image%TYPE,
        in_email IN licenta_person.email_id%TYPE,
        in_house_name IN licenta_house.house_name%TYPE,
        in_house_description IN licenta_house.description%TYPE,
		out_house_id OUT licenta_house.house_id%TYPE
        )
    IS
        v_id licenta_person.person_id%TYPE;
        v_location_id licenta_location.location_id%TYPE;
    BEGIN
        SELECT person_id INTO v_id FROM licenta_person WHERE in_email = email_id;
        INSERT INTO licenta_location (country, city, street_name) 
            VALUES (in_country, in_city, in_street_name)
            RETURNING location_id INTO v_location_id;
        INSERT INTO licenta_house (cost_per_night, person_id, location_id, main_image, description, house_name)
            VALUES (in_cost_per_night, v_id, v_location_id, main_img, in_house_description, in_house_name)
            RETURNING house_id INTO out_house_id;
        COMMIT;
    END insert_house_procedure;

    PROCEDURE get_cheapest_houses (
        house_info_cursor OUT SYS_REFCURSOR)
    IS
    BEGIN 
        OPEN house_info_cursor FOR
            SELECT * FROM (
            SELECT h.house_id, h.house_name, h.cost_per_night, h.main_image, l.country, avg(r.rating) as rating_avg, 
                count(r.review_id) as review_number
                FROM licenta_house h JOIN licenta_location l ON h.location_id = l.location_id
                JOIN licenta_review r ON h.house_id = r.house_id
                GROUP BY h.house_id, h.house_name, h.cost_per_night, h.main_image, l.country
                ORDER BY h.cost_per_night ASC)
                WHERE rownum <= 4;
    END get_cheapest_houses;

    PROCEDURE get_most_appreciated_houses (
        house_info_cursor OUT SYS_REFCURSOR)
    IS
    BEGIN 
        OPEN house_info_cursor FOR
            SELECT * FROM (
            SELECT h.house_id, h.house_name, h.cost_per_night, h.main_image, l.country, avg(r.rating) as rating_avg, 
                count(r.review_id) as review_number
                FROM licenta_house h JOIN licenta_location l ON h.location_id = l.location_id
                JOIN licenta_review r ON h.house_id = r.house_id
                GROUP BY h.house_id, h.house_name, h.cost_per_night, h.main_image, l.country
                ORDER BY rating_avg DESC)
                WHERE rownum <= 4;    
    END get_most_appreciated_houses;

    PROCEDURE get_houses_by_location (
        in_city IN licenta_location.city%TYPE,
        house_info_cursor OUT SYS_REFCURSOR
        )
    IS
    BEGIN
        OPEN house_info_cursor FOR 
            SELECT h.house_id, h.house_name,  h.description, h.cost_per_night, h.main_image, l.city, l.country, 
                avg(r.rating) as rating_avg, count(r.review_id) as review_number
                FROM licenta_house h JOIN licenta_location l ON h.location_id = l.location_id
                JOIN licenta_review r ON r.house_id = h.house_id
                WHERE in_city = l.city or in_city = h.house_name
                GROUP BY h.house_id, h.house_name,  h.description, h.cost_per_night, h.main_image, l.city, l.country;
    END get_houses_by_location;
    
    PROCEDURE get_a_house (
        in_house_id IN licenta_house.house_id%TYPE,
        house_info_cursor OUT SYS_REFCURSOR
        )
    IS
    BEGIN
        OPEN house_info_cursor FOR 
            SELECT h.house_id, h.house_name,  h.description, h.cost_per_night, h.main_image, l.city, l.country, l.street_name,
                    u.last_name, u.photo, avg(r.rating) as rating_avg, count(r.review_id) as review_number
                FROM licenta_house h JOIN licenta_location l ON h.location_id = l.location_id
                JOIN licenta_review r ON r.house_id = h.house_id
                JOIN licenta_person u ON u.person_id = h.person_id
                WHERE in_house_id = h.house_id
                GROUP BY h.house_id, h.house_name,  h.description, h.cost_per_night, h.main_image, l.city, l.country, 
                    l.street_name, u.last_name, u.photo;
    END get_a_house;

END licenta_house_pkg;