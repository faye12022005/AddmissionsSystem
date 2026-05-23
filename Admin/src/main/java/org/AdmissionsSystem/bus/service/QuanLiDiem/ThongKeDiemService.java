package org.AdmissionsSystem.bus.service.QuanLiDiem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThongKeDiemService {

	public ScoreSummary summarize(List<BigDecimal> scores) {
		List<BigDecimal> sanitized = sanitize(scores);
		if (sanitized.isEmpty()) {
			return new ScoreSummary(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO);
		}

		sanitized.sort(Comparator.naturalOrder());
		BigDecimal min = sanitized.get(0);
		BigDecimal max = sanitized.get(sanitized.size() - 1);
		BigDecimal mean = average(sanitized);
		BigDecimal stdDev = standardDeviation(sanitized, mean);
		BigDecimal median = median(sanitized);
		BigDecimal mode = mode(sanitized);

		return new ScoreSummary(sanitized.size(), mean, stdDev, median, mode, min, max);
	}

	private List<BigDecimal> sanitize(List<BigDecimal> scores) {
		if (scores == null || scores.isEmpty()) {
			return new ArrayList<>();
		}
		List<BigDecimal> sanitized = new ArrayList<>();
		for (BigDecimal score : scores) {
			if (score != null) {
				sanitized.add(score);
			}
		}
		return sanitized;
	}

	private BigDecimal average(List<BigDecimal> values) {
		if (values.isEmpty()) {
			return BigDecimal.ZERO;
		}
		BigDecimal total = BigDecimal.ZERO;
		for (BigDecimal value : values) {
			total = total.add(value);
		}
		return total.divide(BigDecimal.valueOf(values.size()), 4, RoundingMode.HALF_UP);
	}

	private BigDecimal standardDeviation(List<BigDecimal> values, BigDecimal mean) {
		if (values.isEmpty()) {
			return BigDecimal.ZERO;
		}
		BigDecimal variance = BigDecimal.ZERO;
		for (BigDecimal value : values) {
			BigDecimal diff = value.subtract(mean);
			variance = variance.add(diff.multiply(diff));
		}
		variance = variance.divide(BigDecimal.valueOf(values.size()), 6, RoundingMode.HALF_UP);
		return sqrt(variance, 6);
	}

	private BigDecimal median(List<BigDecimal> values) {
		if (values.isEmpty()) {
			return BigDecimal.ZERO;
		}
		int size = values.size();
		int mid = size / 2;
		if (size % 2 == 0) {
			return values.get(mid - 1).add(values.get(mid)).divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_UP);
		}
		return values.get(mid);
	}

	private BigDecimal mode(List<BigDecimal> values) {
		if (values.isEmpty()) {
			return BigDecimal.ZERO;
		}
		Map<BigDecimal, Integer> counts = new HashMap<>();
		for (BigDecimal value : values) {
			BigDecimal key = value.stripTrailingZeros();
			counts.put(key, counts.getOrDefault(key, 0) + 1);
		}
		BigDecimal mode = null;
		int maxCount = 0;
		for (Map.Entry<BigDecimal, Integer> entry : counts.entrySet()) {
			if (entry.getValue() > maxCount) {
				maxCount = entry.getValue();
				mode = entry.getKey();
			}
		}
		return mode == null ? BigDecimal.ZERO : mode;
	}

	private BigDecimal sqrt(BigDecimal value, int scale) {
		if (value.compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}
		BigDecimal two = BigDecimal.valueOf(2);
		BigDecimal guess = value.divide(two, scale, RoundingMode.HALF_UP);
		for (int i = 0; i < 20; i++) {
			guess = guess.add(value.divide(guess, scale, RoundingMode.HALF_UP)).divide(two, scale,
					RoundingMode.HALF_UP);
		}
		return guess;
	}

	public record ScoreSummary(
			int count,
			BigDecimal mean,
			BigDecimal stdDev,
			BigDecimal median,
			BigDecimal mode,
			BigDecimal min,
			BigDecimal max) {
	}
}
