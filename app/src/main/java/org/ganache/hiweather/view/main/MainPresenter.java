//MainPresenter class
//Model과 View를 연결하여 동작을 처리함
package org.ganache.hiweather.view.main;

import android.util.Log;

import com.google.android.gms.location.LocationServices;

import org.ganache.hiweather.R;
import org.ganache.hiweather.model.MainModel;
import org.ganache.hiweather.view.main.MainContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainPresenter implements MainContract.Presenter {
    MainContract.View view;
    MainModel mainModel;

    public MainPresenter(MainContract.View view){
        this.view = view;  //Activty View정보 가져와 통신
        mainModel = new MainModel(this);    //Model 객체 생성
    }

    //Presenter를 상속하고 addNum 구현
    @Override
    public void addNum(int num1, int num2) {
        view.showResult(num1 + num2);
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


}
