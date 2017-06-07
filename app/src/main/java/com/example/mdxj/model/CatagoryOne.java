package com.example.mdxj.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import android.os.Environment;

import com.example.mdxj.DwpcApplication;
import com.example.mdxj.util.StorageUtil;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;
import net.tsz.afinal.db.sqlite.DbModel;

@Table(name = "work")
public class CatagoryOne implements Serializable{
	/**
	 * 
	 */
    @Transient
	private static final long serialVersionUID = 5809050595558164234L;
	private String id;
    private String type;
    private String date; 
    private String code; 
    private String personName;
    private String csvPath;
    private int curChildCode = 0;
        
    @Transient
    private ArrayList<CatagoryTwo> childList = new ArrayList<CatagoryTwo>();

    public CatagoryOne() {
    	id = UUID.randomUUID().toString();
    }
        
	public String getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	public String getFolderPath() {
		return date + "_" + type + "_" + code + "_" + personName;
	}
	public String getCsvPath() {
		return getFolderPath();
	}
	public String getName() {
		return type + "_" + code;
	}
	public String getStaticInfo() {
		String info = this.childList.size() + "������";
		
		int picCnt = 0;
		for (CatagoryTwo c : childList) {
			picCnt += c.getChildList().size();
		}
		
		info += " " + picCnt + "����Ƭ";
		
		return info;
	}
	public ArrayList<CatagoryTwo> getChildList() {
		return childList;
	}
	public void setChildList(ArrayList<CatagoryTwo> childList) {
		this.childList = childList;
	}
	public void addChild(CatagoryTwo cg) {
		childList.add(cg);
	}
	public void setCurChildCode(int code) {
		curChildCode = code;
	}
	public int getCurChildCode() {
		return curChildCode;
	}
	public String nextChildCode() {
		curChildCode++;
		return "" + curChildCode;
	}
	public void rollbackChildCode() {
		curChildCode--;
		if (curChildCode < 0) {
			curChildCode = 0;
		}
	}
	
	public void save() {
		FinalDb db = DwpcApplication.getInstance().getDb();
		
		List<CatagoryOne> dbModels = new ArrayList<CatagoryOne>();
		dbModels = db.findAllByWhere(CatagoryOne.class, "id = '" + this.getId() + "'");
		if (dbModels.size() > 0) {
			db.update(this);
		} else {
			db.save(this);
		}
	}
	
	public boolean isExsit() {
		boolean result = false;
		
		FinalDb db = DwpcApplication.getInstance().getDb();

		List<DbModel> dbModels = new ArrayList<DbModel>();
		String sqlStr = "SELECT count(id) as totalcount FROM work where id = '" + this.getId() + "'";
		
		dbModels = (List<DbModel>) db.findDbModelListBySQL(sqlStr);
		if (dbModels.size() != 1) {
			return true;
		}
		
		int count = dbModels.get(0).getInt("totalcount");
		if (count >= 1) {
			result = true;
		}
		
		return result;
	}

	public void delete() {
		for (CatagoryTwo c : childList) {
			c.delete();
		}
		
		String folderPath =  StorageUtil.getOutputMediaPath()
				+ File.separator + getFolderPath();
		StorageUtil.deleteFile(folderPath);
		
		FinalDb db = DwpcApplication.getInstance().getDb();
		db.delete(this);
	}
	
	public String createCsv() {
		String result = "";
		
		String folderPath =  StorageUtil.getOutputMediaPath() 
				+ File.separator + getFolderPath();

		File csvDir = new File(folderPath);
		if (!csvDir.exists()) {
			if (!csvDir.mkdirs()) {
				return "�ļ��С�" + folderPath + "������ʧ�ܡ�";
			} 
		}		
		
		String csvFileName = folderPath+ File.separator + this.getCsvPath() + ".csv";
		this.csvPath = csvFileName;
		
		File file = StorageUtil.createFile(csvFileName);
		if (null == file) {
			return "�ļ���" + csvFileName + "������ʧ�ܡ�";
		}
		
		String content = "";
		BufferedWriter out = null;  
		try {  
		    out = new BufferedWriter(new OutputStreamWriter(  
		            new FileOutputStream(file, true)));  
		    
		    for (CatagoryTwo ct : childList) {
	    		content = ct.getCsvRecord();
			    out.write(content);  
		    }
		    
		    result = "";
		} catch (Exception e) {  
		    e.printStackTrace();  
		    result = e.getMessage();
		} finally {  
		    try {  
		        out.close();  
		    } catch (IOException e) {  
		        e.printStackTrace(); 
			    result = e.getMessage(); 
		    }  
		}  
		
		return result;
	}
	public void setId(String id) {
		this.id = id;
	}

	public void setCsvPath(String csvPath) {
		this.csvPath = csvPath;
	}
}
