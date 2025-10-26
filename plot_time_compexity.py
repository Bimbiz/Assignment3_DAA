import os
import argparse
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from pathlib import Path

# Set style for publication-quality plots
plt.style.use('seaborn-v0_8-darkgrid')
sns.set_palette("husl")


def load_csv_data(filepath):
    """Load and validate CSV data."""
    try:
        df = pd.read_csv(filepath)
        print(f"✓ Loaded {filepath}: {len(df)} rows, {len(df.columns)} columns")
        print(f"  Columns: {', '.join(df.columns)}")
        return df
    except Exception as e:
        print(f"✗ Error loading {filepath}: {e}")
        return None


def plot_execution_time_comparison(df, output_dir):
    """Plot execution time: Prim vs Kruskal"""
    fig, ax = plt.subplots(figsize=(10, 6))

    # Separate Prim and Kruskal data
    prim_data = df[df['Algorithm'] == 'Prim'].copy()
    kruskal_data = df[df['Algorithm'] == 'Kruskal'].copy()

    # Sort by vertices
    prim_data = prim_data.sort_values('Vertices')
    kruskal_data = kruskal_data.sort_values('Vertices')

    # Plot lines
    ax.plot(prim_data['Vertices'], prim_data['Time(ms)'],
            marker='o', linewidth=2, markersize=8, label="Prim's Algorithm", color='#2E86AB')
    ax.plot(kruskal_data['Vertices'], kruskal_data['Time(ms)'],
            marker='s', linewidth=2, markersize=8, label="Kruskal's Algorithm", color='#A23B72')

    ax.set_xlabel('Number of Vertices', fontsize=12, fontweight='bold')
    ax.set_ylabel('Execution Time (ms)', fontsize=12, fontweight='bold')
    ax.set_title('Execution Time Comparison: Prim vs Kruskal', fontsize=14, fontweight='bold')
    ax.legend(fontsize=11, loc='best')
    ax.grid(True, alpha=0.3)

    plt.tight_layout()
    output_path = os.path.join(output_dir, 'execution_time_comparison.png')
    plt.savefig(output_path, dpi=300, bbox_inches='tight')
    print(f"✓ Saved: {output_path}")
    plt.close()


def plot_operations_comparison(df, output_dir):
    """Plot operation count: Prim vs Kruskal"""
    fig, ax = plt.subplots(figsize=(10, 6))

    prim_data = df[df['Algorithm'] == 'Prim'].sort_values('Vertices')
    kruskal_data = df[df['Algorithm'] == 'Kruskal'].sort_values('Vertices')

    ax.plot(prim_data['Vertices'], prim_data['Operations'],
            marker='o', linewidth=2, markersize=8, label="Prim's Algorithm", color='#2E86AB')
    ax.plot(kruskal_data['Vertices'], kruskal_data['Operations'],
            marker='s', linewidth=2, markersize=8, label="Kruskal's Algorithm", color='#A23B72')

    ax.set_xlabel('Number of Vertices', fontsize=12, fontweight='bold')
    ax.set_ylabel('Number of Operations', fontsize=12, fontweight='bold')
    ax.set_title('Operation Count Comparison: Prim vs Kruskal', fontsize=14, fontweight='bold')
    ax.legend(fontsize=11, loc='best')
    ax.grid(True, alpha=0.3)

    plt.tight_layout()
    output_path = os.path.join(output_dir, 'operations_comparison.png')
    plt.savefig(output_path, dpi=300, bbox_inches='tight')
    print(f"✓ Saved: {output_path}")
    plt.close()


