package com.example.ckproject.view;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

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
import com.example.ckproject.viewmodel.LogicMap;
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
    private MutableLiveData<List<Map>> maps;
    private LinearLayout btnGroup;
    LogicMap logic = new LogicMap();

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
        maps = new MutableLiveData<>();
        getImageMap();
    }

    private void getImageMap(){
        logic.getParkingMap(id, maps);
        maps.observe(this, new Observer<List<Map>>() {
            @Override
            public void onChanged(List<Map> maps) {
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