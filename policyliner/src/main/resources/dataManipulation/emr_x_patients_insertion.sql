-- insert a new patient
INSERT INTO patients
VALUES ('FB908FBC-73CD-5A6F-0821-T52183DF385F',
        'Female',
        '1995-05-11 10:38:00.000000',
        'White',
        'Separated',
        'Icelandic',
        '18.31');

INSERT INTO admissions
VALUES (
        'FB908FBC-73CD-5A6F-0821-T52183DF385F',
        '10',
        '2025-06-25 09:21:00.000000',
        '2025-12-01 19:14:00.000000');

INSERT INTO diagnosis
VALUES (
        'FB908FBC-73CD-5A6F-0821-T52183DF385F',
        '10',
        'Z22.31',
        'Carrier of bacterial disease due to meningococci');

INSERT INTO labs
VALUES (
        'FB908FBC-73CD-5A6F-0821-T52183DF385F',
        '10',
        'CBC: MCH',
        '301',
        'pg',
        '2025-07-11 15:32:00.000000'
       );