def plot_mst_cost_verification(df, output_dir):
    """Verify both algorithms produce same MST cost"""
    fig, ax = plt.subplots(figsize=(10, 6))

    prim_data = df[df['Algorithm'] == 'Prim'].sort_values('Vertices')
    kruskal_data = df[df['Algorithm'] == 'Kruskal'].sort_values('Vertices')

    ax.plot(prim_data['Vertices'], prim_data['MST Cost'],
            marker='o', linewidth=2, markersize=8, label="Prim's Algorithm", color='#2E86AB')
    ax.plot(kruskal_data['Vertices'], kruskal_data['MST Cost'],
            marker='s', linewidth=2, markersize=8, label="Kruskal's Algorithm",
            color='#A23B72', linestyle='--', alpha=0.7)

    ax.set_xlabel('Number of Vertices', fontsize=12, fontweight='bold')
    ax.set_ylabel('MST Total Cost', fontsize=12, fontweight='bold')
    ax.set_title('MST Cost Verification (Both Algorithms)', fontsize=14, fontweight='bold')
    ax.legend(fontsize=11, loc='best')
    ax.grid(True, alpha=0.3)

    # Add note if costs match
    if np.allclose(prim_data['MST Cost'].values, kruskal_data['MST Cost'].values):
        ax.text(0.5, 0.95, '✓ All MST costs match (correctness verified)',
                transform=ax.transAxes, ha='center', va='top',
                bbox=dict(boxstyle='round', facecolor='lightgreen', alpha=0.8),
                fontsize=10, fontweight='bold')

    plt.tight_layout()
    output_path = os.path.join(output_dir, 'mst_cost_verification.png')
    plt.savefig(output_path, dpi=300, bbox_inches='tight')
    print(f"✓ Saved: {output_path}")
    plt.close()


def plot_performance_ratio(df, output_dir):
    """Plot performance ratio (Kruskal time / Prim time)"""
    fig, ax = plt.subplots(figsize=(10, 6))

    # Group by graph and calculate ratio
    grouped = df.groupby('Graph')
    ratios = []
    vertices = []

    for graph_name, group in grouped:
        prim_time = group[group['Algorithm'] == 'Prim']['Time(ms)'].values[0]
        kruskal_time = group[group['Algorithm'] == 'Kruskal']['Time(ms)'].values[0]
        v = group['Vertices'].values[0]

        if prim_time > 0:
            ratio = kruskal_time / prim_time
            ratios.append(ratio)
            vertices.append(v)

    # Sort by vertices
    sorted_pairs = sorted(zip(vertices, ratios))
    vertices, ratios = zip(*sorted_pairs)

    colors = ['green' if r < 1 else 'red' for r in ratios]
    ax.bar(range(len(vertices)), ratios, color=colors, alpha=0.7, edgecolor='black')
    ax.axhline(y=1.0, color='black', linestyle='--', linewidth=2, label='Equal Performance')

    ax.set_xlabel('Graph (by vertex count)', fontsize=12, fontweight='bold')
    ax.set_ylabel('Performance Ratio (Kruskal / Prim)', fontsize=12, fontweight='bold')
    ax.set_title('Performance Ratio: Kruskal vs Prim\n(< 1 = Kruskal faster, > 1 = Prim faster)',
                 fontsize=14, fontweight='bold')
    ax.set_xticks(range(len(vertices)))
    ax.set_xticklabels([f'{v}v' for v in vertices], rotation=45)
    ax.legend(fontsize=11)
    ax.grid(True, alpha=0.3, axis='y')

    plt.tight_layout()
    output_path = os.path.join(output_dir, 'performance_ratio.png')
    plt.savefig(output_path, dpi=300, bbox_inches='tight')
    print(f"✓ Saved: {output_path}")
    plt.close()


