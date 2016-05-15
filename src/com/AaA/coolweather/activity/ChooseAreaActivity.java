package com.AaA.coolweather.activity;

import java.util.ArrayList;
import java.util.List;

import com.AaA.coolweather.R;
import com.AaA.coolweather.activity.WeatherActivity;
import com.AaA.coolweather.db.CoolWeatherOpenHelper;
import com.AaA.coolweather.model.City;
import com.AaA.coolweather.model.CoolWeatherDB;
import com.AaA.coolweather.model.County;
import com.AaA.coolweather.model.Province;
import com.AaA.coolweather.util.HttpCallbackListener;
import com.AaA.coolweather.util.HttpUtil;
import com.AaA.coolweather.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	private ListView listview;
	private TextView textview;
	private ArrayAdapter<String> adapter;
	private List<String> datalist = new ArrayList<String>();
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	private CoolWeatherDB coolWeatherDB;
	private final int LEVEL_PROVINCE = 1;
	private final int LEVEL_CITY = 2;
	private final int LEVEL_COUNTY = 3;
	private int currentLevel;
	private Province selectedProvince;
	private City selectedCity;
	private County selectedCounty;
	private ProgressDialog progressdialog;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listview = (ListView) findViewById(R.id.list_view);
		textview = (TextView) findViewById(R.id.title_text);
		coolWeatherDB = CoolWeatherDB.getCoolWeatherDB(this);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, datalist);
		listview.setAdapter(adapter);
if (getIntent().getBooleanExtra("is_from_WeatherActivity", false)){
	Bundle getBundle=getIntent().getBundleExtra("selected");
	selectedCity=(City) getBundle.getSerializable("selectedCity");
	selectedProvince=(Province)getBundle.getSerializable("selectedProvince");
	Log.d("test", selectedProvince.getProvinceName());
	currentLevel = LEVEL_COUNTY;
	queryCounties(selectedCity.getCityID());
}else{
	queryprovinces();
}
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);
					queryCities(selectedProvince.getProvinceID());
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					queryCounties(selectedCity.getCityID());
				} else if (currentLevel == LEVEL_COUNTY) {
					selectedCounty = countyList.get(position);
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherActivity.class);
					intent.putExtra("countyCode",
							selectedCounty.getCountyCode());
	             	Bundle bundle=new Bundle();
	            	bundle.putSerializable("selectedCity", selectedCity);
	            	bundle.putSerializable("selectedProvince", selectedProvince);
	            	intent.putExtra("selected",bundle);
	            	startActivity(intent);
					finish();
				}

			}
		});

		

	}

	private void queryprovinces() {
		provinceList = coolWeatherDB.loadProvince();
		if (provinceList.size() > 0) {
			datalist.clear();
			for (Province p : provinceList) {
				datalist.add(p.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			textview.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	private void queryCities(int provinceID) {
		cityList = coolWeatherDB.loadCity(provinceID);
		if (cityList.size() > 0) {
			datalist.clear();
			for (City c : cityList) {
				datalist.add(c.getCityName());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			textview.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	private void queryCounties(int cityID) {
		countyList = coolWeatherDB.loadCounty(cityID);
		if (countyList.size() > 0) {
			datalist.clear();
			for (County c : countyList) {
				datalist.add(c.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			textview.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	private void queryFromServer(final String code, final String level) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showprogressdialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(level)) {
					result = Utility.handleProvince(coolWeatherDB, response);

				} else if ("city".equals(level)) {
					result = Utility.handleCity(coolWeatherDB, response,
							selectedProvince.getProvinceID());
				} else if ("county".equals(level)) {
					result = Utility.handleCounty(coolWeatherDB, response,
							selectedCity.getCityID());
				}
				if (result) {
					runOnUiThread(new Runnable() {
						public void run() {
							closeProgressdialog();
							if ("province".equals(level)) {
								queryprovinces();
							} else if ("city".equals(level)) {
								queryCities(selectedProvince.getProvinceID());
							} else if ("county".equals(level)) {
								queryCounties(selectedCity.getCityID());
							}
						}

					});
				}
			}

			@Override
			public void onError(final Exception e) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						closeProgressdialog();
						Log.d("test", e.toString());
						Toast.makeText(ChooseAreaActivity.this, "加载失败",
								Toast.LENGTH_SHORT).show();
					}

				});
			}
		});
	}

	public void showprogressdialog() {
		if (progressdialog == null) {
			progressdialog = new ProgressDialog(this);
			progressdialog.setMessage("正在加载。。。");
			progressdialog.setCancelable(false);
		}
		progressdialog.show();
	}

	private void closeProgressdialog() {
		if (progressdialog != null) {
			progressdialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {

		if (currentLevel == LEVEL_CITY) {
			queryprovinces();
		} else if (currentLevel == LEVEL_COUNTY) {
			queryCities(selectedProvince.getProvinceID());
		} else {
			finish();
		}

	}


	public static void main(String[] args) {

	}

}
