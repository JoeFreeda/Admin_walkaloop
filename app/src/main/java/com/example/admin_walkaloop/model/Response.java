package com.example.admin_walkaloop.model;

import com.google.android.gms.location.places.Place;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Response {
	@SerializedName("Place")
	private String Place;
	@SerializedName("Latitude")
	private String Latitude;
	@SerializedName("Longitude")
	private String Longitude;
	@SerializedName("Isdefault")
	private int Isdefault;
	@SerializedName("SubPlace")
	private List<PlaceInfo> SubPlace;
	@SerializedName("NoofPlaces")
	private int NoofPlaces;

	public int getNoofPlaces() {
		return NoofPlaces;
	}

	public void setNoofPlaces(int noofPlaces) {
		NoofPlaces = noofPlaces;
	}

	public String getpName() {
		return Place;
	}

	public void setpName(String pName) {
		this.Place = pName;
	}

	public String getLatitude() {
		return Latitude;
	}

	public void setLatitude(String latitude) {
		this.Latitude = latitude;
	}

	public String getLongitude() {
		return Longitude;
	}

	public void setLongitude(String longitude) {
		this.Longitude = longitude;
	}


	public void setIsdefault(int isdefault){
		this.Isdefault = isdefault;
	}

	public int getIsdefault(){
		return Isdefault;
	}

	public void setMarkers(List<PlaceInfo> markers){
		this.SubPlace = markers;
	}

	public List<PlaceInfo> getMarkers(){
		return SubPlace;
	}

}