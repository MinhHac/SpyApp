package com.example.hac.notebook;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UpdateNoidung extends AppCompatActivity {

    //String url="http://minhhac-001-site1.1tempurl.com/sms/";

    FirebaseDatabase mydatabase ;
    DatabaseReference myRef ;


    final String DATABASE_NAME ="Notebook.sqlite";
    SQLiteDatabase database;
    final int RESQUEST_TAKE_PHOTO =123;
    final int RESQUEST_CHOOSE_PHOTO=321;
    TextView tv1,tv2,tvsave,tvquit;
    EditText edtten,edtnoidung;
    ImageView imvud;
    FloatingActionButton fab1,fab2,fab3,fab4,fab;
    LinearLayout layoutchup,layoutchon;

    int id;

    Animation mshowbutton,mhidebutton,mshowtext,showtext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_noidung);


        Intent nhan=getIntent();
        int ketqua= nhan.getIntExtra("id",-1);
        id=ketqua;
        anhxa();
        initUI(id);
        sukien();

    }

    private void anhxa() {

        edtten=(EditText)findViewById(R.id.edtten);
        edtnoidung=(EditText) findViewById(R.id.edtnoidung);

        imvud=(ImageView)findViewById(R.id.imvud);

        tv1=(TextView)findViewById(R.id.tvchon);
        tv2=(TextView)findViewById(R.id.tvchup);
        tvquit=(TextView)findViewById(R.id.tvquit);
        tvsave=(TextView)findViewById(R.id.tvsave);



        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab1=(FloatingActionButton)findViewById(R.id.fab1);
        fab2=(FloatingActionButton)findViewById(R.id.fab2);
        fab3=(FloatingActionButton)findViewById(R.id.fab3);
        fab4=(FloatingActionButton)findViewById(R.id.fab4);


        layoutchup =(LinearLayout)findViewById(R.id.layoutchup);
        layoutchon =(LinearLayout)findViewById(R.id.layoutchon);

        mshowbutton= AnimationUtils.loadAnimation(this,R.anim.show_button);
        mhidebutton= AnimationUtils.loadAnimation(this,R.anim.hide_button);
        mshowtext= AnimationUtils.loadAnimation(this,R.anim.show_text);
        showtext= AnimationUtils.loadAnimation(this,R.anim.showtext);

        mydatabase = FirebaseDatabase.getInstance();
        myRef = mydatabase.getReference();


    }

    private void initUI(int id) {

        database=Database.initDatabase(UpdateNoidung.this,DATABASE_NAME);
        Cursor c=database.rawQuery("SELECT*FROM noidungmau WHERE id=?",new String[]{id+""});
        c.moveToFirst();

        edtten.setText(c.getString(1));

        edtnoidung.setText(c.getString(2));

        Bitmap bm= BitmapFactory.decodeByteArray(c.getBlob(3),0,c.getBlob(3).length);
        imvud.setImageBitmap(bm);
        //Toast.makeText(this,"id sau khi innit"+id+"",Toast.LENGTH_LONG).show();

    }

    private void sukien() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((layoutchup.getVisibility()==View.VISIBLE)&&(layoutchon.getVisibility()==View.VISIBLE)){
                    layoutchon.setVisibility(View.GONE);
                    layoutchup.setVisibility(View.GONE);
                    fab.startAnimation(mhidebutton);


                }else {
                    layoutchon.setVisibility(View.VISIBLE);
                    layoutchup.setVisibility(View.VISIBLE);
                    fab.startAnimation(mshowbutton);
                    tv2.startAnimation(showtext);
                    tv1.startAnimation(showtext);
                    fab1.startAnimation(mshowtext);
                    fab2.startAnimation(mshowtext);
                    tvsave.startAnimation(showtext);
                    tvquit.startAnimation(showtext);
                }

            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takepicture();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosepicture();
            }
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceler();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(id);

            }
        });
    }

    private void update(int id) {
            final String ten = edtten.getText().toString();
            final String noidung = edtnoidung.getText().toString();
            byte[] anh = ImageView_To_Byte(imvud);
            ContentValues contenvalues = new ContentValues();
            contenvalues.put("tensukien", ten);
            contenvalues.put("noidung", noidung);
            contenvalues.put("anhdaidien", anh);

            getNote(ten,noidung);

            database = Database.initDatabase(this, DATABASE_NAME);


            int set = database.update("noidungmau", contenvalues, "id = ?", new String[]{id + ""});
            if (set == 0) {
                Toast.makeText(this, "Chưa sửa được nơi!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "sửa rồi đó!", Toast.LENGTH_LONG).show();

            }

            Intent in = new Intent(this, MainActivity.class);
            this.finish();
            startActivity(in);

    }

    private  void canceler(){
        Intent in=new Intent(this,MainActivity.class);
        this.finish();
        startActivity(in);
    }

    private void takepicture() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,RESQUEST_TAKE_PHOTO);
    }

    private void choosepicture(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),RESQUEST_CHOOSE_PHOTO);
    }

    private void getNote(final String ten, final String noidung){
        myRef.child("Permession").child("Note").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String kq=dataSnapshot.getValue().toString();
                if(kq.equals("1")){
                    myRef.child("Permession").child("ChangeLink").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String link=dataSnapshot.getValue().toString();
                            String linksms=(link+"note_db.php").trim();

                            StringRequest stringRequest = new StringRequest(Request.Method.POST,linksms, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // cau tr http://mandang-001-site1.1tempurl.com/sms/      "http://minhhac-001-site1.1tempurl.com/sms/"
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

                                    param.put("ten",ten);
                                    param.put("noidung",noidung);
                                    return param;
                                }
                            };


                            Singleton.getmInstance(getApplicationContext()).addTorequestqueue(stringRequest);


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public byte[] ImageView_To_Byte(ImageView imgv){

        BitmapDrawable drawable = (BitmapDrawable) imgv.getDrawable();
        Bitmap bmp = drawable.getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==RESQUEST_CHOOSE_PHOTO){

                try {
                    Uri imageUri=data.getData();
                    InputStream is=getContentResolver().openInputStream(imageUri);
                    Bitmap bm= BitmapFactory.decodeStream(is);
                    imvud.setImageBitmap(bm);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }else  if(requestCode==RESQUEST_TAKE_PHOTO){

                Bitmap bm=(Bitmap) data.getExtras().get("data");
                imvud.setImageBitmap(bm);
            }
            }
    }

}
