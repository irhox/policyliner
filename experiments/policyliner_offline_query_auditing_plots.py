import csv
import os

import matplotlib.pyplot as plt
import numpy as np

execution_times_ms = [
    126, 1, 23, 2, 12, 2, 11, 1, 12, 1, 16, 1, 9, 1,
    4393, 2, 22, 2, 23, 1, 12, 0, 6, 0, 78, 5, 1, 26,
    12, 1, 44, 22, 1, 52, 23, 2, 83, 0, 3, 1, 8, 5, 0,
    10, 2, 4236, 1, 20, 2, 14, 2, 50, 31, 1, 4, 0, 0, 1,
    13, 2, 4, 0, 93, 12, 2, 21, 1, 5, 1, 7, 5, 1, 10, 6, 2,
    0, 7, 4, 1, 4, 1, 4, 2, 6, 8, 1, 0, 3, 0, 1, 5, 0,
    4, 0, 1, 4, 0, 0, 1, 0
]

USE_CSV = True
CSV_5000_FILE_PATH = "5000Queries100Patients.csv"
CSV_1000_FILE_PATH = "1000Queries100Patients.csv"
CSV_100_FOR_10000_PATIENTS_FILE_PATH = "100Queries10000Patients.csv"
CSV_1000_FOR_10000_PATIENTS_FILE_PATH = "1000Queries10000Patients.csv"
CSV_100_FOR_100000_PATIENTS_FILE_PATH = "100Queries100000Patients.csv"
CSV_COLUMN_NAME = "Duration in ms"


if USE_CSV:
    if not os.path.exists(CSV_100_FOR_100000_PATIENTS_FILE_PATH ):
        print(f"Error: CSV file '{CSV_100_FOR_100000_PATIENTS_FILE_PATH }' not found!")
        exit(1)

    execution_times_ms = []
    with open(CSV_100_FOR_100000_PATIENTS_FILE_PATH, 'r') as csvfile:
        reader = csv.DictReader(csvfile)

        # Check if column exists
        if CSV_COLUMN_NAME not in reader.fieldnames:
            print(f"Error: Column '{CSV_COLUMN_NAME}' not found in CSV!")
            print(f"Available columns: {', '.join(reader.fieldnames)}")
            exit(1)

        for row in reader:
            try:
                execution_times_ms.append(float(row[CSV_COLUMN_NAME]))
            except ValueError:
                print(f"Warning: Skipping invalid value: {row[CSV_COLUMN_NAME]}")

    print(f"Loaded {len(execution_times_ms)} values from CSV file.")



# Create execution index
execution_index = list(range(1, len(execution_times_ms) + 1))

# Create the plot
plt.figure(figsize=(12, 6))
plt.plot(execution_index, execution_times_ms, marker='o', markersize=3, linewidth=1.5, color='#2E86AB')

# Customize the plot
plt.xlabel('Query Execution Index', fontsize=12)
plt.ylabel('Execution Time (ms)', fontsize=12)
plt.title('Offline Query Auditing Execution Time per Query', fontsize=14, fontweight='bold')
plt.grid(True, alpha=0.3, linestyle='--')

# Add some statistics as text
mean_time = np.mean(execution_times_ms)
min_time = np.min(execution_times_ms)
max_time = np.max(execution_times_ms)
median_time = np.median(execution_times_ms)
sum_time = np.sum(execution_times_ms)

stats_text = f'Avg: {mean_time:.2f} ms\nMedian: {median_time:.2f} ms\nMin: {min_time:.2f} ms\nMax: {max_time:.2f} ms'
plt.text(0.02, 0.98, stats_text, transform=plt.gca().transAxes,
         fontsize=10, verticalalignment='top',
         bbox=dict(boxstyle='round', facecolor='wheat', alpha=0.5))

# Tight layout for better spacing
plt.tight_layout()

# Save the plot
plt.savefig('plots/100000-patients_1000-queries_offline_query_auditing_plot.png', dpi=300, bbox_inches='tight')
print(f"Plot saved! Total executions: {len(execution_times_ms)}")
print(f"Statistics - Mean: {mean_time:.2f} ms, Min: {min_time:.2f} ms, Max: {max_time:.2f} ms, Sum: {sum_time:.2f} ms")

# Display the plot
plt.show()