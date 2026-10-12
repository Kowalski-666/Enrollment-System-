
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginForm extends JFrame {

    // Colors - Student Portal Theme
    private static final Color PRIMARY = UI.ACCENT;
    private static final Color BACKGROUND = UI.BG;
    private static final Color TEXT = UI.PRIMARY;
    private static final Color MUTED = UI.GRAY;
    private static final Color BORDER = UI.BORDER;

    private final JTextField tfUser = new JTextField();
    private final JPasswordField pfPass = new JPasswordField();

    private JButton btnLogin;
    private JCheckBox cbShowPassword;

    public LoginForm() {
        super("Student Management System | Login");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 580));
        setSize(1050, 680);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBackground(BACKGROUND);

        root.add(createWelcomePanel());
        root.add(createLoginPanel());

        setContentPane(root);

        getRootPane().setDefaultButton(btnLogin);
    }

    // LEFT SIDE: Welcome panel
    private JPanel createWelcomePanel() {

        JPanel panel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                GradientPaint gradient = new GradientPaint(
                    0, 0,
                    UI.PRIMARY,
                    getWidth(), getHeight(),
                    UI.ACCENT
                );

                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(getWidth() - 160, -100, 300, 300);
                g2.fillOval(-100, getHeight() - 160, 250, 250);
                g2.fillOval(getWidth() - 100, getHeight() - 100, 180, 180);
                g2.setColor(UI.DANGER);
                g2.fillRect(getWidth() - 5, 0, 5, getHeight());

                g2.dispose();
            }
        };

        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(44, 48, 44, 48));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("STUDENT PORTAL");
        brand.setFont(new Font("SansSerif", Font.BOLD, 16));
        brand.setForeground(new Color(226, 235, 250));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel icon = new JLabel("✦");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 60));
        icon.setForeground(Color.WHITE);
        icon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel(
            "<html>Welcome to<br>Student Management</html>"
        );
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
            "<html><div style='width:260px;'>" +
            "A simple and organized way to manage " +
            "student information and academic records." +
            "</div></html>"
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 17));
        subtitle.setForeground(new Color(226, 235, 250));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel feature1 = createFeature("✓", "Organized student records");
        JLabel feature2 = createFeature("✓", "Easy access to information");
        JLabel feature3 = createFeature("✓", "Simple management experience");

        content.add(brand);
        content.add(Box.createVerticalStrut(35));
        content.add(icon);
        content.add(Box.createVerticalStrut(12));
        content.add(title);
        content.add(Box.createVerticalStrut(18));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(35));
        content.add(feature1);
        content.add(Box.createVerticalStrut(15));
        content.add(feature2);
        content.add(Box.createVerticalStrut(15));
        content.add(feature3);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        panel.add(content, gbc);

        return panel;
    }

    private JLabel createFeature(String symbol, String text) {

        JLabel label = new JLabel(symbol + "   " + text);

        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(Color.WHITE);

        return label;
    }

    // RIGHT SIDE: Login form
    private JPanel createLoginPanel() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(36, 44, 36, 44));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(40, 42, 40, 42));

        // Heading
        JLabel heading = new JLabel("Sign in");
        heading.setFont(new Font("SansSerif", Font.BOLD, 28));
        heading.setForeground(TEXT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel description = new JLabel("Enter your account details to continue.");
        description.setFont(new Font("SansSerif", Font.PLAIN, 15));
        description.setForeground(MUTED);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username
        JLabel usernameLabel = createLabel("Username");
        styleTextField(tfUser);

        tfUser.setToolTipText("Enter your username");

        // Password
        JLabel passwordLabel = createLabel("Password");
        styleTextField(pfPass);

        pfPass.setToolTipText("Enter your password");
        tfUser.setForeground(TEXT);
        pfPass.setForeground(TEXT);

        // Show password
        cbShowPassword = new JCheckBox("Show password");
        cbShowPassword.setFont(new Font("SansSerif", Font.PLAIN, 15));
        cbShowPassword.setForeground(MUTED);
        cbShowPassword.setBackground(Color.WHITE);
        cbShowPassword.setFocusPainted(false);
        cbShowPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbShowPassword.addActionListener(e -> {
            if (cbShowPassword.isSelected()) {
                pfPass.setEchoChar((char) 0);
            } else {
                pfPass.setEchoChar(
                    (Character) UIManager.get("PasswordField.echoChar")
                );
            }
        });

        // Buttons
        btnLogin = new JButton("Sign In");
        stylePrimaryButton(btnLogin);

        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        btnLogin.addActionListener(e -> doLogin());

        JButton btnClear = new JButton("Clear fields");
        btnClear.setFont(new Font("SansSerif", Font.PLAIN, 15));
        btnClear.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnClear.setForeground(PRIMARY);
        btnClear.setBackground(Color.WHITE);
        btnClear.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        btnClear.setContentAreaFilled(true);
        btnClear.setOpaque(true);
        btnClear.setFocusPainted(false);
        btnClear.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnClear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnClear.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnClear.setBackground(new Color(232, 240, 253));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnClear.setBackground(Color.WHITE);
            }
        });

        btnClear.addActionListener(e -> {
            tfUser.setText("");
            pfPass.setText("");
            cbShowPassword.setSelected(false);
            pfPass.setEchoChar('*');
            tfUser.requestFocusInWindow();
        });

        // Footer
        JLabel footer = new JLabel(
            "Student Management System"
        );

        footer.setFont(new Font("SansSerif", Font.PLAIN, 14));
        footer.setForeground(MUTED);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assemble the form
        form.add(heading);
        form.add(Box.createVerticalStrut(8));
        form.add(description);

        form.add(Box.createVerticalStrut(36));
        form.add(usernameLabel);
        form.add(Box.createVerticalStrut(8));
        form.add(tfUser);

        form.add(Box.createVerticalStrut(26));
        form.add(passwordLabel);
        form.add(Box.createVerticalStrut(8));
        form.add(pfPass);

        form.add(Box.createVerticalStrut(10));
        form.add(cbShowPassword);

        form.add(Box.createVerticalStrut(30));
        form.add(btnLogin);

        form.add(Box.createVerticalStrut(8));
        form.add(btnClear);

        form.add(Box.createVerticalStrut(34));
        form.add(new JSeparator());
        form.add(Box.createVerticalStrut(18));
        form.add(footer);

        // Let the form stretch horizontally
        form.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createMatteBorder(4, 0, 0, 0, UI.DANGER)
            ),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        card.add(form, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);

        panel.add(card, gbc);

        return panel;
    }

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private void styleTextField(JTextField field) {

        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setForeground(TEXT);
        field.setBackground(new Color(248, 250, 252));
        field.setCaretColor(PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        field.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 50)
        );

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void stylePrimaryButton(JButton button) {
        UI.styleButton(button, PRIMARY);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // LOGIN LOGIC
    private void doLogin() {

        String username = tfUser.getText().trim();
        String password = new String(pfPass.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter both username and password.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        User user = DataStore.get().authenticate(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(
                this,
                "Invalid username or password. Please try again.",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
            );

            pfPass.setText("");
            pfPass.requestFocusInWindow();
            return;
        }

        dispose();
        new Dashboard(user).setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {
                // Use the default Swing look and feel.
            }

            UI.installTheme();
            new LoginForm().setVisible(true);
        });
    }
}
