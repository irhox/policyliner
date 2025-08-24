CREATE OR REPLACE FUNCTION randomized_response_patientrace(race TEXT, epsilon DOUBLE PRECISION DEFAULT 1.0)
RETURNS TEXT AS $$
DECLARE
    truth_probability DOUBLE PRECISION;
    rand FLOAT;
    options TEXT[];
    result TEXT;
BEGIN
    options := ARRAY['White', 'Asian', 'African American', 'Unknown'];
    rand := RANDOM();
    truth_probability := exp(epsilon) / (exp(epsilon) + array_length(options, 1) - 1);

    if rand < truth_probability then
        return race;
    else
        result := options[floor(rand * array_length(options, 1)) + 1];
        return result;
    end if;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION randomized_response_patientgender(gender TEXT, epsilon DOUBLE PRECISION DEFAULT 1.0)
    RETURNS TEXT AS $$
DECLARE
    truth_probability DOUBLE PRECISION;
    rand FLOAT;
    options TEXT[];
    result TEXT;
BEGIN
    options := ARRAY['Male', 'Female', 'Unknown'];
    rand := RANDOM();
    truth_probability := exp(epsilon) / (exp(epsilon) + array_length(options, 1) - 1);

    if rand < truth_probability then
        return gender;
    else
        result := options[floor(rand * array_length(options, 1)) + 1];
        return result;
    end if;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION randomized_response_patientmaritalstatus(status TEXT, epsilon DOUBLE PRECISION DEFAULT 1.0)
    RETURNS TEXT AS $$
DECLARE
    truth_probability DOUBLE PRECISION;
    rand FLOAT;
    options TEXT[];
    result TEXT;
BEGIN
    options := ARRAY['Single', 'Married', 'Divorced', 'Separated', 'Widowed', 'Unknown'];
    rand := RANDOM();
    truth_probability := exp(epsilon) / (exp(epsilon) + array_length(options, 1) - 1);

    if rand < truth_probability then
        return status;
    else
        result := options[floor(rand * array_length(options, 1)) + 1];
        return result;
    end if;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION randomized_response_patientlanguage(language TEXT, epsilon DOUBLE PRECISION DEFAULT 1.0)
    RETURNS TEXT AS $$
DECLARE
    truth_probability DOUBLE PRECISION;
    rand FLOAT;
    options TEXT[];
    result TEXT;
BEGIN
    options := ARRAY['English', 'Spanish', 'Icelandic', 'Unknown'];
    rand := RANDOM();
    truth_probability := exp(epsilon) / (exp(epsilon) + array_length(options, 1) - 1);

    if rand < truth_probability then
        return language;
    else
        result := options[floor(rand * array_length(options, 1)) + 1];
        return result;
    end if;
END;
$$ LANGUAGE plpgsql;