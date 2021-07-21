package org.ganache.hiweather;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.ganache.hiweather.model.Example;
import org.ganache.hiweather.model.Item;
import org.ganache.hiweather.model.LatXLngY;
import org.ganache.hiweather.retrofit.WeatherService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends AppCompatActivity {

    private String pty  = "";
    private String sky  = "";
    private String t3h = "";
    private String pop = "";
    private float wsd = 0;
    private String r06 = "";
    private String reh = "";

    String nowTime = "";
    String nowDay = "";

    LatXLngY gridXy;

    TextView weather_tv;
    TextView t3h_tv;
    TextView pop_tv;
    TextView wsd_tv;
    TextView reh_tv;
    TextView r06_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FusedLocationProviderClient fusedLocationClient;
        weather_tv = findViewById(R.id.weather);
        t3h_tv = findViewById(R.id.t3h);
        pop_tv = findViewById(R.id.pop);
        wsd_tv = findViewById(R.id.wsd);
        reh_tv = findViewById(R.id.reh);
        r06_tv = findViewById(R.id.r06);

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat dayDate = new SimpleDateFormat("yyyyMMdd");
        nowDay = dayDate.format(mDate);

        SimpleDateFormat timeDate = new SimpleDateFormat("HHmm");
        String dateTime = timeDate.format(mDate);

        Log.d("debug_test", "nowDay = " + nowDay);
        Log.d("debug_test", "dateTime = " + dateTime);

        int nowTimeInt = Integer.parseInt(dateTime);

        Log.d("debug_test", "nowTimeInt = " + nowTimeInt);
        //0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300

        if (nowTimeInt < 200) {
            // nowDay -1
            // 2300
        } else if (nowTimeInt >= 200 && nowTimeInt < 500) {
            nowTime = "0200";
        } else if (nowTimeInt >= 500 && nowTimeInt < 800) {
            nowTime = "0500";
        } else if (nowTimeInt >= 800 && nowTimeInt < 1100) {
            nowTime = "0800";
        } else if (nowTimeInt >= 1100 && nowTimeInt < 1400) {
            nowTime = "1100";
        } else if (nowTimeInt >= 1400 && nowTimeInt < 1700) {
            nowTime = "1400";
        } else if (nowTimeInt >= 1700 && nowTimeInt < 2000) {
            nowTime = "1700";
        } else if (nowTimeInt >= 2000 && nowTimeInt < 2300) {
            nowTime = "2000";
        } else if (nowTimeInt >= 2300) {
            nowTime = "2300";
        }

        Log.d("debug_test", "nowTime = " + nowTime);


        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };


        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.d("debug_test", "getLatitude = " + location.getLatitude());
                            Log.d("debug_test", "getLongitude = " + location.getLongitude());

                            locationChange locChange = new locationChange();
                            gridXy = locChange.convertGRID_GPS(0, location.getLatitude(), location.getLongitude());
                            Log.d("debug_test", "x = " + gridXy.x);
                            Log.d("debug_test", "y = " + gridXy.y);

                            retrofitGo();

                        }
                    }
                }).addOnFailureListener(this, e->{
                    Log.d("debug_test" , "error =" + e.getCause());
                });



    }

    private void retrofitGo() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherService.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();



        WeatherService service = retrofit.create(WeatherService.class);
        Call<Example> reposCall = service.listRepos("JSON", nowDay, nowTime, gridXy.x, gridXy.y, "100");
        reposCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                if(response.isSuccessful()) {
                    Log.d("debug_test", "레트로핏 성공");

                    if (response.body().getResponse() != null) {
                        List<Item> items = response.body().getResponse().getBody().getItems().getItem();
                        String fcDate = items.get(0).getFcstDate();
                        String fcTime = items.get(0).getBaseTime();

                        Log.d("fcDate", "fcDate = " + fcDate);
                        Log.d("fcTime", "fcTime = " + fcTime);

                        for (int i = 0 ; i < items.size() ; i++) {
                            if (items.get(i).getCategory().equals("PTY") && items.get(i).getFcstDate().equals(nowDay) && items.get(i).getFcstTime().equals(fcTime)) {
                                //강수형태 :  없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4), 빗방울(5), 빗방울/눈날림(6), 눈날림(7)
                                switch (String.valueOf(Math.round(items.get(i).getFcstValue()))) {
                                    case "0" : pty = "없음";
                                        break;
                                    case "1" : pty = "비";
                                        break;
                                    case "2" : pty = "비/눈";
                                        break;
                                    case "3" : pty = "눈";
                                        break;
                                    case "4" : pty = "소나기";
                                        break;
                                    case "5" : pty = "빗방울";
                                        break;
                                    case "6" : pty = "빗방울/눈날림";
                                        break;
                                    case "7" : pty = "눈날림";
                                        break;
                                    default : pty = "알수없음";
                                        break;
                                }
                            } else if (items.get(i).getCategory().equals("SKY") && items.get(i).getFcstDate().equals(nowDay) && items.get(i).getFcstTime().equals(fcTime)) {
                                // 구름 상태 : 맑음(1), 구름많음(3), 흐림(4)
                                sky = String.valueOf(Math.round(items.get(i).getFcstValue()));
                            } else if (items.get(i).getCategory().equals("T3H") && items.get(i).getFcstDate().equals(nowDay) && items.get(i).getFcstTime().equals(fcTime)) {
                                // 3시간 기온
                                t3h = String.valueOf(Math.round(items.get(i).getFcstValue()));
                            } else if (items.get(i).getCategory().equals("POP") && items.get(i).getFcstDate().equals(nowDay) && items.get(i).getFcstTime().equals(fcTime)) {
                                // 강수확률
                                pop = String.valueOf(Math.round(items.get(i).getFcstValue()));
                            } else if (items.get(i).getCategory().equals("WSD") && items.get(i).getFcstDate().equals(nowDay) && items.get(i).getFcstTime().equals(fcTime)) {
                                // 풍속
                                wsd = items.get(i).getFcstValue();
                            } else if (items.get(i).getCategory().equals("REH") && items.get(i).getFcstDate().equals(nowDay) && items.get(i).getFcstTime().equals(fcTime)) {
                                // 습도
                                reh = String.valueOf(Math.round(items.get(i).getFcstValue()));
                            } else if (items.get(i).getCategory().equals("R06") && items.get(i).getFcstDate().equals(nowDay) && items.get(i).getFcstTime().equals(fcTime)) {
                                // 강수량
                                r06 = String.valueOf(Math.round(items.get(i).getFcstValue()));
                            }
                        }

                        if (pty == "없음") {
                            // 구름 상태 : 맑음(1), 구름많음(3), 흐림(4)
                            switch(sky) {
                                case "1" : sky = "맑음";
                                    break;
                                case "3" : sky = "구름많음";
                                    break;
                                case "4" : sky = "흐림";
                                    break;
                                default : sky = pty;
                                    break;
                            }

                            weather_tv.setText(sky);
                        } else {
                            weather_tv.setText(pty);
                        }

                        t3h_tv.setText(t3h + "℃");
                        pop_tv.setText(pop + "%");
                        wsd_tv.setText(wsd + "m/s");
                        reh_tv.setText(reh + "%");
                        r06_tv.setText(r06 + "mm");

                        Log.d("debug_test", ">>>>>>> 강수형태(PTY) = " + pty);
                        Log.d("debug_test", ">>>>>>> 하늘상태(SKY) = " + sky);
                        Log.d("debug_test", ">>>>>>> 기온(T3H) = " + t3h + "℃");
                        Log.d("debug_test", ">>>>>>> 강수확률(POP) = " + pop + "%");
                        Log.d("debug_test", ">>>>>>> 풍속(WSD) = " + wsd + "m/s");
                        Log.d("debug_test", ">>>>>>> 습도(REH) = " + reh + "%");
                        Log.d("debug_test", ">>>>>>> 강수량(R06) = " + r06 + "mm");

                    }


                } else {
                    Log.d("debug_test", "레트로핏 실패");
                    //실패
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.d("debug_test", "레트로핏 예외, 인터넷 끊김 등 시스템적인 이유 실패");
                Log.d("debug_test", t.toString());
            }

        });
    }

}






















