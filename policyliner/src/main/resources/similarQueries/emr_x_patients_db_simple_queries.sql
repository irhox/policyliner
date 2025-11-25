-- ######################
-- SIMILAR QUERIES
-- ######################

-- SIMILAR SET 1: Basic SELECT queries
-- Q1a: Select all from patients policy
SELECT * FROM patients_policy;

-- Q1b: Select with explicit columns (same data)
SELECT patientid, patientgender, patientmartialstatus, patientlanguage, patientrace, patientdateofbirth
FROM patients_policy;

-- SIMILAR SET 2: Simple counts
-- Q2a: Count all patients
SELECT COUNT(*) FROM patients_policy;

-- Q2b: Count using subquery
SELECT COUNT(*) FROM (SELECT * FROM patients_policy) AS subquery;

-- SIMILAR SET 3: Filter by gender
-- Q3a: Male patients
SELECT patientid FROM patients_policy WHERE patientgender = 'Male';

-- Q3b: Male patients with different column order
SELECT patientid FROM patients_policy WHERE 'Male' = patientgender;

-- SIMILAR SET 4: Simple GROUP BY
-- Q4a: Count by gender
SELECT patientgender, COUNT(*) FROM patients_policy GROUP BY patientgender;

-- Q4b: Same with alias
SELECT patientgender AS gender, COUNT(*) AS total FROM patients_policy GROUP BY patientgender;

-- SIMILAR SET 5: ORDER BY variations
-- Q5a: Order by patient id ascending
SELECT patientid FROM patients_policy ORDER BY patientid;

-- Q5b: Order by patient id ascending explicitly
SELECT patientid FROM patients_policy ORDER BY patientid ASC;

-- SIMILAR SET 6: Simple WHERE clauses
-- Q6a: Filter by race
SELECT * FROM patients_policy WHERE patientrace = 'White';

-- Q6b: Same filter with parentheses
SELECT * FROM patients_policy WHERE (patientrace = 'White');

-- SIMILAR SET 7: Count with filter
-- Q7a: Count female patients
SELECT COUNT(*) FROM patients_policy WHERE patientgender = 'Female';

-- Q7b: Count female patients using subquery
SELECT COUNT(*) FROM (SELECT * FROM patients_policy WHERE patientgender = 'Female') AS females;

-- SIMILAR SET 8: Lab queries
-- Q8a: Select all labs
SELECT * FROM labs_policy;

-- Q8b: Select labs with explicit columns
SELECT labname, to_date_to_char_labdatetime, labvalue, labunits FROM labs_policy;

-- SIMILAR SET 9: Admission queries
-- Q9a: Get admission IDs
SELECT admissions_admissionid FROM patients_admissions_policy;

-- Q9b: Get admission IDs with subquery
SELECT admissions_admissionid FROM (SELECT * FROM patients_admissions_policy) AS admissions;

-- SIMILAR SET 10: Diagnosis queries
-- Q10a: Get diagnosis codes
SELECT primarydiagnosiscode FROM admissions_diagnosis_policy;

-- Q10b: Get distinct diagnosis codes then select all
SELECT primarydiagnosiscode FROM (SELECT DISTINCT primarydiagnosiscode FROM admissions_diagnosis_policy) AS codes;

-- SIMILAR SET 11: Simple two-column selects
-- Q11a: Patient ID and gender
SELECT patientid, patientgender FROM patients_policy;

-- Q11b: Same with WHERE 1=1 (always true)
SELECT patientid, patientgender FROM patients_policy WHERE 1=1;

-- SIMILAR SET 12: Count distinct
-- Q12a: Count distinct genders
SELECT COUNT(DISTINCT patientgender) FROM patients_policy;

-- Q12b: Count genders using GROUP BY
SELECT COUNT(*) FROM (SELECT patientgender FROM patients_policy GROUP BY patientgender) AS genders;

-- ##############################################################
-- DIFFERENT QUERIES
-- ##############################################################


-- DIFFERENT SET 1: Different views
-- D1a: Count patients
SELECT COUNT(*) FROM patients_policy;

-- D1b: Count admissions (different table)
SELECT COUNT(*) FROM patients_admissions_policy;

-- DIFFERENT SET 2: Opposite gender
-- D2a: Male patients
SELECT COUNT(*) FROM patients_policy WHERE patientgender = 'Male';

-- D2b: Female patients
SELECT COUNT(*) FROM patients_policy WHERE patientgender = 'Female';

-- DIFFERENT SET 3: Equal vs Not Equal
-- D3a: Male patients
SELECT COUNT(*) FROM patients_policy WHERE patientgender = 'Male';

-- D3b: Non-male patients
SELECT COUNT(*) FROM patients_policy WHERE patientgender != 'Male';

-- DIFFERENT SET 4: IS NULL vs IS NOT NULL
-- D4a: Patients with null race
SELECT COUNT(*) FROM patients_policy WHERE patientrace IS NULL;

-- D4b: Patients with non-null race
SELECT COUNT(*) FROM patients_policy WHERE patientrace IS NOT NULL;

-- DIFFERENT SET 5: Different date ranges
-- D5a: Admissions in 2020
SELECT COUNT(*) FROM patients_admissions_policy 
WHERE to_date_to_char_admissionstartdate >= '2020-01-01' 
  AND to_date_to_char_admissionstartdate < '2021-01-01';

-- D5b: Admissions in 2021
SELECT COUNT(*) FROM patients_admissions_policy 
WHERE to_date_to_char_admissionstartdate >= '2021-01-01' 
  AND to_date_to_char_admissionstartdate < '2022-01-01';

-- DIFFERENT SET 6: Lab value comparisons
-- D6a: High lab values
SELECT COUNT(*) FROM labs_policy WHERE labvalue > 100;

-- D6b: Low lab values
SELECT COUNT(*) FROM labs_policy WHERE labvalue <= 100;

-- DIFFERENT SET 7: COUNT vs COUNT DISTINCT
-- D7a: Total admissions
SELECT COUNT(*) FROM patients_admissions_policy;

-- D7b: Unique patients with admissions
SELECT COUNT(DISTINCT patients_patientid) FROM patients_admissions_policy;

-- DIFFERENT SET 8: Different columns
-- D8a: Count by gender
SELECT patientgender, COUNT(*) FROM patients_policy GROUP BY patientgender;

-- D8b: Count by race (different grouping)
SELECT patientrace, COUNT(*) FROM patients_policy GROUP BY patientrace;

-- DIFFERENT SET 9: MIN vs MAX
-- D9a: Minimum lab value
SELECT MIN(labvalue) FROM labs_policy;

-- D9b: Maximum lab value
SELECT MAX(labvalue) FROM labs_policy;

-- DIFFERENT SET 10: First vs Last records
-- D10a: First 10 patients
SELECT patientid FROM patients_policy ORDER BY patientid LIMIT 10;

-- D10b: Last 10 patients
SELECT patientid FROM patients_policy ORDER BY patientid DESC LIMIT 10;

-- DIFFERENT SET 11: Different marital statuses
-- D11a: Married patients
SELECT COUNT(*) FROM patients_policy WHERE patientmartialstatus = 'Married';

-- D11b: Single patients
SELECT COUNT(*) FROM patients_policy WHERE patientmartialstatus = 'Single';

-- DIFFERENT SET 12: Before vs After date
-- D12a: Admissions before 2020
SELECT COUNT(*) FROM patients_admissions_policy WHERE to_date_to_char_admissionstartdate < '2020-01-01';

-- D12b: Admissions after 2020
SELECT COUNT(*) FROM patients_admissions_policy WHERE to_date_to_char_admissionstartdate >= '2020-01-01';
