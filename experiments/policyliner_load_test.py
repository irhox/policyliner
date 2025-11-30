import json
import logging
import random

import time
from locust import HttpUser, task, between, events
from locust import LoadTestShape
from locust.contrib.fasthttp import FastHttpUser

logging.basicConfig(level=logging.INFO)

SIMPLE_QUERIES = [
    "SELECT * FROM patients_policy;",
    "SELECT patientid, patientgender, patientmartialstatus, patientlanguage, patientrace, patientdateofbirth FROM patients_policy;",
    "SELECT COUNT(*) FROM patients_policy;",
    "SELECT COUNT(*) FROM (SELECT * FROM patients_policy) AS subquery;",
    "SELECT patientid FROM patients_policy WHERE patientgender = 'Male';",
    "SELECT patientid FROM patients_policy WHERE 'Male' = patientgender;",
    "SELECT patientgender, COUNT(*) FROM patients_policy GROUP BY patientgender;",
    "SELECT patientgender, COUNT(*) AS total FROM patients_policy GROUP BY patientgender;",
    "SELECT patientid FROM patients_policy ORDER BY patientid;",
    "SELECT patientid FROM patients_policy ORDER BY patientid ASC;",
    "SELECT * FROM patients_policy WHERE patientrace = 'White';",
    "SELECT COUNT(*) FROM patients_policy WHERE patientgender = 'Female';",
    "SELECT * FROM labs_policy;",
    "SELECT patientid, patientgender FROM patients_policy WHERE 1=1;",
    "SELECT labname, to_date_to_char_labdatetime, labvalue, labunits FROM labs_policy;",
    "SELECT admissions_admissionid FROM patients_admissions_policy;",
    "SELECT primarydiagnosiscode FROM admissions_diagnosis_policy;",
    "SELECT COUNT(*) FROM patients_policy;",
    "SELECT COUNT(*) FROM patients_admissions_policy;",
    "SELECT COUNT(*) FROM patients_policy WHERE patientgender = 'Male';"
]

COMPLEX_QUERIES = [
    "SELECT SUM(cnt) AS total_count FROM (SELECT COUNT(*) AS cnt FROM patients_admissions_diagnosis_policy GROUP BY patientrace) subquery;",
    "SELECT COUNT(*) FROM patients_admissions_diagnosis_policy HAVING COUNT(*) > 0;",
    "SELECT patientgender, COUNT(*) AS count FROM patients_policy GROUP BY patientgender ORDER BY patientgender;",
    "SELECT CASE WHEN patientgender IS NOT NULL THEN patientgender ELSE NULL END AS patientgender, COUNT(*) AS count FROM patients_policy GROUP BY patientgender ORDER BY patientgender;",
    "SELECT patientgender, COUNT(*) AS count FROM (SELECT patientgender FROM patients_policy) t GROUP BY patientgender ORDER BY patientgender;",
    "SELECT COUNT(*) FROM patients_admissions_policy WHERE to_date_to_char_admissionstartdate BETWEEN '2020-01-01' AND '2020-12-31';",
    "SELECT COUNT(*) FROM patients_admissions_policy WHERE to_date_to_char_admissionstartdate >= '2020-01-01' AND to_date_to_char_admissionstartdate <= '2020-12-31';",
    "SELECT COUNT(*) FROM patients_admissions_policy WHERE to_date_to_char_admissionstartdate >= '2020-01-01' AND to_date_to_char_admissionstartdate < '2021-01-01';",
    "SELECT patients_policy.patientid, patients_admissions_policy.admissions_admissionid FROM patients_policy INNER JOIN patients_admissions_policy ON patients_policy.patientid = patients_admissions_policy.patients_patientid;",
    "SELECT patients_admissions_policy.patients_patientid, patients_admissions_policy.admissions_admissionid FROM patients_admissions_policy INNER JOIN patients_policy ON patients_admissions_policy.patients_patientid = patients_policy.patientid;",
    "SELECT p.patientid, a.admissions_admissionid FROM patients_policy p, patients_admissions_policy a WHERE p.patientid = a.patients_patientid;",
    "SELECT patientrace, patientgender, COUNT(*) AS count FROM patients_admissions_diagnosis_policy GROUP BY patientrace, patientgender ORDER BY patientrace, patientgender;",
    "SELECT patientrace, patientgender, COUNT(*) AS count FROM patients_admissions_diagnosis_policy GROUP BY patientgender, patientrace ORDER BY patientrace, patientgender;",
    "SELECT DISTINCT primarydiagnosiscode FROM patients_admissions_diagnosis_policy;",
    "SELECT primarydiagnosiscode FROM patients_admissions_diagnosis_policy GROUP BY primarydiagnosiscode;",
    "SELECT COUNT(patientrace) AS non_null_race_count FROM patients_policy;",
    "SELECT COUNT(*) AS non_null_race_count FROM patients_policy WHERE patientrace IS NOT NULL;",
    "SELECT SUM(CASE WHEN patientrace IS NOT NULL THEN 1 ELSE 0 END) AS non_null_race_count FROM patients_policy;",
    "SELECT patientid, patientgender FROM patients_policy WHERE patientgender = 'Male' UNION ALL SELECT patientid, patientgender FROM patients_policy WHERE patientgender = 'Female';"
    "SELECT patients_patientid, MAX(to_date_to_char_admissionstartdate) AS latest_admission FROM patients_admissions_policy GROUP BY patients_patientid;",
]

