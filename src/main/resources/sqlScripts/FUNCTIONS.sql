CREATE FUNCTION buyer_seller_validate()
    RETURNS TRIGGER
AS $$
BEGIN
    IF length(NEW.password) < 10 OR NEW.password IS NULL THEN
        RAISE EXCEPTION 'password cannot be less than 10 characters';
    END IF;
    IF NEW.NAME IS NULL THEN
        RAISE EXCEPTION 'Name cannot be NULL';
    END IF;
    RETURN NEW;
END;
$$
    LANGUAGE plpgsql;