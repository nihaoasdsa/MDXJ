package com.example.mdxj.model;

import com.example.mdxj.DwpcApplication;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;

@Table(name = "zuobiao")
public class CatagoryTwo implements Serializable{
	/**
	 * 
	 */
    @Transient
	private static final long serialVersionUID = 7456902205531697937L;
	private String parentId;
	private String destPath;
    private String id;
    private String type; 
    private String code; 
    private String personName;
    private String lat;
    private String lng;
    private String alt;
    private String updateTime;
    private int curChildCode = 0;

    @Transient
    private ArrayList<CatagoryThree> childList = new ArrayList<CatagoryThree>();
    
    public CatagoryTwo() {
    	id = UUID.randomUUID().toString();
    }
    
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getName() {
		return type + "_" + code;
	}
	public String getStaticInfo() {
		String info = this.childList.size() + "����Ƭ";
		if (null != lat && !"".equals(lat)) {
			String s2 = " ����:" + lng + " γ��:" + lat;
			
			for (CatagoryThree c : childList) {
				if (null == c.getLat() || "".equals(c.getLat())) {
					s2 = "  �ȴ���λ����";
					break;
				}
			}
			
			info += s2;
		} else {
			info += "  �ȴ���λ����";
		}
		
		return info;
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
	public ArrayList<CatagoryThree> getChildList() {
		return childList;
	}
	public void setChildList(ArrayList<CatagoryThree> childList) {
		this.childList = childList;
	}
	public void addChild(CatagoryThree cg) {
		childList.add(cg);
	}
	public String getDestPath() {
		return destPath;
	}
	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}
	
	public void save() {
		FinalDb db = DwpcApplication.getInstance().getDb();
		
		List<CatagoryTwo> dbModels = new ArrayList<CatagoryTwo>();
		dbModels = db.findAllByWhere(CatagoryTwo.class, "id = '" + this.getId() + "'");
		if (dbModels.size() > 0) {
			db.update(this);
		} else {
			db.save(this);
		}
	}
	
	public void update() {
		FinalDb db = DwpcApplication.getInstance().getDb();
		db.update(this);
	}

	public void delete() {
		for (CatagoryThree c : childList) {
			c.delete();
		}
		
		FinalDb db = DwpcApplication.getInstance().getDb();
		db.delete(this);
	}
	public String getCsvRecord() {
		return type + "," + code + "," + getChildCsv() + ","  + lng + "," + lat + "," + alt + ","  + updateTime + "," + personName + "\n";
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}
	
	public String getChildCsv() {
		String csv = "";		
		String pName = "";
		
		for (CatagoryThree c : childList) {
			pName = c.getOriFilePath();
			

			pName= pName.substring(pName.lastIndexOf(File.separator) + 1, pName.lastIndexOf("."));
			
			csv += pName + " ";
		}
		csv = csv.substring(0, csv.length() - 1);
		
		return csv;
	}
}
