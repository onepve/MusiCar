package com.ecarx.eas.xsf.mediacenter;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

/* loaded from: classes5.dex */
public interface IExCallback extends IInterface {
    public static final String DESCRIPTOR = "com.ecarx.eas.xsf.mediacenter.IExCallback";

    String onAction(int i, String str, String str2, IBinder iBinder) throws RemoteException;

    IExContent onExAction(int i, String str, String str2, IExContent iExContent, IBinder iBinder) throws RemoteException;

    public static class Default implements IExCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.ecarx.eas.xsf.mediacenter.IExCallback
        public String onAction(int i, String str, String str2, IBinder iBinder) throws RemoteException {
            return null;
        }

        @Override // com.ecarx.eas.xsf.mediacenter.IExCallback
        public IExContent onExAction(int i, String str, String str2, IExContent iExContent, IBinder iBinder) throws RemoteException {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IExCallback {
        static final int TRANSACTION_onAction = 1;
        static final int TRANSACTION_onExAction = 2;

        public static class Proxy implements IExCallback {
            private IBinder mRemote;

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IExCallback.DESCRIPTOR;
            }

            @Override // com.ecarx.eas.xsf.mediacenter.IExCallback
            public String onAction(int i, String str, String str2, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IExCallback.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.ecarx.eas.xsf.mediacenter.IExCallback
            public IExContent onExAction(int i, String str, String str2, IExContent iExContent, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IExCallback.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    _Parcel.writeTypedObject(obtain, iExContent, 0);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return (IExContent) _Parcel.readTypedObject(obtain2, IExContent.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IExCallback.DESCRIPTOR);
        }

        public static IExCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IExCallback.DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IExCallback)) ? new Proxy(iBinder) : (IExCallback) queryLocalInterface;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IExCallback.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IExCallback.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                String onAction = onAction(parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readStrongBinder());
                parcel2.writeNoException();
                parcel2.writeString(onAction);
                return true;
            }
            if (i != 2) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            IExContent onExAction = onExAction(parcel.readInt(), parcel.readString(), parcel.readString(), (IExContent) _Parcel.readTypedObject(parcel, IExContent.CREATOR), parcel.readStrongBinder());
            parcel2.writeNoException();
            _Parcel.writeTypedObject(parcel2, onExAction, 1);
            return true;
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
