package org.ganache.hiweather;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.ganache.hiweather.model.Example;
import org.ganache.hiweather.model.Item;
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
    private String wsd = "";
    private String r06 = "";
    private String reh = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FusedLocationProviderClient fusedLocationClient;
        TextView weather_tv = findViewById(R.id.weather);
        TextView t3h_tv = findViewById(R.id.t3h);
        TextView pop_tv = findViewById(R.id.pop);
        TextView wsd_tv = findViewById(R.id.wsd);
        TextView reh_tv = findViewById(R.id.reh);
        TextView r06_tv = findViewById(R.id.r06);

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat dayDate = new SimpleDateFormat("yyyyMMdd");
        String nowDay = dayDate.format(mDate);

        SimpleDateFormat timeDate = new SimpleDateFormat("HHmm");
        String dateTime = timeDate.format(mDate);

        Log.d("debug", "nowDay = " + nowDay);
        Log.d("debug", "dateTime = " + dateTime);

        int nowTimeInt = Integer.parseInt(dateTime);

        Log.d("debug", "nowTimeInt = " + nowTimeInt);
        //0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300

        String nowTime = "";
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

        Log.d("debug", "nowTime = " + nowTime);



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
                            Log.d("debug", "getLatitude = " + location.getLatitude());
                            Log.d("debug", "getLongitude = " + location.getLongitude());
                        }
                    }
                }).addOnFailureListener(this, e->{
                    Log.d("debug" , "error =" + e.getCause());
                });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherService.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);
        Call<Example> reposCall = service.listRepos("JSON", nowDay, nowTime,"58", "125");
        reposCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                if(response.isSuccessful()) {
                    Log.d("debug", "레트로핏 성공");

                    if (response.body().getResponse() != null) {
                        List<Item> items = response.body().getResponse().getBody().getItems().getItem();
                        for (int i = 0 ; i < items.size() ; i++) {
                            if (items.get(i).getCategory().equals("PTY")) {
                                //강수형태 :  없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4), 빗방울(5), 빗방울/눈날림(6), 눈날림(7)
                                switch (items.get(i).getFcstValue()) {
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
                            } else if (items.get(i).getCategory().equals("SKY")) {
                                // 구름 상태 : 맑음(1), 구름많음(3), 흐림(4)
                                sky = items.get(i).getFcstValue();
                            } else if (items.get(i).getCategory().equals("T3H")) {
                                // 3시간 기온
                                t3h = items.get(i).getFcstValue();
                            } else if (items.get(i).getCategory().equals("POP")) {
                                // 강수확률
                                pop = items.get(i).getFcstValue();
                            } else if (items.get(i).getCategory().equals("WSD")) {
                                // 풍속
                                wsd = items.get(i).getFcstValue();
                            } else if (items.get(i).getCategory().equals("REH")) {
                                // 습도
                                reh = items.get(i).getFcstValue();
                            } else if (items.get(i).getCategory().equals("R06")) {
                                // 강수량
                                r06 = items.get(i).getFcstValue();
                            }
                        }

                        if (pty.equals("없음")) {
                            weather_tv.setText(sky);
                        } else {
                            weather_tv.setText(pty);
                        }

                        weather_tv.setText(pty);
                        t3h_tv.setText(t3h + "℃");
                        pop_tv.setText(pop + "%");
                        wsd_tv.setText(wsd + "m/s");
                        reh_tv.setText(reh + "%");
                        r06_tv.setText(pop + "mm");

                        Log.d("debug", "강수형태 = " + pty);
                        Log.d("debug", "기온 = " + t3h + "℃");
                        Log.d("debug", "강수확률 = " + pop + "%");
                        Log.d("debug", "풍속 = " + wsd + "m/s");
                        Log.d("debug", "습도 = " + reh + "%");
                        Log.d("debug", "강수량 = " + pop + "mm");

                    }


                } else {
                    Log.d("debug", "레트로핏 실패");
                    //실패
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.d("debug", "레트로핏 예외, 인터넷 끊김 등 시스템적인 이유 실패");
                Log.d("debug", t.toString());
            }

        });










    }
}
