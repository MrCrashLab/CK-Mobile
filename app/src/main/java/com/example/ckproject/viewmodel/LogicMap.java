package com.example.ckproject.viewmodel;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.ckproject.MapService;
import com.example.ckproject.ParkingService;
import com.example.ckproject.R;
import com.example.ckproject.listener.ButtonTapListener;
import com.example.ckproject.model.Parking;
import com.example.ckproject.model.Point;
import com.example.ckproject.view.ParkingMapActivity;
import com.squareup.picasso.Picasso;
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
            }
            @Override
            public void onFailure(Call<Parking> call, Throwable t) {
                getParkingInfo(parking, parkingPointMap, mapObject);
            }
        });
    }
    public void getParkingMap(int id, MutableLiveData<List<com.example.ckproject.model.Map>> maps){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MapService service = retrofit.create(MapService.class);
        Call<List<com.example.ckproject.model.Map>> call = service.getMap(id);
        call.enqueue(new Callback<List<com.example.ckproject.model.Map>>() {
            @Override
            public void onResponse(Call<List<com.example.ckproject.model.Map>> call, Response<List<com.example.ckproject.model.Map>> response) {
                maps.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<com.example.ckproject.model.Map>> call, Throwable t) {
                getParkingMap(id, maps);
            }
        });
    }
}
