package com.ecarx.eas.framework.sdk;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.ecarx.sdk.openapi.msg.EASFrameworkMessage;
import com.ecarx.sdk.openapi.msg.EASFrameworkRetMessage;
import java.util.List;

/* loaded from: classes2.dex */
public interface IEASFrameworkService extends IInterface {
    public static final String DESCRIPTOR = "com.ecarx.eas.framework.sdk.IEASFrameworkService";

    EASFrameworkRetMessage asyncBinderCall(EASFrameworkMessage eASFrameworkMessage, IBinder iBinder) throws RemoteException;

    EASFrameworkRetMessage asyncCall(EASFrameworkMessage eASFrameworkMessage, IBinder iBinder) throws RemoteException;

    EASFrameworkRetMessage call(EASFrameworkMessage eASFrameworkMessage) throws RemoteException;

    List<String> getAvailableEASServices() throws RemoteException;

    List<String> getAvailableServices() throws RemoteException;

    IBinder getService(int i, int i2, String str, String str2) throws RemoteException;

    void init(String[] strArr) throws RemoteException;

    boolean registerNotifyListener(IBinder iBinder) throws RemoteException;

    void reserved3() throws RemoteException;

    public static class Default implements IEASFrameworkService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
        public EASFrameworkRetMessage asyncBinderCall(EASFrameworkMessage eASFrameworkMessage, IBinder iBinder) throws RemoteException {
            return null;
        }

        @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
        public EASFrameworkRetMessage asyncCall(EASFrameworkMessage eASFrameworkMessage, IBinder iBinder) throws RemoteException {
            return null;
        }

        @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
        public EASFrameworkRetMessage call(EASFrameworkMessage eASFrameworkMessage) throws RemoteException {
            return null;
        }

        @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
        public List<String> getAvailableEASServices() throws RemoteException {
            return null;
        }

        @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
        public List<String> getAvailableServices() throws RemoteException {
            return null;
        }

        @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
        public IBinder getService(int i, int i2, String str, String str2) throws RemoteException {
            return null;
        }

        @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
        public void init(String[] strArr) throws RemoteException {
        }

        @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
        public boolean registerNotifyListener(IBinder iBinder) {
            return false;
        }

        @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
        public void reserved3() throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IEASFrameworkService {
        static final int TRANSACTION_asyncBinderCall = 6;
        static final int TRANSACTION_asyncCall = 5;
        static final int TRANSACTION_call = 4;
        static final int TRANSACTION_getAvailableEASServices = 9;
        static final int TRANSACTION_getAvailableServices = 7;
        static final int TRANSACTION_getService = 8;
        static final int TRANSACTION_init = 1;
        static final int TRANSACTION_registerNotifyListener = 2;
        static final int TRANSACTION_reserved3 = 3;

        public static class Proxy implements IEASFrameworkService {
            private IBinder mRemote;

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
            public EASFrameworkRetMessage asyncBinderCall(EASFrameworkMessage eASFrameworkMessage, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IEASFrameworkService.DESCRIPTOR);
                    _Parcel.writeTypedObject(obtain, eASFrameworkMessage, 0);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return (EASFrameworkRetMessage) _Parcel.readTypedObject(obtain2, EASFrameworkRetMessage.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
            public EASFrameworkRetMessage asyncCall(EASFrameworkMessage eASFrameworkMessage, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IEASFrameworkService.DESCRIPTOR);
                    _Parcel.writeTypedObject(obtain, eASFrameworkMessage, 0);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    return (EASFrameworkRetMessage) _Parcel.readTypedObject(obtain2, EASFrameworkRetMessage.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
            public EASFrameworkRetMessage call(EASFrameworkMessage eASFrameworkMessage) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IEASFrameworkService.DESCRIPTOR);
                    _Parcel.writeTypedObject(obtain, eASFrameworkMessage, 0);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return (EASFrameworkRetMessage) _Parcel.readTypedObject(obtain2, EASFrameworkRetMessage.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
            public List<String> getAvailableEASServices() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IEASFrameworkService.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getAvailableEASServices, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createStringArrayList();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
            public List<String> getAvailableServices() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IEASFrameworkService.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getAvailableServices, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createStringArrayList();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return IEASFrameworkService.DESCRIPTOR;
            }

            @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
            public IBinder getService(int i, int i2, String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IEASFrameworkService.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(Stub.TRANSACTION_getService, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readStrongBinder();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
            public void init(String[] strArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IEASFrameworkService.DESCRIPTOR);
                    obtain.writeStringArray(strArr);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
            public boolean registerNotifyListener(IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IEASFrameworkService.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.ecarx.eas.framework.sdk.IEASFrameworkService
            public void reserved3() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IEASFrameworkService.DESCRIPTOR);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, IEASFrameworkService.DESCRIPTOR);
        }

        public static IEASFrameworkService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IEASFrameworkService.DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IEASFrameworkService)) ? new Proxy(iBinder) : (IEASFrameworkService) queryLocalInterface;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IEASFrameworkService.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IEASFrameworkService.DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    init(parcel.createStringArray());
                    parcel2.writeNoException();
                    return true;
                case 2:
                    boolean registerNotifyListener = registerNotifyListener(parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeInt(registerNotifyListener ? 1 : 0);
                    return true;
                case 3:
                    reserved3();
                    parcel2.writeNoException();
                    return true;
                case 4:
                    EASFrameworkRetMessage call = call((EASFrameworkMessage) _Parcel.readTypedObject(parcel, EASFrameworkMessage.CREATOR));
                    parcel2.writeNoException();
                    _Parcel.writeTypedObject(parcel2, call, 1);
                    return true;
                case 5:
                    EASFrameworkRetMessage asyncCall = asyncCall((EASFrameworkMessage) _Parcel.readTypedObject(parcel, EASFrameworkMessage.CREATOR), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    _Parcel.writeTypedObject(parcel2, asyncCall, 1);
                    return true;
                case 6:
                    EASFrameworkRetMessage asyncBinderCall = asyncBinderCall((EASFrameworkMessage) _Parcel.readTypedObject(parcel, EASFrameworkMessage.CREATOR), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    _Parcel.writeTypedObject(parcel2, asyncBinderCall, 1);
                    return true;
                case TRANSACTION_getAvailableServices /* 7 */:
                    List<String> availableServices = getAvailableServices();
                    parcel2.writeNoException();
                    parcel2.writeStringList(availableServices);
                    return true;
                case TRANSACTION_getService /* 8 */:
                    IBinder service = getService(parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(service);
                    return true;
                case TRANSACTION_getAvailableEASServices /* 9 */:
                    List<String> availableEASServices = getAvailableEASServices();
                    parcel2.writeNoException();
                    parcel2.writeStringList(availableEASServices);
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
