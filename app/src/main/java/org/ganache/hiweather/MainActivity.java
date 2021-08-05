package org.ganache.hiweather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends AppCompatActivity {

    private String pty = "";
    private String sky = "";
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
    TextView location_tv;

    double latitude = 0;
    double longitude = 0;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window dd = getWindow();
        dd.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        FusedLocationProviderClient fusedLocationClient = null;
        weather_tv = findViewById(R.id.weather);
        t3h_tv = findViewById(R.id.t3h);
        pop_tv = findViewById(R.id.pop);
        wsd_tv = findViewById(R.id.wsd);
        reh_tv = findViewById(R.id.reh);
        r06_tv = findViewById(R.id.r06);
        location_tv = findViewById(R.id.location);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

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
            SimpleDateFormat ytDayFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);

            String ytDay = ytDayFormat.format(calendar.getTime());
            Log.d("debug_test", "ytDay = " + ytDay);
            nowDay = ytDay;
            nowTime = "2300";

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
                Toast.makeText(MainActivity.this, "위치 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
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



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
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

                            getTownWeather();

                        } else {
                            Log.d("debug_test", "####### location null #######");
                        }
                    }
                }).addOnFailureListener(this, e -> {
            Log.d("debug_test", "error =" + e.getCause());
        });
    }


    private void getTownWeather() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherRetrofit.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        WeatherRetrofit service = retrofit.create(WeatherRetrofit.class);
        Call<Example> reposCall = service.getTown("JSON", nowDay, nowTime, gridXy.x, gridXy.y, "200");
        reposCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
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

                        String getDay = dayList.get(0);
                        List<String> ptyDataList = new ArrayList<>();
                        List<String> skyDataList = new ArrayList<>();
                        List<String> t3hDataList = new ArrayList<>();

                        for (int i = 0; i < items.size(); i++) {
                            for(int j = 0 ; j < 7 ; j++) {
                                if (j != 0 && timeList.get(j).equals("0000")) {
                                    getDay = dayList.get(1);
                                }

                                if (items.get(i).getCategory().equals("PTY") && items.get(i).getFcstDate().equals(getDay) && items.get(i).getFcstTime().equals(timeList.get(j))) {
                                    ptyDataList.add(String.valueOf(Math.round(items.get(i).getFcstValue())));
                                }

                                if (items.get(i).getCategory().equals("SKY") && items.get(i).getFcstDate().equals(getDay) && items.get(i).getFcstTime().equals(timeList.get(j))) {
                                    skyDataList.add(String.valueOf(Math.round(items.get(i).getFcstValue())));
                                }

                                if (items.get(i).getCategory().equals("T3H") && items.get(i).getFcstDate().equals(getDay) && items.get(i).getFcstTime().equals(timeList.get(j))) {
                                    t3hDataList.add(String.valueOf(Math.round(items.get(i).getFcstValue())));
                                }
                            }
                        }

                        Log.d("debug_test", "############# ptyDataList = " + ptyDataList);
                        Log.d("debug_test", "############# skyDataList = " + skyDataList);
                        Log.d("debug_test", "############# t3hDataList = " + t3hDataList);

                        WeatherAdapter adapter = new WeatherAdapter();
                        recyclerView.setAdapter(adapter);

                        List<TomorrowWeather> weatherItems = new ArrayList<>();

                        String toDay = dayList.get(0);
                        for (int i=0; i<7 ;i++) {
                            TomorrowWeather item = new TomorrowWeather();
                            if (!(nowDay.equals(toDay)))
                                item.setDay("내일");

                            item.setTime((timeList.get(i).substring(0 , 2)) + "시");
                            item.setTomoT3h(t3hDataList.get(i) + "℃");
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
                                switch (String.valueOf(Math.round(items.get(i).getFcstValue()))) {
                                    case "0":
                                        pty = "없음";
                                        break;
                                    case "1":
                                        pty = "비";
                                        break;
                                    case "2":
                                        pty = "비/눈";
                                        break;
                                    case "3":
                                        pty = "눈";
                                        break;
                                    case "4":
                                        pty = "소나기";
                                        break;
                                    case "5":
                                        pty = "빗방울";
                                        break;
                                    case "6":
                                        pty = "빗방울/눈날림";
                                        break;
                                    case "7":
                                        pty = "눈날림";
                                        break;
                                    default:
                                        pty = "알수없음";
                                        break;
                                }
                            } else if (items.get(i).getCategory().equals("SKY") && items.get(i).getFcstDate().equals(nowFcDate) && items.get(i).getFcstTime().equals(nowFcTime)) {
                                // 구름 상태 : 맑음(1), 구름많음(3), 흐림(4)
                                sky = String.valueOf(Math.round(items.get(i).getFcstValue()));
                            } else if (items.get(i).getCategory().equals("T3H") && items.get(i).getFcstDate().equals(nowFcDate) && items.get(i).getFcstTime().equals(nowFcTime)) {
                                // 3시간 기온
                                t3h = String.valueOf(Math.round(items.get(i).getFcstValue()));
                            } else if (items.get(i).getCategory().equals("POP") && items.get(i).getFcstDate().equals(nowFcDate) && items.get(i).getFcstTime().equals(nowFcTime)) {
                                // 강수확률
                                pop = String.valueOf(Math.round(items.get(i).getFcstValue()));
                            } else if (items.get(i).getCategory().equals("WSD") && items.get(i).getFcstDate().equals(nowFcDate) && items.get(i).getFcstTime().equals(nowFcTime)) {
                                // 풍속
                                wsd = items.get(i).getFcstValue();
                            } else if (items.get(i).getCategory().equals("REH") && items.get(i).getFcstDate().equals(nowFcDate) && items.get(i).getFcstTime().equals(nowFcTime)) {
                                // 습도
                                reh = String.valueOf(Math.round(items.get(i).getFcstValue()));
                            } else if (items.get(i).getCategory().equals("R06") && items.get(i).getFcstDate().equals(nowFcDate) && items.get(i).getFcstTime().equals(nowFcTime)) {
                                // 강수량
                                r06 = String.valueOf(Math.round(items.get(i).getFcstValue()));
                            }
                        }

                        if (pty.equals("없음")) {
                            // 구름 상태 : 맑음(1), 구름많음(3), 흐림(4)
                            switch (sky) {
                                case "1":
                                    sky = "맑음";
                                    break;
                                case "3":
                                    sky = "구름많음";
                                    break;
                                case "4":
                                    sky = "흐림";
                                    break;
                                default:
                                    sky = pty;
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

                        r06 = r06.equals("") ? "0" : r06;
                        r06_tv.setText(r06 + "mm");

                        Log.d("debug_test", ">>>>>>> 강수형태(PTY) = " + pty);
                        Log.d("debug_test", ">>>>>>> 하늘상태(SKY) = " + sky);
                        Log.d("debug_test", ">>>>>>> 기온(T3H) = " + t3h + "℃");
                        Log.d("debug_test", ">>>>>>> 강수확률(POP) = " + pop + "%");
                        Log.d("debug_test", ">>>>>>> 풍속(WSD) = " + wsd + "m/s");
                        Log.d("debug_test", ">>>>>>> 습도(REH) = " + reh + "%");
                        Log.d("debug_test", ">>>>>>> 강수량(R06) = " + r06 + "mm");

                        getMyLocation(latitude, longitude);

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

                location_tv.setText(sido + " " + gugun);

            }
        }
    }

}






















