package com.example.lwb.netrequestdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.lwb.netrequestdemo.bean.Banner;
import com.example.lwb.netrequestdemo.http.OkHttpHelper;
import com.example.lwb.netrequestdemo.http.SpotsCallBack;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private SliderLayout mSliderLayout;
    private Gson mGson = new Gson();

    private List<Banner> mBanner;


    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSliderLayout = (SliderLayout) findViewById(R.id.slider);

        requestImages();
    }

    private void requestImages() {
        String url = "http://112.124.22.238:8081/course_api/banner/query?type=1";

        //获取到的数据存入bean中即Banner中，然后再传入OKHttpHelper中的callbackSuccess方法里，再传入baseCallback
        //中的onsuccess中，即SpotsCallBack的onSuccess中。这就是整个数据的传递过程。
        httpHelper.get(url, new SpotsCallBack<List<Banner>>(this) {
            @Override
            public void onFailure(Call call, Exception e) {

            }

            @Override
            public void onSuccess(Response response, List<Banner> banners) {
                mBanner = banners;
                initSlider();
            }

            private void initSlider() {

                if(mBanner !=null){

                    for (Banner banner : mBanner){


                        TextSliderView textSliderView = new TextSliderView(getApplicationContext());
                        textSliderView.image(banner.getImgUrl());
                        textSliderView.description(banner.getName());
                        textSliderView.setScaleType(BaseSliderView.ScaleType.Fit);
                        mSliderLayout.addSlider(textSliderView);

                    }
                }



                mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

                mSliderLayout.setCustomAnimation(new DescriptionAnimation());
                mSliderLayout.setPresetTransformer(SliderLayout.Transformer.RotateUp);
                mSliderLayout.setDuration(3000);
            }


            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }

}