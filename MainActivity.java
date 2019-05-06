package com.example.hac.notebook;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = mdatabase.getReference();
    String urllink;

    final String DATABASE_NAME ="Notebook.sqlite";
    SQLiteDatabase database;

    ListView lv;
    ArrayList<Noidung> list;
    AdapteNote adapter;

    FloatingActionButton fa,fa1,fa2,fa3;
    LinearLayout layoutnote,layoutdoc,layouthuongdan;

    Animation mshowbutton,mhidebutton,mshowtext,showtext;
    TextView tv1,tv2,tv3,textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhxa();                                                                   // lier avec graphique 
        readdata();                                                                // read data
        sukien();                                                                  // evenement 
    }


    private void sukien() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chuyen=new Intent(MainActivity.this,UpdateNoidung.class);
                chuyen.putExtra("id",list.get(position).id);
                startActivity(chuyen);
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                int z=list.get(position).id;
                delete(z);
                return true;
            }
        });


        fa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((layoutnote.getVisibility()==View.VISIBLE)&&(layoutdoc.getVisibility()==View.VISIBLE)&&(layouthuongdan.getVisibility()==View.VISIBLE)){
                    layoutdoc.setVisibility(View.GONE);
                    layoutnote.setVisibility(View.GONE);
                    layouthuongdan.setVisibility(View.GONE);
                    fa.startAnimation(mhidebutton);
                }else {
                    layoutdoc.setVisibility(View.VISIBLE);
                    layoutnote.setVisibility(View.VISIBLE);
                    layouthuongdan.setVisibility(View.VISIBLE);

                    fa.startAnimation(mshowbutton);
                    fa1.startAnimation(mshowtext);
                    fa2.startAnimation(mshowtext);
                    fa3.startAnimation(mshowtext);

                    tv1.startAnimation(showtext);
                    tv2.startAnimation(showtext);
                    tv3.startAnimation(showtext);
                }
            }
        });

        fa1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add=new Intent(MainActivity.this,AddNoidung.class);
                startActivity(add);
                fa1.hide();
                fa2.hide();
                fa3.hide();

            }
        });

        fa2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        fa3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"1.Chọn nút + ở góc phải phía trên xuất hiện 3 mục: add, quit, help.\n -Chọn Add để thêm.\n -Chọn Quit để thoát.\n -Chọn Help để đọc cái này!",Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this,"2.Nhấn đầu mục để xem nội dung.",Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this,"3.Nhấn lâu để xóa.",Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this,"4.Chọn Take Photo de chup ảnh.",Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this,"5.Chọn Photo đê lấy ảnh có sẵn.",Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this,"6.Chon Save để lưu.",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void delete(final int id) {

        if(id==1){
            Toast.makeText(MainActivity.this,"Em có thể sửa cái ni!",Toast.LENGTH_LONG).show();
        }
        else {
            AlertDialog.Builder buider=new AlertDialog.Builder(this);
            buider.setTitle("Hỏi lại 1 lần nửa:");
            buider.setMessage("Em chắc chắn muốn xóa à?");
            buider.setCancelable(false);
            buider.setPositiveButton("Ukm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    SQLiteDatabase database=Database.initDatabase(MainActivity.this,DATABASE_NAME);
                    int dele=database.delete("noidungmau","id=?",new String[] {id+""});
                    if(dele!=0)  Toast.makeText(MainActivity.this,"Xóa rồi đó!",Toast.LENGTH_LONG).show();

                    readdata();


                }
            });
            buider.setNegativeButton("không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    Toast.makeText(MainActivity.this,"da chon no",Toast.LENGTH_LONG).show();

                }
            });
            AlertDialog dialo=buider.create();
            dialo.show();
        }
    }

    private void anhxa() {
        lv=(ListView)findViewById(R.id.lv);

        list=new ArrayList<Noidung>();
        adapter=new AdapteNote(this,list);
        lv.setAdapter(adapter);

        fa=(FloatingActionButton)findViewById(R.id.fa);
        fa1=(FloatingActionButton)findViewById(R.id.fa1);
        fa2=(FloatingActionButton)findViewById(R.id.fa2);
        fa3=(FloatingActionButton)findViewById(R.id.fa3);

        layoutnote =(LinearLayout)findViewById(R.id.layoutnote);
        layoutdoc =(LinearLayout)findViewById(R.id.layoutdoc);
        layouthuongdan =(LinearLayout)findViewById(R.id.layouthuongdan);

        mshowbutton= AnimationUtils.loadAnimation(MainActivity.this,R.anim.show_button);
        mhidebutton= AnimationUtils.loadAnimation(MainActivity.this,R.anim.hide_button);
        mshowtext= AnimationUtils.loadAnimation(this,R.anim.show_text);
        showtext= AnimationUtils.loadAnimation(this,R.anim.showtext);

        tv1=(TextView)findViewById(R.id.textView);
        tv2=(TextView)findViewById(R.id.textView2);
        tv3=(TextView)findViewById(R.id.tv3);


    }

    private void readdata() {
        database=Database.initDatabase(MainActivity.this,DATABASE_NAME);
        Cursor c=database.rawQuery("SELECT*FROM noidungmau",null);
        list.clear();

        for (int i=0;i<c.getCount();i++)
        {
            c.moveToPosition(i);
            int idnd=c.getInt(0);
            String ten=c.getString(1);
            String noidung=c.getString(2);
            byte[] anh=c.getBlob(3);
            list.add(new Noidung(idnd,ten,noidung,anh));


        }
        adapter.notifyDataSetChanged();
        c.close();


    }

}
