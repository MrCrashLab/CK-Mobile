package com.example.ckproject;

import com.example.ckproject.model.Map;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MapService {
    @GET("map/{parking_id}")
    Call<List<Map>> getMap(@Path("parking_id") int parking_id);
}
