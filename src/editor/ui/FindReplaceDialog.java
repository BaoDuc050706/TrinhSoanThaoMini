package editor.ui;

import editor.model.FindReplaceService;
import editor.model.FindReplaceService.SearchResult;
import editor.model.TextDocument;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Supplier;

/**
 * JDialog tìm / thay thế văn bản.
 */
public class FindReplaceDialog extends JDialog {

    private final JTextField findField = new JTextField(24);
    private final JTextField replaceField = new JTextField(24);
    private final JCheckBox matchCaseBox = new JCheckBox("Phân biệt hoa/thường");
    private final JCheckBox wrapBox = new JCheckBox("Quay vòng", true);
    private final FindReplaceService service = new FindReplaceService();
    private final Supplier<EditorTabPanel> activeTabSupplier;

    public FindReplaceDialog(JFrame owner, Supplier<EditorTabPanel> activeTabSupplier) {
        super(owner, "Tìm kiếm & Thay thế", false);
        this.activeTabSupplier = activeTabSupplier;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUi();
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUi() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Tìm:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        form.add(findField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        form.add(new JLabel("Thay bằng:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        form.add(replaceField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        form.add(matchCaseBox, gbc);
        gbc.gridy = 3;
        form.add(wrapBox, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton findNextBtn = new JButton("Tìm tiếp");
        JButton findPrevBtn = new JButton("Tìm trước");
        JButton replaceBtn = new JButton("Thay thế");
        JButton replaceAllBtn = new JButton("Thay tất cả");
        JButton closeBtn = new JButton("Đóng");

        findNextBtn.addActionListener(e -> find(true));
        findPrevBtn.addActionListener(e -> find(false));
        replaceBtn.addActionListener(e -> replaceOne());
        replaceAllBtn.addActionListener(e -> replaceAll());
        closeBtn.addActionListener(e -> dispose());

        buttons.add(findPrevBtn);
        buttons.add(findNextBtn);
        buttons.add(replaceBtn);
        buttons.add(replaceAllBtn);
        buttons.add(closeBtn);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(findNextBtn);
    }

    private EditorTabPanel requireTab() {
        EditorTabPanel tab = activeTabSupplier.get();
        if (tab == null) {
            JOptionPane.showMessageDialog(this, "Không có tab đang mở.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
        return tab;
    }

    private void find(boolean forward) {
        EditorTabPanel tab = requireTab();
        if (tab == null) {
            return;
        }
        String query = findField.getText();
        if (query.isEmpty()) {
            return;
        }
        TextDocument doc = tab.getDocument();
        int caret = tab.getTextArea().getCaretPosition();
        SearchResult result = forward
                ? service.findNext(doc.getContent(), query, caret,
                matchCaseBox.isSelected(), wrapBox.isSelected())
                : service.findPrevious(doc.getContent(), query, caret,
                matchCaseBox.isSelected(), wrapBox.isSelected());

        if (result == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy \"" + query + "\".",
                    "Tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        tab.getTextArea().select(result.getStart(), result.getEnd());
        tab.requestFocusOnEditor();
    }

    private void replaceOne() {
        EditorTabPanel tab = requireTab();
        if (tab == null) {
            return;
        }
        String selected = tab.getTextArea().getSelectedText();
        String find = findField.getText();
        if (selected != null && !selected.isEmpty()) {
            boolean same = matchCaseBox.isSelected()
                    ? selected.equals(find)
                    : selected.equalsIgnoreCase(find);
            if (same) {
                tab.getTextArea().replaceSelection(replaceField.getText());
            }
        }
        find(true);
    }

    private void replaceAll() {
        EditorTabPanel tab = requireTab();
        if (tab == null) {
            return;
        }
        int count = service.replaceAll(tab.getDocument(), findField.getText(),
                replaceField.getText(), matchCaseBox.isSelected());
        JOptionPane.showMessageDialog(this,
                "Đã thay thế " + count + " lần.",
                "Thay tất cả", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showForFind() {
        setVisible(true);
        findField.requestFocusInWindow();
        findField.selectAll();
    }
}
