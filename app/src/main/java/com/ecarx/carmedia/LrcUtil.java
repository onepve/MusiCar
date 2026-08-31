package com.ecarx.carmedia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes3.dex */
public final class LrcUtil {
    private LrcUtil() {
    }

    public static class Line implements Comparable<Line> {
        public String text;
        public long timeMs;

        public Line(long timeMs, String text) {
            this.timeMs = timeMs;
            this.text = text;
        }

        @Override // java.lang.Comparable
        public int compareTo(Line o) {
            return Long.compare(this.timeMs, o.timeMs);
        }
    }

    public static List<Line> parse(String lrc) {
        int end;
        long t = 0;
        String text = "";
        List<Line> lines = new ArrayList<>();
        if (lrc == null || lrc.isEmpty()) {
            return lines;
        }
        String[] rows = lrc.split("\n");
        for (String row : rows) {
            String s = row.trim();
            if (!s.isEmpty()) {
                int idx = 0;
                while (idx < s.length() && s.charAt(idx) == '[' && (end = s.indexOf(93, idx)) >= 0) {
                    String tag = s.substring(idx + 1, end);
                    try {
                        t = parseTime(tag);
                        text = s.substring(end + 1).trim();
                    } catch (Exception e) {
                    }
                    if (text.trim().isEmpty()) {
                        idx = end + 1;
                    } else {
                        lines.add(new Line(t, text));
                        idx = end + 1;
                    }
                }
            }
        }
        Collections.sort(lines);
        return lines;
    }

    public static String getCurrentLine(List<Line> lines, long positionMs) {
        if (lines == null || lines.isEmpty()) {
            return "";
        }
        String line = "";
        for (Line l : lines) {
            if (l.timeMs > positionMs) {
                break;
            }
            if (l.text != null && !l.text.trim().isEmpty()) {
                line = l.text;
            }
        }
        return line;
    }

    public static String getNextLine(List<Line> lines, long positionMs) {
        if (lines == null || lines.isEmpty()) {
            return "";
        }
        for (Line l : lines) {
            if (l.timeMs > positionMs && l.text != null && !l.text.trim().isEmpty()) {
                return l.text;
            }
        }
        return "";
    }

    private static long parseTime(String s) {
        int sec;
        int colon = s.indexOf(58);
        if (colon < 0) {
            throw new IllegalArgumentException("not a time tag");
        }
        String mm = s.substring(0, colon);
        String rest = s.substring(colon + 1);
        int dot = rest.indexOf(46);
        int ms = 0;
        if (dot >= 0) {
            sec = Integer.parseInt(rest.substring(0, dot));
            String msStr = rest.substring(dot + 1);
            while (msStr.length() < 3) {
                msStr = msStr + "0";
            }
            ms = Integer.parseInt(msStr.substring(0, Math.min(3, msStr.length())));
        } else {
            sec = Integer.parseInt(rest);
        }
        return (Long.parseLong(mm) * 60000) + (sec * 1000) + ms;
    }
}
