package editor.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * JDialog giới thiệu — có banner vẽ bằng Graphics2D.
 */
public class AboutDialog extends JDialog {

    public AboutDialog(JFrame owner) {
        super(owner, "Giới thiệu", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel banner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint paint = new GradientPaint(
                            0, 0, new Color(40, 80, 140),
                            getWidth(), getHeight(), new Color(70, 140, 180));
                    g2.setPaint(paint);
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fillOval(-40, -30, 160, 120);
                    g2.fillOval(getWidth() - 100, 10, 140, 100);

                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                    g2.drawString("Mini Text Editor", 24, 48);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    g2.drawString("Đồ án OOP — Swing & JFreeChart", 24, 74);
                } finally {
                    g2.dispose();
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(420, 100);
            }
        };

        JLabel info = new JLabel(
                "<html><body style='width:360px'>"
                        + "<b>Trình soạn thảo văn bản Mini</b><br><br>"
                        + "Tính năng: nhiều tab, mở/lưu file, tìm–thay thế,<br>"
                        + "hoàn tác/làm lại, thống kê + biểu đồ JFreeChart.<br><br>"
                        + "Nguyên lý OOP: Đóng gói, Kế thừa, Đa hình, Trừu tượng.<br>"
                        + "JDK 17 · Swing · JFreeChart 1.5.x"
                        + "</body></html>",
                SwingConstants.LEFT
        );
        info.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Đóng");
        close.addActionListener(e -> dispose());
        south.add(close);

        setLayout(new BorderLayout());
        add(banner, BorderLayout.NORTH);
        add(info, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }
}
