package com.example.ckproject;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ckproject.model.Parking;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ParkingTapListener implements MapObjectTapListener {
    private BottomSheetDialog sheet;
    private Button parkButton;
    private int idParking = 0;
    private final String BASE_URL = "http://192.168.0.106:8000";
    private Map<MapObject, com.example.ckproject.model.Point> parkingPointMap = new HashMap<>();

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
        Log.println(Log.ASSERT, "Ya Ustal", String.valueOf(point.getLatitude()));
        sheet = new BottomSheetDialog(currentActivity, R.style.BottomSheetTheme);
        View sheetView = LayoutInflater.from(currentActivity.getApplicationContext()).inflate(R.layout.bottom_dialog, null);
        parkButton = sheetView.findViewById(R.id.parkButton);
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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.println(Log.ASSERT, "YObjects", parkingPointMap.values().toString());
        int id = parkingPointMap.get(mapObject).getId();
        Log.println(Log.ASSERT, "Ya Ustal", String.valueOf(id));
        HomeActivity.ParkingService service = retrofit.create(HomeActivity.ParkingService.class);
        Call<Parking> call = service.getParking(id);
        call.enqueue(new Callback<Parking>() {
            @Override
            public void onResponse(Call<Parking> call, Response<Parking> response) {
                TextView nameParking = (TextView) sheetView.findViewById(R.id.nameParking);
                TextView descParking = (TextView) sheetView.findViewById(R.id.descParking);
                TextView freeText = (TextView) sheetView.findViewById(R.id.freeText);
                TextView allText = (TextView) sheetView.findViewById(R.id.allText);
                TextView brokeText = (TextView) sheetView.findViewById(R.id.brokeText);
                idParking = response.body().getId();
                nameParking.setText(response.body().getName());
                descParking.setText(response.body().getDescription());
                freeText.setText(String.valueOf(response.body().getFree_slot()));
                allText.setText(String.valueOf(response.body().getAll_slot()));
                brokeText.setText(String.valueOf(response.body().getAll_slot() - response.body().getFree_slot()));
            }

            @Override
            public void onFailure(Call<Parking> call, Throwable t) {
                idParking = 0;
                Toast.makeText(currentActivity, "You have internet problem!\n", Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }
}
