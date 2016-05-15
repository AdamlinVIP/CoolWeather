package com.AaA.coolweather.activity;

import java.util.ArrayList;
import java.util.List;

import com.AaA.coolweather.R;
import com.AaA.coolweather.model.City;
import com.AaA.coolweather.model.County;
import com.AaA.coolweather.util.HttpCallbackListener;
import com.AaA.coolweather.util.HttpUtil;
import com.AaA.coolweather.util.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener{

	private TextView currentDateText;
	private TextView publishText;
	private TextView weatherText;
	private TextView tempText;
	private TextView cityNameText;
	private LinearLayout weatherLayout;
	private String countyCode;
private Button backButton;
private Button refreshButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		currentDateText = (TextView) findViewById(R.id.current_date);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherText = (TextView) findViewById(R.id.weather);
		tempText = (TextView) findViewById(R.id.temp);
		cityNameText = (TextView) findViewById(R.id.city_name);
		countyCode = getIntent().getStringExtra("countyCode");
	
		weatherLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		weatherLayout.setVisibility(View.INVISIBLE);
		backButton=(Button)findViewById(R.id.button_swith_city);
		refreshButton=(Button)findViewById(R.id.button_refresh);
		publishText.setText("同步中。。。");
		queryWeatherCode();
        backButton.setOnClickListener(this); 
		refreshButton.setOnClickListener(this); 
	}

	private void queryWeatherCode() {
		String addressCountycode = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		HttpUtil.sendHttpRequest(addressCountycode, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				String[] array;
				array = response.split("\\|");
				String weatherCode = array[1];
			
				queryWeatherInfo(weatherCode);
			}
			@Override
			public void onError(Exception e) {
			}
		});
	}
		private void queryWeatherInfo(String weatherCode){
		
		String addressWeatherCode = "http://www.weather.com.cn/adat/cityinfo/"
				+ weatherCode + ".html";
	
	
		HttpUtil.sendHttpRequest(addressWeatherCode,
				new HttpCallbackListener() {
					@Override
					public void onFinish(String response) {
					Utility.handleWeatherRespond(WeatherActivity.this,
								response);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								showWeather();
							}

						});
					}

					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub
					}
				});
		
	}
	private void showWeather() {
		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(this);
		publishText.setText("今天" + spf.getString("publishTime", "") + "发布");
		cityNameText.setText(spf.getString("cityName", ""));
		weatherText.setText(spf.getString("weather", ""));
		tempText.setText(spf.getString("temp1","") + " ~ " + 
		   spf.getString("temp2",""));
		currentDateText.setText(spf.getString("currentDate", ""));
		weatherLayout.setVisibility(View.VISIBLE);

	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.button_swith_city:
				Intent intent=new Intent(WeatherActivity.this, ChooseAreaActivity.class);
				intent.putExtra("is_from_WeatherActivity", true);
		        Bundle bundle=getIntent().getBundleExtra("selected");		
		        intent.putExtra("selected",bundle);
				startActivity(intent);
		break;
			case R.id.button_refresh:
				publishText.setText("同步中。。。");
				queryWeatherCode();	
		}
		
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
//		Intent intent=new Intent(WeatherActivity.this, ChooseAreaActivity.class);
//		intent.putExtra("is_from_WeatherActivity", true);
//        Bundle bundle=getIntent().getBundleExtra("selected");		
//        intent.putExtra("selected",bundle);
//		startActivity(intent);
	}
	


}
