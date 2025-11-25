-- ######################
-- SIMILAR QUERIES
-- ######################

-- SIMILAR SET 1: Count queries on same view with different approaches
-- Q1a: Direct count
SELECT COUNT(*) 
FROM patients_admissions_diagnosis_policy;

-- Q1b: Count with GROUP BY and aggregate (returns same total)
SELECT SUM(cnt) AS total_count
FROM (
    SELECT COUNT(*) AS cnt
    FROM patients_admissions_diagnosis_policy
    GROUP BY patientrace
) subquery;

-- Q1c: Count with HAVING clause that includes everything
SELECT COUNT(*)
FROM patients_admissions_diagnosis_policy
HAVING COUNT(*) > 0;

-- SIMILAR SET 2: Gender distribution queries
-- Q2a: Group by gender
SELECT patientgender, COUNT(*) AS count
FROM patients_policy
GROUP BY patientgender
ORDER BY patientgender;

-- Q2b: Same query using CASE statement
SELECT 
    CASE WHEN patientgender IS NOT NULL THEN patientgender ELSE NULL END AS gender,
    COUNT(*) AS count
FROM patients_policy
GROUP BY patientgender
ORDER BY patientgender;

-- Q2c: Using subquery
SELECT gender, COUNT(*) AS count
FROM (SELECT patientgender AS gender FROM patients_policy) t
GROUP BY gender
ORDER BY gender;

-- SIMILAR SET 3: Date range queries with different syntax
-- Q3a: Using BETWEEN
SELECT COUNT(*)
FROM patients_admissions_policy
WHERE to_date_to_char_admissionstartdate BETWEEN '2020-01-01' AND '2020-12-31';

-- Q3b: Using >= and <=
SELECT COUNT(*)
FROM patients_admissions_policy
WHERE to_date_to_char_admissionstartdate >= '2020-01-01' 
  AND to_date_to_char_admissionstartdate <= '2020-12-31';

-- Q3c: Using >= and < (next year)
SELECT COUNT(*)
FROM patients_admissions_policy
WHERE to_date_to_char_admissionstartdate >= '2020-01-01' 
  AND to_date_to_char_admissionstartdate < '2021-01-01';

-- SIMILAR SET 4: JOIN operations that produce same results
-- Q4a: Join patients and admissions policies
SELECT p.patientid, a.admissions_admissionid
FROM patients_policy p
INNER JOIN patients_admissions_policy a ON p.patientid = a.patients_patientid;

-- Q4b: Same join with table aliases reversed
SELECT p2.patientid, a2.admissions_admissionid
FROM patients_admissions_policy a2
INNER JOIN patients_policy p2 ON a2.patients_patientid = p2.patientid;

-- Q4c: Using WHERE clause instead of INNER JOIN
SELECT p.patientid, a.admissions_admissionid
FROM patients_policy p, patients_admissions_policy a
WHERE p.patientid = a.patients_patientid;

-- SIMILAR SET 5: Aggregation with different grouping orders
-- Q5a: Group by race then gender
SELECT patientrace, patientgender, COUNT(*) AS count
FROM patients_admissions_diagnosis_policy
GROUP BY patientrace, patientgender
ORDER BY patientrace, patientgender;

-- Q5b: Group by gender then race (same result set, different order)
SELECT patientrace, patientgender, COUNT(*) AS count
FROM patients_admissions_diagnosis_policy
GROUP BY patientgender, patientrace
ORDER BY patientrace, patientgender;

-- SIMILAR SET 6: DISTINCT queries
-- Q6a: Using DISTINCT
SELECT DISTINCT primarydiagnosiscode
FROM patients_admissions_diagnosis_policy;

-- Q6b: Using GROUP BY (equivalent to DISTINCT)
SELECT primarydiagnosiscode
FROM patients_admissions_diagnosis_policy
GROUP BY primarydiagnosiscode;

-- SIMILAR SET 7: NULL handling queries
-- Q7a: Count non-null values
SELECT COUNT(patientrace) AS non_null_race_count
FROM patients_policy;

