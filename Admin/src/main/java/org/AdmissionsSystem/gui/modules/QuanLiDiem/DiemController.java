package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import java.awt.Component;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DiemController {
	private final DiemService diemService;
	private final ImportExcel importExcel;

	public DiemController() {
		this(new DiemService(), new ImportExcel());
	}

	DiemController(DiemService diemService, ImportExcel importExcel) {
		this.diemService = diemService;
		this.importExcel = importExcel;
	}

	public List<DiemService.DiemRecord> getDanhSach(String searchText, String loaiDiem, String mon) {
		return diemService.query(searchText, loaiDiem, mon);
	}

	public Optional<DiemService.DiemRecord> getById(int id) {
		return diemService.findById(id);
	}

	public DiemService.DiemRecord them(DiemService.DiemRecordInput input) {
		return diemService.add(input);
	}

	public Optional<DiemService.DiemRecord> capNhat(int id, DiemService.DiemRecordInput input) {
		return diemService.update(id, input);
	}

	public boolean xoa(int id) {
		return diemService.delete(id);
	}

	public int importExcel(Component parent) throws IOException {
		List<DiemService.DiemRecordInput> importedRows = importExcel.chooseAndRead(parent);
		return diemService.importRows(importedRows);
	}

	public Map<String, Double> thongKeTheoLoai(List<DiemService.DiemRecord> source) {
		return diemService.averageByLoaiDiem(source);
	}

	public Map<String, Double> thongKeTheoMon(List<DiemService.DiemRecord> source) {
		return diemService.averageByMon(source);
	}

	public String buildThongKeText(String tieuDe, Map<String, Double> stats) {
		Map<String, Double> safeStats = stats == null ? Collections.emptyMap() : stats;
		DecimalFormat decimalFormat = new DecimalFormat("0.##");

		StringBuilder sb = new StringBuilder();
		sb.append(tieuDe).append(':').append('\n');

		if (safeStats.isEmpty()) {
			sb.append("- Không có dữ liệu");
			return sb.toString();
		}

		for (Map.Entry<String, Double> entry : safeStats.entrySet()) {
			sb.append("- ")
					.append(entry.getKey())
					.append(": ")
					.append(decimalFormat.format(entry.getValue()))
					.append('\n');
		}

		return sb.toString().trim();
	}
}
