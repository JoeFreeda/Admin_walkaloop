package com.example.admin_walkaloop.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MarkersItem{
	public List<Response> getListofplaces() {
		return listofplaces;
	}

	public void setListofplaces(List<Response> listofplaces) {
		this.listofplaces = listofplaces;
	}

	@SerializedName("listofplaces")
	private List<Response> listofplaces;

}
