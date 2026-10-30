package editor.ui;

import editor.model.RecentFilesManager;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.util.function.Consumer;

/**
 * Panel JList hiển thị file gần đây — minh họa JList trong Swing.
 */
public class RecentFilesPanel extends JPanel {

    private final RecentFilesManager manager;
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> list = new JList<>(listModel);
    private Consumer<File> openHandler;

    public RecentFilesPanel(RecentFilesManager manager) {
        super(new BorderLayout(4, 4));
        this.manager = manager;
        setBorder(BorderFactory.createTitledBorder("File gần đây"));
        setPreferredSize(new Dimension(220, 120));

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && list.getSelectedIndex() >= 0 && openHandler != null) {
                openHandler.accept(manager.get(list.getSelectedIndex()));
            }
        });

        JButton clearBtn = new JButton("Xóa danh sách");
        clearBtn.addActionListener(e -> {
            manager.clear();
            refresh();
        });

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        south.add(clearBtn);

        add(new JScrollPane(list), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        refresh();
    }

    public void setOpenHandler(Consumer<File> openHandler) {
        this.openHandler = openHandler;
    }

    public void refresh() {
        listModel.clear();
        for (String name : manager.getDisplayNames()) {
            listModel.addElement(name);
        }
    }
}
