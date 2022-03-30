package org.ganache.hiweather.contract;

import android.content.Context;

import org.ganache.hiweather.model.Item;
import org.ganache.hiweather.model.LatXLngY;
import org.ganache.hiweather.model.TomorrowWeather;

import java.util.HashMap;
import java.util.List;

public interface MainContract {
    interface View {
        void startApp();
        void showProgress(boolean isShow);
        void setNowData(String nowDay, String nowTime); // 현재 날짜, 시간 셋팅
        void setNowLayout(int layoutColor, int StatusColor);    // 현재 시간에 맞춰 레이아웃 디자인 변경
        void setCoordinate(double latitude, double longitude, LatXLngY gridXy); // 현재 좌표 셋팅
        void getWeather();  // 현재 날짜, 시간, 좌표를 이용하여 날씨 정보 획득
        void showToast(String text);
        void updateRecyclerView(List<TomorrowWeather> list); // 시간별 날씨 정보 RecyclerView 업데이트
        void setWeatherImg(int drawable);   // 날씨 이미지 셋팅
        void setWeatherTextView(String weather);    // 날씨 정보 셋팅
        void setWeatherItems(HashMap<String, String> items);    // 습도, 강수확률, 강수량, 풍속 등 셋팅
        void setLocationTextView(String sido, String gugun);    // 현재 내 위치 시군구 셋팅
        void setMyLocation();   // 현재 나의 위치 획득
        void startAnim();
    }
    interface Presenter {
        void getNowData();  // 현재 날짜,시간 정보 획득
        void getCoordinate(Context context);  // 현재 위치의 좌표 획득
        void requestApi(String nowDay, String nowTime, LatXLngY gridXy);    // MainModel 연동 하여 Retrofit 실행
        void setWeatherInfo(List<Item> items); // 날짜, 시간, 좌표를 이용하여 날씨 정보 획득
        void getMyLocation(Context cont, double latitude, double longitude);    // 현재 나의 위치
    }

    interface Model {
        void getWeatherList(String nowDay, String nowTime, LatXLngY gridXy);    // Retrofit를 이용하여 공공데이터 API와 연동
    }
}
