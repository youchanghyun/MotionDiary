package com.example.googlemaptest;

import java.io.IOException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends FragmentActivity {
	private static final String TAG = "LogTest";

	public int count = 0; // ���° �������� �����ϴ� index
	LocationManager manager;
	Location loca;
	public GoogleMap mGoogleMap;
	Polyline polyline;
	DecimalFormat df = new DecimalFormat();

	long time;
	int currentDay;

	ArrayList<Data> data = new ArrayList<Data>(); // dataŬ���� ArrayList ����
	public ArrayList<Time> list_time = new ArrayList<Time>();
	public ArrayList<LatLng> list_LatLng = new ArrayList<LatLng>();
	public ArrayList<PolylineOptions> ops = new ArrayList<PolylineOptions>();

	Data firstData;
	Data secondData;

	DB_OPEN db_open;
	SQLiteDatabase db;
	public int index = 0;
	public int _index = 0;
	public int markIndex = 0;
	public int num_db;
	public int end_num;

	MyLocationListener listener = new MyLocationListener();

	LatLng loc = new LatLng(37.58660, 126.94352); // LatLng�� ���� �浵 ���� Ŭ����
	CameraPosition cp = new CameraPosition.Builder().target((loc)).zoom(18)
			.build();
	public ArrayList<MarkerOptions> marker = new ArrayList<MarkerOptions>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		Calendar cal = Calendar.getInstance();
		currentDay = cal.get(Calendar.DAY_OF_MONTH);
		marker.add(new MarkerOptions());
		db_open = new DB_OPEN(this);
		db = db_open.getWritableDatabase();

		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap(); // ȭ�鿡 ���۸� ǥ��

		mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp)); // ������ġ��
																				// �̵�
		ops.clear();

		// DB�� ����� value�� �޾ƿ���
		Cursor c_id = db.rawQuery("SELECT user_id FROM sensor_data WHERE day ="
				+ currentDay, null);
		c_id.moveToFirst();
		c_id.getCount();

		for (int i = 0; i < c_id.getCount(); i++) {
			data.add(new Data());
		}

		for (int i = 0; i < c_id.getCount(); i++) {
			data.get(i).id = c_id.getInt(0);
			Log.v(TAG, "id" + " " + data.get(i).id);
			c_id.moveToNext();
		}
		Log.v(TAG, "ó�� id �޾ƿ���");

		Cursor c_lat = db.rawQuery(
				"SELECT latitude FROM sensor_data WHERE day =" + currentDay,
				null);
		c_lat.moveToFirst();
		c_lat.getCount();

		for (int i = 0; i < c_lat.getCount(); i++) {
			data.get(i)._lat = c_lat.getDouble(0);
			Log.v(TAG, "lat[" + i + "] : " + " " + data.get(i)._lat);
			c_lat.moveToNext();
		}
		Log.v(TAG, "ó�� lat �޾ƿ���");

		Cursor c_lng = db.rawQuery(
				"SELECT longitude FROM sensor_data WHERE day =" + currentDay,
				null);
		c_lng.moveToFirst();
		c_lng.getCount();

		for (int i = 0; i < c_lng.getCount(); i++) {
			data.get(i)._lng = c_lng.getDouble(0);
			Log.v(TAG, "lng[" + i + "] : " + " " + data.get(i)._lng);
			c_lng.moveToNext();
		}
		Log.v(TAG, "ó�� lng �޾ƿ���");

		Log.e(TAG, c_lng.getCount() + "");

		for (int i = 0; i < c_lng.getCount(); i++) {
			Log.e(TAG,
					i + " id : " + data.get(i).id + " " + "lat[i] : "
							+ data.get(i)._lat + " " + "lng[i] : "
							+ data.get(i)._lng);
		}

		num_db = c_lng.getCount();

		Cursor c_month = db.rawQuery(
				"SELECT month FROM sensor_data WHERE day =" + currentDay, null);
		c_month.moveToFirst();
		c_month.getCount();

		for (int i = 0; i < c_month.getCount(); i++) {
			data.get(i).month = c_month.getInt(0);
			Log.v(TAG, "month[" + i + "] : " + " " + data.get(i).month);
			c_month.moveToNext();
		}
		Log.v(TAG, "ó�� month �޾ƿ���");

		Cursor c_day = db.rawQuery("SELECT day FROM sensor_data WHERE day ="
				+ currentDay, null);
		c_day.moveToFirst();
		c_day.getCount();

		for (int i = 0; i < c_day.getCount(); i++) {
			data.get(i).day = c_day.getInt(0);
			Log.v(TAG, "day[" + i + "] : " + " " + data.get(i).day);
			c_day.moveToNext();
		}
		Log.v(TAG, "ó�� day �޾ƿ���");

		Cursor c_hour = db.rawQuery("SELECT hour FROM sensor_data WHERE day ="
				+ currentDay, null);
		c_hour.moveToFirst();
		c_hour.getCount();

		for (int i = 0; i < c_hour.getCount(); i++) {
			data.get(i).hour = c_hour.getInt(0);
			Log.v(TAG, "hour[" + i + "] : " + " " + data.get(i).hour);
			c_hour.moveToNext();
		}
		Log.v(TAG, "ó�� hour �޾ƿ���");

		Cursor c_minute = db
				.rawQuery("SELECT minute FROM sensor_data WHERE day ="
						+ currentDay, null);
		c_minute.moveToFirst();
		c_minute.getCount();

		for (int i = 0; i < c_minute.getCount(); i++) {
			data.get(i).minute = c_minute.getInt(0);
			Log.v(TAG, "minute[" + i + "] : " + " " + data.get(i).minute);
			c_minute.moveToNext();
		}
		Log.v(TAG, "ó�� minute �޾ƿ���");

		Cursor c_second = db
				.rawQuery("SELECT second FROM sensor_data WHERE day ="
						+ currentDay, null);
		c_second.moveToFirst();
		c_second.getCount();

		for (int i = 0; i < c_second.getCount(); i++) {
			data.get(i).second = c_second.getInt(0);
			Log.v(TAG, "second[" + i + "] : " + " " + data.get(i).second);
			c_second.moveToNext();
		}
		Log.v(TAG, "ó�� second �޾ƿ���");

		int time_ = 0;

		marker.add(new MarkerOptions()); // marker Icon�� ���� ��ü �߰�
		for (int i = 0; i < c_lng.getCount(); i++) {
			if (i == 0 || i == 1) {
				if (i == 0) 
					firstData = data.get(i);
				else {
					if (data.get(i).id != data.get(i - 1).id) {
						secondData = data.get(i);
						time_ = diffTime(firstData, secondData);

						switch (data.get(i).id) {
						// �ȱ�� �ൿ�� �ٲ��� �� ������ ���
						case 1:
							marker.get(markIndex)
									.position(
											new LatLng(data.get(i)._lat, data
													.get(i)._lng))
									.title("�ȱ�")
									.snippet(time_ + "�� �� �̵�")
									.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.walking));
							mGoogleMap.addMarker(marker.get(markIndex));
							// marker.add(new MarkerOptions());
							break;
						// �ٱ�� �ൿ�� �ٲ��� �� ������ ���
						case 2:
							marker.get(markIndex)
									.position(
											new LatLng(data.get(i)._lat, data
													.get(i)._lng))
									.title("�ٱ�")
									.snippet(time_ + "�� �� �̵�")
									.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.running));
							mGoogleMap.addMarker(marker.get(markIndex));
							// marker.add(new MarkerOptions());
							break;
						// ���� �ÿ� �ݰ��� ǥ��
						default:
							mGoogleMap.addCircle(new CircleOptions()
									.center(new LatLng(data.get(i)._lat, data
											.get(i)._lng)).radius(20)
									.fillColor(0x3380F5FF)
									.strokeColor(0x5580F5FF));
							break;
						}
						
						firstData = data.get(i);
					} else
						continue;
				}
				continue;
			} else {
				if (data.get(i - 1).id == data.get(i - 2).id)
					continue;
				else {

					secondData = data.get(i - 1);
					time_ = diffTime(firstData, secondData);

					marker.add(new MarkerOptions());
					// �ȱ�,�ٱ�,���� �� ���� �ൿ�� �̺�Ʈ ����
					switch (data.get(i - 1).id) {
					// �ȱ�� �ൿ�� �ٲ��� �� ������ ���
					case 1:
						marker.get(markIndex)
								.position(
										new LatLng(data.get(i - 1)._lat, data
												.get(i - 1)._lng))
								.title("�ȱ�")
								.snippet(time_ + "�� �� �̵�")
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.walking));
						mGoogleMap.addMarker(marker.get(markIndex));
						// marker.add(new MarkerOptions());
						break;
					// �ٱ�� �ൿ�� �ٲ��� �� ������ ���
					case 2:
						marker.get(markIndex)
								.position(
										new LatLng(data.get(i - 1)._lat, data
												.get(i - 1)._lng))
								.title("�ٱ�")
								.snippet(time_ + "�� �� �̵�")
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.running));
						mGoogleMap.addMarker(marker.get(markIndex));
						// marker.add(new MarkerOptions());
						break;
					// ���� �ÿ� �ݰ��� ǥ��
					default:
						mGoogleMap.addCircle(new CircleOptions()
								.center(new LatLng(data.get(i - 1)._lat, data
										.get(i - 1)._lng)).radius(20)
								.fillColor(0x3380F5FF).strokeColor(0x5580F5FF));
						break;
					}
				}
				markIndex++;

				firstData = data.get(i - 1);
			}
		}

		ops.add(new PolylineOptions());
		// �ൿ���� ���� �׷��ִ� �κ�
		if (data.size() > 1 && count == 0) {
			for (int i = 0; i < num_db; i++) {
				if (data.get(i).id == 1) {
					ops.add(new PolylineOptions());
					if (i != 0) {
						mGoogleMap.addPolyline(ops
								.get(index)
								.add(new LatLng(data.get(i - 1)._lat, data
										.get(i - 1)._lng)).color(Color.RED));
					}
					mGoogleMap
							.addPolyline(ops
									.get(index)
									.add(new LatLng(data.get(i)._lat, data
											.get(i)._lng)).color(Color.RED));
					Log.e(TAG, index + " �ȱ� red �� �߰�");
				} else {
					if (data.get(i).id == 3) {
						if (data.get(i - 1).id == 1) {
							ops.add(new PolylineOptions());
							if (i != 0) {
								mGoogleMap.addPolyline(ops
										.get(index)
										.add(new LatLng(data.get(i - 1)._lat, data
												.get(i - 1)._lng))
										.color(Color.RED));
							}
							mGoogleMap.addPolyline(ops
									.get(index)
									.add(new LatLng(data.get(i)._lat,
											data.get(i)._lng)).color(Color.RED));
						} else {
							ops.add(new PolylineOptions());
							if (i != 0) {
								mGoogleMap.addPolyline(ops
										.get(index)
										.add(new LatLng(data.get(i - 1)._lat, data
												.get(i - 1)._lng))
										.color(Color.BLUE));
							}
							mGoogleMap.addPolyline(ops
									.get(index)
									.add(new LatLng(data.get(i)._lat,
											data.get(i)._lng)).color(Color.BLUE));
						}
					} else {
						ops.add(new PolylineOptions());
						if (i != 0) {
							mGoogleMap.addPolyline(ops
									.get(index)
									.add(new LatLng(data.get(i - 1)._lat, data
											.get(i - 1)._lng))
									.color(Color.BLUE));
						}
						mGoogleMap.addPolyline(ops
								.get(index)
								.add(new LatLng(data.get(i)._lat,
										data.get(i)._lng)).color(Color.BLUE));
						Log.e(TAG, index + " �ٱ� blue �� �߰�");
					}
				}
				index++;
			}
		}
		end_num = index;

		 getMyLocation();
	}

	private void getMyLocation() {
		if (manager == null) {
			manager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
		}
		// provider ������||GPS �� ���ؼ� �������� �˷��ִ� Stirng ����
		// minTime �ּ��� �󸶸��� �ð��� �帥�� ��ġ������ �������� �ð������� ���� �����ϴ� ����
		// minDistance �󸶸��� �Ÿ��� �������� ��ġ������ �������� �����ϴ� ����
		// manager.requestLocationUpdates(provider, minTime, minDistance,
		// listener);

		// 3��
		long minTime = 3000;
		// �Ÿ��� 0
		float minDistance = 0;

		Criteria criteria = new Criteria();
		// criteria.setAccuracy(Criteria.ACCURACY_FINE);

		String provider = manager.getBestProvider(criteria, true);

		Toast.makeText(MapActivity.this, provider, Toast.LENGTH_SHORT).show();

		manager.requestLocationUpdates(provider, minTime, minDistance, listener);
		loca = manager.getLastKnownLocation(provider);
	}

	class MyLocationListener implements LocationListener {
		// ��ġ������ �Ʒ� �޼��带 ���ؼ� ���޵ȴ�.
		@Override
		public void onLocationChanged(Location location) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			long nowTime;
			String st;

			nowTime = System.currentTimeMillis();
			int act_num=0;

			st = "" + nowTime;

			list_time.add(new Time());

			Calendar calendar = Calendar.getInstance();
			list_time.get(count).month = calendar.get(Calendar.MONTH) + 1;
			list_time.get(count).day = calendar.get(Calendar.DAY_OF_MONTH);
			list_time.get(count).hour = calendar.get(Calendar.HOUR);
			list_time.get(count).minute = calendar.get(Calendar.MINUTE);
			list_time.get(count).second = calendar.get(Calendar.SECOND);

			Log.v(TAG, list_time.get(count).month + "��"
					+ list_time.get(count).day + "��"
					+ list_time.get(count).hour + "��"
					+ list_time.get(count).minute + "��"
					+ list_time.get(count).second + "��");

			// list_LatLng.add(new LatLng(latitude, longitude));

			list_LatLng.add(new LatLng(latitude + count * 0.1
					* (count * 0.00074), longitude + (count * 0.00042)));

			if (count % 5 == 0) {
				act_num = 1;
				Log.v(TAG, "insert �ȱ�");
			} else {
				if (count % 3 == 0) {
					act_num = 2;
					Log.v(TAG, "insert ����");
				} else {
					act_num = 3;
					Log.v(TAG, "insert �ٱ�");
				}
			}
			
			db.execSQL("INSERT INTO sensor_data"
					+ "(user_id, latitude, longitude,month,day,hour,minute,second)"
					+ " VALUES (" + act_num + ","
					+ list_LatLng.get(count).latitude + ","
					+ list_LatLng.get(count).longitude + ","
					+ list_time.get(count).month + ","
					+ list_time.get(count).day + ","
					+ list_time.get(count).hour + ","
					+ list_time.get(count).minute + ","
					+ list_time.get(count).second + ")");
			
			CameraPosition cp = new CameraPosition.Builder()
					.target(new LatLng(list_LatLng.get(count).latitude,
							list_LatLng.get(count).longitude)).zoom(17).build();

			mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
			count++;
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	// ���ּ� �������� �Լ�
	private String findAddress(double lat, double lng) {
		String bf = new String();
		Geocoder geocoder = new Geocoder(this, Locale.KOREAN);
		List<Address> address = null;
		Log.v(TAG, lat + " " + lng);

		try {
			// ����° �μ��� �ִ������ε� �ϳ��� ���Ϲ޵��� �����ߴ�
			address = geocoder.getFromLocation(lat, lng, 1);
			// ������ �����ͷ� �ּҰ� ���ϵ� �����Ͱ� ������
			Log.e(TAG, "try");
		} catch (IOException e) {
			Toast.makeText(this, "�ּ���� ����", Toast.LENGTH_LONG).show();
			Log.e(TAG, "catch");
			e.printStackTrace();
		}

		if (address == null) {
			Log.e(TAG, "null�� ��ȯ");
			return null;
		}
		if (address.size() > 0) {
			Address addr = address.get(0);
			bf = addr.getCountryName() + " " + " " + addr.getLocality() + " "
					+ addr.getThoroughfare() + " " + addr.getFeatureName();
			Log.e(TAG, bf);
		}
		return bf;
	}

	public int diffTime(Data a, Data b) {
		int result = 0;
		if (a.hour <= b.hour) {
			if (a.minute < b.minute) {
				if (b.second > a.second)
					result = (b.minute - a.minute) * 60 + (b.second - a.second);
				else {
					result = (b.minute - a.minute - 1) * 60
							+ (a.second - b.second);
				}
			} else {
				if (b.second > a.second)
					result = (b.minute - a.minute) * 60 + (b.second - a.second);
			}
		}
		return result;
	}
}