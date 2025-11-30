import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

##### 100 Patients Dataset
summary_100 = {
    "overall": [5980, 5916, 5957],
    "adp": [212, 209, 224],
    "lp": [5073, 5051, 5066],
    "padp": [175, 165, 166],
    "padp944": [159, 149, 156],
    "pap": [60, 56, 56],
    "pp": [41, 37, 33]
}

sample_100 = {
    "adp": [57, 54, 59],
    "lp": [738, 737, 740],
    "padp": [18, 16, 15],
    "padp944": [24, 14, 14],
    "pap": [5, 4, 4],
    "pp": [3, 3, 3]
}
delta_100 = {
    "adp": [9, 13, 9],
    "lp": [755, 748, 749],
    "padp": [18, 18, 19],
    "padp944": [15, 15, 16],
    "pap": [9, 8, 9],
    "pp": [6, 6, 4]
}
t_closeness_100 = {
    "adp": [70, 64, 68],
    "lp": [2164, 2161, 2172],
    "padp": [99, 94, 89],
    "padp944": [89, 87, 90],
    "pap": [28, 29, 29],
    "pp": [18, 16, 13]
}
population_100 = {
    "adp": [11, 11, 15],
    "lp": [1405, 1394, 1393],
    "padp": [28, 27, 28],
    "padp944": [17, 17, 18],
    "pap": [7, 7, 6],
    "pp": [3, 3, 4]
}

###### 10.000 Patients Dataset
summary_10000 = {
    "overall": [202604, 205994, 188339],
    "adp": [4055, 3970, 3926],
    "padp": [10007, 10045, 9780],
    "padp944": [12857, 12891, 11971 ],
    "pap": [129567, 132171, 120315],
    "pp": [45739, 46637, 42043]
}

sample_10000 = {
    "adp": [346, 318, 312],
    "padp": [1222, 1294, 1239],
    "padp944": [853, 857, 789],
    "pap": [250, 232, 253],
    "pp": [58, 38, 36]
}
delta_10000 = {
    "adp": [318, 297, 302],
    "padp": [1367, 1215, 1204],
    "padp944": [1017, 921, 923],
    "pap": [295, 304, 306],
    "pp": [67, 63, 61]
}
t_closeness_10000 = {
    "adp": [2939, 2921, 2885],
    "padp": [5171, 5333, 5108],
    "padp944": [9407, 9523, 8828],
    "pap": [128590, 131210, 119326],
    "pp": [45526, 46434, 41848]
}
population_10000 = {
    "adp": [354, 354, 337],
    "padp": [2233, 2177, 2205],
    "padp944": [1561, 1572, 1413],
    "pap": [417, 376, 381],
    "pp": [70, 67, 68]
}

##### 100.000 Patients Dataset
summary_100000 = {
    "overall": [228940, 228582, 228253],
    "adp": [23119, 23333, 23516],
    "padp": [87952, 87848, 87507],
    "padp944": [107845, 107274, 106636],
    "pap": [7894, 7908, 8409],
    "pp": [1864, 1951, 1928]
}

sample_100000 = {
    "adp": [2859, 2730, 2702],
    "padp": [11634, 11652, 11654],
    "padp944": [8597, 8750, 8627],
    "pap": [2044, 2007, 2052],
    "pp": [518, 567, 527]
}
delta_100000 = {
    "adp": [2856, 2840, 2802],
    "padp": [11933, 12352, 11928],
    "padp944": [9402, 10019, 9332],
    "pap": [2261, 2344, 2522],
    "pp": [629, 616, 653]
}
t_closeness_100000 = {
    "adp": [13794, 13948, 14462],
    "padp": [43047, 42533, 42453],
    "padp944": [75769, 74364, 74420],
    "pap": ['Timeout'],
    "pp": ['Timeout']
}
population_100000 = {
    "adp": [3533, 3733, 3467],
    "padp": [21324, 21295, 21456],
    "padp944": [14236, 14099, 14218],
    "pap": [3577, 3545, 3820],
    "pp": [705, 751, 733]
}

dataset_names = ['100 Patients', '10.000 Patients', '100.000 Patients']

