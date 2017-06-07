package com.example.mdxj.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.mdxj.R;
import com.example.mdxj.model.CatagoryTwo;
import com.example.mdxj.util.StorageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class CatagoryTwoAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    
    List<CatagoryTwo> cgList;
    private List<Integer> selectedList = new ArrayList<Integer>();
    
    private boolean isSelecting = false;
    private static final int REQUEST_CATAGORY_THREE_EDIT = 2;

    public CatagoryTwoAdapter(Context context, List<CatagoryTwo> mt) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        
        cgList = mt;
    }
    
    @Override
    public int getCount() {
        return cgList.size();
    }

    @Override
    public CatagoryTwo getItem(int position) {
        return cgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_catagorytwo, null);
            
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_unread = (TextView) convertView.findViewById(R.id.tv_unread);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.iv_avatarL = (ImageView) convertView.findViewById(R.id.iv_avatarL);
            holder.iv_avatarR = (ImageView) convertView.findViewById(R.id.iv_avatarR);
            holder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CatagoryTwo cg = getItem(position);
        
        holder.tv_name.setText(cg.getName());
        holder.tv_time.setText(cg.getUpdateTime());
        holder.tv_content.setText(cg.getStaticInfo());

    	holder.iv_avatar.setImageResource(R.drawable.catagorytwo);
        if (cg.getChildList().size() > 0) {
        	holder.iv_avatarL.setVisibility(View.VISIBLE);

	        ImageLoader.getInstance().displayImage(StorageUtil.PATH_PREFIX +
	        		cg.getChildList().get(0).getThumbnailSmallPath(), holder.iv_avatarL);
        } else {
        	holder.iv_avatarL.setVisibility(View.GONE);
        	holder.iv_avatarR.setVisibility(View.GONE);
        }
        
        if (cg.getChildList().size() > 1) {
        	holder.iv_avatarR.setVisibility(View.VISIBLE);

	        ImageLoader.getInstance().displayImage(StorageUtil.PATH_PREFIX + 
	        		cg.getChildList().get(1).getThumbnailSmallPath(), holder.iv_avatarR);
        }  else {
        	holder.iv_avatarR.setVisibility(View.GONE);
        }

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
    
    private static class ViewHolder {
        TextView tv_name;
        TextView tv_unread;
        TextView tv_content;
        TextView tv_time;
        ImageView iv_avatar;
        ImageView iv_avatarL;
        ImageView iv_avatarR;
        ImageView iv_select;
    }
}
