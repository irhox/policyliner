-- 1. Medical Staff Policy
CREATE OR REPLACE VIEW patients_admissions_diagnosis_labs_policy
    AS SELECT  randomized_response_patientgender(patientgender) AS patientgender,
               generalize_date(to_date(to_char(patientdateofbirth, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'YEAR') AS to_date_to_char_patientdateofbirth,
               generalize_date(to_date(to_char(labdatetime, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'YEAR') AS to_date_to_char_labdatetime,
               generalize_date(to_date(to_char(admissionstartdate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'MONTH') AS to_date_to_char_admissionstartdate,
               generalize_date(to_date(to_char(admissionenddate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'MONTH') AS to_date_to_char_admissionenddate,
               patients.patientid AS patients_patientid,
               admissions.admissionid AS admissions_admissionid,
               primarydiagnosiscode AS primarydiagnosiscode,
               primarydiagnosisdescription AS primarydiagnosisdescription,
               labname AS labname,
               labvalue AS labvalue,
               labunits AS labunits
FROM patients
    JOIN admissions ON admissions.patientid = patients.patientid
    JOIN diagnosis ON diagnosis.admissionid = admissions.admissionid
    JOIN labs ON labs.admissionid = diagnosis.admissionid;

-- 2. Front Desk Staff Policy
CREATE OR REPLACE VIEW patients_admissions_policy
    AS SELECT  generalize_date(to_date(to_char(admissionstartdate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'MONTH') AS to_date_to_char_admissionstartdate,
               generalize_date(to_date(to_char(admissionenddate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'MONTH') AS to_date_to_char_admissionenddate,
               randomized_response_patientgender(patientgender) AS patientgender,
               randomized_response_patientmaritalstatus(patientmaritalstatus) AS patientmaritalstatus,
               randomized_response_patientlanguage(patientlanguage) AS patientlanguage,
               patients.patientid AS patients_patientid,
               admissions.admissionid AS admissions_admissionid
FROM patients
    JOIN admissions ON admissions.patientid = patients.patientid;

-- 3. Lab Technician
CREATE VIEW labs_policy
    AS SELECT  suppress(patientid) AS patientid,
               generalize_labname(labname) AS labname,
               generalize_date(to_date(to_char(labdatetime, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'MONTH') AS to_date_to_char_labdatetime,
               admissionid AS admissionid,
               labvalue AS labvalue,
               labunits AS labunits
       FROM labs;

-- 4. Public Health Researcher
CREATE VIEW patients_admissions_diagnosis_policy
    AS SELECT  suppress(patients.patientid) AS patients_patientid,
               suppress(admissions.admissionid) AS admissions_admissionid,
               randomized_response_patientlanguage(patientlanguage) AS patientlanguage,
               generalize_primarydiagnosiscode(primarydiagnosiscode) AS primarydiagnosiscode,
               patientpopulationpercentagebelowpoverty AS patientpopulationpercentagebelowpoverty
       FROM patients
           JOIN admissions ON admissions.patientid = patients.patientid
           JOIN diagnosis ON diagnosis.admissionid = admissions.admissionid;

-- 5. Audit Officer
CREATE VIEW patients_admissions_diagnosis_labs_policy899
    AS SELECT  generalize_primarydiagnosiscode(patients.patientid) AS patients_patientid,
               bucketize(admissions.admissionid, 10) AS admissions_admissionid,
               generalize_primarydiagnosiscode(primarydiagnosiscode) AS primarydiagnosiscode,
               generalize_labname(labname) AS labname
       FROM patients
           JOIN admissions ON admissions.patientid = patients.patientid
           JOIN diagnosis ON diagnosis.admissionid = admissions.admissionid
           JOIN labs ON labs.admissionid = diagnosis.admissionid;

-- 6. Specialist Doctor
CREATE VIEW patients_admissions_diagnosis_policy901
    AS SELECT  generalize_primarydiagnosiscode(patients.patientid) AS patients_patientid,
               bucketize(admissions.admissionid, 10) AS admissions_admissionid,
               generalize_primarydiagnosiscode(primarydiagnosiscode) AS primarydiagnosiscode,
               generalize_date(to_date(to_char(admissionstartdate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'MONTH') AS to_date_to_char_admissionstartdate,
               generalize_date(to_date(to_char(admissionenddate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'MONTH') AS to_date_to_char_admissionenddate,
               primarydiagnosisdescription AS primarydiagnosisdescription
       FROM patients
           JOIN admissions ON admissions.patientid = patients.patientid
           JOIN diagnosis ON diagnosis.admissionid = admissions.admissionid;

-- 7. Patient Demographics
CREATE VIEW patients_policy
    AS SELECT  randomized_response_patientgender(patientgender) AS patientgender,
               randomized_response_patientmaritalstatus(patientmaritalstatus) AS patientmaritalstatus,
               randomized_response_patientlanguage(patientlanguage) AS patientlanguage,
               randomized_response_patientrace(patientrace) AS patientrace,
               patientid AS patientid,
               patientdateofbirth AS patientdateofbirth
       FROM patients;

-- 8. Diagnosis per Admission
CREATE VIEW admissions_diagnosis_policy
    AS SELECT  generalize_primarydiagnosiscode(admissions.patientid) AS admissions_patientid,
               generalize_primarydiagnosiscode(primarydiagnosiscode) AS primarydiagnosiscode,
               admissions.admissionid AS admissions_admissionid,
               primarydiagnosisdescription AS primarydiagnosisdescription
       FROM admissions
           JOIN diagnosis ON diagnosis.admissionid = admissions.admissionid