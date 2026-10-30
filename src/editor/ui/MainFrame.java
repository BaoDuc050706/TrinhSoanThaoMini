package editor.ui;

import editor.model.RecentFilesManager;
import editor.model.TextDocument;
import editor.model.TextStatistics;
import editor.util.FileIOHelper;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Cửa sổ chính: JFrame + menu + toolbar + tab + status bar + recent files.
 */
public class MainFrame extends JFrame {

    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final StatusBar statusBar = new StatusBar();
    private final RecentFilesManager recentFilesManager = new RecentFilesManager();
    private final RecentFilesPanel recentFilesPanel = new RecentFilesPanel(recentFilesManager);
    private final JFileChooser fileChooser = new JFileChooser();
    private final Map<EditorTabPanel, JLabel> tabTitles = new IdentityHashMap<>();

    private int untitledCounter = 1;
    private FindReplaceDialog findReplaceDialog;

    private Action newAction;
    private Action openAction;
    private Action saveAction;
    private Action saveAsAction;
    private Action closeTabAction;
    private Action exitAction;
    private Action undoAction;
    private Action redoAction;
    private Action findAction;
    private Action statsAction;
    private Action aboutAction;

    public MainFrame() {
        super("Mini Text Editor");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setPreferredSize(new Dimension(1000, 680));
        setMinimumSize(new Dimension(720, 480));

        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Văn bản (*.txt, *.md, *.java, *.csv)", "txt", "md", "java", "csv"));

        createActions();
        setJMenuBar(createMenuBar());
        buildLayout();
        bindWindowClose();

        recentFilesPanel.setOpenHandler(this::openFilePath);

        createNewTab();
        pack();
        setLocationRelativeTo(null);
        updateUiState();
    }

