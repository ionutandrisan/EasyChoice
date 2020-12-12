CREATE OR REPLACE PACKAGE licenta_booking_pkg AS

    PROCEDURE delete_reservation (
        res_id IN licenta_booking.booking_id%TYPE);
        
    PROCEDURE get_details_for_email_send (
        in_booking_id IN licenta_booking.booking_id%TYPE,
        customer_first_name OUT licenta_person.first_name%TYPE,
        customer_last_name OUT licenta_person.last_name%TYPE,
        out_house_name OUT licenta_house.house_name%TYPE,
        out_house_mail OUT licenta_person.email_id%TYPE
    );
        
END licenta_booking_pkg;

CREATE OR REPLACE PACKAGE BODY licenta_booking_pkg IS

    PROCEDURE delete_reservation (
        res_id IN licenta_booking.booking_id%TYPE
        )
    IS
    BEGIN
        DELETE FROM licenta_booking WHERE res_id = booking_id;
    END delete_reservation;

END licenta_booking_pkg;