-- Q7b: Count with WHERE IS NOT NULL
SELECT COUNT(*) AS non_null_race_count
FROM patients_policy
WHERE patientrace IS NOT NULL;

-- Q7c: Using CASE to count non-nulls
SELECT SUM(CASE WHEN patientrace IS NOT NULL THEN 1 ELSE 0 END) AS non_null_race_count
FROM patients_policy;

-- SIMILAR SET 8: Subquery equivalence
-- Q8a: Using IN clause
SELECT patientid
FROM patients_policy
WHERE patientgender IN ('Male', 'Female');

-- Q8b: Using OR clause
SELECT patientid
FROM patients_policy
WHERE patientgender = 'Male' OR patientgender = 'Female';

-- SIMILAR SET 9: Lab queries with different filters
-- Q9a: Filter labs then count
SELECT COUNT(*)
FROM labs_policy
WHERE labvalue IS NOT NULL;

-- Q9b: Count with subquery filter
SELECT COUNT(*)
FROM (SELECT * FROM labs_policy WHERE labvalue IS NOT NULL) filtered;

-- SIMILAR SET 10: Admission diagnosis queries
-- Q10a: Select from view
SELECT admissions_patientid, primarydiagnosiscode
FROM admissions_diagnosis_policy
ORDER BY admissions_patientid;

-- Q10b: Select with redundant subquery
SELECT admissions_patientid, primarydiagnosiscode
FROM (SELECT * FROM admissions_diagnosis_policy) sub
ORDER BY admissions_patientid;

-- SIMILAR SET 11: UNION ALL with partitioned data
-- Q11a: Single query with OR
SELECT patientid, patientgender
FROM patients_policy
WHERE patientgender = 'Male' OR patientgender = 'Female';

-- Q11b: UNION ALL of separate filters (same result)
SELECT patientid, patientgender
FROM patients_policy
WHERE patientgender = 'Male'
UNION ALL
SELECT patientid, patientgender
FROM patients_policy
WHERE patientgender = 'Female';

-- SIMILAR SET 12: Existence checks
-- Q12a: EXISTS with subquery
SELECT p.patientid
FROM patients_policy p
WHERE EXISTS (
    SELECT 1 FROM patients_admissions_policy a 
    WHERE a.patients_patientid = p.patientid
);

-- Q12b: IN with subquery (equivalent)
SELECT p.patientid
FROM patients_policy p
WHERE p.patientid IN (
    SELECT patients_patientid FROM patients_admissions_policy
);

-- Q12c: INNER JOIN (equivalent for this case)
SELECT DISTINCT p.patientid
FROM patients_policy p
INNER JOIN patients_admissions_policy a ON p.patientid = a.patients_patientid;


-- ##############################################################
-- DIFFERENT QUERIES
-- ##############################################################


-- DIFFERENT SET 1: COUNT vs COUNT DISTINCT
-- D1a: Total rows
SELECT COUNT(*) AS total_admissions
FROM patients_admissions_policy;

-- D1b: Distinct patients (completely different number)
SELECT COUNT(DISTINCT patients_patientid) AS unique_patients
FROM patients_admissions_policy;

-- DIFFERENT SET 2: INNER JOIN vs LEFT JOIN
-- D2a: INNER JOIN (only matching records)
SELECT p.patientid, a.admissions_admissionid
FROM patients_policy p
INNER JOIN patients_admissions_policy a ON p.patientid = a.patients_patientid;

-- D2b: LEFT JOIN (includes patients without admissions)
SELECT p.patientid, a.admissions_admissionid
FROM patients_policy p
LEFT JOIN patients_admissions_policy a ON p.patientid = a.patients_patientid;

-- DIFFERENT SET 3: Aggregation level differences
-- D3a: Total count across all groups
SELECT COUNT(*) AS total
FROM patients_admissions_diagnosis_policy;

