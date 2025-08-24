CREATE OR REPLACE FUNCTION categorize_labname(labname TEXT)
    RETURNS TEXT AS $$
BEGIN
    RETURN split_part(labname, ':', 1); -- choose only the first part of the labname, i.e. category
END;
$$ LANGUAGE plpgsql;