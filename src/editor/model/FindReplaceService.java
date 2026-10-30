package editor.model;

/**
 * Dịch vụ tìm kiếm / thay thế — tách logic khỏi dialog UI.
 * Thể hiện ĐÓNG GÓI nghiệp vụ tìm kiếm.
 */
public class FindReplaceService {

    public static class SearchResult {
        private final int start;
        private final int end;

        public SearchResult(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public int length() {
            return end - start;
        }
    }

    public SearchResult findNext(String content, String query, int fromIndex,
                                 boolean matchCase, boolean wrapAround) {
        if (content == null || query == null || query.isEmpty()) {
            return null;
        }

        String haystack = matchCase ? content : content.toLowerCase();
        String needle = matchCase ? query : query.toLowerCase();

        int start = Math.max(0, fromIndex);
        int index = haystack.indexOf(needle, start);
        if (index < 0 && wrapAround && start > 0) {
            index = haystack.indexOf(needle, 0);
        }
        if (index < 0) {
            return null;
        }
        return new SearchResult(index, index + query.length());
    }

    public SearchResult findPrevious(String content, String query, int fromIndex,
                                     boolean matchCase, boolean wrapAround) {
        if (content == null || query == null || query.isEmpty()) {
            return null;
        }

        String haystack = matchCase ? content : content.toLowerCase();
        String needle = matchCase ? query : query.toLowerCase();

        int end = Math.min(content.length(), Math.max(0, fromIndex));
        int index = haystack.lastIndexOf(needle, Math.max(0, end - 1));
        if (index < 0 && wrapAround) {
            index = haystack.lastIndexOf(needle);
        }
        if (index < 0) {
            return null;
        }
        return new SearchResult(index, index + query.length());
    }

    public int replaceAll(Editable editable, String find, String replace,
                          boolean matchCase) {
        if (find == null || find.isEmpty()) {
            return 0;
        }
        String content = editable.getContent();
        StringBuilder result = new StringBuilder();
        int count = 0;
        int i = 0;
        while (i < content.length()) {
            boolean match = matchesAt(content, find, i, matchCase);
            if (match) {
                result.append(replace == null ? "" : replace);
                i += find.length();
                count++;
            } else {
                result.append(content.charAt(i));
                i++;
            }
        }
        if (count > 0) {
            editable.setContent(result.toString());
        }
        return count;
    }

    private boolean matchesAt(String content, String find, int index, boolean matchCase) {
        if (index + find.length() > content.length()) {
            return false;
        }
        String slice = content.substring(index, index + find.length());
        return matchCase ? slice.equals(find) : slice.equalsIgnoreCase(find);
    }
}
