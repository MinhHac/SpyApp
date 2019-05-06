package com.example.hac.notebook;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddNoidung extends AppCompatActivity {

    final String DATABASE_NAME ="Notebook.sqlite";
    SQLiteDatabase database;
    final int RESQUEST_TAKE_PHOTO =123;
    final int RESQUEST_CHOOSE_PHOTO=321;
    TextView tv1,tv2,tvsave,tvquit;
    EditText edtten,edtnoidung;
    ImageView imvud;
    FloatingActionButton fab1,fab2,fab3,fab4,fab;
    LinearLayout layoutchup,layoutchon;

    Animation mshowbutton,mhidebutton,mshowtext,showtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_noidung);
        anhxa();
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

                insert();

            }
        });
    }

    private void insert() {
        final String ten=edtten.getText().toString();
        final String noidung=edtnoidung.getText().toString();
        byte[] anh=ImageView_To_Byte(imvud);

        ContentValues contenvalues=new ContentValues();
        contenvalues.put("tensukien",ten);
        contenvalues.put("noidung",noidung);
        contenvalues.put("anhdaidien",anh);

        database=Database.initDatabase(this,DATABASE_NAME);

        long set= database.insert("noidungmau",null,contenvalues);
       if(set==0) {Toast.makeText(this,"chua them duoc!",Toast.LENGTH_LONG).show();}
        else {Toast.makeText(this,"da them!",Toast.LENGTH_LONG).show();}

        Intent in=new Intent(this,MainActivity.class);
        this.finish();
        startActivity(in);

    }

    private  void canceler(){
        Intent in=new Intent(this,MainActivity.class);
        this.finish();
        startActivity(in);
    }
    private void takepicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,RESQUEST_TAKE_PHOTO);
    }
    private void choosepicture(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,RESQUEST_CHOOSE_PHOTO);
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
