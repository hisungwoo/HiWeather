package org.ganache.hiweather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.ganache.hiweather.adapter.WeatherAdapter;
import org.ganache.hiweather.model.Example;
import org.ganache.hiweather.model.Item;
import org.ganache.hiweather.model.LatXLngY;
import org.ganache.hiweather.model.TomorrowWeather;
import org.ganache.hiweather.retrofit.WeatherRetrofit;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends AppCompatActivity {

    private String pty = "";
    private String sky = "";
    private String tmp = "";
    private String pop = "";
    private float wsd = 0;
    private String pcp = "";
    private String reh = "";
    private String tmx = "";
    private String tmn = "";

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

    FusedLocationProviderClient fusedLocationClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "위치 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
                startApp();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "위치 권한이 거부되었습니다.\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("앱을 사용하기 위해서는 위치 권한이 필요합니다.")
                .setDeniedMessage("위치 권한이 없으면 앱을 실행할 수 없습니다. \n [설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .setGotoSettingButton(true)
                .check();

        startApp();

        swipeLayout.setOnRefreshListener(() -> {
            progressBar.setVisibility(View.VISIBLE);
            startApp();
            swipeLayout.setRefreshing(false);
        });

//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        // GPS 프로바이더 사용가능여부
//        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        // 네트워크 프로바이더 사용가능여부
//        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//        Log.d("debug_test", "gps 프로바이더 = " + isGPSEnabled);
//        Log.d("debug_test", "네트워크 = " + isNetworkEnabled);
    }

    private void startApp() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat dayDate = new SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault());
        nowDay = dayDate.format(mDate);

        SimpleDateFormat timeDate = new SimpleDateFormat("HHmm", java.util.Locale.getDefault());
        String dateTime = timeDate.format(mDate);

        Log.d("debug_test", "nowDay = " + nowDay);
        Log.d("debug_test", "dateTime = " + dateTime);

        int nowTimeInt = Integer.parseInt(dateTime);

        Log.d("debug_test", "nowTimeInt = " + nowTimeInt);
        //0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300

        if (nowTimeInt < 200) {
            SimpleDateFormat ytDayFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);

            constLayout.setBackgroundColor(getResources().getColor(R.color.colorEveningBack));
            if (Build.VERSION.SDK_INT >= 21)
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorEveningStatus));

            String ytDay = ytDayFormat.format(calendar.getTime());
            Log.d("debug_test", "ytDay = " + ytDay);
            nowDay = ytDay;
            nowTime = "2300";
            // nowDay -1
            // 2300
        } else if (nowTimeInt < 500) {
            nowTime = "0200";
            constLayout.setBackgroundColor(getResources().getColor(R.color.colorEveningBack));
            if (Build.VERSION.SDK_INT >= 21)
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorEveningStatus));
        } else if (nowTimeInt < 800) {
            nowTime = "0500";
            constLayout.setBackgroundColor(getResources().getColor(R.color.colorMorningBack));
            if (Build.VERSION.SDK_INT >= 21)
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorMorningStatus));
        } else if (nowTimeInt < 1100) {
            nowTime = "0800";
            constLayout.setBackgroundColor(getResources().getColor(R.color.colorMorningBack));
            if (Build.VERSION.SDK_INT >= 21)
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorMorningStatus));
        } else if (nowTimeInt < 1400) {
            nowTime = "1100";
            constLayout.setBackgroundColor(getResources().getColor(R.color.colorAfterBack));
            if (Build.VERSION.SDK_INT >= 21)
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorAfterStatus));
        } else if (nowTimeInt < 1700) {
            nowTime = "1400";
            constLayout.setBackgroundColor(getResources().getColor(R.color.colorAfterBack));
            if (Build.VERSION.SDK_INT >= 21)
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorAfterStatus));
        } else if (nowTimeInt < 2000) {
            nowTime = "1700";
            constLayout.setBackgroundColor(getResources().getColor(R.color.colorAfterBack));
            if (Build.VERSION.SDK_INT >= 21)
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorAfterStatus));
        } else if (nowTimeInt < 2300) {
            nowTime = "2000";
            constLayout.setBackgroundColor(getResources().getColor(R.color.colorEveningBack));
            if (Build.VERSION.SDK_INT >= 21)
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorEveningStatus));
        } else {
            nowTime = "2300";
            constLayout.setBackgroundColor(getResources().getColor(R.color.colorEveningBack));
            if (Build.VERSION.SDK_INT >= 21)
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorEveningStatus));
        }

        Log.d("debug_test", "nowTime = " + nowTime);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            locationChange locChange = new locationChange();
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            gridXy = locChange.convertGRID_GPS(0, location.getLatitude(), location.getLongitude());

                            Log.d("debug_test", "x = " + gridXy.x);
                            Log.d("debug_test", "y = " + gridXy.y);

                            getWeather();

                        } else {
                            Log.d("debug_test", "############ location null ############");

                            LocationRequest request = LocationRequest.create()
                                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setInterval(100)
                                    .setFastestInterval(200);

                            LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    for (Location location : locationResult.getLocations()) {
                                        Log.d("debug_test", "location.getLatitude = " + location.getLatitude());
                                        Log.d("debug_test", "location.getLongitude = " + location.getLongitude());
                                        fusedLocationClient.removeLocationUpdates(this);

                                    }
                                    startApp();
                                }
                            };
                            fusedLocationClient.requestLocationUpdates(request, locationCallback, null);

                        }
                    }
                }).addOnFailureListener(this, e -> {
            Log.d("debug_test", "error =" + e.getCause());
        });
    }

    private void getWeather() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherRetrofit.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();


        WeatherRetrofit service = retrofit.create(WeatherRetrofit.class);
        Call<Example> reposCall = service.getTown("JSON", nowDay, nowTime, gridXy.x, gridXy.y, "270");
        reposCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(@NonNull Call<Example> call, @NonNull Response<Example> response) {
                if(response.isSuccessful()) {
                    Log.d("debug_test", "레트로핏 성공");


                    if (response.body() != null && response.body().getResponse() != null) {
                        List<Item> items = response.body().getResponse().getBody().getItems().getItem();

                        LinkedHashSet days = new LinkedHashSet();
                        LinkedHashSet times = new LinkedHashSet();

                        for (int i = 0 ; i < items.size() ; i++) {
                            days.add(items.get(i).getFcstDate());
                            times.add(items.get(i).getFcstTime());
                        }

                        List<String> dayList = new ArrayList<>();
                        List<String> timeList = new ArrayList<>();

                        dayList.addAll(days);
                        timeList.addAll(times);

                        Log.d("debug_test", "dayList = " + dayList.toString());
                        Log.d("debug_test", "timeList = " + timeList.toString());

                        List<String> ptyDataList = new ArrayList<>();
                        List<String> skyDataList = new ArrayList<>();
                        List<String> tmpDataList = new ArrayList<>();
                        List<String> timeDataList = new ArrayList<>();

                        List<String> ptyDataList2 = new ArrayList<>();
                        List<String> skyDataList2 = new ArrayList<>();
                        List<String> tmpDataList2 = new ArrayList<>();
                        List<String> timeDataList2 = new ArrayList<>();

                        String getDay = dayList.get(0);
                        String tomoDay = "";
                        if (dayList.size() > 1) {
                            tomoDay = dayList.get(1);
                        }
                        Log.d("debug_test", "tomoDay = " + tomoDay);


                        for (int i = 0; i < items.size(); i++) {
                            for(int j = 0 ; j < timeList.size() ; j++) {

                                if (items.get(i).getCategory().equals("PTY") && items.get(i).getFcstDate().equals(getDay) && items.get(i).getFcstTime().equals(timeList.get(j))) {
                                    ptyDataList.add(items.get(i).getFcstValue());
                                    timeDataList.add(items.get(i).getFcstTime());
                                }

                                if(items.get(i).getCategory().equals("PTY") && items.get(i).getFcstDate().equals(tomoDay) && items.get(i).getFcstTime().equals(timeList.get(j))) {
                                    ptyDataList2.add(items.get(i).getFcstValue());
                                    timeDataList2.add(items.get(i).getFcstTime());
                                }

                                if (items.get(i).getCategory().equals("SKY") && items.get(i).getFcstDate().equals(getDay) && items.get(i).getFcstTime().equals(timeList.get(j))) {
                                    skyDataList.add(items.get(i).getFcstValue());
                                }

                                if(items.get(i).getCategory().equals("SKY") && items.get(i).getFcstDate().equals(tomoDay) && items.get(i).getFcstTime().equals(timeList.get(j))) {
                                    skyDataList2.add(items.get(i).getFcstValue());
                                }

                                if (items.get(i).getCategory().equals("TMP") && items.get(i).getFcstDate().equals(getDay) && items.get(i).getFcstTime().equals(timeList.get(j))) {
                                    tmpDataList.add(items.get(i).getFcstValue());
                                }

                                if(items.get(i).getCategory().equals("TMP") && items.get(i).getFcstDate().equals(tomoDay) && items.get(i).getFcstTime().equals(timeList.get(j))) {
                                    tmpDataList2.add(items.get(i).getFcstValue());
                                }
                            }
                        }

//                        Log.d("debug_test", "############# ptyDataList = " + ptyDataList);
//                        Log.d("debug_test", "############# skyDataList = " + skyDataList);
//                        Log.d("debug_test", "############# tmpDataList = " + tmpDataList);
//                        Log.d("debug_test", "############# timeDataList = " + timeDataList);
//
//
//                        Log.d("debug_test", "############# ptyDataList2 = " + ptyDataList2);
//                        Log.d("debug_test", "############# skyDataList2 = " + skyDataList2);
//                        Log.d("debug_test", "############# tmpDataList2 = " + tmpDataList2);
//                        Log.d("debug_test", "############# timeDataList2 = " + timeDataList2);

                        WeatherAdapter adapter = new WeatherAdapter();
                        recyclerView.setAdapter(adapter);

                        List<TomorrowWeather> weatherItems = new ArrayList<>();

                        for (int i = 0; i < timeDataList.size(); i++) {
                            TomorrowWeather item = new TomorrowWeather();

                            item.setDay("오늘");
                            item.setTime((timeDataList.get(i).substring(0 , 2)) + "시");
                            item.setTomoTmp(" " + tmpDataList.get(i) + "˚");

                            if (ptyDataList.get(i).equals("0")) {
                                if (skyDataList.get(i).equals("1")) {
                                    item.setWeather("맑음");
                                } else if (skyDataList.get(i).equals("3")) {
                                    item.setWeather("구름많음");
                                } else if (skyDataList.get(i).equals("4"))
                                    item.setWeather("흐림");

                            } else {
                                // 비(1), 비/눈(2), 눈(3), 소나기(4), 빗방울(5), 빗방울/눈날림(6), 눈날림(7)
                                if (ptyDataList.get(i).equals("1") || ptyDataList.get(i).equals("2"))
                                    item.setWeather("비");
                                else if (ptyDataList.get(i).equals("3") || ptyDataList.get(i).equals("6") || ptyDataList.get(i).equals("7"))
                                    item.setWeather("눈");
                                else if (ptyDataList.get(i).equals("4") || ptyDataList.get(i).equals("5"))
                                    item.setWeather("소나기");
                            }
                            weatherItems.add(item);
                        }

                        for (int i = 0; i < timeDataList2.size(); i++) {
                            TomorrowWeather item = new TomorrowWeather();

                            item.setDay("내일");
                            item.setTime((timeDataList2.get(i).substring(0 , 2)) + "시");
                            item.setTomoTmp(" " + tmpDataList2.get(i) + "˚");

                            if (ptyDataList2.get(i).equals("0")) {
                                if (skyDataList2.get(i).equals("1")) {
                                    item.setWeather("맑음");
                                } else if (skyDataList2.get(i).equals("3")) {
                                    item.setWeather("구름많음");
                                } else if (skyDataList2.get(i).equals("4"))
                                    item.setWeather("흐림");

                            } else {
                                // 비(1), 비/눈(2), 눈(3), 소나기(4), 빗방울(5), 빗방울/눈날림(6), 눈날림(7)
                                if (ptyDataList2.get(i).equals("1") || ptyDataList2.get(i).equals("2"))
                                    item.setWeather("비");
                                else if (ptyDataList2.get(i).equals("3") || ptyDataList2.get(i).equals("6") || ptyDataList2.get(i).equals("7"))
                                    item.setWeather("눈");
                                else if (ptyDataList2.get(i).equals("4") || ptyDataList2.get(i).equals("5"))
                                    item.setWeather("소나기");
                            }
                            weatherItems.add(item);
                        }

                        adapter.updateItems(weatherItems);

                        String nowFcDate = items.get(0).getFcstDate();
                        String nowFcTime = items.get(0).getFcstTime();

                        Log.d("debug_test", "nowFcDate = " + nowFcDate);
                        Log.d("debug_test", "nowFcTime = " + nowFcTime);


                        for (int i = 0; i < items.size(); i++) {
                            if (items.get(i).getCategory().equals("PTY") && items.get(i).getFcstDate().equals(nowFcDate) && items.get(i).getFcstTime().equals(nowFcTime)) {
                                //강수형태 :  없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4), 빗방울(5), 빗방울/눈날림(6), 눈날림(7)
                                switch (items.get(i).getFcstValue()) {
                                    case "0":
                                        pty = "없음";
                                        break;
                                    case "1":
                                        pty = "비";
                                        weatherImgView.setImageResource(R.drawable.rain);
                                        break;
                                    case "2":
                                        pty = "비/눈";
                                        weatherImgView.setImageResource(R.drawable.rain);
                                        break;
                                    case "3":
                                        pty = "눈";
                                        weatherImgView.setImageResource(R.drawable.snow);
                                        break;
                                    case "4":
                                        pty = "소나기";
                                        weatherImgView.setImageResource(R.drawable.shower);
                                        break;
                                    case "5":
                                        pty = "빗방울";
                                        weatherImgView.setImageResource(R.drawable.shower);
                                        break;
                                    case "6":
                                        pty = "빗방울/눈날림";
                                        weatherImgView.setImageResource(R.drawable.shower);
                                        break;
                                    case "7":
                                        pty = "눈날림";
                                        weatherImgView.setImageResource(R.drawable.snow);
                                        break;
                                    default:
                                        pty = "알수없음";
                                        break;
                                }
                            } else if (items.get(i).getCategory().equals("SKY") && items.get(i).getFcstDate().equals(nowFcDate) && items.get(i).getFcstTime().equals(nowFcTime)) {
                                // 구름 상태 : 맑음(1), 구름많음(3), 흐림(4)
                                sky = items.get(i).getFcstValue();
                            } else if (items.get(i).getCategory().equals("TMP") && items.get(i).getFcstDate().equals(nowFcDate) && items.get(i).getFcstTime().equals(nowFcTime)) {
                                // 1시간 기온
                                tmp = items.get(i).getFcstValue();
                            } else if (items.get(i).getCategory().equals("POP") && items.get(i).getFcstDate().equals(nowFcDate) && items.get(i).getFcstTime().equals(nowFcTime)) {
                                // 강수확률
                                pop = items.get(i).getFcstValue();
                            } else if (items.get(i).getCategory().equals("WSD") && items.get(i).getFcstDate().equals(nowFcDate) && items.get(i).getFcstTime().equals(nowFcTime)) {
                                // 풍속
                                wsd = 0;
                            } else if (items.get(i).getCategory().equals("REH") && items.get(i).getFcstDate().equals(nowFcDate) && items.get(i).getFcstTime().equals(nowFcTime)) {
                                // 습도
                                reh = items.get(i).getFcstValue();
                            } else if (items.get(i).getCategory().equals("PCP") && items.get(i).getFcstDate().equals(nowFcDate) && items.get(i).getFcstTime().equals(nowFcTime)) {
                                // 강수량
                                pcp = items.get(i).getFcstValue();
                            } else if (items.get(i).getCategory().equals("TMX") && items.get(i).getFcstDate().equals(nowFcDate)) {
                                // 낮 최고온도
                                tmx = items.get(i).getFcstValue();
                            } else if (items.get(i).getCategory().equals("TMN") && items.get(i).getFcstDate().equals(nowFcDate)) {
                                // 낮 최저온도
                                tmn = items.get(i).getFcstValue();
                            }
                        }

                        // 최고, 최저온도 null 일 때
                        if (tmx.equals("") || tmn.equals("")) {
                            String tomorrowDay = dayList.get(1);
                            Log.d("debug_test", "#### TMX(최고기온) 혹은 TMN(최저기온) NULL 값");
                            for (int i = 0; i < items.size(); i++) {
                                if (items.get(i).getCategory().equals("TMX") && items.get(i).getFcstDate().equals(tomorrowDay))
                                    tmx = tmx.equals("") ? items.get(i).getFcstValue() : tmx;
                                else if (items.get(i).getCategory().equals("TMN") && items.get(i).getFcstDate().equals(tomorrowDay))
                                    tmn = tmn.equals("") ? items.get(i).getFcstValue() : tmn;
                            }
                        }

                        if (pty.equals("없음")) {
                            // 구름 상태 : 맑음(1), 구름많음(3), 흐림(4)
                            switch (sky) {
                                case "1":
                                    sky = "맑음";
                                    weatherImgView.setImageResource(R.drawable.sunny);
                                    break;
                                case "3":
                                    sky = "구름많음";
                                    weatherImgView.setImageResource(R.drawable.cloudy);
                                    break;
                                case "4":
                                    sky = "흐림";
                                    weatherImgView.setImageResource(R.drawable.murky);
                                    break;
                                default:
                                    sky = pty;
                                    break;
                            }

                            weather_tv.setText(sky);


                        } else {
                            weather_tv.setText(pty);
                        }

                        tmp_tv.setText(getString(R.string.weather_string, " " + tmp, "˚"));
                        pop_tv.setText(getString(R.string.weather_string, " " + pop, "%"));
                        wsd_tv.setText(getString(R.string.weather_string, String.valueOf(wsd), "m/s"));
                        reh_tv.setText(getString(R.string.weather_string, " " + reh , "%"));
                        ht_val_tv.setText(getString(R.string.weather_string," " + tmx, "˚"));
                        mt_val_tv.setText(getString(R.string.weather_string," " + tmn, "˚"));

                        pcp = pcp.equals("강수없음") ? "0" : pcp;
                        pcp_tv.setText(getString(R.string.weather_string, pcp, "mm"));

                        getMyLocation(latitude, longitude);

                        Log.d("debug_test", ">>>>>>> 강수형태(PTY) = " + pty);
                        Log.d("debug_test", ">>>>>>> 하늘상태(SKY) = " + sky);
                        Log.d("debug_test", ">>>>>>> 기온(TMP) = " + tmp + "˚");
                        Log.d("debug_test", ">>>>>>> 강수확률(POP) = " + pop + "%");
                        Log.d("debug_test", ">>>>>>> 풍속(WSD) = " + wsd + "m/s");
                        Log.d("debug_test", ">>>>>>> 습도(REH) = " + reh + "%");
                        Log.d("debug_test", ">>>>>>> 강수량(PCP) = " + pcp + "mm");
                        Log.d("debug_test", ">>>>>>> 최고온도(TMX) = " + tmx + "˚");
                        Log.d("debug_test", ">>>>>>> 최저온도(TMN) = " + tmn + "˚");

                        weatherImgView.startAnimation(rotateAnim);
                        tmp_tv.startAnimation(scaleAnim_s);
                        rehImgView.startAnimation(scaleAnim);
                        popImgView.startAnimation(scaleAnim);
                        pcpImgView.startAnimation(scaleAnim);
                        wsdImgView.startAnimation(scaleAnim);
                        htImgView.startAnimation(scaleAnim);
                        mtImgView.startAnimation(scaleAnim);
                        progressBar.setVisibility(View.GONE);
                    }


                } else {
                    Log.d("debug_test", "레트로핏 실패");
                    //실패
                }
            }

            @Override
            public void onFailure(@NonNull Call<Example> call, @NonNull Throwable t) {
                Log.d("debug_test", "레트로핏 예외, 인터넷 끊김 등 시스템적인 이유 실패");
                Log.d("debug_test", t.toString());
            }

        });
    }

    void getMyLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> gList = null;
        try {
            gList = geocoder.getFromLocation(latitude, longitude,5);

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("debug_test", "getFromLocation 실패 : " + e.getMessage());
        }
        if (gList != null) {
            if (gList.size() == 0) {
                Log.d("debug_test", "현재위치에서 검색된 주소정보가 없습니다.");

            } else {
                Address address = gList.get(0);
                String sido = address.getAdminArea();
                String gugun = address.getSubLocality();

                Log.d("debug_test", "sido = " + sido);
                Log.d("debug_test", "gugun = " + gugun);

                location_tv.setText(getString(R.string.weather_string, sido + " " , gugun));

            }
        }
    }
}




















