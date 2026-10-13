
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class LoginForm extends JFrame {
    private static final String SESSION_USERNAME_KEY = "activeUsername";

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
        setMinimumSize(new Dimension(640, 420));
        setSize(1050, 680);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBackground(BACKGROUND);

        root.add(createWelcomePanel());
        root.add(createLoginPanel());

        root.setPreferredSize(new Dimension(800, 680));
        setContentPane(UI.scrollable(root, 800, 680));

        getRootPane().setDefaultButton(btnLogin);
        UI.enlargeText(this);
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
        panel.setBackground(new Color(246, 249, 255));
        panel.setBorder(new EmptyBorder(32, 44, 32, 44));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(30, 38, 30, 38));

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
        cbShowPassword.setFont(new Font("SansSerif", Font.PLAIN, 16));
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
        btnLogin.setPreferredSize(new Dimension(180, 54));

        btnLogin.addActionListener(e -> doLogin());

        JButton btnClear = new JButton("Clear fields");
        UI.styleButton(btnClear, new Color(241, 245, 251));
        btnClear.setForeground(PRIMARY);
        btnClear.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnClear.setPreferredSize(new Dimension(180, 54));

        btnClear.addActionListener(e -> {
            tfUser.setText("");
            pfPass.setText("");
            cbShowPassword.setSelected(false);
            pfPass.setEchoChar('*');
            tfUser.requestFocusInWindow();
        });

        // Footer
        JLabel footer = new JLabel("Student Management System");

        footer.setFont(new Font("SansSerif", Font.PLAIN, 14));
        footer.setForeground(MUTED);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assemble the form
        form.add(usernameLabel);
        form.add(Box.createVerticalStrut(8));
        form.add(tfUser);

        form.add(Box.createVerticalStrut(20));
        form.add(passwordLabel);
        form.add(Box.createVerticalStrut(8));
        form.add(pfPass);

        form.add(Box.createVerticalStrut(12));
        form.add(cbShowPassword);

        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        actions.add(btnLogin);
        actions.add(btnClear);

        form.add(Box.createVerticalStrut(24));
        form.add(actions);

        form.add(Box.createVerticalStrut(26));
        form.add(new JSeparator());
        form.add(Box.createVerticalStrut(14));
        form.add(footer);

        // Let the form stretch horizontally
        form.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel banner = new JPanel();
        banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS));
        banner.setBackground(PRIMARY);
        banner.setBorder(new EmptyBorder(24, 38, 20, 38));

        JLabel eyebrow = new JLabel("STUDENT PORTAL ACCOUNT");
        eyebrow.setFont(new Font("SansSerif", Font.BOLD, 13));
        eyebrow.setForeground(new Color(218, 229, 255));
        eyebrow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel("Sign in");
        heading.setFont(new Font("SansSerif", Font.BOLD, 32));
        heading.setForeground(Color.WHITE);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel description = new JLabel(
            "<html><div style='width:210px'>"
            + "Enter your details to access the workspace."
            + "</div></html>"
        );
        description.setFont(new Font("SansSerif", Font.PLAIN, 16));
        description.setForeground(new Color(231, 238, 255));
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        banner.add(eyebrow);
        banner.add(Box.createVerticalStrut(8));
        banner.add(heading);
        banner.add(Box.createVerticalStrut(4));
        banner.add(description);

        JPanel bannerAccent = new JPanel();
        bannerAccent.setBackground(UI.DANGER);
        bannerAccent.setPreferredSize(new Dimension(0, 5));
        bannerAccent.setMinimumSize(new Dimension(0, 5));
        bannerAccent.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));

        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.add(banner, BorderLayout.CENTER);
        bannerPanel.add(bannerAccent, BorderLayout.SOUTH);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        card.add(bannerPanel, BorderLayout.NORTH);
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
        field.setBackground(Color.WHITE);
        field.setCaretColor(PRIMARY);
        Border normalBorder = BorderFactory.createCompoundBorder(
            new UI.RoundedBorder(BORDER, 16, 1),
            new EmptyBorder(11, 13, 11, 13)
        );
        Border focusedBorder = BorderFactory.createCompoundBorder(
            new UI.RoundedBorder(PRIMARY, 16, 2),
            new EmptyBorder(10, 12, 10, 12)
        );
        field.setBorder(normalBorder);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                field.setBorder(focusedBorder);
            }

            @Override
            public void focusLost(FocusEvent event) {
                field.setBorder(normalBorder);
            }
        });

        field.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 54)
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

        rememberSession(user);
        dispose();
        new Dashboard(user).setVisible(true);
    }

    public static void showStartupWindow() {
        Preferences preferences = Preferences.userNodeForPackage(LoginForm.class);
        String username = preferences.get(SESSION_USERNAME_KEY, null);

        if (username != null) {
            for (User user : DataStore.get().getUsers()) {
                if (username.equals(user.getUsername())) {
                    new Dashboard(user).setVisible(true);
                    return;
                }
            }

            clearRememberedSession();
        }

        new LoginForm().setVisible(true);
    }

    private static void rememberSession(User user) {
        try {
            Preferences preferences = Preferences.userNodeForPackage(LoginForm.class);
            preferences.put(SESSION_USERNAME_KEY, user.getUsername());
            preferences.flush();
        } catch (BackingStoreException | SecurityException ignored) {
        }
    }

    static void clearRememberedSession() {
        try {
            Preferences preferences = Preferences.userNodeForPackage(LoginForm.class);
            preferences.remove(SESSION_USERNAME_KEY);
            preferences.flush();
        } catch (BackingStoreException | SecurityException ignored) {
        }
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
            showStartupWindow();
        });
    }
}
