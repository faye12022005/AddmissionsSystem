package org.AdmissionsSystem.util;

public class TinhDiemThxtUtil {
	/**
	 * Tính điểm tổ hợp xét tuyển theo thang 30 dựa trên điểm các môn và hệ số tương
	 * ứng.
	 * 
	 * @param diem1 Điểm môn 1
	 * @param heso1 Hệ số môn 1
	 * @param diem2 Điểm môn 2
	 * @param heso2 Hệ số môn 2
	 * @param diem3 Điểm môn 3
	 * @param heso3 Hệ số môn 3
	 * @return Điểm xét tuyển đã quy về thang 30
	 */
	public static double tinhDTHXT(double diem1, int heso1,
			double diem2, int heso2,
			double diem3, int heso3) {

		// Kiểm tra điểm hợp lệ
		if (diem1 < 0 || diem1 > 10 || diem2 < 0 || diem2 > 10 || diem3 < 0 || diem3 > 10) {
			throw new IllegalArgumentException("Điểm phải nằm trong khoảng 0 - 10");
		}

		// Kiểm tra hệ số hợp lệ
		if (heso1 <= 0 || heso2 <= 0 || heso3 <= 0) {
			throw new IllegalArgumentException("Hệ số phải lớn hơn 0");
		}

		double tongDiemTrongSo = (diem1 * heso1) + (diem2 * heso2) + (diem3 * heso3);
		int tongHeSo = heso1 + heso2 + heso3;

		// Quy về thang 30: (tổng điểm trọng số) * 3 / tổng hệ số
		return (tongDiemTrongSo * 3.0) / tongHeSo;
	}
}
