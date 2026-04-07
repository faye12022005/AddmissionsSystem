package org.AdmissionsSystem.gui.modules.QuanLyToHopMon;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;

import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;

public class ToHopMonPanel extends Application {

    // ── Màu sắc ──────────────────────────────────────────────
    private static final String PRIMARY    = "#137fec";
    private static final String WHITE      = "#ffffff";
    private static final String BG         = "#f6f7f8";
    private static final String BORDER     = "#e2e8f0";
    private static final String TEXT_DARK  = "#0f172a";
    private static final String TEXT_MUTED = "#64748b";
    private static final String TEXT_LIGHT = "#94a3b8";
    private static final String SURFACE    = "#f1f5f9";
    private static final String SUCCESS    = "#10b981";
    private static final String AMBER      = "#f59e0b";
    private static final String ERROR      = "#ef4444";

    private final ObservableList<ToHop> data = FXCollections.observableArrayList(
        new ToHop("A00", "Khối A00", "Toán",    "Vật lý",   "Hóa học", "Đang hoạt động"),
        new ToHop("A01", "Khối A01", "Toán",    "Vật lý",   "Tiếng Anh", "Đang hoạt động"),
        new ToHop("B00", "Khối B00", "Toán",    "Hóa học",  "Sinh học", "Đang hoạt động"),
        new ToHop("D01", "Khối D01", "Ngữ văn", "Toán",     "Tiếng Anh", "Tạm ngưng")
    );
    private final FilteredList<ToHop> filteredData = new FilteredList<>(data, item -> true);
    private TableView<ToHop> table;
    private ComboBox<String> statusCb;
    private Label paginationInfo;

    // ── Data Model ───────────────────────────────────────────
    public static class ToHop {
        private final String ma, ten, mon1, mon2, mon3, status;
        public ToHop(String ma, String ten, String mon1, String mon2, String mon3, String status) {
            this.ma = ma; this.ten = ten;
            this.mon1 = mon1; this.mon2 = mon2; this.mon3 = mon3;
            this.status = status;
        }
        public String getMa()   { return ma; }
        public String getTen()  { return ten; }
        public String getMon1() { return mon1; }
        public String getMon2() { return mon2; }
        public String getMon3() { return mon3; }
        public String getStatus() { return status; }
    }

    private static class ToHopFormData {
        private final String code;
        private final String name;
        private final String subj1;
        private final String subj2;
        private final String subj3;
        private final String status;

        private ToHopFormData(String code, String name, String subj1, String subj2, String subj3, String status) {
            this.code = code;
            this.name = name;
            this.subj1 = subj1;
            this.subj2 = subj2;
            this.subj3 = subj3;
            this.status = status;
        }
    }

    public static void main(String[] args) { launch(args); }

    public static Parent createContent() {
        return new ToHopMonPanel().buildMainPanel();
    }

    @Override
    public void start(Stage stage) {
        ScrollPane scroll = new ScrollPane(buildMainPanel());
        scroll.setFitToWidth(true);
        scroll.setStyle(
            "-fx-background: " + BG + ";" +
            "-fx-background-color: " + BG + ";" +
            "-fx-border-color: transparent;"
        );
        Scene scene = new Scene(scroll, 980, 800);
        stage.setTitle("Quản lý Tổ hợp môn xét tuyển");
        stage.setScene(scene);
        stage.show();
    }

    // ══════════════════════════════════════════════════════════
    //  MAIN PANEL
    // ══════════════════════════════════════════════════════════
    private VBox buildMainPanel() {
        VBox panel = new VBox(24);
        panel.setPadding(new Insets(32));
        panel.setStyle("-fx-background-color: " + BG + ";");
        panel.getChildren().addAll(
            buildPageHeader(),
            buildStatsGrid(),
            buildListControl(),
            buildTableCard(),
            buildFooter()
        );
        return panel;
    }

