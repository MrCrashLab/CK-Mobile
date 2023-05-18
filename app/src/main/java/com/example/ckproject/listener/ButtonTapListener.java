package com.example.ckproject.listener;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.view.View;
import android.widget.Button;

import com.example.ckproject.R;
import com.example.ckproject.model.Map;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ButtonTapListener implements View.OnClickListener {
    private int floor;
    private List<Map> maps;
    private PhotoView photoView;
    private Button btn1, btn2, btn3;

    public ButtonTapListener(int floor, Button btn1, Button btn2, Button btn3, List<Map> maps, PhotoView photoView) {
        this.floor = floor;
        this.btn1 = btn1;
        this.btn2 = btn2;
        this.btn3 = btn3;
        this.maps = maps;
        this.photoView = photoView;
    }

    @Override
    public void onClick(View view) {
        view.setBackgroundResource(R.drawable.rounded_floor_btn_press);
        btn1.setBackgroundResource(R.drawable.rounded_floor_btn);
        btn2.setBackgroundResource(R.drawable.rounded_floor_btn);
        btn3.setBackgroundResource(R.drawable.rounded_floor_btn);
        for(Map m: maps){
            if(m.getFloor() == floor){
                Picasso
                        .with(context)
                        .load(m.getSrc())
                        .resize(2048, 2048)
                        .onlyScaleDown()
                        .into(photoView);
                break;
            }
        }
    }
}
