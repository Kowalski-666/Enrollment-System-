import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/** Small helper class so all forms share the same look. */
public class UI {
    public static final Color PRIMARY = new Color(17, 24, 39);
    public static final Color SUCCESS = new Color(37, 99, 235);
    public static final Color WARNING = new Color(198, 40, 55);
    public static final Color DANGER = new Color(198, 40, 55);
    public static final Color GRAY = new Color(58, 65, 76);
    public static final Color BG = new Color(243, 247, 253);
    public static final Color ACCENT = new Color(37, 99, 235);
    public static final Color BORDER = new Color(215, 224, 237);
    public static final Font TITLE = new Font("SansSerif", Font.BOLD, 28);
    public static final Font BOLD = new Font("SansSerif", Font.BOLD, 16);
    public static final Font BODY = new Font("SansSerif", Font.PLAIN, 16);

    public static void installTheme() {
        UIManager.put("Button.foreground", PRIMARY);
        UIManager.put("Button.background", ACCENT);
        UIManager.put("Button.font", BOLD);
        UIManager.put("Label.font", BODY);
        UIManager.put("Label.foreground", PRIMARY);
        UIManager.put("OptionPane.messageForeground", PRIMARY);
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("OptionPane.buttonFont", BOLD);
        UIManager.put("TextField.foreground", PRIMARY);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.font", BODY);
        UIManager.put("PasswordField.foreground", PRIMARY);
        UIManager.put("PasswordField.background", Color.WHITE);
        UIManager.put("PasswordField.font", BODY);
        UIManager.put("ComboBox.foreground", PRIMARY);
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.font", BODY);
        UIManager.put("CheckBox.foreground", PRIMARY);
        UIManager.put("CheckBox.background", Color.WHITE);
        UIManager.put("CheckBox.font", BODY);
        UIManager.put("Table.foreground", PRIMARY);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.selectionForeground", PRIMARY);
        UIManager.put("Table.selectionBackground", new Color(219, 231, 251));
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("TableHeader.background", PRIMARY);
        UIManager.put("TableHeader.font", BOLD);
        UIManager.put("TabbedPane.foreground", PRIMARY);
        UIManager.put("TabbedPane.font", BOLD);
        UIManager.put("Panel.background", BG);
    }

    public static void styleButton(JButton button, Color bg) {
        button.setUI(new RoundedButtonUI(bg));
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFont(BOLD);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        button.setMargin(new Insets(10, 18, 10, 18));
        button.setMinimumSize(new Dimension(112, 48));
        button.setPreferredSize(new Dimension(
            Math.max(button.getPreferredSize().width, 120),
            Math.max(button.getPreferredSize().height, 48)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setRolloverEnabled(true);
    }

    private static class RoundedButtonUI extends BasicButtonUI {
        private final Color baseColor;

        RoundedButtonUI(Color baseColor) {
            this.baseColor = baseColor;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill = baseColor;
            Color borderColor = baseColor.darker();

            if (model.isPressed()) {
                fill = baseColor.darker();
                borderColor = fill.darker();
            } else if (model.isRollover()) {
                fill = baseColor.brighter();
                borderColor = baseColor.darker();
            }

            g2.setColor(fill);
            g2.fillRoundRect(1, 1, b.getWidth() - 2, b.getHeight() - 2, 18, 18);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, b.getWidth() - 3, b.getHeight() - 3, 18, 18);

            g2.dispose();
            super.paint(g, c);
        }
    }

    public static JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setMargin(new Insets(8, 14, 8, 14));
        styleButton(b, bg);
        return b;
    }

    public static JScrollPane scrollable(
            Component content,
            int minimumContentWidth,
            int minimumContentHeight
    ) {
        Dimension preferred = content.getPreferredSize();
        content.setPreferredSize(new Dimension(
            Math.max(preferred.width, minimumContentWidth),
            Math.max(preferred.height, minimumContentHeight)
        ));

        JScrollPane scroll = new JScrollPane(
            content,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getHorizontalScrollBar().setUnitIncrement(20);
        return scroll;
    }

    public static void enlargeText(Component component) {
        Font font = component.getFont();
        if (font != null && font.getSize() < 16) {
            component.setFont(font.deriveFont(16f));
        }

        if (component instanceof JTable) {
            JTable table = (JTable) component;
            table.setRowHeight(Math.max(table.getRowHeight(), 48));
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                enlargeText(child);
            }
        }
    }

    public static void fillTableViewport(JTable table, JScrollPane scroll) {
        TableColumnModel columns = table.getColumnModel();
        int columnCount = columns.getColumnCount();
        int[] preferredWidths = new int[columnCount];
        int preferredTotal = 0;

        for (int i = 0; i < columnCount; i++) {
            preferredWidths[i] = columns.getColumn(i).getPreferredWidth();
            preferredTotal += preferredWidths[i];
        }

        final int totalPreferredWidth = preferredTotal;
        Runnable resizeColumns = () -> {
            int viewportWidth = scroll.getViewport().getExtentSize().width;
            if (viewportWidth >= totalPreferredWidth) {
                if (table.getAutoResizeMode()
                        != JTable.AUTO_RESIZE_ALL_COLUMNS) {
                    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                }
            } else {
                if (table.getAutoResizeMode() != JTable.AUTO_RESIZE_OFF) {
                    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                }

                for (int i = 0; i < columnCount; i++) {
                    TableColumn column = columns.getColumn(i);
                    column.setPreferredWidth(preferredWidths[i]);
                    column.setWidth(preferredWidths[i]);
                }
            }
        };

        ComponentAdapter resizeListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                resizeColumns.run();
            }
        };
        scroll.addComponentListener(resizeListener);
        scroll.getViewport().addComponentListener(resizeListener);
        scroll.getViewport().addChangeListener(event -> resizeColumns.run());
        scroll.addHierarchyListener(event -> {
            if (scroll.isShowing()) {
                SwingUtilities.invokeLater(resizeColumns);
            }
        });

        SwingUtilities.invokeLater(resizeColumns);
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
        t.setRowHeight(46);
        t.setFont(BODY);
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
        t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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

    public static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundedBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x + 1, y + 1, w - 3, h - 3, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 4, thickness + 6, thickness + 4, thickness + 6);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = thickness + 6;
            insets.top = thickness + 4;
            insets.right = thickness + 6;
            insets.bottom = thickness + 4;
            return insets;
        }
    }

    public static class RoundedLabel extends JLabel {
        private final Color fillColor;
        private final Color borderColor;
        private final int radius;

        public RoundedLabel(String text, Color fillColor, Color borderColor, Color foreColor, int radius) {
            super(text);
            this.fillColor = fillColor;
            this.borderColor = borderColor;
            this.radius = radius;
            setOpaque(false);
            setForeground(foreColor);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("SansSerif", Font.BOLD, 16));
            setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

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
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
