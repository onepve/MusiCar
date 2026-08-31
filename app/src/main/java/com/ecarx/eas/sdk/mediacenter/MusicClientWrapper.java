package com.ecarx.eas.sdk.mediacenter;

import android.os.IBinder;
import android.util.Log;
import com.ecarx.eas.sdk.mediacenter.ExCallbackWrapper;
import com.ecarx.eas.xsf.mediacenter.IExContent;
import ecarx.xsf.mediacenter.IMedia;
import ecarx.xsf.mediacenter.IMediaLists;
import ecarx.xsf.mediacenter.IMusicClient;
import ecarx.xsf.mediacenter.IMusicPlaybackInfo;
import ecarx.xsf.mediacenter.IRecommend;
import ecarx.xsf.mediacenter.ISearchMusicCallback;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes4.dex */
public class MusicClientWrapper extends IMusicClient.Stub implements ExCallbackWrapper.Action {
    private static final String TAG = "MusicClientWrapper";
    private final MusicClient mOriginClazz;

    public MusicClientWrapper(MusicClient musicClient) {
        this.mOriginClazz = musicClient;
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public int ctrlCollect(int i, boolean z) {
        MusicClient musicClient = this.mOriginClazz;
        if (musicClient != null) {
            return musicClient.ctrlCollect(i, z);
        }
        return -1;
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public void ctrlCollectByUUID(int i, String str, boolean z) {
        MusicClient musicClient = this.mOriginClazz;
        if (musicClient != null) {
            musicClient.ctrlCollectByUUID(i, str, z);
        }
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean ctrlPauseMediaList(int i) {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.ctrlPauseMediaList(i);
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean ctrlPlayMediaList(int i) {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.ctrlPlayMediaList(i);
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public List getContentList() {
        MusicClient musicClient = this.mOriginClazz;
        if (musicClient != null) {
            return musicClient.getContentList();
        }
        return null;
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public long getCurrentProgress() {
        MusicClient musicClient = this.mOriginClazz;
        if (musicClient != null) {
            return musicClient.getCurrentProgress();
        }
        return 0L;
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public int getCurrentSourceType() {
        MusicClient musicClient = this.mOriginClazz;
        if (musicClient != null) {
            return musicClient.getCurrentSourceType();
        }
        return 0;
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public int[] getMediaSourceTypeList() {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null ? musicClient.getMediaSourceTypeList() : new int[0];
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public IMediaLists getMultiMediaList(int[] iArr) {
        return null;
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public IMusicPlaybackInfo getMusicPlaybackInfo() {
        MusicClient musicClient = this.mOriginClazz;
        if (musicClient != null) {
            return MediaCenterAPI.createPlaybackInfoBinder(musicClient);
        }
        return null;
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public List getPlaylist(int i) {
        MusicClient musicClient = this.mOriginClazz;
        if (musicClient != null) {
            return musicClient.getPlaylist(i);
        }
        return null;
    }

    @Override // com.ecarx.eas.sdk.mediacenter.ExCallbackWrapper.Action
    public String onAction(int i, String str, String str2, IBinder iBinder) {
        Log.d(TAG, "onAction:" + i + "," + str + "," + str2 + "," + iBinder);
        if (i != 5 || this.mOriginClazz == null) {
            return null;
        }
        try {
            try {
                long optLong = new JSONObject(str2).optLong("progress", -1L);
                if (optLong < 0) {
                    return null;
                }
                this.mOriginClazz.onSeek(optLong);
                return null;
            } catch (NumberFormatException e) {
                Log.w(TAG, "onSeek parse error: " + str2);
                return null;
            }
        } catch (Exception e2) {
            this.mOriginClazz.onSeek(Long.parseLong(str2));
            return null;
        }
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onCancelRecommend(IRecommend iRecommend) {
        return false;
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onCollect(int i, boolean z) {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onCollect(i, z);
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onDownload(int i, boolean z) {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onDownload(i, z);
    }

    @Override // com.ecarx.eas.sdk.mediacenter.ExCallbackWrapper.Action
    public IExContent onExAction(int i, String str, String str2, IExContent iExContent, IBinder iBinder) {
        Log.d(TAG, "onExAction:" + i + "," + str + "," + str2);
        return null;
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onExit() {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onExit();
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onForward() {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onForward();
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onLoopModeChange(int i) {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onLoopModeChange(i);
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public void onMediaCenterFocusChanged(String str) {
        MusicClient musicClient = this.mOriginClazz;
        if (musicClient != null) {
            musicClient.onMediaCenterFocusChanged(str);
        }
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onMediaForward(boolean z) {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onMediaForward(z);
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onMediaQualityChange(int i) {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onMediaQualityChange(i);
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onMediaRewind(boolean z) {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onMediaRewind(z);
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onMediaSelected(IMedia iMedia) {
        return false;
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onMediaSelectedPlay(int i, String str) {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onMediaSelectedPlay(i, str);
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onNext() {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onNext();
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onPause() {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onPause();
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onPlay() {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onPlay();
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onPlayMediaList(int i, int i2) {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onPlayMediaList(i, i2);
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onPlayRecommend(IRecommend iRecommend) {
        return false;
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onPrevious() {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onPrevious();
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onReplay() {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onReplay();
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onRewind() {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onRewind();
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public void onSearchMusic(String str, String str2, int i, boolean z, boolean z2, ISearchMusicCallback iSearchMusicCallback) {
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onSeek(long j) {
        MusicClient musicClient = this.mOriginClazz;
        if (musicClient == null) {
            return false;
        }
        musicClient.onSeek(j);
        return true;
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onSourceChanged(int i, String str) {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onSourceChanged(i, str);
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean onSourceSelected(int i) {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.onSourceSelected(i);
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public void operationType(int i) {
        MusicClient musicClient = this.mOriginClazz;
        if (musicClient != null) {
            musicClient.operationType(i);
        }
    }

    @Override // ecarx.xsf.mediacenter.IMusicClient
    public boolean selectListMediaPlay(int i, int i2, String str) {
        MusicClient musicClient = this.mOriginClazz;
        return musicClient != null && musicClient.selectListMediaPlay(i, i2, str);
    }
}
