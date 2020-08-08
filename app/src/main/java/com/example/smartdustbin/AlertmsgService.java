package com.example.smartdustbin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AlertmsgService extends Service {

    RequestQueue mqueue;
    NotificationCompat.Builder dustbin_full_notify;

    private static final int dfn_id= 10;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        dustbin_full_notify = new NotificationCompat.Builder(this);
        dustbin_full_notify.setAutoCancel(true);

        mqueue = Volley.newRequestQueue(this);

        jsonparse();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void jsonparse(){
        String url="https://io.adafruit.com/api/v2/tdn123/feeds/status?X-AIO-Key=40378cd2dae1484bb9c075a6e555abe4";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int value=0;
                try {
                    value = response.getInt("last_value");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(value>80){
                    dustbin_full_notify.setSmallIcon(R.drawable.dustbin_full);
                    dustbin_full_notify.setTicker("Getting Status");
                    dustbin_full_notify.setWhen(System.currentTimeMillis());
                    dustbin_full_notify.setContentTitle("DustBin Status");
                    dustbin_full_notify.setContentText("DustBin is " + value + "% full.");

                    NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    nm.notify(dfn_id,dustbin_full_notify.build());
                }
                else{
                    //do nothing
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendintent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        dustbin_full_notify.setContentIntent(pendintent);

        mqueue.add(request);
    }
}
