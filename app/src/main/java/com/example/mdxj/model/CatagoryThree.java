package com.example.mdxj.model;

import com.example.mdxj.DwpcApplication;
import com.example.mdxj.util.StorageUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;

@Table(name = "picture")
public class CatagoryThree implements Serializable{
	/**
	 * 
	 */
    @Transient
	private static final long serialVersionUID = 7250819482152553328L;
	private String parentId;
	private String parentCode;
	private String destPath;
    private String id;
    private String type; 
    private String code; 
    private String personName;
    private String lat;
    private String lng;
    private String alt;
    private String updateTime;
    private String oriFilePath;
    private String thumbnailSmallPath;
    private String thumbnailBigPath;
    private Boolean isSaved;

    public CatagoryThree() {
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
	public String getOriFilePath() {
		return oriFilePath;
	}
	public void setOriFilePath(String oriFilePath) {
		this.oriFilePath = oriFilePath;
	}
	public String getThumbnailSmallPath() {
		return thumbnailSmallPath;
	}
	public void setThumbnailSmallPath(String thumbnailSmallPath) {
		this.thumbnailSmallPath = thumbnailSmallPath;
	}
	public String getThumbnailBigPath() {
		return thumbnailBigPath;
	}
	public void setThumbnailBigPath(String thumbnailBigPath) {
		this.thumbnailBigPath = thumbnailBigPath;
	}
	public String getDestFileName() {
		return type + "_" + parentCode + "_" + code + "_" + lng + "_" + lat;
	}
	public String getName() {
		String name = parentCode + "_" + code;
		if (null == type || "".equals(type)) {
			return name;
		}
		return type + "_" + name;
	}
	public String getStaticInfo() {
		if (null == lat || "".equals(lat)) {
			return "�ȴ���λ����...";
		}
		return "����:" + lng + " γ��:" + lat;
	}
	public Boolean getIsSaved() {
		return isSaved;
	}
	public void setIsSaved(Boolean isSaved) {
		this.isSaved = isSaved;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public String getDestPath() {
		return destPath;
	}
	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}
	
	public void save() {
		setIsSaved(true);

        String kzm = oriFilePath.substring(oriFilePath.lastIndexOf("."), oriFilePath.length());
        
        String destFilePath = StorageUtil.getOutputMediaPath() + File.separator +
				getDestPath() + File.separator + getDestFileName() + kzm;
		StorageUtil.moveFile(oriFilePath, destFilePath);
		
		setOriFilePath(destFilePath);
		
		FinalDb db = DwpcApplication.getInstance().getDb();
		
		List<CatagoryThree> dbModels = new ArrayList<CatagoryThree>();
		dbModels = db.findAllByWhere(CatagoryThree.class, "id = '" + this.getId() + "'");
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
		StorageUtil.deleteFile(this.getOriFilePath());
		StorageUtil.deleteFile(this.getThumbnailBigPath());
		StorageUtil.deleteFile(this.getThumbnailSmallPath());
		
		FinalDb db = DwpcApplication.getInstance().getDb();
		db.delete(this);
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}
}