summary_100_df = pd.DataFrame({
    "policy_name": ["summary", "adp", "lp", "padp", "padp944", "pap", "pp"],
    "speed_ms": [np.mean(summary_100["overall"]), np.mean(summary_100["adp"]), np.mean(summary_100["lp"]), np.mean(summary_100["padp"]), np.mean(summary_100["padp944"]), np.mean(summary_100["pap"]), np.mean(summary_100["pp"])]
})

summary_10000_df = pd.DataFrame({
    "policy_name": ["summary", "adp", "padp", "padp944", "pap", "pp"],
    "speed_ms": [np.mean(summary_10000["overall"]), np.mean(summary_10000["adp"]), np.mean(summary_10000["padp"]), np.mean(summary_10000["padp944"]), np.mean(summary_10000["pap"]), np.mean(summary_10000["pp"])]
})
summary_100000_df = pd.DataFrame({
    "policy_name": ["summary", "adp", "padp", "padp944", "pap", "pp"],
    "speed_ms": [np.mean(summary_100000["overall"]), np.mean(summary_100000["adp"]), np.mean(summary_100000["padp"]), np.mean(summary_100000["padp944"]), np.mean(summary_100000["pap"]), np.mean(summary_100000["pp"])]
})

delta_100_df = pd.DataFrame({
    "policy_name": ["adp", "lp", "padp", "padp944", "pap", "pp"],
    "speed_ms": [np.mean(delta_100["adp"]), np.mean(delta_100["lp"]), np.mean(delta_100["padp"]), np.mean(delta_100["padp944"]), np.mean(delta_100["pap"]), np.mean(delta_100["pp"])]
})

delta_10000_df = pd.DataFrame({
    "policy_name": ["adp", "padp", "padp944", "pap", "pp"],
    "speed_ms": [np.mean(delta_10000["adp"]), np.mean(delta_10000["padp"]), np.mean(delta_10000["padp944"]), np.mean(delta_10000["pap"]), np.mean(delta_10000["pp"])]
})
delta_100000_df = pd.DataFrame({
    "policy_name": ["adp", "padp", "padp944", "pap", "pp"],
    "speed_ms": [np.mean(delta_100000["adp"]), np.mean(delta_100000["padp"]), np.mean(delta_100000["padp944"]), np.mean(delta_100000["pap"]), np.mean(delta_100000["pp"])]
})

sample_100_df = pd.DataFrame({
    "policy_name": ["adp", "lp", "padp", "padp944", "pap", "pp"],
    "speed_ms": [np.mean(sample_100["adp"]), np.mean(sample_100["lp"]), np.mean(sample_100["padp"]), np.mean(sample_100["padp944"]), np.mean(sample_100["pap"]), np.mean(sample_100["pp"])]
})

sample_10000_df = pd.DataFrame({
    "policy_name": ["adp", "padp", "padp944", "pap", "pp"],
    "speed_ms": [np.mean(sample_10000["adp"]), np.mean(sample_10000["padp"]), np.mean(sample_10000["padp944"]), np.mean(sample_10000["pap"]), np.mean(sample_10000["pp"])]
})
sample_100000_df = pd.DataFrame({
    "policy_name": ["adp", "padp", "padp944", "pap", "pp"],
    "speed_ms": [np.mean(sample_100000["adp"]), np.mean(sample_100000["padp"]), np.mean(sample_100000["padp944"]), np.mean(sample_100000["pap"]), np.mean(sample_100000["pp"])]
})

population_100_df = pd.DataFrame({
    "policy_name": ["adp", "lp", "padp", "padp944", "pap", "pp"],
    "speed_ms": [np.mean(population_100["adp"]), np.mean(population_100["lp"]), np.mean(population_100["padp"]), np.mean(population_100["padp944"]), np.mean(population_100["pap"]), np.mean(population_100["pp"])]
})

population_10000_df = pd.DataFrame({
    "policy_name": ["adp", "padp", "padp944", "pap", "pp"],
    "speed_ms": [np.mean(population_10000["adp"]), np.mean(population_10000["padp"]), np.mean(population_10000["padp944"]), np.mean(population_10000["pap"]), np.mean(population_10000["pp"])]
})
population_100000_df = pd.DataFrame({
    "policy_name": ["adp", "padp", "padp944", "pap", "pp"],
    "speed_ms": [np.mean(population_100000["adp"]), np.mean(population_100000["padp"]), np.mean(population_100000["padp944"]), np.mean(population_100000["pap"]), np.mean(population_100000["pp"])]
})

