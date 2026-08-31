package ecarx.xsf.mediacenter;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import ecarx.xsf.mediacenter.IMediaCenterClientToken;
import ecarx.xsf.mediacenter.IMusicClient;
import ecarx.xsf.mediacenter.IMusicPlaybackInfo;
import ecarx.xsf.mediacenter.IRecommend;
import java.util.List;

/* loaded from: classes7.dex */
public interface IMediaCenterSvc extends IInterface {
    public static final String DESCRIPTOR = "ecarx.xsf.mediacenter.IMediaCenterSvc";

    boolean asyncSendVrChannelResult(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder, String str) throws RemoteException;

    boolean cancelMusicCtrlCapabilityDeclaration(IBinder iBinder) throws RemoteException;

    boolean cancelNewsCtrlCapabilityDeclaration(IBinder iBinder) throws RemoteException;

    boolean cancelRadioCtrlCapabilityDeclaration(IBinder iBinder) throws RemoteException;

    boolean cancelSupportCollectTypes(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException;

    boolean cancelSupportDownloadTypes(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException;

    boolean cancelVrChannelCapability(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder) throws RemoteException;

    void declareMediaCenterCapability(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException;

    boolean declareMusicCtrlCapability(int[] iArr, IBinder iBinder) throws RemoteException;

    boolean declareNewsCtrlCapability(IBinder iBinder) throws RemoteException;

    boolean declareRadioCtrlCapability(int[] iArr, IBinder iBinder) throws RemoteException;

    boolean declareSupportCollectTypes(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException;

    boolean declareSupportDownloadTypes(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException;

    boolean declareVrChannelCapability(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder, IBinder iBinder2) throws RemoteException;

    void declareVrCtrlPriority(String str, int i, IBinder iBinder, IBinder iBinder2, IBinder iBinder3) throws RemoteException;

    IBinder getMediaControlClientApi() throws RemoteException;

    IBinder getMediaControllerApi() throws RemoteException;

    IBinder getStateBinder() throws RemoteException;

    String queryCurrentFocusClient(IMediaCenterClientToken iMediaCenterClientToken) throws RemoteException;

    IMediaCenterClientToken registerInMusic(String str, IMusicClient iMusicClient) throws RemoteException;

    IMediaCenterClientToken registerInNews(String str, IBinder iBinder) throws RemoteException;

    IMediaCenterClientToken registerInVideo(String str, IBinder iBinder) throws RemoteException;

    IMediaCenterClientToken registerMusic(IMusicClient iMusicClient) throws RemoteException;

    IMediaCenterClientToken registerNews(IBinder iBinder) throws RemoteException;

    IMediaCenterClientToken registerVideo(IBinder iBinder) throws RemoteException;

    boolean requestPlay(IMediaCenterClientToken iMediaCenterClientToken) throws RemoteException;

    boolean unregister(IMediaCenterClientToken iMediaCenterClientToken) throws RemoteException;

    void updateCollectMsg(IMediaCenterClientToken iMediaCenterClientToken, int i, String str) throws RemoteException;

    void updateCurrentLyric(IMediaCenterClientToken iMediaCenterClientToken, String str) throws RemoteException;

    void updateCurrentProgress(IMediaCenterClientToken iMediaCenterClientToken, long j) throws RemoteException;

    boolean updateCurrentRecommendInfo(IMediaCenterClientToken iMediaCenterClientToken, IRecommend iRecommend) throws RemoteException;

    void updateCurrentSourceType(IMediaCenterClientToken iMediaCenterClientToken, int i) throws RemoteException;

    void updateErrorMsg(IMediaCenterClientToken iMediaCenterClientToken, int i, String str) throws RemoteException;

    boolean updateMediaContent(IMediaCenterClientToken iMediaCenterClientToken, List list) throws RemoteException;

    void updateMediaList(IMediaCenterClientToken iMediaCenterClientToken, int i, int i2, List list) throws RemoteException;

    void updateMediaPlayList(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder) throws RemoteException;

    void updateMediaSourceTypeList(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException;

    boolean updateMultiMediaList(IMediaCenterClientToken iMediaCenterClientToken, IMediaLists iMediaLists) throws RemoteException;

    boolean updateMusicPlaybackState(IMediaCenterClientToken iMediaCenterClientToken, IMusicPlaybackInfo iMusicPlaybackInfo) throws RemoteException;

    boolean updateNewsPlaybackState(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder) throws RemoteException;

    void updatePlaylist(IMediaCenterClientToken iMediaCenterClientToken, int i, List list) throws RemoteException;

    boolean updateVideoPlaybackState(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder) throws RemoteException;

    public static class Default implements IMediaCenterSvc {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean asyncSendVrChannelResult(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder, String str) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean cancelMusicCtrlCapabilityDeclaration(IBinder iBinder) {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean cancelNewsCtrlCapabilityDeclaration(IBinder iBinder) {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean cancelRadioCtrlCapabilityDeclaration(IBinder iBinder) {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean cancelSupportCollectTypes(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean cancelSupportDownloadTypes(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean cancelVrChannelCapability(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public void declareMediaCenterCapability(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException {
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean declareMusicCtrlCapability(int[] iArr, IBinder iBinder) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean declareNewsCtrlCapability(IBinder iBinder) {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean declareRadioCtrlCapability(int[] iArr, IBinder iBinder) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean declareSupportCollectTypes(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean declareSupportDownloadTypes(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean declareVrChannelCapability(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder, IBinder iBinder2) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public void declareVrCtrlPriority(String str, int i, IBinder iBinder, IBinder iBinder2, IBinder iBinder3) throws RemoteException {
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public IBinder getMediaControlClientApi() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public IBinder getMediaControllerApi() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public IBinder getStateBinder() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public String queryCurrentFocusClient(IMediaCenterClientToken iMediaCenterClientToken) throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public IMediaCenterClientToken registerInMusic(String str, IMusicClient iMusicClient) throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public IMediaCenterClientToken registerInNews(String str, IBinder iBinder) throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public IMediaCenterClientToken registerInVideo(String str, IBinder iBinder) throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public IMediaCenterClientToken registerMusic(IMusicClient iMusicClient) throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public IMediaCenterClientToken registerNews(IBinder iBinder) {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public IMediaCenterClientToken registerVideo(IBinder iBinder) {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean requestPlay(IMediaCenterClientToken iMediaCenterClientToken) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean unregister(IMediaCenterClientToken iMediaCenterClientToken) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public void updateCollectMsg(IMediaCenterClientToken iMediaCenterClientToken, int i, String str) throws RemoteException {
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public void updateCurrentLyric(IMediaCenterClientToken iMediaCenterClientToken, String str) throws RemoteException {
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public void updateCurrentProgress(IMediaCenterClientToken iMediaCenterClientToken, long j) throws RemoteException {
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean updateCurrentRecommendInfo(IMediaCenterClientToken iMediaCenterClientToken, IRecommend iRecommend) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public void updateCurrentSourceType(IMediaCenterClientToken iMediaCenterClientToken, int i) throws RemoteException {
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public void updateErrorMsg(IMediaCenterClientToken iMediaCenterClientToken, int i, String str) throws RemoteException {
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean updateMediaContent(IMediaCenterClientToken iMediaCenterClientToken, List list) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public void updateMediaList(IMediaCenterClientToken iMediaCenterClientToken, int i, int i2, List list) throws RemoteException {
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public void updateMediaPlayList(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder) throws RemoteException {
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public void updateMediaSourceTypeList(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException {
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean updateMultiMediaList(IMediaCenterClientToken iMediaCenterClientToken, IMediaLists iMediaLists) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean updateMusicPlaybackState(IMediaCenterClientToken iMediaCenterClientToken, IMusicPlaybackInfo iMusicPlaybackInfo) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean updateNewsPlaybackState(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder) throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public void updatePlaylist(IMediaCenterClientToken iMediaCenterClientToken, int i, List list) throws RemoteException {
        }

        @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
        public boolean updateVideoPlaybackState(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder) throws RemoteException {
            return false;
        }
    }

    public static abstract class Stub extends Binder implements IMediaCenterSvc {
        static final int TRANSACTION_asyncSendVrChannelResult = 36;
        static final int TRANSACTION_cancelMusicCtrlCapabilityDeclaration = 24;
        static final int TRANSACTION_cancelNewsCtrlCapabilityDeclaration = 28;
        static final int TRANSACTION_cancelRadioCtrlCapabilityDeclaration = 26;
        static final int TRANSACTION_cancelSupportCollectTypes = 16;
        static final int TRANSACTION_cancelSupportDownloadTypes = 18;
        static final int TRANSACTION_cancelVrChannelCapability = 35;
        static final int TRANSACTION_declareMediaCenterCapability = 30;
        static final int TRANSACTION_declareMusicCtrlCapability = 23;
        static final int TRANSACTION_declareNewsCtrlCapability = 27;
        static final int TRANSACTION_declareRadioCtrlCapability = 25;
        static final int TRANSACTION_declareSupportCollectTypes = 15;
        static final int TRANSACTION_declareSupportDownloadTypes = 17;
        static final int TRANSACTION_declareVrChannelCapability = 34;
        static final int TRANSACTION_declareVrCtrlPriority = 22;
        static final int TRANSACTION_getMediaControlClientApi = 32;
        static final int TRANSACTION_getMediaControllerApi = 33;
        static final int TRANSACTION_getStateBinder = 31;
        static final int TRANSACTION_queryCurrentFocusClient = 42;
        static final int TRANSACTION_registerInMusic = 19;
        static final int TRANSACTION_registerInNews = 20;
        static final int TRANSACTION_registerInVideo = 21;
        static final int TRANSACTION_registerMusic = 1;
        static final int TRANSACTION_registerNews = 2;
        static final int TRANSACTION_registerVideo = 3;
        static final int TRANSACTION_requestPlay = 5;
        static final int TRANSACTION_unregister = 4;
        static final int TRANSACTION_updateCollectMsg = 41;
        static final int TRANSACTION_updateCurrentLyric = 14;
        static final int TRANSACTION_updateCurrentProgress = 10;
        static final int TRANSACTION_updateCurrentRecommendInfo = 13;
        static final int TRANSACTION_updateCurrentSourceType = 8;
        static final int TRANSACTION_updateErrorMsg = 37;
        static final int TRANSACTION_updateMediaContent = 38;
        static final int TRANSACTION_updateMediaList = 29;
        static final int TRANSACTION_updateMediaPlayList = 40;
        static final int TRANSACTION_updateMediaSourceTypeList = 7;
        static final int TRANSACTION_updateMultiMediaList = 39;
        static final int TRANSACTION_updateMusicPlaybackState = 6;
        static final int TRANSACTION_updateNewsPlaybackState = 12;
        static final int TRANSACTION_updatePlaylist = 9;
        static final int TRANSACTION_updateVideoPlaybackState = 11;

        public static class Proxy implements IMediaCenterSvc {
            private IBinder mRemote;

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean asyncSendVrChannelResult(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeString(str);
                    this.mRemote.transact(Stub.TRANSACTION_asyncSendVrChannelResult, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean cancelMusicCtrlCapabilityDeclaration(IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_cancelMusicCtrlCapabilityDeclaration, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean cancelNewsCtrlCapabilityDeclaration(IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_cancelNewsCtrlCapabilityDeclaration, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean cancelRadioCtrlCapabilityDeclaration(IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_cancelRadioCtrlCapabilityDeclaration, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean cancelSupportCollectTypes(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeIntArray(iArr);
                    this.mRemote.transact(Stub.TRANSACTION_cancelSupportCollectTypes, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean cancelSupportDownloadTypes(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeIntArray(iArr);
                    this.mRemote.transact(Stub.TRANSACTION_cancelSupportDownloadTypes, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean cancelVrChannelCapability(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_cancelVrChannelCapability, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public void declareMediaCenterCapability(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeIntArray(iArr);
                    this.mRemote.transact(Stub.TRANSACTION_declareMediaCenterCapability, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean declareMusicCtrlCapability(int[] iArr, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeIntArray(iArr);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_declareMusicCtrlCapability, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean declareNewsCtrlCapability(IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_declareNewsCtrlCapability, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean declareRadioCtrlCapability(int[] iArr, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeIntArray(iArr);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_declareRadioCtrlCapability, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean declareSupportCollectTypes(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeIntArray(iArr);
                    this.mRemote.transact(Stub.TRANSACTION_declareSupportCollectTypes, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean declareSupportDownloadTypes(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeIntArray(iArr);
                    this.mRemote.transact(Stub.TRANSACTION_declareSupportDownloadTypes, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean declareVrChannelCapability(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder, IBinder iBinder2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeStrongBinder(iBinder2);
                    this.mRemote.transact(Stub.TRANSACTION_declareVrChannelCapability, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public void declareVrCtrlPriority(String str, int i, IBinder iBinder, IBinder iBinder2, IBinder iBinder3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeStrongBinder(iBinder2);
                    obtain.writeStrongBinder(iBinder3);
                    this.mRemote.transact(Stub.TRANSACTION_declareVrCtrlPriority, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return IMediaCenterSvc.DESCRIPTOR;
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public IBinder getMediaControlClientApi() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getMediaControlClientApi, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readStrongBinder();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public IBinder getMediaControllerApi() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getMediaControllerApi, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readStrongBinder();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public IBinder getStateBinder() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getStateBinder, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readStrongBinder();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public String queryCurrentFocusClient(IMediaCenterClientToken iMediaCenterClientToken) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    this.mRemote.transact(Stub.TRANSACTION_queryCurrentFocusClient, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public IMediaCenterClientToken registerInMusic(String str, IMusicClient iMusicClient) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeStrongInterface(iMusicClient);
                    this.mRemote.transact(Stub.TRANSACTION_registerInMusic, obtain, obtain2, 0);
                    obtain2.readException();
                    return IMediaCenterClientToken.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public IMediaCenterClientToken registerInNews(String str, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_registerInNews, obtain, obtain2, 0);
                    obtain2.readException();
                    return IMediaCenterClientToken.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public IMediaCenterClientToken registerInVideo(String str, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_registerInVideo, obtain, obtain2, 0);
                    obtain2.readException();
                    return IMediaCenterClientToken.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public IMediaCenterClientToken registerMusic(IMusicClient iMusicClient) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMusicClient);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return IMediaCenterClientToken.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public IMediaCenterClientToken registerNews(IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return IMediaCenterClientToken.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public IMediaCenterClientToken registerVideo(IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return IMediaCenterClientToken.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean requestPlay(IMediaCenterClientToken iMediaCenterClientToken) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean unregister(IMediaCenterClientToken iMediaCenterClientToken) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public void updateCollectMsg(IMediaCenterClientToken iMediaCenterClientToken, int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    this.mRemote.transact(Stub.TRANSACTION_updateCollectMsg, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public void updateCurrentLyric(IMediaCenterClientToken iMediaCenterClientToken, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeString(str);
                    this.mRemote.transact(Stub.TRANSACTION_updateCurrentLyric, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public void updateCurrentProgress(IMediaCenterClientToken iMediaCenterClientToken, long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeLong(j);
                    this.mRemote.transact(Stub.TRANSACTION_updateCurrentProgress, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean updateCurrentRecommendInfo(IMediaCenterClientToken iMediaCenterClientToken, IRecommend iRecommend) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeStrongInterface(iRecommend);
                    this.mRemote.transact(Stub.TRANSACTION_updateCurrentRecommendInfo, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public void updateCurrentSourceType(IMediaCenterClientToken iMediaCenterClientToken, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_updateCurrentSourceType, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public void updateErrorMsg(IMediaCenterClientToken iMediaCenterClientToken, int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    this.mRemote.transact(Stub.TRANSACTION_updateErrorMsg, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean updateMediaContent(IMediaCenterClientToken iMediaCenterClientToken, List list) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeList(list);
                    this.mRemote.transact(Stub.TRANSACTION_updateMediaContent, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public void updateMediaList(IMediaCenterClientToken iMediaCenterClientToken, int i, int i2, List list) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeList(list);
                    this.mRemote.transact(Stub.TRANSACTION_updateMediaList, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public void updateMediaPlayList(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_updateMediaPlayList, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public void updateMediaSourceTypeList(IMediaCenterClientToken iMediaCenterClientToken, int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeIntArray(iArr);
                    this.mRemote.transact(Stub.TRANSACTION_updateMediaSourceTypeList, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean updateMultiMediaList(IMediaCenterClientToken iMediaCenterClientToken, IMediaLists iMediaLists) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    _Parcel.writeTypedObject(obtain, iMediaLists, 0);
                    this.mRemote.transact(Stub.TRANSACTION_updateMultiMediaList, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean updateMusicPlaybackState(IMediaCenterClientToken iMediaCenterClientToken, IMusicPlaybackInfo iMusicPlaybackInfo) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeStrongInterface(iMusicPlaybackInfo);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean updateNewsPlaybackState(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_updateNewsPlaybackState, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public void updatePlaylist(IMediaCenterClientToken iMediaCenterClientToken, int i, List list) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeInt(i);
                    obtain.writeList(list);
                    this.mRemote.transact(Stub.TRANSACTION_updatePlaylist, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMediaCenterSvc
            public boolean updateVideoPlaybackState(IMediaCenterClientToken iMediaCenterClientToken, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMediaCenterSvc.DESCRIPTOR);
                    obtain.writeStrongInterface(iMediaCenterClientToken);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_updateVideoPlaybackState, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IMediaCenterSvc.DESCRIPTOR);
        }

        public static IMediaCenterSvc asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IMediaCenterSvc.DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMediaCenterSvc)) ? new Proxy(iBinder) : (IMediaCenterSvc) queryLocalInterface;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IMediaCenterSvc.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IMediaCenterSvc.DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    IMediaCenterClientToken registerMusic = registerMusic(IMusicClient.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeStrongInterface(registerMusic);
                    return true;
                case 2:
                    IMediaCenterClientToken registerNews = registerNews(parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeStrongInterface(registerNews);
                    return true;
                case 3:
                    IMediaCenterClientToken registerVideo = registerVideo(parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeStrongInterface(registerVideo);
                    return true;
                case 4:
                    boolean unregister = unregister(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(unregister ? 1 : 0);
                    return true;
                case 5:
                    boolean requestPlay = requestPlay(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(requestPlay ? 1 : 0);
                    return true;
                case 6:
                    boolean updateMusicPlaybackState = updateMusicPlaybackState(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), IMusicPlaybackInfo.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(updateMusicPlaybackState ? 1 : 0);
                    return true;
                case TRANSACTION_updateMediaSourceTypeList /* 7 */:
                    updateMediaSourceTypeList(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.createIntArray());
                    parcel2.writeNoException();
                    return true;
                case TRANSACTION_updateCurrentSourceType /* 8 */:
                    updateCurrentSourceType(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case TRANSACTION_updatePlaylist /* 9 */:
                    updatePlaylist(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt(), parcel.readArrayList(getClass().getClassLoader()));
                    parcel2.writeNoException();
                    return true;
                case TRANSACTION_updateCurrentProgress /* 10 */:
                    updateCurrentProgress(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.readLong());
                    parcel2.writeNoException();
                    return true;
                case TRANSACTION_updateVideoPlaybackState /* 11 */:
                    boolean updateVideoPlaybackState = updateVideoPlaybackState(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeInt(updateVideoPlaybackState ? 1 : 0);
                    return true;
                case TRANSACTION_updateNewsPlaybackState /* 12 */:
                    boolean updateNewsPlaybackState = updateNewsPlaybackState(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeInt(updateNewsPlaybackState ? 1 : 0);
                    return true;
                case TRANSACTION_updateCurrentRecommendInfo /* 13 */:
                    boolean updateCurrentRecommendInfo = updateCurrentRecommendInfo(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), IRecommend.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(updateCurrentRecommendInfo ? 1 : 0);
                    return true;
                case TRANSACTION_updateCurrentLyric /* 14 */:
                    updateCurrentLyric(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case TRANSACTION_declareSupportCollectTypes /* 15 */:
                    boolean declareSupportCollectTypes = declareSupportCollectTypes(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.createIntArray());
                    parcel2.writeNoException();
                    parcel2.writeInt(declareSupportCollectTypes ? 1 : 0);
                    return true;
                case TRANSACTION_cancelSupportCollectTypes /* 16 */:
                    boolean cancelSupportCollectTypes = cancelSupportCollectTypes(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.createIntArray());
                    parcel2.writeNoException();
                    parcel2.writeInt(cancelSupportCollectTypes ? 1 : 0);
                    return true;
                case TRANSACTION_declareSupportDownloadTypes /* 17 */:
                    boolean declareSupportDownloadTypes = declareSupportDownloadTypes(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.createIntArray());
                    parcel2.writeNoException();
                    parcel2.writeInt(declareSupportDownloadTypes ? 1 : 0);
                    return true;
                case TRANSACTION_cancelSupportDownloadTypes /* 18 */:
                    boolean cancelSupportDownloadTypes = cancelSupportDownloadTypes(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.createIntArray());
                    parcel2.writeNoException();
                    parcel2.writeInt(cancelSupportDownloadTypes ? 1 : 0);
                    return true;
                case TRANSACTION_registerInMusic /* 19 */:
                    IMediaCenterClientToken registerInMusic = registerInMusic(parcel.readString(), IMusicClient.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeStrongInterface(registerInMusic);
                    return true;
                case TRANSACTION_registerInNews /* 20 */:
                    IMediaCenterClientToken registerInNews = registerInNews(parcel.readString(), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeStrongInterface(registerInNews);
                    return true;
                case TRANSACTION_registerInVideo /* 21 */:
                    IMediaCenterClientToken registerInVideo = registerInVideo(parcel.readString(), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeStrongInterface(registerInVideo);
                    return true;
                case TRANSACTION_declareVrCtrlPriority /* 22 */:
                    declareVrCtrlPriority(parcel.readString(), parcel.readInt(), parcel.readStrongBinder(), parcel.readStrongBinder(), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    return true;
                case TRANSACTION_declareMusicCtrlCapability /* 23 */:
                    boolean declareMusicCtrlCapability = declareMusicCtrlCapability(parcel.createIntArray(), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeInt(declareMusicCtrlCapability ? 1 : 0);
                    return true;
                case TRANSACTION_cancelMusicCtrlCapabilityDeclaration /* 24 */:
                    boolean cancelMusicCtrlCapabilityDeclaration = cancelMusicCtrlCapabilityDeclaration(parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeInt(cancelMusicCtrlCapabilityDeclaration ? 1 : 0);
                    return true;
                case TRANSACTION_declareRadioCtrlCapability /* 25 */:
                    boolean declareRadioCtrlCapability = declareRadioCtrlCapability(parcel.createIntArray(), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeInt(declareRadioCtrlCapability ? 1 : 0);
                    return true;
                case TRANSACTION_cancelRadioCtrlCapabilityDeclaration /* 26 */:
                    boolean cancelRadioCtrlCapabilityDeclaration = cancelRadioCtrlCapabilityDeclaration(parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeInt(cancelRadioCtrlCapabilityDeclaration ? 1 : 0);
                    return true;
                case TRANSACTION_declareNewsCtrlCapability /* 27 */:
                    boolean declareNewsCtrlCapability = declareNewsCtrlCapability(parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeInt(declareNewsCtrlCapability ? 1 : 0);
                    return true;
                case TRANSACTION_cancelNewsCtrlCapabilityDeclaration /* 28 */:
                    boolean cancelNewsCtrlCapabilityDeclaration = cancelNewsCtrlCapabilityDeclaration(parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeInt(cancelNewsCtrlCapabilityDeclaration ? 1 : 0);
                    return true;
                case TRANSACTION_updateMediaList /* 29 */:
                    updateMediaList(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt(), parcel.readInt(), parcel.readArrayList(getClass().getClassLoader()));
                    parcel2.writeNoException();
                    return true;
                case TRANSACTION_declareMediaCenterCapability /* 30 */:
                    declareMediaCenterCapability(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.createIntArray());
                    parcel2.writeNoException();
                    return true;
                case TRANSACTION_getStateBinder /* 31 */:
                    IBinder stateBinder = getStateBinder();
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(stateBinder);
                    return true;
                case TRANSACTION_getMediaControlClientApi /* 32 */:
                    IBinder mediaControlClientApi = getMediaControlClientApi();
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(mediaControlClientApi);
                    return true;
                case TRANSACTION_getMediaControllerApi /* 33 */:
                    IBinder mediaControllerApi = getMediaControllerApi();
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(mediaControllerApi);
                    return true;
                case TRANSACTION_declareVrChannelCapability /* 34 */:
                    boolean declareVrChannelCapability = declareVrChannelCapability(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.readStrongBinder(), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeInt(declareVrChannelCapability ? 1 : 0);
                    return true;
                case TRANSACTION_cancelVrChannelCapability /* 35 */:
                    boolean cancelVrChannelCapability = cancelVrChannelCapability(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeInt(cancelVrChannelCapability ? 1 : 0);
                    return true;
                case TRANSACTION_asyncSendVrChannelResult /* 36 */:
                    boolean asyncSendVrChannelResult = asyncSendVrChannelResult(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.readStrongBinder(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(asyncSendVrChannelResult ? 1 : 0);
                    return true;
                case TRANSACTION_updateErrorMsg /* 37 */:
                    updateErrorMsg(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case TRANSACTION_updateMediaContent /* 38 */:
                    boolean updateMediaContent = updateMediaContent(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.readArrayList(getClass().getClassLoader()));
                    parcel2.writeNoException();
                    parcel2.writeInt(updateMediaContent ? 1 : 0);
                    return true;
                case TRANSACTION_updateMultiMediaList /* 39 */:
                    boolean updateMultiMediaList = updateMultiMediaList(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), (IMediaLists) _Parcel.readTypedObject(parcel, IMediaLists.CREATOR));
                    parcel2.writeNoException();
                    parcel2.writeInt(updateMultiMediaList ? 1 : 0);
                    return true;
                case TRANSACTION_updateMediaPlayList /* 40 */:
                    updateMediaPlayList(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    return true;
                case TRANSACTION_updateCollectMsg /* 41 */:
                    updateCollectMsg(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case TRANSACTION_queryCurrentFocusClient /* 42 */:
                    String queryCurrentFocusClient = queryCurrentFocusClient(IMediaCenterClientToken.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeString(queryCurrentFocusClient);
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
