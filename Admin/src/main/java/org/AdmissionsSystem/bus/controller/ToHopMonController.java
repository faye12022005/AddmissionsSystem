package org.AdmissionsSystem.bus.controller;

import java.awt.Component;
import java.io.IOException;
import java.util.List;
import org.AdmissionsSystem.models.XtTohopMonthi;
import org.AdmissionsSystem.bus.service.ToHopMonService;
import org.AdmissionsSystem.gui.components.ImportExcel;

public class ToHopMonController {
    private final ToHopMonService service = new ToHopMonService();
    private final ImportExcel importExcel = new ImportExcel();

    public List<XtTohopMonthi> taiDuLieu() {
        return service.getAll();
    }

    public List<XtTohopMonthi> timKiem(String keyword) {
        return service.search(keyword);
    }

    public void xuLySuKienThem(XtTohopMonthi entity) {
        service.add(entity);
    }

    public void xuLySuKienCapNhat(XtTohopMonthi entity) {
        // The service now uses findById inside update
        service.update(entity);
    }

    public void xuLySuKienXoaTheoId(Integer id) {
        service.delete(id);
    }

    public XtTohopMonthi layTheoId(Integer id) {
        return service.findById(id);
    }

    public XtTohopMonthi layTheoMaTohop(String ma) {
        return service.findByMaToHop(ma);
    }

    public long demTatCa() {
        return service.count();
    }

    public ToHopMonService.ImportPreview previewImport(Component parent) throws IOException {
        List<Object[]> rows = importExcel.chooseAndRead(
                parent,
                "Chọn file Excel tổ hợp môn",
                service.getImportColumns(),
                service.getImportAliases());
        return service.previewImport(rows);
    }

    public int commitImport(ToHopMonService.ImportPreview preview) {
        if (preview == null || preview.validRows() == null || preview.validRows().isEmpty()) {
            return 0;
        }
        return service.importRows(preview.validRows());
    }
}
