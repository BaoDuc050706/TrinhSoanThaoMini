package editor.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * Thanh trạng thái hiển thị thông tin vị trí con trỏ và thống kê nhanh.
 */
public class StatusBar extends JPanel {

    private final JLabel messageLabel;
    private final JLabel positionLabel;
    private final JLabel statsLabel;

    public StatusBar() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 210)),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        setPreferredSize(new Dimension(10, 26));

        messageLabel = new JLabel("Sẵn sàng");
        positionLabel = new JLabel("Ln 1, Col 1");
        statsLabel = new JLabel("0 từ | 0 ký tự");
        positionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        statsLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setOpaque(false);
        right.add(statsLabel);
        right.add(positionLabel);

        add(messageLabel, BorderLayout.WEST);
        add(right, BorderLayout.EAST);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public void setPosition(int line, int column) {
        positionLabel.setText("Ln " + line + ", Col " + column);
    }

    public void setQuickStats(int words, int characters) {
        statsLabel.setText(words + " từ | " + characters + " ký tự");
    }
}
