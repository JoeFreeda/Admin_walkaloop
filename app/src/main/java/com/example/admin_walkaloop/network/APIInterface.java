package com.example.admin_walkaloop.network;

import com.example.admin_walkaloop.model.MarkersItem;
import com.example.admin_walkaloop.model.Response;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIInterface {

    @POST("save")
    Call<String> insertMarker (@Body Response response);

    @GET("GetAll")
    Call<MarkersItem> getallDetails();
}
