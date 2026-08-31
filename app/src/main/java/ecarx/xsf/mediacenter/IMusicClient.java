package ecarx.xsf.mediacenter;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import ecarx.xsf.mediacenter.IMusicPlaybackInfo;
import ecarx.xsf.mediacenter.IRecommend;
import ecarx.xsf.mediacenter.ISearchMusicCallback;
import java.util.List;

/* loaded from: classes7.dex */
public interface IMusicClient extends IInterface {
    public static final String DESCRIPTOR = "ecarx.xsf.mediacenter.IMusicClient";

    int ctrlCollect(int i, boolean z) throws RemoteException;

    void ctrlCollectByUUID(int i, String str, boolean z) throws RemoteException;

    boolean ctrlPauseMediaList(int i) throws RemoteException;

    boolean ctrlPlayMediaList(int i) throws RemoteException;

    List getContentList() throws RemoteException;

    long getCurrentProgress() throws RemoteException;

    int getCurrentSourceType() throws RemoteException;

    int[] getMediaSourceTypeList() throws RemoteException;

    IMediaLists getMultiMediaList(int[] iArr) throws RemoteException;

    IMusicPlaybackInfo getMusicPlaybackInfo() throws RemoteException;

    List getPlaylist(int i) throws RemoteException;

    boolean onCancelRecommend(IRecommend iRecommend) throws RemoteException;

    boolean onCollect(int i, boolean z) throws RemoteException;

    boolean onDownload(int i, boolean z) throws RemoteException;

    boolean onExit() throws RemoteException;

    boolean onForward() throws RemoteException;

    boolean onLoopModeChange(int i) throws RemoteException;

    void onMediaCenterFocusChanged(String str) throws RemoteException;

    boolean onMediaForward(boolean z) throws RemoteException;

    boolean onMediaQualityChange(int i) throws RemoteException;

    boolean onMediaRewind(boolean z) throws RemoteException;

    boolean onMediaSelected(IMedia iMedia) throws RemoteException;

    boolean onMediaSelectedPlay(int i, String str) throws RemoteException;

    boolean onNext() throws RemoteException;

    boolean onPause() throws RemoteException;

    boolean onPlay() throws RemoteException;

    boolean onPlayMediaList(int i, int i2) throws RemoteException;

    boolean onPlayRecommend(IRecommend iRecommend) throws RemoteException;

    boolean onPrevious() throws RemoteException;

    boolean onReplay() throws RemoteException;

    boolean onRewind() throws RemoteException;

    void onSearchMusic(String str, String str2, int i, boolean z, boolean z2, ISearchMusicCallback iSearchMusicCallback) throws RemoteException;

    boolean onSeek(long j) throws RemoteException;

    boolean onSourceChanged(int i, String str) throws RemoteException;

    boolean onSourceSelected(int i) throws RemoteException;

    void operationType(int i) throws RemoteException;

    boolean selectListMediaPlay(int i, int i2, String str) throws RemoteException;