def plot_edge_density_impact(df, output_dir):
    """Plot how edge density affects performance"""
    fig, ax = plt.subplots(figsize=(10, 6))

    # Calculate edge density (edges / max_possible_edges)
    df = df.copy()
    df['Edge_Density'] = df['Edges'] / (df['Vertices'] * (df['Vertices'] - 1) / 2)

    prim_data = df[df['Algorithm'] == 'Prim']
    kruskal_data = df[df['Algorithm'] == 'Kruskal']

    ax.scatter(prim_data['Edge_Density'], prim_data['Time(ms)'],
               s=100, alpha=0.6, label="Prim's Algorithm", color='#2E86AB', marker='o')
    ax.scatter(kruskal_data['Edge_Density'], kruskal_data['Time(ms)'],
               s=100, alpha=0.6, label="Kruskal's Algorithm", color='#A23B72', marker='s')

    ax.set_xlabel('Edge Density (edges / max possible edges)', fontsize=12, fontweight='bold')
    ax.set_ylabel('Execution Time (ms)', fontsize=12, fontweight='bold')
    ax.set_title('Impact of Edge Density on Performance', fontsize=14, fontweight='bold')
    ax.legend(fontsize=11, loc='best')
    ax.grid(True, alpha=0.3)

    plt.tight_layout()
    output_path = os.path.join(output_dir, 'edge_density_impact.png')
    plt.savefig(output_path, dpi=300, bbox_inches='tight')
    print(f"✓ Saved: {output_path}")
    plt.close()


def plot_all_in_one(df, output_dir):
    """Create a comprehensive 2x2 subplot figure"""
    fig, ((ax1, ax2), (ax3, ax4)) = plt.subplots(2, 2, figsize=(16, 12))

    prim_data = df[df['Algorithm'] == 'Prim'].sort_values('Vertices')
    kruskal_data = df[df['Algorithm'] == 'Kruskal'].sort_values('Vertices')

    # Plot 1: Execution Time
    ax1.plot(prim_data['Vertices'], prim_data['Time(ms)'],
             marker='o', linewidth=2, markersize=8, label="Prim", color='#2E86AB')
    ax1.plot(kruskal_data['Vertices'], kruskal_data['Time(ms)'],
             marker='s', linewidth=2, markersize=8, label="Kruskal", color='#A23B72')
    ax1.set_xlabel('Vertices', fontweight='bold')
    ax1.set_ylabel('Time (ms)', fontweight='bold')
    ax1.set_title('(a) Execution Time', fontweight='bold')
    ax1.legend()
    ax1.grid(True, alpha=0.3)

    # Plot 2: Operations
    ax2.plot(prim_data['Vertices'], prim_data['Operations'],
             marker='o', linewidth=2, markersize=8, label="Prim", color='#2E86AB')
    ax2.plot(kruskal_data['Vertices'], kruskal_data['Operations'],
             marker='s', linewidth=2, markersize=8, label="Kruskal", color='#A23B72')
    ax2.set_xlabel('Vertices', fontweight='bold')
    ax2.set_ylabel('Operations', fontweight='bold')
    ax2.set_title('(b) Operation Count', fontweight='bold')
    ax2.legend()
    ax2.grid(True, alpha=0.3)

    # Plot 3: MST Cost
    ax3.plot(prim_data['Vertices'], prim_data['MST Cost'],
             marker='o', linewidth=2, markersize=8, label="Prim", color='#2E86AB')
    ax3.plot(kruskal_data['Vertices'], kruskal_data['MST Cost'],
             marker='s', linewidth=2, markersize=8, label="Kruskal",
             color='#A23B72', linestyle='--', alpha=0.7)
    ax3.set_xlabel('Vertices', fontweight='bold')
    ax3.set_ylabel('MST Cost', fontweight='bold')
    ax3.set_title('(c) MST Cost Verification', fontweight='bold')
    ax3.legend()
    ax3.grid(True, alpha=0.3)

    # Plot 4: Performance Comparison (Bar chart)
    x = np.arange(len(prim_data))
    width = 0.35
    ax4.bar(x - width / 2, prim_data['Time(ms)'], width, label='Prim', color='#2E86AB', alpha=0.8)
    ax4.bar(x + width / 2, kruskal_data['Time(ms)'], width, label='Kruskal', color='#A23B72', alpha=0.8)
    ax4.set_xlabel('Graph Size (Vertices)', fontweight='bold')
    ax4.set_ylabel('Time (ms)', fontweight='bold')
    ax4.set_title('(d) Side-by-Side Comparison', fontweight='bold')
    ax4.set_xticks(x)
    ax4.set_xticklabels(prim_data['Vertices'].values)
    ax4.legend()
    ax4.grid(True, alpha=0.3, axis='y')

    plt.suptitle('MST Algorithm Comparison: Prim vs Kruskal',
                 fontsize=16, fontweight='bold', y=0.995)
    plt.tight_layout()

    output_path = os.path.join(output_dir, 'comprehensive_comparison.png')
    plt.savefig(output_path, dpi=300, bbox_inches='tight')
    print(f"✓ Saved: {output_path}")
    plt.close()


