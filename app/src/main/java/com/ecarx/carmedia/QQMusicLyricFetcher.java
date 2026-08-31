package com.ecarx.carmedia;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import com.ecarx.carmedia.LrcUtil;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class QQMusicLyricFetcher {
    private static final long ACTIVE_HOLD_MS = 3000;
    public static final int API_ACTIVE = 1;
    public static final int API_ERROR = 2;
    public static final int API_IDLE = 0;
    public static final int API_SUCCESS = 3;
    private static final String DESCRIPTOR_API = "com.tencent.qqmusic.third.api.contract.IQQMusicApi";
    private static final String DESCRIPTOR_CB = "com.tencent.qqmusic.third.api.contract.IQQMusicApiCallback";
    private static final String DESCRIPTOR_LISTENER = "com.tencent.qqmusic.third.api.contract.IQQMusicApiEventListener";
    private static final String PKG = "com.tencent.qqmusiccar";
    private static final String SVC = "com.tencent.qqmusiccar.third.api.QQMusicApiService";
    private static final String TAG = "QQMusicLyric";
    private ApiClient mApi;
    private boolean mAuthChecked;
    private boolean mAuthFileOk;
    private boolean mAuthWritten;
    private final Callback mCallback;
    private CallbackBinder mCallbackBinder;
    private final Context mContext;
    private EventListenerBinder mEventListenerBinder;
    private volatile boolean mHasLyric;
    private boolean mRestartedForAuth;
    private volatile boolean mRestarting;
    private volatile int mApiStatus = 0;
    private volatile String mApiMsg = "";
    private volatile long mApiChangedAt = 0;
    private volatile long mApiActiveAt = 0;
    private final ServiceConnection mConn = new ServiceConnection() { // from class: com.ecarx.carmedia.QQMusicLyricFetcher.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(QQMusicLyricFetcher.TAG, "onServiceConnected: " + name);
            QQMusicLyricFetcher.this.mApi = new ApiClient(service);
            QQMusicLyricFetcher.this.doHandshake();
            QQMusicLyricFetcher.this.fetchCurrentSong();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            Log.d(QQMusicLyricFetcher.TAG, "onServiceDisconnected");
            QQMusicLyricFetcher.this.mApi = null;
            QQMusicLyricFetcher.this.mBound = false;
        }
    };
    private boolean mBound = false;
    private String mCurrentSongId = null;
    private String mCurrentSongTitle = null;
    private String mCurrentSongArtist = null;
    private String mCurrentLrc = null;
    private List<LrcUtil.Line> mParsedLrc = null;
    private long mLastFetchTime = 0;

    public interface Callback {
        void onError(String str);

        void onLyricReady(String str, String str2, String str3);
    }

    public interface RootProbeCallback {
        void onRootProbeResult(boolean z);
    }

    private void setApiStatus(int status, String msg) {
        this.mApiStatus = status;
        this.mApiMsg = msg != null ? msg : "";
        this.mApiChangedAt = System.currentTimeMillis();
        if (status == 1) {
            this.mApiActiveAt = System.currentTimeMillis();
        }
        if (status == 0 || status == 3) {
            this.mApiMsg = "";
        }
        Log.d(TAG, "apiStatus -> " + status + " " + msg);
    }

    public void resetApiStatus() {
        this.mApiStatus = 0;
        this.mApiMsg = "";
        this.mApiActiveAt = 0L;
        this.mApiChangedAt = 0L;
        this.mHasLyric = false;
        this.mRestartedForAuth = false;
        this.mRestarting = false;
    }

    public JSONObject getApiStatus() {
        JSONObject o = new JSONObject();
        try {
            long now = System.currentTimeMillis();
            int st = this.mApiStatus;
            if (st != 1 && ((st == 0 || st == 3) && this.mApiActiveAt > 0 && now - this.mApiActiveAt < ACTIVE_HOLD_MS)) {
                st = 1;
            }
            o.put("status", st);
            o.put("msg", st == 0 ? "" : this.mApiMsg);
        } catch (Throwable th) {
        }
        return o;
    }

    private static class ApiClient {
        private final IBinder mRemote;

        ApiClient(IBinder remote) {
            this.mRemote = remote;
        }

        Bundle execute(String method, Bundle args) throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken(QQMusicLyricFetcher.DESCRIPTOR_API);
                data.writeString(method);
                writeBundle(data, args);
                this.mRemote.transact(1, data, reply, 0);
                reply.readException();
                return readBundle(reply);
            } finally {
                reply.recycle();
                data.recycle();
            }
        }

        void executeAsync(String method, Bundle args, IBinder callbackBinder) throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken(QQMusicLyricFetcher.DESCRIPTOR_API);
                data.writeString(method);
                writeBundle(data, args);
                data.writeStrongBinder(callbackBinder);
                this.mRemote.transact(2, data, reply, 0);
                reply.readException();
            } finally {
                reply.recycle();
                data.recycle();
            }
        }

        Bundle registerEventListener(List<String> events, IBinder listenerBinder) throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken(QQMusicLyricFetcher.DESCRIPTOR_API);
                data.writeStringList(events);
                data.writeStrongBinder(listenerBinder);
                this.mRemote.transact(3, data, reply, 0);
                reply.readException();
                return readBundle(reply);
            } finally {
                reply.recycle();
                data.recycle();
            }
        }

        private static void writeBundle(Parcel p, Bundle b) {
            if (b != null) {
                p.writeInt(1);
                b.writeToParcel(p, 0);
            } else {
                p.writeInt(0);
            }
        }

        private static Bundle readBundle(Parcel p) {
            if (p.readInt() != 0) {
                return (Bundle) Bundle.CREATOR.createFromParcel(p);
            }
            return null;
        }
    }

    private static class CallbackBinder extends Binder {
        private final Supplier<String> mArtistSupplier;
        private final Callback mCallback;
        private final Consumer<List<LrcUtil.Line>> mLrcConsumer;
        private final Consumer<String> mLrcRawConsumer;
        private final Consumer<Integer> mStatusConsumer;
        private final Supplier<String> mTitleSupplier;

        CallbackBinder(Callback cb, Supplier<String> title, Supplier<String> artist, Consumer<List<LrcUtil.Line>> lrcConsumer, Consumer<String> lrcRawConsumer, Consumer<Integer> statusConsumer) {
            this.mCallback = cb;
            this.mTitleSupplier = title;
            this.mArtistSupplier = artist;
            this.mLrcConsumer = lrcConsumer;
            this.mLrcRawConsumer = lrcRawConsumer;
            this.mStatusConsumer = statusConsumer;
        }

        @Override // android.os.Binder
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(QQMusicLyricFetcher.DESCRIPTOR_CB);
                    Bundle b = null;
                    if (data.readInt() != 0) {
                        b = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    }
                    int code2 = b != null ? b.getInt("code") : -1;
                    String lyric = b != null ? b.getString("data") : null;
                    Log.d(QQMusicLyricFetcher.TAG, "getLyric code=" + code2 + " len=" + (lyric != null ? lyric.length() : 0));
                    if (code2 == 0 && lyric != null && !lyric.isEmpty()) {
                        this.mLrcRawConsumer.accept(lyric);
                        this.mLrcConsumer.accept(LrcUtil.parse(lyric));
                        if (this.mStatusConsumer != null) {
                            this.mStatusConsumer.accept(3);
                        }
                        if (this.mCallback != null) {
                            this.mCallback.onLyricReady(lyric, this.mTitleSupplier.get(), this.mArtistSupplier.get());
                        }
                    } else {
                        Log.w(QQMusicLyricFetcher.TAG, "getLyric failed code=" + code2);
                        this.mLrcConsumer.accept(null);
                        if (this.mStatusConsumer != null) {
                            this.mStatusConsumer.accept(2);
                        }
                        if (this.mCallback != null) {
                            this.mCallback.onLyricReady("", this.mTitleSupplier.get(), this.mArtistSupplier.get());
                        }
                    }
                    reply.writeNoException();
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    private static class EventListenerBinder extends Binder {
        private final Runnable mOnPlaySong;

        EventListenerBinder(Runnable onPlaySong) {
            this.mOnPlaySong = onPlaySong;
        }

        @Override // android.os.Binder
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(QQMusicLyricFetcher.DESCRIPTOR_LISTENER);
                    String event = data.readString();
                    Log.d(QQMusicLyricFetcher.TAG, "EVENT " + event);
                    if ("playSong".equals(event) && this.mOnPlaySong != null) {
                        this.mOnPlaySong.run();
                    }
                    reply.writeNoException();
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    public QQMusicLyricFetcher(Context context, Callback callback) {
        this.mContext = context;
        this.mCallback = callback;
    }

    public void bind() {
        if (!isQqMusicInstalled()) {
            Log.d(TAG, "bind: QQ music not installed, skip su/bind");
            setApiStatus(0, "QQ音乐未安装");
            return;
        }
        if (this.mBound) {
            return;
        }
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.tencent.qqmusiccar", SVC));
            setApiStatus(1, "绑定服务中");
            boolean ok = this.mContext.bindService(intent, this.mConn, 1);
            Log.d(TAG, "bindService returned: " + ok);
            this.mBound = ok;
            if (!ok) {
                setApiStatus(2, "bindService=false");
            }
        } catch (Exception e) {
            Log.e(TAG, "bind error: " + e.getMessage());
            setApiStatus(2, e.getMessage());
            if (this.mCallback != null) {
                this.mCallback.onError("bind: " + e.getMessage());
            }
        }
    }

    public boolean restartQqProcess() {
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("am force-stop com.tencent.qqmusiccar\n");
            os.writeBytes("exit\n");
            os.flush();
            if (!p.waitFor(3L, TimeUnit.SECONDS)) {
                Log.w(TAG, ">>> restartQqProcess: su timeout, killing");
                try {
                    p.destroy();
                } catch (Throwable th) {
                }
                return false;
            }
            int exit = p.exitValue();
            Log.d(TAG, ">>> restartQqProcess: force-stop exit=" + exit);
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                }
                if (!isQqProcessAlive()) {
                    break;
                }
            }
            unbind();
            return true;
        } catch (Exception e2) {
            Log.d(TAG, ">>> restartQqProcess FAIL: " + e2.getMessage());
            return false;
        }
    }

    private boolean isQqProcessAlive() {
        String line;
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("ps -A | grep com.tencent.qqmusiccar\n");
            os.writeBytes("exit\n");
            os.flush();
            if (!p.waitFor(3L, TimeUnit.SECONDS)) {
                try {
                    p.destroy();
                } catch (Throwable th) {
                }
                return true;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            do {
                line = br.readLine();
                if (line == null) {
                    try {
                        p.destroy();
                        return false;
                    } catch (Throwable th2) {
                        return false;
                    }
                }
            } while (!line.contains("com.tencent.qqmusiccar"));
            p.destroy();
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public void unbind() {
        if (this.mBound && this.mConn != null) {
            try {
                this.mContext.unbindService(this.mConn);
            } catch (Exception e) {
            }
        }
        this.mBound = false;
        this.mApi = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doHandshake() {
        if (this.mApi == null) {
            setApiStatus(2, "服务未连接");
            if (this.mCallback != null) {
                this.mCallback.onError("服务未连接");
                return;
            }
            return;
        }
        try {
            setApiStatus(1, "hi");
            Bundle b = new Bundle();
            b.putInt("sdkVersionCode", 10000);
            Bundle r = this.mApi.execute("hi", b);
            int code = r != null ? r.getInt("code") : -1;
            Log.d(TAG, "hi code=" + code);
            if (code != 0) {
                setApiStatus(2, "hi code=" + code);
                if (this.mCallback != null) {
                    this.mCallback.onError("hi code=" + code);
                }
            } else {
                setApiStatus(3, "已连接");
            }
            this.mEventListenerBinder = new EventListenerBinder(new Runnable() { // from class: com.ecarx.carmedia.QQMusicLyricFetcher$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    QQMusicLyricFetcher.this.m0lambda$doHandshake$0$comecarxcarmediaQQMusicLyricFetcher();
                }
            });
            this.mApi.registerEventListener(Arrays.asList("playSong", "playState"), this.mEventListenerBinder);
            fetchCurrentSong();
        } catch (Exception e) {
            Log.e(TAG, "handshake error: " + e.getMessage());
            setApiStatus(2, e.getMessage());
            if (this.mCallback != null) {
                this.mCallback.onError("handshake: " + e.getMessage());
            }
        }
    }

    /* renamed from: lambda$doHandshake$0$com-ecarx-carmedia-QQMusicLyricFetcher, reason: not valid java name */
    /* synthetic */ void m0lambda$doHandshake$0$comecarxcarmediaQQMusicLyricFetcher() {
        try {
            fetchCurrentSong();
        } catch (Exception e) {
            Log.e(TAG, "event fetchCurrentSong error: " + e.getMessage());
        }
    }

    public void fetchCurrentSong() {
        if (this.mApi != null) {
            try {
                setApiStatus(1, "getCurrentSong");
                Bundle r = this.mApi.execute("getCurrentSong", null);
                int code = r != null ? r.getInt("code") : -1;
                String data = r != null ? r.getString("data") : null;
                Log.d(TAG, "getCurrentSong code=" + code + " data=" + data);
                if (code == 0 && data != null) {
                    setApiStatus(3, "歌曲信息就绪");
                    parseAndFetchLyric(data);
                    return;
                }
                if (code != 5 || this.mRestartedForAuth) {
                    setApiStatus(2, "getCurrentSong code=" + code);
                    if (this.mCallback != null && !this.mHasLyric) {
                        this.mCallback.onError("getCurrentSong code=" + code);
                        return;
                    }
                    return;
                }
                this.mRestartedForAuth = true;
                Log.w(TAG, "getCurrentSong code=5, no permission (need manual auth)");
                setApiStatus(2, "未授权");
                if (this.mCallback != null && !this.mHasLyric) {
                    this.mCallback.onError("getCurrentSong code=5 未授权");
                    return;
                }
                return;
            } catch (Exception e) {
                Log.e(TAG, "getCurrentSong error: " + e.getMessage());
                setApiStatus(2, e.getMessage());
                return;
            }
        }
        Log.d(TAG, "fetchCurrentSong: mApi null (binding), skip silently");
    }

    private void parseAndFetchLyric(String json) {
        int t;
        int end;
        String songNumId = null;
        String mid = null;
        String title = null;
        String artist = null;
        try {
            int idIdx = json.lastIndexOf("\"id\":\"");
            if (idIdx >= 0) {
                String s = json.substring(idIdx + 6);
                int pipe = s.indexOf(124);
                int quote = s.indexOf(34);
                if (pipe > 0 && (quote < 0 || pipe < quote)) {
                    end = pipe;
                } else if (quote > 0) {
                    end = quote;
                } else {
                    end = -1;
                }
                String songNumId2 = end > 0 ? s.substring(0, end) : s;
                songNumId = songNumId2.replaceAll("[^0-9]", "");
            }
            int midIdx = json.lastIndexOf("\"mid\":\"");
            if (midIdx >= 0) {
                String s2 = json.substring(midIdx + 7);
                mid = s2.split("\"")[0];
            }
            int titleIdx = json.lastIndexOf("\"title\":\"");
            if (titleIdx >= 0) {
                String s3 = json.substring(titleIdx + 9);
                title = s3.split("\"")[0];
            }
            int singerIdx = json.indexOf("\"singer\":{");
            if (singerIdx >= 0 && (t = json.indexOf("\"title\":\"", singerIdx)) >= 0) {
                String s4 = json.substring(t + 9);
                artist = s4.split("\"")[0];
            }
            this.mCurrentSongTitle = title;
            this.mCurrentSongArtist = artist;
            Log.d(TAG, "parsed: id=" + songNumId + " mid=" + mid + " title=" + title + " artist=" + artist);
            if (songNumId != null && !songNumId.isEmpty() && !songNumId.equals(this.mCurrentSongId)) {
                this.mCurrentSongId = songNumId;
                Log.d(TAG, "calling fetchLyric for songId=" + songNumId);
                fetchLyric(Long.parseLong(songNumId));
                return;
            }
            Log.d(TAG, "skipping fetchLyric (songNumId null/empty/same)");
        } catch (Exception e) {
            Log.e(TAG, "parse error: " + e.getMessage());
        }
    }

    private void fetchLyric(long songId) {
        if (this.mApi == null) {
            setApiStatus(2, "服务未连接");
            return;
        }
        try {
            Bundle b = new Bundle();
            b.putLong("songId", songId);
            Log.d(TAG, "calling executeAsync getLyric...");
            setApiStatus(1, "getLyric");
            this.mCallbackBinder = new CallbackBinder(this.mCallback, new Supplier() { // from class: com.ecarx.carmedia.QQMusicLyricFetcher$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    return QQMusicLyricFetcher.this.m1lambda$fetchLyric$1$comecarxcarmediaQQMusicLyricFetcher();
                }
            }, new Supplier() { // from class: com.ecarx.carmedia.QQMusicLyricFetcher$$ExternalSyntheticLambda2
                @Override // java.util.function.Supplier
                public final Object get() {
                    return QQMusicLyricFetcher.this.m2lambda$fetchLyric$2$comecarxcarmediaQQMusicLyricFetcher();
                }
            }, new Consumer() { // from class: com.ecarx.carmedia.QQMusicLyricFetcher$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    QQMusicLyricFetcher.this.m3lambda$fetchLyric$3$comecarxcarmediaQQMusicLyricFetcher((List) obj);
                }
            }, new Consumer() { // from class: com.ecarx.carmedia.QQMusicLyricFetcher$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    QQMusicLyricFetcher.this.m4lambda$fetchLyric$4$comecarxcarmediaQQMusicLyricFetcher((String) obj);
                }
            }, new Consumer() { // from class: com.ecarx.carmedia.QQMusicLyricFetcher$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    QQMusicLyricFetcher.this.m5lambda$fetchLyric$5$comecarxcarmediaQQMusicLyricFetcher((Integer) obj);
                }
            });
            this.mApi.executeAsync("getLyric", b, this.mCallbackBinder);
        } catch (Exception e) {
            Log.e(TAG, "fetchLyric error: " + e.getMessage());
        }
    }

    /* renamed from: lambda$fetchLyric$1$com-ecarx-carmedia-QQMusicLyricFetcher, reason: not valid java name */
    /* synthetic */ String m1lambda$fetchLyric$1$comecarxcarmediaQQMusicLyricFetcher() {
        return this.mCurrentSongTitle;
    }

    /* renamed from: lambda$fetchLyric$2$com-ecarx-carmedia-QQMusicLyricFetcher, reason: not valid java name */
    /* synthetic */ String m2lambda$fetchLyric$2$comecarxcarmediaQQMusicLyricFetcher() {
        return this.mCurrentSongArtist;
    }

    /* renamed from: lambda$fetchLyric$3$com-ecarx-carmedia-QQMusicLyricFetcher, reason: not valid java name */
    /* synthetic */ void m3lambda$fetchLyric$3$comecarxcarmediaQQMusicLyricFetcher(List lines) {
        this.mParsedLrc = lines;
        this.mLastFetchTime = System.currentTimeMillis();
    }

    /* renamed from: lambda$fetchLyric$4$com-ecarx-carmedia-QQMusicLyricFetcher, reason: not valid java name */
    /* synthetic */ void m4lambda$fetchLyric$4$comecarxcarmediaQQMusicLyricFetcher(String lrc) {
        this.mCurrentLrc = lrc;
        this.mHasLyric = (lrc == null || lrc.isEmpty()) ? false : true;
    }

    /* renamed from: lambda$fetchLyric$5$com-ecarx-carmedia-QQMusicLyricFetcher, reason: not valid java name */
    /* synthetic */ void m5lambda$fetchLyric$5$comecarxcarmediaQQMusicLyricFetcher(Integer status) {
        if (status.intValue() == 0) {
            setApiStatus(0, "");
        } else if (status.intValue() == 3) {
            setApiStatus(3, "歌词就绪");
        } else {
            setApiStatus(2, "getLyric 失败");
        }
    }

    public String getCurrentLyricLine(long positionMs) {
        if (this.mParsedLrc != null && !this.mParsedLrc.isEmpty()) {
            return LrcUtil.getCurrentLine(this.mParsedLrc, positionMs);
        }
        return "";
    }

    public String getNextLyricLine(long positionMs) {
        if (this.mParsedLrc != null && !this.mParsedLrc.isEmpty()) {
            return LrcUtil.getNextLine(this.mParsedLrc, positionMs);
        }
        return "";
    }

    public String getSongTitle() {
        return this.mCurrentSongTitle;
    }

    public String getSongArtist() {
        return this.mCurrentSongArtist;
    }

    public boolean isReady() {
        return (this.mApi == null || this.mParsedLrc == null) ? false : true;
    }

    public boolean hasLyric() {
        return this.mHasLyric;
    }

    public boolean isRestarting() {
        return this.mRestarting;
    }

    private void writeAuthViaSu() {
        if (this.mAuthWritten) {
            return;
        }
        Log.d(TAG, ">>> writeAuthViaSu: writing authorization");
        Process p = null;
        try {
            try {
                try {
                    long now = System.currentTimeMillis();
                    Process p2 = Runtime.getRuntime().exec("su");
                    DataOutputStream os = new DataOutputStream(p2.getOutputStream());
                    os.writeBytes("grep -q com.ecarx.carmedia /data/data/com.tencent.qqmusiccar/app_aidl/PackageFile 2>/dev/null || echo 'com.ecarx.carmedia' >> /data/data/com.tencent.qqmusiccar/app_aidl/PackageFile\n");
                    os.writeBytes("cat > /data/data/com.tencent.qqmusiccar/shared_prefs/com.ecarx.carmedia.xml << 'EOF'\n");
                    os.writeBytes("<?xml version='1.0' encoding='utf-8' standalone='yes' ?>\n<map>\n");
                    os.writeBytes("<string name='appid'>161</string>\n");
                    os.writeBytes("<long name='authtime' value='" + now + "'/>\n");
                    os.writeBytes("<long name='activetime' value='" + now + "'/>\n");
                    os.writeBytes("<long name='userauthtime' value='" + now + "'/>\n");
                    os.writeBytes("</map>\nEOF\n");
                    os.writeBytes("chown $(stat -c '%u:%g' /data/data/com.tencent.qqmusiccar) /data/data/com.tencent.qqmusiccar/shared_prefs/com.ecarx.carmedia.xml\n");
                    os.writeBytes("chmod 660 /data/data/com.tencent.qqmusiccar/shared_prefs/com.ecarx.carmedia.xml\nexit\n");
                    os.flush();
                    if (p2.waitFor(3L, TimeUnit.SECONDS)) {
                        int exit = p2.exitValue();
                        Log.d(TAG, ">>> writeAuthViaSu: exit=" + exit);
                        if (exit == 0) {
                            this.mAuthWritten = true;
                            this.mAuthChecked = false;
                        }
                    } else {
                        Log.w(TAG, ">>> writeAuthViaSu: su timeout, killing");
                        try {
                            p2.destroy();
                        } catch (Throwable th) {
                        }
                    }
                    if (p2 != null) {
                        p2.destroy();
                    }
                } catch (Exception e) {
                    Log.d(TAG, ">>> writeAuthViaSu FAIL: " + e.getMessage());
                    if (0 != 0) {
                        p.destroy();
                    }
                }
            } catch (Throwable th2) {
                if (0 != 0) {
                    try {
                        p.destroy();
                    } catch (Throwable th3) {
                    }
                }
                throw th2;
            }
        } catch (Throwable th4) {
        }
    }

    public boolean isQqMusicInstalled() {
        try {
            this.mContext.getPackageManager().getPackageInfo("com.tencent.qqmusiccar", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public int getAuthStatus() {
        if (isQqMusicInstalled()) {
            return (this.mAuthWritten || this.mAuthFileOk || this.mApiStatus != 2) ? 1 : 2;
        }
        return 0;
    }

    public void forceAuthWrite() {
        this.mAuthWritten = false;
        writeAuthViaSu();
    }

    public void probeRoot(final RootProbeCallback cb) {
        Thread t = new Thread(new Runnable() { // from class: com.ecarx.carmedia.QQMusicLyricFetcher.2
            @Override // java.lang.Runnable
            public void run() {
                Process p = null;
                boolean ok = false;
                try {
                    try {
                        Process p2 = Runtime.getRuntime().exec("su -c id");
                        BufferedReader br = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                        if (p2.waitFor(5L, TimeUnit.SECONDS)) {
                            String line = br.readLine();
                            ok = line != null && line.contains("uid=0");
                            Log.d(QQMusicLyricFetcher.TAG, ">>> probeRoot: hasRoot=" + ok);
                        } else {
                            Log.d(QQMusicLyricFetcher.TAG, ">>> probeRoot: su timeout, assume no root");
                        }
                        if (p2 != null) {
                            p2.destroy();
                        }
                    } catch (Exception e) {
                        Log.d(QQMusicLyricFetcher.TAG, ">>> probeRoot FAIL: " + e.getMessage());
                        if (0 != 0) {
                            p.destroy();
                        }
                    }
                    boolean result = ok;
                    if (cb != null) {
                        cb.onRootProbeResult(result);
                    }
                } catch (Throwable th) {
                    if (0 != 0) {
                        try {
                            p.destroy();
                        } catch (Throwable th2) {
                        }
                    }
                    throw th;
                }
            }
        }, "qq-probe-root");
        t.start();
    }

    public void refreshAuthState() {
        this.mAuthChecked = false;
    }
}
