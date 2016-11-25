package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by XX on 2016/5/6.
 */
public class FilterHtml {
    /**
     * 基本功能：过滤所有以"<"开头以">"结尾的标签
     *
     * @param str
     * @return String
     */
    public static String filterHtml(String str) {
        final String regxpForHtml = "<([^>]*)>"; // 过滤所有以<开头以>结尾的标签
        Pattern pattern = Pattern.compile(regxpForHtml);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {//将HTML标签替换为空
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
//        Toast.makeText(SearchActivity.this,sb.toString().trim(),0).show();
        return sb.toString().trim();
    }
}
