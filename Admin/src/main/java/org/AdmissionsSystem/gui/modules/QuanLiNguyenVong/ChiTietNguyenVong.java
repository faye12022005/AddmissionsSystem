package org.AdmissionsSystem.gui.modules.QuanLiNguyenVong;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class ChiTietNguyenVong {

    // ── Màu sắc ──────────────────────────────────────────────
    private static final String PRIMARY      = "#137fec";
    private static final String WHITE        = "#ffffff";
    private static final String BG           = "#f6f7f8";
    private static final String BORDER       = "#e2e8f0";
    private static final String TEXT_DARK    = "#0f172a";
    private static final String TEXT_MUTED   = "#64748b";
    private static final String SURFACE_LOW  = "#f8fafc";
    private static final String SURFACE_MID  = "#f1f5f9";
    private static final String ERROR_COLOR  = "#ef4444";
    private static final String SUCCESS_BG   = "#f0fdf4";
    private static final String SUCCESS_TEXT = "#16a34a";

    private final NguyenVongPanel.NguyenVong data;

    public ChiTietNguyenVong(NguyenVongPanel.NguyenVong data) {
        this.data = data;
    }

    // Open a modal window showing details for the given data
    public void show() {
        Stage stage = new Stage();

        // ── Background mờ (giả lập danh sách phía sau) ───────
        VBox background = buildBackground();
        background.setEffect(new GaussianBlur(6));

        // ── Overlay tối ───────────────────────────────────────
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(15,23,42,0.45);");

        // ── Modal ─────────────────────────────────────────────
        VBox modal = buildModal(stage);
        StackPane.setAlignment(modal, Pos.CENTER);
        StackPane.setMargin(modal, new Insets(32));

        overlay.getChildren().add(modal);

        // ── Root ──────────────────────────────────────────────
        StackPane root = new StackPane(background, overlay);

        Scene scene = new Scene(root, 1200, 780);
        stage.setTitle("Chi tiết Nguyện vọng");
        stage.setScene(scene);
        stage.show();
    }

    // ══════════════════════════════════════════════════════════
    //  BACKGROUND GIẢ LẬP
    // ══════════════════════════════════════════════════════════
    private VBox buildBackground() {
        VBox bg = new VBox();
        bg.setStyle("-fx-background-color: " + BG + ";");

        // giả header
        HBox header = new HBox(32);
        header.setPrefHeight(56);
        header.setPadding(new Insets(0, 32, 0, 32));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");
        Label logo = new Label("Azure Scholar");
        logo.setFont(Font.font("System", FontWeight.BOLD, 18));
        logo.setStyle("-fx-text-fill: " + PRIMARY + ";");
        header.getChildren().add(logo);

        // giả bảng
        VBox tableArea = new VBox(16);
        tableArea.setPadding(new Insets(32));
        Label title = new Label("Aspiration Management");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));

        VBox fakeTable = new VBox();
        fakeTable.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        for (int i = 0; i < 6; i++) {
            HBox row = new HBox();
            row.setPrefHeight(44);
            row.setStyle(i % 2 == 0 ? "-fx-background-color: white;" : "-fx-background-color: #f8fafc;");
            tableArea.getChildren().add(row);
        }
        tableArea.getChildren().addAll(0, java.util.List.of(title, fakeTable));

        bg.getChildren().addAll(header, tableArea);
        VBox.setVgrow(tableArea, Priority.ALWAYS);
        return bg;
    }

    // ══════════════════════════════════════════════════════════
    //  MODAL CHÍNH
    // ══════════════════════════════════════════════════════════
    private VBox buildModal(Stage stage) {
        VBox modal = new VBox();
        modal.setMaxWidth(980);
        modal.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 14;" +
            "-fx-effect: dropshadow(gaussian, rgba(19,127,236,0.18), 40, 0, 0, 8);"
        );

        modal.getChildren().addAll(
            buildModalHeader(stage),
            buildModalBody(),
            buildModalFooter()
        );
        return modal;
    }

    // ── Header modal ─────────────────────────────────────────
    private HBox buildModalHeader(Stage stage) {
        HBox header = new HBox();
        header.setPadding(new Insets(16, 20, 16, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        // icon star
        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(38, 38);
        iconBox.setStyle("-fx-background-color: rgba(19,127,236,0.1); -fx-background-radius: 9;");
        Label star = new Label("★");
        star.setFont(Font.font(18));
        star.setStyle("-fx-text-fill: " + PRIMARY + ";");
        iconBox.getChildren().add(star);

        Label title = new Label("Chi tiết Nguyện vọng");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: " + TEXT_DARK + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setPrefSize(36, 36);
        closeBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-background-radius: 8; -fx-font-size: 14; -fx-cursor: hand;"
        );
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(
            "-fx-background-color: #f1f5f9; -fx-text-fill: " + TEXT_DARK + ";" +
            "-fx-background-radius: 8; -fx-font-size: 14; -fx-cursor: hand;"
        ));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-background-radius: 8; -fx-font-size: 14; -fx-cursor: hand;"
        ));
        closeBtn.setOnAction(e -> stage.close());

        header.setSpacing(12);
        header.getChildren().addAll(iconBox, title, spacer, closeBtn);
        return header;
    }

    // ── Body modal (3 cột) ───────────────────────────────────
    private HBox buildModalBody() {
        HBox body = new HBox();

        VBox col1 = buildCol1ThiSinh();
        VBox col2 = buildCol2DangKy();
        VBox col3 = buildCol3KetQua();

        col1.prefWidthProperty().bind(body.widthProperty().multiply(0.28));
        col2.prefWidthProperty().bind(body.widthProperty().multiply(0.32));
        col3.prefWidthProperty().bind(body.widthProperty().multiply(0.40));

        // đường phân cách dọc
        col1.setStyle("-fx-border-color: " + BORDER + "; -fx-border-width: 0 1 0 0;");
        col2.setStyle("-fx-background-color: " + SURFACE_LOW + "; -fx-border-color: " + BORDER + "; -fx-border-width: 0 1 0 0;");

        body.getChildren().addAll(col1, col2, col3);
        return body;
    }

    // ── Cột 1: Thông tin thí sinh ─────────────────────────────
    private VBox buildCol1ThiSinh() {
        VBox col = new VBox(20);
        col.setPadding(new Insets(28));

        // Avatar + tên
        HBox avatarRow = new HBox(14);
        avatarRow.setAlignment(Pos.CENTER_LEFT);

        StackPane avatarWrap = new StackPane();
        Rectangle avatar = new Rectangle(72, 72);
        avatar.setArcWidth(14); avatar.setArcHeight(14);
        avatar.setFill(Color.web("#cbd5e1"));
        Label avatarIcon = new Label("👤");
        avatarIcon.setFont(Font.font(32));
        // badge xanh online
        Circle badge = new Circle(7, Color.web("#22c55e"));
        badge.setStroke(Color.WHITE); badge.setStrokeWidth(3);
        StackPane.setAlignment(badge, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(badge, new Insets(0, 0, 2, 0));
        avatarWrap.getChildren().addAll(avatar, avatarIcon, badge);

        VBox nameBlock = new VBox(4);
        Label name = new Label(data != null ? data.getTenThiSinh() : "Nguyễn Văn An");
        name.setFont(Font.font("System", FontWeight.BOLD, 16));
        name.setStyle("-fx-text-fill: " + TEXT_DARK + ";");
        Label type = new Label("Thí sinh tự do");
        type.setFont(Font.font(12));
        type.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
        nameBlock.getChildren().addAll(name, type);

        avatarRow.getChildren().addAll(avatarWrap, nameBlock);

        // Divider
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #f1f5f9;");

        // Thông tin chi tiết
        VBox infoBlock = new VBox(14);
        infoBlock.getChildren().addAll(
            buildInfoRow("SỐ BÁO DANH", data != null ? data.getSbd() : "2400015",   true),
            buildInfoRow("CCCD",         "012345678901", false),
            buildInfoRow("NGÀY SINH",    "15/04/2006",  false)
        );

        col.getChildren().addAll(avatarRow, sep, infoBlock);
        return col;
    }

    private HBox buildInfoRow(String label, String value, boolean highlight) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        Label lbl = new Label(label);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 9.5));
        lbl.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-letter-spacing: 0.08em;");
        HBox.setHgrow(lbl, Priority.ALWAYS);

        if (highlight) {
            Label val = new Label(value);
            val.setFont(Font.font("System", FontWeight.BOLD, 12));
            val.setPadding(new Insets(2, 8, 2, 8));
            val.setStyle(
                "-fx-text-fill: " + PRIMARY + ";" +
                "-fx-background-color: rgba(19,127,236,0.08);" +
                "-fx-background-radius: 5;"
            );
            row.getChildren().addAll(lbl, val);
        } else {
            Label val = new Label(value);
            val.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
            val.setStyle("-fx-text-fill: " + TEXT_DARK + ";");
            row.getChildren().addAll(lbl, val);
        }
        return row;
    }

    // ── Cột 2: Thông tin đăng ký ─────────────────────────────
    private VBox buildCol2DangKy() {
        VBox col = new VBox(22);
        col.setPadding(new Insets(28));

        Label sectionTitle = new Label("THÔNG TIN ĐĂNG KÝ");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 9.5));
        sectionTitle.setStyle("-fx-text-fill: " + PRIMARY + "; -fx-letter-spacing: 0.1em;");

        // Ngành học
        VBox nganhBlock = new VBox(4);
        Label nganhLbl = new Label("NGÀNH HỌC");
        nganhLbl.setFont(Font.font("System", FontWeight.BOLD, 9.5));
        nganhLbl.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
        Label nganhVal = new Label(data != null ? String.format("%s (%s)", data.getTenNganh(), data.getMaNganh()) : "Khoa học máy tính (7480101)");
        nganhVal.setFont(Font.font("System", FontWeight.BOLD, 14));
        nganhVal.setStyle("-fx-text-fill: " + TEXT_DARK + ";");
        nganhVal.setWrapText(true);
        Label truong = new Label("Trường Đại học Bách Khoa - ĐHQG TP.HCM");
        truong.setFont(Font.font(11));
        truong.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
        truong.setWrapText(true);
        nganhBlock.getChildren().addAll(nganhLbl, nganhVal, truong);

        // Tổ hợp + Thứ tự (2 cột)
        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(6);

        Label tohopLbl = new Label("TỔ HỢP XÉT TUYỂN");
        tohopLbl.setFont(Font.font("System", FontWeight.BOLD, 9.5));
        tohopLbl.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");

        Label thutuLbl = new Label("THỨ TỰ ƯU TIÊN");
        thutuLbl.setFont(Font.font("System", FontWeight.BOLD, 9.5));
        thutuLbl.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");

        // Badge A00
        StackPane badgeA00 = new StackPane();
        badgeA00.setPadding(new Insets(4, 10, 4, 10));
        badgeA00.setMaxWidth(Region.USE_PREF_SIZE);
        badgeA00.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 6;");
        Label a00 = new Label("A00");
        a00.setFont(Font.font("System", FontWeight.BOLD, 12));
        a00.setStyle("-fx-text-fill: white;");
        badgeA00.getChildren().add(a00);

        Label tohopMon = new Label("Toán, Lý, Hóa");
        tohopMon.setFont(Font.font(10));
        tohopMon.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");

        VBox tohopBlock = new VBox(4, badgeA00, tohopMon);

        Label thutuVal = new Label(data != null ? data.getThuTu() : "01");
        thutuVal.setFont(Font.font("System", FontWeight.BOLD, 28));
        thutuVal.setStyle("-fx-text-fill: " + PRIMARY + ";");

        grid.add(tohopLbl,   0, 0);
        grid.add(thutuLbl,   1, 0);
        grid.add(tohopBlock, 0, 1);
        grid.add(thutuVal,   1, 1);
        ColumnConstraints cc = new ColumnConstraints(); cc.setPercentWidth(50);
        grid.getColumnConstraints().addAll(cc, cc);

        col.getChildren().addAll(sectionTitle, nganhBlock, grid);
        return col;
    }

    // ── Cột 3: Kết quả xét tuyển ─────────────────────────────
    private VBox buildCol3KetQua() {
        VBox col = new VBox(20);
        col.setPadding(new Insets(28));

        // Tiêu đề + badge trúng tuyển
        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label sectionTitle = new Label("KẾT QUẢ CHI TIẾT");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 9.5));
        sectionTitle.setStyle("-fx-text-fill: " + PRIMARY + ";");
        HBox.setHgrow(sectionTitle, Priority.ALWAYS);

        HBox statusBadge = new HBox(6);
        statusBadge.setPadding(new Insets(4, 12, 4, 12));
        statusBadge.setAlignment(Pos.CENTER);
        statusBadge.setStyle(
            "-fx-background-color: " + SUCCESS_BG + ";" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: rgba(34,197,94,0.25);" +
            "-fx-border-radius: 20;"
        );
        Circle dot = new Circle(4, Color.web("#22c55e"));
        Label statusLbl = new Label("Trúng tuyển");
        statusLbl.setFont(Font.font("System", FontWeight.BOLD, 11));
        statusLbl.setStyle("-fx-text-fill: " + SUCCESS_TEXT + ";");
        statusBadge.getChildren().addAll(dot, statusLbl);

        titleRow.getChildren().addAll(sectionTitle, statusBadge);

        // 3 ô điểm
        HBox scoresRow = new HBox(10);
        scoresRow.getChildren().addAll(
            buildScoreCard("TOÁN",    "9.0"),
            buildScoreCard("VẬT LÝ",  "9.5"),
            buildScoreCard("HÓA HỌC", "9.0")
        );

        // Điểm ưu tiên
        HBox uuTienRow = new HBox();
        uuTienRow.setAlignment(Pos.CENTER_LEFT);
        Label uuTienLbl = new Label("Điểm ưu tiên");
        uuTienLbl.setFont(Font.font(13));
        uuTienLbl.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
        HBox.setHgrow(uuTienLbl, Priority.ALWAYS);
        Label uuTienVal = new Label("+0.75");
        uuTienVal.setFont(Font.font("System", FontWeight.BOLD, 13));
        uuTienVal.setStyle("-fx-text-fill: " + TEXT_DARK + ";");
        uuTienRow.getChildren().addAll(uuTienLbl, uuTienVal);

        // Tổng điểm (banner xanh)
        HBox totalBox = new HBox();
        totalBox.setPadding(new Insets(16, 20, 16, 20));
        totalBox.setAlignment(Pos.CENTER_LEFT);
        totalBox.setStyle(
            "-fx-background-color: " + PRIMARY + ";" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(19,127,236,0.35), 16, 0, 0, 4);"
        );
        Label totalLbl = new Label("TỔNG ĐIỂM XÉT TUYỂN");
        totalLbl.setFont(Font.font("System", FontWeight.BOLD, 10));
        totalLbl.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-letter-spacing: 0.08em;");
        HBox.setHgrow(totalLbl, Priority.ALWAYS);
        Label totalVal = new Label(data != null ? data.getTongDiem() : "28.25");
        totalVal.setFont(Font.font("System", FontWeight.BOLD, 26));
        totalVal.setStyle("-fx-text-fill: white;");
        totalBox.getChildren().addAll(totalLbl, totalVal);

        col.getChildren().addAll(titleRow, scoresRow, uuTienRow, totalBox);
        return col;
    }

    private VBox buildScoreCard(String subject, String score) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12));
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: " + SURFACE_MID + ";" +
            "-fx-background-radius: 10;"
        );
        HBox.setHgrow(card, Priority.ALWAYS);
        Label subLbl = new Label(subject);
        subLbl.setFont(Font.font("System", FontWeight.BOLD, 9));
        subLbl.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
        Label scoreLbl = new Label(score);
        scoreLbl.setFont(Font.font("System", FontWeight.BOLD, 20));
        scoreLbl.setStyle("-fx-text-fill: " + TEXT_DARK + ";");
        card.getChildren().addAll(subLbl, scoreLbl);
        return card;
    }

    // ── Footer modal ─────────────────────────────────────────
    private HBox buildModalFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(16, 20, 16, 20));
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setStyle(
            "-fx-background-color: " + SURFACE_LOW + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-width: 1 0 0 0;" +
            "-fx-background-radius: 0 0 14 14;"
        );

        // Nút xóa
        Button deleteBtn = new Button("🗑  Xóa hồ sơ");
        deleteBtn.setFont(Font.font("System", FontWeight.BOLD, 13));
        deleteBtn.setPadding(new Insets(9, 16, 9, 16));
        deleteBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + ERROR_COLOR + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: transparent;" +
            "-fx-cursor: hand;"
        );
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(
            "-fx-background-color: #fee2e2; -fx-text-fill: " + ERROR_COLOR + ";" +
            "-fx-background-radius: 10; -fx-border-color: transparent; -fx-cursor: hand;"
        ));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: " + ERROR_COLOR + ";" +
            "-fx-background-radius: 10; -fx-border-color: transparent; -fx-cursor: hand;"
        ));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Nút chỉnh sửa
        Button editBtn = new Button("Chỉnh sửa");
        editBtn.setFont(Font.font("System", FontWeight.BOLD, 13));
        editBtn.setPadding(new Insets(9, 22, 9, 22));
        editBtn.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: #334155;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        );
        editBtn.setOnMouseEntered(e -> editBtn.setStyle(
            "-fx-background-color: #f8fafc; -fx-text-fill: #334155;" +
            "-fx-background-radius: 10; -fx-border-color: " + BORDER + "; -fx-border-radius: 10; -fx-cursor: hand;"
        ));
        editBtn.setOnMouseExited(e -> editBtn.setStyle(
            "-fx-background-color: white; -fx-text-fill: #334155;" +
            "-fx-background-radius: 10; -fx-border-color: " + BORDER + "; -fx-border-radius: 10; -fx-cursor: hand;"
        ));

        // Nút duyệt hồ sơ
        Button approveBtn = new Button("Duyệt hồ sơ");
        approveBtn.setFont(Font.font("System", FontWeight.BOLD, 13));
        approveBtn.setPadding(new Insets(9, 28, 9, 28));
        approveBtn.setStyle(
            "-fx-background-color: " + PRIMARY + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: transparent;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(19,127,236,0.35), 12, 0, 0, 3);"
        );
        approveBtn.setOnMouseEntered(e -> approveBtn.setStyle(
            "-fx-background-color: #0f6fd4; -fx-text-fill: white;" +
            "-fx-background-radius: 10; -fx-border-color: transparent; -fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(19,127,236,0.45), 16, 0, 0, 4);"
        ));
        approveBtn.setOnMouseExited(e -> approveBtn.setStyle(
            "-fx-background-color: " + PRIMARY + "; -fx-text-fill: white;" +
            "-fx-background-radius: 10; -fx-border-color: transparent; -fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(19,127,236,0.35), 12, 0, 0, 3);"
        ));

        HBox rightBtns = new HBox(10, editBtn, approveBtn);
        rightBtns.setAlignment(Pos.CENTER_RIGHT);

        footer.getChildren().addAll(deleteBtn, spacer, rightBtns);
        return footer;
    }
}