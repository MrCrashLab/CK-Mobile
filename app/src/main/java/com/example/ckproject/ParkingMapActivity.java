package com.example.ckproject;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ckproject.model.Map;
import com.example.ckproject.model.Point;
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
    private int id;
    private int floor = 1;
    private final String BASE_URL = "http://192.168.0.106:8000";
    private List<Map> maps;
    private LinearLayout btnGroup;
    public interface MapService {
        @GET("map/{parking_id}")
        Call<List<Map>> getMap(@Path("parking_id") int parking_id);
    }

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
                String imageUri = new String("");
                for (Map m : maps){
                    if(m.getFloor() == floor){
                        imageUri = m.getSrc();
                        Log.println(Log.ASSERT,"WTF", String.valueOf(m.getFloor()));
                        break;
                    }
                }
                Log.println(Log.ASSERT,"WTF",  imageUri);
                Picasso
                    .with(context)
                    .load(imageUri)
                    .resize(2048, 2048)
                    .onlyScaleDown()
                    .into(photoView);
                photoView.setZoomable(true);
                btnGroup.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<List<Map>> call, Throwable t) {
                Toast.makeText(ParkingMapActivity.this, "You have internet problem!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}