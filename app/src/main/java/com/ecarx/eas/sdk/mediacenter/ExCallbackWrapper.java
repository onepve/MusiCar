package com.ecarx.eas.sdk.mediacenter;

import android.os.IBinder;
import android.util.Log;
import com.ecarx.eas.xsf.mediacenter.IExCallback;
import com.ecarx.eas.xsf.mediacenter.IExContent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes4.dex */
public class ExCallbackWrapper extends IExCallback.Stub {
    private static final String TAG = "ExCallbackWrapper";
    private final Map<String, Action> mMap = new HashMap();

    public interface Action {
        String onAction(int i, String str, String str2, IBinder iBinder);

        IExContent onExAction(int i, String str, String str2, IExContent iExContent, IBinder iBinder);
    }

    @Override // com.ecarx.eas.xsf.mediacenter.IExCallback
    public String onAction(int i, String str, String str2, IBinder iBinder) {
        Log.d(TAG, "onAction:" + i + "," + str + "," + str2);
        Iterator<Map.Entry<String, Action>> it = this.mMap.entrySet().iterator();
        if (it.hasNext()) {
            return it.next().getValue().onAction(i, str, str2, iBinder);
        }
        return null;
    }

    @Override // com.ecarx.eas.xsf.mediacenter.IExCallback
    public IExContent onExAction(int i, String str, String str2, IExContent iExContent, IBinder iBinder) {
        Log.d(TAG, "onExAction:" + i + "," + str + "," + str2);
        Iterator<Map.Entry<String, Action>> it = this.mMap.entrySet().iterator();
        if (it.hasNext()) {
            return it.next().getValue().onExAction(i, str, str2, iExContent, iBinder);
        }
        return null;
    }

    public void setListener(String str, Action action) {
        this.mMap.put(str, action);
    }
}