    private void buildLayout() {
        JPanel center = new JPanel(new BorderLayout());
        center.add(createToolBar(), BorderLayout.NORTH);

        JPanel tabs = new JPanel(new BorderLayout());
        tabs.add(tabbedPane, BorderLayout.CENTER);
        JButton newTabButton = button(newAction, "+");
        newTabButton.setToolTipText("Tạo tài liệu mới");
        tabs.add(newTabButton, BorderLayout.EAST);
        center.add(tabs, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                recentFilesPanel, center);
        split.setDividerLocation(230);
        split.setResizeWeight(0);

        setLayout(new BorderLayout());
        add(split, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        tabbedPane.addChangeListener(e -> updateUiState());
    }

    private void createActions() {
        newAction = action("Mới", KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK),
                e -> createNewTab());
        openAction = action("Mở...", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK),
                e -> openFile());
        saveAction = action("Lưu", KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK),
                e -> saveFile(false));
        saveAsAction = action("Lưu thành...",
                KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK),
                e -> saveFile(true));
        closeTabAction = action("Đóng tab", KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK),
                e -> closeCurrentTab());
        exitAction = action("Thoát", KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK),
                e -> tryExit());
        undoAction = action("Hoàn tác", KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK),
                e -> undo());
        redoAction = action("Làm lại", KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK),
                e -> redo());
        findAction = action("Tìm / Thay thế...", KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK),
                e -> showFindReplace());
        statsAction = action("Thống kê...", KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK),
                e -> showStatistics());
        aboutAction = action("Giới thiệu", null, e -> new AboutDialog(this).setVisible(true));
    }

    private Action action(String name, KeyStroke stroke, java.util.function.Consumer<ActionEvent> handler) {
        AbstractAction a = new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                handler.accept(e);
            }
        };
        if (stroke != null) {
            a.putValue(Action.ACCELERATOR_KEY, stroke);
        }
        return a;
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu fileMenu = new JMenu("Tệp");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(new JMenuItem(newAction));
        fileMenu.add(new JMenuItem(openAction));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(saveAction));
        fileMenu.add(new JMenuItem(saveAsAction));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(closeTabAction));
        fileMenu.add(new JMenuItem(exitAction));

        JMenu editMenu = new JMenu("Chỉnh sửa");
        editMenu.setMnemonic(KeyEvent.VK_E);
        editMenu.add(new JMenuItem(undoAction));
        editMenu.add(new JMenuItem(redoAction));
        editMenu.addSeparator();
        editMenu.add(new JMenuItem(findAction));

        JMenu viewMenu = new JMenu("Xem");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        viewMenu.add(new JMenuItem(statsAction));

        JMenu helpMenu = new JMenu("Trợ giúp");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.add(new JMenuItem(aboutAction));

        bar.add(fileMenu);
        bar.add(editMenu);
        bar.add(viewMenu);
        bar.add(helpMenu);
        return bar;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        toolBar.add(button(newAction, "Mới"));
        toolBar.add(button(openAction, "Mở"));
        toolBar.add(button(saveAction, "Lưu"));
        toolBar.addSeparator();
        toolBar.add(button(undoAction, "Hoàn tác"));
        toolBar.add(button(redoAction, "Làm lại"));
        toolBar.addSeparator();
        toolBar.add(button(findAction, "Tìm"));
        toolBar.add(button(statsAction, "Thống kê"));
        return toolBar;
    }

    private JButton button(Action action, String text) {
        JButton btn = new JButton(action);
        btn.setText(text);
        btn.setFocusable(false);
        return btn;
    }

    private void bindWindowClose() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                tryExit();
            }
        });
    }

    public EditorTabPanel getActiveTab() {
        int index = tabbedPane.getSelectedIndex();
        if (index < 0) {
            return null;
        }
        return (EditorTabPanel) tabbedPane.getComponentAt(index);
    }

    private void createNewTab() {
        TextDocument document = TextDocument.createUntitled(untitledCounter++);
        addTab(document);
        statusBar.setMessage("Đã tạo tài liệu mới");
    }

    private void addTab(TextDocument document) {
        EditorTabPanel panel = new EditorTabPanel(document);
        JLabel title = new JLabel(document.getDisplayTitle());
        tabTitles.put(panel, title);
        document.setChangeListener(doc -> SwingUtilities.invokeLater(() -> {
            updateTabTitle(panel);
            updateUiState();
        }));
        panel.addCaretListener(e -> updateUiState());
        tabbedPane.addTab(document.getDisplayTitle(), panel);
        int index = tabbedPane.indexOfComponent(panel);
        tabbedPane.setTabComponentAt(index, createTabHeader(panel, title));
        tabbedPane.setSelectedComponent(panel);
        panel.requestFocusOnEditor();
        updateUiState();
    }

    private JPanel createTabHeader(EditorTabPanel tab, JLabel title) {
        JPanel header = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 0));
        header.setOpaque(false);
        header.add(title);

        JButton closeButton = new JButton("×");
        closeButton.setToolTipText("Đóng tab");
        closeButton.setFocusable(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        closeButton.setContentAreaFilled(false);
        closeButton.addActionListener(e -> closeTab(tab));
        header.add(closeButton);
        return header;
    }

    private void updateTabTitle(EditorTabPanel tab) {
        int index = tabbedPane.indexOfComponent(tab);
        if (index >= 0) {
            String title = tab.getDocument().getDisplayTitle();
            tabbedPane.setTitleAt(index, title);
            JLabel label = tabTitles.get(tab);
            if (label != null) {
                label.setText(title);
            }
        }
    }

    private void openFile() {
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        openFilePath(fileChooser.getSelectedFile());
    }

    private void openFilePath(File file) {
        if (file == null || !file.isFile()) {
            return;
        }
        // Nếu file đã mở sẵn thì chuyển tab
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            EditorTabPanel panel = (EditorTabPanel) tabbedPane.getComponentAt(i);
            if (file.equals(panel.getDocument().getFile())) {
                tabbedPane.setSelectedIndex(i);
                statusBar.setMessage("Đã chuyển tới tab: " + file.getName());
                return;
            }
        }
        try {
            String content = FileIOHelper.readText(file);
            TextDocument document = new TextDocument(file.getName());
            document.loadContent(content);
            document.setFile(file);
            addTab(document);
            recentFilesManager.add(file);
            recentFilesPanel.refresh();
            statusBar.setMessage("Đã mở: " + file.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể mở file:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean saveFile(boolean saveAs) {
        EditorTabPanel tab = getActiveTab();
        if (tab == null) {
            return false;
        }
        TextDocument doc = tab.getDocument();
        File target = doc.getFile();
        if (saveAs || target == null) {
            if (target != null) {
                fileChooser.setSelectedFile(target);
            } else {
                fileChooser.setSelectedFile(new File(doc.getTitle() + ".txt"));
            }
            if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return false;
            }
            target = fileChooser.getSelectedFile();
        }
        try {
            FileIOHelper.writeText(target, doc.getContent());
            doc.setFile(target);
            doc.setModified(false);
            updateTabTitle(tab);
            recentFilesManager.add(target);
            recentFilesPanel.refresh();
            statusBar.setMessage("Đã lưu: " + target.getAbsolutePath());
            updateUiState();
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể lưu file:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void closeCurrentTab() {
        EditorTabPanel tab = getActiveTab();
        if (tab == null) {
            return;
        }
        closeTab(tab);
    }

    private void closeTab(EditorTabPanel tab) {
        if (tabbedPane.indexOfComponent(tab) < 0) {
            return;
        }
        if (!confirmSaveIfNeeded(tab)) {
            return;
        }
        tabbedPane.remove(tab);
        tabTitles.remove(tab);
        updateUiState();
    }

    private boolean confirmSaveIfNeeded(EditorTabPanel tab) {
        TextDocument doc = tab.getDocument();
        if (!doc.isModified()) {
            return true;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "\"" + doc.getTitle() + "\" đã thay đổi. Bạn có muốn lưu?",
                "Lưu thay đổi",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) {
            return false;
        }
        if (choice == JOptionPane.YES_OPTION) {
            tabbedPane.setSelectedComponent(tab);
            return saveFile(false);
        }
        return true;
    }

    private void tryExit() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            EditorTabPanel tab = (EditorTabPanel) tabbedPane.getComponentAt(i);
            tabbedPane.setSelectedIndex(i);
            if (!confirmSaveIfNeeded(tab)) {
                return;
            }
        }
        dispose();
        System.exit(0);
    }

    private void undo() {
        EditorTabPanel tab = getActiveTab();
        if (tab != null) {
            tab.getDocument().undo();
            updateUiState();
        }
    }

    private void redo() {
        EditorTabPanel tab = getActiveTab();
        if (tab != null) {
            tab.getDocument().redo();
            updateUiState();
        }
    }

    private void showFindReplace() {
        if (findReplaceDialog == null) {
            findReplaceDialog = new FindReplaceDialog(this, this::getActiveTab);
        }
        findReplaceDialog.showForFind();
    }

    private void showStatistics() {
        EditorTabPanel tab = getActiveTab();
        if (tab == null) {
            return;
        }
        TextStatistics stats = tab.getStatistics();
        new StatisticsDialog(this, tab.getDocument().getTitle(), stats).setVisible(true);
    }

    private void updateUiState() {
        EditorTabPanel tab = getActiveTab();
        boolean hasTab = tab != null;
        saveAction.setEnabled(hasTab);
        saveAsAction.setEnabled(hasTab);
        closeTabAction.setEnabled(hasTab);
        findAction.setEnabled(hasTab);
        statsAction.setEnabled(hasTab);
        undoAction.setEnabled(hasTab && tab.getDocument().canUndo());
        redoAction.setEnabled(hasTab && tab.getDocument().canRedo());

        if (hasTab) {
            statusBar.setPosition(tab.getCaretLine(), tab.getCaretColumn());
            TextStatistics stats = tab.getStatistics();
            statusBar.setQuickStats(stats.getWords(), stats.getCharacters());
        } else {
            statusBar.setPosition(1, 1);
            statusBar.setQuickStats(0, 0);
        }
    }

    public static void launch() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Giữ L&F mặc định
        }
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
