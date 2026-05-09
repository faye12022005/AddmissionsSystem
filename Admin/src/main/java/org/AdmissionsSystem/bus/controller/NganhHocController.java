package org.AdmissionsSystem.bus.controller;

import java.util.List;

import org.AdmissionsSystem.bus.service.NganhHocService;
import org.AdmissionsSystem.models.XtNganh;

public class NganhHocController {

    private final NganhHocService service = new NganhHocService();

    public List<XtNganh> loadAll() {
        return service.getAll();
    }

    public List<XtNganh> search(String keyword) {
        return service.search(keyword);
    }

    public void add(XtNganh model) {
        service.add(model);
    }

    public void update(String selectedMaNganh, XtNganh model) {
        service.update(selectedMaNganh, model);
    }

    public void deleteByMaNganh(String maNganh) {
        service.deleteByMaNganh(maNganh);
    }

    public void upsert(XtNganh model) {
        service.upsert(model);
    }
}
