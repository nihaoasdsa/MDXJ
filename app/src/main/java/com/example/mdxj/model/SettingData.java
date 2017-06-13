package com.example.mdxj.model;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingData{
    public static final String PREFERENCE_NAME = "local_setting";
    private static SharedPreferences mSP = null;
    private static SharedPreferences.Editor editor = null;
    
	private String personName;
    private String workType;
    private String allowAllDelete; 
    private int picWidth; 
    private int picHeight;
    private String picSize;
    private String rootPath;
    private int startIndex;
    private SharedPreferences sp;
    public SettingData() {
    	personName = "";
    	workType = "低压";
    	picSize = "720 x 1280";
    	allowAllDelete = "否";
    	rootPath = "DWPC";
    	startIndex = 1;
    }
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
		editor.putString("personName", personName); 
		editor.commit();
	}
	public String getWorkType() {
		return workType;
	}
	public void setWorkType(String workType) {
		this.workType = workType;
		editor.putString("workType", workType); 
		editor.commit();
	}
	public int getPicWidth() {
		return picWidth;
	}
	public void setPicWidth(int picWidth) {
		this.picWidth = picWidth;
	}
	public int getPicHeight() {
		return picHeight;
	}
	public void setPicHeight(int picHeight) {
		this.picHeight = picHeight;
	}
	public String getPicSize() {
		return picSize;
	}
	public void setPicSize(String picSize) {
		this.picSize = picSize;
		editor.putString("picSize", picSize); 
		editor.commit();
		
		if ("540 x 960".equals(picSize)) {
			picWidth = 540;
			picHeight = 960;
		} else if("720 x 1280".equals(picSize)) {
			picWidth = 720;
			picHeight = 1280;
		} else if("1080 x 1920".equals(picSize)) {
			picWidth = 1080;
			picHeight = 1920;
		}
		
	}
	public String getRootPath() {
		return rootPath;
	}
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
		editor.putString("rootPath", rootPath); 
		editor.commit();
	}
	public String getAllowAllDelete() {
		return allowAllDelete;
	}
	public void setAllowAllDelete(String allowAllDelete) {
		this.allowAllDelete = allowAllDelete;
		editor.putString("allowAllDelete", allowAllDelete); 
		editor.commit();
	}
	public void init(Context cxt) {
		mSP = cxt.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
		editor = mSP.edit();
		sp = cxt.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		this.setPersonName(sp.getString("USER_NAME", ""));
		this.setWorkType(mSP.getString("workType", this.workType));
		this.setPicSize(mSP.getString("picSize", this.picSize));
		this.setAllowAllDelete(mSP.getString("allowAllDelete", this.allowAllDelete));
		this.setRootPath(mSP.getString("rootPath", this.rootPath));
		this.setStartIndex(mSP.getInt("startIndex", this.startIndex));
	}
	public void toDefault() {
		SettingData s = new SettingData();
		
		this.setPersonName(sp.getString("USER_NAME", ""));
		this.setWorkType(s.getWorkType());
		this.setPicSize(s.getPicSize());
		this.setAllowAllDelete(s.getAllowAllDelete());
		this.setRootPath(s.getRootPath());
	}
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
		editor.putInt("startIndex", startIndex); 
		editor.commit();
	}
}
