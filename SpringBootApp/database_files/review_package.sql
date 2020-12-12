CREATE OR REPLACE PACKAGE licenta_review_pkg AS

     PROCEDURE insert_review (
        in_review_comment IN licenta_review.review_comments%TYPE,
        in_review_rating IN licenta_review.rating%TYPE,
        in_email IN licenta_person.email_id%TYPE,
        in_house_id IN licenta_house.house_id%TYPE);
        
    PROCEDURE get_details_for_email_send (
        in_booking_id IN licenta_booking.booking_id%TYPE,
        out_first_name OUT licenta_person.first_name%TYPE,
        out_last_name OUT licenta_person.last_name%TYPE,
        out_house_name OUT licenta_house.house_name%TYPE,
        out_owner_email OUT licenta_person.email_id%TYPE
    );
    
END licenta_review_pkg;
/

CREATE OR REPLACE PACKAGE BODY licenta_review_pkg IS

   PROCEDURE insert_review (
        in_review_comment IN licenta_review.review_comments%TYPE,
        in_review_rating IN licenta_review.rating%TYPE,
        in_email IN licenta_person.email_id%TYPE,
        in_house_id IN licenta_house.house_id%TYPE) 
    IS
        v_person_id licenta_person.person_id%TYPE;
    BEGIN
        SELECT person_id INTO v_person_id FROM licenta_person WHERE in_email = email_id;

        INSERT INTO licenta_review (REVIEW_COMMENTS, RATING, REVIEW_DATE, PERSON_ID, HOUSE_ID)
            VALUES (in_review_comment, in_review_rating, sysdate, v_person_id, in_house_id);
        COMMIT;    
    END insert_review;
    
    PROCEDURE get_details_for_email_send (
        in_booking_id IN licenta_booking.booking_id%TYPE,
        out_first_name OUT licenta_person.first_name%TYPE,
        out_last_name OUT licenta_person.last_name%TYPE,
        out_house_name OUT licenta_house.house_name%TYPE,
        out_owner_email OUT licenta_person.email_id%TYPE
    )
    IS
    BEGIN
        SELECT p.first_name, p.last_name INTO out_first_name, out_last_name
            FROM licenta_booking b JOIN licenta_person p ON b.person_id = p.person_id
            WHERE b.booking_id = in_booking_id;
        SELECT h.house_name INTO out_house_name
            FROM licenta_booking b JOIN licenta_house h ON b.house_id = h.house_id
            WHERE b.booking_id = in_booking_id;
        SELECT p.email_id INTO out_owner_email
            FROM licenta_booking b JOIN licenta_house h ON b.house_id = h.house_id
            JOIN licenta_person p ON h.person_id = p.person_id
            WHERE b.booking_id = in_booking_id;
    END get_details_for_email_send;

END licenta_review_pkg;
/