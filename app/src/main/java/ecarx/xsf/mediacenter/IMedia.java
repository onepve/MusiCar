package ecarx.xsf.mediacenter;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes7.dex */
public class IMedia implements Parcelable {
    public static final Parcelable.Creator<IMedia> CREATOR = new Parcelable.Creator<IMedia>() { // from class: ecarx.xsf.mediacenter.IMedia.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IMedia createFromParcel(Parcel parcel) {
            return new IMedia(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IMedia[] newArray(int i) {
            return new IMedia[i];
        }
    };

    public IMedia() {
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
    }

    public IMedia(Parcel parcel) {
    }
}
