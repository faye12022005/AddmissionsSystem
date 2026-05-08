package org.AdmissionsSystem.gui.modules.QuanLiDiem;

import org.AdmissionsSystem.gui.common.Style;
import org.AdmissionsSystem.gui.modules.QuanLiDiemCong.Toast;
import org.AdmissionsSystem.service.QuanLiDiem.QuanLiDiemService;
import org.AdmissionsSystem.service.QuanLiDiem.QuanLiDiemVSATService;
import org.AdmissionsSystem.service.QuanLiDiem.ThongKeDiemService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYBarRenderer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ThongKeDiemPanel extends JPanel {
	private static final String TYPE_THPT = "THPT";
	private static final String TYPE_DGNL = "ĐGNL";
	private static final String TYPE_VSAT = "VSAT";

	private static final double THPT_MAX = 10.0;
	private static final double DGNL_MAX = 1200.0;
	private static final double VSAT_MAX = 150.0;

	private final JComboBox<String> cboLoaiDiem = new JComboBox<>(new String[] { TYPE_THPT, TYPE_DGNL, TYPE_VSAT });
	private final DefaultListModel<String> subjectModel = new DefaultListModel<>();
	private final JList<String> lstSubjects = new JList<>(subjectModel);
	private final ChartPanel chartPanel = new ChartPanel(null);

	private final JLabel lblCount = new JLabel("0");
	private final JLabel lblMean = new JLabel("0");
	private final JLabel lblStd = new JLabel("0");
	private final JLabel lblMedian = new JLabel("0");
	private final JLabel lblMode = new JLabel("0");
	private final JLabel lblMin = new JLabel("0");
	private final JLabel lblMax = new JLabel("0");
	private final JLabel lblStatus = new JLabel(" ");
	private final JProgressBar progressBar = new JProgressBar();

	private final JButton btnExportCurrent = new JButton("Xuất biểu đồ hiện tại");
	private final JButton btnExportBatch = new JButton("Xuất hàng loạt");

	private final QuanLiDiemService thptService = new QuanLiDiemService();
	private final QuanLiDiemVSATService vsatService = new QuanLiDiemVSATService();
	private final ThongKeDiemService thongKeService = new ThongKeDiemService();

	private final DecimalFormat numberFormat = new DecimalFormat("0.##");

	private JFreeChart currentChart;
	private String currentType = TYPE_THPT;
	private String currentSubject = "";
	private List<Double> currentScores = new ArrayList<>();

	public ThongKeDiemPanel() {
		setLayout(new BorderLayout(8, 8));
		setOpaque(false);

		numberFormat.setGroupingUsed(false);

		JPanel leftPanel = buildLeftPanel();
		JPanel rightPanel = buildRightPanel();

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
		splitPane.setResizeWeight(0.26);
		splitPane.setBorder(BorderFactory.createEmptyBorder());
		splitPane.setDividerLocation(260);

		add(splitPane, BorderLayout.CENTER);

		cboLoaiDiem.addActionListener(e -> onTypeChange());
		lstSubjects.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				refreshChart();
			}
		});

		btnExportCurrent.addActionListener(this::onExportCurrent);
		btnExportBatch.addActionListener(this::onExportBatch);

		progressBar.setVisible(false);
		lblStatus.setForeground(new Color(71, 85, 105));

		updateSubjectList();
		refreshChart();
	}

	private JPanel buildLeftPanel() {
		JPanel leftPanel = new JPanel(new BorderLayout(8, 8));
		leftPanel.setOpaque(false);

		JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
		typePanel.setOpaque(false);
		typePanel.add(new JLabel("Phương thức:"));
		typePanel.add(cboLoaiDiem);

		JPanel subjectPanel = new JPanel(new BorderLayout(6, 6));
		subjectPanel.setOpaque(false);
		TitledBorder subjectBorder = BorderFactory.createTitledBorder("Môn thống kê");
		subjectBorder.setTitleFont(Style.PANEL_TITLE_FONT);
		subjectPanel.setBorder(subjectBorder);

		lstSubjects.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lstSubjects.setVisibleRowCount(10);
		lstSubjects.setFont(Style.TABLE_FONT);
		JScrollPane subjectScroll = new JScrollPane(lstSubjects);
		subjectPanel.add(subjectScroll, BorderLayout.CENTER);

		JPanel exportPanel = new JPanel(new GridLayout(2, 1, 8, 8));
		exportPanel.setOpaque(false);
		Style.styleFunctionButton(btnExportCurrent, Style.BTN_EXPORT, Color.WHITE);
		Style.styleFunctionButton(btnExportBatch, Style.BTN_EXPORT, Color.WHITE);
		exportPanel.add(btnExportCurrent);
		exportPanel.add(btnExportBatch);

		JPanel statusPanel = new JPanel(new BorderLayout(6, 6));
		statusPanel.setOpaque(false);
		statusPanel.add(progressBar, BorderLayout.NORTH);
		statusPanel.add(lblStatus, BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout(8, 8));
		bottom.setOpaque(false);
		bottom.add(exportPanel, BorderLayout.NORTH);
		bottom.add(statusPanel, BorderLayout.CENTER);

		leftPanel.add(typePanel, BorderLayout.NORTH);
		leftPanel.add(subjectPanel, BorderLayout.CENTER);
		leftPanel.add(bottom, BorderLayout.SOUTH);

		return leftPanel;
	}

	private JPanel buildRightPanel() {
		JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
		rightPanel.setOpaque(false);

		chartPanel.setPreferredSize(new Dimension(640, 420));
		chartPanel.setBackground(Color.WHITE);
		chartPanel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

		JPanel summaryPanel = new JPanel(new GridLayout(2, 4, 12, 8));
		summaryPanel.setOpaque(false);
		summaryPanel.setBorder(BorderFactory.createTitledBorder("Thông tin thống kê"));

		summaryPanel.add(buildMetric("Số lượng", lblCount));
		summaryPanel.add(buildMetric("Trung bình", lblMean));
		summaryPanel.add(buildMetric("Độ lệch chuẩn", lblStd));
		summaryPanel.add(buildMetric("Trung vị", lblMedian));
		summaryPanel.add(buildMetric("Mode", lblMode));
		summaryPanel.add(buildMetric("Min", lblMin));
		summaryPanel.add(buildMetric("Max", lblMax));

		rightPanel.add(chartPanel, BorderLayout.CENTER);
		rightPanel.add(summaryPanel, BorderLayout.SOUTH);
		return rightPanel;
	}

	private JPanel buildMetric(String label, JLabel valueLabel) {
		JPanel panel = new JPanel(new BorderLayout(4, 4));
		panel.setOpaque(false);
		JLabel title = new JLabel(label);
		title.setFont(Style.TABLE_FONT.deriveFont(12f));
		valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		valueLabel.setFont(Style.PANEL_TITLE_FONT);
		panel.add(title, BorderLayout.NORTH);
		panel.add(valueLabel, BorderLayout.CENTER);
		return panel;
	}

	private void onTypeChange() {
		currentType = cboLoaiDiem.getSelectedItem() == null ? TYPE_THPT : cboLoaiDiem.getSelectedItem().toString();
		updateSubjectList();
		refreshChart();
	}

	private void updateSubjectList() {
		subjectModel.clear();
		for (String subject : subjectsForType(currentType)) {
			subjectModel.addElement(subject);
		}
		if (!subjectModel.isEmpty()) {
			lstSubjects.setSelectedIndex(0);
		}
	}

	private List<String> subjectsForType(String type) {
		List<String> subjects = new ArrayList<>();
		if (TYPE_VSAT.equals(type)) {
			subjects.add("Toán");
			subjects.add("Văn");
			subjects.add("Anh");
			subjects.add("Lý");
			subjects.add("Hóa");
			subjects.add("Sinh");
			subjects.add("Sử");
			subjects.add("Địa");
		} else if (TYPE_DGNL.equals(type)) {
			subjects.add("NL1");
		} else {
			subjects.add("Toán");
			subjects.add("Lý");
			subjects.add("Hóa");
			subjects.add("Sinh");
			subjects.add("Sử");
			subjects.add("Địa");
			subjects.add("Văn");
			subjects.add("GDCD");
			subjects.add("N1_THI");
			subjects.add("N1_CC");
			subjects.add("CNCN");
			subjects.add("CNNN");
			subjects.add("Tin học");
			subjects.add("KTPL");
			subjects.add("NK1");
			subjects.add("NK2");
			subjects.add("NK3");
			subjects.add("NK4");
			subjects.add("NK5");
			subjects.add("NK6");
		}
		return subjects;
	}

	private void refreshChart() {
		List<String> selected = lstSubjects.getSelectedValuesList();
		if (selected.isEmpty()) {
			if (!subjectModel.isEmpty()) {
				lstSubjects.setSelectedIndex(0);
			}
			return;
		}

		currentSubject = selected.get(0);
		currentScores = collectScores(currentType, currentSubject);
		currentChart = buildChart(currentType, currentSubject, currentScores);
		chartPanel.setChart(currentChart);
		updateSummary(currentScores);
	}

	private List<Double> collectScores(String type, String subject) {
		List<Double> scores = new ArrayList<>();
		if (TYPE_VSAT.equals(type)) {
			List<QuanLiDiemVSATService.VsatRecord> records = vsatService.query("");
			for (QuanLiDiemVSATService.VsatRecord record : records) {
				Double score = extractVsatScore(record, subject);
				if (score != null) {
					scores.add(score);
				}
			}
			return scores;
		}

		String filter = TYPE_DGNL.equals(type) ? TYPE_DGNL : TYPE_THPT;
		List<QuanLiDiemService.DiemRecord> records = thptService.query("", filter);
		for (QuanLiDiemService.DiemRecord record : records) {
			Double score = extractThptScore(record, subject);
			if (score != null) {
				scores.add(score);
			}
		}
		return scores;
	}

	private Double extractThptScore(QuanLiDiemService.DiemRecord record, String subject) {
		if (record == null || subject == null) {
			return null;
		}
		return switch (subject) {
			case "Toán" -> toDouble(record.to());
			case "Lý" -> toDouble(record.li());
			case "Hóa" -> toDouble(record.ho());
			case "Sinh" -> toDouble(record.si());
			case "Sử" -> toDouble(record.su());
			case "Địa" -> toDouble(record.di());
			case "Văn" -> toDouble(record.va());
			case "GDCD" -> toDouble(record.gdcd());
			case "N1_THI" -> toDouble(record.n1Thi());
			case "N1_CC" -> toDouble(record.n1Cc());
			case "CNCN" -> toDouble(record.cncn());
			case "CNNN" -> toDouble(record.cnnn());
			case "Tin học" -> toDouble(record.ti());
			case "KTPL" -> toDouble(record.ktpl());
			case "NL1" -> toDouble(record.nl1());
			case "NK1" -> toDouble(record.nk1());
			case "NK2" -> toDouble(record.nk2());
			case "NK3" -> toDouble(record.nk3());
			case "NK4" -> toDouble(record.nk4());
			case "NK5" -> toDouble(record.nk5());
			case "NK6" -> toDouble(record.nk6());
			default -> null;
		};
	}

	private Double extractVsatScore(QuanLiDiemVSATService.VsatRecord record, String subject) {
		if (record == null || subject == null) {
			return null;
		}
		return switch (subject) {
			case "Toán" -> toDouble(record.toan());
			case "Văn" -> toDouble(record.van());
			case "Anh" -> toDouble(record.anh());
			case "Lý" -> toDouble(record.ly());
			case "Hóa" -> toDouble(record.hoa());
			case "Sinh" -> toDouble(record.sinh());
			case "Sử" -> toDouble(record.su());
			case "Địa" -> toDouble(record.dia());
			default -> null;
		};
	}

	private JFreeChart buildChart(String type, String subject, List<Double> scores) {
		HistogramDataset dataset = new HistogramDataset();
		dataset.setType(HistogramType.FREQUENCY);

		double maxRange = resolveMaxRange(type, subject);
		int bins = resolveBins(maxRange);

		if (scores != null && !scores.isEmpty()) {
			double[] values = scores.stream().mapToDouble(Double::doubleValue).toArray();
			dataset.addSeries("Điểm", values, bins, 0, maxRange);
		}

		String title = "Phân bố điểm " + subject + " (" + type + ")";
		JFreeChart chart = ChartFactory.createHistogram(
				title,
				"Điểm",
				"Tần suất",
				dataset);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(new Color(226, 232, 240));
		plot.setRangeGridlinePaint(new Color(226, 232, 240));

		// Make histogram bars semi-transparent so the curve is more visible
		XYBarRenderer barRenderer = (XYBarRenderer) plot.getRenderer();
		barRenderer.setSeriesPaint(0, new Color(79, 129, 189, 180)); // Semi-transparent blue
		barRenderer.setDrawBarOutline(true);
		barRenderer.setSeriesOutlinePaint(0, new Color(79, 129, 189));

		NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		domainAxis.setRange(0, maxRange);

		if (scores != null && scores.size() > 1) {
			addNormalCurve(plot, scores, maxRange, bins);
		}

		return chart;
	}

	private void addNormalCurve(XYPlot plot, List<Double> scores, double maxRange, int bins) {
		ThongKeDiemService.ScoreSummary summary = thongKeService.summarize(toBigDecimals(scores));
		if (summary.stdDev() == null || summary.stdDev().compareTo(java.math.BigDecimal.ZERO) <= 0) {
			return;
		}

		double mean = summary.mean().doubleValue();
		double std = summary.stdDev().doubleValue();
		if (std == 0) {
			return;
		}

		double binWidth = maxRange / bins;
		double scale = summary.count() * binWidth;

		XYSeries series = new XYSeries("Đường chuẩn");
		int steps = 200;
		for (int i = 0; i <= steps; i++) {
			double x = (maxRange * i) / steps;
			double y = normalPdf(x, mean, std) * scale;
			series.add(x, y);
		}

		XYSeriesCollection normalDataset = new XYSeriesCollection();
		normalDataset.addSeries(series);

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, new Color(220, 38, 38)); // Red color for better visibility
		renderer.setSeriesStroke(0, new java.awt.BasicStroke(2.5f)); // Thicker line
		renderer.setSeriesShapesVisible(0, false);

		// Add the normal curve dataset at index 1 (rendered on top of histogram at
		// index 0)
		plot.setDataset(1, normalDataset);
		plot.setRenderer(1, renderer);
	}

	private double normalPdf(double x, double mean, double std) {
		double variance = std * std;
		double denom = Math.sqrt(2 * Math.PI * variance);
		double exponent = -((x - mean) * (x - mean)) / (2 * variance);
		return Math.exp(exponent) / denom;
	}

	private void updateSummary(List<Double> scores) {
		ThongKeDiemService.ScoreSummary summary = thongKeService.summarize(toBigDecimals(scores));
		lblCount.setText(String.valueOf(summary.count()));
		lblMean.setText(formatNumber(summary.mean()));
		lblStd.setText(formatNumber(summary.stdDev()));
		lblMedian.setText(formatNumber(summary.median()));
		lblMode.setText(formatNumber(summary.mode()));
		lblMin.setText(formatNumber(summary.min()));
		lblMax.setText(formatNumber(summary.max()));
	}

	private void onExportCurrent(ActionEvent event) {
		if (currentChart == null || currentScores.isEmpty()) {
			Toast.showToast(this, "Không có dữ liệu để xuất.", true);
			return;
		}

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Lưu biểu đồ thống kê");

		FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG (*.png)", "png");
		FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter("PDF (*.pdf)", "pdf");
		chooser.addChoosableFileFilter(pngFilter);
		chooser.addChoosableFileFilter(pdfFilter);
		chooser.setFileFilter(pngFilter);

		int result = chooser.showSaveDialog(this);
		if (result != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File selected = chooser.getSelectedFile();
		boolean exportPdf = chooser.getFileFilter() == pdfFilter
				|| selected.getName().toLowerCase(Locale.ROOT).endsWith(".pdf");
		String extension = exportPdf ? ".pdf" : ".png";
		Path out = ensureExtension(selected.toPath(), extension);

		try {
			if (exportPdf) {
				writeChartPdf(out, currentChart, 960, 540);
			} else {
				ChartUtils.saveChartAsPNG(out.toFile(), currentChart, 960, 540);
			}
			Toast.showToast(this, "Đã xuất biểu đồ.", false);
		} catch (IOException ex) {
			Toast.showToast(this, "Không thể xuất biểu đồ: " + ex.getMessage(), true);
		}
	}

	private void onExportBatch(ActionEvent event) {
		List<String> selectedSubjects = lstSubjects.getSelectedValuesList();
		if (selectedSubjects.isEmpty()) {
			Toast.showToast(this, "Vui lòng chọn ít nhất một môn để xuất.", true);
			return;
		}

		Object[] options = { "PNG", "PDF" };
		int choice = JOptionPane.showOptionDialog(
				this,
				"Chọn định dạng xuất cho các biểu đồ.",
				"Định dạng xuất",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);

		if (choice < 0) {
			return;
		}

		boolean exportPdf = choice == 1;

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Lưu file zip biểu đồ");
		chooser.setFileFilter(new FileNameExtensionFilter("ZIP (*.zip)", "zip"));

		int result = chooser.showSaveDialog(this);
		if (result != JFileChooser.APPROVE_OPTION) {
			return;
		}

		Path out = ensureExtension(chooser.getSelectedFile().toPath(), ".zip");
		runBatchExport(out, selectedSubjects, exportPdf);
	}

	private void runBatchExport(Path out, List<String> subjects, boolean exportPdf) {
		btnExportBatch.setEnabled(false);
		btnExportCurrent.setEnabled(false);
		progressBar.setVisible(true);
		progressBar.setMinimum(0);
		progressBar.setMaximum(subjects.size());
		progressBar.setValue(0);
		lblStatus.setText("Đang xuất 0/" + subjects.size());

		SwingWorker<Void, Integer> worker = new SwingWorker<>() {
			@Override
			protected Void doInBackground() throws Exception {
				try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(out))) {
					int index = 0;
					for (String subject : subjects) {
						if (isCancelled()) {
							break;
						}

						List<Double> scores = collectScores(currentType, subject);
						if (!scores.isEmpty()) {
							JFreeChart chart = buildChart(currentType, subject, scores);
							byte[] content = exportPdf
									? buildPdfBytes(chart, 960, 540)
									: buildPngBytes(chart, 960, 540);
							String entryName = buildEntryName(currentType, subject, exportPdf ? "pdf" : "png");
							zos.putNextEntry(new ZipEntry(entryName));
							zos.write(content);
							zos.closeEntry();
						}

						index++;
						publish(index);
					}
				}
				return null;
			}

			@Override
			protected void process(List<Integer> chunks) {
				int value = chunks.get(chunks.size() - 1);
				progressBar.setValue(value);
				lblStatus.setText("Đang xuất " + value + "/" + subjects.size());
			}

			@Override
			protected void done() {
				progressBar.setVisible(false);
				btnExportBatch.setEnabled(true);
				btnExportCurrent.setEnabled(true);
				lblStatus.setText("Hoàn tất xuất biểu đồ.");
				Toast.showToast(ThongKeDiemPanel.this, "Xuất biểu đồ hoàn tất.", false);
			}
		};

		worker.execute();
	}

	private byte[] buildPngBytes(JFreeChart chart, int width, int height) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ChartUtils.writeChartAsPNG(out, chart, width, height);
		return out.toByteArray();
	}

	private byte[] buildPdfBytes(JFreeChart chart, int width, int height) throws IOException {
		BufferedImage image = chart.createBufferedImage(width, height);
		try (PDDocument document = new PDDocument()) {
			PDRectangle pageSize = new PDRectangle(width, height);
			PDPage page = new PDPage(pageSize);
			document.addPage(page);

			PDImageXObject pdImage = LosslessFactory.createFromImage(document, image);
			try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
				contentStream.drawImage(pdImage, 0, 0, width, height);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.save(out);
			return out.toByteArray();
		}
	}

	private void writeChartPdf(Path out, JFreeChart chart, int width, int height) throws IOException {
		Files.write(out, buildPdfBytes(chart, width, height));
	}

	private String buildEntryName(String type, String subject, String ext) {
		String base = (type + "_" + subject).toLowerCase(Locale.ROOT)
				.replace("đ", "d")
				.replace("/", "_")
				.replace(" ", "_")
				.replace("-", "_");
		base = base.replaceAll("[^a-z0-9_]+", "");
		return base + "." + ext;
	}

	private Path ensureExtension(Path path, String extension) {
		String name = path.toString();
		if (name.toLowerCase(Locale.ROOT).endsWith(extension)) {
			return path;
		}
		return Path.of(name + extension);
	}

	private double resolveMaxRange(String type, String subject) {
		if (TYPE_VSAT.equals(type)) {
			return VSAT_MAX;
		}
		if (TYPE_DGNL.equals(type) || "NL1".equals(subject)) {
			return DGNL_MAX;
		}
		return THPT_MAX;
	}

	private int resolveBins(double maxRange) {
		if (maxRange <= THPT_MAX) {
			return 20;
		}
		if (maxRange <= VSAT_MAX) {
			return 30;
		}
		return 40;
	}

	private String formatNumber(java.math.BigDecimal value) {
		if (value == null) {
			return "0";
		}
		return numberFormat.format(value);
	}

	private Double toDouble(java.math.BigDecimal value) {
		return value == null ? null : value.doubleValue();
	}

	private List<java.math.BigDecimal> toBigDecimals(List<Double> scores) {
		List<java.math.BigDecimal> list = new ArrayList<>();
		if (scores == null) {
			return list;
		}
		for (Double value : scores) {
			if (value != null) {
				list.add(java.math.BigDecimal.valueOf(value));
			}
		}
		return list;
	}
}
