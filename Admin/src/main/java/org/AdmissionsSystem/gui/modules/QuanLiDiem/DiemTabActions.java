package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import org.AdmissionsSystem.gui.common.Searchable;

public interface DiemTabActions extends Searchable {
	void onAdd();

	void onEdit();

	void onDelete();

	void onView();

	void onImport();

	void onRefresh();
}
