import javax.swing.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

/** Small helper class so all forms share the same look. */
public class UI {
    public static final Color PRIMARY = new Color(25, 52, 100);
    public static final Color SUCCESS = new Color(37, 99, 235);
    public static final Color WARNING = new Color(198, 40, 55);
    public static final Color DANGER = new Color(198, 40, 55);
    public static final Color GRAY = new Color(91, 103, 122);
    public static final Color BG = new Color(243, 247, 253);
    public static final Color ACCENT = new Color(37, 99, 235);
    public static final Color BORDER = new Color(215, 224, 237);
    public static final Font TITLE = new Font("SansSerif", Font.BOLD, 26);
    public static final Font BOLD = new Font("SansSerif", Font.BOLD, 15);
    public static final Font BODY = new Font("SansSerif", Font.PLAIN, 15);

    public static void installTheme() {
        UIManager.put("Button.foreground", PRIMARY);
        UIManager.put("Button.background", ACCENT);
        UIManager.put("Button.font", BOLD);
        UIManager.put("Label.foreground", PRIMARY);
        UIManager.put("OptionPane.messageForeground", PRIMARY);
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("OptionPane.buttonFont", BOLD);
        UIManager.put("TextField.foreground", PRIMARY);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("PasswordField.foreground", PRIMARY);
        UIManager.put("PasswordField.background", Color.WHITE);
        UIManager.put("ComboBox.foreground", PRIMARY);
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("CheckBox.foreground", PRIMARY);
        UIManager.put("CheckBox.background", Color.WHITE);
        UIManager.put("Table.foreground", PRIMARY);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.selectionForeground", PRIMARY);
        UIManager.put("Table.selectionBackground", new Color(219, 231, 251));
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("TableHeader.background", PRIMARY);
        UIManager.put("TabbedPane.foreground", PRIMARY);
        UIManager.put("Panel.background", BG);
    }

    public static void styleButton(JButton button, Color bg) {
        button.setUI(new BasicButtonUI());
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFont(BOLD);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker()),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        button.setMinimumSize(new Dimension(96, 40));
        button.setPreferredSize(new Dimension(
            Math.max(button.getPreferredSize().width, 96),
            Math.max(button.getPreferredSize().height, 40)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(bg.darker());
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bg);
            }
        });
    }

    public static JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setMargin(new Insets(8, 14, 8, 14));
        styleButton(b, bg);
        return b;
    }

    public static JPanel header(String title) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 12));
        p.setBackground(PRIMARY);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, DANGER));
        JLabel l = new JLabel(title);
        l.setFont(TITLE);
        l.setForeground(Color.WHITE);
        p.add(l);
        return p;
    }

    public static DefaultTableModel model(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    public static void style(JTable t) {
        t.setRowHeight(40);
        t.setFont(new Font("SansSerif", Font.PLAIN, 15));
        t.setForeground(PRIMARY);
        t.setBackground(Color.WHITE);
        t.setSelectionBackground(new Color(219, 231, 251));
        t.setSelectionForeground(PRIMARY);
        t.setGridColor(new Color(232, 237, 245));
        t.setFillsViewportHeight(true);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setBackground(PRIMARY);
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setFont(BOLD);
        t.getTableHeader().setPreferredSize(new Dimension(0, 46));
        t.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean selected,
                    boolean focused,
                    int row,
                    int column) {
                JLabel label = new JLabel(
                    value == null ? "" : value.toString(),
                    SwingConstants.LEFT
                );
                label.setOpaque(true);
                label.setBackground(PRIMARY);
                label.setForeground(Color.WHITE);
                label.setFont(BOLD);
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(
                        0, 0, 1, 1, new Color(62, 87, 128)
                    ),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)
                ));
                return label;
            }
        });
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setShowVerticalLines(false);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        DefaultTableCellRenderer bodyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean selected,
                    boolean focused,
                    int row,
                    int column) {
                Component cell = super.getTableCellRendererComponent(
                    table, value, selected, focused, row, column
                );
                if (!selected) {
                    cell.setBackground(
                        row % 2 == 0 ? Color.WHITE : new Color(246, 249, 253)
                    );
                    cell.setForeground(PRIMARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return cell;
            }
        };
        for (int column = 0; column < t.getColumnCount(); column++) {
            t.getColumnModel().getColumn(column).setCellRenderer(bodyRenderer);
        }
    }

    public static String peso(double v) { return String.format("PHP %,.2f", v); }

    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmYesNo(
            Component parent,
            String message,
            String title
    ) {
        Window owner = parent == null
                ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(
                owner,
                title,
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(18, 18));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(22, 24, 18, 24)
        ));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        messageLabel.setForeground(PRIMARY);
        content.add(messageLabel, BorderLayout.CENTER);

        boolean[] confirmed = {false};
        JButton yes = button("Yes", DANGER);
        JButton no = button("No", PRIMARY);
        yes.setPreferredSize(new Dimension(110, 44));
        no.setPreferredSize(new Dimension(110, 44));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actions.setOpaque(false);
        actions.add(no);
        actions.add(yes);
        content.add(actions, BorderLayout.SOUTH);

        yes.addActionListener(e -> {
            confirmed[0] = true;
            dialog.dispose();
        });
        no.addActionListener(e -> dialog.dispose());
        dialog.getRootPane().setDefaultButton(no);
        dialog.setContentPane(content);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(390, dialog.getPreferredSize().height));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return confirmed[0];
    }
}
