package editor.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Quản lý danh sách file gần đây (tối đa 10).
 * Đóng gói trạng thái recent files, cung cấp dữ liệu cho JList.
 */
public class RecentFilesManager {

    private static final int MAX_SIZE = 10;
    private final List<File> recentFiles = new ArrayList<>();

    public void add(File file) {
        if (file == null) {
            return;
        }
        recentFiles.remove(file);
        recentFiles.add(0, file);
        while (recentFiles.size() > MAX_SIZE) {
            recentFiles.remove(recentFiles.size() - 1);
        }
    }

    public List<File> getFiles() {
        return Collections.unmodifiableList(recentFiles);
    }

    public String[] getDisplayNames() {
        String[] names = new String[recentFiles.size()];
        for (int i = 0; i < recentFiles.size(); i++) {
            names[i] = recentFiles.get(i).getAbsolutePath();
        }
        return names;
    }

    public File get(int index) {
        return recentFiles.get(index);
    }

    public void clear() {
        recentFiles.clear();
    }

    public boolean isEmpty() {
        return recentFiles.isEmpty();
    }
}
