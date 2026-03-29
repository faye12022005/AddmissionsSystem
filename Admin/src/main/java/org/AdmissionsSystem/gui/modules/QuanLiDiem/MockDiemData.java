package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import java.util.List;

public final class MockDiemData {
	private MockDiemData() {
	}

	public static List<DiemService.DiemRecordInput> createSeedData() {
		return List.of(
				new DiemService.DiemRecordInput("031098001234", "A001", "Nguyễn Văn An", "THPT", "Toán", 8.75),
				new DiemService.DiemRecordInput("031098001234", "A001", "Nguyễn Văn An", "THPT", "Tiếng Anh", 8.5),
				new DiemService.DiemRecordInput("042199004567", "A002", "Trần Minh Châu", "THPT", "Ngữ văn", 7.5),
				new DiemService.DiemRecordInput("078304001111", "B018", "Lê Hoài Nam", "VSAT", "Toán", 23.25),
				new DiemService.DiemRecordInput("078304001111", "B018", "Lê Hoài Nam", "VSAT", "Vật lý", 21.75),
				new DiemService.DiemRecordInput("079105007654", "B021", "Phạm Thị Hạnh", "VSAT", "Hóa học", 24.0),
				new DiemService.DiemRecordInput("055812009876", "DG201", "Hoàng Gia Huy", "ĐGNL", "Năng lực tổng hợp",
						845.0),
				new DiemService.DiemRecordInput("055812001122", "DG208", "Đào Thùy Linh", "ĐGNL", "Năng lực tổng hợp",
						902.0),
				new DiemService.DiemRecordInput("038901223344", "NK010", "Bùi Nhật Quang", "THPT", "Năng khiếu 1",
						7.25),
				new DiemService.DiemRecordInput("038901223344", "NK010", "Bùi Nhật Quang", "THPT", "Năng khiếu 2",
						8.0));
	}
}
