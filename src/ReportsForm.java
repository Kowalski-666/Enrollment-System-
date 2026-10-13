import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/** Form 5: Reports / Summary - all values are computed from the real records. */
public class ReportsForm extends JFrame {
    private final DataStore store = DataStore.get();
    private final JPanel overview = new JPanel(new BorderLayout(0, 16));
    private final DefaultTableModel courseModel = UI.model("Course", "Total", "Active", "Inactive", "Graduated");
    private final DefaultTableModel subjectModel = UI.model(
            "Course", "Code", "Title", "Units", "Capacity", "Enrolled",
            "Available", "Utilization");
    private final DefaultTableModel billingModel = UI.model("Student ID", "Name", "Units", "Tuition",
            "Misc Fee", "Total Fee");
    private final JLabel lblBillingTotal = new JLabel(" ");
        private final JLabel lblBillingCount = new JLabel(" ");
        private final JTextField tfBillingSearch = new JTextField();
        private final JComboBox<String> cbBillingCourse = new JComboBox<>(
            withAll("All Courses", DataStore.COURSES)
        );
        private final JComboBox<String> cbBillingYear = new JComboBox<>(
            withAll("All Years", DataStore.YEARS)
        );

        private static String[] withAll(String all, String[] values) {
        String[] options = new String[values.length + 1];
        options[0] = all;
        System.arraycopy(values, 0, options, 1, values.length);
        return options;
        }

