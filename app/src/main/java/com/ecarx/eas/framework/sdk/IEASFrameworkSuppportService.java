package com.ecarx.eas.framework.sdk;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.ecarx.sdk.openapi.msg.EASFrameworkMessage;
import com.ecarx.sdk.openapi.msg.SupportServiceRetMessage;

/* loaded from: classes2.dex */
public interface IEASFrameworkSuppportService extends IInterface {
    public static final String DESCRIPTOR = "com.ecarx.eas.framework.sdk.IEASFrameworkSuppportService";

    SupportServiceRetMessage asyncBinderCall(EASFrameworkMessage eASFrameworkMessage, IBinder iBinder, int i, int i2) throws RemoteException;

    SupportServiceRetMessage asyncCall(EASFrameworkMessage eASFrameworkMessage, IBinder iBinder, int i, int i2) throws RemoteException;

    SupportServiceRetMessage call(EASFrameworkMessage eASFrameworkMessage, int i, int i2) throws RemoteException;

    public static class Default implements IEASFrameworkSuppportService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.ecarx.eas.framework.sdk.IEASFrameworkSuppportService
        public SupportServiceRetMessage asyncBinderCall(EASFrameworkMessage eASFrameworkMessage, IBinder iBinder, int i, int i2) throws RemoteException {
            return null;
        }

        @Override // com.ecarx.eas.framework.sdk.IEASFrameworkSuppportService
        public SupportServiceRetMessage asyncCall(EASFrameworkMessage eASFrameworkMessage, IBinder iBinder, int i, int i2) throws RemoteException {
            return null;
        }

        @Override // com.ecarx.eas.framework.sdk.IEASFrameworkSuppportService
        public SupportServiceRetMessage call(EASFrameworkMessage eASFrameworkMessage, int i, int i2) throws RemoteException {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IEASFrameworkSuppportService {
        static final int TRANSACTION_asyncBinderCall = 3;
        static final int TRANSACTION_asyncCall = 2;
        static final int TRANSACTION_call = 1;

        public static class Proxy implements IEASFrameworkSuppportService {
            private IBinder mRemote;

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.ecarx.eas.framework.sdk.IEASFrameworkSuppportService
            public SupportServiceRetMessage asyncBinderCall(EASFrameworkMessage eASFrameworkMessage, IBinder iBinder, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IEASFrameworkSuppportService.DESCRIPTOR);
                    _Parcel.writeTypedObject(obtain, eASFrameworkMessage, 0);
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return (SupportServiceRetMessage) _Parcel.readTypedObject(obtain2, SupportServiceRetMessage.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.ecarx.eas.framework.sdk.IEASFrameworkSuppportService
            public SupportServiceRetMessage asyncCall(EASFrameworkMessage eASFrameworkMessage, IBinder iBinder, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IEASFrameworkSuppportService.DESCRIPTOR);
                    _Parcel.writeTypedObject(obtain, eASFrameworkMessage, 0);
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return (SupportServiceRetMessage) _Parcel.readTypedObject(obtain2, SupportServiceRetMessage.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.ecarx.eas.framework.sdk.IEASFrameworkSuppportService
            public SupportServiceRetMessage call(EASFrameworkMessage eASFrameworkMessage, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IEASFrameworkSuppportService.DESCRIPTOR);
                    _Parcel.writeTypedObject(obtain, eASFrameworkMessage, 0);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return (SupportServiceRetMessage) _Parcel.readTypedObject(obtain2, SupportServiceRetMessage.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return IEASFrameworkSuppportService.DESCRIPTOR;
            }
        }

        public Stub() {
            attachInterface(this, IEASFrameworkSuppportService.DESCRIPTOR);
        }

        public static IEASFrameworkSuppportService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IEASFrameworkSuppportService.DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IEASFrameworkSuppportService)) ? new Proxy(iBinder) : (IEASFrameworkSuppportService) queryLocalInterface;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IEASFrameworkSuppportService.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IEASFrameworkSuppportService.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                SupportServiceRetMessage call = call((EASFrameworkMessage) _Parcel.readTypedObject(parcel, EASFrameworkMessage.CREATOR), parcel.readInt(), parcel.readInt());
                parcel2.writeNoException();
                _Parcel.writeTypedObject(parcel2, call, 1);
                return true;
            }
            if (i == 2) {
                SupportServiceRetMessage asyncCall = asyncCall((EASFrameworkMessage) _Parcel.readTypedObject(parcel, EASFrameworkMessage.CREATOR), parcel.readStrongBinder(), parcel.readInt(), parcel.readInt());
                parcel2.writeNoException();
                _Parcel.writeTypedObject(parcel2, asyncCall, 1);
                return true;
            }
            if (i != 3) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            SupportServiceRetMessage asyncBinderCall = asyncBinderCall((EASFrameworkMessage) _Parcel.readTypedObject(parcel, EASFrameworkMessage.CREATOR), parcel.readStrongBinder(), parcel.readInt(), parcel.readInt());
            parcel2.writeNoException();
            _Parcel.writeTypedObject(parcel2, asyncBinderCall, 1);
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