-- D3b: Count per diagnosis (returns multiple rows)
SELECT primarydiagnosiscode, COUNT(*) AS count_per_diagnosis
FROM patients_admissions_diagnosis_policy
GROUP BY primarydiagnosiscode;

-- DIFFERENT SET 4: WHERE vs HAVING
-- D4a: Filter before aggregation
SELECT patientgender, COUNT(*) AS count
FROM patients_policy
WHERE patientdateofbirth > '1980-01-01'
GROUP BY patientgender;

-- D4b: Filter after aggregation (completely different logic)
SELECT patientgender, COUNT(*) AS count
FROM patients_policy
GROUP BY patientgender
HAVING COUNT(*) > 100;

-- DIFFERENT SET 5: EXISTS vs NOT EXISTS
-- D5a: Patients with admissions
SELECT patientid
FROM patients_policy p
WHERE EXISTS (
    SELECT 1 FROM patients_admissions_policy a 
    WHERE a.patients_patientid = p.patientid
);

-- D5b: Patients WITHOUT admissions
SELECT patientid
FROM patients_policy p
WHERE NOT EXISTS (
    SELECT 1 FROM patients_admissions_policy a 
    WHERE a.patients_patientid = p.patientid
);

-- DIFFERENT SET 6: Aggregation with different functions
-- D6a: Average lab value
SELECT AVG(labvalue) AS avg_value
FROM labs_policy
WHERE labvalue IS NOT NULL;

-- D6b: Sum of lab values (totally different)
SELECT SUM(labvalue) AS total_value
FROM labs_policy
WHERE labvalue IS NOT NULL;

-- DIFFERENT SET 7: Date comparison with different operators
-- D7a: Before a date
SELECT COUNT(*)
FROM patients_admissions_policy
WHERE to_date_to_char_admissionstartdate < '2020-06-01';

-- D7b: After the same date (mutually exclusive)
SELECT COUNT(*)
FROM patients_admissions_policy
WHERE to_date_to_char_admissionstartdate >= '2020-06-01';

-- DIFFERENT SET 8: NULL handling differences
-- D8a: IS NULL check
SELECT COUNT(*)
FROM labs_policy
WHERE labvalue IS NULL;

-- D8b: IS NOT NULL check (opposite)
SELECT COUNT(*)
FROM labs_policy
WHERE labvalue IS NOT NULL;

-- DIFFERENT SET 9: MIN vs MAX aggregation
-- D9a: Earliest admission date per patient
SELECT patients_patientid, MIN(to_date_to_char_admissionstartdate) AS earliest_admission
FROM patients_admissions_policy
GROUP BY patients_patientid;

-- D9b: Latest admission date per patient
SELECT patients_patientid, MAX(to_date_to_char_admissionstartdate) AS latest_admission
FROM patients_admissions_policy
GROUP BY patients_patientid;

-- DIFFERENT SET 10: IN vs NOT IN
-- D10a: Specific diagnosis codes
SELECT COUNT(*)
FROM patients_admissions_diagnosis_policy
WHERE primarydiagnosiscode IN ('A00', 'A01', 'A02');

-- D10b: All OTHER diagnosis codes
SELECT COUNT(*)
FROM patients_admissions_diagnosis_policy
WHERE primarydiagnosiscode NOT IN ('A00', 'A01', 'A02');

-- DIFFERENT SET 11: ORDER BY with LIMIT
-- D11a: First 10 patients
SELECT patientid FROM patients_policy
ORDER BY patientid ASC
LIMIT 10;

-- D11b: Last 10 patients (completely different set)
SELECT patientid FROM patients_policy
ORDER BY patientid DESC
LIMIT 10;

-- DIFFERENT SET 12: Filtering on different columns
-- D12a: Filter by race
SELECT COUNT(*)
FROM patients_admissions_diagnosis_policy
WHERE patientrace = 'White';

-- D12b: Filter by marital status (different population)
SELECT COUNT(*)
FROM patients_admissions_diagnosis_policy
WHERE patientmartialstatus = 'Married';