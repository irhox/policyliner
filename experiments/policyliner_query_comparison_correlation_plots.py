from __future__ import annotations

import os

import matplotlib.pyplot as plt
import pandas as pd

if __name__ == "__main__":

    simple_true_counts = [4, 4, 2, 2, 4, 6, 3, 3, 3, 3, 3, 3, 4, 4]
    complex_true_counts = [3,3,3,3,2,2,3,2,2,2,2,3,2,2,2,2,2,2,2,2,2,2,2,2]

    simple_algorithms_results = {
        "Custom Algorithm": [4,4,2,2,2,6,2,2,3,3,3,1,2,4],
        "Levenshtein Distance": [0,4,2,2,2,2,0,0,0,0,1,1,0,0],
        "String Comparison": [0,0,0,0,0,0,1,3,3,3,3,3,4,4],
    }

    complex_algorithms_results = {
        "Custom Algorithm": [2,3,2,3,2,0,0,0,0,2,0,3,2,0,0,0,2,2,2,2,0,2,0,2],
        "Levenshtein Distance": [2,0,2,2,2,0,0,2,2,2,0,0,2,0,2,2,0,0,0,0,0,0,0,0],
        "String Comparison": [0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,2,2,2,2,2,2,2],
    }

    simple_df = pd.DataFrame({"true": simple_true_counts, "algo": simple_algorithms_results.get("Custom Algorithm")})
    simple_r = simple_df.corr(method="pearson").loc["true", "algo"]

    simple_df_lev = pd.DataFrame({"true": simple_true_counts, "algo": simple_algorithms_results.get("Levenshtein Distance")})
    simple_r_lev = simple_df_lev.corr(method="pearson").loc["true", "algo"]

    simple_sdf = pd.DataFrame({"true": simple_true_counts, "algo": simple_algorithms_results.get("String Comparison")})
    simple_sr = simple_sdf.corr(method="pearson").loc["true", "algo"]


    complex_df = pd.DataFrame({"true": complex_true_counts, "algo": complex_algorithms_results.get("Custom Algorithm")})
    complex_r = complex_df.corr(method="pearson").loc["true", "algo"]

    complex_df_lev = pd.DataFrame({"true": complex_true_counts, "algo": complex_algorithms_results.get("Levenshtein Distance")})
    complex_r_lev = complex_df_lev.corr(method="pearson").loc["true", "algo"]

    complex_sdf = pd.DataFrame({"true": complex_true_counts, "algo": complex_algorithms_results.get("String Comparison")})
    complex_sr = complex_sdf.corr(method="pearson").loc["true", "algo"]

    print(f"Correlation between simple true and algo: {simple_r:.3f}")
    print(f"Correlation between simple true and algo levenshtein: {simple_r_lev:.3f}")
    print(f"Correlation between simple true and algo string: {simple_sr:.3f}")
    labels = ['Custom Algorithm', 'Levenshtein Distance', 'String Comparison']
    values = [simple_r, simple_r_lev, simple_sr]
    colors = ['green', 'red', 'blue']
    # Plot
    plt.bar(labels, values, color=colors)

    # Labels and title
    plt.xlabel('Comparison Algorithm')
    plt.ylabel('Correlation Value')
    plt.title('Correlation of Evaluated Simple Queries to ground truth')

    out_dir = os.path.dirname(os.path.abspath(__file__))
    plots_dir = os.path.join(out_dir, "plots")
    try:
        os.makedirs(plots_dir, exist_ok=True)
    except Exception:
        pass
    png_path = os.path.join(plots_dir, "online_query_auditing_simple_similarity_correlation.png")
    plt.savefig(png_path, dpi=200)

    # Show plot
    plt.show()


    print(f"Correlation between complex true and algo: {complex_r:.3f}")
    print(f"Correlation between complex true and algo levenshtein: {complex_r_lev:.3f}")
    print(f"Correlation between complex true and algo string: {complex_sr:.3f}")
    labels = ['Custom Algorithm', 'Levenshtein Distance', 'String Comparison']
    values = [complex_r, complex_r_lev, complex_sr]
    colors = ['green', 'red', 'blue']
    # Plot
    plt.bar(labels, values, color=colors)

    # Labels and title
    plt.xlabel('Comparison Algorithm')
    plt.ylabel('Correlation Value')
    plt.title('Correlation of Evaluated Complex Queries to ground truth')

    out_dir = os.path.dirname(os.path.abspath(__file__))
    plots_dir = os.path.join(out_dir, "plots")
    try:
        os.makedirs(plots_dir, exist_ok=True)
    except Exception:
        pass
    png_path = os.path.join(plots_dir, "online_query_auditing_complex_similarity_correlation.png")
    plt.savefig(png_path, dpi=200)

    # Show plot
    plt.show()

