package com.example.ckproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ckproject.model.Parking;
import com.example.ckproject.model.Point;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class HomeActivity extends AppCompatActivity {
    private MapView mapView;
    private BottomSheetDialog sheet;
    private boolean markerTrackFlag = true;
    private Button parkButton;
    private int idParking = 0;
    public static final int COMFORTABLE_ZOOM_LEVEL = 18;
    private final com.yandex.mapkit.geometry.Point TARGET_LOCATION = new com.yandex.mapkit.geometry.Point(59.952, 30.318);
    private final String BASE_URL = "http://192.168.0.106:8000";
    private Map<MapObject,Point> parkingPointMap = new HashMap<>();
    public interface ParkingService {
        @GET("points")
        Call<List<Point>> getListPoint();
        @GET("parkings/{id_point}")
        Call<Parking> getParking(@Path("id_point") int id_point);
    }
    private MapObjectTapListener listener = new ParkingTapListener(sheet, parkButton, idParking, parkingPointMap, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("3a3187b3-3c9c-48d4-90f2-c4bb76205eb8");
        setContentView(R.layout.activity_home);
        MapKitFactory.initialize(this);
        mapView = (MapView)findViewById(R.id.mapView);
        mapView.getMap().move(
                new CameraPosition(TARGET_LOCATION, 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        MapKit mapKit = MapKitFactory.getInstance();
        drawAllMarkers(mapView);
        mapView.getMap().getMapObjects().addTapListener(listener);
    }
    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    private void drawAllMarkers(MapView mapView) {
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
                    MapObject mapObject = mapView.getMap().getMapObjects().addPlacemark(new com.yandex.mapkit.geometry.Point(i.getLatitude(),i.getLongitude()),ImageProvider.fromResource(
                            getApplicationContext(), R.drawable.pin), new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                            .setRotationType(RotationType.NO_ROTATION)
                            .setZIndex(1f)
                            .setScale(1.0f));
                    parkingPointMap.put(mapObject, i);
                }
            }
            @Override
            public void onFailure(Call<List<Point>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "You have internet problem!\n", Toast.LENGTH_SHORT).show();
            }
        });
    }
}