package com.example.mdxj.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mdxj.R;
import com.example.mdxj.model.CatagoryOne;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("InflateParams")
public class CatagoryOneAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    
    List<CatagoryOne> cgList;
    private List<Integer> selectedList = new ArrayList<Integer>();
    
    private boolean isSelecting = false;
    private static final int REQUEST_CATAGORY_TWO = 1;

    public CatagoryOneAdapter(Context context, List<CatagoryOne> mt) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        
        cgList = mt;
    }
    
    @Override
    public int getCount() {
        return cgList.size();
    }

    @Override
    public CatagoryOne getItem(int position) {
        return cgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_catagoryone, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_unread = (TextView) convertView.findViewById(R.id.tv_unread);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.iv_avatar_low = (ImageView) convertView.findViewById(R.id.iv_avatar_low);
            holder.iv_avatar_middle=(ImageView)convertView.findViewById(R.id.iv_avatar_middle);
            holder.iv_avatar_high=(ImageView)convertView.findViewById(R.id.iv_avatar_high);
            holder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CatagoryOne cg = getItem(position);
        
        holder.tv_name.setText(cg.getName());
        holder.tv_time.setText(cg.getDate());
        holder.tv_content.setText(cg.getStaticInfo());
        String name=cg.getName().substring(0,2);
        if(name.equals("低压")){
            holder.iv_avatar_low.setVisibility(View.VISIBLE);
        }else if(name.equals("中压")){
            holder.iv_avatar_middle.setVisibility(View.VISIBLE);
        }else {
            holder.iv_avatar_high.setVisibility(View.VISIBLE);
        }
//        if (cg.getChildList().size() > 0) {
//            holder.iv_avatar.setImageResource(R.drawable.folder_open);
//        } else {
//            holder.iv_avatar.setImageResource(R.drawable.folder_close);
//        }
        
        if (isSelecting) {
        	holder.iv_select.setVisibility(View.VISIBLE);
        	if (isSelected(position)) {
                holder.iv_select.setImageResource(R.drawable.dx_checkbox_on);
        	} else {
                holder.iv_select.setImageResource(R.drawable.dx_checkbox_gray_on);
        	}
        } else {
        	holder.iv_select.setVisibility(View.GONE);
        }
        return convertView;
    }
    
    public boolean isSelecting() {
		return isSelecting;
	}

	public void setSelecting(boolean isSelecting) {
		this.isSelecting = isSelecting;
	}

	public void changeSelectItem(int pos) {
		boolean result = false;

    	for (Integer i : selectedList) {
    		if (i.intValue() == pos) {
    			result = true;
    			selectedList.remove(i);
    			break;
    		}
    	}
    	
    	if (!result) {
    		selectedList.add(pos);
    	}
	}

	public void addSelectItem(int pos) {
		selectedList.add(pos);
	}
	
	public void clearSelectItem() {
		selectedList.clear();;
	}
	
	public int getSelectCount() {
		return selectedList.size();
	}
	
	public boolean isSelected(int pos) {
		boolean result = false;

    	for (Integer i : selectedList) {
    		if (i.intValue() == pos) {
    			result = true;
    			break;
    		}
    	}
		
		return result;
	}
	
	public int getSelectItem(int i) {
		return selectedList.get(i);
	}

	private static class ViewHolder {
        TextView tv_name;
        TextView tv_unread;
        TextView tv_content;
        TextView tv_time;
        ImageView iv_avatar_low,iv_avatar_middle,iv_avatar_high;
        ImageView iv_select;
    }
}
