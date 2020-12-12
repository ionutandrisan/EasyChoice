create or replace PACKAGE licenta_person_pkg AS
    PROCEDURE insert_person_procedure (
        person_firstname IN licenta_person.first_name%TYPE,
        person_lastname IN licenta_person.last_name%TYPE,
        person_email IN licenta_person.email_id%TYPE,
        person_password IN licenta_person.password%TYPE,
        person_photopath IN licenta_person.photo%TYPE,
        person_birthdate IN licenta_person.first_name%TYPE,
        person_phonenumber IN licenta_person.phone_number%TYPE);

    PROCEDURE get_person_by_email (
        person_email IN licenta_person.email_id%TYPE,
        person_information_cursor OUT SYS_REFCURSOR);

    PROCEDURE get_email_and_password (
        person_email IN licenta_person.email_id%TYPE,
        person_information_cursor OUT SYS_REFCURSOR);

    PROCEDURE set_user_photo (
        in_person_photo IN licenta_person.photo%TYPE,
        in_person_id IN licenta_person.person_id%TYPE);
END licenta_person_pkg;


create or replace PACKAGE BODY licenta_person_pkg IS
    PROCEDURE insert_person_procedure (
        person_firstname IN licenta_person.first_name%TYPE,
        person_lastname IN licenta_person.last_name%TYPE,
        person_email IN licenta_person.email_id%TYPE,
        person_password IN licenta_person.password%TYPE,
        person_photopath IN licenta_person.photo%TYPE,
        person_birthdate IN licenta_person.first_name%TYPE,
        person_phonenumber IN licenta_person.phone_number%TYPE) 
    IS
    BEGIN
        INSERT INTO licenta_person VALUES (null, person_firstname, person_lastname, person_email, person_password, 
            person_photopath, TO_DATE(person_birthdate, 'yyyy-MM-dd'), person_phonenumber);
        COMMIT;
    END insert_person_procedure;

    PROCEDURE get_person_by_email (
        person_email IN licenta_person.email_id%TYPE,
        person_information_cursor OUT SYS_REFCURSOR)
    IS 
    BEGIN 
        OPEN person_information_cursor FOR
            SELECT * FROM licenta_person WHERE email_id = person_email;
    END get_person_by_email;

    PROCEDURE get_email_and_password (
        person_email IN licenta_person.email_id%TYPE,
        person_information_cursor OUT SYS_REFCURSOR)
    IS
    BEGIN 
        OPEN person_information_cursor FOR
            SELECT email_id, password FROM licenta_person WHERE email_id = person_email;
    END get_email_and_password;

    PROCEDURE set_user_photo (
        in_person_photo IN licenta_person.photo%TYPE,
        in_person_id IN licenta_person.person_id%TYPE)
    IS
    BEGIN
        UPDATE licenta_person 
        SET photo = in_person_photo
        WHERE person_id = in_person_id;
    END set_user_photo;
END licenta_person_pkg;
