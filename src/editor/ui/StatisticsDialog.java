package editor.ui;

import editor.model.TextStatistics;
import editor.util.ChartHelper;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Map;

/**
 * JDialog thống kê văn bản: JTable + JFreeChart (cột & tròn).
 */
public class StatisticsDialog extends JDialog {

    public StatisticsDialog(JFrame owner, String documentTitle, TextStatistics stats) {
        super(owner, "Thống kê — " + documentTitle, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(780, 520));

        JTable summaryTable = createSummaryTable(stats);
        JTable frequencyTable = createFrequencyTable(stats);

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JSplitPane tableSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                wrap("Tóm tắt", summaryTable),
                wrap("Tần suất chữ cái", frequencyTable));
        tableSplit.setResizeWeight(0.45);
        tablePanel.add(tableSplit, BorderLayout.CENTER);

        JFreeChart barChart = ChartHelper.createSummaryBarChart(stats);
        JFreeChart pieChart = ChartHelper.createLetterFrequencyChart(stats);
        ChartPanel barPanel = new ChartPanel(barChart);
        ChartPanel piePanel = new ChartPanel(pieChart);

        JTabbedPane chartTabs = new JTabbedPane();
        chartTabs.addTab("Biểu đồ cột", barPanel);
        chartTabs.addTab("Biểu đồ tròn", piePanel);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                tablePanel, chartTabs);
        mainSplit.setResizeWeight(0.4);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Đóng");
        closeBtn.addActionListener(e -> dispose());
        south.add(closeBtn);

        setLayout(new BorderLayout());
        add(mainSplit, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }

    private JScrollPane wrap(String title, JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder(title));
        return scroll;
    }

    private JTable createSummaryTable(TextStatistics stats) {
        String[] columns = {"Chỉ số", "Giá trị"};
        DefaultTableModel model = new DefaultTableModel(stats.toSummaryTableData(), columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(22);
        return table;
    }

    private JTable createFrequencyTable(TextStatistics stats) {
        String[] columns = {"Chữ cái", "Số lần"};
        Map<Character, Integer> freq = stats.getLetterFrequency();
        Object[][] data = new Object[freq.size()][2];
        int i = 0;
        for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
            data[i][0] = entry.getKey();
            data[i][1] = entry.getValue();
            i++;
        }
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        return new JTable(model);
    }
}
