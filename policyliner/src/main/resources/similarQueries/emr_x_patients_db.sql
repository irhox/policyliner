-- Similar Patient Queries
-- 1
SELECT * FROM patients;
-- 2
SELECT patientid, patientgender, patientdateofbirth, patientrace, patientmaritalstatus, patientlanguage, patientpopulationpercentagebelowpoverty
FROM patients;
-- 3
SELECT patientid, patientgender, patientdateofbirth, patientrace, patientmaritalstatus, patientlanguage
FROM patients;
-- 4
SELECT patientgender, patientdateofbirth, patientrace, patientmaritalstatus, patientlanguage, patientpopulationpercentagebelowpoverty
FROM patients;

-- Similar Patient Queries with where clause
-- 1
SELECT * FROM patients WHERE patientid = 1;
-- 2
SELECT patientid, patientgender, patientdateofbirth, patientrace, patientmaritalstatus, patientlanguage, patientpopulationpercentagebelowpoverty
FROM patients WHERE patientid = 1;
-- 3
SELECT * FROM patients WHERE patientid = 1 AND patientgender = 'M';
-- 4
SELECT patientid, patientdateofbirth, patientrace, patientmaritalstatus, patientlanguage, patientpopulationpercentagebelowpoverty
FROM patients WHERE patientgender = 'M';

