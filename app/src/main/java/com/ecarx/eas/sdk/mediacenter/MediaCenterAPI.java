package com.ecarx.eas.sdk.mediacenter;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.ecarx.eas.framework.sdk.IEASFrameworkService;
import com.ecarx.eas.framework.sdk.IEASFrameworkSuppportService;
import com.ecarx.eas.sdk.ECarXApiClient;
import com.ecarx.sdk.openapi.msg.EASFrameworkMessage;
import com.ecarx.sdk.openapi.msg.EASFrameworkRetMessage;
import com.ecarx.sdk.openapi.msg.SupportServiceRetMessage;
import ecarx.xsf.mediacenter.IMediaCenterClientToken;
import ecarx.xsf.mediacenter.IMediaCenterSvc;
import ecarx.xsf.mediacenter.IMusicPlaybackInfo;
import org.json.JSONObject;

/* loaded from: classes4.dex */
public class MediaCenterAPI {
    private static final int GET_BINDER_MAX_RETRIES = 10;
    private static final long GET_BINDER_RETRY_DELAY_MS = 200;
    private static final String MEDIACENTER_MODULE = "MediaCenterAPI";
    private static final String MEDIACENTER_SERVICE = "mediacenter";
    private static final String TAG = "EasMediaCenterAPIImpl";
    private static Context sAppContext;
    private static MediaCenterAPI sInstance;
    private IEASFrameworkService easService;
    private ECarXApiClient.Callback initCallback;
    private Handler initHandler;
    private IBinder mediaCenterBinder;
    private volatile IMediaCenterSvc mediaCenterSvc;
    private MusicClient musicClient;
    private MusicClientWrapper musicClientWrapper;
    private IEASFrameworkSuppportService supportService;
    private boolean isBound = false;
    private boolean isInitialized = false;
    private final byte[] mAttachParam = new byte[0];
    private final IBinder.DeathRecipient deathRecipient = new C0003AnonymousClass1();
    private final ServiceConnection easConnection = new ServiceConnection() { // from class: com.ecarx.eas.sdk.mediacenter.MediaCenterAPI.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(MediaCenterAPI.TAG, "EAS Framework connected");
            MediaCenterAPI.this.easService = IEASFrameworkService.Stub.asInterface(iBinder);
            MediaCenterAPI.this.completeInit();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.w(MediaCenterAPI.TAG, "EAS Framework disconnected");
            MediaCenterAPI.this.easService = null;
            MediaCenterAPI.this.isBound = false;
        }
    };
    private final ServiceConnection mediaCenterConnection = new ServiceConnection() { // from class: com.ecarx.eas.sdk.mediacenter.MediaCenterAPI.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IBinder iBinder2;
            Log.d(MediaCenterAPI.TAG, "MediaCenter service connected directly (fallback)");
            try {
                if (IEASFrameworkSuppportService.DESCRIPTOR.equals(iBinder.getInterfaceDescriptor())) {
                    MediaCenterAPI.this.supportService = IEASFrameworkSuppportService.Stub.asInterface(iBinder);
                    SupportServiceRetMessage call = MediaCenterAPI.this.supportService.call(new EASFrameworkMessage(MediaCenterAPI.MEDIACENTER_SERVICE, MediaCenterAPI.MEDIACENTER_MODULE, "getMainBinder", "NoParam".getBytes(), MediaCenterAPI.this.mAttachParam), Process.myUid(), 0);
                    if (call != null && (iBinder2 = call.mBinder) != null) {
                        MediaCenterAPI.this.setMediaCenterBinder(iBinder2);
                        MediaCenterAPI.this.notifyReady(true);
                        return;
                    }
                }
                MediaCenterAPI.this.setMediaCenterBinder(iBinder);
                MediaCenterAPI.this.notifyReady(true);
            } catch (Exception e2) {
                Log.e(MediaCenterAPI.TAG, "Failed in direct connection", e2);
                MediaCenterAPI.this.notifyReady(false);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.w(MediaCenterAPI.TAG, "MediaCenter service disconnected");
            MediaCenterAPI.this.mediaCenterSvc = null;
        }
    };
    private ExCallbackWrapper exCallbackWrapper = new ExCallbackWrapper();

    /* renamed from: com.ecarx.eas.sdk.mediacenter.MediaCenterAPI$AnonymousClass1, reason: case insensitive filesystem */
    public class C0003AnonymousClass1 implements IBinder.DeathRecipient {
        public C0003AnonymousClass1() {
        }

        public void lambda$binderDied$0() {
            MediaCenterAPI.this.init(MediaCenterAPI.sAppContext, MediaCenterAPI.this.initCallback);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Log.w(MediaCenterAPI.TAG, "MediaCenter service died — cleaning up");
            synchronized (MediaCenterAPI.this) {
                if (MediaCenterAPI.this.mediaCenterBinder != null) {
                    try {
                        MediaCenterAPI.this.mediaCenterBinder.unlinkToDeath(this, 0);
                    } catch (Exception e) {
                    }
                    MediaCenterAPI.this.mediaCenterBinder = null;
                }
                MediaCenterAPI.this.mediaCenterSvc = null;
                MediaCenterAPI.this.isInitialized = false;
            }
            if (MediaCenterAPI.sAppContext == null || MediaCenterAPI.this.initCallback == null) {
                return;
            }
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.ecarx.eas.sdk.mediacenter.MediaCenterAPI.AnonymousClass1.1
                @Override // java.lang.Runnable
                public final void run() {
                    C0003AnonymousClass1.this.lambda$binderDied$0();
                }
            }, 3000L);
        }
    }

    private MediaCenterAPI() {
    }

    private void attemptGetMainBinder(final int i) {
        try {
            IBinder sendStrMsgForBinder = sendStrMsgForBinder("getMainBinder", "");
            if (sendStrMsgForBinder != null) {
                setMediaCenterBinder(sendStrMsgForBinder);
                Log.i(TAG, "getMainBinder via EAS: OK (attempt " + i + ")");
                Log.d(TAG, "InitialImpl OK");
                notifyReady(true);
                return;
            }
        } catch (Exception e2) {
            Log.w(TAG, "getMainBinder attempt " + i + " failed: " + e2.getMessage());
        }
        if (i >= GET_BINDER_MAX_RETRIES) {
            Log.w(TAG, "getMainBinder failed after 10 attempts, trying direct binding");
            bindDirectlyToMediaCenter();
        } else {
            Log.d(TAG, "getMainBinder returned null, retry in 200ms (" + (i + 1) + "/10)");
            getInitHandler().postDelayed(new Runnable() { // from class: com.ecarx.eas.sdk.mediacenter.MediaCenterAPI.3
                @Override // java.lang.Runnable
                public final void run() {
                    MediaCenterAPI.this.lambda$attemptGetMainBinder$0(i);
                }
            }, GET_BINDER_RETRY_DELAY_MS);
        }
    }

    private void bindDirectlyToMediaCenter() {
        Intent intent = new Intent("com.ecarx.eas.core.intent.action.SUPPORT_SERVICE");
        intent.setComponent(new ComponentName("ecarx.xsf.mediacenter", "ecarx.xsf.mediacenter.MediaCenterService"));
        try {
            if (!sAppContext.bindService(intent, this.mediaCenterConnection, 1)) {
                Log.e(TAG, "Direct MediaCenter binding failed");
                notifyReady(false);
            }
        } catch (Exception e2) {
            Log.e(TAG, "Error in direct binding", e2);
            notifyReady(false);
        }
    }

    public void completeInit() {
        IEASFrameworkService iEASFrameworkService = this.easService;
        if (iEASFrameworkService == null) {
            notifyReady(false);
            return;
        }
        try {
            iEASFrameworkService.init(new String[]{MEDIACENTER_SERVICE});
            Log.d(TAG, "EAS Framework init() done");
            attemptGetMainBinder(0);
        } catch (Exception e2) {
            Log.e(TAG, "completeInit failed", e2);
            bindDirectlyToMediaCenter();
        }
    }

    public static IMusicPlaybackInfo.Stub createPlaybackInfoBinder(final MusicClient musicClient) {
        return new IMusicPlaybackInfo.Stub() { // from class: com.ecarx.eas.sdk.mediacenter.MediaCenterAPI.4
            private MusicPlaybackInfo info() {
                if (musicClient != null) {
                    return musicClient.getMusicPlaybackInfo();
                }
                return null;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getAlbum() {
                MusicPlaybackInfo info = info();
                return (info == null || info.getAlbum() == null) ? "" : info.getAlbum();
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getAppIcon() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getAppIcon();
                }
                return null;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getAppName() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getAppName();
                }
                return null;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getArtist() {
                MusicPlaybackInfo info = info();
                return (info == null || info.getArtist() == null) ? "" : info.getArtist();
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public Uri getArtwork() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getArtwork();
                }
                return null;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getCurrentLyricSentence() {
                MusicPlaybackInfo info = info();
                return (info == null || info.getCurrentLyricSentence() == null) ? "" : info.getCurrentLyricSentence();
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public int getDisplayId() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getDisplayId();
                }
                return 0;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public long getDuration() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getDuration();
                }
                return 0L;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public PendingIntent getLaunchIntent() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getLaunchIntent();
                }
                return null;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public int getLoopMode() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getLoopMode();
                }
                return 0;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public Uri getLyric() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getLyric();
                }
                return null;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getLyricContent() {
                MusicPlaybackInfo info = info();
                return (info == null || info.getLyricContent() == null) ? "" : info.getLyricContent();
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public Uri getMediaPath() {
                return null;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getMediaType() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getMediaType();
                }
                return null;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public Uri getNextArtwork() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getNextArtwork();
                }
                return null;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getPackageName() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getPackageName();
                }
                return null;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public int getPlaybackStatus() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getPlaybackStatus();
                }
                return 0;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public PendingIntent getPlayerIntent() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getPlayerIntent();
                }
                return null;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public int getPlayingItemPositionInQueue() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getPlayingItemPositionInQueue();
                }
                return 0;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getPlayingMediaListId() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getPlayingMediaListId();
                }
                return null;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public int getPlayingMediaListType() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getPlayingMediaListType();
                }
                return -1;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public Uri getPreviousArtwork() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getPreviousArtwork();
                }
                return null;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getRadioFrequency() {
                MusicPlaybackInfo info = info();
                return (info == null || info.getRadioFrequency() == null) ? "" : info.getRadioFrequency();
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public int getRadioMode() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getRadioMode();
                }
                return 0;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getRadioStationName() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getRadioStationName();
                }
                return null;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public int getSourceType() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getSourceType();
                }
                return 0;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getTitle() {
                MusicPlaybackInfo info = info();
                return (info == null || info.getTitle() == null) ? "" : info.getTitle();
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getUuid() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getUuid();
                }
                return null;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public int getVip() {
                MusicPlaybackInfo info = info();
                if (info != null) {
                    return info.getVip();
                }
                return -1;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public boolean isCollected() {
                MusicPlaybackInfo info = info();
                return info != null && info.isCollected();
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public boolean isDownloaded() {
                MusicPlaybackInfo info = info();
                return info != null && info.isDownloaded();
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public boolean isSupportCollect() {
                MusicPlaybackInfo info = info();
                return info != null && info.isSupportCollect();
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public boolean isSupportDownload() {
                MusicPlaybackInfo info = info();
                return info != null && info.isSupportDownload();
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public boolean isSupportLoopModeSwitch() {
                MusicPlaybackInfo info = info();
                return info == null || info.isSupportLoopModeSwitch();
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public boolean isSupportVrCtrlPlayStatus() {
                MusicPlaybackInfo info = info();
                return info == null || info.isSupportVrCtrlPlayStatus();
            }
        };
    }

    public static synchronized MediaCenterAPI get(Context context) {
        synchronized (MediaCenterAPI.class) {
            if (sInstance == null) {
                sInstance = new MediaCenterAPI();
            }
            sAppContext = context.getApplicationContext();
            return sInstance;
        }
    }

    private Handler getInitHandler() {
        if (this.initHandler == null) {
            this.initHandler = new Handler(Looper.getMainLooper());
        }
        return this.initHandler;
    }

    public void lambda$attemptGetMainBinder$0(int i) {
        attemptGetMainBinder(i + 1);
    }

    public void notifyReady(boolean z) {
        if (this.initCallback != null) {
            Log.i(TAG, "onAPIReady(" + z + ")");
            this.initCallback.onAPIReady(z);
        }
    }

    private EASFrameworkRetMessage sendMsg(EASFrameworkMessage eASFrameworkMessage) {
        IEASFrameworkService iEASFrameworkService = this.easService;
        if (iEASFrameworkService != null) {
            try {
                EASFrameworkRetMessage call = iEASFrameworkService.call(eASFrameworkMessage);
                if (call != null && call.mCode != 200) {
                    Log.w(TAG, "sendMsg fail:" + call.mCode + "," + call.mMsg);
                    return call;
                }
                return call;
            } catch (Exception e2) {
                Log.w(TAG, "sendMsg RemoteException: " + e2.getMessage());
            }
        }
        IEASFrameworkSuppportService iEASFrameworkSuppportService = this.supportService;
        if (iEASFrameworkSuppportService == null) {
            return null;
        }
        try {
            SupportServiceRetMessage call2 = iEASFrameworkSuppportService.call(eASFrameworkMessage, Process.myUid(), 0);
            if (call2 == null) {
                return null;
            }
            EASFrameworkRetMessage eASFrameworkRetMessage = new EASFrameworkRetMessage();
            eASFrameworkRetMessage.mCode = call2.mCode;
            eASFrameworkRetMessage.mRetMsg = call2;
            return eASFrameworkRetMessage;
        } catch (Exception e3) {
            Log.w(TAG, "sendMsg via support failed: " + e3.getMessage());
            return null;
        }
    }

    private EASFrameworkRetMessage sendMsgAndBinder(EASFrameworkMessage eASFrameworkMessage, IBinder iBinder) {
        IEASFrameworkService iEASFrameworkService = this.easService;
        if (iEASFrameworkService == null) {
            return null;
        }
        try {
            EASFrameworkRetMessage asyncBinderCall = iEASFrameworkService.asyncBinderCall(eASFrameworkMessage, iBinder);
            if (asyncBinderCall != null && asyncBinderCall.mCode != 200) {
                Log.w(TAG, "sendMsgAndBinder fail:" + asyncBinderCall.mCode + "," + asyncBinderCall.mMsg);
                return asyncBinderCall;
            }
            return asyncBinderCall;
        } catch (Exception e2) {
            Log.w(TAG, "sendMsgAndBinder RemoteException: " + e2.getMessage());
            return null;
        }
    }

    private String sendStrMsgAndBinderForStr(String str, String str2, IBinder iBinder) {
        String str3;
        SupportServiceRetMessage supportServiceRetMessage;
        byte[] bArr;
        if (this.easService == null) {
            return null;
        }
        try {
            if (TextUtils.isEmpty(str2)) {
                str2 = "NoParam";
            }
            str3 = str;
        } catch (Exception e2) {
            Log.w(TAG, "sendStrMsgAndBinderForStr(init) failed: " + e2.getMessage());
            str3 = str;
        }
        try {
            EASFrameworkRetMessage sendMsgAndBinder = sendMsgAndBinder(new EASFrameworkMessage(MEDIACENTER_SERVICE, MEDIACENTER_MODULE, str3, str2.getBytes(), this.mAttachParam), iBinder);
            if (sendMsgAndBinder == null || (supportServiceRetMessage = sendMsgAndBinder.mRetMsg) == null || (bArr = supportServiceRetMessage.mData) == null || bArr.length <= 0) {
                return null;
            }
            return new String(bArr);
        } catch (Exception e3) {
            Log.w(TAG, "sendStrMsgAndBinderForStr(" + str3 + ") failed: " + e3.getMessage());
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r0v4, types: [android.os.IBinder] */
    private IBinder sendStrMsgForBinder(String str, String str2) {
        SupportServiceRetMessage supportServiceRetMessage;
        if (this.easService == null) {
            return null;
        }
        try {
            if (TextUtils.isEmpty(str2)) {
                str2 = "NoParam";
            }
            try {
                EASFrameworkRetMessage sendMsg = sendMsg(new EASFrameworkMessage(MEDIACENTER_SERVICE, MEDIACENTER_MODULE, str, str2.getBytes(), this.mAttachParam));
                if (sendMsg != null && (supportServiceRetMessage = sendMsg.mRetMsg) != null) {
                    return supportServiceRetMessage.mBinder;
                }
            } catch (Exception e2) {
                Log.w(TAG, "sendStrMsgForBinder(" + str + ") failed: " + e2.getMessage());
                return null;
            }
        } catch (Exception e3) {
            Log.w(TAG, "sendStrMsgForBinder(init) failed: " + e3.getMessage());
        }
        return null;
    }

    public synchronized void setMediaCenterBinder(IBinder iBinder) {
        this.mediaCenterBinder = iBinder;
        this.mediaCenterSvc = IMediaCenterSvc.Stub.asInterface(iBinder);
        try {
            iBinder.linkToDeath(this.deathRecipient, 0);
        } catch (RemoteException e2) {
            Log.e(TAG, "Failed to link death recipient", e2);
        }
        this.isInitialized = true;
        Log.i(TAG, "IMediaCenterSvc obtained successfully");
    }

    public void declareMediaCenterCapability(Object obj, int[] iArr) {
        IMediaCenterSvc iMediaCenterSvc = this.mediaCenterSvc;
        if (iMediaCenterSvc == null || obj == null) {
            return;
        }
        try {
            iMediaCenterSvc.declareMediaCenterCapability((IMediaCenterClientToken) obj, iArr);
        } catch (Exception e2) {
            Log.w(TAG, "declareMediaCenterCapability failed: " + e2.getMessage());
        }
    }

    public boolean declareSupportCollectTypes(Object obj, int[] iArr) {
        IMediaCenterSvc iMediaCenterSvc = this.mediaCenterSvc;
        if (iMediaCenterSvc != null && obj != null) {
            try {
                return iMediaCenterSvc.declareSupportCollectTypes((IMediaCenterClientToken) obj, iArr);
            } catch (Exception e2) {
                Log.w(TAG, "declareSupportCollectTypes failed: " + e2.getMessage());
                return false;
            }
        }
        return false;
    }

    public void destroy() {
        Context context;
        Handler handler = this.initHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            this.initHandler = null;
        }
        try {
            if (this.isBound && (context = sAppContext) != null) {
                context.unbindService(this.easConnection);
            }
        } catch (Exception e) {
        }
        try {
            Context context2 = sAppContext;
            if (context2 != null) {
                context2.unbindService(this.mediaCenterConnection);
            }
        } catch (Exception e2) {
        }
        this.mediaCenterSvc = null;
        this.easService = null;
        this.supportService = null;
        this.isInitialized = false;
        this.isBound = false;
    }

    public void init(Context context, ECarXApiClient.Callback callback) {
        this.initCallback = callback;
        sAppContext = context.getApplicationContext();
        Log.i(TAG, "init() called — binding to EAS Framework");
        Intent intent = new Intent("com.ecarx.easframework.intent.action.EASFRAMEWORK");
        intent.setPackage("com.ecarx.sdk.openapi");
        try {
            if (sAppContext.bindService(intent, this.easConnection, 1)) {
                this.isBound = true;
                Log.d(TAG, "Binding to EAS Framework...");
            } else {
                Log.w(TAG, "EAS Framework bind failed, trying direct binding");
                bindDirectlyToMediaCenter();
            }
        } catch (Exception e2) {
            Log.e(TAG, "Error binding to EAS Framework", e2);
            bindDirectlyToMediaCenter();
        }
    }

    public boolean isReady() {
        return this.isInitialized && this.mediaCenterSvc != null;
    }

    public boolean isServiceAlive() {
        IMediaCenterSvc iMediaCenterSvc = this.mediaCenterSvc;
        if (iMediaCenterSvc == null) {
            return false;
        }
        try {
            IBinder asBinder = iMediaCenterSvc.asBinder();
            if (asBinder != null && asBinder.isBinderAlive()) {
                if (asBinder.pingBinder()) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public String queryCurrentFocusClient(Object obj) {
        IMediaCenterSvc iMediaCenterSvc = this.mediaCenterSvc;
        if (iMediaCenterSvc != null && obj != null) {
            try {
                Log.d(TAG, "queryCurrentFocusClient");
                String queryCurrentFocusClient = iMediaCenterSvc.queryCurrentFocusClient((IMediaCenterClientToken) obj);
                return queryCurrentFocusClient != null ? queryCurrentFocusClient : "";
            } catch (Exception e2) {
                Log.w(TAG, "queryCurrentFocusClient failed: " + e2.getMessage());
            }
        }
        return "";
    }

    public void reconnect() {
        ECarXApiClient.Callback callback;
        Log.w(TAG, "reconnect() — forcing re-initialization");
        synchronized (this) {
            IBinder iBinder = this.mediaCenterBinder;
            if (iBinder != null) {
                try {
                    iBinder.unlinkToDeath(this.deathRecipient, 0);
                } catch (Exception e) {
                }
                this.mediaCenterBinder = null;
            }
            this.mediaCenterSvc = null;
            this.isInitialized = false;
        }
        Context context = sAppContext;
        if (context != null) {
            if (this.isBound) {
                try {
                    context.unbindService(this.easConnection);
                } catch (Exception e2) {
                }
                this.isBound = false;
                this.easService = null;
            }
            try {
                sAppContext.unbindService(this.mediaCenterConnection);
            } catch (Exception e3) {
            }
        }
        Context context2 = sAppContext;
        if (context2 == null || (callback = this.initCallback) == null) {
            return;
        }
        init(context2, callback);
    }

    public Object registerMusic(String str, MusicClient musicClient) {
        IMediaCenterSvc iMediaCenterSvc = this.mediaCenterSvc;
        if (iMediaCenterSvc == null) {
            Log.e(TAG, "registerMusic: mediaCenterSvc is null");
            return null;
        }
        try {
            Log.d(TAG, "svc: " + iMediaCenterSvc);
            this.musicClient = musicClient;
            MusicClientWrapper musicClientWrapper = new MusicClientWrapper(musicClient);
            this.musicClientWrapper = musicClientWrapper;
            IMediaCenterClientToken registerInMusic = iMediaCenterSvc.registerInMusic(str, musicClientWrapper);
            Log.d(TAG, "token: " + registerInMusic);
            if (registerInMusic == null) {
                try {
                    registerInMusic = iMediaCenterSvc.registerMusic(this.musicClientWrapper);
                    Log.d(TAG, "registerMusic fallback token: " + registerInMusic);
                } catch (Exception e2) {
                    Log.e(TAG, "registerMusic fallback failed: " + e2.getMessage());
                }
            }
            if (registerInMusic == null) {
                Log.e(TAG, "Registration failed — no token");
                return null;
            }
            Log.d(TAG, "registerEx: " + sendStrMsgAndBinderForStr("registerEx", str, this.exCallbackWrapper.asBinder()));
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("packageName", str);
            jSONObject.put("displayId", 0);
            Log.d(TAG, "registerClientWithRequest: " + sendStrMsgAndBinderForStr("registerClientWithRequest", jSONObject.toString(), registerInMusic.asBinder()));
            this.exCallbackWrapper.setListener("MusicClient", this.musicClientWrapper);
            return registerInMusic;
        } catch (Exception e3) {
            Log.e(TAG, "registerMusic failed: " + e3.getMessage());
            Log.d(TAG, "Exception: " + Log.getStackTraceString(e3));
            return null;
        }
    }

    public boolean requestPlay(Object obj) {
        IMediaCenterSvc iMediaCenterSvc = this.mediaCenterSvc;
        if (iMediaCenterSvc != null && obj != null) {
            try {
                return iMediaCenterSvc.requestPlay((IMediaCenterClientToken) obj);
            } catch (Exception e2) {
                Log.w(TAG, "requestPlay failed: " + e2.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean unregister(Object obj) {
        IMediaCenterSvc iMediaCenterSvc = this.mediaCenterSvc;
        if (iMediaCenterSvc != null && obj != null) {
            try {
                return iMediaCenterSvc.unregister((IMediaCenterClientToken) obj);
            } catch (Exception e2) {
                Log.w(TAG, "unregister failed: " + e2.getMessage());
                return false;
            }
        }
        return false;
    }

    public void updateCurrentLyric(Object obj, String str) {
        IMediaCenterSvc iMediaCenterSvc = this.mediaCenterSvc;
        if (iMediaCenterSvc == null || obj == null) {
            return;
        }
        try {
            iMediaCenterSvc.updateCurrentLyric((IMediaCenterClientToken) obj, str);
        } catch (Exception e2) {
            Log.d(TAG, "updateCurrentLyric failed: " + e2.getMessage());
        }
    }

    public void updateCurrentProgress(Object obj, long j) {
        IMediaCenterSvc iMediaCenterSvc = this.mediaCenterSvc;
        if (iMediaCenterSvc == null || obj == null) {
            return;
        }
        try {
            iMediaCenterSvc.updateCurrentProgress((IMediaCenterClientToken) obj, j);
        } catch (Exception e2) {
            Log.d(TAG, "updateCurrentProgress failed: " + e2.getMessage());
        }
    }

    public void updateCurrentSourceType(Object obj, int i) {
        IMediaCenterSvc iMediaCenterSvc = this.mediaCenterSvc;
        if (iMediaCenterSvc == null || obj == null) {
            return;
        }
        try {
            iMediaCenterSvc.updateCurrentSourceType((IMediaCenterClientToken) obj, i);
        } catch (Exception e2) {
            Log.w(TAG, "updateCurrentSourceType failed: " + e2.getMessage());
        }
    }

    public void updateDisplayId(Object obj, int i) {
        if (obj == null || this.easService == null) {
            return;
        }
        sendStrMsgAndBinderForStr("updateDisplayId", String.valueOf(i), ((IMediaCenterClientToken) obj).asBinder());
    }

    public void updateErrorMsg(Object obj, int i, String str) {
        IMediaCenterSvc iMediaCenterSvc = this.mediaCenterSvc;
        if (iMediaCenterSvc == null || obj == null) {
            return;
        }
        try {
            iMediaCenterSvc.updateErrorMsg((IMediaCenterClientToken) obj, i, str);
        } catch (Exception e2) {
            Log.d(TAG, "updateErrorMsg failed: " + e2.getMessage());
        }
    }

    public boolean updateMediaSourceTypeList(Object obj, int[] iArr) {
        IMediaCenterSvc iMediaCenterSvc = this.mediaCenterSvc;
        if (iMediaCenterSvc != null && obj != null) {
            try {
                iMediaCenterSvc.updateMediaSourceTypeList((IMediaCenterClientToken) obj, iArr);
                return true;
            } catch (Exception e2) {
                Log.w(TAG, "updateMediaSourceTypeList failed: " + e2.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean updateMusicPlaybackState(Object obj, MusicPlaybackInfo musicPlaybackInfo) {
        if (obj != null && musicPlaybackInfo != null) {
            IMediaCenterSvc iMediaCenterSvc = this.mediaCenterSvc;
            if (iMediaCenterSvc == null) {
                Log.e(TAG, "updateMusicPlaybackState: unbind media center service");
                return false;
            }
            try {
                boolean updateMusicPlaybackState = iMediaCenterSvc.updateMusicPlaybackState((IMediaCenterClientToken) obj, createPlaybackInfoBinder(this.musicClient));
                Log.d(TAG, "updateMusicPlaybackState: " + updateMusicPlaybackState + " [" + musicPlaybackInfo.getTitle() + " / " + musicPlaybackInfo.getArtist() + "] status=" + musicPlaybackInfo.getPlaybackStatus());
                return updateMusicPlaybackState;
            } catch (Exception e2) {
                Log.e(TAG, "updateMusicPlaybackState failed: " + e2.getMessage());
            }
        }
        return false;
    }

    public void updatePlayState(String str, int i, int i2) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("packageName", str);
            jSONObject.put("displayId", i2);
            jSONObject.put("playState", i);
            sendMsg(new EASFrameworkMessage(MEDIACENTER_SERVICE, MEDIACENTER_MODULE, "updatePlayState", jSONObject.toString().getBytes(), this.mAttachParam));
        } catch (Exception e2) {
            Log.d(TAG, "updatePlayState failed: " + e2.getMessage());
        }
    }
}
