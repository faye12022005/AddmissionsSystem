package org.AdmissionsSystem.service;

import java.util.HashMap;
import java.util.Map;
import org.AdmissionsSystem.dao.ThiSinhDao;
import org.AdmissionsSystem.models.XtThisinhxettuyen25;

public class ThiSinhService {

	private final ThiSinhDao dao = new ThiSinhDao();
	private final Map<String, String> hoTenCache = new HashMap<>();

	public String resolveHoTen(String cccd, String soBaoDanh) {
		String key = buildCacheKey(cccd, soBaoDanh);
		if (hoTenCache.containsKey(key)) {
			return hoTenCache.get(key);
		}

		XtThisinhxettuyen25 model = null;
		if (!isBlank(cccd)) {
			model = dao.findByCccd(cccd);
		}
		if (model == null && !isBlank(soBaoDanh)) {
			model = dao.findBySoBaoDanh(soBaoDanh);
		}

		String hoTen = "";
		if (model != null) {
			String ho = safeText(model.getHo());
			String ten = safeText(model.getTen());
			hoTen = (ho + " " + ten).trim();
		}

		hoTenCache.put(key, hoTen);
		return hoTen;
	}

	private String buildCacheKey(String cccd, String soBaoDanh) {
		return safeText(cccd) + "|" + safeText(soBaoDanh);
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private String safeText(String value) {
		return value == null ? "" : value.trim();
	}
}
