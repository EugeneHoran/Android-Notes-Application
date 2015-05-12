package com.eugene.androidnotes.Utilities;

public class Util {
    public static String capSentences(final String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
