package ecarx.xsf.mediacenter;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes7.dex */
public interface ISearchMusicCallback extends IInterface {
    public static final String DESCRIPTOR = "ecarx.xsf.mediacenter.ISearchMusicCallback";

    void onSearchFail(String str) throws RemoteException;

    void onSearchSuccess(String str) throws RemoteException;

    public static class Default implements ISearchMusicCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // ecarx.xsf.mediacenter.ISearchMusicCallback
        public void onSearchFail(String str) throws RemoteException {
        }

        @Override // ecarx.xsf.mediacenter.ISearchMusicCallback
        public void onSearchSuccess(String str) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ISearchMusicCallback {
        static final int TRANSACTION_onSearchFail = 2;
        static final int TRANSACTION_onSearchSuccess = 1;

        public static class Proxy implements ISearchMusicCallback {
            private IBinder mRemote;

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISearchMusicCallback.DESCRIPTOR;
            }

            @Override // ecarx.xsf.mediacenter.ISearchMusicCallback
            public void onSearchFail(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISearchMusicCallback.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // ecarx.xsf.mediacenter.ISearchMusicCallback
            public void onSearchSuccess(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISearchMusicCallback.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, ISearchMusicCallback.DESCRIPTOR);
        }

        public static ISearchMusicCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(ISearchMusicCallback.DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof ISearchMusicCallback)) ? new Proxy(iBinder) : (ISearchMusicCallback) queryLocalInterface;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(ISearchMusicCallback.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(ISearchMusicCallback.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                onSearchSuccess(parcel.readString());
                parcel2.writeNoException();
            } else {
                if (i != 2) {
                    return super.onTransact(i, parcel, parcel2, i2);
                }
                onSearchFail(parcel.readString());
                parcel2.writeNoException();
            }
            return true;
        }
    }
}