class PolicyLinerUser(FastHttpUser):
    # Wait time between tasks
    wait_time = between(1, 2)

    user_id = None

    def on_start(self):
        self.user_id = random.randint(100, 100000)
        logging.info(f"Starting user {self.user_id}")

    def on_stop(self):
        logging.info(f"Stopping user {self.user_id}")

    @task(5)
    def simple_query(self):
        query = random.choice(SIMPLE_QUERIES)
        with (self.client.post(
                "/api/query/analyze",
                json={
                    "query": query,
                    "userId": self.user_id,
                    "userRole": "user",
                    "comparatorType": "CUSTOM"
                },
                catch_response=True,
                name="POST /api/query/analyze [Simple]"
        ) as response):
            if response.status_code == 200:
                try:
                    data = response.json()
                    query_id = data["id"]

                    if query_id:
                        response.success()

                        self.query_id = query_id

                        time.sleep(random.uniform(0.5, 1.5))

                    else:
                        response.failure("Query ID not found in response")
                except json.decoder.JSONDecodeError:
                    response.failure("Invalid JSON response")
            else:
                response.failure(f"Unexpected response status code {response.status_code}")

    @task(3)
    def complex_query(self):
        query = random.choice(COMPLEX_QUERIES)
        with self.client.post(
                "/api/query/analyze",
                json={
                    "query": query,
                    "userId": self.user_id,
                    "userRole": "user",
                    "comparatorType": "CUSTOM"
                },
                catch_response=True,
                name="POST /api/query/analyze [Complex]"
        ) as response:
            if response.status_code == 200:
                try:
                    data = response.json()
                    query_id = data["id"]

                    if query_id:
                        response.success()

                        self.query_id = query_id

                        time.sleep(random.uniform(0.5, 1.5))

                    else:
                        response.failure("Query ID not found in response")
                except json.decoder.JSONDecodeError:
                    response.failure("Invalid JSON response")
            else:
                response.failure(f"Unexpected response status code {response.status_code}")


class PolicyLinerLoadTestShape(LoadTestShape):
    stages = [
        {"duration": 60, "users": 50, "spawn_rate": 2},
        {"duration": 180, "users": 100, "spawn_rate": 2},
        {"duration": 240, "users": 150, "spawn_rate": 2},
        {"duration": 300, "users": 200, "spawn_rate": 2},
    ]

    def tick(self):
        run_time = self.get_run_time()

        for stage in self.stages:
            if run_time < stage["duration"]:
                return (stage["users"], stage["spawn_rate"])

        return None


if __name__ == "__main__":
    import subprocess

    host = "http://localhost:8080"

    print("Starting Locust with 100 concurrent users...")
    print(f"Target: http://localhost:8080")

# Run locust command
    cmd = [
        "locust",
        "-f", __file__,
        "--host", host,
        "--users", "100",
        "--spawn-rate", "10",
        "--run-time", "5m",
        "--headless",
        "--html", "policyliner-load-test-report.html",
        "--csv", "policyliner-load-test-results"
    ]

    subprocess.run(cmd)