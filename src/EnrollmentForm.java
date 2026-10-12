
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class EnrollmentForm extends JFrame {
    private final Color BLUE = UI.ACCENT;
    private final Color DARK_BLUE = UI.PRIMARY;
    private final Color BACKGROUND = UI.BG;
    private final Color WHITE = Color.WHITE;
    private final Color TEXT = UI.PRIMARY;
    private final Color GRAY = UI.GRAY;
    private final Color BORDER = UI.BORDER;
    private final Color BLUE_ACTION = UI.ACCENT;
    private final Color RED = UI.DANGER;

    private final DataStore store = DataStore.get();
    private final JComboBox<String> cbStudent = new JComboBox<>();
    private final JComboBox<String> cbSubject = new JComboBox<>();
    private final JLabel lblSubjectInfo = new JLabel("Select a subject");
    private final JLabel lblUnits = new JLabel("0");
    private final JLabel lblTuition = new JLabel(UI.peso(0));
    private final JLabel lblMisc = new JLabel(UI.peso(0));
    private final JLabel lblTotal = new JLabel(UI.peso(0));
    private final JLabel lblCount = new JLabel("0 records");
    private final JTextField tfSearch = new JTextField();

    private final JComboBox<String> cbSearchBy = new JComboBox<>(
            new String[]{
                "All Fields", "Enrollment ID", "Student ID", "Name",
                "Course", "Year Level", "Subject Code", "Subject Title"
            }
    );
    private final JComboBox<String> cbCourseFilter =
            new JComboBox<>(withAll("All Courses", DataStore.COURSES));
    private final JComboBox<String> cbYearFilter =
            new JComboBox<>(withAll("All Years", DataStore.YEARS));
    private final JComboBox<String> cbStatusFilter =
            new JComboBox<>(new String[]{
                "All Statuses", "Enrolled", "Dropped"
            });

    private final JCheckBox chkSelectedOnly =
            new JCheckBox("Selected student only");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{
                "Enrollment ID", "Student ID", "Student Name",
                "Subject Code", "Subject Title", "Units", "Date", "Status"
            }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private boolean loading = false;

    private static String[] withAll(String all, String[] values) {
        String[] options = new String[values.length + 1];
        options[0] = all;
        System.arraycopy(values, 0, options, 1, values.length);
        return options;
    }

    public EnrollmentForm() {
        super("Enrollment Management");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setMinimumSize(new Dimension(850, 500));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);

        add(buildMainPanel(), BorderLayout.CENTER);

        styleTable();
        reloadAll();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, UI.DANGER),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                        BorderFactory.createEmptyBorder(14, 20, 14, 20)
                )
        ));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Enrollment Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Register students, manage subjects, and view fees"
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(GRAY);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitle);

        JButton btnRefresh = button("↻ Refresh", DARK_BLUE, WHITE);
        btnRefresh.setPreferredSize(new Dimension(145, 48));
        btnRefresh.addActionListener(e -> reloadAll());

        header.add(titlePanel, BorderLayout.CENTER);
        header.add(btnRefresh, BorderLayout.EAST);
        return header;
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout(0, 12));
        main.setBackground(BACKGROUND);
        main.setBorder(BorderFactory.createEmptyBorder(20, 22, 22, 22));
        main.add(buildTopSection(), BorderLayout.NORTH);
        main.add(buildRecordsPanel(), BorderLayout.CENTER);
        return main;
    }

    private JPanel buildTopSection() {
        JPanel top = new JPanel(new GridBagLayout());
        top.setBackground(BACKGROUND);

        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.fill = GridBagConstraints.BOTH;
        g.anchor = GridBagConstraints.NORTH;
        g.weighty = 1.0;

        g.gridx = 0;
        g.weightx = 1.35;
        g.insets = new Insets(0, 0, 0, 10);
        top.add(buildEnrollmentPanel(), g);

        g.gridx = 1;
        g.weightx = 1.0;
        g.insets = new Insets(0, 0, 0, 0);
        top.add(buildAssessmentPanel(), g);

        return top;
    }

    private JPanel buildEnrollmentPanel() {
        JPanel panel = card();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;
        g.weightx = 1.0;
        panel.add(sectionTitle("New Enrollment"), g);

        g.gridwidth = 1;
        g.gridx = 0;
        g.gridy = 1;
        g.weightx = 0;
        panel.add(fieldLabel("Student"), g);

        g.gridx = 1;
        g.weightx = 1.0;
        panel.add(cbStudent, g);

        g.gridx = 0;
        g.gridy = 2;
        g.weightx = 0;
        panel.add(fieldLabel("Subject"), g);

        g.gridx = 1;
        g.weightx = 1.0;
        panel.add(cbSubject, g);

        g.gridx = 0;
        g.gridy = 3;
        g.gridwidth = 2;
        g.weightx = 1.0;
        g.insets = new Insets(8, 5, 8, 5);

        lblSubjectInfo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubjectInfo.setForeground(GRAY);
        panel.add(lblSubjectInfo, g);

        JButton btnEnroll = button("Enroll Student", BLUE_ACTION, WHITE);
        btnEnroll.setPreferredSize(new Dimension(200, 48));
        btnEnroll.setMinimumSize(new Dimension(150, 46));

        g.gridx = 0;
        g.gridy = 4;
        g.gridwidth = 2;
        g.weightx = 1.0;
        g.insets = new Insets(5, 5, 5, 5);
        panel.add(btnEnroll, g);

        styleCombo(cbStudent);
        styleCombo(cbSubject);

        cbStudent.setPrototypeDisplayValue(
                "2026-0000 - Sample Student Name"
        );
        cbSubject.setPrototypeDisplayValue(
                "BSN301 - Maternal and Child Health Nursing [BSN]"
        );
        cbSubject.setToolTipText(
                "Shows this student's course majors and general/minor subjects."
        );

        btnEnroll.addActionListener(e -> doEnroll());

        cbStudent.addActionListener(e -> {
            if (!loading) {
                refreshSubjectOptions(selectedSubjectCode());
                updateAssessment();
                refreshTable();
            }
        });

        cbSubject.addActionListener(e -> {
            if (!loading) {
                updateSubjectInfo();
            }
        });

        return panel;
    }

    private JPanel buildAssessmentPanel() {
        JPanel panel = card();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;
        g.weightx = 1.0;
        panel.add(sectionTitle("Fee Assessment"), g);

        addFeeRow(panel, g, 1, "Total Units", lblUnits);
        addFeeRow(panel, g, 2, "Tuition", lblTuition);
        addFeeRow(panel, g, 3, "Miscellaneous", lblMisc);

        g.gridx = 0;
        g.gridy = 4;
        g.gridwidth = 2;
        g.weightx = 1.0;
        g.insets = new Insets(10, 5, 5, 5);

        JPanel totalPanel = new JPanel(new BorderLayout(8, 0));
        totalPanel.setBackground(new Color(232, 240, 253));
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, UI.ACCENT),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel totalText = new JLabel("TOTAL FEE");
        totalText.setFont(new Font("SansSerif", Font.BOLD, 15));
        totalText.setForeground(BLUE);

        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTotal.setForeground(DARK_BLUE);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);

        totalPanel.add(totalText, BorderLayout.WEST);
        totalPanel.add(lblTotal, BorderLayout.CENTER);
        panel.add(totalPanel, g);

        return panel;
    }

    private void addFeeRow(
            JPanel panel,
            GridBagConstraints g,
            int row,
            String text,
            JLabel value) {

        g.gridy = row;
        g.gridx = 0;
        g.weightx = 1.0;
        g.insets = new Insets(5, 5, 5, 5);

        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 15));
        label.setForeground(GRAY);
        panel.add(label, g);

        g.gridx = 1;
        g.weightx = 0;
        value.setFont(new Font("SansSerif", Font.BOLD, 15));
        value.setForeground(TEXT);
        value.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(value, g);
    }

    private JPanel buildRecordsPanel() {
        JPanel panel = card();
        panel.setLayout(new BorderLayout(0, 10));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.add(sectionTitle("Enrollment Records"), BorderLayout.WEST);

        lblCount.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblCount.setForeground(BLUE);
        heading.add(lblCount, BorderLayout.EAST);

        JPanel toolbar = new JPanel(new GridBagLayout());
        toolbar.setOpaque(false);

        tfSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tfSearch.setPreferredSize(new Dimension(260, 44));
        tfSearch.setToolTipText("Enter a value for the selected search category");
        tfSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        styleCombo(cbSearchBy);
        cbSearchBy.setPreferredSize(new Dimension(175, 44));
        styleCombo(cbStatusFilter);
        cbStatusFilter.setPreferredSize(new Dimension(170, 44));
        styleCombo(cbCourseFilter);
        cbCourseFilter.setPreferredSize(new Dimension(190, 44));
        styleCombo(cbYearFilter);
        cbYearFilter.setPreferredSize(new Dimension(170, 44));

        chkSelectedOnly.setOpaque(false);
        chkSelectedOnly.setFont(new Font("SansSerif", Font.BOLD, 14));
        chkSelectedOnly.setForeground(TEXT);
        GridBagConstraints filterGrid = new GridBagConstraints();
        filterGrid.gridy = 0;
        filterGrid.fill = GridBagConstraints.HORIZONTAL;
        filterGrid.anchor = GridBagConstraints.WEST;
        filterGrid.insets = new Insets(0, 0, 6, 10);

        filterGrid.gridx = 0;
        filterGrid.weightx = 0;
        toolbar.add(filterLabel("Search category", cbSearchBy), filterGrid);

        filterGrid.gridx = 1;
        filterGrid.weightx = 1;
        toolbar.add(filterLabel("Search Student", tfSearch), filterGrid);

        filterGrid.gridx = 2;
        filterGrid.weightx = 0;
        filterGrid.insets = new Insets(0, 0, 6, 0);
        toolbar.add(filterLabel("Enrollment status", cbStatusFilter), filterGrid);

        filterGrid.gridy = 1;
        filterGrid.gridx = 0;
        filterGrid.weightx = 1;
        filterGrid.insets = new Insets(0, 0, 0, 10);
        toolbar.add(filterLabel("Course", cbCourseFilter), filterGrid);

        filterGrid.gridx = 1;
        toolbar.add(filterLabel("Year level", cbYearFilter), filterGrid);

        filterGrid.gridx = 2;
        filterGrid.weightx = 0;
        filterGrid.insets = new Insets(0, 0, 0, 0);
        toolbar.add(chkSelectedOnly, filterGrid);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setOpaque(false);
        top.add(heading, BorderLayout.NORTH);
        top.add(toolbar, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(
                table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);

        JPanel bottom = new JPanel(new BorderLayout(10, 0));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JButton btnDrop = button("Drop Selected", RED, WHITE);
        btnDrop.setPreferredSize(new Dimension(180, 48));
        btnDrop.setMinimumSize(new Dimension(150, 46));
        btnDrop.addActionListener(e -> doDrop());

        JLabel hint = new JLabel(
                "Select a row before dropping an enrollment."
        );
        hint.setForeground(GRAY);
        hint.setFont(new Font("SansSerif", Font.PLAIN, 14));

        bottom.add(btnDrop, BorderLayout.WEST);
        bottom.add(hint, BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshTable();
            }
        });

        cbStatusFilter.addActionListener(e -> refreshTable());
        cbSearchBy.addActionListener(e -> refreshTable());
        cbCourseFilter.addActionListener(e -> refreshTable());
        cbYearFilter.addActionListener(e -> refreshTable());
        chkSelectedOnly.addActionListener(e -> refreshTable());

        return panel;
    }

    private void styleTable() {
        UI.style(table);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setForeground(TEXT);
        table.setBackground(WHITE);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT);
        table.setRowHeight(42);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(238, 242, 247));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );
        table.getTableHeader().setBackground(UI.PRIMARY);
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 46));
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        int[] widths = {110, 100, 170, 110, 200, 65, 110, 100};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    public void reloadAll() {
        loading = true;

        String oldStudent = selectedStudentId();
        String oldSubject = selectedSubjectCode();

        cbStudent.removeAllItems();
        for (Student s : store.getStudents()) {
            if ("Active".equals(s.getStatus())) {
                cbStudent.addItem(s.getId() + " - " + s.getFullName());
            }
        }

        selectById(cbStudent, oldStudent);
        refreshSubjectOptions(oldSubject);

        loading = false;
        updateSubjectInfo();
        updateAssessment();
        refreshTable();
    }

    private void selectById(JComboBox<String> combo, String id) {
        if (id == null) {
            return;
        }

        for (int i = 0; i < combo.getItemCount(); i++) {
            String item = combo.getItemAt(i);
            if (item.startsWith(id + " - ")) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private String selectedStudentId() {
        String item = (String) cbStudent.getSelectedItem();
        return item == null ? null : item.split(" - ", 2)[0];
    }

    private String selectedSubjectCode() {
        String item = (String) cbSubject.getSelectedItem();
        return item == null ? null : item.split(" - ", 2)[0];
    }

    private void refreshSubjectOptions(String preferredCode) {
        Student student = store.findStudent(selectedStudentId());
        String course = student == null ? null : student.getCourse();
        Subject preferred = store.findSubject(preferredCode);
        String selectedCode = preferredCode;

        if (preferred != null && course != null
                && !"General".equals(preferred.getCourse())
                && !course.equals(preferred.getCourse())) {
            selectedCode = null;
        }

        loading = true;
        cbSubject.removeAllItems();
        java.util.List<Subject> subjects = course == null
                ? store.getSubjects()
                : store.getSubjectsForCourse(course);
        for (Subject subject : subjects) {
            cbSubject.addItem(
                    subject.getCode() + " - " + subject.getTitle()
                    + " [" + subject.getCourse() + "]"
            );
        }
        if (selectedCode == null) {
            cbSubject.setSelectedIndex(-1);
        } else {
            selectById(cbSubject, selectedCode);
        }
        if (cbSubject.getSelectedIndex() < 0 && course != null) {
            for (int i = 0; i < subjects.size(); i++) {
                Subject subject = subjects.get(i);
                if (course.equals(subject.getCourse())) {
                    cbSubject.setSelectedIndex(i);
                    break;
                }
            }
        }
        if (cbSubject.getSelectedIndex() < 0 && cbSubject.getItemCount() > 0) {
            cbSubject.setSelectedIndex(0);
        }
        loading = false;
        updateSubjectInfo();
    }

    private void updateSubjectInfo() {
        Subject subject = store.findSubject(selectedSubjectCode());

        if (subject == null) {
            lblSubjectInfo.setText("No subject selected.");
            return;
        }

        int taken = store.countEnrolled(subject.getCode());
        int available = subject.getCapacity() - taken;

        lblSubjectInfo.setText(
                subject.getCourse() + "    |    Units: " + subject.getUnits()
                + "    |    Slots: " + taken
                + "/" + subject.getCapacity()
                + "    |    Available: " + Math.max(0, available)
        );
    }

    private void updateAssessment() {
        String id = selectedStudentId();
        int units = id == null ? 0 : store.totalUnits(id);

        lblUnits.setText(units + " / " + DataStore.MAX_UNITS);
        lblTuition.setText(UI.peso(DataStore.tuitionFor(units)));
        lblMisc.setText(UI.peso(DataStore.miscFor(units)));
        lblTotal.setText(UI.peso(DataStore.totalFeeFor(units)));
    }

    private void refreshTable() {
        String query = tfSearch.getText().trim().toLowerCase();
        String searchBy = (String) cbSearchBy.getSelectedItem();
        String courseFilter = (String) cbCourseFilter.getSelectedItem();
        String yearFilter = (String) cbYearFilter.getSelectedItem();
        String status = (String) cbStatusFilter.getSelectedItem();

        String onlyStudent = chkSelectedOnly.isSelected()
                ? selectedStudentId() : null;

        model.setRowCount(0);
        int shown = 0;

        for (Enrollment e : store.getEnrollments()) {
            Student student = store.findStudent(e.getStudentId());
            Subject subject = store.findSubject(e.getSubjectCode());

            String studentName = student == null
                    ? "(deleted)" : student.getFullName();

            String subjectTitle = subject == null
                    ? "" : subject.getTitle();
            String studentCourse = student == null
                    ? "" : student.getCourse();
            String studentYear = student == null
                    ? "" : student.getYearLevel();

            if (onlyStudent != null
                    && !e.getStudentId().equals(onlyStudent)) {
                continue;
            }

            if (!"All Courses".equals(courseFilter)
                    && !studentCourse.equals(courseFilter)) {
                continue;
            }

            if (!"All Years".equals(yearFilter)
                    && !studentYear.equals(yearFilter)) {
                continue;
            }

            if (!"All Statuses".equals(status)
                    && !e.getStatus().equals(status)) {
                continue;
            }

            if (!query.isEmpty()) {
                String searchable;
                switch (searchBy) {
                    case "Enrollment ID":
                        searchable = e.getEnrollmentId();
                        break;
                    case "Student ID":
                        searchable = e.getStudentId();
                        break;
                    case "Name":
                        searchable = studentName;
                        break;
                    case "Course":
                        searchable = studentCourse;
                        break;
                    case "Year Level":
                        searchable = studentYear;
                        break;
                    case "Subject Code":
                        searchable = e.getSubjectCode();
                        break;
                    case "Subject Title":
                        searchable = subjectTitle;
                        break;
                    default:
                        searchable = e.getEnrollmentId() + " "
                                + e.getStudentId() + " "
                                + studentName + " "
                                + studentCourse + " "
                                + studentYear + " "
                                + e.getSubjectCode() + " "
                                + subjectTitle + " "
                                + (subject == null ? "" : subject.getCourse());
                }
                if (!searchable.toLowerCase().contains(query)) {
                    continue;
                }
            }

            model.addRow(new Object[]{
                    e.getEnrollmentId(),
                    e.getStudentId(),
                    studentName,
                    e.getSubjectCode(),
                    subjectTitle,
                    subject == null ? 0 : subject.getUnits(),
                    e.getDate(),
                    e.getStatus()
            });

            shown++;
        }

        lblCount.setText(shown + (shown == 1 ? " record" : " records"));
    }

    private void doEnroll() {
        String studentId = selectedStudentId();
        String subjectCode = selectedSubjectCode();

        if (studentId == null) {
            UI.error(this, "No active student is available to enroll.");
            return;
        }

        if (subjectCode == null) {
            UI.error(this, "Please select a subject.");
            return;
        }

        String error = store.enroll(studentId, subjectCode);
        if (error != null) {
            UI.error(this, error);
            return;
        }

        updateSubjectInfo();
        updateAssessment();
        refreshTable();

        UI.info(this, "Enrollment successful!\n"
                + studentId + " is now enrolled in " + subjectCode + ".");
    }

    private void doDrop() {
        int viewRow = table.getSelectedRow();

        if (viewRow < 0) {
            UI.error(this, "Please select an enrollment record to drop.");
            return;
        }

        int row = table.convertRowIndexToModel(viewRow);
        String enrollmentId = String.valueOf(model.getValueAt(row, 0));
        String subjectCode = String.valueOf(model.getValueAt(row, 3));
        String studentName = String.valueOf(model.getValueAt(row, 2));

        boolean confirmed = UI.confirmYesNo(
                this,
                "Drop " + subjectCode + " for " + studentName + "?",
                "Confirm Drop"
        );

        if (!confirmed) {
            return;
        }

        String error = store.drop(enrollmentId);
        if (error != null) {
            UI.error(this, error);
            return;
        }

        updateSubjectInfo();
        updateAssessment();
        refreshTable();

        UI.info(this, "Subject dropped successfully. Assessment updated.");
    }

    private JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        return panel;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 19));
        label.setForeground(TEXT);
        return label;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setForeground(TEXT);
        return label;
    }

    private JPanel filterLabel(String text, JComponent input) {
        JPanel field = new JPanel();
        field.setOpaque(false);
        field.setLayout(new BoxLayout(field, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(TEXT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.add(label);
        field.add(input);
        return field;
    }

    private JButton button(String text, Color background, Color foreground) {
        JButton b = new JButton(text);
        UI.styleButton(b, background);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setForeground(foreground);
        return b;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        combo.setForeground(TEXT);
        combo.setBackground(WHITE);
        combo.setPreferredSize(new Dimension(190, 44));
    }
}
