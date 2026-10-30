package editor.util;

import editor.model.TextStatistics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.util.Map;

/**
 * Tạo biểu đồ JFreeChart từ TextStatistics.
 * Tách biệt trực quan hóa khỏi dialog UI.
 */
public final class ChartHelper {

    private ChartHelper() {
    }

    public static JFreeChart createSummaryBarChart(TextStatistics stats) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(stats.getCharacters(), "Số lượng", "Ký tự");
        dataset.addValue(stats.getCharactersNoSpaces(), "Số lượng", "Ký tự (no space)");
        dataset.addValue(stats.getWords(), "Số lượng", "Từ");
        dataset.addValue(stats.getLines(), "Số lượng", "Dòng");
        dataset.addValue(stats.getParagraphs(), "Số lượng", "Đoạn");

        return ChartFactory.createBarChart(
                "Thống kê tổng quan",
                "Loại",
                "Số lượng",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
    }

    public static JFreeChart createLetterFrequencyChart(TextStatistics stats) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Map<Character, Integer> frequency = stats.getLetterFrequency();
        if (frequency.isEmpty()) {
            dataset.setValue("(trống)", 1);
        } else {
            // Chỉ lấy top chữ cái có tần suất > 0
            for (Map.Entry<Character, Integer> entry : frequency.entrySet()) {
                dataset.setValue(String.valueOf(entry.getKey()), entry.getValue());
            }
        }

        return ChartFactory.createPieChart(
                "Tần suất chữ cái (a–z)",
                dataset,
                true,
                true,
                false
        );
    }
}
