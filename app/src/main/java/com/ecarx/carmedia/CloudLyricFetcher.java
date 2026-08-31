package com.ecarx.carmedia;

import android.util.Log;
import com.ecarx.carmedia.CloudLyricFetcher;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class CloudLyricFetcher {
    private static final long ACTIVE_HOLD_MS = 3000;
    public static final int API_ACTIVE = 1;
    public static final int API_ERROR = 2;
    public static final int API_IDLE = 0;
    public static final String API_NETEASE_LYRIC = "netease_lyric";
    public static final String API_NETEASE_SEARCH = "netease_search";
    public static final int API_SUCCESS = 3;
    public static final String API_XMF2 = "xmf2";
    private static final String TAG = "CarMedia";
    private static final String[] XMF2_TYPES;
    private int mActiveSearches;
    private String mBroadcastArtist;
    private String mBroadcastLrc;
    private volatile boolean mBroadcastLrcActive;
    private final Callback mCallback;
    private String mCurrentLrc;
    private String mCurrentSongArtist;
    private String mCurrentSongTitle;
    private boolean mFallbackRunning;
    private List<LrcLine> mParsedLrc;
    private int mSearchSeq;
    private String mSearchingArtist;
    private String mSearchingTitle;
    private volatile String mXmf2ActiveType;
    private final Map<String, int[]> mApiStatus = new ConcurrentHashMap();
    private final Map<String, String> mApiMsg = new ConcurrentHashMap();
    private final Map<String, Long> mApiChangedAt = new ConcurrentHashMap();
    private final Map<String, Long> mApiActiveAt = new ConcurrentHashMap();
    private final Object mLock = new Object();

    public interface Callback {
        void onError(String str);

        void onLyricReady(String str, String str2, String str3);
    }

    static /* synthetic */ int access$910(CloudLyricFetcher x0) {
        int i = x0.mActiveSearches;
        x0.mActiveSearches = i - 1;
        return i;
    }

    static {
        try {
            System.setProperty("java.net.preferIPv4Stack", "true");
        } catch (Throwable th) {
        }
        XMF2_TYPES = new String[]{"qq", "kugou", "kuwo", "wy"};
    }

    private void setApiStatus(String api, int status, String msg) {
        this.mApiStatus.put(api, new int[]{status, (int) (System.currentTimeMillis() / 1000)});
        this.mApiChangedAt.put(api, Long.valueOf(System.currentTimeMillis()));
        if (status == 1) {
            this.mApiActiveAt.put(api, Long.valueOf(System.currentTimeMillis()));
        }
        if (msg != null) {
            this.mApiMsg.put(api, msg);
        }
        if (status == 0) {
            this.mApiMsg.remove(api);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setApiStatusIfCurrent(int seq, String api, int status, String msg) {
        if (seq != this.mSearchSeq) {
            return;
        }
        setApiStatus(api, status, msg);
    }

    public void resetApiStatus() {
        this.mXmf2ActiveType = null;
        String[] strArr = {API_NETEASE_SEARCH, API_NETEASE_LYRIC, API_XMF2};
        for (int i = 0; i < 3; i++) {
            String api = strArr[i];
            this.mApiStatus.put(api, new int[]{0, (int) (System.currentTimeMillis() / 1000)});
            this.mApiMsg.remove(api);
            this.mApiChangedAt.remove(api);
            this.mApiActiveAt.remove(api);
        }
    }

    public JSONObject getApiStatus() {
        Long activeAt;
        JSONObject o = new JSONObject();
        try {
            long now = System.currentTimeMillis();
            String[] strArr = {API_NETEASE_SEARCH, API_NETEASE_LYRIC, API_XMF2};
            for (int i = 0; i < 3; i++) {
                String api = strArr[i];
                JSONObject s = new JSONObject();
                int[] v = this.mApiStatus.get(api);
                int st = v != null ? v[0] : 0;
                if (st != 1 && ((st == 0 || st == 3) && (activeAt = this.mApiActiveAt.get(api)) != null && now - activeAt.longValue() < ACTIVE_HOLD_MS)) {
                    st = 1;
                }
                s.put("status", st);
                s.put("msg", this.mApiMsg.get(api) != null ? this.mApiMsg.get(api) : "");
                o.put(api, s);
            }
        } catch (Throwable th) {
        }
        return o;
    }

    public static class LrcLine {
        public String text;
        public long timeMs;

        public LrcLine(long timeMs, String text) {
            this.timeMs = timeMs;
            this.text = text;
        }
    }

    private static boolean titleMatches(String want, String got) {
        if (want == null || want.isEmpty() || got == null || got.isEmpty()) {
            return false;
        }
        String normalize = TextUtil.normalize(want);
        String normalize2 = TextUtil.normalize(got);
        if (normalize.equals(normalize2)) {
            return true;
        }
        return normalize.contains(normalize2) || normalize2.contains(normalize);
    }

    private static boolean artistMatches(String want, String got) {
        if (want == null || want.isEmpty()) {
            return true;
        }
        if (got == null || got.isEmpty()) {
            return false;
        }
        String w = TextUtil.normalize(want);
        String normalize = TextUtil.normalize(got);
        for (String str : w.split("/")) {
            if (!str.isEmpty() && (normalize.contains(str) || str.contains(normalize))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isRealLrc(String lrc) {
        if (lrc == null || lrc.isEmpty()) {
            return false;
        }
        String[] rows = lrc.split("\n");
        int stampRows = 0;
        int textRows = 0;
        for (String row : rows) {
            String s = row.trim();
            if (s.startsWith("[") && s.indexOf(93) > 0) {
                stampRows++;
                String v = s.substring(s.indexOf(93) + 1).trim();
                if (!v.isEmpty()) {
                    textRows++;
                }
            }
        }
        return stampRows >= 2 && textRows >= 1;
    }

    private static String[] extractLrcInfo(String lrc) {
        if (lrc == null || lrc.isEmpty()) {
            return null;
        }
        String[] rows = lrc.split("\n");
        for (String row : rows) {
            String s = row.trim();
            if (!s.isEmpty() && s.startsWith("[") && s.indexOf(93) > 0) {
                String v = s.substring(s.indexOf(93) + 1).trim();
                if (!v.isEmpty() && !v.startsWith("浣滆瘝") && !v.startsWith("浣滄洸") && !v.startsWith("缂栨洸") && !v.startsWith("鍘熷敱") && !v.startsWith("鍒朵綔") && !v.startsWith("褰曢煶") && !v.startsWith("娣烽煶") && !v.startsWith("鍜屽０") && !v.startsWith("鍑哄搧") && !v.startsWith("OP") && !v.startsWith("SP") && !v.startsWith("缁熺\ue132") && !v.startsWith("浼佸垝") && !v.startsWith("鐩戝埗") && !v.startsWith("鍙戣\ue511")) {
                    int sep1 = v.indexOf(" - ");
                    int sep2 = v.indexOf(" -");
                    if (sep1 <= 0 && sep2 <= 0) {
                        return null;
                    }
                    int sep = sep1 > 0 ? sep1 : sep2;
                    String a = v.substring(0, sep).trim();
                    String b = v.substring((sep1 > 0 ? 3 : 2) + sep).trim();
                    if (a.isEmpty() || b.isEmpty()) {
                        return null;
                    }
                    return new String[]{a, b};
                }
            }
        }
        return null;
    }

    private static boolean lrcMatches(String lrc, String wantTitle, String wantArtist) {
        if (!isRealLrc(lrc)) {
            return false;
        }
        String[] info = extractLrcInfo(lrc);
        if (info == null) {
            return true;
        }
        boolean t1 = titleMatches(wantTitle, info[0]);
        boolean t2 = titleMatches(wantTitle, info[1]);
        return t1 || t2 || artistMatches(wantArtist, info[0]) || artistMatches(wantArtist, info[1]);
    }

    private static String buildSearchKey(String wantTitle, String wantArtist) {
        String t = wantTitle != null ? wantTitle.trim() : "";
        String a = wantArtist != null ? wantArtist.trim() : "";
        String t2 = stripBrackets(t);
        if (!t2.isEmpty() && !a.isEmpty()) {
            return t2 + "-" + a;
        }
        return t2;
    }

    private static String stripBrackets(String s) {
        String prev;
        if (s == null || s.isEmpty()) {
            return s;
        }
        String cur = s;
        do {
            prev = cur;
            cur = prev.replaceAll("\\([^()]*\\)", "").replaceAll("\\uFF08[^\\uFF08\\uFF09]*\\uFF09", "");
        } while (!cur.equals(prev));
        return cur.trim();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String fetchXmf2WithVerify(String wantTitle, String wantArtist) {
        for (String type : XMF2_TYPES) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("http://api.xmf2.cn/api/music/lyric.php?type=").append(type).append("&lrc=").append(URLEncoder.encode(buildSearchKey(wantTitle, wantArtist), "UTF-8"));
                String body = httpGetV4(sb.toString(), "Mozilla/5.0", null, 10000);
                if (body != null && !body.trim().isEmpty()) {
                    if (!lrcMatches(body, wantTitle, wantArtist)) {
                        Log.d("CarMedia", "CloudLyric: xmf2 type=" + type + " mismatch (" + wantTitle + "), try next");
                    } else {
                        this.mXmf2ActiveType = type;
                        return body;
                    }
                }
            } catch (Throwable t) {
                Log.d("CarMedia", "CloudLyric: xmf2 type=" + type + " error: " + t.getMessage());
            }
        }
        return null;
    }

    public CloudLyricFetcher(Callback callback) {
        this.mCallback = callback;
        try {
            System.setProperty("java.net.preferIPv4Stack", "true");
        } catch (Throwable th) {
        }
    }

    public boolean isReady() {
        return this.mParsedLrc != null;
    }

    public String getSongTitle() {
        return this.mCurrentSongTitle;
    }

    public String getSongArtist() {
        return this.mCurrentSongArtist;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean fetchFromXmf2(final String title, final String artist, final int seq) {
        synchronized (this.mLock) {
            if (this.mFallbackRunning) {
                Log.d("CarMedia", "CloudLyric: xmf2 already running, skip duplicate");
                return false;
            }
            this.mFallbackRunning = true;
            new Thread(new Runnable() { // from class: com.ecarx.carmedia.CloudLyricFetcher.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        CloudLyricFetcher.this.setApiStatusIfCurrent(seq, CloudLyricFetcher.API_XMF2, 1, null);
                        String body = CloudLyricFetcher.this.fetchXmf2WithVerify(title != null ? title : "", artist != null ? artist : "");
                        if (body != null) {
                            CloudLyricFetcher.this.setApiStatusIfCurrent(seq, CloudLyricFetcher.API_XMF2, 3, CloudLyricFetcher.this.mXmf2ActiveType);
                            if (seq == CloudLyricFetcher.this.mSearchSeq) {
                                CloudLyricFetcher.this.mCurrentLrc = body;
                                CloudLyricFetcher.this.mParsedLrc = CloudLyricFetcher.parseLrc(body);
                            }
                            if (CloudLyricFetcher.this.mCallback != null && seq == CloudLyricFetcher.this.mSearchSeq) {
                                CloudLyricFetcher.this.mCallback.onLyricReady(body, title != null ? title : "", artist != null ? artist : "");
                            }
                            synchronized (CloudLyricFetcher.this.mLock) {
                                CloudLyricFetcher.this.mFallbackRunning = false;
                                CloudLyricFetcher.access$910(CloudLyricFetcher.this);
                                if (CloudLyricFetcher.this.mActiveSearches <= 0) {
                                    CloudLyricFetcher.this.mSearchingTitle = null;
                                    CloudLyricFetcher.this.mSearchingArtist = null;
                                }
                            }
                            return;
                        }
                        CloudLyricFetcher.this.mXmf2ActiveType = null;
                        CloudLyricFetcher.this.setApiStatusIfCurrent(seq, CloudLyricFetcher.API_XMF2, 2, "no matching lyric");
                        if (seq == CloudLyricFetcher.this.mSearchSeq) {
                            CloudLyricFetcher.this.mCurrentLrc = null;
                            CloudLyricFetcher.this.mParsedLrc = null;
                        }
                        if (CloudLyricFetcher.this.mCallback != null && seq == CloudLyricFetcher.this.mSearchSeq) {
                            CloudLyricFetcher.this.mCallback.onError("xmf2: no matching lyric");
                        }
                        synchronized (CloudLyricFetcher.this.mLock) {
                            CloudLyricFetcher.this.mFallbackRunning = false;
                            CloudLyricFetcher.access$910(CloudLyricFetcher.this);
                            if (CloudLyricFetcher.this.mActiveSearches <= 0) {
                                CloudLyricFetcher.this.mSearchingTitle = null;
                                CloudLyricFetcher.this.mSearchingArtist = null;
                            }
                        }
                    } catch (Throwable t) {
                        try {
                            Log.w("CarMedia", "CloudLyric: xmf2 error: " + t.getMessage());
                            CloudLyricFetcher.this.setApiStatusIfCurrent(seq, CloudLyricFetcher.API_XMF2, 2, t.getMessage());
                            if (seq == CloudLyricFetcher.this.mSearchSeq) {
                                CloudLyricFetcher.this.mCurrentLrc = null;
                                CloudLyricFetcher.this.mParsedLrc = null;
                            }
                            if (CloudLyricFetcher.this.mCallback != null && seq == CloudLyricFetcher.this.mSearchSeq) {
                                CloudLyricFetcher.this.mCallback.onError("xmf2: " + t.getMessage());
                            }
                            synchronized (CloudLyricFetcher.this.mLock) {
                                CloudLyricFetcher.this.mFallbackRunning = false;
                                CloudLyricFetcher.access$910(CloudLyricFetcher.this);
                                if (CloudLyricFetcher.this.mActiveSearches <= 0) {
                                    CloudLyricFetcher.this.mSearchingTitle = null;
                                    CloudLyricFetcher.this.mSearchingArtist = null;
                                }
                            }
                        } catch (Throwable th) {
                            synchronized (CloudLyricFetcher.this.mLock) {
                                CloudLyricFetcher.this.mFallbackRunning = false;
                                CloudLyricFetcher.access$910(CloudLyricFetcher.this);
                                if (CloudLyricFetcher.this.mActiveSearches <= 0) {
                                    CloudLyricFetcher.this.mSearchingTitle = null;
                                    CloudLyricFetcher.this.mSearchingArtist = null;
                                }
                                throw th;
                            }
                        }
                    }
                }
            }).start();
            return true;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x0045, code lost:
    
        if (r6.equals(r4.mSearchingArtist) != false) goto L21;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void searchXmf2Only(final java.lang.String r5, final java.lang.String r6) {
        /*
            r4 = this;
            boolean r0 = r4.mBroadcastLrcActive
            if (r0 == 0) goto L27
            java.lang.String r0 = r4.mBroadcastLrc
            if (r0 == 0) goto L27
            java.lang.String r0 = "CarMedia"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "CloudLyric: broadcast lrc active, skip xmf2 search ("
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.StringBuilder r1 = r1.append(r5)
            java.lang.String r2 = ")"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r0, r1)
            return
        L27:
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            int r1 = r4.mActiveSearches     // Catch: java.lang.Throwable -> Lb4
            if (r1 <= 0) goto L63
            if (r5 == 0) goto L63
            java.lang.String r1 = r4.mSearchingTitle     // Catch: java.lang.Throwable -> Lb4
            boolean r1 = r5.equals(r1)     // Catch: java.lang.Throwable -> Lb4
            if (r1 == 0) goto L63
            if (r6 != 0) goto L3f
            java.lang.String r1 = r4.mSearchingArtist     // Catch: java.lang.Throwable -> Lb4
            if (r1 != 0) goto L63
            goto L47
        L3f:
            java.lang.String r1 = r4.mSearchingArtist     // Catch: java.lang.Throwable -> Lb4
            boolean r1 = r6.equals(r1)     // Catch: java.lang.Throwable -> Lb4
            if (r1 == 0) goto L63
        L47:
            java.lang.String r1 = "CarMedia"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lb4
            r2.<init>()     // Catch: java.lang.Throwable -> Lb4
            java.lang.String r3 = "CloudLyric: same-title search in progress, reuse #"
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> Lb4
            int r3 = r4.mSearchSeq     // Catch: java.lang.Throwable -> Lb4
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> Lb4
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> Lb4
            android.util.Log.d(r1, r2)     // Catch: java.lang.Throwable -> Lb4
            monitor-exit(r0)     // Catch: java.lang.Throwable -> Lb4
            return
        L63:
            r4.mSearchingTitle = r5     // Catch: java.lang.Throwable -> Lb4
            r4.mSearchingArtist = r6     // Catch: java.lang.Throwable -> Lb4
            monitor-exit(r0)     // Catch: java.lang.Throwable -> Lb4
            r4.mCurrentSongTitle = r5
            r4.mCurrentSongArtist = r6
            r0 = 0
            r4.mCurrentLrc = r0
            r4.mParsedLrc = r0
            java.lang.Object r1 = r4.mLock
            monitor-enter(r1)
            int r0 = r4.mSearchSeq     // Catch: java.lang.Throwable -> Lb1
            int r0 = r0 + 1
            r4.mSearchSeq = r0     // Catch: java.lang.Throwable -> Lb1
            int r2 = r4.mActiveSearches     // Catch: java.lang.Throwable -> Lb1
            int r2 = r2 + 1
            r4.mActiveSearches = r2     // Catch: java.lang.Throwable -> Lb1
            monitor-exit(r1)     // Catch: java.lang.Throwable -> Lb1
            java.lang.String r1 = "CarMedia"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "CloudLyric: xmf2 search start #"
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.StringBuilder r2 = r2.append(r0)
            java.lang.String r3 = " "
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.StringBuilder r2 = r2.append(r5)
            java.lang.String r2 = r2.toString()
            android.util.Log.d(r1, r2)
            java.lang.Thread r1 = new java.lang.Thread
            com.ecarx.carmedia.CloudLyricFetcher$2 r2 = new com.ecarx.carmedia.CloudLyricFetcher$2
            r2.<init>()
            r1.<init>(r2)
            r1.start()
            return
        Lb1:
            r0 = move-exception
            monitor-exit(r1)     // Catch: java.lang.Throwable -> Lb1
            throw r0
        Lb4:
            r1 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> Lb4
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ecarx.carmedia.CloudLyricFetcher.searchXmf2Only(java.lang.String, java.lang.String):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x0045, code lost:
    
        if (r6.equals(r4.mSearchingArtist) != false) goto L21;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void search(final java.lang.String r5, final java.lang.String r6) {
        /*
            r4 = this;
            boolean r0 = r4.mBroadcastLrcActive
            if (r0 == 0) goto L27
            java.lang.String r0 = r4.mBroadcastLrc
            if (r0 == 0) goto L27
            java.lang.String r0 = "CarMedia"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "CloudLyric: broadcast lrc active, skip network search ("
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.StringBuilder r1 = r1.append(r5)
            java.lang.String r2 = ")"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r0, r1)
            return
        L27:
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            int r1 = r4.mActiveSearches     // Catch: java.lang.Throwable -> Ld1
            if (r1 <= 0) goto L73
            if (r5 == 0) goto L73
            java.lang.String r1 = r4.mSearchingTitle     // Catch: java.lang.Throwable -> Ld1
            boolean r1 = r5.equals(r1)     // Catch: java.lang.Throwable -> Ld1
            if (r1 == 0) goto L73
            if (r6 != 0) goto L3f
            java.lang.String r1 = r4.mSearchingArtist     // Catch: java.lang.Throwable -> Ld1
            if (r1 != 0) goto L73
            goto L47
        L3f:
            java.lang.String r1 = r4.mSearchingArtist     // Catch: java.lang.Throwable -> Ld1
            boolean r1 = r6.equals(r1)     // Catch: java.lang.Throwable -> Ld1
            if (r1 == 0) goto L73
        L47:
            java.lang.String r1 = "CarMedia"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Ld1
            r2.<init>()     // Catch: java.lang.Throwable -> Ld1
            java.lang.String r3 = "CloudLyric: same-title search in progress, reuse #"
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> Ld1
            int r3 = r4.mSearchSeq     // Catch: java.lang.Throwable -> Ld1
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> Ld1
            java.lang.String r3 = " ("
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> Ld1
            java.lang.StringBuilder r2 = r2.append(r5)     // Catch: java.lang.Throwable -> Ld1
            java.lang.String r3 = ")"
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch: java.lang.Throwable -> Ld1
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> Ld1
            android.util.Log.d(r1, r2)     // Catch: java.lang.Throwable -> Ld1
            monitor-exit(r0)     // Catch: java.lang.Throwable -> Ld1
            return
        L73:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> Ld1
            r4.mCurrentSongTitle = r5
            r4.mCurrentSongArtist = r6
            r0 = 0
            r4.mCurrentLrc = r0
            r4.mParsedLrc = r0
            java.lang.Object r1 = r4.mLock
            monitor-enter(r1)
            int r0 = r4.mSearchSeq     // Catch: java.lang.Throwable -> Lce
            int r0 = r0 + 1
            r4.mSearchSeq = r0     // Catch: java.lang.Throwable -> Lce
            int r2 = r4.mActiveSearches     // Catch: java.lang.Throwable -> Lce
            int r2 = r2 + 1
            r4.mActiveSearches = r2     // Catch: java.lang.Throwable -> Lce
            r4.mSearchingTitle = r5     // Catch: java.lang.Throwable -> Lce
            r4.mSearchingArtist = r6     // Catch: java.lang.Throwable -> Lce
            monitor-exit(r1)     // Catch: java.lang.Throwable -> Lce
            java.lang.String r1 = "CarMedia"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "CloudLyric: search start #"
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.StringBuilder r2 = r2.append(r0)
            java.lang.String r3 = " "
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.StringBuilder r2 = r2.append(r5)
            java.lang.String r2 = r2.toString()
            android.util.Log.d(r1, r2)
            java.lang.Thread r1 = new java.lang.Thread
            com.ecarx.carmedia.CloudLyricFetcher$3 r2 = new com.ecarx.carmedia.CloudLyricFetcher$3
            r2.<init>()
            r1.<init>(r2)
            r1.start()
            java.lang.Thread r2 = new java.lang.Thread
            com.ecarx.carmedia.CloudLyricFetcher$4 r3 = new com.ecarx.carmedia.CloudLyricFetcher$4
            r3.<init>()
            r2.<init>(r3)
            r2.start()
            return
        Lce:
            r0 = move-exception
            monitor-exit(r1)     // Catch: java.lang.Throwable -> Lce
            throw r0
        Ld1:
            r1 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> Ld1
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ecarx.carmedia.CloudLyricFetcher.search(java.lang.String, java.lang.String):void");
    }

    public void setBroadcastLrc(String lrc, String title, String artist) {
        if (lrc == null || lrc.isEmpty()) {
            return;
        }
        synchronized (this.mLock) {
            this.mBroadcastLrc = lrc;
            this.mBroadcastArtist = artist;
            this.mBroadcastLrcActive = true;
            this.mParsedLrc = parseLrc(lrc);
            this.mCurrentLrc = lrc;
            this.mCurrentSongTitle = title;
            this.mCurrentSongArtist = artist;
        }
    }

    public void clearBroadcastLrc() {
        synchronized (this.mLock) {
            this.mBroadcastLrcActive = false;
            this.mBroadcastLrc = null;
            this.mBroadcastArtist = null;
        }
    }

    public void clearLoadedLrc() {
        synchronized (this.mLock) {
            this.mParsedLrc = null;
            this.mCurrentLrc = null;
            this.mCurrentSongTitle = null;
            this.mCurrentSongArtist = null;
            this.mBroadcastLrcActive = false;
            this.mBroadcastLrc = null;
            this.mBroadcastArtist = null;
            this.mSearchSeq++;
            this.mSearchingTitle = null;
            this.mSearchingArtist = null;
        }
    }

    public String getCurrentLyricLine(long positionMs) {
        List<LrcLine> lrc = this.mParsedLrc;
        if (lrc == null || lrc.isEmpty()) {
            return "";
        }
        String line = "";
        for (LrcLine l : lrc) {
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
        List<LrcLine> lrc = this.mParsedLrc;
        if (lrc == null || lrc.isEmpty()) {
            return "";
        }
        for (LrcLine l : lrc) {
            if (l.timeMs > positionMs && l.text != null && !l.text.trim().isEmpty()) {
                return l.text;
            }
        }
        return "";
    }

    private static String readAll(HttpURLConnection conn) throws Exception {
        InputStream is = conn.getInputStream();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            while (true) {
                int n = is.read(buf);
                if (n == -1) {
                    break;
                }
                bos.write(buf, 0, n);
            }
            return new String(bos.toByteArray(), "UTF-8");
        } finally {
            try {
                is.close();
            } catch (Throwable th) {
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:103:0x02e8 A[Catch: all -> 0x0351, TRY_ENTER, TryCatch #0 {all -> 0x0351, blocks: (B:88:0x0267, B:90:0x0275, B:94:0x02b5, B:103:0x02e8, B:104:0x0314, B:119:0x02a2, B:121:0x02ac, B:130:0x0315, B:131:0x0337, B:54:0x033f, B:55:0x0350), top: B:52:0x01be }] */
    /* JADX WARN: Removed duplicated region for block: B:109:0x035e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:115:? A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:118:0x029a  */
    /* JADX WARN: Removed duplicated region for block: B:130:0x0315 A[Catch: all -> 0x0351, TryCatch #0 {all -> 0x0351, blocks: (B:88:0x0267, B:90:0x0275, B:94:0x02b5, B:103:0x02e8, B:104:0x0314, B:119:0x02a2, B:121:0x02ac, B:130:0x0315, B:131:0x0337, B:54:0x033f, B:55:0x0350), top: B:52:0x01be }] */
    /* JADX WARN: Removed duplicated region for block: B:67:0x01fe  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x025a A[ADDED_TO_REGION, LOOP:2: B:83:0x025a->B:90:0x0275, LOOP_START, PHI: r0 r6 r9 r11 r16
      0x025a: PHI (r0v65 'total' int) = (r0v53 'total' int), (r0v67 'total' int) binds: [B:82:0x0258, B:90:0x0275] A[DONT_GENERATE, DONT_INLINE]
      0x025a: PHI (r6v7 'sp2' int) = (r6v5 'sp2' int), (r6v10 'sp2' int) binds: [B:82:0x0258, B:90:0x0275] A[DONT_GENERATE, DONT_INLINE]
      0x025a: PHI (r9v8 'n' int) = (r9v0 'n' int), (r9v14 'n' int) binds: [B:82:0x0258, B:90:0x0275] A[DONT_GENERATE, DONT_INLINE]
      0x025a: PHI (r11v2 'addrs' java.net.InetAddress[]) = (r11v0 'addrs' java.net.InetAddress[]), (r11v7 'addrs' java.net.InetAddress[]) binds: [B:82:0x0258, B:90:0x0275] A[DONT_GENERATE, DONT_INLINE]
      0x025a: PHI (r16v9 'buf' byte[]) = (r16v7 'buf' byte[]), (r16v12 'buf' byte[]) binds: [B:82:0x0258, B:90:0x0275] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:97:0x02e0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static java.lang.String httpGetV4(java.lang.String r29, java.lang.String r30, java.lang.String r31, int r32) throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 893
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ecarx.carmedia.CloudLyricFetcher.httpGetV4(java.lang.String, java.lang.String, java.lang.String, int):java.lang.String");
    }

    public static List<LrcLine> parseLrc(String lrc) {
        int end;
        List<LrcLine> lines = new ArrayList<>();
        if (lrc == null || lrc.isEmpty()) {
            return lines;
        }
        String[] raw = lrc.split("\n");
        for (String line : raw) {
            String s = line.trim();
            if (!s.isEmpty()) {
                int idx = 0;
                while (idx < s.length() && s.charAt(idx) == '[' && (end = s.indexOf(93, idx)) != -1) {
                    String tag = s.substring(idx + 1, end);
                    long t = parseTime(tag);
                    if (t >= 0) {
                        String text = s.substring(end + 1);
                        if (text.trim().isEmpty()) {
                            idx = end + 1;
                        } else {
                            lines.add(new LrcLine(t, text));
                        }
                    }
                    idx = end + 1;
                }
            }
        }
        Collections.sort(lines, new Comparator() { // from class: com.ecarx.carmedia.CloudLyricFetcher$$ExternalSyntheticLambda0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int compare;
                compare = Long.compare(((CloudLyricFetcher.LrcLine) obj).timeMs, ((CloudLyricFetcher.LrcLine) obj2).timeMs);
                return compare;
            }
        });
        return lines;
    }

    private static long parseTime(String s) {
        try {
            String[] mmss = s.split(":");
            if (mmss.length < 2) {
                return -1L;
            }
            int mm = Integer.parseInt(mmss[0].trim());
            String ssPart = mmss[1].trim();
            double sec = Double.parseDouble(ssPart);
            return (long) ((mm * 60000) + (1000.0d * sec));
        } catch (Throwable th) {
            return -1L;
        }
    }
}
