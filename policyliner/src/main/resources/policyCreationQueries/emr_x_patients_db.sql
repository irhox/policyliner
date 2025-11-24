
-- Disclosure Policies used in the Experiments Chapter of the master thesis
-- 1. Public Health Researcher Policy
CREATE VIEW patients_admissions_diagnosis_policy AS
    SELECT
        bucketize_age(patientdateofbirth, '5 years') AS patientdateofbirth,
        randomized_response_patientrace(patientrace) AS patientrace,
        randomized_response_patientmaritalstatus(patientmartialstatus) AS patientmartialstatus,
        randomized_response_patientgender(patientgender) AS patientgender,
        bucketize(patientpopulationpercentagebelowpoverty, 5) AS patientpopulationpercentagebelowpoverty,
        generalize_primarydiagnosiscode(primarydiagnosiscode) AS primarydiagnosiscode,
        generalize_date(to_date(to_char(admissionstartdate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'MONTH') AS to_date_to_char_admissionstartdate,
        generalize_date(to_date(to_char(admissionenddate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'MONTH') AS to_date_to_char_admissionenddate
    FROM patients
        JOIN admissions ON admissions.patientid = patients.patientid
        JOIN diagnosis ON diagnosis.patientid = admissions.patientid;



-- Examples of other Disclosure Policies
-- 1. Front Desk Staff Policy
CREATE VIEW patients_admissions_policy AS
    SELECT  generalize_date(to_date(to_char(admissionstartdate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'MONTH') AS to_date_to_char_admissionstartdate,
            generalize_date(to_date(to_char(admissionenddate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'MONTH') AS to_date_to_char_admissionenddate,
            randomized_response_patientgender(patientgender) AS patientgender,
            randomized_response_patientmaritalstatus(patientmartialstatus) AS patientmartialstatus,
            randomized_response_patientlanguage(patientlanguage) AS patientlanguage,
            patients.patientid AS patients_patientid,
            admissions.admissionid AS admissions_admissionid
    FROM patients
        JOIN admissions ON admissions.patientid = patients.patientid;

-- 2. Lab Technician
CREATE VIEW labs_policy AS
    SELECT  generalize_labname(labname) AS labname,
            generalize_date(to_date(to_char(labdatetime, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'MONTH') AS to_date_to_char_labdatetime,
            labvalue AS labvalue,
            labunits AS labunits
    FROM labs;

-- 3. Specialist Doctor
CREATE VIEW patients_admissions_diagnosis_policy944 AS
    SELECT  generalize_primarydiagnosiscode(patients.patientid) AS patients_patientid,
            bucketize(admissions.admissionid, 5) AS admissions_admissionid,
            generalize_primarydiagnosiscode(primarydiagnosiscode) AS primarydiagnosiscode,
            generalize_date(to_date(to_char(admissionstartdate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'MONTH') AS to_date_to_char_admissionstartdate,
            generalize_date(to_date(to_char(admissionenddate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'MONTH') AS to_date_to_char_admissionenddate,
            primarydiagnosisdescription AS primarydiagnosisdescription
    FROM patients
        JOIN admissions ON admissions.patientid = patients.patientid
        JOIN diagnosis ON diagnosis.patientid = admissions.patientid;

-- 7. Patient Demographics
CREATE VIEW patients_policy AS
    SELECT  randomized_response_patientgender(patientgender) AS patientgender,
            randomized_response_patientmaritalstatus(patientmartialstatus) AS patientmartialstatus,
            randomized_response_patientlanguage(patientlanguage) AS patientlanguage,
            randomized_response_patientrace(patientrace) AS patientrace,
            patientid AS patientid,
            patientdateofbirth AS patientdateofbirth
    FROM patients;

-- 8. Diagnosis per Admission
CREATE VIEW admissions_diagnosis_policy AS
    SELECT  generalize_primarydiagnosiscode(admissions.patientid) AS admissions_patientid,
            generalize_primarydiagnosiscode(primarydiagnosiscode) AS primarydiagnosiscode,
            admissions.admissionid AS admissions_admissionid,
            primarydiagnosisdescription AS primarydiagnosisdescription
    FROM admissions
        JOIN diagnosis ON diagnosis.patientid = admissions.patientid;