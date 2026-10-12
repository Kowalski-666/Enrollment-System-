import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.regex.Pattern;

/** Form 3: Manage Students */
public class ManageStudentsForm extends JFrame {

    private final DataStore store = DataStore.get();

    // Theme
    private static final Color BLUE = UI.ACCENT;
    private static final Color BACKGROUND = UI.BG;
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = UI.PRIMARY;
    private static final Color MUTED = UI.GRAY;
    private static final Color BORDER = UI.BORDER;
    private static final Color BLUE_ACTION = UI.ACCENT;
    private static final Color RED = UI.DANGER;

    // Student fields
    private final JTextField tfId = new JTextField();
    private final JTextField tfFirst = new JTextField();
    private final JTextField tfLast = new JTextField();
    private final JTextField tfEmail = new JTextField();
    private final JTextField tfContact = new JTextField();

    private final JComboBox<String> cbGender =
            new JComboBox<>(new String[]{"Male", "Female"});

    private final JComboBox<String> cbCourse =
            new JComboBox<>(DataStore.COURSES);

    private final JComboBox<String> cbYear =
            new JComboBox<>(DataStore.YEARS);

    private final JComboBox<String> cbStatus =
            new JComboBox<>(DataStore.STATUSES);

    // Search and filters
    private final JTextField tfSearch = new JTextField();

    private final JComboBox<String> fCourse =
            new JComboBox<>(withAll(DataStore.COURSES));

    private final JComboBox<String> fYear =
            new JComboBox<>(withAll(DataStore.YEARS));

    private final JComboBox<String> fStatus =
            new JComboBox<>(withAll(DataStore.STATUSES));

    private final JLabel lblCount = new JLabel("0 records");