def generate_summary_statistics(df, output_dir):
    """Generate and save summary statistics"""
    summary = []

    prim_data = df[df['Algorithm'] == 'Prim']
    kruskal_data = df[df['Algorithm'] == 'Kruskal']

    summary.append("=" * 60)
    summary.append("MST ALGORITHM PERFORMANCE SUMMARY")
    summary.append("=" * 60)
    summary.append("")

    summary.append("Prim's Algorithm:")
    summary.append(f"  Average Time: {prim_data['Time(ms)'].mean():.2f} ms")
    summary.append(f"  Average Operations: {prim_data['Operations'].mean():.0f}")
    summary.append(f"  Total Graphs: {len(prim_data)}")
    summary.append("")

    summary.append("Kruskal's Algorithm:")
    summary.append(f"  Average Time: {kruskal_data['Time(ms)'].mean():.2f} ms")
    summary.append(f"  Average Operations: {kruskal_data['Operations'].mean():.0f}")
    summary.append(f"  Total Graphs: {len(kruskal_data)}")
    summary.append("")

    # Winner analysis
    prim_wins = sum(prim_data['Time(ms)'].values < kruskal_data['Time(ms)'].values)
    kruskal_wins = sum(kruskal_data['Time(ms)'].values < prim_data['Time(ms)'].values)

    summary.append("Performance Summary:")
    summary.append(f"  Prim faster: {prim_wins} times")
    summary.append(f"  Kruskal faster: {kruskal_wins} times")
    summary.append("")

    # Correctness verification
    costs_match = np.allclose(prim_data['MST Cost'].values, kruskal_data['MST Cost'].values)
    summary.append(f"Correctness: {'✓ All MST costs match' if costs_match else '✗ MST costs differ!'}")
    summary.append("=" * 60)

    summary_text = "\n".join(summary)
    print(summary_text)

    # Save to file
    output_path = os.path.join(output_dir, 'summary_statistics.txt')
    with open(output_path, 'w') as f:
        f.write(summary_text)
    print(f"\n✓ Saved: {output_path}")


def main():
    parser = argparse.ArgumentParser(description='Generate plots for MST algorithm comparison')
    parser.add_argument('--input', '-i', default='data/output/comparison.csv',
                        help='Input CSV file (default: data/output/comparison.csv)')
    parser.add_argument('--output', '-o', default='docs/plots',
                        help='Output directory for plots (default: docs/plots)')

    args = parser.parse_args()

    # Create output directory
    os.makedirs(args.output, exist_ok=True)

    print("=" * 60)
    print("MST RESULTS PLOTTING SCRIPT")
    print("=" * 60)

    # Load data
    df = load_csv_data(args.input)
    if df is None:
        return

    print(f"\nGenerating plots...")
    print("-" * 60)

    # Generate all plots
    try:
        plot_execution_time_comparison(df, args.output)
        plot_operations_comparison(df, args.output)
        plot_mst_cost_verification(df, args.output)
        plot_performance_ratio(df, args.output)
        plot_edge_density_impact(df, args.output)
        plot_all_in_one(df, args.output)
        generate_summary_statistics(df, args.output)

        print("-" * 60)
        print(f"✓ All plots saved to: {args.output}")
        print("=" * 60)

    except Exception as e:
        print(f"\n✗ Error generating plots: {e}")
        import traceback
        traceback.print_exc()


if __name__ == '__main__':
    main()