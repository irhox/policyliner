-- View Creation Queries:
-- 1
CREATE MATERIALIZED VIEW IF NOT EXISTS patients_policy169_materialized AS SELECT  generalize_date(to_date(to_char(patientdateofbirth, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'YEAR') AS to_date_to_char_patientdateofbirth,  randomized_response_patientrace(patientrace) AS patientrace, patientid AS patientid, patientgender AS patientgender, patientmaritalstatus AS patientmaritalstatus, patientlanguage AS patientlanguage, patientpopulationpercentagebelowpoverty AS patientpopulationpercentagebelowpoverty FROM patients;
-- 2
CREATE OR REPLACE VIEW patients_policy169 AS SELECT  generalize_date(to_date(to_char(patientdateofbirth, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'YEAR') AS to_date_to_char_patientdateofbirth,  randomized_response_patientrace(patientrace) AS patientrace, patientid AS patientid, patientgender AS patientgender, patientmaritalstatus AS patientmaritalstatus, patientlanguage AS patientlanguage, patientpopulationpercentagebelowpoverty AS patientpopulationpercentagebelowpoverty FROM patients;
-- 3
CREATE OR REPLACE VIEW admissions_policy AS SELECT  generalize_date(to_date(to_char(admissionstartdate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'YEAR') AS to_date_to_char_admissionstartdate,  generalize_date(to_date(to_char(admissionenddate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'YEAR') AS to_date_to_char_admissionenddate, patientid AS patientid, admissionid AS admissionid FROM admissions;
-- 4
CREATE MATERIALIZED VIEW IF NOT EXISTS admissions_policy_materialized AS SELECT  generalize_date(to_date(to_char(admissionstartdate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'YEAR') AS to_date_to_char_admissionstartdate,  generalize_date(to_date(to_char(admissionenddate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), 'YEAR') AS to_date_to_char_admissionenddate, patientid AS patientid, admissionid AS admissionid FROM admissions;

-- Similar Patient Queries
-- 1
SELECT * FROM patients_policy169;
-- 2
SELECT patientid, patientgender, to_date_to_char_patientdateofbirth, patientrace, patientmaritalstatus, patientlanguage, patientpopulationpercentagebelowpoverty
FROM patients_policy169;
-- 3
SELECT patientid, patientgender, to_date_to_char_patientdateofbirth, patientrace, patientmaritalstatus, patientlanguage
FROM patients_policy169_materialized;
-- 4
SELECT patientgender, to_date_to_char_patientdateofbirth, patientrace, patientmaritalstatus, patientlanguage, patientpopulationpercentagebelowpoverty
FROM patients_policy169;

-- Similar Patient Queries with where clause
-- 1
SELECT * FROM patients_policy169 WHERE patientid = 1;
-- 2
SELECT patientid, patientgender, to_date_to_char_patientdateofbirth, patientrace, patientmaritalstatus, patientlanguage, patientpopulationpercentagebelowpoverty
FROM patients_policy169 WHERE patientid = 1;
-- 3
SELECT * FROM patients_policy169_materialized WHERE patientid = 1 AND patientgender = 'Male';
-- 4
SELECT patientid, to_date_to_char_patientdateofbirth, patientrace, patientmaritalstatus, patientlanguage, patientpopulationpercentagebelowpoverty
FROM patients_policy169_materialized WHERE patientgender = 'Male';

-- Similar Admission Queries
-- 1
SELECT * FROM admissions_policy;
-- 2
SELECT patientid, admissionid, to_date_to_char_admissionenddate, to_date_to_char_admissionstartdate FROM admissions_policy;
-- 3
SELECT admissions.patientid, admissions.to_date_to_char_admissionenddate, admissions.to_date_to_char_admissionstartdate, admissions.admissionid
FROM admissions_policy as admissions;
-- 4
SELECT admissions.to_date_to_char_admissionenddate, admissions.admissionid, admissions.patientid, admissions.to_date_to_char_admissionenddate, admissions.to_date_to_char_admissionstartdate
FROM admissions_policy as admissions;

-- Similar Admission Queries with where clause
-- 1
SELECT * FROM admissions WHERE admissionid = 1;
-- 2
SELECT patientid, admissionid, to_date_to_char_admissionenddate, to_date_to_char_admissionstartdate
FROM admissions_policy WHERE admissionid = 1;
-- 3
SELECT admissions.patientid, admissions.to_date_to_char_admissionenddate, admissions.to_date_to_char_admissionstartdate, admissions.admissionid
FROM admissions_policy as admissions WHERE admissionid = 1;
-- 4
SELECT admissions.to_date_to_char_admissionenddate, admissions.admissionid, admissions.patientid, admissions.to_date_to_char_admissionstartdate
FROM admissions_policy as admissions WHERE to_date_to_char_admissionenddate = '2018-01-01';
