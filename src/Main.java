import javax.swing.*;

/** Program entry point. */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) { }
        UI.installTheme();
        SwingUtilities.invokeLater(LoginForm::showStartupWindow);
    }
}