t_closeness_100_df = pd.DataFrame({
    "policy_name": ["adp", "lp", "padp", "padp944", "pap", "pp"],
    "speed_ms": [np.mean(t_closeness_100["adp"]), np.mean(t_closeness_100["lp"]), np.mean(t_closeness_100["padp"]), np.mean(t_closeness_100["padp944"]), np.mean(t_closeness_100["pap"]), np.mean(t_closeness_100["pp"])]
})

t_closeness_10000_df = pd.DataFrame({
    "policy_name": ["adp", "padp", "padp944", "pap", "pp"],
    "speed_ms": [np.mean(t_closeness_10000["adp"]), np.mean(t_closeness_10000["padp"]), np.mean(t_closeness_10000["padp944"]), np.mean(t_closeness_10000["pap"]), np.mean(t_closeness_10000["pp"])]
})
t_closeness_100000_df = pd.DataFrame({
    "policy_name": ["adp", "padp", "padp944"],
    "speed_ms": [np.mean(t_closeness_100000["adp"]), np.mean(t_closeness_100000["padp"]), np.mean(t_closeness_100000["padp944"])]
})

# Create figure with 3 subplots
fig, axes = plt.subplots(1, 3, figsize=(18, 6))

# List of dataframes
dataframes = [t_closeness_100_df, t_closeness_10000_df, t_closeness_100000_df]

# Color palette
colors = ['#2E86AB', '#A23B72', '#F18F01', '#C73E1D', '#6A994E', 'red', 'orange']

# Plot each dataset
for idx, (df, ax, dataset_name) in enumerate(zip(dataframes, axes, dataset_names)):
    # Create bar plot
    bars = ax.bar(df['policy_name'], df['speed_ms'],
                  color=colors[:len(df)],
                  edgecolor='black',
                  linewidth=1.2,
                  alpha=0.8)

    # Customize subplot
    ax.set_xlabel('Policy Name', fontsize=13, fontweight='bold')
    ax.set_ylabel('Speed (ms)', fontsize=13, fontweight='bold')
    ax.set_title(dataset_name, fontsize=15, fontweight='bold', pad=15)
    ax.grid(axis='y', alpha=0.3, linestyle='--', linewidth=0.7)
    ax.set_axisbelow(True)

    # Rotate x-axis labels if needed
    ax.tick_params(axis='x', rotation=45)

    # Add value labels on top of bars
    for bar in bars:
        height = bar.get_height()
        ax.text(bar.get_x() + bar.get_width()/2., height,
                f'{height:.1f}',
                ha='center', va='bottom', fontsize=9, fontweight='bold')

    # Add statistics text box
    mean_speed = df['speed_ms'].mean()
    min_speed = df['speed_ms'].min()
    max_speed = df['speed_ms'].max()

    stats_text = f'Avg: {mean_speed:.1f} ms\nMin: {min_speed:.1f} ms\nMax: {max_speed:.1f} ms'
    # ax.text(0.02, 0.98, stats_text, transform=ax.transAxes,
    #         fontsize=9, verticalalignment='top', horizontalalignment='left',
    #         bbox=dict(boxstyle='round', facecolor='wheat', alpha=0.7))

# Add main title
# fig.suptitle('Offline Policy Auditing Performance Comparison Across Datasets',
#              fontsize=16, fontweight='bold', y=1.02)

# Adjust layout
plt.tight_layout()

# Save the plot
output_path = 'plots/t-closeness_policy_auditing_plot.png'
plt.savefig(output_path, dpi=300, bbox_inches='tight')
print(f"Plot saved to: {output_path}")

# Print summary statistics
print("\n=== Summary Statistics ===")
for dataset_name, df in zip(dataset_names, dataframes):
    print(f"\n{dataset_name}:")
    print(f"  Avg: {df['speed_ms'].mean():.2f} ms")
    print(f"  Min:  {df['speed_ms'].min():.2f} ms")
    print(f"  Max:  {df['speed_ms'].max():.2f} ms")
    print(f"  Median:    {df['speed_ms'].median():.2f} ms")

# Display the plot
plt.show()
