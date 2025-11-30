-- Remove the new inserted patient from emr_x_patients_insertion.sql
DELETE FROM labs WHERE patientid = 'FB908FBC-73CD-5A6F-0821-T52183DF385F';
DELETE FROM diagnosis WHERE patientid = 'FB908FBC-73CD-5A6F-0821-T52183DF385F';
DELETE FROM admissions WHERE patientid = 'FB908FBC-73CD-5A6F-0821-T52183DF385F';
DELETE FROM patients WHERE patientid = 'FB908FBC-73CD-5A6F-0821-T52183DF385F';


-- Delete all admissions before 1950
DELETE FROM admissions WHERE admissionstartdate < '1950-01-01';

