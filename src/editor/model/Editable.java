package editor.model;

/**
 * Interface trừu tượng hóa thao tác chỉnh sửa văn bản.
 * Thể hiện nguyên lý TRỪU TƯỢNG (Abstraction).
 */
public interface Editable {

    String getContent();

    void setContent(String content);

    void insertText(int offset, String text);

    void deleteText(int offset, int length);

    boolean canUndo();

    boolean canRedo();

    void undo();

    void redo();
}
