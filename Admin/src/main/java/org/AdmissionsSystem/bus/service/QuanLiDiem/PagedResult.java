package org.AdmissionsSystem.bus.service.QuanLiDiem;

import java.util.List;

public record PagedResult<T>(List<T> rows, long totalRows) {
}
