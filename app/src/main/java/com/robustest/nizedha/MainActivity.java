package com.robustest.nizedha;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.Image;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.CallLog;
import android.provider.Telephony;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text = (TextView) findViewById(R.id.text);
        Bundle command = getIntent().getExtras();
        if (command!=null) {
            if (command.containsKey("wifi")) {
                try{
                    WifiManager(Boolean.parseBoolean(command.getString("wifi")));
                    text.setText("TOGGLING WIFI");
                }catch (Exception e){}
            }
            else if(command.containsKey("gps")) {
                try{
                    GPSManager(Boolean.parseBoolean(command.getString("gps")));
                    text.setText("TOGGLING GPS");
                }catch (Exception e){}
            }
            else if(command.containsKey("location")) {
                String[] location = command.getString("location").split(",");
                try{
                    Double latitude = Double.parseDouble(location[0]);
                    Double longitude = Double.parseDouble(location[1]);
                    Float accuracy = Float.parseFloat(location[2]);
                    setMockLocation(latitude,longitude,accuracy);
                    text.setText("MOCKING LOCATION");
                }catch (Exception e){}
            }
            else if(command.containsKey("sms")){
                text.setText("Fetching SMS");
                try{
                    getSMS(Integer.parseInt(command.getString("sms")));
                }catch (Exception e){}
            }
            else if(command.containsKey("clean")){
                if(command.getString("clean").equals("log")) {
                    try {
                        text.setText("Deleting Call Log");
                        cleanCallLog();
                    } catch (Exception e) {}
                }
                else if(command.getString("clean").equals("inbox")){
                    try{
                        text.setText("Deleting Messages");
                        cleanInbox();
                    }catch (Exception e){}
                }
            }
            else if(command.containsKey("reset")) {
                try{
                    WifiManager(true);
                    setMockLocation(17.444792,78.348310,100);
                    cleanCallLog();
                    text.setText("RESETTING");
                }catch (Exception e){}
            }
        }

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, 2000);
    }

    private void WifiManager(boolean enable){
        WifiManager wfm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wfm.setWifiEnabled(enable);
    }

    private void GPSManager(boolean enable){
        Intent intent  = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled",enable);
        sendBroadcast(intent);
    }

    private void setMockLocation(double latitude,double longitude,float accuracy){
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lm.addTestProvider(LocationManager.GPS_PROVIDER,
                "requiersNetwork" == "",
                "requiresSatellite" == "",
                "requiresCell" == "",
                "hasMonetaryCost" == "",
                "supportsAltitude" == "",
                "supportsSpeed" == "",
                "supportsBearing" == "",
                android.location.Criteria.POWER_LOW,
                android.location.Criteria.ACCURACY_FINE);
        Location newlocation = new Location(LocationManager.GPS_PROVIDER);
        newlocation.setLatitude(latitude);
        newlocation.setLongitude(longitude);
        newlocation.setAccuracy(accuracy);
        int api_version = Build.VERSION.SDK_INT;
        if(api_version >= 17){
            newlocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        newlocation.setTime(System.currentTimeMillis());
        lm.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
        lm.setTestProviderStatus(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
        lm.setTestProviderLocation(LocationManager.GPS_PROVIDER, newlocation);

    }

    private void getSMS(int number){
        Uri SMS_uri = Uri.parse("content://sms/inbox");
        String[] reqCols = new String[] {"address","date","body","_id"};
        Cursor cur = getContentResolver().query(SMS_uri,reqCols,null,null,null);
        int count = 0;
        try{
            FileOutputStream fOut = openFileOutput("sms_data.json",MODE_WORLD_READABLE);
            JSONObject parent =  new JSONObject();
            if(cur.moveToFirst()){
                do{
                    if(count == number){
                        break;
                    }
                    JSONObject details = new JSONObject();
                    details.put("sender",cur.getString(0));
                    details.put("body", cur.getString(2));
                    details.put("time",cur.getString(1));
                    details.put("id",cur.getString(3));
                    parent.put(cur.getString(1)+"_"+cur.getString(3),details);
                    count = count + 1;
                }while(cur.moveToNext());
            }
            fOut.write(parent.toString().getBytes());
            fOut.flush();
            fOut.close();
        }catch (Exception e){}
        cur.close();
    }

    private void cleanCallLog(){
        getContentResolver().delete(CallLog.Calls.CONTENT_URI,null,null);
    }

    private void cleanInbox(){
        Uri inboxUri = Uri.parse("content://sms/");
        Cursor c = getContentResolver().query(inboxUri,null,null,null,null);
        try{
            while(c.moveToNext()) {
                String id = c.getString(0);
                getApplicationContext().getContentResolver().delete(Uri.parse("content://sms/" + id), null, null);
            }
        }catch (Exception e){
            Log.d("Error deleting sms",String.valueOf(e));
        }finally {
            c.close();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}