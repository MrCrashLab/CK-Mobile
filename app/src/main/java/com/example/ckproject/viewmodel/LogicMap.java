package com.example.ckproject.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.example.ckproject.ParkingService;
import com.example.ckproject.model.Point;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogicMap {
    private String BASE_URL = "http://192.168.0.100:8000";

    public void getMarkers(MutableLiveData<List<Point>> pointsLive){
        List<Point> points = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ParkingService service = retrofit.create(ParkingService.class);
        Call<List<Point>> call = service.getListPoint();
        call.enqueue(new Callback<List<Point>>() {
            @Override
            public void onResponse(Call<List<Point>> call, Response<List<Point>> response) {
                List<Point> myParkingPointList = response.body();
                for (Point i : myParkingPointList){
                    points.add(i);
                }
                pointsLive.setValue(points);
            }
            @Override
            public void onFailure(Call<List<Point>> call, Throwable t) {
                getMarkers(pointsLive);
            }
        });

    }
}
