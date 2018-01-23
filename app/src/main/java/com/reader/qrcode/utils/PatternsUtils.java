package com.reader.qrcode.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternsUtils {

    public static boolean isMatching(PatternExp patternexp, String input) {
        Matcher matcher = Pattern.compile(patternexp.getPattern(), Pattern.CASE_INSENSITIVE).matcher(input);
        return matcher.find();
    }

    public enum PatternExp {
        Phone("^[0-9]{10}$");

        private String pattern;

        PatternExp(String pattern) {
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }
    }
}
