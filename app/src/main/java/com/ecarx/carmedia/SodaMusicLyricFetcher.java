package com.ecarx.carmedia;

import android.util.Log;
import com.ecarx.carmedia.LrcUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class SodaMusicLyricFetcher {
    private static final String ANDROID_UA = "com.luna.music/100198030 (Linux; U; Android 15; zh_CN_#Hans; ABR-AL80; Build/V417IR;tt-ok/3.12.13.19)";
    private static final String PC_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36";
    private static final String SEARCH_URL = "https://api.qishui.com/luna/search/track";
    private static final String SEO_URL = "https://beta-luna.douyin.com/luna/h5/seo_track";
    private static final String TAG = "CarMedia";
    private int mActiveSearches;
    private volatile String mApiMsg;
    private volatile int mApiState;
    private final Callback mCallback;
    private volatile String mCurrentLrc;
    private volatile String mCurrentSongArtist;
    private volatile String mCurrentSongTitle;
    private final Object mLock = new Object();
    private volatile List<LrcUtil.Line> mParsedLrc;
    private int mSearchSeq;
    private String mSearchingArtist;
    private String mSearchingTitle;
    private static final String[] CURL_BINS = {"/system/bin/curl", "/system/xbin/curl", "curl"};
    private static final String[] VERSION_BADGE = {"dj", "翻唱", "cover", "伴奏", "remix", "女声", "男声", "live", "铃声", "现场"};

    public interface Callback {
        void onError(String str);

        void onLyricReady(String str, String str2, String str3);
    }

    static /* synthetic */ int access$510(SodaMusicLyricFetcher x0) {
        int i = x0.mActiveSearches;
        x0.mActiveSearches = i - 1;
        return i;
    }

    public SodaMusicLyricFetcher(Callback callback) {
        this.mCallback = callback;
        try {
            System.setProperty("java.net.preferIPv4Stack", "true");
        } catch (Throwable th) {
        }
    }

    public boolean isReady() {
        return this.mParsedLrc != null;
    }

    public int getApiState() {
        return this.mApiState;
    }

    public String getApiMsg() {
        return this.mApiMsg;
    }

    public String getSongTitle() {
        return this.mCurrentSongTitle;
    }

    public String getSongArtist() {
        return this.mCurrentSongArtist;
    }

    public String getCurrentLyricLine(long positionMs) {
        List<LrcUtil.Line> lrc = this.mParsedLrc;
        if (lrc == null || lrc.isEmpty()) {
            return "";
        }
        String line = "";
        for (LrcUtil.Line l : lrc) {
            if (l.timeMs > positionMs) {
                break;
            }
            if (l.text != null && !l.text.trim().isEmpty()) {
                line = l.text;
            }
        }
        return line;
    }

    public String getNextLyricLine(long positionMs) {
        List<LrcUtil.Line> lrc = this.mParsedLrc;
        if (lrc == null || lrc.isEmpty()) {
            return "";
        }
        for (LrcUtil.Line l : lrc) {
            if (l.timeMs > positionMs && l.text != null && !l.text.trim().isEmpty()) {
                return l.text;
            }
        }
        return "";
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x001e, code lost:
    
        if (r6.equals(r4.mSearchingArtist) != false) goto L15;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void search(final String title, final String artist) {
        if (title == null || title.isEmpty()) {
            return;
        }
        this.mCurrentSongTitle = title;
        this.mCurrentSongArtist = artist;
        this.mCurrentLrc = null;
        this.mParsedLrc = null;
        this.mApiState = 1;
        this.mApiMsg = null;
        final int seq;
        synchronized (this.mLock) {
            this.mSearchSeq++;
            seq = this.mSearchSeq;
            this.mActiveSearches++;
            this.mSearchingTitle = title;
            this.mSearchingArtist = artist;
        }
        Log.d("CarMedia", "SodaLyric: search start #" + seq + " " + title);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String krc = null;
                try {
                    List<String> trackIds = SodaMusicLyricFetcher.this.searchTrack(title, artist);
                    if (trackIds != null && !trackIds.isEmpty()) {
                        for (String trackId : trackIds) {
                            krc = SodaMusicLyricFetcher.this.fetchSeoLyric(trackId);
                            if (krc != null && !krc.trim().isEmpty()) {
                                break;
                            }
                        }
                    }
                } catch (Throwable th) {
                    synchronized (SodaMusicLyricFetcher.this.mLock) {
                        SodaMusicLyricFetcher.this.mActiveSearches--;
                        if (SodaMusicLyricFetcher.this.mActiveSearches <= 0) {
                            SodaMusicLyricFetcher.this.mSearchingTitle = null;
                            SodaMusicLyricFetcher.this.mSearchingArtist = null;
                        }
                    }
                    return;
                }
                if (krc != null && !krc.trim().isEmpty()) {
                    SodaMusicLyricFetcher.this.applyKrc(seq, krc, title, artist);
                }
                synchronized (SodaMusicLyricFetcher.this.mLock) {
                    SodaMusicLyricFetcher.this.mActiveSearches--;
                    if (SodaMusicLyricFetcher.this.mActiveSearches <= 0) {
                        SodaMusicLyricFetcher.this.mSearchingTitle = null;
                        SodaMusicLyricFetcher.this.mSearchingArtist = null;
                    }
                }
            }
        }).start();
    }

    public void fetchByTrackId(final String trackId, final String title, final String artist) {
        final int seq;
        if (trackId == null || trackId.isEmpty()) {
            return;
        }
        this.mCurrentSongTitle = title;
        this.mCurrentSongArtist = artist;
        this.mCurrentLrc = null;
        this.mParsedLrc = null;
        this.mApiState = 1;
        this.mApiMsg = null;
        synchronized (this.mLock) {
            seq = this.mSearchSeq + 1;
            this.mSearchSeq = seq;
            this.mActiveSearches++;
            this.mSearchingTitle = title;
            this.mSearchingArtist = artist;
        }
        Log.i("CarMedia", "SodaLyric: debugFetch track_id=" + trackId);
        new Thread(new Runnable() { // from class: com.ecarx.carmedia.SodaMusicLyricFetcher.2
            @Override // java.lang.Runnable
            public void run() {
                String krc = null;
                try {
                    krc = SodaMusicLyricFetcher.this.fetchSeoLyric(trackId);
                } catch (Throwable t) {
                    try {
                        Log.w("CarMedia", "SodaLyric: error: " + t.getMessage());
                        SodaMusicLyricFetcher.this.fail(seq, t.getMessage());
                        synchronized (SodaMusicLyricFetcher.this.mLock) {
                            SodaMusicLyricFetcher.access$510(SodaMusicLyricFetcher.this);
                            if (SodaMusicLyricFetcher.this.mActiveSearches <= 0) {
                                SodaMusicLyricFetcher.this.mSearchingTitle = null;
                                SodaMusicLyricFetcher.this.mSearchingArtist = null;
                            }
                        }
                    } catch (Throwable th) {
                        synchronized (SodaMusicLyricFetcher.this.mLock) {
                            SodaMusicLyricFetcher.access$510(SodaMusicLyricFetcher.this);
                            if (SodaMusicLyricFetcher.this.mActiveSearches <= 0) {
                                SodaMusicLyricFetcher.this.mSearchingTitle = null;
                                SodaMusicLyricFetcher.this.mSearchingArtist = null;
                            }
                            throw th;
                        }
                    }
                }
                if (krc != null && !krc.trim().isEmpty()) {
                    SodaMusicLyricFetcher.this.applyKrc(seq, krc, title, artist);
                    synchronized (SodaMusicLyricFetcher.this.mLock) {
                        SodaMusicLyricFetcher.access$510(SodaMusicLyricFetcher.this);
                        if (SodaMusicLyricFetcher.this.mActiveSearches <= 0) {
                            SodaMusicLyricFetcher.this.mSearchingTitle = null;
                            SodaMusicLyricFetcher.this.mSearchingArtist = null;
                        }
                    }
                    return;
                }
                SodaMusicLyricFetcher.this.fail(seq, "no lyric content (track " + trackId + ")");
                synchronized (SodaMusicLyricFetcher.this.mLock) {
                    SodaMusicLyricFetcher.access$510(SodaMusicLyricFetcher.this);
                    if (SodaMusicLyricFetcher.this.mActiveSearches <= 0) {
                        SodaMusicLyricFetcher.this.mSearchingTitle = null;
                        SodaMusicLyricFetcher.this.mSearchingArtist = null;
                    }
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyKrc(int seq, String krc, String title, String artist) {
        String lrc = krcToLrc(krc);
        List<LrcUtil.Line> parsed = LrcUtil.parse(lrc);
        if (parsed == null || parsed.isEmpty()) {
            fail(seq, "empty parsed lrc");
            return;
        }
        if (seq == this.mSearchSeq) {
            this.mCurrentLrc = lrc;
            this.mParsedLrc = parsed;
            this.mApiState = 3;
            this.mApiMsg = null;
            Log.i("CarMedia", "SodaLyric: loaded " + parsed.size() + " lines for " + title);
            if (this.mCallback != null) {
                this.mCallback.onLyricReady(lrc, title != null ? title : "", artist != null ? artist : "");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String tryFetchLyric(List<String> trackIds) {
        for (String tid : trackIds) {
            try {
                String c = fetchSeoLyric(tid);
                if (c != null && !c.trim().isEmpty()) {
                    Log.i("CarMedia", "SodaLyric: lyric found via track_id=" + tid);
                    return c;
                }
            } catch (Throwable th) {
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fail(int seq, String msg) {
        if (seq == this.mSearchSeq) {
            this.mCurrentLrc = null;
            this.mParsedLrc = null;
            this.mApiState = 2;
            this.mApiMsg = msg;
        }
        if (this.mCallback != null && seq == this.mSearchSeq) {
            this.mCallback.onError(msg);
        }
    }

    private static String headOf(String s) {
        if (s == null) {
            return "(null)";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length() && sb.length() < 160; i++) {
            char c = s.charAt(i);
            if (c == '\n' || c == '\r') {
                sb.append(' ');
            } else if (c >= ' ') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String httpGetCurl(String urlStr, String ua) throws Exception {
        InputStream is;
        boolean can;
        String bin = null;
        String[] strArr = CURL_BINS;
        int length = strArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            String c = strArr[i];
            File f = new File(c);
            try {
                can = f.canExecute();
            } catch (Throwable th) {
                can = false;
            }
            boolean can2 = f.exists();
            if (!can2 || !can) {
                i++;
            } else {
                bin = c;
                break;
            }
        }
        if (bin == null) {
            Log.w("CarMedia", "SodaLyric: curl unavailable, fallback HttpURLConnection");
            return null;
        }
        String[] cmd = {bin, "-s", "--max-time", "12", "-A", ua, "--resolve", "api.qishui.com:443:112.95.9.93", "-w", "\n__remote_ip=%{remote_ip}", urlStr};
        Process p = new ProcessBuilder(cmd).redirectErrorStream(false).start();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        is = p.getInputStream();
        while (true) {
            int n = is.read(buf);
            if (n == -1) {
                break;
            }
            out.write(buf, 0, n);
        }
        InputStream es = p.getErrorStream();
        byte[] eb = new byte[4096];
        StringBuilder ev = new StringBuilder();
        while (true) {
            int en = es.read(eb);
            if (en == -1 || ev.length() >= 2048) {
                break;
            }
            ev.append(new String(eb, 0, en, "UTF-8"));
        }
        Log.i("CarMedia", "SodaLyric: curl verbose:\n" + ev.toString());
        boolean done = p.waitFor(13L, TimeUnit.SECONDS);
        if (!done) {
            Log.w("CarMedia", "SodaLyric: curl timeout");
            p.destroy();
            return null;
        }
        int ec = p.exitValue();
        byte[] raw = out.toByteArray();
        Log.i("CarMedia", "SodaLyric: curl exit=" + ec + " bin=" + bin + " bytes=" + raw.length);
        if (ec != 0) {
            return null;
        }
        String s = new String(raw, "UTF-8");
        int ri = s.lastIndexOf("\n__remote_ip=");
        if (ri >= 0) {
            Log.i("CarMedia", "SodaLyric: curl remote_ip=" + s.substring(ri + 13).trim());
            s = s.substring(0, ri);
        }
        Log.i("CarMedia", "SodaLyric: curl body=" + headOf(s));
        return s;
    }

    private static String httpGet(String urlStr, String ua) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        try {
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", ua);
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Connection", "close");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            int code = conn.getResponseCode();
            InputStream is = (code < 200 || code >= 300) ? conn.getErrorStream() : conn.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            if (is != null) {
                byte[] buf = new byte[4096];
                while (true) {
                    int n = is.read(buf);
                    if (n == -1) {
                        break;
                    }
                    bout.write(buf, 0, n);
                }
                is.close();
            }
            return new String(bout.toByteArray(), "UTF-8");
        } finally {
            conn.disconnect();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<String> searchTrack(String title, String artist) throws Exception {
        JSONObject json;
        String url;
        String body;
        JSONArray groups;
        JSONObject json2;
        String url2;
        String body2;
        JSONArray groups2;
        JSONObject g;
        String str = title;
        String str2 = artist;
        String kw = ((str == null ? "" : str) + " " + (str2 != null ? str2 : "")).trim();
        String url3 = "https://api.qishui.com/luna/search/track?q=" + URLEncoder.encode(kw, "UTF-8") + "&cursor=0&count=10&aid=386088&device_platform=android&app_name=luna&version_code=100198030";
        String body3 = null;
        JSONArray jSONArray = null;
        try {
            body3 = httpGetCurl(url3, ANDROID_UA);
            if (body3 == null) {
                try {
                    Log.w("CarMedia", "SodaLyric: curl unavailable, fallback HttpURLConnection");
                    body3 = httpGet(url3, ANDROID_UA);
                } catch (Throwable th) {
                    Log.w("CarMedia", "SodaLyric: searchTrack parse fail: " + th.getMessage() + " head=" + headOf(body3));
                    return null;
                }
            }
            try {
                Log.i("CarMedia", "SodaLyric: url=" + url3);
                Log.i("CarMedia", "SodaLyric: resp=" + headOf(body3));
                JSONObject json3 = new JSONObject(body3);
                JSONArray groups3 = json3.optJSONArray("result_groups");
                if (groups3 == null) {
                    return null;
                }
                List<long[]> scored = new ArrayList<>();
                int i = 0;
                while (i < groups3.length()) {
                    JSONObject g2 = groups3.optJSONObject(i);
                    JSONArray data = g2 != null ? g2.optJSONArray("data") : jSONArray;
                    if (data == null) {
                        json = json3;
                        url = url3;
                        body = body3;
                        groups = groups3;
                    } else {
                        int j = 0;
                        while (j < data.length()) {
                            JSONObject entity = data.optJSONObject(j).optJSONObject("entity");
                            JSONObject t = entity != null ? entity.optJSONObject("track") : null;
                            if (t == null) {
                                json2 = json3;
                                url2 = url3;
                                body2 = body3;
                                groups2 = groups3;
                                g = g2;
                            } else if (!t.has("id")) {
                                json2 = json3;
                                url2 = url3;
                                body2 = body3;
                                groups2 = groups3;
                                g = g2;
                            } else {
                                String.valueOf(t.optLong("id"));
                                json2 = json3;
                                url2 = url3;
                                String name = t.optString("name");
                                body2 = body3;
                                groups2 = groups3;
                                String[] arts = new String[0];
                                JSONArray ar = t.optJSONArray("artists");
                                if (ar != null) {
                                    arts = new String[ar.length()];
                                    int k = 0;
                                    while (true) {
                                        g = g2;
                                        if (k >= ar.length()) {
                                            break;
                                        }
                                        arts[k] = ar.optJSONObject(k).optString("name");
                                        k++;
                                        g2 = g;
                                    }
                                } else {
                                    g = g2;
                                }
                                int sc = scoreTrack(name, arts, str, str2);
                                scored.add(new long[]{sc, t.optLong("id")});
                            }
                            j++;
                            str = title;
                            str2 = artist;
                            json3 = json2;
                            url3 = url2;
                            body3 = body2;
                            groups3 = groups2;
                            g2 = g;
                        }
                        json = json3;
                        url = url3;
                        body = body3;
                        groups = groups3;
                    }
                    i++;
                    str = title;
                    str2 = artist;
                    json3 = json;
                    url3 = url;
                    body3 = body;
                    groups3 = groups;
                    jSONArray = null;
                }
                scored.sort(new Comparator() { // from class: com.ecarx.carmedia.SodaMusicLyricFetcher$$ExternalSyntheticLambda0
                    @Override // java.util.Comparator
                    public final int compare(Object obj, Object obj2) {
                        int compare;
                        compare = Long.compare(((long[]) obj2)[0], ((long[]) obj)[0]);
                        return compare;
                    }
                });
                List<String> ids = new ArrayList<>();
                for (int n = 0; n < scored.size() && n < 3; n++) {
                    ids.add(String.valueOf(scored.get(n)[1]));
                }
                Log.d("CarMedia", "SodaLyric: search '" + kw + "' top=" + ids);
                if (ids.isEmpty()) {
                    return null;
                }
                return ids;
            } catch (Throwable th2) {
                Log.w("CarMedia", "SodaLyric: searchTrack parse fail: " + th2.getMessage() + " head=" + headOf(body3));
                return null;
            }
        } catch (Throwable th3) {
            Log.w("CarMedia", "SodaLyric: searchTrack error: " + th3.getMessage());
            return null;
        }
    }

    private static int scoreTrack(String name, String[] artists, String title, String artist) {
        int s;
        String normalize = TextUtil.normalize(name);
        String normalize2 = TextUtil.normalize(title);
        if (normalize.equals(normalize2)) {
            s = 0 + 100;
        } else if (!normalize.contains(normalize2) && !normalize2.contains(normalize)) {
            s = 0 + (commonChars(normalize, normalize2) * 2);
        } else {
            s = 0 + 60;
        }
        if (artist != null && !artist.isEmpty()) {
            String normalize3 = TextUtil.normalize(artist);
            boolean hit = false;
            boolean sub = false;
            for (String a : artists) {
                String normalize4 = TextUtil.normalize(a);
                if (normalize4.equals(normalize3)) {
                    hit = true;
                } else if (normalize4.contains(normalize3) || normalize3.contains(normalize4)) {
                    sub = true;
                }
            }
            if (hit) {
                s += 100;
            } else if (sub) {
                s += 50;
            } else {
                s -= 40;
            }
        }
        String low = name.toLowerCase();
        for (String b : VERSION_BADGE) {
            if (low.contains(b)) {
                s -= 25;
            }
        }
        return s;
    }

    private static int commonChars(String a, String b) {
        if (a.isEmpty() || b.isEmpty()) {
            return 0;
        }
        boolean[] seen = new boolean[65536];
        for (int i = 0; i < a.length(); i++) {
            seen[a.charAt(i)] = true;
        }
        int c = 0;
        for (int i2 = 0; i2 < b.length(); i2++) {
            if (seen[b.charAt(i2)]) {
                c++;
            }
        }
        return c;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String fetchSeoLyric(String trackId) throws Exception {
        JSONObject l2;
        String url = "https://beta-luna.douyin.com/luna/h5/seo_track?track_id=" + trackId + "&device_platform=web";
        String body = null;
        JSONObject lyric = null;
        try {
            body = httpGet(url, PC_UA);
            JSONObject json = new JSONObject(body);
            JSONObject st = json.optJSONObject("seo_track");
            if (st != null) {
                lyric = st.optJSONObject("lyric");
            }
            String content = lyric != null ? lyric.optString("content") : "";
            if (content.isEmpty() && (l2 = json.optJSONObject("lyric")) != null) {
                return l2.optString("content");
            }
            return content;
        } catch (Throwable t) {
            Log.w("CarMedia", "SodaLyric: fetchSeoLyric parse fail: " + t.getMessage() + " head=" + headOf(body));
            return null;
        }
    }

    static String krcToLrc(String krc) {
        if (krc == null) {
            return "";
        }
        Pattern lineRe = Pattern.compile("^\\[(\\d+),(\\d+)\\](.*)$");
        Pattern wordRe = Pattern.compile("<[^>]+>");
        Pattern tagRe = Pattern.compile("\\[[^\\]]*\\]");
        StringBuilder sb = new StringBuilder();
        for (String line : krc.split("\n")) {
            String s = line.trim();
            if (!s.isEmpty()) {
                Matcher m = lineRe.matcher(s);
                if (!m.matches()) {
                    String text = tagRe.matcher(wordRe.matcher(s).replaceAll("")).replaceAll("").trim();
                    if (!text.isEmpty()) {
                        appendLrc(sb, 0L, text);
                    }
                } else {
                    long startMs = Long.parseLong(m.group(1));
                    String text2 = wordRe.matcher(m.group(3)).replaceAll("").trim();
                    if (!text2.isEmpty()) {
                        appendLrc(sb, startMs, text2);
                    }
                }
            }
        }
        return sb.toString();
    }

    private static void appendLrc(StringBuilder sb, long startMs, String text) {
        long mm = startMs / 60000;
        long ss = (startMs % 60000) / 1000;
        long cs = (startMs % 1000) / 10;
        sb.append(String.format(Locale.US, "[%02d:%02d.%02d]%s%n", Long.valueOf(mm), Long.valueOf(ss), Long.valueOf(cs), text));
    }
}
