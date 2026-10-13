
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Dashboard extends JFrame {
    private final DataStore store = DataStore.get();

    private final JLabel lblStudents = metricValue();
    private final JLabel lblActive = metricValue();
    private final JLabel lblSubjects = metricValue();
    private final JLabel lblEnrollments = metricValue();

    private ManageStudentsForm studentsForm;
    private EnrollmentForm enrollmentForm;
    private ReportsForm reportsForm;

    public Dashboard(User user) {
        super("Student Portal | Dashboard");

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(640, 420));
        setSize(1100, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UI.BG);
        setLayout(new BorderLayout());
        setJMenuBar(buildMenu());

        JPanel header = new JPanel(new BorderLayout(12, 8));
        header.setBackground(UI.PRIMARY);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 4, 0, UI.DANGER),
            BorderFactory.createEmptyBorder(24, 32, 24, 32)
        ));

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("STUDENT PORTAL");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("STUDENT MANAGEMENT WORKSPACE");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setForeground(new Color(205, 220, 243));

        brand.add(title);
        brand.add(Box.createVerticalStrut(5));
        brand.add(sub);
        header.add(brand, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 26));
        content.setOpaque(false);
        content.setBorder(
            BorderFactory.createEmptyBorder(24, 28, 24, 28)
        );

        JPanel intro = new JPanel();
        intro.setOpaque(false);
        intro.setLayout(new BoxLayout(intro, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Overview");
        heading.setFont(new Font("SansSerif", Font.BOLD, 28));
        heading.setForeground(UI.PRIMARY);

        JLabel welcome = new JLabel("Welcome back, " + user.getFullName() + "!");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcome.setForeground(UI.PRIMARY);

        JLabel hint = new JLabel(
            "A quick look at your student records and academic activity."
        );
        hint.setForeground(UI.GRAY);
        hint.setFont(UI.BODY);

        intro.add(heading);
        intro.add(Box.createVerticalStrut(8));
        intro.add(welcome);
        intro.add(Box.createVerticalStrut(4));
        intro.add(hint);

        JPanel top = new JPanel(new BorderLayout(0, 18));
        top.setOpaque(false);
        top.add(intro, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 4, 16, 0));
        cards.setOpaque(false);

        cards.add(card("TOTAL STUDENTS", lblStudents, UI.ACCENT, "01"));
        cards.add(card("ACTIVE STUDENTS", lblActive, UI.DANGER, "02"));
        cards.add(card(
            "SUBJECTS", lblSubjects, UI.PRIMARY, "03"
        ));
        cards.add(card(
            "ACTIVE ENROLLMENTS", lblEnrollments, UI.DANGER, "04"
        ));

        top.add(cards, BorderLayout.CENTER);
        content.add(top, BorderLayout.NORTH);

        JPanel modules = new JPanel(new BorderLayout(0, 14));
        modules.setOpaque(false);

        JLabel moduleTitle = new JLabel("Quick access");
        moduleTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        moduleTitle.setForeground(UI.PRIMARY);
        modules.add(moduleTitle, BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridLayout(1, 3, 16, 0));
        actions.setOpaque(false);

        actions.add(moduleCard(
            "Manage Students",
            "View, add and update student records",
            "Open student records",
            UI.ACCENT,
            e -> openStudents()
        ));

        actions.add(moduleCard(
            "Enrollment",
            "Manage subject enrollment transactions",
            "Open enrollment",
            UI.DANGER,
            e -> openEnrollment()
        ));

        actions.add(moduleCard(
            "Reports & Summary",
            "Review academic and enrollment summaries",
            "View reports",
            UI.PRIMARY,
            e -> openReports()
        ));

        modules.add(actions, BorderLayout.CENTER);
        content.add(modules, BorderLayout.CENTER);
        add(UI.scrollable(content, 1040, 620), BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                refreshStats();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                exitApp();
            }
        });

        refreshStats();
        UI.enlargeText(this);
    }

    private static JLabel metricValue() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("SansSerif", Font.BOLD, 40));
        label.setForeground(UI.PRIMARY);
        return label;
    }

    private JPanel card(
        String title,
        JLabel value,
        Color accent,
        String number
    ) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);

        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UI.BORDER),
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
            )
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(UI.GRAY);

        JLabel numberLabel = new JLabel(number);
        numberLabel.setForeground(UI.GRAY);
        numberLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(titleLabel, BorderLayout.WEST);
        top.add(numberLabel, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);
        panel.add(value, BorderLayout.CENTER);

        return panel;
    }

    private JPanel moduleCard(
        String title,
        String description,
        String action,
        Color color,
        ActionListener listener
    ) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UI.BORDER),
            BorderFactory.createEmptyBorder(28, 28, 28, 28)
        ));

        JLabel dot = new JLabel("●");
        dot.setForeground(color);
        dot.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel name = new JLabel(title);
        name.setForeground(UI.PRIMARY);
        name.setFont(new Font("SansSerif", Font.BOLD, 21));

        JLabel desc = new JLabel(
            "<html><div style='width:260px'>"
            + description
            + "</div></html>"
        );
        desc.setForeground(UI.GRAY);
        desc.setFont(UI.BODY);

        // Initialize the button before configuring it.
        JButton actionButton = UI.button(action + "  →", color);
        actionButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        actionButton.setPreferredSize(new Dimension(
            actionButton.getPreferredSize().width,
            50
        ));
        actionButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        actionButton.addActionListener(listener);

        panel.add(dot);
        panel.add(Box.createVerticalStrut(12));
        panel.add(name);
        panel.add(Box.createVerticalStrut(8));
        panel.add(desc);
        panel.add(Box.createVerticalGlue());
        panel.add(actionButton);

        return panel;
    }

    private JMenuBar buildMenu() {
        JMenuBar bar = new JMenuBar();
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createMatteBorder(
            0, 0, 1, 0, UI.BORDER
        ));

        JButton logoutButton = UI.button("Log Out", UI.DANGER);
        logoutButton.addActionListener(e -> logout());
        bar.add(logoutButton);

        return bar;
    }

    private void refreshStats() {
        int active = 0;

        for (Student student : store.getStudents()) {
            if ("Active".equals(student.getStatus())) {
                active++;
            }
        }

        int enrolled = 0;

        for (Enrollment enrollment : store.getEnrollments()) {
            if (enrollment.isEnrolled()) {
                enrolled++;
            }
        }

        lblStudents.setText(
            String.valueOf(store.getStudents().size())
        );
        lblActive.setText(String.valueOf(active));
        lblSubjects.setText(
            String.valueOf(store.getSubjects().size())
        );
        lblEnrollments.setText(String.valueOf(enrolled));
    }

    private void openStudents() {
        if (studentsForm == null || !studentsForm.isDisplayable()) {
            studentsForm = new ManageStudentsForm();
        }

        studentsForm.setVisible(true);
        studentsForm.toFront();
    }

    private void openEnrollment() {
        if (enrollmentForm == null || !enrollmentForm.isDisplayable()) {
            enrollmentForm = new EnrollmentForm();
        } else {
            enrollmentForm.reloadAll();
        }

        enrollmentForm.setVisible(true);
        enrollmentForm.toFront();
    }

    private void openReports() {
        if (reportsForm == null || !reportsForm.isDisplayable()) {
            reportsForm = new ReportsForm();
        } else {
            reportsForm.refresh();
        }

        reportsForm.setVisible(true);
        reportsForm.toFront();
    }

    private void closeChildren() {
        if (studentsForm != null) {
            studentsForm.dispose();
        }

        if (enrollmentForm != null) {
            enrollmentForm.dispose();
        }

        if (reportsForm != null) {
            reportsForm.dispose();
        }
    }

    private void logout() {
        boolean confirmed = UI.confirmYesNo(
            this,
            "Are you sure you want to logout?",
            "Logout"
        );

        if (confirmed) {
            LoginForm.clearRememberedSession();
            closeChildren();
            dispose();
            new LoginForm().setVisible(true);
        }
    }

    private void exitApp() {
        boolean confirmed = UI.confirmYesNo(
            this,
            "Are you sure you want to exit the application?",
            "Confirm Exit"
        );

        if (confirmed) {
            System.exit(0);
        }
    }
}
