package editor.ui;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Vẽ số dòng bằng Graphics2D — minh họa đồ họa tùy chỉnh trong Swing.
 */
public class LineNumberPanel extends JPanel {

    private final JTextArea textArea;
    private static final int PADDING = 8;

    public LineNumberPanel(JTextArea textArea) {
        this.textArea = textArea;
        setBackground(new Color(245, 245, 248));
        setPreferredWidth();

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refresh();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refresh();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refresh();
            }
        });

        textArea.addCaretListener(e -> repaint());
        textArea.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                refresh();
            }
        });
    }

    private void refresh() {
        setPreferredWidth();
        revalidate();
        repaint();
    }

    private void setPreferredWidth() {
        int lines = Math.max(1, textArea.getLineCount());
        int digits = String.valueOf(lines).length();
        FontMetrics fm = getFontMetrics(textArea.getFont());
        int width = PADDING * 2 + fm.charWidth('0') * Math.max(2, digits);
        setPreferredSize(new Dimension(width, Integer.MAX_VALUE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Font font = textArea.getFont();
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();

            // Nền và đường kẻ phải
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(210, 210, 220));
            g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());

            int startOffset = textArea.viewToModel2D(textArea.getVisibleRect().getLocation());
            int endOffset = textArea.viewToModel2D(new java.awt.Point(
                    textArea.getVisibleRect().x,
                    textArea.getVisibleRect().y + textArea.getVisibleRect().height
            ));

            int startLine;
            int endLine;
            try {
                startLine = textArea.getLineOfOffset(Math.max(0, startOffset));
                endLine = textArea.getLineOfOffset(Math.max(0, endOffset));
            } catch (javax.swing.text.BadLocationException e) {
                return;
            }

            g2.setColor(new Color(120, 120, 140));
            for (int line = startLine; line <= endLine; line++) {
                try {
                    java.awt.Rectangle rect = textArea.modelToView2D(
                            textArea.getLineStartOffset(line)).getBounds();
                    String label = String.valueOf(line + 1);
                    int x = getWidth() - PADDING - fm.stringWidth(label);
                    int y = rect.y + fm.getAscent();
                    g2.drawString(label, x, y);
                } catch (javax.swing.text.BadLocationException ignored) {
                    // Bỏ qua dòng lỗi
                }
            }
        } finally {
            g2.dispose();
        }
    }
}
