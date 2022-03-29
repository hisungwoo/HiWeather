//MainModel class
//데이터 관리를 해줄 클래스
package org.ganache.hiweather.model;

import org.ganache.hiweather.view.main.MainContract;

public class MainModel {
    MainContract.Presenter presenter;

    public MainModel(MainContract.Presenter presenter){
        this.presenter = presenter;
    }
}
