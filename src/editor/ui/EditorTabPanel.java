package editor.ui;

import editor.model.TextDocument;
import editor.model.TextStatistics;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretListener;
import java.awt.BorderLayout;
import java.awt.Font;

/**
 * Một tab soạn thảo gắn với một TextDocument.
 * Đóng gói UI của tab + liên kết với model.
 */
public class EditorTabPanel extends JPanel {

    private final TextDocument document;
    private final JTextArea textArea;
    private final LineNumberPanel lineNumberPanel;

    public EditorTabPanel(TextDocument document) {
        super(new BorderLayout());
        this.document = document;

        textArea = new JTextArea();
        textArea.setDocument(document.getSwingDocument());
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        textArea.setLineWrap(false);
        textArea.setTabSize(4);

        lineNumberPanel = new LineNumberPanel(textArea);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumberPanel);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> lineNumberPanel.repaint());

        add(scrollPane, BorderLayout.CENTER);
    }

    public TextDocument getDocument() {
        return document;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void addCaretListener(CaretListener listener) {
        textArea.addCaretListener(listener);
    }

    public TextStatistics getStatistics() {
        return document.computeStatistics();
    }

    public int getCaretLine() {
        try {
            int pos = textArea.getCaretPosition();
            return textArea.getLineOfOffset(pos) + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    public int getCaretColumn() {
        try {
            int pos = textArea.getCaretPosition();
            int lineStart = textArea.getLineStartOffset(textArea.getLineOfOffset(pos));
            return pos - lineStart + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    public void requestFocusOnEditor() {
        textArea.requestFocusInWindow();
    }
}
