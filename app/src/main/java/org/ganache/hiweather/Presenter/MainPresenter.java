package org.ganache.hiweather.Presenter;

//Presenter줌
public class MainPresenter implements Contract.Presenter {
    Contract.View view;
//    MainModel mainModel;
    public MainPresenter(Contract.View view){
        this.view = view;                   //Activty View정보 가져와 통신
//        mainModel = new MainModel(this);    //Model 객체 생성
    }

    //Presenter를 상속하고 addNum 구현
    @Override
    public void addNum(int num1, int num2) {
        view.showResult(num1 + num2);
    }
}
