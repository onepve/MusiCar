package ecarx.xsf.mediacenter;

import android.app.PendingIntent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

/* loaded from: classes7.dex */
public interface IMusicPlaybackInfo extends IInterface {
    public static final String DESCRIPTOR = "ecarx.xsf.mediacenter.IMusicPlaybackInfo";

    String getAlbum() throws RemoteException;

    String getAppIcon() throws RemoteException;

    String getAppName() throws RemoteException;

    String getArtist() throws RemoteException;

    Uri getArtwork() throws RemoteException;

    String getCurrentLyricSentence() throws RemoteException;

    int getDisplayId() throws RemoteException;

    long getDuration() throws RemoteException;

    PendingIntent getLaunchIntent() throws RemoteException;

    int getLoopMode() throws RemoteException;

    Uri getLyric() throws RemoteException;

    String getLyricContent() throws RemoteException;

    Uri getMediaPath() throws RemoteException;

    String getMediaType() throws RemoteException;

    Uri getNextArtwork() throws RemoteException;

    String getPackageName() throws RemoteException;

    int getPlaybackStatus() throws RemoteException;

    PendingIntent getPlayerIntent() throws RemoteException;

    int getPlayingItemPositionInQueue() throws RemoteException;

    String getPlayingMediaListId() throws RemoteException;

    int getPlayingMediaListType() throws RemoteException;

    Uri getPreviousArtwork() throws RemoteException;

    String getRadioFrequency() throws RemoteException;

    int getRadioMode() throws RemoteException;

    String getRadioStationName() throws RemoteException;

    int getSourceType() throws RemoteException;

    String getTitle() throws RemoteException;

    String getUuid() throws RemoteException;

    int getVip() throws RemoteException;

    boolean isCollected() throws RemoteException;

    boolean isDownloaded() throws RemoteException;

    boolean isSupportCollect() throws RemoteException;

    boolean isSupportDownload() throws RemoteException;

    boolean isSupportLoopModeSwitch() throws RemoteException;

    boolean isSupportVrCtrlPlayStatus() throws RemoteException;

