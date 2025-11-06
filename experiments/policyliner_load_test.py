import json
import logging
import random

import time
from locust import HttpUser, task, between, events
from locust import LoadTestShape
from locust.contrib.fasthttp import FastHttpUser

logging.basicConfig(level=logging.INFO)

SIMPLE_QUERIES = [
    "SELECT * FROM patients_policy WHERE patientgender = 'M' LIMIT 100",
    "SELECT * FROM patients_policy WHERE patientmaritalstatus = 'Married' LIMIT 50",
    "SELECT * FROM labs_policy WHERE labname LIKE 'CBC%' LIMIT 100",
    "SELECT * FROM patients_admissions_policy WHERE patientgender = 'Female' LIMIT 100",
    "SELECT * FROM admissions_diagnosis_policy WHERE primarydiagnosiscode LIKE 'F%' LIMIT 100"
]
AGGREGATE_QUERIES = [
    "SELECT COUNT(*) as total_patients FROM patients_policy",
    "SELECT patientgender, COUNT(*) as count FROM patients_policy GROUP BY patientgender",
    "SELECT patientmaritalstatus, COUNT(*) as count FROM patients_policy GROUP BY patientmaritalstatus",
    "SELECT patientrace, COUNT(*) as count FROM patients_policy GROUP BY patientrace ORDER BY count DESC",
    "SELECT patientgender, COUNT(*) as admission_count FROM patients_admissions_policy GROUP BY patientgender",
    "SELECT COUNT(DISTINCT patients_patientid) as unique_patients FROM patients_admissions_policy",
    "SELECT labname, COUNT(*) as test_count FROM labs_policy GROUP BY labname ORDER BY test_count DESC LIMIT 20",
    "SELECT labnames, COUNT(DISTINCT patients_patientid) as patient_count FROM patients_admissions_diagnosis_labs_policy WHERE labnames IS NOT NULL GROUP BY labnames ORDER BY patient_count DESC LIMIT 30",
    "SELECT to_date_to_char_admissionstartdate, to_date_to_char_admissionenddate, COUNT(*) as count FROM patients_admissions_diagnosis_policy556 GROUP BY to_date_to_char_admissionstartdate, to_date_to_char_admissionenddate ORDER BY count DESC LIMIT 50"
]
COMPLEX_QUERIES = [
    "SELECT primarydiagnosiscode, COUNT(*) as count, AVG(patientpopulationpercentagebelowpoverty) as avg_poverty, MIN(patientpopulationpercentagebelowpoverty) as min_poverty, MAX(patientpopulationpercentagebelowpoverty) as max_poverty FROM patients_admissions_diagnosis_policy GROUP BY primarydiagnosiscode HAVING COUNT(*) > 20 ORDER BY avg_poverty DESC LIMIT 50",
    "SELECT admissions_admissionid, COUNT(DISTINCT labname) as unique_lab_tests FROM patients_admissions_diagnosis_labs_policy790 GROUP BY admissions_admissionid ORDER BY unique_lab_tests DESC LIMIT 500",
    "SELECT labname, to_date_to_char_labdatetime, COUNT(*) as tests_per_day FROM labs_policy GROUP BY labname, to_date_to_char_labdatetime ORDER BY tests_per_day DESC LIMIT 100",
    "SELECT * FROM patients_admissions_diagnosis_labs_policy WHERE to_date_to_char_admissionstartdate > '2024-01-01' LIMIT 200",
]

class PolicyLinerUser(FastHttpUser):
    # Wait time between tasks
    wait_time = between(2, 5)

    user_id = None

    def on_start(self):
        self.user_id = random.randint(1, 100000)
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
    def aggregate_query(self):
        query = random.choice(AGGREGATE_QUERIES)
        with self.client.post(
                "/api/query/analyze",
                json={
                    "query": query,
                    "userId": self.user_id,
                    "userRole": "user",
                    "comparatorType": "CUSTOM"
                },
                catch_response=True,
                name="POST /api/query/analyze [Aggregate]"
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

    @task(1)
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
        {"duration": 60, "users": 500, "spawn_rate": 2},
        {"duration": 180, "users": 100, "spawn_rate": 2},
        {"duration": 240, "users": 200, "spawn_rate": 3},
        {"duration": 480, "users": 400, "spawn_rate": 3},
        {"duration": 540, "users": 0, "spawn_rate": 5},
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