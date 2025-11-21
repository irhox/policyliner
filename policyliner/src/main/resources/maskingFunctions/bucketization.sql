CREATE OR REPLACE FUNCTION bucketize_age(dateOfBirth timestamp, bucketSize interval)
    RETURNS text
    LANGUAGE plpgsql
AS $$
DECLARE
    age_in_years numeric;
    bucket_years numeric;
    bucket_start integer;
    bucket_end integer;
BEGIN
    age_in_years := EXTRACT(YEAR FROM AGE(CURRENT_TIMESTAMP, dateOfBirth));
    bucket_years := EXTRACT(YEAR FROM bucketSize);
    bucket_start := FLOOR(age_in_years / bucket_years) * bucket_years;
    bucket_end := bucket_start + bucket_years - 1;

    -- Return the bucket range as text
    RETURN bucket_start || ' - ' || bucket_end;
END;
$$;