    public static class Default implements IMusicClient {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public int ctrlCollect(int i, boolean z) throws RemoteException {
            return 0;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public void ctrlCollectByUUID(int i, String str, boolean z) throws RemoteException {
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean ctrlPauseMediaList(int i) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean ctrlPlayMediaList(int i) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public List getContentList() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public long getCurrentProgress() throws RemoteException {
            return 0L;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public int getCurrentSourceType() throws RemoteException {
            return 0;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public int[] getMediaSourceTypeList() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public IMediaLists getMultiMediaList(int[] iArr) throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public IMusicPlaybackInfo getMusicPlaybackInfo() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public List getPlaylist(int i) throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onCancelRecommend(IRecommend iRecommend) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onCollect(int i, boolean z) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onDownload(int i, boolean z) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onExit() throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onForward() throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onLoopModeChange(int i) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public void onMediaCenterFocusChanged(String str) throws RemoteException {
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onMediaForward(boolean z) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onMediaQualityChange(int i) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onMediaRewind(boolean z) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onMediaSelected(IMedia iMedia) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onMediaSelectedPlay(int i, String str) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onNext() throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onPause() throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onPlay() throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onPlayMediaList(int i, int i2) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onPlayRecommend(IRecommend iRecommend) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onPrevious() throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onReplay() throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onRewind() throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public void onSearchMusic(String str, String str2, int i, boolean z, boolean z2, ISearchMusicCallback iSearchMusicCallback) throws RemoteException {
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onSeek(long j) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onSourceChanged(int i, String str) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean onSourceSelected(int i) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public void operationType(int i) throws RemoteException {
        }

        @Override // ecarx.xsf.mediacenter.IMusicClient
        public boolean selectListMediaPlay(int i, int i2, String str) throws RemoteException {
            return false;
        }
    }

    public static abstract class Stub extends Binder implements IMusicClient {
        static final int TRANSACTION_ctrlCollect = 32;
        static final int TRANSACTION_ctrlCollectByUUID = 34;
        static final int TRANSACTION_ctrlPauseMediaList = 31;
        static final int TRANSACTION_ctrlPlayMediaList = 30;
        static final int TRANSACTION_getContentList = 28;
        static final int TRANSACTION_getCurrentProgress = 13;
        static final int TRANSACTION_getCurrentSourceType = 12;
        static final int TRANSACTION_getMediaSourceTypeList = 11;
        static final int TRANSACTION_getMultiMediaList = 29;
        static final int TRANSACTION_getMusicPlaybackInfo = 10;
        static final int TRANSACTION_getPlaylist = 14;
        static final int TRANSACTION_onCancelRecommend = 20;
        static final int TRANSACTION_onCollect = 15;
        static final int TRANSACTION_onDownload = 16;
        static final int TRANSACTION_onExit = 26;
        static final int TRANSACTION_onForward = 5;
        static final int TRANSACTION_onLoopModeChange = 7;
        static final int TRANSACTION_onMediaCenterFocusChanged = 25;
        static final int TRANSACTION_onMediaForward = 22;
        static final int TRANSACTION_onMediaQualityChange = 24;
        static final int TRANSACTION_onMediaRewind = 23;
        static final int TRANSACTION_onMediaSelected = 9;
        static final int TRANSACTION_onMediaSelectedPlay = 21;
        static final int TRANSACTION_onNext = 3;
        static final int TRANSACTION_onPause = 2;
        static final int TRANSACTION_onPlay = 1;
        static final int TRANSACTION_onPlayMediaList = 36;
        static final int TRANSACTION_onPlayRecommend = 19;
        static final int TRANSACTION_onPrevious = 4;
        static final int TRANSACTION_onReplay = 18;
        static final int TRANSACTION_onRewind = 6;
        static final int TRANSACTION_onSearchMusic = 35;
        static final int TRANSACTION_onSeek = 37;
        static final int TRANSACTION_onSourceChanged = 17;
        static final int TRANSACTION_onSourceSelected = 8;
        static final int TRANSACTION_operationType = 33;
        static final int TRANSACTION_selectListMediaPlay = 27;

        public static class Proxy implements IMusicClient {
            private IBinder mRemote;

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public int ctrlCollect(int i, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(Stub.TRANSACTION_ctrlCollect, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public void ctrlCollectByUUID(int i, String str, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(Stub.TRANSACTION_ctrlCollectByUUID, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean ctrlPauseMediaList(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_ctrlPauseMediaList, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean ctrlPlayMediaList(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_ctrlPlayMediaList, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public List getContentList() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getContentList, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readArrayList(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public long getCurrentProgress() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getCurrentProgress, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readLong();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public int getCurrentSourceType() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getCurrentSourceType, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return IMusicClient.DESCRIPTOR;
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public int[] getMediaSourceTypeList() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getMediaSourceTypeList, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createIntArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public IMediaLists getMultiMediaList(int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeIntArray(iArr);
                    this.mRemote.transact(Stub.TRANSACTION_getMultiMediaList, obtain, obtain2, 0);
                    obtain2.readException();
                    return (IMediaLists) _Parcel.readTypedObject(obtain2, IMediaLists.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public IMusicPlaybackInfo getMusicPlaybackInfo() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getMusicPlaybackInfo, obtain, obtain2, 0);
                    obtain2.readException();
                    return IMusicPlaybackInfo.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public List getPlaylist(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_getPlaylist, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readArrayList(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onCancelRecommend(IRecommend iRecommend) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeStrongInterface(iRecommend);
                    this.mRemote.transact(Stub.TRANSACTION_onCancelRecommend, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onCollect(int i, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(Stub.TRANSACTION_onCollect, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onDownload(int i, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(Stub.TRANSACTION_onDownload, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onExit() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onExit, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onForward() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onLoopModeChange(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_onLoopModeChange, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public void onMediaCenterFocusChanged(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(Stub.TRANSACTION_onMediaCenterFocusChanged, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onMediaForward(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(Stub.TRANSACTION_onMediaForward, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onMediaQualityChange(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_onMediaQualityChange, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onMediaRewind(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(Stub.TRANSACTION_onMediaRewind, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onMediaSelected(IMedia iMedia) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    _Parcel.writeTypedObject(obtain, iMedia, 0);
                    this.mRemote.transact(Stub.TRANSACTION_onMediaSelected, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onMediaSelectedPlay(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    this.mRemote.transact(Stub.TRANSACTION_onMediaSelectedPlay, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onNext() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onPause() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onPlay() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onPlayMediaList(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(Stub.TRANSACTION_onPlayMediaList, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onPlayRecommend(IRecommend iRecommend) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeStrongInterface(iRecommend);
                    this.mRemote.transact(Stub.TRANSACTION_onPlayRecommend, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onPrevious() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onReplay() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onReplay, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onRewind() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public void onSearchMusic(String str, String str2, int i, boolean z, boolean z2, ISearchMusicCallback iSearchMusicCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    int i2 = 1;
                    obtain.writeInt(z ? 1 : 0);
                    if (!z2) {
                        i2 = 0;
                    }
                    obtain.writeInt(i2);
                    obtain.writeStrongInterface(iSearchMusicCallback);
                    this.mRemote.transact(Stub.TRANSACTION_onSearchMusic, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onSeek(long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeLong(j);
                    this.mRemote.transact(Stub.TRANSACTION_onSeek, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onSourceChanged(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    this.mRemote.transact(Stub.TRANSACTION_onSourceChanged, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean onSourceSelected(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_onSourceSelected, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public void operationType(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_operationType, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicClient
            public boolean selectListMediaPlay(int i, int i2, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicClient.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeString(str);
                    this.mRemote.transact(Stub.TRANSACTION_selectListMediaPlay, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IMusicClient.DESCRIPTOR);
        }

        public static IMusicClient asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IMusicClient.DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMusicClient)) ? new Proxy(iBinder) : (IMusicClient) queryLocalInterface;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IMusicClient.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IMusicClient.DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    boolean onPlay = onPlay();
                    parcel2.writeNoException();
                    parcel2.writeInt(onPlay ? 1 : 0);
                    return true;
                case 2:
                    boolean onPause = onPause();
                    parcel2.writeNoException();
                    parcel2.writeInt(onPause ? 1 : 0);
                    return true;
                case 3:
                    boolean onNext = onNext();
                    parcel2.writeNoException();
                    parcel2.writeInt(onNext ? 1 : 0);
                    return true;
                case 4:
                    boolean onPrevious = onPrevious();
                    parcel2.writeNoException();
                    parcel2.writeInt(onPrevious ? 1 : 0);
                    return true;
                case 5:
                    boolean onForward = onForward();
                    parcel2.writeNoException();
                    parcel2.writeInt(onForward ? 1 : 0);
                    return true;
                case 6:
                    boolean onRewind = onRewind();
                    parcel2.writeNoException();
                    parcel2.writeInt(onRewind ? 1 : 0);
                    return true;
                case TRANSACTION_onLoopModeChange /* 7 */:
                    boolean onLoopModeChange = onLoopModeChange(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(onLoopModeChange ? 1 : 0);
                    return true;
                case TRANSACTION_onSourceSelected /* 8 */:
                    boolean onSourceSelected = onSourceSelected(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(onSourceSelected ? 1 : 0);
                    return true;
                case TRANSACTION_onMediaSelected /* 9 */:
                    boolean onMediaSelected = onMediaSelected((IMedia) _Parcel.readTypedObject(parcel, IMedia.CREATOR));
                    parcel2.writeNoException();
                    parcel2.writeInt(onMediaSelected ? 1 : 0);
                    return true;
                case TRANSACTION_getMusicPlaybackInfo /* 10 */:
                    IMusicPlaybackInfo musicPlaybackInfo = getMusicPlaybackInfo();
                    parcel2.writeNoException();
                    parcel2.writeStrongInterface(musicPlaybackInfo);
                    return true;
                case TRANSACTION_getMediaSourceTypeList /* 11 */:
                    int[] mediaSourceTypeList = getMediaSourceTypeList();
                    parcel2.writeNoException();
                    parcel2.writeIntArray(mediaSourceTypeList);
                    return true;
                case TRANSACTION_getCurrentSourceType /* 12 */:
                    int currentSourceType = getCurrentSourceType();
                    parcel2.writeNoException();
                    parcel2.writeInt(currentSourceType);
                    return true;
                case TRANSACTION_getCurrentProgress /* 13 */:
                    long currentProgress = getCurrentProgress();
                    parcel2.writeNoException();
                    parcel2.writeLong(currentProgress);
                    return true;
                case TRANSACTION_getPlaylist /* 14 */:
                    List playlist = getPlaylist(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeList(playlist);
                    return true;
                case TRANSACTION_onCollect /* 15 */:
                    boolean onCollect = onCollect(parcel.readInt(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(onCollect ? 1 : 0);
                    return true;
                case TRANSACTION_onDownload /* 16 */:
                    boolean onDownload = onDownload(parcel.readInt(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(onDownload ? 1 : 0);
                    return true;
                case TRANSACTION_onSourceChanged /* 17 */:
                    boolean onSourceChanged = onSourceChanged(parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(onSourceChanged ? 1 : 0);
                    return true;
                case TRANSACTION_onReplay /* 18 */:
                    boolean onReplay = onReplay();
                    parcel2.writeNoException();
                    parcel2.writeInt(onReplay ? 1 : 0);
                    return true;
                case TRANSACTION_onPlayRecommend /* 19 */:
                    boolean onPlayRecommend = onPlayRecommend(IRecommend.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(onPlayRecommend ? 1 : 0);
                    return true;
                case TRANSACTION_onCancelRecommend /* 20 */:
                    boolean onCancelRecommend = onCancelRecommend(IRecommend.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(onCancelRecommend ? 1 : 0);
                    return true;
                case TRANSACTION_onMediaSelectedPlay /* 21 */:
                    boolean onMediaSelectedPlay = onMediaSelectedPlay(parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(onMediaSelectedPlay ? 1 : 0);
                    return true;
                case TRANSACTION_onMediaForward /* 22 */:
                    boolean onMediaForward = onMediaForward(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(onMediaForward ? 1 : 0);
                    return true;
                case TRANSACTION_onMediaRewind /* 23 */:
                    boolean onMediaRewind = onMediaRewind(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(onMediaRewind ? 1 : 0);
                    return true;
                case TRANSACTION_onMediaQualityChange /* 24 */:
                    boolean onMediaQualityChange = onMediaQualityChange(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(onMediaQualityChange ? 1 : 0);
                    return true;
                case TRANSACTION_onMediaCenterFocusChanged /* 25 */:
                    onMediaCenterFocusChanged(parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case TRANSACTION_onExit /* 26 */:
                    boolean onExit = onExit();
                    parcel2.writeNoException();
                    parcel2.writeInt(onExit ? 1 : 0);
                    return true;
                case TRANSACTION_selectListMediaPlay /* 27 */:
                    boolean selectListMediaPlay = selectListMediaPlay(parcel.readInt(), parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(selectListMediaPlay ? 1 : 0);
                    return true;
                case TRANSACTION_getContentList /* 28 */:
                    List contentList = getContentList();
                    parcel2.writeNoException();
                    parcel2.writeList(contentList);
                    return true;
                case TRANSACTION_getMultiMediaList /* 29 */:
                    IMediaLists multiMediaList = getMultiMediaList(parcel.createIntArray());
                    parcel2.writeNoException();
                    _Parcel.writeTypedObject(parcel2, multiMediaList, 1);
                    return true;
                case TRANSACTION_ctrlPlayMediaList /* 30 */:
                    boolean ctrlPlayMediaList = ctrlPlayMediaList(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(ctrlPlayMediaList ? 1 : 0);
                    return true;
                case TRANSACTION_ctrlPauseMediaList /* 31 */:
                    boolean ctrlPauseMediaList = ctrlPauseMediaList(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(ctrlPauseMediaList ? 1 : 0);
                    return true;
                case TRANSACTION_ctrlCollect /* 32 */:
                    int ctrlCollect = ctrlCollect(parcel.readInt(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeInt(ctrlCollect);
                    return true;
                case TRANSACTION_operationType /* 33 */:
                    operationType(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case TRANSACTION_ctrlCollectByUUID /* 34 */:
                    ctrlCollectByUUID(parcel.readInt(), parcel.readString(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case TRANSACTION_onSearchMusic /* 35 */:
                    onSearchMusic(parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt() != 0, parcel.readInt() != 0, ISearchMusicCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case TRANSACTION_onPlayMediaList /* 36 */:
                    boolean onPlayMediaList = onPlayMediaList(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(onPlayMediaList ? 1 : 0);
                    return true;
                case TRANSACTION_onSeek /* 37 */:
                    boolean onSeek = onSeek(parcel.readLong());
                    parcel2.writeNoException();
                    parcel2.writeInt(onSeek ? 1 : 0);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    public static class _Parcel {
        public static <T> T readTypedObject(Parcel parcel, Parcelable.Creator<T> creator) {
            if (parcel.readInt() != 0) {
                return creator.createFromParcel(parcel);
            }
            return null;
        }

        public static <T extends Parcelable> void writeTypedObject(Parcel parcel, T t, int i) {
            if (t == null) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(1);
                t.writeToParcel(parcel, i);
            }
        }
    }
}
