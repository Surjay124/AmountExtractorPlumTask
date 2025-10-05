package com.example.MedicalAmountExtractor.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RawTokenExtractor {
// extracts sequences of digits, digits with separators and percentages
    private static final Pattern TOKEN = Pattern.compile("(\\d[\\d,\\.]*%?)");


    public List<String> extractNumbersAndPercents(String text) {
    List<String> out = new ArrayList<>();
    Matcher m = TOKEN.matcher(text.replaceAll("[lI]", "1")); // small common OCR fixes
    while (m.find()) {
    String t = m.group(1).replaceAll(",", "");
    out.add(t);
    }
    return out;
}
}
