package org.ganache.hiweather;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.ganache.hiweather.model.Example;
import org.ganache.hiweather.model.Repos;
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

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");
        String nowTime = simpleDate.format(mDate);

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
        Call<Example> reposCall = service.listRepos("JSON", "20210620","2000","58", "125");
        reposCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                if(response.isSuccessful()) {
                    Log.d("debug", "레트로핏 성공");
                    Example test = response.body();

                    Log.d("debug", "toString = " + test.toString());
                    Log.d("debug", "getTotalCount = " + test.getResponse().getBody().getTotalCount());


                    //성공
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