    public static class Default implements IMusicPlaybackInfo {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public String getAlbum() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public String getAppIcon() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public String getAppName() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public String getArtist() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public Uri getArtwork() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public String getCurrentLyricSentence() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public int getDisplayId() throws RemoteException {
            return 0;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public long getDuration() throws RemoteException {
            return 0L;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public PendingIntent getLaunchIntent() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public int getLoopMode() throws RemoteException {
            return 0;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public Uri getLyric() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public String getLyricContent() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public Uri getMediaPath() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public String getMediaType() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public Uri getNextArtwork() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public String getPackageName() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public int getPlaybackStatus() throws RemoteException {
            return 0;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public PendingIntent getPlayerIntent() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public int getPlayingItemPositionInQueue() throws RemoteException {
            return 0;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public String getPlayingMediaListId() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public int getPlayingMediaListType() throws RemoteException {
            return 0;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public Uri getPreviousArtwork() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public String getRadioFrequency() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public int getRadioMode() throws RemoteException {
            return 0;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public String getRadioStationName() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public int getSourceType() throws RemoteException {
            return 0;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public String getTitle() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public String getUuid() throws RemoteException {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public int getVip() throws RemoteException {
            return 0;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public boolean isCollected() throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public boolean isDownloaded() throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public boolean isSupportCollect() throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public boolean isSupportDownload() throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public boolean isSupportLoopModeSwitch() throws RemoteException {
            return false;
        }

        @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
        public boolean isSupportVrCtrlPlayStatus() throws RemoteException {
            return false;
        }
    }

    public static abstract class Stub extends Binder implements IMusicPlaybackInfo {
        static final int TRANSACTION_getAlbum = 4;
        static final int TRANSACTION_getAppIcon = 26;
        static final int TRANSACTION_getAppName = 25;
        static final int TRANSACTION_getArtist = 3;
        static final int TRANSACTION_getArtwork = 16;
        static final int TRANSACTION_getCurrentLyricSentence = 14;
        static final int TRANSACTION_getDisplayId = 34;
        static final int TRANSACTION_getDuration = 7;
        static final int TRANSACTION_getLaunchIntent = 1;
        static final int TRANSACTION_getLoopMode = 18;
        static final int TRANSACTION_getLyric = 13;
        static final int TRANSACTION_getLyricContent = 12;
        static final int TRANSACTION_getMediaPath = 10;
        static final int TRANSACTION_getMediaType = 35;
        static final int TRANSACTION_getNextArtwork = 17;
        static final int TRANSACTION_getPackageName = 27;
        static final int TRANSACTION_getPlaybackStatus = 11;
        static final int TRANSACTION_getPlayerIntent = 33;
        static final int TRANSACTION_getPlayingItemPositionInQueue = 8;
        static final int TRANSACTION_getPlayingMediaListId = 30;
        static final int TRANSACTION_getPlayingMediaListType = 32;
        static final int TRANSACTION_getPreviousArtwork = 15;
        static final int TRANSACTION_getRadioFrequency = 5;
        static final int TRANSACTION_getRadioMode = 19;
        static final int TRANSACTION_getRadioStationName = 6;
        static final int TRANSACTION_getSourceType = 9;
        static final int TRANSACTION_getTitle = 2;
        static final int TRANSACTION_getUuid = 24;
        static final int TRANSACTION_getVip = 31;
        static final int TRANSACTION_isCollected = 21;
        static final int TRANSACTION_isDownloaded = 23;
        static final int TRANSACTION_isSupportCollect = 20;
        static final int TRANSACTION_isSupportDownload = 22;
        static final int TRANSACTION_isSupportLoopModeSwitch = 28;
        static final int TRANSACTION_isSupportVrCtrlPlayStatus = 29;

        public static class Proxy implements IMusicPlaybackInfo {
            private IBinder mRemote;

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getAlbum() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getAppIcon() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getAppIcon, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getAppName() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getAppName, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getArtist() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public Uri getArtwork() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getArtwork, obtain, obtain2, 0);
                    obtain2.readException();
                    return (Uri) _Parcel.readTypedObject(obtain2, Uri.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getCurrentLyricSentence() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getCurrentLyricSentence, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public int getDisplayId() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getDisplayId, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public long getDuration() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getDuration, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readLong();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return IMusicPlaybackInfo.DESCRIPTOR;
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public PendingIntent getLaunchIntent() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return (PendingIntent) _Parcel.readTypedObject(obtain2, PendingIntent.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public int getLoopMode() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getLoopMode, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public Uri getLyric() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getLyric, obtain, obtain2, 0);
                    obtain2.readException();
                    return (Uri) _Parcel.readTypedObject(obtain2, Uri.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getLyricContent() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getLyricContent, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public Uri getMediaPath() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getMediaPath, obtain, obtain2, 0);
                    obtain2.readException();
                    return (Uri) _Parcel.readTypedObject(obtain2, Uri.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getMediaType() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getMediaType, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public Uri getNextArtwork() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getNextArtwork, obtain, obtain2, 0);
                    obtain2.readException();
                    return (Uri) _Parcel.readTypedObject(obtain2, Uri.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getPackageName() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getPackageName, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public int getPlaybackStatus() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getPlaybackStatus, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public PendingIntent getPlayerIntent() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getPlayerIntent, obtain, obtain2, 0);
                    obtain2.readException();
                    return (PendingIntent) _Parcel.readTypedObject(obtain2, PendingIntent.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public int getPlayingItemPositionInQueue() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getPlayingItemPositionInQueue, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getPlayingMediaListId() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getPlayingMediaListId, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public int getPlayingMediaListType() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getPlayingMediaListType, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public Uri getPreviousArtwork() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getPreviousArtwork, obtain, obtain2, 0);
                    obtain2.readException();
                    return (Uri) _Parcel.readTypedObject(obtain2, Uri.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getRadioFrequency() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public int getRadioMode() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getRadioMode, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getRadioStationName() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public int getSourceType() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getSourceType, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getTitle() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public String getUuid() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getUuid, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public int getVip() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getVip, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public boolean isCollected() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isCollected, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public boolean isDownloaded() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isDownloaded, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public boolean isSupportCollect() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isSupportCollect, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public boolean isSupportDownload() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isSupportDownload, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public boolean isSupportLoopModeSwitch() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isSupportLoopModeSwitch, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.IMusicPlaybackInfo
            public boolean isSupportVrCtrlPlayStatus() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMusicPlaybackInfo.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isSupportVrCtrlPlayStatus, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IMusicPlaybackInfo.DESCRIPTOR);
        }

        public static IMusicPlaybackInfo asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IMusicPlaybackInfo.DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMusicPlaybackInfo)) ? new Proxy(iBinder) : (IMusicPlaybackInfo) queryLocalInterface;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IMusicPlaybackInfo.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IMusicPlaybackInfo.DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    PendingIntent launchIntent = getLaunchIntent();
                    parcel2.writeNoException();
                    _Parcel.writeTypedObject(parcel2, launchIntent, 1);
                    return true;
                case 2:
                    String title = getTitle();
                    parcel2.writeNoException();
                    parcel2.writeString(title);
                    return true;
                case 3:
                    String artist = getArtist();
                    parcel2.writeNoException();
                    parcel2.writeString(artist);
                    return true;
                case 4:
                    String album = getAlbum();
                    parcel2.writeNoException();
                    parcel2.writeString(album);
                    return true;
                case 5:
                    String radioFrequency = getRadioFrequency();
                    parcel2.writeNoException();
                    parcel2.writeString(radioFrequency);
                    return true;
                case 6:
                    String radioStationName = getRadioStationName();
                    parcel2.writeNoException();
                    parcel2.writeString(radioStationName);
                    return true;
                case TRANSACTION_getDuration /* 7 */:
                    long duration = getDuration();
                    parcel2.writeNoException();
                    parcel2.writeLong(duration);
                    return true;
                case TRANSACTION_getPlayingItemPositionInQueue /* 8 */:
                    int playingItemPositionInQueue = getPlayingItemPositionInQueue();
                    parcel2.writeNoException();
                    parcel2.writeInt(playingItemPositionInQueue);
                    return true;
                case TRANSACTION_getSourceType /* 9 */:
                    int sourceType = getSourceType();
                    parcel2.writeNoException();
                    parcel2.writeInt(sourceType);
                    return true;
                case TRANSACTION_getMediaPath /* 10 */:
                    Uri mediaPath = getMediaPath();
                    parcel2.writeNoException();
                    _Parcel.writeTypedObject(parcel2, mediaPath, 1);
                    return true;
                case TRANSACTION_getPlaybackStatus /* 11 */:
                    int playbackStatus = getPlaybackStatus();
                    parcel2.writeNoException();
                    parcel2.writeInt(playbackStatus);
                    return true;
                case TRANSACTION_getLyricContent /* 12 */:
                    String lyricContent = getLyricContent();
                    parcel2.writeNoException();
                    parcel2.writeString(lyricContent);
                    return true;
                case TRANSACTION_getLyric /* 13 */:
                    Uri lyric = getLyric();
                    parcel2.writeNoException();
                    _Parcel.writeTypedObject(parcel2, lyric, 1);
                    return true;
                case TRANSACTION_getCurrentLyricSentence /* 14 */:
                    String currentLyricSentence = getCurrentLyricSentence();
                    parcel2.writeNoException();
                    parcel2.writeString(currentLyricSentence);
                    return true;
                case TRANSACTION_getPreviousArtwork /* 15 */:
                    Uri previousArtwork = getPreviousArtwork();
                    parcel2.writeNoException();
                    _Parcel.writeTypedObject(parcel2, previousArtwork, 1);
                    return true;
                case TRANSACTION_getArtwork /* 16 */:
                    Uri artwork = getArtwork();
                    parcel2.writeNoException();
                    _Parcel.writeTypedObject(parcel2, artwork, 1);
                    return true;
                case TRANSACTION_getNextArtwork /* 17 */:
                    Uri nextArtwork = getNextArtwork();
                    parcel2.writeNoException();
                    _Parcel.writeTypedObject(parcel2, nextArtwork, 1);
                    return true;
                case TRANSACTION_getLoopMode /* 18 */:
                    int loopMode = getLoopMode();
                    parcel2.writeNoException();
                    parcel2.writeInt(loopMode);
                    return true;
                case TRANSACTION_getRadioMode /* 19 */:
                    int radioMode = getRadioMode();
                    parcel2.writeNoException();
                    parcel2.writeInt(radioMode);
                    return true;
                case TRANSACTION_isSupportCollect /* 20 */:
                    boolean isSupportCollect = isSupportCollect();
                    parcel2.writeNoException();
                    parcel2.writeInt(isSupportCollect ? 1 : 0);
                    return true;
                case TRANSACTION_isCollected /* 21 */:
                    boolean isCollected = isCollected();
                    parcel2.writeNoException();
                    parcel2.writeInt(isCollected ? 1 : 0);
                    return true;
                case TRANSACTION_isSupportDownload /* 22 */:
                    boolean isSupportDownload = isSupportDownload();
                    parcel2.writeNoException();
                    parcel2.writeInt(isSupportDownload ? 1 : 0);
                    return true;
                case TRANSACTION_isDownloaded /* 23 */:
                    boolean isDownloaded = isDownloaded();
                    parcel2.writeNoException();
                    parcel2.writeInt(isDownloaded ? 1 : 0);
                    return true;
                case TRANSACTION_getUuid /* 24 */:
                    String uuid = getUuid();
                    parcel2.writeNoException();
                    parcel2.writeString(uuid);
                    return true;
                case TRANSACTION_getAppName /* 25 */:
                    String appName = getAppName();
                    parcel2.writeNoException();
                    parcel2.writeString(appName);
                    return true;
                case TRANSACTION_getAppIcon /* 26 */:
                    String appIcon = getAppIcon();
                    parcel2.writeNoException();
                    parcel2.writeString(appIcon);
                    return true;
                case TRANSACTION_getPackageName /* 27 */:
                    String packageName = getPackageName();
                    parcel2.writeNoException();
                    parcel2.writeString(packageName);
                    return true;
                case TRANSACTION_isSupportLoopModeSwitch /* 28 */:
                    boolean isSupportLoopModeSwitch = isSupportLoopModeSwitch();
                    parcel2.writeNoException();
                    parcel2.writeInt(isSupportLoopModeSwitch ? 1 : 0);
                    return true;
                case TRANSACTION_isSupportVrCtrlPlayStatus /* 29 */:
                    boolean isSupportVrCtrlPlayStatus = isSupportVrCtrlPlayStatus();
                    parcel2.writeNoException();
                    parcel2.writeInt(isSupportVrCtrlPlayStatus ? 1 : 0);
                    return true;
                case TRANSACTION_getPlayingMediaListId /* 30 */:
                    String playingMediaListId = getPlayingMediaListId();
                    parcel2.writeNoException();
                    parcel2.writeString(playingMediaListId);
                    return true;
                case TRANSACTION_getVip /* 31 */:
                    int vip = getVip();
                    parcel2.writeNoException();
                    parcel2.writeInt(vip);
                    return true;
                case TRANSACTION_getPlayingMediaListType /* 32 */:
                    int playingMediaListType = getPlayingMediaListType();
                    parcel2.writeNoException();
                    parcel2.writeInt(playingMediaListType);
                    return true;
                case TRANSACTION_getPlayerIntent /* 33 */:
                    PendingIntent playerIntent = getPlayerIntent();
                    parcel2.writeNoException();
                    _Parcel.writeTypedObject(parcel2, playerIntent, 1);
                    return true;
                case TRANSACTION_getDisplayId /* 34 */:
                    int displayId = getDisplayId();
                    parcel2.writeNoException();
                    parcel2.writeInt(displayId);
                    return true;
                case TRANSACTION_getMediaType /* 35 */:
                    String mediaType = getMediaType();
                    parcel2.writeNoException();
                    parcel2.writeString(mediaType);
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
