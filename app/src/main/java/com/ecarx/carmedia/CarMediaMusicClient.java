package com.ecarx.carmedia;

import android.util.Log;
import com.ecarx.eas.sdk.mediacenter.MusicClient;
import com.ecarx.eas.sdk.mediacenter.MusicPlaybackInfo;

/* loaded from: classes3.dex */
public class CarMediaMusicClient extends MusicClient {
    private static final String TAG = "CarMediaMusicClient";
    private volatile Controller mController;
    private MusicPlaybackInfo mPlaybackInfo;

    public interface Controller {
        void onForwardCommand();

        void onNextCommand();

        void onPauseCommand();

        void onPlayCommand();

        void onPreviousCommand();

        void onRewindCommand();

        void onSeekCommand(long j);

        void onStopCommand();
    }

    public void setController(Controller controller) {
        this.mController = controller;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private boolean forward(String action) {
        char c;
        Controller c2 = this.mController;
        if (c2 == null) {
            Log.w(TAG, "No controller attached, cannot handle: " + action);
            return false;
        }
        try {
            switch (action.hashCode()) {
                case -1880989509:
                    if (action.equals("REWIND")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case -491148553:
                    if (action.equals("PREVIOUS")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 2392819:
                    if (action.equals("NEXT")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 2458420:
                    if (action.equals("PLAY")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 2555906:
                    if (action.equals("STOP")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 75902422:
                    if (action.equals("PAUSE")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 1374124482:
                    if (action.equals("FAST_FORWARD")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    c2.onPlayCommand();
                    return true;
                case 1:
                    c2.onPauseCommand();
                    return true;
                case 2:
                    c2.onNextCommand();
                    return true;
                case 3:
                    c2.onPreviousCommand();
                    return true;
                case 4:
                    c2.onStopCommand();
                    return true;
                case MusicClient.TYPE_MEDIA_INNER /* 5 */:
                    c2.onRewindCommand();
                    return true;
                case MusicClient.TYPE_GESTURE /* 6 */:
                    c2.onForwardCommand();
                    return true;
                default:
                    return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "forward " + action + " failed", e);
            return false;
        }
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean onPlay() {
        return forward("PLAY");
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean onPause() {
        return forward("PAUSE");
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean onNext() {
        return forward("NEXT");
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean onPrevious() {
        return forward("PREVIOUS");
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean onExit() {
        return forward("STOP");
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean onReplay() {
        return forward("PLAY");
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean onRewind() {
        return forward("REWIND");
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean onForward() {
        return forward("FAST_FORWARD");
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean onMediaForward(boolean z) {
        return z && forward("FAST_FORWARD");
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean onMediaRewind(boolean z) {
        return z && forward("REWIND");
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public void onSeek(long j) {
        Controller c = this.mController;
        if (c != null) {
            try {
                c.onSeekCommand(j);
            } catch (Exception e) {
                Log.e(TAG, "onSeek failed", e);
            }
        }
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean onSourceSelected(int i) {
        if (i != 6) {
            return true;
        }
        return forward("PLAY");
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean ctrlPlayMediaList(int i) {
        return forward("PLAY");
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean ctrlPauseMediaList(int i) {
        return forward("PAUSE");
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean onCollect(int i, boolean z) {
        return true;
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public int ctrlCollect(int i, boolean z) {
        return 0;
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public void ctrlCollectByUUID(int i, String str, boolean z) {
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean onLoopModeChange(int i) {
        return true;
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean onMediaQualityChange(int i) {
        return true;
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public boolean onSourceChanged(int i, String str) {
        return true;
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public void operationType(int i) {
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public void onMediaCenterFocusChanged(String str) {
        Log.i(TAG, "onMediaCenterFocusChanged: focus -> " + str + " (不反抢,保持空让 XCMedia2 判定自己有焦点)");
        CarMediaService svc = CarMediaService.sInstance;
        if (svc != null) {
            svc.onEasFocusLost();
        }
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public int getCurrentSourceType() {
        return 6;
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public int[] getMediaSourceTypeList() {
        return new int[]{6};
    }

    @Override // com.ecarx.eas.sdk.mediacenter.MusicClient
    public MusicPlaybackInfo getMusicPlaybackInfo() {
        return this.mPlaybackInfo;
    }

    public void setPlaybackInfo(MusicPlaybackInfo musicPlaybackInfo) {
        this.mPlaybackInfo = musicPlaybackInfo;
    }
}
