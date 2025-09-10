package com.erling.utils.pattern;

import java.util.List;
import java.util.regex.Pattern;

public class PatternUtils {
    public static boolean isKeyWord(String str,String keyWord) {
        Pattern keywordPattern = Pattern.compile("\\b" + keyWord + "\\b", Pattern.CASE_INSENSITIVE);
        return keywordPattern.matcher(str).find();
    }
    public static boolean isListKeyWordOR(String str, List<String> listKeyWord) {
        for (String keyWord : listKeyWord) {
            if (isKeyWord(str, keyWord)) { //满足其中一个关键词就返回true
                return true;
            }
        }
        return false;
    }
    public static boolean isListKeyWordAND(String str, List<String> listKeyWord) {
        for (String keyWord : listKeyWord) {
            if (!isKeyWord(str, keyWord)) {
                return false;           //只要有一个关键词不满足就返回false
            }
        }
        return true;
    }
}
