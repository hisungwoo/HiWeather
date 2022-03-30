//MainPresenter class
//Model과 View를 연결하여 동작을 처리함
package org.ganache.hiweather.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.ganache.hiweather.R;
import org.ganache.hiweather.contract.MainContract;
import org.ganache.hiweather.locationChange;
import org.ganache.hiweather.model.Example;
import org.ganache.hiweather.model.Item;
import org.ganache.hiweather.model.LatXLngY;
import org.ganache.hiweather.model.MainModel;
import org.ganache.hiweather.model.TomorrowWeather;
import org.ganache.hiweather.retrofit.WeatherRetrofit;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainPresenter implements MainContract.Presenter {
    MainContract.View view;
    MainModel mainModel;

    public MainPresenter(MainContract.View view){
        this.view = view;
        mainModel = new MainModel(this);
    }

    @Override
    public void getNowData() {
        String nowDay;
        String nowTime;
        int layoutColor;
        int statusColor;

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat dayDate = new SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault());
        nowDay = dayDate.format(mDate);

        SimpleDateFormat timeDate = new SimpleDateFormat("HHmm", java.util.Locale.getDefault());
        String dateTime = timeDate.format(mDate);

        Log.d("debug_test", "nowDay = " + nowDay);
        Log.d("debug_test", "dateTime = " + dateTime);

        int nowTimeInt = Integer.parseInt(dateTime);
        //0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300

        if (nowTimeInt < 200) {
            SimpleDateFormat ytDayFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);

            layoutColor = R.color.colorEveningBack;
            statusColor = R.color.colorEveningStatus;
            nowDay = ytDayFormat.format(calendar.getTime());
            nowTime = "2300";
            // nowDay -1
            // 2300

        } else if (nowTimeInt < 500) {
            nowTime = "0200";
            layoutColor = R.color.colorEveningBack;
            statusColor = R.color.colorEveningStatus;
        } else if (nowTimeInt < 800) {
            nowTime = "0500";
            layoutColor = R.color.colorMorningBack;
            statusColor = R.color.colorMorningStatus;
        } else if (nowTimeInt < 1100) {
            nowTime = "0800";
            layoutColor = R.color.colorMorningBack;
            statusColor = R.color.colorMorningStatus;
        } else if (nowTimeInt < 1400) {
            nowTime = "1100";
            layoutColor = R.color.colorAfterBack;
            statusColor = R.color.colorAfterStatus;
        } else if (nowTimeInt < 1700) {
            nowTime = "1400";
            layoutColor = R.color.colorAfterBack;
            statusColor = R.color.colorAfterStatus;
        } else if (nowTimeInt < 2000) {
            nowTime = "1700";
            layoutColor = R.color.colorAfterBack;
            statusColor = R.color.colorAfterStatus;
        } else if (nowTimeInt < 2300) {
            nowTime = "2000";
            layoutColor = R.color.colorEveningBack;
            statusColor = R.color.colorEveningStatus;
        } else {
            nowTime = "2300";
            layoutColor = R.color.colorEveningBack;
            statusColor = R.color.colorEveningStatus;
        }
        Log.d("debug_test", "nowTime = " + nowTime);

        view.setNowData(nowDay, nowTime);
        view.setNowLayout(layoutColor, statusColor);
    }

    @Override
    public void getCoordinate(Context cont) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(cont);
        if (ActivityCompat.checkSelfPermission(cont, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(cont, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) cont, new OnSuccessListener<Location>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            locationChange locChange = new locationChange();
                            LatXLngY gridXy = locChange.convertGRID_GPS(0, location.getLatitude(), location.getLongitude());
                            Log.d("debug_test", "x = " + gridXy.x);
                            Log.d("debug_test", "y = " + gridXy.y);

                            view.setCoordinate(location.getLatitude(), location.getLongitude(), gridXy);
                            view.getWeather();

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
                                    view.startApp();
                                }
                            };
                            fusedLocationClient.requestLocationUpdates(request, locationCallback, null);

                        }
                    }
                }).addOnFailureListener((Activity) cont, e -> {
                    Log.d("debug_test", "error =" + e.getCause());
                });
    }

    @Override
    public void requestApi(String nowDay, String nowTime, LatXLngY gridXy) {
        mainModel.getWeatherList(nowDay, nowTime, gridXy);
    }

    @Override
    public void setWeatherInfo(List<Item> items) {
        if(items != null) {
            String pty = "";
            String sky = "";
            String tmp = "";
            String pop = "";
            float wsd = 0;
            String pcp = "";
            String reh = "";
            int tmx = 0;
            int tmn = 0;

            List<String> dayTempList = new ArrayList<>();
            List<String> timeTempList = new ArrayList<>();

            for (int i = 0 ; i < items.size() ; i++) {
                dayTempList.add(items.get(i).getFcstDate());
                timeTempList.add(items.get(i).getFcstTime());
            }

            List<String> dayList = dayTempList.stream().distinct().collect(Collectors.toList());
            List<String> timeList = timeTempList.stream().distinct().collect(Collectors.toList());


            Log.d("debug_test", "dayList = " + dayList.toString());
            Log.d("debug_test", "timeList = " + timeList.toString());

            List<String> ptyDataList = new ArrayList<>();
            List<String> skyDataList = new ArrayList<>();
            List<Integer> tmpDataList = new ArrayList<>();
            List<String> timeDataList = new ArrayList<>();

            List<String> ptyDataList2 = new ArrayList<>();
            List<String> skyDataList2 = new ArrayList<>();
            List<Integer> tmpDataList2 = new ArrayList<>();
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
                        tmpDataList.add(Integer.parseInt(items.get(i).getFcstValue()));
                    }

                    if(items.get(i).getCategory().equals("TMP") && items.get(i).getFcstDate().equals(tomoDay) && items.get(i).getFcstTime().equals(timeList.get(j))) {
                        tmpDataList2.add(Integer.parseInt(items.get(i).getFcstValue()));
                    }
                }
            }

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

            view.updateRecyclerView(weatherItems);

            String nowFcDate = items.get(0).getFcstDate();
            String nowFcTime = items.get(0).getFcstTime();

            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getCategory().equals("PTY") && items.get(i).getFcstDate().equals(nowFcDate) && items.get(i).getFcstTime().equals(nowFcTime)) {
                    //강수형태 :  없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4), 빗방울(5), 빗방울/눈날림(6), 눈날림(7)
                    switch (items.get(i).getFcstValue()) {
                        case "0":
                            pty = "없음";
                            break;
                        case "1":
                            pty = "비";
                            view.setWeatherImg(R.drawable.rain);
                            break;
                        case "2":
                            pty = "비/눈";
                            view.setWeatherImg(R.drawable.rain);
                            break;
                        case "3":
                            pty = "눈";
                            view.setWeatherImg(R.drawable.snow);
                            break;
                        case "4":
                            pty = "소나기";
                            view.setWeatherImg(R.drawable.shower);
                            break;
                        case "5":
                            pty = "빗방울";
                            view.setWeatherImg(R.drawable.shower);
                            break;
                        case "6":
                            pty = "빗방울/눈날림";
                            view.setWeatherImg(R.drawable.shower);
                            break;
                        case "7":
                            pty = "눈날림";
                            view.setWeatherImg(R.drawable.snow);
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
                }
            }

            tmx = Collections.max(tmpDataList);
            tmn = Collections.min(tmpDataList);

            if (pty.equals("없음")) {
                // 구름 상태 : 맑음(1), 구름많음(3), 흐림(4)
                switch (sky) {
                    case "1":
                        sky = "맑음";
                        view.setWeatherImg(R.drawable.sunny);
                        break;
                    case "3":
                        sky = "구름많음";
                        view.setWeatherImg(R.drawable.cloudy);
                        break;
                    case "4":
                        sky = "흐림";
                        view.setWeatherImg(R.drawable.murky);
                        break;
                    default:
                        sky = pty;
                        break;
                }
                view.setWeatherTextView(sky);
            } else {
                view.setWeatherTextView(pty);
            }

            HashMap<String, String> weathers = new HashMap<>();
            weathers.put("tmp", tmp);
            weathers.put("pop", pop);
            weathers.put("wsd", String.valueOf(wsd));
            weathers.put("reh", reh);
            weathers.put("tmx", String.valueOf(tmx));
            weathers.put("tmn", String.valueOf(tmn));
            weathers.put("pcp", pcp);

            view.setWeatherItems(weathers);

            Log.d("debug_test", ">>>>>>> 강수형태(PTY) = " + pty);
            Log.d("debug_test", ">>>>>>> 하늘상태(SKY) = " + sky);
            Log.d("debug_test", ">>>>>>> 기온(TMP) = " + tmp + "˚");
            Log.d("debug_test", ">>>>>>> 강수확률(POP) = " + pop + "%");
            Log.d("debug_test", ">>>>>>> 풍속(WSD) = " + wsd + "m/s");
            Log.d("debug_test", ">>>>>>> 습도(REH) = " + reh + "%");
            Log.d("debug_test", ">>>>>>> 강수량(PCP) = " + pcp + "mm");
            Log.d("debug_test", ">>>>>>> 최고온도(TMX) = " + tmx + "˚");
            Log.d("debug_test", ">>>>>>> 최저온도(TMN) = " + tmn + "˚");

            view.startAnim();
            view.setMyLocation();
            view.showProgress(false);
        } else {
            view.showToast("알 수 없는 이유로 데이터를 가져올 수 없습니다.");
        }
    }

    @Override
    public void getMyLocation(Context cont, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(cont);
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

                view.setLocationTextView(sido, gugun);
            }
        }
    }


}
