package org.AdmissionsSystem.gui.modules.QuanLiNguyenVong;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

public class NguyenVongPanel extends Application {

    // ==================== DATA MODEL ====================
    public static class NguyenVong {
        private final String thuTu;
        private final String tenThiSinh;
        private final String sbd;
        private final String maNganh;
        private final String tenNganh;
        private final String tongDiem;
        private final String trangThai;

        public NguyenVong(String thuTu, String tenThiSinh, String sbd,
                          String maNganh, String tenNganh, String tongDiem, String trangThai) {
            this.thuTu = thuTu;
            this.tenThiSinh = tenThiSinh;
            this.sbd = sbd;
            this.maNganh = maNganh;
            this.tenNganh = tenNganh;
            this.tongDiem = tongDiem;
            this.trangThai = trangThai;
        }

        public String getThuTu()     { return thuTu; }
        public String getTenThiSinh(){ return tenThiSinh; }
        public String getSbd()       { return sbd; }
        public String getMaNganh()   { return maNganh; }
        public String getTenNganh()  { return tenNganh; }
        public String getTongDiem()  { return tongDiem; }
        public String getTrangThai() { return trangThai; }
    }

    // ==================== STYLE CONSTANTS ====================
    private static final String PRIMARY      = "#137fec";
    private static final String BG_LIGHT     = "#f6f7f8";
    private static final String WHITE        = "#ffffff";
    // removed unused SIDEBAR_BG
    private static final String TEXT_DARK    = "#0f172a";
    private static final String TEXT_MUTED   = "#94a3b8";
    private static final String BORDER_COLOR = "#cbd5e1";

