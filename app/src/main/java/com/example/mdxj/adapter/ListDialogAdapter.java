package com.example.mdxj.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mdxj.R;

import java.util.List;

@SuppressLint("InflateParams")
public class ListDialogAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private int curSelected;
    private ImageView curIV;

    List<String> vbList;
    List<Bitmap>bitmapList;
    public ListDialogAdapter(Context context, List<String> mt, List<Bitmap>bitmapList, int cur) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.bitmapList=bitmapList;
        vbList = mt;
        curSelected = cur;
    }

    public ListDialogAdapter(Context context, List<String> mt) {
        this.context = context;
        inflater = LayoutInflater.from(context);

        vbList = mt;
        curSelected = 0;
    }
    
    @Override
    public int getCount() {
        return vbList.size();
    }

    @Override
    public String getItem(int position) {
        return vbList.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_dialog_item, null);

            holder = new ViewHolder();
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.iv_content = (ImageView) convertView.findViewById(R.id.iv_content);
            holder.img=(ImageView)convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_content.setText(getItem(position));
        if(bitmapList!=null&&bitmapList.size()>0)
        holder.img.setImageBitmap(bitmapList.get(position));
        if (curSelected == position) {
            holder.iv_content.setVisibility(View.VISIBLE);
        } else {
            holder.iv_content.setVisibility(View.GONE);
        }

        return convertView;
    }
    
    private static class ViewHolder {
        TextView tv_content;
        ImageView iv_content,img;
    }
    

}