    // ══════════════════════════════════════════════════════════
    //  1. PAGE HEADER
    // ══════════════════════════════════════════════════════════
    private HBox buildPageHeader() {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        VBox titleBlock = new VBox(5);
        HBox.setHgrow(titleBlock, Priority.ALWAYS);
        Label title = new Label("Tổ hợp môn xét tuyển");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: " + TEXT_DARK + ";");
        Label subtitle = new Label("Quản lý và cấu hình danh sách các khối thi cho kỳ tuyển sinh.");
        subtitle.setFont(Font.font(13));
        subtitle.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
        titleBlock.getChildren().addAll(title, subtitle);

        HBox btnGroup = new HBox(10);
        btnGroup.setAlignment(Pos.CENTER_RIGHT);

        Button importBtn = new Button("📄  Import Excel");
        importBtn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        importBtn.setPadding(new Insets(9, 18, 9, 18));
        importBtn.setStyle(outlineButtonStyle());
        hover(importBtn,
            outlineButtonStyle().replace(WHITE, "#f8fafc"),
            outlineButtonStyle()
        );

        Button addBtn = new Button("＋  Thêm tổ hợp");
        addBtn.setFont(Font.font("System", FontWeight.BOLD, 13));
        addBtn.setPadding(new Insets(9, 18, 9, 18));
        addBtn.setStyle(primaryButtonStyle());
        hover(addBtn,
            primaryButtonStyle().replace(PRIMARY, "#0f6fd4"),
            primaryButtonStyle()
        );
        addBtn.setOnAction(e -> handleAddToHop());

        btnGroup.getChildren().addAll(importBtn, addBtn);
        row.getChildren().addAll(titleBlock, btnGroup);
        return row;
    }

    // ══════════════════════════════════════════════════════════
    //  2. STATS GRID (3 thẻ)
    // ══════════════════════════════════════════════════════════
    private GridPane buildStatsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(16);

