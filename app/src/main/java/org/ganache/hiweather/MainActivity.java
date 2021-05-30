package org.ganache.hiweather;

import android.os.Bundle;

import org.ganache.hiweather.model.Example;
import org.ganache.hiweather.model.Repos;
import org.ganache.hiweather.retrofit.WeatherService;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherService.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);
        String key = "E6PZth5Xxp14kb9K%2BcdqMVPdltgGfmjR5OY8gEi1ARAV7mibmfWj7lq54rPJx0wiWoNJ0jZHAyMMsto875iTPw%3D%3D";

        Call<Example> reposCall = service.listRepos2();

        reposCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                if(response.isSuccessful()) {
                    System.out.println("성공");
                    Example test = response.body();
                    System.out.println(test.toString());
                    System.out.println(test.getResponse().getBody().getTotalCount());


                    //성공
                } else {
                    System.out.println("실패");
                    //실패
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                //예외, 인터넷 끊김 등 시스템적인 이유 실패
                System.out.println("2");
            }


        });

    }
}
