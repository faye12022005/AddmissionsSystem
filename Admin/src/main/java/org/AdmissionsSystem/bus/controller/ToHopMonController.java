package org.AdmissionsSystem.bus.controller;

import java.util.List;
import org.AdmissionsSystem.models.XtTohopMonthi;
import org.AdmissionsSystem.bus.service.ToHopMonService;

public class ToHopMonController {
    private final ToHopMonService service = new ToHopMonService();

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
}
