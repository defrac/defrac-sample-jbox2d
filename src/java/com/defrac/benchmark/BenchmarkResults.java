package com.defrac.benchmark;

import defrac.util.ArrayUtil;

/**
 *
 */
public final class BenchmarkResults {
  private double[] scores = new double[256];
  private double best = Double.POSITIVE_INFINITY;
  private double sum = 0.0;
  private double mean;
  private double error;
  private int numScores;

  public void add(double timeMs) {
    if(numScores == scores.length) {
      scores = ArrayUtil.realloc(scores, scores.length << 1);
    }

    scores[numScores++] = timeMs;
    best = Math.min(timeMs, best);
    sum += timeMs;
    mean = sum / (double)numScores;
    double standardDeviation = computeStandardDeviation();
    double standardError = standardDeviation / Math.sqrt(numScores);
    error = (computeTDistribution() * standardError / mean) * 100.0;
  }

  private double computeStandardDeviation() {
    double deltaSquaredSum = 0.0;
    for(int i = 0; i < numScores; ++i) {
      double delta = scores[i] - mean;
      deltaSquaredSum += delta * delta;
    }
    double variance = deltaSquaredSum / (scores.length - 1);
    return Math.sqrt(variance);
  }

  private double computeTDistribution() {
    if (numScores >= 474) return 1.96;
    else if (numScores >= 160) return 1.97;
    else if (numScores >= TABLE.length) return 1.98;
    else return TABLE[numScores];
  }

  public double bestMs() {
    return best;
  }

  public double meanMs() {
    return mean;
  }

  public double errorPercent() {
    return error;
  }

  private static final double[] TABLE = {
      Double.NaN, Double.NaN, 12.71,
      4.30, 3.18, 2.78, 2.57, 2.45, 2.36, 2.31, 2.26, 2.23, 2.20, 2.18, 2.16,
      2.14, 2.13, 2.12, 2.11, 2.10, 2.09, 2.09, 2.08, 2.07, 2.07, 2.06, 2.06,
      2.06, 2.05, 2.05, 2.05, 2.04, 2.04, 2.04, 2.03, 2.03, 2.03, 2.03, 2.03,
      2.02, 2.02, 2.02, 2.02, 2.02, 2.02, 2.02, 2.01, 2.01, 2.01, 2.01, 2.01,
      2.01, 2.01, 2.01, 2.01, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00,
      2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 1.99, 1.99, 1.99, 1.99, 1.99,
      1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99,
      1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99, 1.99 };
}
