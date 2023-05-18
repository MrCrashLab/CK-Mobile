package com.example.ckproject.viewmodel;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.ckproject.ParkingService;
import com.example.ckproject.R;
import com.example.ckproject.model.Parking;
import com.example.ckproject.model.Point;
import com.yandex.mapkit.map.MapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogicMap {
    private final String BASE_URL = "http://192.168.0.100:8000";

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

    public void getParkingInfo(MutableLiveData<Parking> parking, Map<MapObject, Point> parkingPointMap, MapObject mapObject){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        int id = parkingPointMap.get(mapObject).getId();
        Log.println(Log.ASSERT, "Ya Ustal", String.valueOf(id));
        ParkingService service = retrofit.create(ParkingService.class);
        Call<Parking> call = service.getParking(id);
        call.enqueue(new Callback<Parking>() {
            @Override
            public void onResponse(Call<Parking> call, Response<Parking> response) {
                parking.setValue(response.body());
//                TextView nameParking = (TextView) sheetView.findViewById(R.id.nameParking);
//                TextView descParking = (TextView) sheetView.findViewById(R.id.descParking);
//                TextView freeText = (TextView) sheetView.findViewById(R.id.freeText);
//                TextView allText = (TextView) sheetView.findViewById(R.id.allText);
//                TextView brokeText = (TextView) sheetView.findViewById(R.id.brokeText);
//                idParking = response.body().getId();
//                nameParking.setText(response.body().getName());
//                descParking.setText(response.body().getDescription());
//                freeText.setText(String.valueOf(response.body().getFree_slot()));
//                allText.setText(String.valueOf(response.body().getAll_slot()));
//                brokeText.setText(String.valueOf(response.body().getAll_slot() - response.body().getFree_slot()));
            }

            @Override
            public void onFailure(Call<Parking> call, Throwable t) {
                getParkingInfo(parking, parkingPointMap, mapObject);
            }
        });
    }
}