    // ==================== MAIN ====================
    public static void main(String[] args) { 
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_LIGHT + ";");
        // Keep only the central panel (no sidebar, no header)
        Region center = buildMainContent();
        Scene scene = new Scene(center, 1280, 820);
        primaryStage.setTitle("Hệ thống Tuyển sinh - Quản lý Nguyện vọng");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ==================== MAIN CONTENT ====================
    public VBox buildMainContent() {
        VBox content = new VBox();
        content.setStyle("-fx-background-color: " + BG_LIGHT + ";");
        // header removed: keep only the page content
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox innerContent = new VBox(24);
        innerContent.setPadding(new Insets(28, 32, 28, 32));
        innerContent.getChildren().addAll(
            buildPageTitle(),
            buildStatsGrid(),
            buildFilters(),
            buildTable()
        );

        scroll.setContent(innerContent);
        content.getChildren().add(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return content;
    }

    // Public factory to allow embedding this panel inside a JFXPanel
    public static javafx.scene.layout.Region createContent() {
        return new NguyenVongPanel().buildMainContent();
    }

    // ==================== PAGE TITLE ====================
    private HBox buildPageTitle() {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);

        VBox titleBlock = new VBox(4);
        HBox.setHgrow(titleBlock, Priority.ALWAYS);
        Label title = new Label("Danh sách Nguyện vọng Thí sinh");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill: " + TEXT_DARK + ";");
        Label subtitle = new Label("Kỳ xét tuyển đại học chính quy - Năm học 2024-2025");
        subtitle.setFont(Font.font(13));
        subtitle.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
        titleBlock.getChildren().addAll(title, subtitle);

        Button runBtn = new Button("▶  Chạy xét tuyển hệ thống");
        runBtn.setFont(Font.font("System", FontWeight.BOLD, 13));
        runBtn.setPadding(new Insets(10, 20, 10, 20));
        runBtn.setStyle(
            "-fx-background-color: " + PRIMARY + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: transparent;" +
            "-fx-cursor: hand;"
        );
        runBtn.setOnMouseEntered(e -> runBtn.setStyle(
            "-fx-background-color: #0f6fd4; -fx-text-fill: white; -fx-background-radius: 10; -fx-border-color: transparent; -fx-cursor: hand;"
        ));
        runBtn.setOnMouseExited(e -> runBtn.setStyle(
            "-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-background-radius: 10; -fx-border-color: transparent; -fx-cursor: hand;"
        ));

        // Thêm nút 'Thêm' (add new) và 'Import Excel'
        Button addBtn = new Button("＋ Thêm");
        addBtn.setFont(Font.font("System", FontWeight.BOLD, 13));
        addBtn.setPadding(new Insets(10, 18, 10, 18));
        addBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_DARK + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #e6eefc;" +
            "-fx-cursor: hand;"
        );
        addBtn.setOnAction(ev -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Thêm Nguyện vọng");
            a.setHeaderText(null);
            a.setContentText("Chức năng Thêm chưa được triển khai.");
            a.showAndWait();
        });

        Button importBtn = new Button("⤓ Import Excel");
        importBtn.setFont(Font.font("System", FontWeight.NORMAL, 13));
        importBtn.setPadding(new Insets(10, 14, 10, 14));
        importBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_DARK + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #e6eefc;" +
            "-fx-cursor: hand;"
        );
        importBtn.setOnAction(ev -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Chọn file Excel để import");
            chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xls", "*.xlsx"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            Stage stage = (Stage) box.getScene().getWindow();
            java.io.File file = chooser.showOpenDialog(stage);
            if (file != null) {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Import Excel");
                a.setHeaderText(null);
                a.setContentText("Chọn file: " + file.getAbsolutePath());
                a.showAndWait();
            }
        });

        HBox buttons = new HBox(8);
        buttons.getChildren().addAll(importBtn, addBtn, runBtn);
        box.getChildren().addAll(titleBlock, buttons);
        return box;
    }

    // ==================== STATS GRID ====================
    private GridPane buildStatsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        String[][] stats = {
            {"👥", "#bfdbfe", "#1e40af", "Tổng nguyện vọng", "12,450"},
            {"⏳", "#ffedd5", "#b45309", "Đang chờ xử lý",   "8,120"},
            {"✅", "#bbf7d0", "#15803d", "Đã trúng tuyển",    "3,240"},
            {"❌", "#fecaca", "#dc2626", "Đã trượt",           "1,090"},
        };

        for (int i = 0; i < stats.length; i++) {
            VBox card = buildStatCard(stats[i][0], stats[i][1], stats[i][2], stats[i][3], stats[i][4]);
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25);
            if (grid.getColumnConstraints().size() < 4) grid.getColumnConstraints().add(cc);
            grid.add(card, i, 0);
        }

        return grid;
    }

    private VBox buildStatCard(String icon, String bgColor, String iconColor, String label, String value) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 8, 0, 0, 2);"
        );

        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(36, 36);
        iconBox.setMaxSize(36, 36);
        iconBox.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 8;"
        );
        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font(18));
        iconBox.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 6;"
        );
        iconBox.getChildren().add(iconLbl);

        HBox iconRow = new HBox();
        iconRow.getChildren().add(iconBox);

        Label labelLbl = new Label(label);
        labelLbl.setFont(Font.font("System", 12));
        labelLbl.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");

        Label valueLbl = new Label(value);
        valueLbl.setFont(Font.font("System", FontWeight.BOLD, 24));
        valueLbl.setStyle("-fx-text-fill: " + TEXT_DARK + ";");

        card.getChildren().addAll(iconRow, labelLbl, valueLbl);
        return card;
    }

    // ==================== FILTERS ====================
    private VBox buildFilters() {
        VBox card = new VBox();
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-radius: 12;"
        );

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> nganh = buildCombo("🔽  Tất cả các ngành", "Khoa học máy tính", "Kỹ thuật phần mềm");
        ComboBox<String> diem  = buildCombo("⭐  Mọi mức điểm",   "Dưới 20 điểm", "20 - 25 điểm", "Trên 25 điểm");
        ComboBox<String> sort  = buildCombo("↕  Thứ tự ưu tiên", "Điểm từ cao xuống thấp", "Mới nhất trước");

        HBox.setHgrow(nganh, Priority.ALWAYS);
        HBox.setHgrow(diem,  Priority.ALWAYS);
        HBox.setHgrow(sort,  Priority.ALWAYS);

        Button filterBtn = new Button("Lọc kết quả");
        filterBtn.setFont(Font.font("System", FontWeight.BOLD, 13));
        filterBtn.setPadding(new Insets(9, 20, 9, 20));
        filterBtn.setStyle(
            "-fx-background-color: rgba(19,127,236,0.12);" +
            "-fx-text-fill: " + PRIMARY + ";" +
            "-fx-background-radius: 9;" +
            "-fx-border-color: transparent;" +
            "-fx-cursor: hand;"
        );

        Button resetBtn = new Button("Đặt lại");
        resetBtn.setFont(Font.font("System", 13));
        resetBtn.setPadding(new Insets(9, 20, 9, 20));
        resetBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #64748b;" +
            "-fx-background-radius: 9;" +
            "-fx-border-color: transparent;" +
            "-fx-cursor: hand;"
        );

        row.getChildren().addAll(nganh, diem, sort, filterBtn, resetBtn);
        card.getChildren().add(row);
        return card;
    }

    private ComboBox<String> buildCombo(String... items) {
        ComboBox<String> cb = new ComboBox<>(FXCollections.observableArrayList(items));
        cb.getSelectionModel().selectFirst();
        cb.setPrefHeight(38);
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setStyle(
            "-fx-background-color: #f8fafc;" +
            "-fx-background-radius: 9;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 9;" +
            "-fx-font-size: 12;"
        );
        return cb;
    }

    // ==================== TABLE ====================
    private VBox buildTable() {
        VBox card = new VBox();
        card.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 8, 0, 0, 2);"
        );

        TableView<NguyenVong> table = new TableView<>();
        table.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-table-cell-border-color: #f1f5f9;"
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Columns
        TableColumn<NguyenVong, String> colThuTu = makeCol("THỨ TỰ", "thuTu", 80);
        TableColumn<NguyenVong, String> colThiSinh = makeCol("THÍ SINH", "tenThiSinh", 180);
        TableColumn<NguyenVong, String> colMa = makeCol("MÃ NGÀNH", "maNganh", 110);
        TableColumn<NguyenVong, String> colTen = makeCol("TÊN NGÀNH", "tenNganh", 200);
        TableColumn<NguyenVong, String> colDiem = makeCol("TỔNG ĐIỂM", "tongDiem", 110);
        TableColumn<NguyenVong, String> colTT = makeCol("TRẠNG THÁI", "trangThai", 130);

        // Custom cell for Thứ tự (badge)
        colThuTu.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                boolean isFirst = getIndex() == 0;
                StackPane badge = new StackPane();
                badge.setPrefSize(30, 30);
                badge.setStyle(
                    "-fx-background-radius: 15;" +
                    "-fx-background-color: " + (isFirst ? PRIMARY : "#f1f5f9") + ";"
                );
                Label lbl = new Label(item);
                lbl.setFont(Font.font("System", FontWeight.BOLD, 11));
                lbl.setStyle("-fx-text-fill: " + (isFirst ? "white" : "#475569") + ";");
                badge.getChildren().add(lbl);
                setGraphic(badge);
                setText(null);
                setAlignment(Pos.CENTER_LEFT);
            }
        });

        // Custom cell for Thí sinh (name + SBD)
        colThiSinh.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                NguyenVong row = getTableView().getItems().get(getIndex());
                HBox hbox = new HBox(10);
                hbox.setAlignment(Pos.CENTER_LEFT);
                Circle avatar = new Circle(16, Color.web("#e2e8f0"));
                VBox info = new VBox(2);
                Label name = new Label(item);
                name.setFont(Font.font("System", FontWeight.BOLD, 12));
                Label sbd = new Label("SBD: " + row.getSbd());
                sbd.setFont(Font.font(10));
                sbd.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
                info.getChildren().addAll(name, sbd);
                hbox.getChildren().addAll(avatar, info);
                setGraphic(hbox);
                setText(null);
            }
        });

        // Custom cell for Điểm (colored)
        colDiem.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setFont(Font.font("System", FontWeight.BOLD, 14));
                setStyle("-fx-text-fill: " + PRIMARY + "; -fx-alignment: CENTER;");
            }
        });

        // Custom cell for Trạng thái (badge)
        colTT.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                boolean trung = item.equals("Trúng tuyển");
                HBox badge = new HBox(6);
                badge.setPadding(new Insets(4, 10, 4, 10));
                badge.setMaxWidth(Region.USE_PREF_SIZE);
                badge.setAlignment(Pos.CENTER_LEFT);
                badge.setStyle(
                    "-fx-background-radius: 20;" +
                    "-fx-background-color: " + (trung ? "#f0fdf4" : "#fffbeb") + ";"
                );
                Circle dot = new Circle(4, Color.web(trung ? "#22c55e" : "#f59e0b"));
                Label lbl = new Label(item);
                lbl.setFont(Font.font("System", FontWeight.BOLD, 10));
                lbl.setStyle("-fx-text-fill: " + (trung ? "#16a34a" : "#d97706") + ";");
                badge.getChildren().addAll(dot, lbl);
                setGraphic(badge);
                setText(null);
            }
        });

        table.getColumns().addAll(colThuTu, colThiSinh, colMa, colTen, colDiem, colTT);

        // Sample data
        ObservableList<NguyenVong> data = FXCollections.observableArrayList(
            new NguyenVong("01", "Nguyễn Văn A", "2400015", "7480101", "Khoa học máy tính", "28.50", "Trúng tuyển"),
            new NguyenVong("02", "Lê Thị B",     "2400288", "7480103", "Kỹ thuật phần mềm", "26.25", "Đang chờ"),
            new NguyenVong("03", "Trần Văn C",   "2400312", "7480201", "Hệ thống thông tin", "25.75", "Đang chờ"),
            new NguyenVong("04", "Phạm Thị D",   "2400456", "7480104", "Mạng máy tính",     "24.00", "Đang chờ"),
            new NguyenVong("05", "Hoàng Văn E",  "2400589", "7480101", "Khoa học máy tính", "22.50", "Đang chờ"),
            new NguyenVong("06", "Vũ Thị F",     "2400671", "7480103", "Kỹ thuật phần mềm", "20.75", "Đã trượt"),
            new NguyenVong("07", "Đặng Văn G",   "2400710", "7480105", "An toàn thông tin", "19.50", "Đã trượt")
        );
        table.setItems(data);
        table.setPrefHeight(300);

        // double-click row -> open detail form
        table.setRowFactory(tv -> {
            TableRow<NguyenVong> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    NguyenVong item = row.getItem();
                    // show detail window bound to this row's data
                    ChiTietNguyenVong detail = new ChiTietNguyenVong(item);
                    detail.show();
                }
            });
            return row;
        });

        // Pagination row
        HBox pagination = buildPagination();

        card.getChildren().addAll(table, pagination);
        return card;
    }

    private TableColumn<NguyenVong, String> makeCol(String title, String field, double minW) {
        TableColumn<NguyenVong, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(field));
        col.setMinWidth(minW);
        col.setStyle("-fx-alignment: CENTER-LEFT;");
        return col;
    }

    // ==================== PAGINATION ====================
    private HBox buildPagination() {
        HBox box = new HBox(8);
        box.setPadding(new Insets(14, 20, 14, 20));
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setStyle(
            "-fx-background-color: #f8fafc;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1 0 0 0;" +
            "-fx-background-radius: 0 0 12 12;"
        );

        Label info = new Label("Hiển thị 1-10 trong số 1,240 bản ghi");
        info.setFont(Font.font(11));
        info.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
        HBox.setHgrow(info, Priority.ALWAYS);

        Button prev = buildPageBtn("‹", false);
        Button p1   = buildPageBtn("1", true);
        Button p2   = buildPageBtn("2", false);
        Button p3   = buildPageBtn("3", false);
        Label dots  = new Label("...");
        dots.setPadding(new Insets(0, 4, 0, 4));
        dots.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
        Button p124 = buildPageBtn("124", false);
        Button next = buildPageBtn("›", false);

        box.getChildren().addAll(info, prev, p1, p2, p3, dots, p124, next);
        return box;
    }

    private Button buildPageBtn(String text, boolean active) {
        Button btn = new Button(text);
        btn.setPrefSize(32, 32);
        btn.setFont(Font.font("System", active ? FontWeight.BOLD : FontWeight.NORMAL, 12));
        btn.setStyle(
            "-fx-background-color: " + (active ? PRIMARY : "transparent") + ";" +
            "-fx-text-fill: " + (active ? "white" : "#475569") + ";" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-cursor: hand;"
        );
        if (!active) {
            btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-background-radius: 8; -fx-border-color: transparent; -fx-cursor: hand;"
            ));
            btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #475569; -fx-background-radius: 8; -fx-border-color: transparent; -fx-cursor: hand;"
            ));
        }
        return btn;
    }
}