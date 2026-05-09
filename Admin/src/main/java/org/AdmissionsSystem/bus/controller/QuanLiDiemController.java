package org.AdmissionsSystem.bus.controller;

import java.awt.Component;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.AdmissionsSystem.gui.components.ImportExcel;
import org.AdmissionsSystem.bus.service.QuanLiDiem.QuanLiDiemService;
import org.AdmissionsSystem.bus.service.QuanLiDiem.QuanLiDiemVSATService;

public class QuanLiDiemController {
	private final QuanLiDiemService thptDgnlService = new QuanLiDiemService();
	private final QuanLiDiemVSATService vsatService = new QuanLiDiemVSATService();
	private final ImportExcel importExcel = new ImportExcel();

	public List<QuanLiDiemService.DiemRecord> getDanhSach(String searchText, String loaiDiem) {
		return thptDgnlService.query(searchText, loaiDiem);
	}

	public QuanLiDiemService.DiemRecord them(QuanLiDiemService.DiemInput input) {
		return thptDgnlService.add(input);
	}

	public Optional<QuanLiDiemService.DiemRecord> capNhat(QuanLiDiemService.DiemRecord existing,
			QuanLiDiemService.DiemInput input) {
		return thptDgnlService.update(existing, input);
	}

	public boolean xoa(QuanLiDiemService.DiemRecord existing) {
		return thptDgnlService.delete(existing);
	}

	public QuanLiDiemService.ImportPreview previewImport(Component parent) throws IOException {
		List<Object[]> rows = importExcel.chooseAndRead(
				parent,
				"Chọn file Excel điểm thí sinh",
				thptDgnlService.getImportColumns(),
				thptDgnlService.getImportAliases());
		return thptDgnlService.previewImport(rows);
	}

	public int commitImport(QuanLiDiemService.ImportPreview preview) {
		if (preview == null || preview.validRows() == null || preview.validRows().isEmpty()) {
			return 0;
		}
		return thptDgnlService.importRows(preview.validRows());
	}

	public List<QuanLiDiemVSATService.VsatRecord> getDanhSachVsat(String searchText) {
		return vsatService.query(searchText);
	}

	public QuanLiDiemVSATService.VsatRecord themVsat(QuanLiDiemVSATService.VsatInput input) {
		return vsatService.add(input);
	}

	public Optional<QuanLiDiemVSATService.VsatRecord> capNhatVsat(QuanLiDiemVSATService.VsatRecord existing,
			QuanLiDiemVSATService.VsatInput input) {
		return vsatService.update(existing, input);
	}

	public boolean xoaVsat(QuanLiDiemVSATService.VsatRecord existing) {
		return vsatService.delete(existing);
	}

	public QuanLiDiemVSATService.ImportPreview previewImportVsat(Component parent) throws IOException {
		List<Object[]> rows = importExcel.chooseAndRead(
				parent,
				"Chọn file Excel điểm VSAT",
				vsatService.getImportColumns(),
				vsatService.getImportAliases());
		return vsatService.previewImport(rows);
	}

	public int commitImportVsat(QuanLiDiemVSATService.ImportPreview preview) {
		if (preview == null || preview.validRows() == null || preview.validRows().isEmpty()) {
			return 0;
		}
		return vsatService.importRows(preview.validRows());
	}

}