    public ReportsForm() {
        super("Reports / Summary");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(950, 620);
        setMinimumSize(new Dimension(640, 420));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(UI.header("Reports / Summary"), BorderLayout.NORTH);

        overview.setBackground(UI.BG);
        overview.setBorder(new EmptyBorder(18, 18, 18, 18));
        overview.setPreferredSize(new Dimension(1080, 1120));

        JTable courseTable = new JTable(courseModel);
        JTable subjectTable = new JTable(subjectModel);
        JTable billingTable = new JTable(billingModel);
        styleReportTable(courseTable);
        styleReportTable(subjectTable);
        styleReportTable(billingTable);

        JPanel billingPanel = new JPanel(new BorderLayout());
        billingPanel.setBackground(Color.WHITE);
        billingPanel.add(buildBillingToolbar(), BorderLayout.NORTH);
        lblBillingTotal.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblBillingTotal.setForeground(UI.PRIMARY);
        lblBillingTotal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UI.BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        billingPanel.add(lblBillingTotal, BorderLayout.SOUTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UI.BG);
        tabs.setForeground(UI.PRIMARY);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 16));
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        JScrollPane overviewScroll = new JScrollPane(
            overview,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        overviewScroll.setBorder(BorderFactory.createLineBorder(UI.BORDER));
        overviewScroll.getViewport().setBackground(UI.BG);
        overviewScroll.getVerticalScrollBar().setUnitIncrement(20);
        JScrollPane courseScroll = reportTableScroll(courseTable);
        JScrollPane subjectScroll = reportTableScroll(subjectTable);
        JScrollPane billingScroll = reportTableScroll(billingTable);
        billingPanel.add(billingScroll, BorderLayout.CENTER);

        tabs.addTab("Overview", overviewScroll);
        tabs.addTab("Students per Course", courseScroll);
        tabs.addTab("Subject Enrollment", subjectScroll);
        tabs.addTab("Billing Summary", billingPanel);

        JPanel reportContent = new JPanel(new BorderLayout());
        reportContent.setBackground(UI.BG);
        reportContent.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        reportContent.add(tabs, BorderLayout.CENTER);
        add(UI.scrollable(reportContent, 1080, 580), BorderLayout.CENTER);

        JButton btnRefresh = UI.button("Refresh", UI.PRIMARY);
        JButton btnExport = UI.button("Export Report to Image", UI.SUCCESS);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottom.setBackground(UI.BG);
        bottom.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        bottom.add(btnRefresh);
        bottom.add(btnExport);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> refresh());
        btnExport.addActionListener(e -> exportImage());
        refresh();
        UI.enlargeText(this);
    }

    private JScrollPane reportTableScroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UI.BORDER));
        scroll.getViewport().setBackground(Color.WHITE);
        UI.fillTableViewport(table, scroll);
        return scroll;
    }

    private void styleReportTable(JTable table) {
        UI.style(table);
        JTableHeader header = table.getTableHeader();
        header.setOpaque(true);
        header.setBackground(UI.PRIMARY);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(0, 48));

        DefaultTableCellRenderer headerRenderer =
                new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable source,
                    Object value,
                    boolean selected,
                    boolean focused,
                    int row,
                    int column) {
                String title = value == null ? "" : value.toString();
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        source, title, false, false, row, column
                );
                label.setText(title);
                label.setOpaque(true);
                label.setBackground(UI.PRIMARY);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("SansSerif", Font.BOLD, 16));
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 1, new Color(75, 99, 137)
                        ),
                        BorderFactory.createEmptyBorder(0, 12, 0, 12)
                ));
                return label;
            }
        };

        header.setDefaultRenderer(headerRenderer);
        for (int column = 0; column < table.getColumnModel().getColumnCount(); column++) {
            TableColumn tableColumn =
                    table.getColumnModel().getColumn(column);
            Object title = tableColumn.getHeaderValue();
            tableColumn.setHeaderValue(
                    title == null ? "" : title.toString()
            );
            tableColumn.setHeaderRenderer(headerRenderer);
            tableColumn.setPreferredWidth(
                    Math.max(tableColumn.getPreferredWidth(), 120)
            );
        }
        header.revalidate();
        header.repaint();
    }

    public void refresh() {
        buildOverviewDashboard();
        buildCourseTable();
        buildSubjectTable();
        buildBillingTable();
    }

        private void buildOverviewDashboard() {
        List<Student> students = store.getStudents();
        Map<String, Double> studentsByCourse = new LinkedHashMap<>();
        Map<String, Double> studentsByYear = new LinkedHashMap<>();
        Map<String, Double> studentsByStatus = new LinkedHashMap<>();
        Map<String, Double> enrollmentsByStatus = new LinkedHashMap<>();
        Map<String, Double> feesByCourse = new LinkedHashMap<>();

        for (String course : DataStore.COURSES) {
            studentsByCourse.put(course, 0.0);
            feesByCourse.put(course, 0.0);
        }
        for (String year : DataStore.YEARS) {
            studentsByYear.put(year, 0.0);
        }
        studentsByStatus.put("Active", 0.0);
        studentsByStatus.put("Inactive", 0.0);
        studentsByStatus.put("Graduated", 0.0);
        enrollmentsByStatus.put("Enrolled", 0.0);
        enrollmentsByStatus.put("Dropped", 0.0);

        double totalAssessed = 0;
        for (Student s : students) {
            studentsByCourse.computeIfPresent(
                s.getCourse(), (key, count) -> count + 1
            );
            studentsByYear.computeIfPresent(
                s.getYearLevel(), (key, count) -> count + 1
            );
            studentsByStatus.computeIfPresent(
                s.getStatus(), (key, count) -> count + 1
            );

            double assessed = DataStore.totalFeeFor(
                store.totalUnits(s.getId())
            );
            totalAssessed += assessed;
            feesByCourse.computeIfPresent(
                s.getCourse(), (key, amount) -> amount + assessed
            );
        }

        int enrolledCount = 0;
        int droppedCount = 0;
        for (Enrollment enrollment : store.getEnrollments()) {
            String status = enrollment.isEnrolled() ? "Enrolled" : "Dropped";
            enrollmentsByStatus.computeIfPresent(
                status, (key, count) -> count + 1
            );
            if (enrollment.isEnrolled()) enrolledCount++;
            else droppedCount++;
        }

        String popular = "-";
        int best = -1;
        for (Subject s : store.getSubjects()) {
            int c = store.countEnrolled(s.getCode());
            if (c > best) {
            best = c;
            popular = s.getCode() + " - " + s.getTitle()
                + " (" + c + " students)";
            }
        }

        JPanel heading = new JPanel(new BorderLayout(12, 0));
        heading.setOpaque(false);
        JLabel title = new JLabel("STUDENT MANAGEMENT SUMMARY");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(UI.PRIMARY);
        JLabel generated = new JLabel("Generated "
            + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            ));
        generated.setFont(new Font("SansSerif", Font.PLAIN, 14));
        generated.setForeground(UI.GRAY);
        heading.add(title, BorderLayout.WEST);
        heading.add(generated, BorderLayout.EAST);

        JPanel metrics = new JPanel(new GridLayout(1, 4, 12, 0));
        metrics.setOpaque(false);
        metrics.add(metricCard(
            "TOTAL STUDENTS", String.valueOf(students.size()), UI.ACCENT
        ));
        metrics.add(metricCard(
            "ACTIVE STUDENTS",
            String.valueOf(studentsByStatus.get("Active").intValue()),
            UI.DANGER
        ));
        metrics.add(metricCard(
            "ACTIVE ENROLLMENTS", String.valueOf(enrolledCount), UI.ACCENT
        ));
        metrics.add(metricCard(
            "ASSESSED FEES", UI.peso(totalAssessed), UI.PRIMARY
        ));

        JPanel summaryHeader = new JPanel(new BorderLayout(0, 14));
        summaryHeader.setOpaque(false);
        summaryHeader.add(heading, BorderLayout.NORTH);
        summaryHeader.add(metrics, BorderLayout.CENTER);

        JPanel charts = new JPanel(new GridLayout(2, 2, 14, 14));
        charts.setOpaque(false);
        charts.add(new BarChartPanel(
            "Students by Course", studentsByCourse, false, UI.ACCENT
        ));
        charts.add(new BarChartPanel(
            "Students by Year Level", studentsByYear, false, UI.ACCENT
        ));
        charts.add(new BarChartPanel(
            "Students by Status", studentsByStatus, false, UI.ACCENT
        ));
        charts.add(new BarChartPanel(
            "Enrollment Status", enrollmentsByStatus, false, UI.ACCENT
        ));

        JPanel financialChart = new BarChartPanel(
            "Assessed Fees by Course", feesByCourse, true, UI.ACCENT
        );
        JPanel popularSubject = new JPanel(new BorderLayout(10, 0));
        popularSubject.setBackground(Color.WHITE);
        popularSubject.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, UI.DANGER),
            new EmptyBorder(12, 16, 12, 16)
        ));
        JLabel popularTitle = new JLabel("MOST POPULAR SUBJECT");
        popularTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        popularTitle.setForeground(UI.DANGER);
        JLabel popularValue = new JLabel(popular);
        popularValue.setFont(new Font("SansSerif", Font.BOLD, 16));
        popularValue.setForeground(UI.PRIMARY);
        popularSubject.add(popularTitle, BorderLayout.WEST);
        popularSubject.add(popularValue, BorderLayout.CENTER);

        JPanel chartArea = new JPanel(new BorderLayout(0, 14));
        chartArea.setOpaque(false);
        chartArea.add(charts, BorderLayout.NORTH);
        chartArea.add(financialChart, BorderLayout.CENTER);

        overview.removeAll();
        overview.add(summaryHeader, BorderLayout.NORTH);
        overview.add(chartArea, BorderLayout.CENTER);
        overview.add(popularSubject, BorderLayout.SOUTH);
        overview.revalidate();
        overview.repaint();
        }

        private JPanel metricCard(String labelText, String valueText, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
            new EmptyBorder(12, 14, 12, 10)
        ));
        card.setPreferredSize(new Dimension(240, 88));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(UI.GRAY);
        JLabel value = new JLabel(valueText);
        value.setFont(new Font("SansSerif", Font.BOLD, 20));
        value.setForeground(UI.PRIMARY);
        card.add(label);
        card.add(Box.createVerticalStrut(8));
        card.add(value);
        return card;
        }

        private static class BarChartPanel extends JPanel {
        private final Map<String, Double> values;
        private final boolean currency;
        private final Color barColor;
        private final String title;

        BarChartPanel(
            String title,
            Map<String, Double> values,
            boolean currency,
            Color barColor
        ) {
            this.title = title;
            this.values = new LinkedHashMap<>(values);
            this.currency = currency;
            this.barColor = barColor;
            setOpaque(true);
            setBackground(Color.WHITE);
            setFont(new Font("SansSerif", Font.PLAIN, 14));
            setBorder(BorderFactory.createLineBorder(UI.BORDER));
            setPreferredSize(new Dimension(500, 250));
            setMinimumSize(new Dimension(360, 230));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );
            g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );

            Insets insets = getInsets();
            int left = insets.left + 14;
            int right = getWidth() - insets.right - 14;
            g.setFont(new Font("SansSerif", Font.BOLD, 17));
            g.setColor(UI.PRIMARY);
            g.drawString(title, left, insets.top + 25);

            int labelWidth = Math.min(118, Math.max(88, getWidth() / 4));
            int valueWidth = currency ? 92 : 48;
            int barX = left + labelWidth + 8;
            int barWidth = Math.max(20, right - barX - valueWidth - 8);
            int rowHeight = Math.max(
                22,
                Math.min(30, (getHeight() - insets.top - insets.bottom - 54)
                    / Math.max(1, values.size()))
            );
            int firstRowY = insets.top + 50;
            double maxValue = 0;
            for (double value : values.values()) {
            maxValue = Math.max(maxValue, value);
            }
            if (maxValue <= 0) maxValue = 1;

            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            FontMetrics metrics = g.getFontMetrics();
            int row = 0;
            for (Map.Entry<String, Double> entry : values.entrySet()) {
            int y = firstRowY + row * rowHeight;
            String label = entry.getKey();
            g.setColor(UI.GRAY);
            g.drawString(label, left, y + metrics.getAscent());

            g.setColor(new Color(239, 243, 249));
            g.fillRoundRect(barX, y + 2, barWidth, 16, 8, 8);
            int fillWidth = entry.getValue() <= 0 ? 0
                : Math.max(3, (int) (barWidth * entry.getValue() / maxValue));
            g.setColor(label.equals("Dropped") || label.equals("Inactive")
                || label.equals("Graduated") ? UI.DANGER : barColor);
            if (fillWidth > 0) {
                g.fillRoundRect(barX, y + 2, fillWidth, 16, 8, 8);
            }

            String valueLabel = currency
                ? String.format("PHP %,.0f", entry.getValue())
                : String.format("%,.0f", entry.getValue());
            g.setColor(UI.PRIMARY);
            g.drawString(valueLabel, right - metrics.stringWidth(valueLabel),
                y + metrics.getAscent());
            row++;
            }
            g.dispose();
        }
    }

    private void buildCourseTable() {
        courseModel.setRowCount(0);
        List<Student> students = store.getStudents();
        for (String c : DataStore.COURSES) {
            int total = 0, a = 0, i = 0, g = 0;
            for (Student s : students) {
                if (!s.getCourse().equals(c)) continue;
                total++;
                if ("Active".equals(s.getStatus())) a++;
                else if ("Inactive".equals(s.getStatus())) i++;
                else g++;
            }
            courseModel.addRow(new Object[]{c, total, a, i, g});
        }
    }

    private void buildSubjectTable() {
        subjectModel.setRowCount(0);
        for (Subject s : store.getSubjects()) {
            int taken = store.countEnrolled(s.getCode());
            double pct = s.getCapacity() == 0 ? 0 : taken * 100.0 / s.getCapacity();
            subjectModel.addRow(new Object[]{
                    s.getCourse(), s.getCode(), s.getTitle(),
                    s.getUnits(), s.getCapacity(), taken,
                    s.getCapacity() - taken, String.format("%.1f%%", pct)
            });
        }
    }

    private void buildBillingTable() {
        billingModel.setRowCount(0);
        double grand = 0;
        int totalWithBilling = 0;
        int shown = 0;
        String query = tfBillingSearch.getText().trim().toLowerCase(Locale.ROOT);
        String courseFilter = (String) cbBillingCourse.getSelectedItem();
        String yearFilter = (String) cbBillingYear.getSelectedItem();

        for (Student s : store.getStudents()) {
            int units = store.totalUnits(s.getId());
            if (units == 0) continue;
            totalWithBilling++;

            if (!query.isEmpty()) {
                String searchable = s.getId() + " " + s.getFullName()
                        + " " + s.getEmail();
                if (!searchable.toLowerCase(Locale.ROOT).contains(query)) {
                    continue;
                }
            }
            if (courseFilter != null && !"All Courses".equals(courseFilter)
                    && !courseFilter.equals(s.getCourse())) {
                continue;
            }
            if (yearFilter != null && !"All Years".equals(yearFilter)
                    && !yearFilter.equals(s.getYearLevel())) {
                continue;
            }

            double total = DataStore.totalFeeFor(units);
            grand += total;
            billingModel.addRow(new Object[]{s.getId(), s.getFullName(), units,
                    UI.peso(DataStore.tuitionFor(units)), UI.peso(DataStore.miscFor(units)), UI.peso(total)});
            shown++;
        }
        lblBillingCount.setText(
                "Showing " + shown + " of " + totalWithBilling + " billed students"
        );
        lblBillingTotal.setText(
                "Total for visible students: " + UI.peso(grand)
        );
    }

    private JPanel buildBillingToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(0, 10));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel toolbarHeading = new JPanel(new BorderLayout());
        toolbarHeading.setOpaque(false);
        JLabel title = new JLabel("Find a student");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(UI.PRIMARY);
        lblBillingCount.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblBillingCount.setForeground(UI.GRAY);
        toolbarHeading.add(title, BorderLayout.WEST);
        toolbarHeading.add(lblBillingCount, BorderLayout.EAST);

        JPanel controls = new JPanel(new GridBagLayout());
        controls.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.SOUTHWEST;
        g.insets = new Insets(0, 0, 0, 12);

        JPanel searchGroup = new JPanel(new BorderLayout(0, 6));
        searchGroup.setOpaque(false);
        JLabel searchLabel = new JLabel("Student ID or name");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        searchLabel.setForeground(UI.PRIMARY);
        tfBillingSearch.setFont(new Font("SansSerif", Font.PLAIN, 18));
        tfBillingSearch.setToolTipText("Search by student ID, name, or email");
        tfBillingSearch.setPreferredSize(new Dimension(440, 48));
        tfBillingSearch.setBorder(BorderFactory.createCompoundBorder(
                new UI.RoundedBorder(UI.BORDER, 16, 1),
                new EmptyBorder(10, 13, 10, 13)
        ));
        searchGroup.add(searchLabel, BorderLayout.NORTH);
        searchGroup.add(tfBillingSearch, BorderLayout.CENTER);

        g.gridx = 0;
        g.weightx = 1;
        controls.add(searchGroup, g);

        g.gridx = 1;
        g.weightx = 0;
        controls.add(billingFilter("Course", cbBillingCourse), g);

        g.gridx = 2;
        controls.add(billingFilter("Year level", cbBillingYear), g);

        JButton btnReset = UI.button("Reset", UI.PRIMARY);
        btnReset.setPreferredSize(new Dimension(120, 48));
        btnReset.addActionListener(e -> {
            tfBillingSearch.setText("");
            cbBillingCourse.setSelectedIndex(0);
            cbBillingYear.setSelectedIndex(0);
        });
        g.gridx = 3;
        g.insets = new Insets(0, 0, 0, 0);
        controls.add(btnReset, g);

        tfBillingSearch.getDocument().addDocumentListener(
                new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                buildBillingTable();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                buildBillingTable();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                buildBillingTable();
            }
        });
        cbBillingCourse.addActionListener(e -> buildBillingTable());
        cbBillingYear.addActionListener(e -> buildBillingTable());

        toolbar.add(toolbarHeading, BorderLayout.NORTH);
        toolbar.add(controls, BorderLayout.CENTER);
        return toolbar;
    }

    private JPanel billingFilter(String title, JComboBox<String> combo) {
        JPanel group = new JPanel(new BorderLayout(0, 6));
        group.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setForeground(UI.PRIMARY);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        combo.setPreferredSize(new Dimension(190, 48));
        group.add(label, BorderLayout.NORTH);
        group.add(combo, BorderLayout.CENTER);
        return group;
    }

    private void exportImage() {
        JFileChooser chooser = new JFileChooser(new File("data"));
        chooser.setDialogTitle("Export Report as Image");
        chooser.setSelectedFile(new File("summary_report.png"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".png")) {
            file = new File(file.getParentFile(), file.getName() + ".png");
        }

        Dimension imageSize = overview.getPreferredSize();
        imageSize.width = Math.max(imageSize.width, overview.getWidth());
        imageSize.height = Math.max(imageSize.height, overview.getHeight());
        overview.setSize(imageSize);
        layoutForImage(overview);

        BufferedImage image = new BufferedImage(
                imageSize.width,
                imageSize.height,
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(UI.BG);
        graphics.fillRect(0, 0, imageSize.width, imageSize.height);
        overview.printAll(graphics);
        graphics.dispose();

        try {
            ImageIO.write(image, "png", file);
        } catch (IOException ex) {
            UI.error(this, "Could not save the image: " + ex.getMessage());
            return;
        }
        UI.info(this, "Report image saved to:\n" + file.getAbsolutePath());
    }

    private void layoutForImage(Container container) {
        container.doLayout();
        for (Component child : container.getComponents()) {
            if (child instanceof Container) {
                layoutForImage((Container) child);
            }
        }
    }
}
