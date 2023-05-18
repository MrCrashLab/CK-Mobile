package com.example.ckproject.listener;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.ckproject.R;
import com.example.ckproject.view.ParkingMapActivity;
import com.example.ckproject.model.Parking;
import com.example.ckproject.viewmodel.LogicMap;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;

import java.util.HashMap;
import java.util.Map;

public class ParkingTapListener implements MapObjectTapListener {
    private BottomSheetDialog sheet;
    private Button parkButton;
    private int idParking = 0;
    private LogicMap logicMap = new LogicMap();
    private Map<MapObject, com.example.ckproject.model.Point> parkingPointMap = new HashMap<>();
    private MutableLiveData<Parking> parking = new MutableLiveData<>();
    private AppCompatActivity currentActivity;

    public ParkingTapListener(BottomSheetDialog sheet, Button parkButton, int idParking, Map<MapObject, com.example.ckproject.model.Point> parkingPointMap, AppCompatActivity currentActivity) {
        this.sheet = sheet;
        this.parkButton = parkButton;
        this.idParking = idParking;
        this.parkingPointMap = parkingPointMap;
        this.currentActivity = currentActivity;
    }

    @Override
    public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
        parking = new MutableLiveData<>();
        sheet = new BottomSheetDialog(currentActivity, R.style.BottomSheetTheme);
        View sheetView = LayoutInflater.from(currentActivity.getApplicationContext()).inflate(R.layout.bottom_dialog, null);
        parkButton = sheetView.findViewById(R.id.parkButton);
        parkButton.setEnabled(false);
        parkButton.setBackgroundResource(R.drawable.button_rounded_dis);


        parkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheet.dismiss();
                Log.println(Log.ASSERT, "Click", "casdas");
                Intent i = new Intent(currentActivity, ParkingMapActivity.class);
                i.putExtra("idParking", idParking);
                currentActivity.startActivity(i);
//                currentActivity.finish();
            }
        });
        sheet.setContentView(sheetView);
        sheet.show();
        logicMap.getParkingInfo(parking, parkingPointMap, mapObject);
        parking.observe(sheet, new Observer<Parking>() {
            @Override
            public void onChanged(Parking parking) {
                TextView nameParking = (TextView) sheetView.findViewById(R.id.nameParking);
                TextView descParking = (TextView) sheetView.findViewById(R.id.descParking);
                TextView freeText = (TextView) sheetView.findViewById(R.id.freeText);
                TextView allText = (TextView) sheetView.findViewById(R.id.allText);
                TextView brokeText = (TextView) sheetView.findViewById(R.id.brokeText);
                idParking = parking.getId();
                nameParking.setText(parking.getName());
                descParking.setText(parking.getDescription());
                freeText.setText(String.valueOf(parking.getFree_slot()));
                allText.setText(String.valueOf(parking.getAll_slot()));
                brokeText.setText(String.valueOf(parking.getAll_slot() - parking.getFree_slot()));
                parkButton.setEnabled(true);
                parkButton.setBackgroundResource(R.drawable.button_rounded);
            }
        });
        return true;
    }
}
