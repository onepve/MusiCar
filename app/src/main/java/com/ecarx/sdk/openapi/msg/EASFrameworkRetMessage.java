package com.ecarx.sdk.openapi.msg;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes4.dex */
public class EASFrameworkRetMessage implements Parcelable {
    public static final Parcelable.Creator<EASFrameworkRetMessage> CREATOR = new Parcelable.Creator<EASFrameworkRetMessage>() { // from class: com.ecarx.sdk.openapi.msg.EASFrameworkRetMessage.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EASFrameworkRetMessage createFromParcel(Parcel parcel) {
            return new EASFrameworkRetMessage(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EASFrameworkRetMessage[] newArray(int i) {
            return new EASFrameworkRetMessage[i];
        }
    };
    public byte[] mAttachInfo;
    public int mCode;
    public String mMsg;
    public SupportServiceRetMessage mRetMsg;

    public EASFrameworkRetMessage(Parcel parcel) {
        this.mCode = parcel.readInt();
        this.mMsg = parcel.readString();
        this.mAttachInfo = parcel.createByteArray();
        this.mRetMsg = (SupportServiceRetMessage) parcel.readParcelable(SupportServiceRetMessage.class.getClassLoader());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mCode);
        parcel.writeString(this.mMsg);
        parcel.writeByteArray(this.mAttachInfo);
        parcel.writeParcelable(this.mRetMsg, i);
    }

    public EASFrameworkRetMessage() {
    }
}
