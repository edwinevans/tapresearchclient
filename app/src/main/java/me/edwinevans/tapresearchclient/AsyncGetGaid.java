package me.edwinevans.tapresearchclient;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import java.io.IOException;

import static com.google.android.gms.ads.identifier.AdvertisingIdClient.getAdvertisingIdInfo;

// Class to help get the GAID on a non-UI thread (as required)
public class AsyncGetGaid {
    static void execute(final Context context, final ResponseHandler handler) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String gaid = "";
                try {
                    gaid = getAdvertisingIdInfo(context).getId();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }
                handler.OnGotGaid(gaid);
            }
        });
    }

    public interface ResponseHandler {
        void OnGotGaid(String gaid);
    }
}
