package org.AdmissionsSystem.service.QuanLiDiem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class QuanLiDiemEnglishService {

	public BigDecimal resolveN1Cc(BigDecimal n1Thi, BigDecimal n1Cc) {
		return n1Cc == null ? n1Thi : n1Cc;
	}

	public List<String> validateEnglishScores(BigDecimal n1Thi, BigDecimal n1Cc) {
		List<String> errors = new ArrayList<>();
		validateRange(n1Thi, "N1_THI", errors);
		validateRange(n1Cc, "N1_CC", errors);
		return errors;
	}

	private void validateRange(BigDecimal score, String label, List<String> errors) {
		if (score == null) {
			return;
		}
		if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.TEN) > 0) {
			errors.add("Điểm " + label + " phải nằm trong khoảng từ 0 đến 10");
		}
	}
}
