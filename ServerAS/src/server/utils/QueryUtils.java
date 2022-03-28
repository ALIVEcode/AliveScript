package server.utils;

public class QueryUtils {
    public static String getValueOfQuery(String query, String queryParameter) {
        if (query == null) return null;
        queryParameter = queryParameter + "=";
        int queryIdx = query.indexOf(queryParameter);
        if (queryIdx == -1) return null;
        queryIdx = queryIdx + queryParameter.length();
        int endOfQuery = query.indexOf("&", queryIdx);
        return endOfQuery == -1 ? query.substring(queryIdx) : query.substring(queryIdx, endOfQuery);
    }

    public static String getValueOfQuery(String query, String queryParameter, String defaultValue) {
        var value = getValueOfQuery(query, queryParameter);
        return value == null ? defaultValue : value;
    }
}
