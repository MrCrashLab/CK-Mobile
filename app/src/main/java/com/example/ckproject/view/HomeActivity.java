package com.example.ckproject.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

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
    private MutableLiveData<Map<String, Integer>> parkingNamesLive = new MutableLiveData<>();
    private Map<String, Integer> parkingNames = new HashMap<>();
    private MapObjectTapListener listener = new ParkingTapListener(sheet, parkButton, idParking, parkingPointMap, this);
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("3a3187b3-3c9c-48d4-90f2-c4bb76205eb8");
        setContentView(R.layout.activity_home);
        MapKitFactory.initialize(this);
        searchView = findViewById(R.id.editTextSearch);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (parkingNames.keySet().contains(s) && pointsLive.getValue() != null) {
                    double lati = 59.952, longi = 30.318;
                    for (Point i : pointsLive.getValue()) {
                        if (i.getId() == parkingNames.get(s)) {
                            lati = i.getLatitude();
                            longi = i.getLongitude();
                            break;
                        }
                    }
                    mapView.getMap().move(
                            new CameraPosition(new com.yandex.mapkit.geometry.Point(lati, longi), 20.0f, 0.0f, 0.0f),
                            new Animation(Animation.Type.SMOOTH, 0),
                            null);
                } else {
                    Toast.makeText(HomeActivity.this, "Нет такой парковки", Toast.LENGTH_SHORT).show();
                }
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                searchView.setQuery("", false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMap().move(
                new CameraPosition(TARGET_LOCATION, 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        MapKit mapKit = MapKitFactory.getInstance();
        logicMap.getMarkers(pointsLive);
        logicMap.getParkingsName(parkingNamesLive);
        pointsLive.observe(this, new Observer<List<Point>>() {
            @Override
            public void onChanged(List<Point> points) {
                drawAllMarkers(mapView, points);
            }
        });
        parkingNames.keySet().toArray();
        parkingNamesLive.observe(this, new Observer<Map<String, Integer>>() {
            @Override
            public void onChanged(Map<String, Integer> stringIntegerMap) {
                parkingNames = stringIntegerMap;
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