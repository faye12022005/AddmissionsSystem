package org.AdmissionsSystem.bus.controller;

import java.util.List;
import org.AdmissionsSystem.bus.service.DiemCongService;
import org.AdmissionsSystem.models.XtDiemcongxetuyen;

public class DiemCongController {

    private final DiemCongService service = new DiemCongService();

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
}
