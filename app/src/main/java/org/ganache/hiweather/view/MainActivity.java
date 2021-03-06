package org.ganache.hiweather.view;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.ganache.hiweather.R;
import org.ganache.hiweather.adapter.WeatherAdapter;
import org.ganache.hiweather.contract.MainContract;
import org.ganache.hiweather.model.LatXLngY;
import org.ganache.hiweather.model.TomorrowWeather;
import org.ganache.hiweather.presenter.MainPresenter;

import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity implements MainContract.View{

    private MainContract.Presenter presenter;

    PermissionListener permissionlistener;
    String nowTime = "";
    String nowDay = "";

    LatXLngY gridXy;

    TextView weather_tv;
    TextView tmp_tv;
    TextView pop_tv;
    TextView wsd_tv;
    TextView reh_tv;
    TextView pcp_tv;
    TextView location_tv;

    TextView ht_val_tv;
    TextView mt_val_tv;

    ImageView weatherImgView;
    ImageView rehImgView;
    ImageView popImgView;
    ImageView pcpImgView;
    ImageView wsdImgView;
    ImageView htImgView;
    ImageView mtImgView;

    ProgressBar progressBar;

    double latitude = 0;
    double longitude = 0;

    RecyclerView recyclerView;

    Animation scaleAnim;
    Animation scaleAnim_s;
    Animation rotateAnim;
    Animation rotateAnim_s;

    SwipeRefreshLayout swipeLayout;
    ConstraintLayout constLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainPresenter(this);
        init();

        // ?????? ?????? ?????? ??????
        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                presenter.getNowData();
                startApp();
            }
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "?????? ????????? ?????????????????????.\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        // ?????? ??????
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("?????? ???????????? ???????????? ?????? ????????? ???????????????.")
                .setDeniedMessage("?????? ????????? ????????? ?????? ????????? ??? ????????????. \n [??????] > [??????] ?????? ????????? ????????? ??? ????????????.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .setGotoSettingButton(true)
                .check();


        // ????????????
        swipeLayout.setOnRefreshListener(() -> {
            showProgress(true);
            startApp();
            swipeLayout.setRefreshing(false);
        });
    }

    private void init() {
        scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale);
        scaleAnim_s = AnimationUtils.loadAnimation(this, R.anim.scale_s);
        rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotateAnim_s = AnimationUtils.loadAnimation(this, R.anim.rotate_s);

        weather_tv = findViewById(R.id.weather);
        tmp_tv = findViewById(R.id.tmp);
        pop_tv = findViewById(R.id.pop);
        wsd_tv = findViewById(R.id.wsd);
        reh_tv = findViewById(R.id.reh);
        pcp_tv = findViewById(R.id.pcp);
        location_tv = findViewById(R.id.location);

        ht_val_tv = findViewById(R.id.ht_val_tv);
        mt_val_tv = findViewById(R.id.mt_val_tv);

        weatherImgView = findViewById(R.id.imageView);
        rehImgView = findViewById(R.id.reh_icon);
        popImgView = findViewById(R.id.pop_icon);
        pcpImgView = findViewById(R.id.pcp_icon);
        wsdImgView = findViewById(R.id.wsd_icon);
        htImgView = findViewById(R.id.ht_img);
        mtImgView = findViewById(R.id.mt_img);

        swipeLayout = findViewById(R.id.swipeLayout);
        constLayout = findViewById(R.id.constLayout);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void startApp() {
        presenter.getNowData();
        presenter.getCoordinate(this);
    }

    @Override
    public void showProgress(boolean isShow) {
        if(isShow) progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setNowData(String nowDay, String nowTime) {
        this.nowDay = nowDay;
        this.nowTime = nowTime;
    }

    @Override
    public void setNowLayout(int layoutColor, int StatusColor) {
        constLayout.setBackgroundColor(getResources().getColor(layoutColor));
        getWindow().setStatusBarColor(getResources().getColor(StatusColor));
    }

    @Override
    public void setCoordinate(double latitude, double longitude, LatXLngY gridXy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.gridXy = gridXy;
    }

    @Override
    public void getWeather() {
        presenter.requestApi(nowDay, nowTime, gridXy);
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateRecyclerView(List<TomorrowWeather> list) {
        WeatherAdapter adapter = new WeatherAdapter();
        recyclerView.setAdapter(adapter);
        adapter.updateItems(list);
    }

    @Override
    public void setWeatherImg(int drawable) {
        weatherImgView.setImageResource(drawable);
    }

    @Override
    public void setWeatherTextView(String weather) {
        weather_tv.setText(weather);
    }

    @Override
    public void setWeatherItems(HashMap<String, String> items) {
        tmp_tv.setText(getString(R.string.weather_string, " " + items.get("tmp"), "??"));
        pop_tv.setText(getString(R.string.weather_string, " " + items.get("pop"), "%"));
        wsd_tv.setText(getString(R.string.weather_string, items.get("wsd"), "m/s"));
        reh_tv.setText(getString(R.string.weather_string, " " + items.get("reh") , "%"));
        ht_val_tv.setText(getString(R.string.weather_string," " + items.get("tmx"), "??"));
        mt_val_tv.setText(getString(R.string.weather_string," " + items.get("tmn"), "??"));
        pcp_tv.setText(getString(R.string.weather_string, items.get("pcp"), ""));
    }

    @Override
    public void setLocationTextView(String sido, String gugun) {
        location_tv.setText(getString(R.string.weather_string, sido + " " , gugun));
    }

    @Override
    public void setMyLocation() {
        presenter.getMyLocation(this, latitude, longitude);
    }

    @Override
    public void startAnim() {
        weatherImgView.startAnimation(rotateAnim);
        tmp_tv.startAnimation(scaleAnim_s);
        rehImgView.startAnimation(scaleAnim);
        popImgView.startAnimation(scaleAnim);
        pcpImgView.startAnimation(scaleAnim);
        wsdImgView.startAnimation(scaleAnim);
        htImgView.startAnimation(scaleAnim);
        mtImgView.startAnimation(scaleAnim);
    }

}




















