package com.example.smartdustbin;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import me.itangqi.waveloadingview.WaveLoadingView;

public class MainActivity extends AppCompatActivity {

    //These are Defined Variables

    private static final String TAG = MainActivity.class.getName();

    FloatingActionButton plus,message;
    Animation open,close,clockwise,anticlockwise;
    Handler service_handler = new Handler();
    Handler waveloading_handler = new Handler();
    RequestQueue mqueue;
    WaveLoadingView view;

    boolean isopen=false;
    RequestQueue httprequest;
    StringRequest stringRequest;
    //String url="https://maker.ifttt.com/trigger/smartdustbin/with/key/d_-Odke5J-SGYTXPV4W9WMeszpskuj0mtbVtcpi3-XY";
    String url="https://maker.ifttt.com/trigger/Smart Dustbin/with/key/bcqdUlpYs_l0ikEZFXI1B71yRY-7pBM1b6x3AxzlBKF";
    long backtime;
    Toast backtoast;

    //Method on Starting the App

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(" Smart DustBin");
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_actionbar_logo);

        plus =(FloatingActionButton)findViewById(R.id.fab_plus);
        message =(FloatingActionButton)findViewById(R.id.fab_message);
        //refresh = (FloatingActionButton)findViewById(R.id.fab_refresh);

        open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        clockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
        anticlockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);

        mqueue = Volley.newRequestQueue(this);

        view = (WaveLoadingView)findViewById(R.id.loading_screen);
        view.setProgressValue(0);

        floatingbutton_plus();

        //floatingbutton_refresh();

        floatingbutton_message();

        waveloading.run();
        service.run();
    }

    //For Options Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.option1:
                confirmation_send_request();
                return true;
            case R.id.option2:
                Toast.makeText(this, "Not Applicable", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.option3:
                about();
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    //For Back Button Clicked

    @Override
    public void onBackPressed() {

        if(backtime+2000>System.currentTimeMillis()){
            backtoast.cancel();
            super.onBackPressed();
            return;
        } else{
            backtoast = Toast.makeText(this, "Press back again to Exit", Toast.LENGTH_SHORT);
            backtoast.show();
        }

        backtime = System.currentTimeMillis();
    }

    //Sending Message with Confirmation

    public void confirmation_send_request() {
        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setMessage("Send Complain?")
                .setCancelable(true)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Toast.makeText(MainActivity.this, "Complaint Cancelled", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        send_request();
                        Toast.makeText(MainActivity.this, "Sending Complain", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alertDialog = confirm.create();
        alertDialog.show();
    }

    public void send_request() {
        httprequest = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG,"Response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,"Error: " + error.toString());
            }
        });

        httprequest.add(stringRequest);
    }

    //For About Option Clicked

    public void about() {
        AlertDialog.Builder about_message = new AlertDialog.Builder(this);
        about_message.setTitle("About")
                .setMessage("Developed By: The Dark Ninja\nPowered By: Android Studio")
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alertDialog = about_message.create();
        alertDialog.show();
    }

    //For plus Button Clicked

    public void floatingbutton_plus(){

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isopen){
                    //refresh.startAnimation(close);
                    message.startAnimation(close);
                    plus.startAnimation(anticlockwise);
                    //refresh.setClickable(false);
                    message.setClickable(false);
                    isopen=false;
                }
                else{
                    //refresh.startAnimation(open);
                    message.startAnimation(open);
                    plus.startAnimation(clockwise);
                    //refresh.setClickable(true);
                    message.setClickable(true);
                    isopen=true;
                }
            }
        });
    }

    //For Refresh floating Button Clicked

    /*public void floatingbutton_refresh(){
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }*/

    //For Sending Message when floating Button Clicked

    public void floatingbutton_message(){
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmation_send_request();
            }
        });
    }

    //For Service Starting

    public Runnable service = new Runnable() {
        @Override
        public void run() {
            start_service();
            service_handler.postDelayed(this, 15000);
        }
    };

    public void start_service(){
        startService(new Intent(this,AlertmsgService.class));
    }

    //Loading Wave Loading Layout

    public Runnable waveloading = new Runnable() {
        @Override
        public void run() {
            getdata();
            waveloading_handler.postDelayed(this, 1000);
        }
    };

    public void getdata(){
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
                view.setProgressValue(value);
                if(value<=50){
                    view.setBottomTitle(String.format("%d%%",value));
                    view.setCenterTitle("");
                    view.setTopTitle("");
                }
                else if(value<80){
                    view.setBottomTitle("");
                    view.setCenterTitle(String.format("%d%%",value));
                    view.setTopTitle("");
                }
                else{
                    view.setBottomTitle("");
                    view.setCenterTitle("");
                    view.setTopTitle(String.format("%d%%",value));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mqueue.add(request);
    }
}


