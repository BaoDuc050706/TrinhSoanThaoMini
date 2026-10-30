package editor.model;

import java.io.File;

/**
 * Lớp trừu tượng đại diện một tài liệu trong trình soạn thảo.
 * Thể hiện nguyên lý TRỪU TƯỢNG + ĐÓNG GÓI.
 */
public abstract class Document {

    private String title;
    private File file;
    private boolean modified;

    protected Document(String title) {
        this.title = title;
        this.file = null;
        this.modified = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        if (file != null) {
            this.title = file.getName();
        }
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public boolean hasFile() {
        return file != null;
    }

    /** Mỗi loại tài liệu tự quyết định cách đọc nội dung. */
    public abstract String getContent();

    /** Mỗi loại tài liệu tự quyết định cách ghi nội dung. */
    public abstract void setContent(String content);

    /** Thống kê đặc thù theo loại tài liệu. */
    public abstract TextStatistics computeStatistics();

    public String getDisplayTitle() {
        return modified ? title + " *" : title;
    }
}