        // Thẻ 1: Tổng số tổ hợp
        VBox card1 = buildStatCard(
            "🔷", "rgba(19,127,236,0.1)", PRIMARY,
            "Tổng số tổ hợp", "42",
            1.0, PRIMARY, null
        );
        // Thẻ 2: Đang sử dụng
        VBox card2 = buildStatCard(
            "✅", "#dcfce7", SUCCESS,
            "Đang sử dụng", "38",
            0.904, SUCCESS, null
        );
        // Thẻ 3: Mới cập nhật
        VBox card3 = buildStatCard(
            "🕐", "#fef3c7", AMBER,
            "Mới cập nhật", "12",
            -1, null, "Cập nhật lần cuối 2 giờ trước"
        );

        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(33.33);
        grid.getColumnConstraints().addAll(cc, cc, cc);
        grid.add(card1, 0, 0);
        grid.add(card2, 1, 0);
        grid.add(card3, 2, 0);
        return grid;
    }

    private VBox buildStatCard(String icon, String iconBg, String iconColor,
                                String label, String value,
                                double barRatio, String barColor, String note) {
        VBox card = new VBox(0);
        card.setPadding(new Insets(22));
        card.setStyle(cardStyle());

        // Icon
        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(40, 40);
        iconBox.setMaxSize(40, 40);
        iconBox.setStyle("-fx-background-color: " + iconBg + "; -fx-background-radius: 10;");
        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font(17));
        iconBox.getChildren().add(iconLbl);

        HBox iconRow = new HBox(iconBox);
        iconRow.setPadding(new Insets(0, 0, 12, 0));

        Label labelLbl = new Label(label);
        labelLbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        labelLbl.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
        labelLbl.setPadding(new Insets(0, 0, 4, 0));

        Label valueLbl = new Label(value);
        valueLbl.setFont(Font.font("System", FontWeight.BOLD, 30));
        valueLbl.setStyle("-fx-text-fill: " + TEXT_DARK + ";");

        card.getChildren().addAll(iconRow, labelLbl, valueLbl);

        if (barRatio > 0 && barColor != null) {
            // Progress bar
            StackPane barBg = new StackPane();
            barBg.setPrefHeight(5);
            barBg.setMaxWidth(Double.MAX_VALUE);
            barBg.setStyle("-fx-background-color: #e2e8f0; -fx-background-radius: 3;");
            barBg.setPadding(new Insets(0));

            Rectangle barFill = new Rectangle();
            barFill.setHeight(5);
            barFill.setArcWidth(6); barFill.setArcHeight(6);
            barFill.setFill(Color.web(barColor));

            // bind width after layout
            barBg.widthProperty().addListener((obs, o, n) ->
                barFill.setWidth(n.doubleValue() * barRatio)
            );

            StackPane barWrap = new StackPane(barBg, barFill);
            StackPane.setAlignment(barFill, Pos.CENTER_LEFT);
            barWrap.setPadding(new Insets(14, 0, 0, 0));
            barWrap.setMaxWidth(Double.MAX_VALUE);
            card.getChildren().add(barWrap);
            VBox.setVgrow(barWrap, Priority.ALWAYS);

        } else if (note != null) {
            Label noteLbl = new Label(note);
            noteLbl.setFont(Font.font(11));
            noteLbl.setStyle("-fx-text-fill: " + TEXT_LIGHT + ";");
            noteLbl.setPadding(new Insets(10, 0, 0, 0));
            card.getChildren().add(noteLbl);
        }

        return card;
    }

    // ══════════════════════════════════════════════════════════
    //  3. LIST CONTROL BAR
    // ══════════════════════════════════════════════════════════
    private HBox buildListControl() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(12, 16, 12, 16));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle(cardStyle());

        // Trạng thái filter
        HBox filterBox = new HBox(8);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setPadding(new Insets(6, 12, 6, 12));
        filterBox.setStyle(
            "-fx-background-color: " + SURFACE + ";" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;"
        );

        Label statusLbl = new Label("TRẠNG THÁI:");
        statusLbl.setFont(Font.font("System", FontWeight.BOLD, 9.5));
        statusLbl.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-letter-spacing: 0.06em;");

        statusCb = new ComboBox<>(FXCollections.observableArrayList(
            "Tất cả trạng thái", "Đang hoạt động", "Tạm ngưng"
        ));
        statusCb.getSelectionModel().selectFirst();
        statusCb.setPrefHeight(26);
        statusCb.setStyle(
            "-fx-background-color: transparent; -fx-border-color: transparent;" +
            "-fx-font-size: 12; -fx-font-weight: 600;"
        );
        statusCb.setOnAction(e -> applyStatusFilter());

        filterBox.getChildren().addAll(statusLbl, statusCb);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Nút filter + sort
        Button filterBtn = buildIconToolBtn("☰");
        Button sortBtn   = buildIconToolBtn("↕");

        HBox rightBtns = new HBox(6, filterBtn, sortBtn);
        bar.getChildren().addAll(filterBox, spacer, rightBtns);
        return bar;
    }

    private Button buildIconToolBtn(String icon) {
        Button btn = new Button(icon);
        btn.setPrefSize(34, 34);
        btn.setFont(Font.font(14));
        btn.setStyle(
            "-fx-background-color: transparent; -fx-background-radius: 8;" +
            "-fx-text-fill: " + TEXT_MUTED + "; -fx-border-color: transparent; -fx-cursor: hand;"
        );
        hover(btn,
            "-fx-background-color: " + SURFACE + "; -fx-background-radius: 8; -fx-text-fill: " + TEXT_DARK + "; -fx-border-color: transparent; -fx-cursor: hand;",
            "-fx-background-color: transparent; -fx-background-radius: 8; -fx-text-fill: " + TEXT_MUTED + "; -fx-border-color: transparent; -fx-cursor: hand;"
        );
        return btn;
    }

    // ══════════════════════════════════════════════════════════
    //  4. TABLE CARD
    // ══════════════════════════════════════════════════════════
    private VBox buildTableCard() {
        VBox card = new VBox();
        card.setStyle(cardStyle());

        table = new TableView<>();
        table.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-table-cell-border-color: #f1f5f9;" +
            "-fx-font-size: 13;"
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(270);

        // ── Cột: Mã tổ hợp ──
        TableColumn<ToHop, String> colMa = new TableColumn<>("MÃ TỔ HỢP");
        colMa.setCellValueFactory(new PropertyValueFactory<>("ma"));
        colMa.setMinWidth(120);
        colMa.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setGraphic(null); setText(null); return; }
                Label badge = new Label(v);
                badge.setFont(Font.font("System", FontWeight.BOLD, 10));
                badge.setPadding(new Insets(3, 10, 3, 10));
                badge.setStyle(
                    "-fx-background-color: rgba(19,127,236,0.08);" +
                    "-fx-text-fill: " + PRIMARY + ";" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: rgba(19,127,236,0.2);" +
                    "-fx-border-radius: 20;"
                );
                setGraphic(badge);
                setText(null);
                setAlignment(Pos.CENTER_LEFT);
            }
        });

        // ── Cột: Tên tổ hợp ──
        TableColumn<ToHop, String> colTen = new TableColumn<>("TÊN TỔ HỢP");
        colTen.setCellValueFactory(new PropertyValueFactory<>("ten"));
        colTen.setMinWidth(150);
        colTen.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); return; }
                setText(v);
                setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
                setStyle("-fx-text-fill: " + TEXT_DARK + ";");
            }
        });

        // ── Cột: Môn 1, 2, 3 ──
        TableColumn<ToHop, String> colM1 = makeMonCol("MÔN 1", "mon1");
        TableColumn<ToHop, String> colM2 = makeMonCol("MÔN 2", "mon2");
        TableColumn<ToHop, String> colM3 = makeMonCol("MÔN 3", "mon3");

        // ── Cột: Hành động ──
        TableColumn<ToHop, Void> colAction = new TableColumn<>("HÀNH ĐỘNG");
        colAction.setMinWidth(110);
        colAction.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }

                Button editBtn = buildActionBtn("✏", PRIMARY, "rgba(19,127,236,0.1)");
                Button delBtn  = buildActionBtn("🗑", ERROR, "rgba(239,68,68,0.08)");

                editBtn.setOnAction(e -> {
                    ToHop selected = getTableRow() == null ? null : getTableRow().getItem();
                    if (selected != null) {
                        handleEditToHop(selected);
                    }
                });
                delBtn.setOnAction(e -> {
                    ToHop selected = getTableRow() == null ? null : getTableRow().getItem();
                    if (selected != null) {
                        handleDeleteToHop(selected);
                    }
                });

                HBox btns = new HBox(6, editBtn, delBtn);
                btns.setAlignment(Pos.CENTER_RIGHT);
                setGraphic(btns);
                setText(null);
                setAlignment(Pos.CENTER_RIGHT);
            }
        });

        table.getColumns().addAll(colMa, colTen, colM1, colM2, colM3, colAction);
        table.setItems(filteredData);
        applyStatusFilter();

        // ── Pagination ──
        HBox pagination = buildPagination();

        card.getChildren().addAll(table, new Separator(), pagination);
        return card;
    }

    private TableColumn<ToHop, String> makeMonCol(String title, String field) {
        TableColumn<ToHop, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(field));
        col.setMinWidth(120);
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); return; }
                setText(v);
                setFont(Font.font(13));
                setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
            }
        });
        return col;
    }

    private Button buildActionBtn(String icon, String hoverColor, String hoverBg) {
        Button btn = new Button(icon);
        btn.setPrefSize(32, 32);
        btn.setFont(Font.font(13));
        btn.setStyle(
            "-fx-background-color: transparent; -fx-background-radius: 8;" +
            "-fx-border-color: transparent; -fx-cursor: hand;" +
            "-fx-text-fill: " + TEXT_LIGHT + ";"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + hoverBg + "; -fx-background-radius: 8;" +
            "-fx-border-color: transparent; -fx-cursor: hand;" +
            "-fx-text-fill: " + hoverColor + ";"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent; -fx-background-radius: 8;" +
            "-fx-border-color: transparent; -fx-cursor: hand;" +
            "-fx-text-fill: " + TEXT_LIGHT + ";"
        ));
        return btn;
    }

    // ── Pagination ──────────────────────────────────────────
    private HBox buildPagination() {
        HBox box = new HBox(6);
        box.setPadding(new Insets(12, 20, 12, 20));
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setStyle("-fx-background-color: rgba(248,250,252,0.7); -fx-background-radius: 0 0 14 14;");

        paginationInfo = new Label();
        paginationInfo.setFont(Font.font("System", FontWeight.MEDIUM, 11));
        paginationInfo.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
        HBox.setHgrow(paginationInfo, Priority.ALWAYS);

        box.getChildren().add(paginationInfo);

        String[] pages = {"‹", "1", "2", "3", "›"};
        for (String p : pages) {
            boolean active = p.equals("1");
            boolean arrow  = p.equals("‹") || p.equals("›");
            Button btn = new Button(p);
            btn.setPrefSize(32, 32);
            btn.setFont(Font.font("System", active ? FontWeight.BOLD : FontWeight.NORMAL, 12));
            btn.setStyle(
                "-fx-background-color: " + (active ? PRIMARY : "transparent") + ";" +
                "-fx-text-fill: " + (active ? "white" : TEXT_MUTED) + ";" +
                "-fx-background-radius: 8; -fx-border-color: transparent; -fx-cursor: hand;"
            );
            if (!active) {
                hover(btn,
                    "-fx-background-color: " + SURFACE + "; -fx-text-fill: " + TEXT_DARK + "; -fx-background-radius: 8; -fx-border-color: transparent; -fx-cursor: hand;",
                    "-fx-background-color: transparent; -fx-text-fill: " + TEXT_MUTED + "; -fx-background-radius: 8; -fx-border-color: transparent; -fx-cursor: hand;"
                );
            }
            box.getChildren().add(btn);
        }
        updatePaginationInfo();
        return box;
    }

    private void handleAddToHop() {
        ToHopFormData form = showAddDialog();
        if (form == null) {
            return;
        }

        String validationError = validateForm(form, null);
        if (validationError != null) {
            showWarning(validationError);
            return;
        }

        data.add(new ToHop(form.code, form.name, form.subj1, form.subj2, form.subj3, form.status));
        applyStatusFilter();
    }

    private void handleEditToHop(ToHop existing) {
        ToHopFormData form = showEditDialog(existing);
        if (form == null) {
            return;
        }

        String validationError = validateForm(form, existing.getMa());
        if (validationError != null) {
            showWarning(validationError);
            return;
        }

        int index = data.indexOf(existing);
        if (index >= 0) {
            data.set(index, new ToHop(form.code, form.name, form.subj1, form.subj2, form.subj3, form.status));
            applyStatusFilter();
        }
    }

    private void handleDeleteToHop(ToHop selected) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Xóa tổ hợp " + selected.getMa() + "?");
        confirm.setContentText("Thao tác này không thể hoàn tác.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                data.remove(selected);
                applyStatusFilter();
            }
        });
    }

    private ToHopFormData showAddDialog() {
        final ToHopFormData[] holder = new ToHopFormData[1];
        try {
            SwingUtilities.invokeAndWait(() -> {
                AddToHopDialog dialog = new AddToHopDialog((Frame) null);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    holder[0] = new ToHopFormData(
                        dialog.getCode().toUpperCase(),
                        dialog.getNameValue(),
                        dialog.getSubj1(),
                        dialog.getSubj2(),
                        dialog.getSubj3(),
                        dialog.getStatus()
                    );
                }
                dialog.dispose();
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            showWarning("Không thể mở hộp thoại thêm tổ hợp.");
        } catch (InvocationTargetException e) {
            showWarning("Có lỗi khi mở hộp thoại thêm tổ hợp: " + e.getCause().getMessage());
        }
        return holder[0];
    }

    private ToHopFormData showEditDialog(ToHop existing) {
        final ToHopFormData[] holder = new ToHopFormData[1];
        try {
            SwingUtilities.invokeAndWait(() -> {
                EditToHopDialog dialog = new EditToHopDialog(
                    (Frame) null,
                    existing.getMa(),
                    existing.getTen(),
                    existing.getMon1(),
                    existing.getMon2(),
                    existing.getMon3(),
                    existing.getStatus()
                );
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    holder[0] = new ToHopFormData(
                        dialog.getCode().toUpperCase(),
                        dialog.getNameValue(),
                        dialog.getSubj1(),
                        dialog.getSubj2(),
                        dialog.getSubj3(),
                        dialog.getStatus()
                    );
                }
                dialog.dispose();
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            showWarning("Không thể mở hộp thoại chỉnh sửa tổ hợp.");
        } catch (InvocationTargetException e) {
            showWarning("Có lỗi khi mở hộp thoại chỉnh sửa tổ hợp: " + e.getCause().getMessage());
        }
        return holder[0];
    }

    private String validateForm(ToHopFormData form, String currentCode) {
        if (form.code == null || form.code.isBlank()
            || form.name == null || form.name.isBlank()
            || form.subj1 == null || form.subj1.isBlank()
            || form.subj2 == null || form.subj2.isBlank()
            || form.subj3 == null || form.subj3.isBlank()) {
            return "Vui lòng nhập đầy đủ các trường bắt buộc.";
        }

        for (ToHop item : data) {
            if (item.getMa().equalsIgnoreCase(form.code)
                && (currentCode == null || !item.getMa().equalsIgnoreCase(currentCode))) {
                return "Mã tổ hợp đã tồn tại: " + form.code;
            }
        }
        return null;
    }

    private void applyStatusFilter() {
        String status = statusCb == null ? "Tất cả trạng thái" : statusCb.getSelectionModel().getSelectedItem();
        filteredData.setPredicate(item ->
            "Tất cả trạng thái".equals(status) || item.getStatus().equals(status)
        );
        updatePaginationInfo();
    }

    private void updatePaginationInfo() {
        if (paginationInfo == null) {
            return;
        }
        int visible = filteredData.size();
        int total = data.size();
        if (visible == 0) {
            paginationInfo.setText("Không có dữ liệu phù hợp");
            return;
        }
        paginationInfo.setText("Hiển thị 1-" + visible + " trong số " + total + " tổ hợp");
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ══════════════════════════════════════════════════════════
    //  5. FOOTER
    // ══════════════════════════════════════════════════════════
    private HBox buildFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(12, 0, 4, 0));
        footer.setStyle("-fx-border-color: " + BORDER + "; -fx-border-width: 1 0 0 0;");
        Label text = new Label("© 2024 Hệ thống Quản lý Tuyển sinh. Phát triển bởi Đội ngũ Công nghệ thông tin.");
        text.setFont(Font.font(10.5));
        text.setStyle("-fx-text-fill: " + TEXT_LIGHT + ";");
        footer.getChildren().add(text);
        return footer;
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

    private String primaryButtonStyle() {
        return "-fx-background-color: " + PRIMARY + "; -fx-text-fill: white;" +
               "-fx-background-radius: 10; -fx-border-color: transparent; -fx-cursor: hand;" +
               "-fx-effect: dropshadow(gaussian, rgba(19,127,236,0.28), 10, 0, 0, 3);";
    }

    private String outlineButtonStyle() {
        return "-fx-background-color: " + WHITE + "; -fx-text-fill: " + TEXT_DARK + ";" +
               "-fx-background-radius: 10; -fx-border-color: " + BORDER + ";" +
               "-fx-border-radius: 10; -fx-cursor: hand;" +
               "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);";
    }

    private void hover(Button btn, String onEnter, String onExit) {
        btn.setOnMouseEntered(e -> btn.setStyle(onEnter));
        btn.setOnMouseExited(e -> btn.setStyle(onExit));
    }
}