package org.ganache.hiweather.view.main;

public interface MainContract {
    interface View{
        void showResult(int answer);      //값을 보여줄 View 메소드 선언
        void showProgress(boolean isShow);
        void setNowData(String nowDay, String nowTime);
        void setNowLayout(int layoutColor, int StatusColor);    // 현재 시간에 맞춰 레이아웃 디자인 변경
    }
    interface Presenter{
        void addNum(int num1, int num2);  //결과 값 구하기 위한 메소드 선언
        void getNowData();
    }
}
