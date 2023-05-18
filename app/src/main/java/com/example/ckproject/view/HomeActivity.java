package com.example.ckproject.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.graphics.PointF;
import android.os.Bundle;
import android.widget.Button;

import com.example.ckproject.listener.ParkingTapListener;
import com.example.ckproject.R;
import com.example.ckproject.model.Point;
import com.example.ckproject.viewmodel.LogicMap;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {
    private MapView mapView;
    private BottomSheetDialog sheet;
    private Button parkButton;
    private int idParking = 0;
    LogicMap logicMap = new LogicMap();
    private final com.yandex.mapkit.geometry.Point TARGET_LOCATION = new com.yandex.mapkit.geometry.Point(59.952, 30.318);
    private Map<MapObject, Point> parkingPointMap = new HashMap<>();
    private MutableLiveData<List<Point>> pointsLive = new MutableLiveData<>();
    private MapObjectTapListener listener = new ParkingTapListener(sheet, parkButton, idParking, parkingPointMap, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("3a3187b3-3c9c-48d4-90f2-c4bb76205eb8");
        setContentView(R.layout.activity_home);
        MapKitFactory.initialize(this);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMap().move(
                new CameraPosition(TARGET_LOCATION, 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        MapKit mapKit = MapKitFactory.getInstance();
        logicMap.getMarkers(pointsLive);
        pointsLive.observe(this, new Observer<List<Point>>() {
            @Override
            public void onChanged(List<Point> points) {
                drawAllMarkers(mapView, points);
            }
        });
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

    private void drawAllMarkers(MapView mapView, List<Point> points) {
        for (Point i : points) {
            MapObject mapObject = mapView.getMap().getMapObjects().addPlacemark(new com.yandex.mapkit.geometry.Point(i.getLatitude(), i.getLongitude()), ImageProvider.fromResource(
                    getApplicationContext(), R.drawable.pin), new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                    .setRotationType(RotationType.NO_ROTATION)
                    .setZIndex(1f)
                    .setScale(1.0f));
            parkingPointMap.put(mapObject, i);
        }
    }
}