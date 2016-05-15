package com.AaA.coolweather.model;

import java.util.ArrayList;
import java.util.List;

import com.AaA.coolweather.db.CoolWeatherOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
private CoolWeatherOpenHelper dbhelper;
private static final String DATABASENAME="coolweather1.db";
private static final int VERSION =1;
private static CoolWeatherDB coolWeatherDB;
private SQLiteDatabase db;
	private CoolWeatherDB(Context context){
	dbhelper=new CoolWeatherOpenHelper(context, DATABASENAME, null, VERSION);
	db=dbhelper.getWritableDatabase();
	}
	public synchronized static CoolWeatherDB getCoolWeatherDB(Context context){
		if(coolWeatherDB==null){
			coolWeatherDB=new CoolWeatherDB(context);
		}
		return coolWeatherDB;		
	}
	public void saveProvince(Province province){
		if(province !=null){
		ContentValues values=new ContentValues();
		values.put("province_name", province.getProvinceName());
		values.put("province_code", province.getProvinceCode());
		db.insert("province", null, values);
	}}
	public List<Province> loadProvince(){
		List<Province> list= new ArrayList<Province>();
			Cursor cursor=db.query("province", null,null,null,null,null,null);
		if(cursor.moveToFirst()){
			do{ Province province=new Province();
			province.setProvinceID(cursor.getInt(cursor.getColumnIndex("id")));
			province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
			province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
			list.add(province);
			}while(cursor.moveToNext());
		}
		return list;
	}
	
	public void saveCity(City city){
		if(city !=null){
		ContentValues values=new ContentValues();
		values.put("city_name", city.getCityName());
		values.put("city_code", city.getCityCode());
		values.put("province_id", city.getProvince_id());
		db.insert("city", null, values);
	}}
	
	public List<City> loadCity(int provinceID){
		List<City> list=new ArrayList<City>();
		Cursor cursor=db.query("city", null, "province_id=?", 
				new String[]{String.valueOf(provinceID)}, null,null,null);
		if(cursor.moveToFirst()){
			do{City city=new City();
			city.setCityID(cursor.getInt(cursor.getColumnIndex("id")));
			city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
			city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
			city.setProvince_id(provinceID);
			list.add(city);
			}while(cursor.moveToNext());
		}
		return list;
		
	}
	
	public void saveCounty(County county){
		if(county !=null){
		ContentValues values=new ContentValues();
		values.put("county_name", county.getCountyName());
		values.put("county_code", county.getCountyCode());
		values.put("city_id", county.getCity_id());
		db.insert("county", null, values);
	}}
	
	public List<County> loadCounty(int cityID){
		List<County> list=new ArrayList<County>();
		Cursor cursor=db.query("county", null, "city_id=?", new String[]{String.valueOf(cityID)}, 
				null,null,null);
		if(cursor.moveToFirst()){
			do{
				County county=new County();
				county.setCity_id(cityID);
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyID(cursor.getInt(cursor.getColumnIndex("id")));
				list.add(county);
			}while(cursor.moveToNext());
		}
		return list;
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
