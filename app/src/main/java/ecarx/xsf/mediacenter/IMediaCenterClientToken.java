package ecarx.xsf.mediacenter;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes7.dex */
public interface IMediaCenterClientToken extends IInterface {
    public static final String DESCRIPTOR = "ecarx.xsf.mediacenter.IMediaCenterClientToken";

    public static class Default implements IMediaCenterClientToken {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IMediaCenterClientToken {

        public static class Proxy implements IMediaCenterClientToken {
            private IBinder mRemote;

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMediaCenterClientToken.DESCRIPTOR;
            }
        }

        public Stub() {
            attachInterface(this, IMediaCenterClientToken.DESCRIPTOR);
        }

        public static IMediaCenterClientToken asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IMediaCenterClientToken.DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IMediaCenterClientToken)) ? new Proxy(iBinder) : (IMediaCenterClientToken) queryLocalInterface;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel2.writeString(IMediaCenterClientToken.DESCRIPTOR);
            return true;
        }
    }
}
