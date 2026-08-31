package com.ecarx.carmedia;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

/* loaded from: classes3.dex */
public class KuwoAidlClient {
    private static final String DESCRIPTOR = "cn.kuwo.service.remote.AIDLPlayContentInterface";
    private static final String PKG_CAR = "cn.kuwo.kwmusiccar";
    private static final String PKG_CAS = "cn.kuwo.kwmusiccas";
    private static final int PLAYER_ID_MUSIC = 1;
    private static final long POLL_INTERVAL_MS = 1000;
    private static final long REBIND_RETRY_MS = 3000;
    private static final String SVC = "cn.kuwo.service.remote.RemoteService";
    private static final String TAG = "KuwoAidl";
    private static final int TRANSACTION_getCurrentPos = 13;
    private static final int TRANSACTION_getDuration = 12;
    private static final int TRANSACTION_getStatus = 11;
    private volatile boolean mAvailable;
    private volatile boolean mBound;
    private final Context mContext;
    private volatile OnPosListener mOnPos;
    private Handler mPollHandler;
    private HandlerThread mPollThread;
    private volatile IBinder mRemote;
    private volatile String mActivePkg = null;
    private final ServiceConnection mConn = new ServiceConnection() { // from class: com.ecarx.carmedia.KuwoAidlClient.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(KuwoAidlClient.TAG, "onServiceConnected: " + name);
            KuwoAidlClient.this.mRemote = service;
            KuwoAidlClient.this.mBound = true;
            if (KuwoAidlClient.this.mPollHandler != null && !KuwoAidlClient.this.mStopped) {
                KuwoAidlClient.this.mPollHandler.removeCallbacks(KuwoAidlClient.this.pollRunnable);
                KuwoAidlClient.this.mPollHandler.post(KuwoAidlClient.this.pollRunnable);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            Log.d(KuwoAidlClient.TAG, "onServiceDisconnected");
            KuwoAidlClient.this.mRemote = null;
            KuwoAidlClient.this.mBound = false;
            KuwoAidlClient.this.mAvailable = false;
            KuwoAidlClient.this.scheduleRebind();
        }
    };
    private volatile long lastPosMs = -1;
    private volatile long lastPosTimeMs = 0;
    private volatile boolean mStopped = false;
    private volatile boolean mHasPosOnce = false;
    private final Runnable pollRunnable = new Runnable() { // from class: com.ecarx.carmedia.KuwoAidlClient.2
        @Override // java.lang.Runnable
        public void run() {
            if (!KuwoAidlClient.this.mStopped) {
                KuwoAidlClient.this.pollOnce();
                if (KuwoAidlClient.this.mPollHandler != null && !KuwoAidlClient.this.mStopped) {
                    KuwoAidlClient.this.mPollHandler.postDelayed(this, KuwoAidlClient.POLL_INTERVAL_MS);
                }
            }
        }
    };

    public interface OnPosListener {
        void onPosReady();
    }

    public void setOnPosListener(OnPosListener l) {
        this.mOnPos = l;
    }

