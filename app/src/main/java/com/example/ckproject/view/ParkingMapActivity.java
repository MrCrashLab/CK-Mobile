package com.example.ckproject.view;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ckproject.MapService;
import com.example.ckproject.listener.ButtonTapListener;
import com.example.ckproject.R;
import com.example.ckproject.model.Map;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class ParkingMapActivity extends AppCompatActivity {

    private PhotoView photoView;
    private Button btn1, btn2, btn3, btn4;
    private int id;
    private int floor = 1;
    private int floor_count = 0;
    private final String BASE_URL = "http://192.168.0.100:8000";
    private List<Map> maps;
    private LinearLayout btnGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_map);
        photoView = (PhotoView) findViewById(R.id.photo_view);
        btnGroup = findViewById(R.id.btnGroup);
        btnGroup.setVisibility(View.GONE);
        photoView.setMaximumScale(100);
        id = getIntent().getExtras().getInt("idParking");
        photoView.setImageResource(R.drawable.load_icon);
        photoView.setZoomable(false);
        photoView.setMinimumScale(0.5F);
        photoView.setScale(0.5F);
        btn1 = findViewById(R.id.btn_floor1);
        btn2 = findViewById(R.id.btn_floor2);
        btn3 = findViewById(R.id.btn_floor3);
        btn4 = findViewById(R.id.btn_floor4);
        getImageMap();
    }

    private void getImageMap(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MapService service = retrofit.create(MapService.class);
        Call<List<Map>> call = service.getMap(id);
        call.enqueue(new Callback<List<Map>>() {
            @Override
            public void onResponse(Call<List<Map>> call, Response<List<Map>> response) {
                maps = response.body();
                floor_count = 0;
                String imageUri = new String("");
                for (Map m : maps){
                    if(m.getFloor() == floor){
                        imageUri = m.getSrc();
                    }
                    floor_count++;
                }
                Log.println(Log.ASSERT, "Hello", imageUri);
                Picasso
                    .with(context)
                    .load(imageUri)
                    .resize(2048, 2048)
                    .onlyScaleDown()
                    .into(photoView);
                photoView.setZoomable(true);
                btnGroup.setVisibility(View.VISIBLE);
                disableBtn();
                btn1.setOnClickListener(new ButtonTapListener(1, btn2, btn3, btn4, maps, photoView));
                btn2.setOnClickListener(new ButtonTapListener(2, btn1, btn3, btn4, maps, photoView));
                btn3.setOnClickListener(new ButtonTapListener(3, btn1, btn2, btn4, maps, photoView));
                btn4.setOnClickListener(new ButtonTapListener(4, btn1, btn2, btn3, maps, photoView));
            }

            @Override
            public void onFailure(Call<List<Map>> call, Throwable t) {
                Toast.makeText(ParkingMapActivity.this, "You have internet problem!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void disableBtn(){
        switch (floor_count){
            case 4:
                break;
            case 1:
                btn2.setVisibility(View.GONE);
            case 2:
                btn3.setVisibility(View.GONE);
            case 3:
                btn4.setVisibility(View.GONE);
                break;
        }
    }
}