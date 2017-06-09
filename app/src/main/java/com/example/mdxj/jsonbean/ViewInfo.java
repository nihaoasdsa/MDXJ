package com.example.mdxj.jsonbean;

public class ViewInfo {	
	private String name;
	private String image;
	private float lat;//经度
	private float lng;//纬度


	public ViewInfo(String name, float lat, float lng) {
		super();
		this.name = name;
		this.lat = lat;
		this.lng = lng;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getLat() {
		return lat;
	}
	public void setLat(float lat) {
		this.lat = lat;
	}
	public float getLng() {
		return lng;
	}
	public void setLng(float lng) {
		this.lng = lng;
	}
	
	

}