    public KuwoAidlClient(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public synchronized void start() {
        this.mStopped = false;
        if (this.mPollThread == null) {
            this.mPollThread = new HandlerThread("kuwo-aidl-poll");
            this.mPollThread.start();
            this.mPollHandler = new Handler(this.mPollThread.getLooper());
        }
        String pkg = findInstalledPkg();
        if (pkg == null) {
            Log.d(TAG, "start: kuwo not installed, skip");
            return;
        }
        this.mActivePkg = pkg;
        if (!this.mBound && this.mRemote == null) {
            bindTo(this.mActivePkg);
        }
    }

    private void bindTo(String pkg) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(pkg, SVC));
            boolean ok = this.mContext.bindService(intent, this.mConn, 1);
            Log.d(TAG, "start: bindService(" + pkg + ")=" + ok);
            this.mBound = ok;
            if (!ok) {
                scheduleRebind();
            }
        } catch (Exception e) {
            Log.w(TAG, "start bind error: " + e.getMessage());
            scheduleRebind();
        }
    }

    public synchronized void stop() {
        this.mStopped = true;
        if (this.mPollHandler != null) {
            this.mPollHandler.removeCallbacksAndMessages(null);
        }
        if (this.mBound) {
            try {
                this.mContext.unbindService(this.mConn);
            } catch (Exception e) {
            }
        }
        this.mBound = false;
        this.mRemote = null;
        this.mAvailable = false;
        this.lastPosMs = -1L;
        this.lastPosTimeMs = 0L;
        this.mHasPosOnce = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleRebind() {
        if (this.mPollHandler == null || this.mStopped) {
            return;
        }
        this.mPollHandler.postDelayed(new Runnable() { // from class: com.ecarx.carmedia.KuwoAidlClient.3
            @Override // java.lang.Runnable
            public void run() {
                if (KuwoAidlClient.this.mStopped || KuwoAidlClient.this.mBound || KuwoAidlClient.this.mRemote != null || KuwoAidlClient.this.findInstalledPkg() == null || KuwoAidlClient.this.mActivePkg == null) {
                    return;
                }
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(KuwoAidlClient.this.mActivePkg, KuwoAidlClient.SVC));
                    boolean ok = KuwoAidlClient.this.mContext.bindService(intent, KuwoAidlClient.this.mConn, 1);
                    Log.d(KuwoAidlClient.TAG, "rebind attempt(" + KuwoAidlClient.this.mActivePkg + ")=" + ok);
                    KuwoAidlClient.this.mBound = ok;
                    if (!ok) {
                        KuwoAidlClient.this.scheduleRebind();
                    }
                } catch (Exception e) {
                    KuwoAidlClient.this.scheduleRebind();
                }
            }
        }, REBIND_RETRY_MS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pollOnce() {
        OnPosListener l;
        IBinder remote = this.mRemote;
        if (remote == null) {
            this.mAvailable = false;
            return;
        }
        try {
            int pos = transactInt(remote, TRANSACTION_getCurrentPos, 1);
            if (pos >= 0) {
                boolean firstValid = !this.mHasPosOnce;
                this.lastPosMs = pos;
                this.lastPosTimeMs = SystemClock.elapsedRealtime();
                this.mAvailable = true;
                this.mHasPosOnce = true;
                Log.d(TAG, "pollOnce: pos=" + pos + "ms");
                if (firstValid && (l = this.mOnPos) != null) {
                    l.onPosReady();
                }
                return;
            }
            this.mAvailable = false;
            Log.w(TAG, "pollOnce: pos<0 (" + pos + ")");
        } catch (Exception e) {
            this.mAvailable = false;
            Log.w(TAG, "pollOnce error: " + e.getMessage());
        }
    }

    private static int transactInt(IBinder remote, int code, int arg) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeInt(arg);
            remote.transact(code, data, reply, 0);
            reply.readException();
            return reply.readInt();
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    public long getEstimatedPosMs() {
        if (!this.mAvailable || this.lastPosMs < 0) {
            return -1L;
        }
        long now = SystemClock.elapsedRealtime();
        if (this.lastPosTimeMs <= 0) {
            return Math.max(0L, this.lastPosMs);
        }
        long est = this.lastPosMs + (now - this.lastPosTimeMs);
        return Math.max(0L, est);
    }

    public boolean isAvailable() {
        return this.mAvailable;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String findInstalledPkg() {
        PackageManager pm = this.mContext.getPackageManager();
        String[] strArr = {"cn.kuwo.kwmusiccar", "cn.kuwo.kwmusiccas"};
        for (int i = 0; i < 2; i++) {
            String p = strArr[i];
            try {
                pm.getPackageInfo(p, 0);
                return p;
            } catch (Exception e) {
            }
        }
        return null;
    }
}
