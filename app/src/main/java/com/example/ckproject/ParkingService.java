package com.example.ckproject;

import com.example.ckproject.model.Parking;
import com.example.ckproject.model.Point;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ParkingService {
    @GET("points")
    Call<List<Point>> getListPoint();
    @GET("parkings")
    Call<List<Parking>> getListParking();
    @GET("parkings/{id_point}")
    Call<Parking> getParking(@Path("id_point") int id_point);
}
