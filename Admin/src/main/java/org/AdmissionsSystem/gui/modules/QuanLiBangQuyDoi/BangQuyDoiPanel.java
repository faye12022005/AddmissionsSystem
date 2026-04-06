package org.AdmissionsSystem.gui.modules.QuanLiBangQuyDoi;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class BangQuyDoiPanel extends Application {

    // ── Màu sắc ──────────────────────────────────────────────
    private static final String PRIMARY      = "#137fec";
    private static final String WHITE        = "#ffffff";
    private static final String BG           = "#f6f7f8";
    private static final String BORDER       = "#e2e8f0";
    private static final String TEXT_DARK    = "#0f172a";
    private static final String TEXT_MUTED   = "#64748b";
    private static final String TEXT_LIGHT   = "#94a3b8";
    private static final String SURFACE      = "#f1f5f9";
    private static final String ERROR        = "#ef4444";
    private static final String SUCCESS      = "#10b981";

    // ── Data Model ───────────────────────────────────────────
    public static class QuyTac {
        private final String id, loai, phuongThuc, toHop, mon, khoangDiem, quyDoi;
        public QuyTac(String id, String loai, String phuongThuc,
                      String toHop, String mon, String khoangDiem, String quyDoi) {
            this.id = id; this.loai = loai; this.phuongThuc = phuongThuc;
            this.toHop = toHop; this.mon = mon;
            this.khoangDiem = khoangDiem; this.quyDoi = quyDoi;
        }
        public String getId()         { return id; }
        public String getLoai()       { return loai; }
        public String getPhuongThuc() { return phuongThuc; }
        public String getToHop()      { return toHop; }
        public String getMon()        { return mon; }
        public String getKhoangDiem() { return khoangDiem; }
        public String getQuyDoi()     { return quyDoi; }
    }

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) {
        ScrollPane scroll = new ScrollPane(buildMainPanel());
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + "; -fx-border-color: transparent;");

        Scene scene = new Scene(scroll, 1000, 780);
        stage.setTitle("Quản lý Bảng quy đổi");
        stage.setScene(scene);
        stage.show();
    }

    // ══════════════════════════════════════════════════════════
    //  MAIN PANEL
    // ══════════════════════════════════════════════════════════
    private VBox buildMainPanel() {
        VBox panel = new VBox(28);
        panel.setPadding(new Insets(32));
        panel.setStyle("-fx-background-color: " + BG + ";");

        panel.getChildren().addAll(
            buildPageHeader(),
            buildFilterBar(),
            buildTable(),
            buildStatsRow()
        );
        return panel;
    }

    // Public factory to allow embedding this panel inside a JFXPanel
    public static javafx.scene.layout.Region createContent() {
        return new BangQuyDoiPanel().buildMainPanel();
    }

    // ══════════════════════════════════════════════════════════
    //  1. PAGE HEADER
    // ══════════════════════════════════════════════════════════
    private HBox buildPageHeader() {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        VBox titleBlock = new VBox(5);
        HBox.setHgrow(titleBlock, Priority.ALWAYS);

        Label title = new Label("Quản lý Bảng quy đổi");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: " + TEXT_DARK + ";");

        Label subtitle = new Label("Cấu hình quy tắc đổi điểm chứng chỉ và điểm ưu tiên cho kỳ tuyển sinh 2024");
        subtitle.setFont(Font.font(13));
        subtitle.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");

        titleBlock.getChildren().addAll(title, subtitle);

        Button importBtn = new Button("📄  Import Excel");
        importBtn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        importBtn.setPadding(new Insets(10, 20, 10, 20));
        importBtn.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-text-fill: " + TEXT_DARK + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);"
        );
        styleHover(importBtn,
            "-fx-background-color: #f8fafc; -fx-text-fill: " + TEXT_DARK + "; -fx-background-radius: 10; -fx-border-color: " + BORDER + "; -fx-border-radius: 10; -fx-cursor: hand;",
            "-fx-background-color: " + WHITE + "; -fx-text-fill: " + TEXT_DARK + "; -fx-background-radius: 10; -fx-border-color: " + BORDER + "; -fx-border-radius: 10; -fx-cursor: hand;"
        );

        row.getChildren().addAll(titleBlock, importBtn);
        return row;
    }

    // ══════════════════════════════════════════════════════════
    //  2. FILTER BAR
    // ══════════════════════════════════════════════════════════
    private VBox buildFilterBar() {
        VBox card = new VBox();
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle(cardStyle());

        HBox row = new HBox(14);
        row.setAlignment(Pos.BOTTOM_CENTER);

        VBox f1 = buildFilterGroup("LOẠI QUY ĐỔI",  new String[]{"Tất cả loại", "Chứng chỉ", "Điểm ưu tiên"});
        VBox f2 = buildFilterGroup("PHƯƠNG THỨC",    new String[]{"Chọn phương thức", "IELTS", "TOEFL iBT", "VSTEP"});
        VBox f3 = buildFilterGroup("TỔ HỢP",         new String[]{"Tất cả tổ hợp", "A00", "D01"});
        VBox f4 = buildFilterGroup("MÔN",            new String[]{"Tất cả môn", "Toán", "Tiếng Anh"});

        HBox.setHgrow(f1, Priority.ALWAYS);
        HBox.setHgrow(f2, Priority.ALWAYS);
        HBox.setHgrow(f3, Priority.ALWAYS);
        HBox.setHgrow(f4, Priority.ALWAYS);

        Button addBtn = new Button("＋  Thêm mới");
        addBtn.setFont(Font.font("System", FontWeight.BOLD, 13));
        addBtn.setPadding(new Insets(10, 22, 10, 22));
        addBtn.setStyle(
            "-fx-background-color: " + PRIMARY + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: transparent;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(19,127,236,0.3), 10, 0, 0, 3);"
        );
        styleHover(addBtn,
            "-fx-background-color: #0f6fd4; -fx-text-fill: white; -fx-background-radius: 10; -fx-border-color: transparent; -fx-cursor: hand;",
            "-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-background-radius: 10; -fx-border-color: transparent; -fx-cursor: hand;"
        );

        row.getChildren().addAll(f1, f2, f3, f4, addBtn);
        card.getChildren().add(row);
        return card;
    }

    private VBox buildFilterGroup(String label, String[] options) {
        VBox group = new VBox(6);

        Label lbl = new Label(label);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 9.5));
        lbl.setStyle("-fx-text-fill: " + TEXT_LIGHT + "; -fx-letter-spacing: 0.08em;");

        ComboBox<String> cb = new ComboBox<>(FXCollections.observableArrayList(options));
        cb.getSelectionModel().selectFirst();
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setPrefHeight(36);
        cb.setStyle(
            "-fx-background-color: " + SURFACE + ";" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: 500;"
        );

        group.getChildren().addAll(lbl, cb);
        return group;
    }

    // ══════════════════════════════════════════════════════════
    //  3. TABLE
    // ══════════════════════════════════════════════════════════
    private VBox buildTable() {
        VBox card = new VBox();
        card.setStyle(cardStyle());

        TableView<QuyTac> table = new TableView<>();
        table.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-table-cell-border-color: #f1f5f9;" +
            "-fx-font-size: 13;"
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(260);

        // ── Columns ──
        // ID
        TableColumn<QuyTac, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMinWidth(80);
        colId.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); return; }
                setText(v);
                setFont(Font.font("System", FontWeight.BOLD, 12));
                setStyle("-fx-text-fill: " + PRIMARY + ";");
            }
        });

        // Loại
        TableColumn<QuyTac, String> colLoai = new TableColumn<>("LOẠI");
        colLoai.setCellValueFactory(new PropertyValueFactory<>("loai"));
        colLoai.setMinWidth(110);
        colLoai.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setGraphic(null); setText(null); return; }
                boolean isCC = v.equals("Chứng chỉ");
                Label badge = new Label(v);
                badge.setFont(Font.font("System", FontWeight.BOLD, 10));
                badge.setPadding(new Insets(3, 9, 3, 9));
                badge.setStyle(
                    "-fx-background-radius: 6;" +
                    (isCC
                        ? "-fx-background-color: #eff6ff; -fx-text-fill: #2563eb;"
                        : "-fx-background-color: #fffbeb; -fx-text-fill: #d97706;")
                );
                setGraphic(badge);
                setText(null);
            }
        });

        // Phương thức
        TableColumn<QuyTac, String> colPT = makeTextCol("PHƯƠNG THỨC", "phuongThuc", 120, FontWeight.SEMI_BOLD, TEXT_DARK);
        // Tổ hợp
        TableColumn<QuyTac, String> colTH = makeTextCol("TỔ HỢP", "toHop", 80, FontWeight.NORMAL, TEXT_LIGHT);
        // Môn
        TableColumn<QuyTac, String> colMon = makeTextCol("MÔN", "mon", 100, FontWeight.NORMAL, TEXT_DARK);
        // Khoảng điểm
        TableColumn<QuyTac, String> colKD = makeTextCol("KHOẢNG ĐIỂM", "khoangDiem", 120, FontWeight.BOLD, TEXT_DARK);

        // Quy đổi
        TableColumn<QuyTac, String> colQD = new TableColumn<>("QUY ĐỔI");
        colQD.setCellValueFactory(new PropertyValueFactory<>("quyDoi"));
        colQD.setMinWidth(90);
        colQD.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); return; }
                setText(v);
                setFont(Font.font("System", FontWeight.BOLD, 13));
                boolean isPlus = v.startsWith("+");
                setStyle("-fx-text-fill: " + (isPlus ? SUCCESS : PRIMARY) + "; -fx-alignment: CENTER-RIGHT;");
            }
        });

        // Hành động
        TableColumn<QuyTac, String> colAction = new TableColumn<>("HÀNH ĐỘNG");
        colAction.setMinWidth(100);
        colAction.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }

                Button editBtn = buildActionBtn("✏", PRIMARY, "#eff6ff");
                Button delBtn  = buildActionBtn("🗑", ERROR, "#fff1f2");

                HBox btns = new HBox(6, editBtn, delBtn);
                btns.setAlignment(Pos.CENTER);
                setGraphic(btns);
                setText(null);
            }
        });

        table.getColumns().addAll(colId, colLoai, colPT, colTH, colMon, colKD, colQD, colAction);

        // ── Data ──
        ObservableList<QuyTac> data = FXCollections.observableArrayList(
            new QuyTac("QD-001", "Chứng chỉ",    "IELTS",      "-", "Tiếng Anh", "8.0 - 9.0",  "10.0"),
            new QuyTac("QD-002", "Chứng chỉ",    "TOEFL iBT",  "-", "Tiếng Anh", "95 - 120",   "10.0"),
            new QuyTac("QD-003", "Điểm ưu tiên", "Đối tượng",  "-", "-",         "Nhóm 1",     "+2.0"),
            new QuyTac("QD-004", "Chứng chỉ",    "VSTEP",      "-", "Tiếng Anh", "8.5 - 10.0", "9.0")
        );
        table.setItems(data);

        // Pagination
        HBox pagination = buildPagination();

        card.getChildren().addAll(table, new Separator(), pagination);
        return card;
    }

    private TableColumn<QuyTac, String> makeTextCol(String title, String field, double minW,
                                                     FontWeight fw, String color) {
        TableColumn<QuyTac, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(field));
        col.setMinWidth(minW);
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); return; }
                setText(v);
                setFont(Font.font("System", fw, 13));
                setStyle("-fx-text-fill: " + color + ";");
            }
        });
        return col;
    }

    private Button buildActionBtn(String icon, String hoverColor, String hoverBg) {
        Button btn = new Button(icon);
        btn.setPrefSize(30, 30);
        btn.setFont(Font.font(13));
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background-radius: 7;" +
            "-fx-border-color: transparent;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + hoverBg + ";" +
            "-fx-background-radius: 7; -fx-border-color: transparent; -fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent; -fx-background-radius: 7; -fx-border-color: transparent; -fx-cursor: hand;"
        ));
        return btn;
    }

    private HBox buildPagination() {
        HBox box = new HBox(6);
        box.setPadding(new Insets(12, 20, 12, 20));
        box.setAlignment(Pos.CENTER_RIGHT);

        Label info = new Label("Hiển thị 1 - 4 trong tổng số 42 quy tắc");
        info.setFont(Font.font(11));
        info.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
        HBox.setHgrow(info, Priority.ALWAYS);

        String[] pages = {"‹", "1", "2", "3", "...", "12", "›"};
        for (String p : pages) {
            if (p.equals("...")) {
                Label dots = new Label("...");
                dots.setPadding(new Insets(0, 4, 0, 4));
                dots.setStyle("-fx-text-fill: " + TEXT_LIGHT + ";");
                box.getChildren().add(dots);
            } else {
                boolean active = p.equals("1");
                Button btn = new Button(p);
                btn.setPrefSize(32, 32);
                btn.setFont(Font.font("System", active ? FontWeight.BOLD : FontWeight.NORMAL, 12));
                btn.setStyle(
                    "-fx-background-color: " + (active ? PRIMARY : "transparent") + ";" +
                    "-fx-text-fill: " + (active ? "white" : TEXT_MUTED) + ";" +
                    "-fx-background-radius: 8; -fx-border-color: transparent; -fx-cursor: hand;"
                );
                if (!active) {
                    styleHover(btn,
                        "-fx-background-color: " + SURFACE + "; -fx-text-fill: " + TEXT_DARK + "; -fx-background-radius: 8; -fx-border-color: transparent; -fx-cursor: hand;",
                        "-fx-background-color: transparent; -fx-text-fill: " + TEXT_MUTED + "; -fx-background-radius: 8; -fx-border-color: transparent; -fx-cursor: hand;"
                    );
                }
                box.getChildren().add(btn);
            }
        }

        HBox result = new HBox();
        result.setAlignment(Pos.CENTER_LEFT);
        result.getChildren().addAll(info);

        HBox full = new HBox();
        full.setPadding(new Insets(12, 20, 12, 20));
        full.setAlignment(Pos.CENTER);
        HBox.setHgrow(info, Priority.ALWAYS);
        full.getChildren().addAll(info);
        full.getChildren().addAll(box.getChildren());
        return full;
    }

    // ══════════════════════════════════════════════════════════
    //  4. STATS ROW (3 thẻ)
    // ══════════════════════════════════════════════════════════
    private HBox buildStatsRow() {
        HBox row = new HBox(16);

        // Thẻ 1: Tổng quy tắc
        HBox card1 = buildInfoCard(
            "📋", "#eff6ff", "#2563eb",
            "TỔNG QUY TẮC", "42", "+3 từ tuần trước", SUCCESS
        );

        // Thẻ 2: Cập nhật cuối
        HBox card2 = buildInfoCard(
            "🔄", "#fffbeb", "#d97706",
            "CẬP NHẬT CUỐI", "15:30, 20/10/2023", "bởi Admin: Nguyen Minh", TEXT_MUTED
        );

        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);

        // Thẻ 3: Kiểm tra Logic (xanh)
        VBox card3 = buildLogicCard();
        HBox.setHgrow(card3, Priority.ALWAYS);

        row.getChildren().addAll(card1, card2, card3);
        return row;
    }

    private HBox buildInfoCard(String icon, String iconBg, String iconColor,
                                String label, String value, String sub, String subColor) {
        HBox card = new HBox(16);
        card.setPadding(new Insets(22));
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle(cardStyle());

        // Icon box
        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(48, 48);
        iconBox.setMinSize(48, 48);
        iconBox.setStyle("-fx-background-color: " + iconBg + "; -fx-background-radius: 12;");
        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font(20));
        iconBox.getChildren().add(iconLbl);

        VBox info = new VBox(4);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 9.5));
        lbl.setStyle("-fx-text-fill: " + TEXT_LIGHT + "; -fx-letter-spacing: 0.08em;");
        Label val = new Label(value);
        val.setFont(Font.font("System", FontWeight.BOLD, value.length() < 5 ? 24 : 15));
        val.setStyle("-fx-text-fill: " + TEXT_DARK + ";");
        Label subLbl = new Label(sub);
        subLbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11));
        subLbl.setStyle("-fx-text-fill: " + subColor + ";");
        info.getChildren().addAll(lbl, val, subLbl);

        card.getChildren().addAll(iconBox, info);
        return card;
    }

    private VBox buildLogicCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(22));
        card.setStyle(
            "-fx-background-color: " + PRIMARY + ";" +
            "-fx-background-radius: 14;" +
            "-fx-effect: dropshadow(gaussian, rgba(19,127,236,0.35), 18, 0, 0, 5);"
        );

        Label title = new Label("Kiểm tra Logic");
        title.setFont(Font.font("System", FontWeight.BOLD, 17));
        title.setStyle("-fx-text-fill: white;");

        Label desc = new Label("Hệ thống phát hiện 2 quy tắc\ncó thể bị trùng lặp khoảng điểm.");
        desc.setFont(Font.font(12));
        desc.setStyle("-fx-text-fill: rgba(255,255,255,0.85);");
        desc.setWrapText(true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button detailBtn = new Button("Xem chi tiết");
        detailBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
        detailBtn.setPadding(new Insets(8, 18, 8, 18));
        detailBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-cursor: hand;"
        );
        detailBtn.setOnMouseEntered(e -> detailBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.32); -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-border-color: transparent; -fx-cursor: hand;"
        ));
        detailBtn.setOnMouseExited(e -> detailBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-border-color: transparent; -fx-cursor: hand;"
        ));

        card.getChildren().addAll(title, desc, spacer, detailBtn);
        return card;
    }

    // ══════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════
    private String cardStyle() {
        return "-fx-background-color: " + WHITE + ";" +
               "-fx-background-radius: 14;" +
               "-fx-border-color: " + BORDER + ";" +
               "-fx-border-radius: 14;" +
               "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);";
    }

    private void styleHover(Button btn, String hoverStyle, String normalStyle) {
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
    }
}

