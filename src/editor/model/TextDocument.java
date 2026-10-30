package editor.model;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * Tài liệu văn bản thuần — kế thừa Document và triển khai Editable.
 * Thể hiện KẾ THỪA + ĐA HÌNH (ghi đè getContent/setContent/computeStatistics).
 */
public class TextDocument extends Document implements Editable {

    private final PlainDocument swingDocument;
    private final UndoManager undoManager;
    private DocumentChangeListener changeListener;

    public TextDocument(String title) {
        super(title);
        this.swingDocument = new PlainDocument();
        this.undoManager = new UndoManager();
        this.undoManager.setLimit(200);

        swingDocument.addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
            }
        });

        swingDocument.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                markChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                markChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                markChanged();
            }
        });
    }

    public static TextDocument createUntitled(int index) {
        return new TextDocument("Untitled-" + index);
    }

    public PlainDocument getSwingDocument() {
        return swingDocument;
    }

    public void setChangeListener(DocumentChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    private void markChanged() {
        setModified(true);
        if (changeListener != null) {
            changeListener.onDocumentChanged(this);
        }
    }

    @Override
    public String getContent() {
        try {
            return swingDocument.getText(0, swingDocument.getLength());
        } catch (BadLocationException e) {
            return "";
        }
    }

    /**
     * Thay toàn bộ nội dung (dùng cho tìm–thay tất cả).
     * Giữ cờ modified thông qua DocumentListener.
     */
    @Override
    public void setContent(String content) {
        try {
            swingDocument.remove(0, swingDocument.getLength());
            if (content != null && !content.isEmpty()) {
                swingDocument.insertString(0, content, null);
            }
        } catch (BadLocationException e) {
            throw new IllegalStateException("Không thể ghi nội dung tài liệu", e);
        }
    }

    /** Nạp nội dung từ file: xóa lịch sử undo và đánh dấu chưa sửa. */
    public void loadContent(String content) {
        setContent(content);
        undoManager.discardAllEdits();
        setModified(false);
    }

    @Override
    public void insertText(int offset, String text) {
        try {
            swingDocument.insertString(offset, text, null);
        } catch (BadLocationException e) {
            throw new IllegalArgumentException("Offset không hợp lệ: " + offset, e);
        }
    }

    @Override
    public void deleteText(int offset, int length) {
        try {
            swingDocument.remove(offset, length);
        } catch (BadLocationException e) {
            throw new IllegalArgumentException("Vùng xóa không hợp lệ", e);
        }
    }

    @Override
    public boolean canUndo() {
        return undoManager.canUndo();
    }

    @Override
    public boolean canRedo() {
        return undoManager.canRedo();
    }

    @Override
    public void undo() {
        try {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        } catch (CannotUndoException ignored) {
            // Không còn thao tác để hoàn tác
        }
    }

    @Override
    public void redo() {
        try {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        } catch (CannotRedoException ignored) {
            // Không còn thao tác để làm lại
        }
    }

    @Override
    public TextStatistics computeStatistics() {
        return TextStatistics.fromText(getContent());
    }

    /** Callback khi nội dung thay đổi — đóng gói sự kiện domain. */
    public interface DocumentChangeListener {
        void onDocumentChanged(TextDocument document);
    }
}
