import os
from typing import List

import matplotlib.pyplot as plt
import numpy as np

# data insertion results
delta: List[float] = [0.015, 0.1667, 0.0139]
tCloseness: List[float] = [0.103, 0.5446, 0.1205]
sampleRatio: List[float] = [0.0, 0.000001, 0.0]
popEstimation: List[float] = [0.000701, 0.000702, 0.000374]

scenarios = [
    "initial state ",
    "policy with new data ",
    "policy after \nadjustment",
]
# Prepare metrics as separate subplots
metrics = [
    ("delta-Presence", delta),
    ("t-Closeness", tCloseness),
    ("Sample Uniqueness Ratio", sampleRatio),
    ("Population Uniqueness Estimation", popEstimation),
]

# data deletion results
delta2: List[float] = [0.132, 0.0172, 0.0147, 0.0208, 0.0156, 0.0192, 0.02, 0.0714, 0.4, 0.2368]
tCloseness2: List[float] = [0.0959, 0.0738, 0.0965, 0.1009, 0.1236, 0.1397, 0.0984, 0.2283, 0.3092, 0.4866]
sampleRatio2: List[float] = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.000014, 0.009479]
popEstimation2: List[float] = [0.000701, 0.000714, 0.000811, 0.000915, 0.001131, 0.001349, 0.001882, 0.004984, 0.014669, 0.163507]

scenarios_deletion = [
    "initial",
    ">1950",
    ">1975",
    ">1985",
    ">1995",
    ">2000",
    ">2005",
    ">2010",
    ">2012",
    ">2015"
]
# Prepare metrics as separate subplots
metrics_deletion = [
    ("delta-Presence", delta2),
    ("t-Closeness", tCloseness2),
    ("Sample Uniqueness Ratio", sampleRatio2),
    ("Population Uniqueness Estimation", popEstimation2),
]

def _annotate_bars(ax, rects):
    """Annotate bars with their numeric values."""
    for r in rects:
        height = r.get_height()
        ax.annotate(
            f"{height:.6f}" if height < 0.01 else f"{height:.4f}",
            xy=(r.get_x() + r.get_width() / 2, height),
            xytext=(0, 3),
            textcoords="offset points",
            ha="center",
            va="bottom",
            fontsize=8,
        )


def create_bar_plot(scenarios, metrics, plot_name):

    x = np.arange(len(scenarios))
    bar_width = 0.5

    fig, axes = plt.subplots(2, 2, figsize=(12, 8), sharex=True)
    axes = axes.flatten()

    cmap = plt.get_cmap("tab10")
    colors = [cmap(i) for i in range(10)]

    for idx, (label, values) in enumerate(metrics):
        ax = axes[idx]
        rects = ax.bar(x, values, bar_width, color=colors[idx])
        #_annotate_bars(ax, rects)

        ax.set_title(label)
        ax.set_ylabel("score")
        ax.grid(axis="y", linestyle=":", alpha=0.5)
        ax.set_xticks(x)
        if idx in (2, 3):
            ax.set_xticklabels(scenarios)
        else:
            ax.set_xticklabels(["" for _ in scenarios], fontsize=12)

    fig.suptitle("Offline Policy auditing effectiveness metrics across scenarios", y=0.98)
    fig.tight_layout(rect=[0, 0, 1, 0.96])

    out_dir = os.path.dirname(os.path.abspath(__file__))
    plots_dir = os.path.join(out_dir, "plots")
    try:
        os.makedirs(plots_dir, exist_ok=True)
    except Exception:
        pass
    png_path = os.path.join(plots_dir, plot_name)
    fig.savefig(png_path, dpi=300)

    return fig, axes


if __name__ == "__main__":
    create_bar_plot(scenarios, metrics, "policy_auditing_effectiveness_subplots.png")
    create_bar_plot(scenarios_deletion, metrics_deletion, "policy_deletion_auditing_effectiveness_subplots.png")
    try:
        plt.show()
    except Exception:
        pass
