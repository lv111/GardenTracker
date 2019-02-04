package com.gardentracker;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gardentracker.classes.DayWeather;
import com.gardentracker.classes.Shared;
import com.gardentracker.classes.Weather;
import com.gardentracker.adapters.WeatherAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class WeatherActivity extends AppCompatActivity {

    Shared shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setUI();
    }

    private void setUI() {
        shared = new Shared();
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute("košice");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(WeatherActivity.this,SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    public class WeatherTask extends AsyncTask<String, Void, JSONObject> {
        private final String APPID_KEY = "89d15296f24595504c7abd4eae948aa7";
        private final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=%s&mode=json&units=metric&cnt=17&appid=%s";
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            this.progress = new ProgressDialog(WeatherActivity.this);
            this.progress.setMessage(getResources().getString(R.string.searching));
            this.progress.show();
        }

        @Override
        protected JSONObject doInBackground(String... cities) {
            for (String city : cities) {
                try {
                    URL url = new URL(String.format(WEATHER_URL, city, APPID_KEY));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    if (connection.getResponseCode() == 404) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.city_not_found), Toast.LENGTH_SHORT).show();
                        return null;
                    } else if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                        return null;
                    }
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null)
                        stringBuilder.append(line + '\n');
                    connection.disconnect();
                    return new JSONObject(stringBuilder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            this.progress.dismiss();
            if (json != null)
                try {
                    Weather weather = new Weather();
                    JSONObject city = json.getJSONObject("city");
                    weather.setCityName(city.getString("name"));
                    weather.setCountry(city.getString("country"));
                    JSONObject coord = city.getJSONObject("coord");
                    weather.setCityCoordLon(coord.getDouble("lon"));
                    weather.setCityCoordLat(coord.getDouble("lat"));

                    JSONArray list = json.getJSONArray("list");
                    DayWeather firstDayWeather = new DayWeather();
                    ArrayList<DayWeather> dayWeathers = new ArrayList<>();
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject item = list.getJSONObject(i);
                        DayWeather dayWeather = new DayWeather();
                        dayWeather.setTime(item.getLong("dt"));

                        JSONObject weatherItem = item.getJSONArray("weather").getJSONObject(0);
                        dayWeather.setWeather(weatherItem.getString("main"));
                        dayWeather.setWeatherDescription(weatherItem.getString("description"));
                        dayWeather.setIcon(weatherItem.getString("icon"));

                        JSONObject temp = item.getJSONObject("temp");
                        dayWeather.setMaxTemperature(temp.getDouble("max"));
                        dayWeather.setMinTemperature(temp.getDouble("min"));
                        dayWeather.setDayTemperature(temp.getDouble("day"));
                        dayWeather.setNightTemperature(temp.getDouble("night"));
                        dayWeather.setMorningTemperature(temp.getDouble("morn"));
                        dayWeather.setEveningTemperature(temp.getDouble("eve"));

                        dayWeather.setPressure(item.getDouble("pressure"));
                        dayWeather.setHumidityInPercent(item.getInt("humidity"));
                        dayWeather.setWindSpeed(item.getDouble("speed"));
                        dayWeather.setWindDirectionInDegrees(item.getInt("deg"));
                        dayWeather.setCloudsCoverageInPercent(item.getInt("clouds"));

                        if (i != 0)
                            dayWeathers.add(dayWeather);
                        else
                            firstDayWeather = dayWeather;
                    }
                    weather.setDayWeathers(dayWeathers);

                    ((TextView) findViewById(R.id.textCity)).setText(String.format(getResources().getString(R.string.city_country), weather.getCityName(), weather.getCountry()));
                    ((ImageView) findViewById(R.id.imageViewWeatherIcon)).setImageResource(
                            getResources().getIdentifier("b_icon_" + firstDayWeather.getIcon(), "drawable", getPackageName()));
                    ((TextView) findViewById(R.id.textWeather)).setText(firstDayWeather.getWeather());
                    ((TextView) findViewById(R.id.textTemperature)).setText(
                            String.format(getResources().getString(R.string.temperature_in_celsius), (int) firstDayWeather.getMaxTemperature(), (int) firstDayWeather.getMinTemperature()));
                    ((TextView) findViewById(R.id.textUpdated)).setText(String.format(getResources().getString(R.string.updated_date),
                            shared.longToStringDateLong(System.currentTimeMillis() / 1000)));
                    ((ListView) findViewById(R.id.listView)).setAdapter(new WeatherAdapter(WeatherActivity.this, dayWeathers));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }
}
