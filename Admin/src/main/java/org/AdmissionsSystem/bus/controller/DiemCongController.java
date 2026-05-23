package org.AdmissionsSystem.bus.controller;

import java.awt.Component;
import java.io.IOException;
import java.util.List;
import org.AdmissionsSystem.bus.service.DiemCongService;
import org.AdmissionsSystem.gui.components.ImportExcel;
import org.AdmissionsSystem.models.XtDiemcongxetuyen;

public class DiemCongController {

    private final DiemCongService service = new DiemCongService();
    private final ImportExcel importExcel = new ImportExcel();

    public List<XtDiemcongxetuyen> getAll() {
        return service.getAll();
    }

    public XtDiemcongxetuyen findById(int id) {
        return service.findById(id);
    }

    public List<XtDiemcongxetuyen> findByCccd(String cccd) {
        return service.findByCccd(cccd);
    }

    public XtDiemcongxetuyen findByKeys(String keys) {
        return service.findByKeys(keys);
    }

    public List<XtDiemcongxetuyen> search(String keyword) {
        return service.search(keyword);
    }

    public List<XtDiemcongxetuyen> searchByCccd(String cccdKeyword) {
        return service.searchByCccd(cccdKeyword);
    }

    public void add(XtDiemcongxetuyen entity) {
        service.add(entity);
    }

    public void update(XtDiemcongxetuyen entity) {
        service.update(entity);
    }

    public void delete(int id) {
        service.delete(id);
    }

    public long count() {
        return service.count();
    }

    public DiemCongService.ImportResult importDcc(Component parent) throws IOException {
        List<Object[]> rows = importExcel.chooseAndRead(
                parent,
                "Chọn file import ĐCC (quy đổi tiếng Anh)",
                service.getImportDccColumns(),
                service.getImportDccAliases());
        return service.importDccRows(rows);
    }

    public DiemCongService.ImportResult importUtxt(Component parent) throws IOException {
        List<Object[]> rows = importExcel.chooseAndRead(
                parent,
                "Chọn file import UTXT",
                service.getImportUtxtColumns(),
                service.getImportUtxtAliases());
        return service.importUtxtRows(rows);
    }
}