    private final DefaultTableModel model =
            new DefaultTableModel(
                    new String[]{
                        "Student ID", "Last Name", "First Name",
                        "Email", "Contact", "Gender", "Course",
                        "Year Level", "Status"
                    }, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable table = new JTable(model);

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    private static final Pattern ID_PATTERN =
            Pattern.compile("\\d{4}-\\d{4}");

    private static final Pattern NAME_PATTERN =
            Pattern.compile("[A-Za-z][A-Za-z .'-]*");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+");

    private static final Pattern CONTACT_PATTERN =
            Pattern.compile("09\\d{9}");

    private static String[] withAll(String[] items) {
        String[] result = new String[items.length + 1];
        result[0] = "All";
        System.arraycopy(items, 0, result, 1, items.length);
        return result;
    }

    public ManageStudentsForm() {
        super("Student Portal | Manage Students");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1250, 740);
        setMinimumSize(new Dimension(850, 600));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout());
        ((AbstractDocument) tfContact.getDocument()).setDocumentFilter(
                new ContactNumberFilter()
        );

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);


        refresh();
    }

    // =====================================================
    // HEADER
    // =====================================================

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, UI.DANGER),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 0, BORDER
                        ),
                        new EmptyBorder(18, 25, 18, 25)
                )
        ));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Manage Students");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Add, update, search, and manage student records."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);

        titles.add(title);
        titles.add(Box.createVerticalStrut(5));
        titles.add(subtitle);

        JLabel badge = new JLabel("STUDENT RECORDS");
        badge.setFont(new Font("SansSerif", Font.BOLD, 12));
        badge.setForeground(BLUE);
        badge.setOpaque(true);
        badge.setBackground(new Color(232, 240, 253));
        badge.setBorder(new EmptyBorder(9, 12, 9, 12));

        header.add(titles, BorderLayout.CENTER);
        header.add(badge, BorderLayout.EAST);

        return header;
    }

    // =====================================================
    // MAIN CONTENT
    // =====================================================

    private JPanel createMainContent() {

        JPanel main = new JPanel(new BorderLayout(18, 0));
        main.setBackground(BACKGROUND);
        main.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel formPanel = buildFormPanel();
        formPanel.setPreferredSize(new Dimension(370, 0));

        JPanel tablePanel = buildTablePanel();

        main.add(formPanel, BorderLayout.WEST);
        main.add(tablePanel, BorderLayout.CENTER);

        return main;
    }

    // =====================================================
    // STUDENT FORM
    // =====================================================

    private JPanel buildFormPanel() {

        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 15));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(
                titlePanel, BoxLayout.Y_AXIS
        ));

        JLabel title = new JLabel("Student Information");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Enter the student's details");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(0, 0, 12, 0);

        addField(fields, g, 0, "Student ID", tfId,
                "Example: 2026-0023");

        addField(fields, g, 1, "First Name", tfFirst,
                "Enter first name");

        addField(fields, g, 2, "Last Name", tfLast,
                "Enter last name");

        addField(fields, g, 3, "Email Address", tfEmail,
                "student@example.com");

        addField(fields, g, 4, "Contact Number", tfContact,
                "09XXXXXXXXX");

        addField(fields, g, 5, "Gender", cbGender, null);
        addField(fields, g, 6, "Course", cbCourse, null);
        addField(fields, g, 7, "Year Level", cbYear, null);
        addField(fields, g, 8, "Status", cbStatus, null);

        tfId.setToolTipText("Format: YYYY-NNNN");
        tfContact.setToolTipText("11 digits starting with 09");

        // Buttons
        btnAdd = createButton("Add Student", BLUE_ACTION);
        btnUpdate = createButton("Update", BLUE);
        btnDelete = createButton("Delete", RED);
        btnClear = createButton("Clear Form", UI.PRIMARY);

        JPanel buttons = new JPanel(new GridLayout(2, 2, 10, 10));
        buttons.setOpaque(false);
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        JScrollPane scroll = new JScrollPane(fields);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        buttons.setOpaque(true);
        buttons.setBackground(WHITE);
        buttons.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                new EmptyBorder(10, 0, 0, 0)
        ));
        card.add(buttons, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e -> clearForm());

        return card;
    }

    private void addField(
            JPanel panel,
            GridBagConstraints g,
            int row,
            String title,
            JComponent field,
            String placeholder) {

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(
                wrapper, BoxLayout.Y_AXIS
        ));

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        styleField(field);

        if (placeholder != null && field instanceof JTextField) {
            ((JTextField) field).setToolTipText(placeholder);
        }

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        wrapper.add(label);
        wrapper.add(Box.createVerticalStrut(6));
        wrapper.add(field);

        g.gridy = row;
        panel.add(wrapper, g);
    }

    private void styleField(JComponent field) {

        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setBackground(WHITE);

        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(9, 11, 9, 11)
        ));

        if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setFocusable(true);
        }
    }

    // =====================================================
    // TABLE, SEARCH AND FILTERS
    // =====================================================

    private JPanel buildTablePanel() {

        JPanel outer = new JPanel(new BorderLayout(0, 14));
        outer.setOpaque(false);

        // Toolbar card
        JPanel toolbar = createCard();
        toolbar.setLayout(new BoxLayout(
                toolbar, BoxLayout.Y_AXIS
        ));

        JLabel searchTitle = new JLabel("Find Student");
        searchTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        searchTitle.setForeground(TEXT);
        searchTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchSubtitle = new JLabel(
                "Search records or narrow results using filters."
        );
        searchSubtitle.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        searchSubtitle.setForeground(MUTED);
        searchSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        searchRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        tfSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tfSearch.setPreferredSize(new Dimension(100, 44));
        tfSearch.setToolTipText("Search all student record fields");
        styleField(tfSearch);

        JButton btnSearch = createButton("Search", BLUE);
        JButton btnReset = createButton("Reset", UI.PRIMARY);

        JPanel searchActions = new JPanel(new GridLayout(1, 2, 6, 0));
        searchActions.setOpaque(false);
        searchActions.add(btnSearch);
        searchActions.add(btnReset);

        searchRow.add(tfSearch, BorderLayout.CENTER);
        searchRow.add(searchActions, BorderLayout.EAST);

        JPanel filters = new JPanel(new GridLayout(1, 3, 10, 0));
        filters.setOpaque(false);
        filters.setAlignmentX(Component.LEFT_ALIGNMENT);

        filters.add(createFilter("Course", fCourse));
        filters.add(createFilter("Year Level", fYear));
        filters.add(createFilter("Status", fStatus));

        toolbar.add(searchTitle);
        toolbar.add(Box.createVerticalStrut(5));
        toolbar.add(searchSubtitle);
        toolbar.add(Box.createVerticalStrut(15));
        toolbar.add(searchRow);
        toolbar.add(Box.createVerticalStrut(14));
        toolbar.add(filters);

        // Table card
        JPanel tableCard = createCard();
        tableCard.setLayout(new BorderLayout(0, 12));

        JPanel tableHeading = new JPanel(new BorderLayout());
        tableHeading.setOpaque(false);

        JLabel recordsTitle = new JLabel("Student Records");
        recordsTitle.setFont(
                new Font("SansSerif", Font.BOLD, 20)
        );
        recordsTitle.setForeground(TEXT);

        lblCount.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblCount.setForeground(MUTED);
        lblCount.setHorizontalAlignment(SwingConstants.RIGHT);

        tableHeading.add(recordsTitle, BorderLayout.WEST);
        tableHeading.add(lblCount, BorderLayout.EAST);

        styleTable();

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(new LineBorder(BORDER, 1));
        tableScroll.getViewport().setBackground(WHITE);
        tableScroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        tableCard.add(tableHeading, BorderLayout.NORTH);
        tableCard.add(tableScroll, BorderLayout.CENTER);

        outer.add(toolbar, BorderLayout.NORTH);
        outer.add(tableCard, BorderLayout.CENTER);

        // Search and filter actions
        btnSearch.addActionListener(e -> refresh());

        btnReset.addActionListener(e -> {
            tfSearch.setText("");
            fCourse.setSelectedIndex(0);
            fYear.setSelectedIndex(0);
            fStatus.setSelectedIndex(0);
            refresh();
        });

        tfSearch.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void insertUpdate(DocumentEvent e) {
                        refresh();
                    }

                    public void removeUpdate(DocumentEvent e) {
                        refresh();
                    }

                    public void changedUpdate(DocumentEvent e) {
                        refresh();
                    }
                }
        );

        fCourse.addActionListener(e -> refresh());
        fYear.addActionListener(e -> refresh());
        fStatus.addActionListener(e -> refresh());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelection();
            }
        });

        return outer;
    }

    private JPanel createFilter(
            String title, JComboBox<String> combo) {

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(
                panel, BoxLayout.Y_AXIS
        ));

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        styleField(combo);
        combo.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42)
        );
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(combo);

        return panel;
    }

    private void styleTable() {

        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setForeground(TEXT);
        table.setBackground(WHITE);
        table.setRowHeight(44);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setForeground(Color.WHITE);
        header.setBackground(UI.PRIMARY);
        header.setPreferredSize(new Dimension(0, 48));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean selected,
                    boolean focused,
                    int row,
                    int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, selected, focused, row, column
                );
                label.setOpaque(true);
                label.setBackground(UI.PRIMARY);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("SansSerif", Font.BOLD, 14));
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 1, new Color(62, 87, 128)
                        ),
                        new EmptyBorder(0, 8, 0, 8)
                ));
                return label;
            }
        });

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean selected,
                            boolean focused,
                            int row,
                            int column) {

                        Component c = super.getTableCellRendererComponent(
                                table, value, selected, focused,
                                row, column
                        );

                        if (selected) {
                            c.setBackground(
                                    new Color(219, 234, 254)
                            );
                            c.setForeground(TEXT);
                        } else {
                            c.setBackground(
                                    row % 2 == 0
                                    ? WHITE
                                    : new Color(250, 251, 253)
                            );
                            c.setForeground(TEXT);
                        }

                        setBorder(new EmptyBorder(0, 9, 0, 9));

                        return c;
                    }
                };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(renderer);
        }

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        int[] widths = {
            125, 130, 130, 240, 155, 90, 135, 120, 105
        };

        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }
    }

    private JPanel createCard() {

        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        return panel;
    }

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);
        UI.styleButton(button, color);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setMinimumSize(new Dimension(120, 48));
        button.setPreferredSize(new Dimension(150, 48));

        return button;
    }

    // =====================================================
    // READ, SEARCH AND FILTER
    // =====================================================

    public void refresh() {

        String q = tfSearch.getText().trim().toLowerCase();

        String course = (String) fCourse.getSelectedItem();
        String year = (String) fYear.getSelectedItem();
        String status = (String) fStatus.getSelectedItem();

        model.setRowCount(0);

        int shown = 0;

        for (Student s : store.getStudents()) {

            if (!q.isEmpty()) {
                String searchable = s.getId() + " "
                        + s.getFirstName() + " " + s.getLastName() + " "
                        + s.getFullName() + " " + s.getEmail() + " "
                        + s.getContact() + " " + s.getGender() + " "
                        + s.getCourse() + " " + s.getYearLevel() + " "
                        + s.getStatus();
                if (!searchable.toLowerCase().contains(q)) {
                    continue;
                }
            }

            if (!"All".equals(course)
                    && !s.getCourse().equals(course)) {
                continue;
            }

            if (!"All".equals(year)
                    && !s.getYearLevel().equals(year)) {
                continue;
            }

            if (!"All".equals(status)
                    && !s.getStatus().equals(status)) {
                continue;
            }

            model.addRow(new Object[]{
                s.getId(),
                s.getLastName(),
                s.getFirstName(),
                s.getEmail(),
                s.getContact(),
                s.getGender(),
                s.getCourse(),
                s.getYearLevel(),
                s.getStatus()
            });

            shown++;
        }

        lblCount.setText(
                "Showing " + shown + " of "
                + store.getStudents().size() + " records"
        );
    }

    private void fillFormFromSelection() {

        int viewRow = table.getSelectedRow();

        if (viewRow < 0) {
            return;
        }

        // Convert the sorted table row into its model row.
        int row = table.convertRowIndexToModel(viewRow);

        String id = (String) model.getValueAt(row, 0);
        Student s = store.findStudent(id);

        if (s == null) {
            return;
        }

        tfId.setText(s.getId());
        tfId.setEnabled(false);

        tfFirst.setText(s.getFirstName());
        tfLast.setText(s.getLastName());
        tfEmail.setText(s.getEmail());
        tfContact.setText(s.getContact());

        cbGender.setSelectedItem(s.getGender());
        cbCourse.setSelectedItem(s.getCourse());
        cbYear.setSelectedItem(s.getYearLevel());
        cbStatus.setSelectedItem(s.getStatus());
    }

    private void clearForm() {

        table.clearSelection();

        tfId.setText("");
        tfId.setEnabled(true);

        tfFirst.setText("");
        tfLast.setText("");
        tfEmail.setText("");
        tfContact.setText("");

        cbGender.setSelectedIndex(0);
        cbCourse.setSelectedIndex(0);
        cbYear.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);

        tfId.requestFocusInWindow();
    }

    // =====================================================
    // VALIDATION
    // =====================================================

    private Student readForm(boolean isNew) {

        String id = tfId.getText().trim();
        String first = tfFirst.getText().trim();
        String last = tfLast.getText().trim();
        String email = tfEmail.getText().trim();
        String contact = tfContact.getText().trim();

        if (id.isEmpty() || first.isEmpty()
                || last.isEmpty() || email.isEmpty()
                || contact.isEmpty()
                || cbGender.getSelectedItem() == null
                || cbCourse.getSelectedItem() == null
                || cbYear.getSelectedItem() == null
                || cbStatus.getSelectedItem() == null) {

            UI.error(this,
                    "All fields are required. Please fill in every field.");
            return null;
        }

        if (!ID_PATTERN.matcher(id).matches()) {
            UI.error(this,
                    "Invalid Student ID. Use YYYY-NNNN, "
                    + "for example 2026-0023.");
            return null;
        }

        if (isNew && store.findStudent(id) != null) {
            UI.error(this,
                    "Student ID " + id + " already exists.");
            return null;
        }

        if (!NAME_PATTERN.matcher(first).matches()
                || !NAME_PATTERN.matcher(last).matches()) {

            UI.error(this,
                    "Please enter valid first and last names.");
            return null;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            UI.error(this,
                    "Please enter a valid email address.");
            return null;
        }

        if (store.emailExists(email, id)) {
            UI.error(this,
                    "This email is already used by another student.");
            return null;
        }

        if (!CONTACT_PATTERN.matcher(contact).matches()) {
            UI.error(this,
                    "Contact number must contain 11 digits "
                    + "and start with 09.");
            return null;
        }

        return new Student(
                id,
                first,
                last,
                email,
                contact,
                (String) cbGender.getSelectedItem(),
                (String) cbCourse.getSelectedItem(),
                (String) cbYear.getSelectedItem(),
                (String) cbStatus.getSelectedItem()
        );
    }

    private static class ContactNumberFilter extends DocumentFilter {
        @Override
        public void insertString(
                FilterBypass bypass,
                int offset,
                String text,
                AttributeSet attributes
        ) throws BadLocationException {
            replace(bypass, offset, 0, text, attributes);
        }

        @Override
        public void replace(
                FilterBypass bypass,
                int offset,
                int length,
                String text,
                AttributeSet attributes
        ) throws BadLocationException {
            String inserted = text == null ? "" : text;
            String current = bypass.getDocument().getText(
                    0, bypass.getDocument().getLength()
            );
            String candidate = current.substring(0, offset)
                    + inserted
                    + current.substring(offset + length);

            if (candidate.length() <= 11
                    && candidate.matches("\\d*")) {
                bypass.replace(offset, length, inserted, attributes);
            }
        }
    }

    // =====================================================
    // CREATE
    // =====================================================

    private void addStudent() {

        if (!tfId.isEnabled()) {
            UI.error(this,
                    "Click Clear Form before adding a new student.");
            return;
        }

        Student s = readForm(true);

        if (s == null) {
            return;
        }

        store.addStudent(s);

        refresh();
        clearForm();

        UI.info(this, "Student added successfully.");
    }

    // =====================================================
    // UPDATE
    // =====================================================

    private void updateStudent() {

        if (table.getSelectedRow() < 0) {
            UI.error(this,
                    "Please select a student from the table first.");
            return;
        }

        Student s = readForm(false);

        if (s == null) {
            return;
        }

        store.updateStudent(s);

        refresh();
        clearForm();

        UI.info(this, "Student updated successfully.");
    }

    // =====================================================
    // DELETE
    // =====================================================

    private void deleteStudent() {

        int viewRow = table.getSelectedRow();

        if (viewRow < 0) {
            UI.error(this,
                    "Please select a student to delete.");
            return;
        }

        int row = table.convertRowIndexToModel(viewRow);

        String id = (String) model.getValueAt(row, 0);

        String name = model.getValueAt(row, 2)
                + " " + model.getValueAt(row, 1);

        boolean confirmed = UI.confirmYesNo(
                this,
                "Delete this student record?\n\n"
                + name + "\nStudent ID: " + id,
                "Confirm Deletion"
        );

        if (!confirmed) {
            return;
        }

        String error = store.deleteStudent(id);

        if (error != null) {
            UI.error(this, error);
            return;
        }

        refresh();
        clearForm();

        UI.info(this, "Student deleted successfully.");
    }
}