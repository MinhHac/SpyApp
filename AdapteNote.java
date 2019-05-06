package com.example.hac.notebook;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Hac on 18/03/2017.
 */
public class AdapteNote extends BaseAdapter {
    Activity context;
    ArrayList<Noidung> list;

    public AdapteNote(Activity context, ArrayList<Noidung> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row= inflater.inflate(R.layout.customlisview,null);
        ImageView imv=(ImageView) row.findViewById(R.id.imv);
        TextView tvten=(TextView) row.findViewById(R.id.tvten);
        TextView tvnd=(TextView) row.findViewById(R.id.tvnd);

        Noidung nd = list.get(position);

        tvten.setText(nd.tensukien);
        tvnd.setText(nd.noidung);


        Bitmap bm= BitmapFactory.decodeByteArray(nd.hinhdaidien,0,nd.hinhdaidien.length);
        imv.setImageBitmap(bm);
        return row;
    }
}

