package com.ecarx.carmedia;

/* loaded from: classes3.dex */
public final class TextUtil {
    private TextUtil() {
    }

    public static String normalize(String s) {
        String prev;
        if (s == null) {
            return "";
        }
        String t = s.replace((char) 65288, '(').replace((char) 65289, ')').replace((char) 12304, '[').replace((char) 12305, ']').replace((char) 12308, '[').replace((char) 12309, ']');
        do {
            prev = t;
            t = prev.replaceAll("\\([^()]*\\)", "").replaceAll("\\[[^\\[\\]]*\\]", "");
        } while (!t.equals(prev));
        return t.replaceAll("\\s+", "").replace("·", "").replace("•", "").replace("⋅", "").replace("：", ":").replace(":", "").replace("～", "~").replace("~", "").replace("－", "-").replace("—", "-").replace("–", "-").replace("-", "").toLowerCase();
    }
}
