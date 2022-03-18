package interpreteur.utils;

public class StringUtils {
    public int indexOfIgnoreInString(String ligne) {
        return -1;
    }

    /**
     *
     * @param s the string checked
     * @param open the openning symbol
     * @param close the closing symbol
     * @return null if there are no openning symbol or the first openning symbol is before the last closing symbol
     */
    public static Range enclose(String s, final String open, final String close) {
        int start = s.indexOf(open);
        if (start == -1 || start > s.indexOf(close)) return null;
        int cptr = 1, idx = start + 1;
        for (; idx < s.length() && cptr != 0; idx++) {
            String currentChar = s.charAt(idx) + "";
            cptr += currentChar.equals(open) ? 1 : currentChar.equals(close) ? -1 : 0;
        }
        return new Range(start, idx);
    }

    /**
     *
     * @param s the string checked
     * @param open the openning symbol
     * @param close the closing symbol
     * @param from the index from which to start
     * @return null if there are no openning symbol or the first openning symbol is before the last closing symbol
     */
    public static Range enclose(String s, final String open, final String close, int from) {
        s = s.substring(from);
        int start = s.indexOf(open);
        if (start == -1 || start > s.indexOf(close)) return null;
        int cptr = 1, idx = start + 1;
        for (; idx < s.length() && cptr != 0; idx++) {
            String currentChar = s.charAt(idx) + "";
            cptr += currentChar.equals(open) ? 1 : currentChar.equals(close) ? -1 : 0;
        }
        return new Range(start, idx);
    }

}
