package org.ganache.hiweather.Presenter;

public interface Contract {
    interface View{
        void showResult(int answer);      //값을 보여줄 View 메소드 선언
    }
    interface Presenter{
        void addNum(int num1, int num2);  //결과 값 구하기 위한 메소드 선언
    }
}
