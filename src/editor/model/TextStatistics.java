package editor.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Thống kê văn bản — đối tượng bất biến (immutable) sau khi tạo.
 * Đóng gói dữ liệu thống kê, tách biệt khỏi UI.
 */
public final class TextStatistics {

    private final int characters;
    private final int charactersNoSpaces;
    private final int words;
    private final int lines;
    private final int paragraphs;
    private final Map<Character, Integer> letterFrequency;

    private TextStatistics(int characters, int charactersNoSpaces, int words,
                           int lines, int paragraphs,
                           Map<Character, Integer> letterFrequency) {
        this.characters = characters;
        this.charactersNoSpaces = charactersNoSpaces;
        this.words = words;
        this.lines = lines;
        this.paragraphs = paragraphs;
        this.letterFrequency = Collections.unmodifiableMap(letterFrequency);
    }

    public static TextStatistics fromText(String text) {
        if (text == null || text.isEmpty()) {
            return new TextStatistics(0, 0, 0, 0, 0, new LinkedHashMap<>());
        }

        int characters = text.length();
        int charactersNoSpaces = 0;
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                charactersNoSpaces++;
            }
        }

        String trimmed = text.trim();
        int words = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;

        int lines = 1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                lines++;
            }
        }

        String[] parts = text.split("\\R\\s*\\R");
        int paragraphs = 0;
        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                paragraphs++;
            }
        }

        Map<Character, Integer> frequency = new TreeMap<>();
        String lower = text.toLowerCase(Locale.ROOT);
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            if (c >= 'a' && c <= 'z') {
                frequency.merge(c, 1, Integer::sum);
            }
        }

        return new TextStatistics(characters, charactersNoSpaces, words, lines,
                paragraphs, frequency);
    }

    public int getCharacters() {
        return characters;
    }

    public int getCharactersNoSpaces() {
        return charactersNoSpaces;
    }

    public int getWords() {
        return words;
    }

    public int getLines() {
        return lines;
    }

    public int getParagraphs() {
        return paragraphs;
    }

    public Map<Character, Integer> getLetterFrequency() {
        return letterFrequency;
    }

    /** Dữ liệu tóm tắt cho JTable. */
    public Object[][] toSummaryTableData() {
        return new Object[][]{
                {"Ký tự (có khoảng trắng)", characters},
                {"Ký tự (không khoảng trắng)", charactersNoSpaces},
                {"Từ", words},
                {"Dòng", lines},
                {"Đoạn văn", paragraphs}
        };
    }
}
