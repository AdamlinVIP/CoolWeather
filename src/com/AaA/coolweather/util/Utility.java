package com.AaA.coolweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.AaA.coolweather.model.City;
import com.AaA.coolweather.model.CoolWeatherDB;
import com.AaA.coolweather.model.County;
import com.AaA.coolweather.model.Province;

public class Utility {

	/**
	 * 解释和处理服务器返回的数据
	 */

	public synchronized static boolean handleProvince(
			CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allprovinces = response.split(",");
			if (allprovinces != null && allprovinces.length > 0) {
				for (String p : allprovinces) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	public synchronized static boolean handleCity(CoolWeatherDB coolWeatherDB,
			String response, int provinceID) {
		if (!TextUtils.isEmpty(response)) {
			String[] allcity = response.split(",");
			if (allcity != null && allcity.length > 0) {
				for (String c : allcity) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvince_id(provinceID);
					coolWeatherDB.saveCity(city);
				}
			}
			return true;
		}

		return false;
	}

	public synchronized static boolean handleCounty(
			CoolWeatherDB coolWeatherDB, String response, int cityID) {
		if (!TextUtils.isEmpty(response)) {
			String[] allcounty = response.split(",");
             if(allcounty!=null && allcounty.length>0){
            	 for(String c:allcounty){
            		 County county=new County();
            		 String[] array=c.split("\\|");
            		 county.setCountyCode(array[0]);
            		 county.setCountyName(array[1]);
            		 county.setCity_id(cityID);
            	 coolWeatherDB.saveCounty(county);
            	 }
             }
             return true;
		}
		return false;

	}
	/**
	 * 解释Json数据
	*/
	public static void handleWeatherRespond(Context context, String response){
		try {
			JSONObject jsonObject=new JSONObject(response);
			JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
			String cityName=weatherInfo.getString("city");
			String temp1=weatherInfo.getString("temp1");
			String temp2=weatherInfo.getString("temp2");
			String weather=weatherInfo.getString("weather");
			String publishTime=weatherInfo.getString("ptime");
			saveWeather(context, cityName, temp1, temp2, weather, publishTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 保存从handleWeatherResponse来的天气信息到preference
	 * @param args
	 */
	  public static void saveWeather(Context context, String cityName, String temp1, String temp2,
    		 String weather, String publishTime){
	      SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		  SharedPreferences sharePreference=PreferenceManager.getDefaultSharedPreferences(context);
	      SharedPreferences.Editor editor = sharePreference.edit();
	      editor.putBoolean("selectCounty", true);
	      editor.putString("cityName", cityName);
	      editor.putString("temp1", temp1);
	      editor.putString("temp2", temp2);
	      editor.putString("weather", weather);
	      editor.putString("weather",weather);
	      editor.putString("publishTime", publishTime);
	      editor.putString("currentDate", sdf.format(new Date()));
	      editor.commit();
}
	public static void main(String[] args) {

	}

}
