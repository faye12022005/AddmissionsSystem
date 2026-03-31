package org.AdmissionsSystem.gui.modules.QuanLiNguyenVong;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class NguyenVongPanel extends JPanel {
    private final JFXPanel fxPanel;

    public NguyenVongPanel() {
        setLayout(new BorderLayout());
        fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);

        Platform.setImplicitExit(false);
        Platform.runLater(() -> fxPanel.setScene(buildScene()));
    }

    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f6f7f8;");

        VBox content = new VBox(16);
        content.setPadding(new Insets(18));

        content.getChildren().add(buildHeader());
        content.getChildren().add(buildStatCards());
        content.getChildren().add(buildFilters());
        content.getChildren().add(buildTableSection());

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f6f7f8; -fx-background-color: #f6f7f8; -fx-border-color: transparent;");
        root.setCenter(scroll);

        return new Scene(root);
    }

    private HBox buildHeader() {
        Label title = new Label("Danh sach Nguyen vong Thi sinh");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#111827"));

        Label subtitle = new Label("Ky xet tuyen dai hoc chinh quy - Nam hoc 2024-2025");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setTextFill(Color.web("#64748b"));

        VBox left = new VBox(4, title, subtitle);

        Button runButton = new Button("Chay xet tuyen he thong");
        runButton.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        runButton.setStyle("-fx-background-color: #137fec; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 10 16 10 16;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(12, left, spacer, runButton);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private FlowPane buildStatCards() {
        FlowPane cards = new FlowPane();
        cards.setHgap(12);
        cards.setVgap(12);

        cards.getChildren().add(createCard("Tong nguyen vong", "12,450", "#2563eb", "#eff6ff"));
        cards.getChildren().add(createCard("Dang cho xu ly", "8,120", "#d97706", "#fffbeb"));
        cards.getChildren().add(createCard("Da trung tuyen", "3,240", "#059669", "#ecfdf5"));
        cards.getChildren().add(createCard("Da truot", "1,090", "#e11d48", "#fff1f2"));

        return cards;
    }

    private VBox createCard(String label, String value, String accent, String bg) {
        Label icon = new Label("●");
        icon.setTextFill(Color.web(accent));
        icon.setStyle("-fx-background-color: " + bg + "; -fx-padding: 6 10 6 10; -fx-background-radius: 8;");

        Label text = new Label(label);
        text.setFont(Font.font("Segoe UI", 13));
        text.setTextFill(Color.web("#64748b"));

        Label number = new Label(value);
        number.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        number.setTextFill(Color.web("#111827"));

        VBox card = new VBox(10, icon, text, number);
        card.setMinWidth(230);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 14;");
        return card;
    }

    private HBox buildFilters() {
        ComboBox<String> major = new ComboBox<>(FXCollections.observableArrayList("Tat ca cac nganh", "Khoa hoc may tinh", "Ky thuat phan mem"));
        major.getSelectionModel().selectFirst();

        ComboBox<String> score = new ComboBox<>(FXCollections.observableArrayList("Moi muc diem", "Duoi 20 diem", "20 - 25 diem", "Tren 25 diem"));
        score.getSelectionModel().selectFirst();

        ComboBox<String> sort = new ComboBox<>(FXCollections.observableArrayList("Thu tu uu tien", "Diem tu cao xuong thap", "Moi nhat truoc"));
        sort.getSelectionModel().selectFirst();

        major.setPrefWidth(220);
        score.setPrefWidth(220);
        sort.setPrefWidth(220);

        Button filterBtn = new Button("Loc ket qua");
        filterBtn.setStyle("-fx-background-color: #dbeafe; -fx-text-fill: #1d4ed8; -fx-font-weight: 700; -fx-background-radius: 10; -fx-padding: 10 16 10 16;");

        Button resetBtn = new Button("Dat lai");
        resetBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-font-weight: 600; -fx-background-radius: 10; -fx-padding: 10 16 10 16;");

        HBox filters = new HBox(10, major, score, sort, filterBtn, resetBtn);
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setPadding(new Insets(12));
        filters.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-background-radius: 10;");

        return filters;
    }

    private VBox buildTableSection() {
        TableView<NguyenVongRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setFixedCellSize(52);
        table.setPrefHeight(350);

        TableColumn<NguyenVongRow, String> sttCol = new TableColumn<>("Thu tu");
        sttCol.setCellValueFactory(v -> v.getValue().sttProperty());

        TableColumn<NguyenVongRow, String> tsCol = new TableColumn<>("Thi sinh");
        tsCol.setCellValueFactory(v -> v.getValue().thiSinhProperty());

        TableColumn<NguyenVongRow, String> maNganhCol = new TableColumn<>("Ma nganh");
        maNganhCol.setCellValueFactory(v -> v.getValue().maNganhProperty());

        TableColumn<NguyenVongRow, String> tenNganhCol = new TableColumn<>("Ten nganh");
        tenNganhCol.setCellValueFactory(v -> v.getValue().tenNganhProperty());

        TableColumn<NguyenVongRow, String> tongDiemCol = new TableColumn<>("Tong diem");
        tongDiemCol.setCellValueFactory(v -> v.getValue().tongDiemProperty());
        tongDiemCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<NguyenVongRow, String> statusCol = new TableColumn<>("Trang thai");
        statusCol.setCellValueFactory(v -> v.getValue().trangThaiProperty());
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                Label pill = new Label(item);
                pill.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-padding: 5 10 5 10; -fx-background-radius: 999;");
                if (item.toLowerCase().contains("trung")) {
                    pill.setStyle(pill.getStyle() + "-fx-background-color: #dcfce7; -fx-text-fill: #15803d;");
                } else if (item.toLowerCase().contains("dang")) {
                    pill.setStyle(pill.getStyle() + "-fx-background-color: #fef3c7; -fx-text-fill: #b45309;");
                } else {
                    pill.setStyle(pill.getStyle() + "-fx-background-color: #ffe4e6; -fx-text-fill: #be123c;");
                }
                setGraphic(pill);
            }
        });

        TableColumn<NguyenVongRow, String> actionCol = new TableColumn<>("Thao tac");
        actionCol.setCellValueFactory(v -> new SimpleStringProperty(""));
        actionCol.setStyle("-fx-alignment: CENTER;");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("Xem chi tiet");
            private final Button deleteBtn = new Button("Xoa");
            private final HBox box = new HBox(8, viewBtn, deleteBtn);

            {
                viewBtn.setStyle("-fx-background-color: #dbeafe; -fx-text-fill: #1d4ed8; -fx-font-size: 11px; -fx-font-weight: 700; -fx-background-radius: 8;");
                deleteBtn.setStyle("-fx-background-color: #ffe4e6; -fx-text-fill: #be123c; -fx-font-size: 11px; -fx-font-weight: 700; -fx-background-radius: 8;");

                viewBtn.setOnAction(e -> {
                    NguyenVongRow row = getTableView().getItems().get(getIndex());
                    showDetailDialog(row, getTableView());
                });

                deleteBtn.setOnAction(e -> {
                    NguyenVongRow row = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(row, getTableView());
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(sttCol, tsCol, maNganhCol, tenNganhCol, tongDiemCol, statusCol, actionCol);
        table.setItems(FXCollections.observableArrayList(
                new NguyenVongRow("01", "Nguyen Van A - SBD: 2400015", "7480101", "Khoa hoc may tinh", "28.50", "Trung tuyen"),
                new NguyenVongRow("02", "Le Thi B - SBD: 2400288", "7480103", "Ky thuat phan mem", "26.25", "Dang cho")
        ));

        VBox wrapper = new VBox(table);
        wrapper.setPadding(new Insets(0));
        wrapper.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-background-radius: 10;");

        return wrapper;
    }

    private void showDetailDialog(NguyenVongRow row, TableView<NguyenVongRow> table) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Chi tiet nguyen vong");
        dialog.setHeaderText("Cap nhat thong tin nguyen vong");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        javafx.scene.control.TextField sttField = new javafx.scene.control.TextField(row.getStt());
        javafx.scene.control.TextField thiSinhField = new javafx.scene.control.TextField(row.getThiSinh());
        javafx.scene.control.TextField maNganhField = new javafx.scene.control.TextField(row.getMaNganh());
        javafx.scene.control.TextField tenNganhField = new javafx.scene.control.TextField(row.getTenNganh());
        javafx.scene.control.TextField tongDiemField = new javafx.scene.control.TextField(row.getTongDiem());
        ComboBox<String> statusField = new ComboBox<>(FXCollections.observableArrayList("Trung tuyen", "Dang cho", "Da truot"));
        statusField.setValue(row.getTrangThai());

        grid.add(new Label("Thu tu:"), 0, 0);
        grid.add(sttField, 1, 0);
        grid.add(new Label("Thi sinh:"), 0, 1);
        grid.add(thiSinhField, 1, 1);
        grid.add(new Label("Ma nganh:"), 0, 2);
        grid.add(maNganhField, 1, 2);
        grid.add(new Label("Ten nganh:"), 0, 3);
        grid.add(tenNganhField, 1, 3);
        grid.add(new Label("Tong diem:"), 0, 4);
        grid.add(tongDiemField, 1, 4);
        grid.add(new Label("Trang thai:"), 0, 5);
        grid.add(statusField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                row.setStt(sttField.getText());
                row.setThiSinh(thiSinhField.getText());
                row.setMaNganh(maNganhField.getText());
                row.setTenNganh(tenNganhField.getText());
                row.setTongDiem(tongDiemField.getText());
                row.setTrangThai(statusField.getValue());
                table.refresh();
            }
        });
    }

    private void showDeleteConfirmation(NguyenVongRow row, TableView<NguyenVongRow> table) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xac nhan xoa");
        confirm.setHeaderText("Ban co chac chan muon xoa nguyen vong nay?");
        confirm.setContentText("Thi sinh: " + row.getThiSinh());

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                table.getItems().remove(row);
            }
        });
    }

    private static class NguyenVongRow {
        private final SimpleStringProperty stt;
        private final SimpleStringProperty thiSinh;
        private final SimpleStringProperty maNganh;
        private final SimpleStringProperty tenNganh;
        private final SimpleStringProperty tongDiem;
        private final SimpleStringProperty trangThai;

        NguyenVongRow(String stt, String thiSinh, String maNganh, String tenNganh, String tongDiem, String trangThai) {
            this.stt = new SimpleStringProperty(stt);
            this.thiSinh = new SimpleStringProperty(thiSinh);
            this.maNganh = new SimpleStringProperty(maNganh);
            this.tenNganh = new SimpleStringProperty(tenNganh);
            this.tongDiem = new SimpleStringProperty(tongDiem);
            this.trangThai = new SimpleStringProperty(trangThai);
        }

        public String getStt() { return stt.get(); }
        public void setStt(String value) { stt.set(value); }
        public SimpleStringProperty sttProperty() { return stt; }

        public String getThiSinh() { return thiSinh.get(); }
        public void setThiSinh(String value) { thiSinh.set(value); }
        public SimpleStringProperty thiSinhProperty() { return thiSinh; }

        public String getMaNganh() { return maNganh.get(); }
        public void setMaNganh(String value) { maNganh.set(value); }
        public SimpleStringProperty maNganhProperty() { return maNganh; }

        public String getTenNganh() { return tenNganh.get(); }
        public void setTenNganh(String value) { tenNganh.set(value); }
        public SimpleStringProperty tenNganhProperty() { return tenNganh; }

        public String getTongDiem() { return tongDiem.get(); }
        public void setTongDiem(String value) { tongDiem.set(value); }
        public SimpleStringProperty tongDiemProperty() { return tongDiem; }

        public String getTrangThai() { return trangThai.get(); }
        public void setTrangThai(String value) { trangThai.set(value); }
        public SimpleStringProperty trangThaiProperty() { return trangThai; }
    }
}