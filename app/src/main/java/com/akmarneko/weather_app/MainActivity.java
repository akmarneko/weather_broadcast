package com.akmarneko.weather_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.icu.text.UnicodeSet.CASE;

public class MainActivity extends AppCompatActivity {

    private TextView tvLocation, tvTemperature, tvHumidity, tvWindSpeed, tvCloudiness;
    private Button btnRefresh;
    private ImageView iVIcon;

    private static  final String WEATHER_SOURCE = "http://api.openweathermap.org/data/2.5/weather?APPID=82445b6c96b99bc3ffb78a4c0e17fca5&mode=json&id=1735161";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = findViewById(R.id.location);
        tvTemperature = findViewById(R.id.temperature);
        tvHumidity = findViewById(R.id.humidity);
        tvWindSpeed = findViewById(R.id.wind_speed);
        tvCloudiness = findViewById(R.id.cloudiness);
        btnRefresh = findViewById(R.id.button_refresh);
        iVIcon = findViewById(R.id.icon);

        btnRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new WeatherDataRetrival().execute();
            }
        });
    }

    private class WeatherDataRetrival extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0){
            NetworkInfo networkInfo = ((ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()){
                try {
                    URL url = new URL(WEATHER_SOURCE);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(15000);
                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK){
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        if(bufferedReader != null){
                            String readline;
                            StringBuffer strBuffer = new StringBuffer();
                            while ((readline = bufferedReader.readLine()) != null){
                                strBuffer.append(readline);
                            }
                            return strBuffer.toString();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                //
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            if(result != null) try {
                final JSONObject weatherJSON = new JSONObject(result);
                tvLocation.setText(weatherJSON.getString("name") + "," + weatherJSON.getJSONObject("sys").getString("country"));
                tvWindSpeed.setText(String.valueOf(weatherJSON.getJSONObject("wind").getDouble("speed")) + "mps");
                tvCloudiness.setText(String.valueOf(weatherJSON.getJSONObject("clouds").getInt("all")) + "%");

                final JSONObject mainJSON = weatherJSON.getJSONObject("main");
                tvTemperature.setText(String.valueOf(mainJSON.getDouble("temp")-273.15));
                tvHumidity.setText(String.valueOf(mainJSON.getInt("humidity")) + "%");

                final JSONArray weatherJSONArray = weatherJSON.getJSONArray("weather");
                if (weatherJSONArray.length() > 0) {
                    int code = weatherJSONArray.getJSONObject(0).getInt("id");
                    iVIcon.setImageResource(getIcon(code));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private int getIcon(int code) {
        switch (code)
        {
            //ThunderStorm
            case 200:
            case 201:
            case 202:
            case 210:
            case 211:
            case 212:
            case 221:
            case 230:
            case 231:
            case 232:
                return (R.drawable.ic_thunderstorm_large);

            //Drizzle
            case 300:
            case 301:
            case 302:
            case 310:
            case 311:
            case 312:
            case 313:
            case 314:
            case 321:
                return (R.drawable.ic_drizzle_large);

            //Rain
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
            case 511:
            case 520:
            case 521:
            case 522:
            case 531:
                return (R.drawable.ic_rain_large);

            //Snow
            case 600:
            case 601:
            case 602:
            case 611:
            case 612:
            case 615:
            case 616:
            case 620:
            case 621:
            case 622:
                return (R.drawable.ic_snow_large);

            //Clear Sky
            case 800:
                return (R.drawable.ic_day_clear_large);

            //Few Clouds
            case 801:
                return (R.drawable.ic_day_few_clouds_large);

            //Scatted Clouds
            case 802:
                return (R.drawable.ic_scattered_clouds_large);

            //Broken and OverCast Clouds
            case 803:
            case 804:
                return (R.drawable.ic_broken_clouds_large);

            //Fog
            case 701:
            case 711:
            case 721:
            case 731:
            case 741:
            case 751:
            case 761:
            case 762:
                return (R.drawable.ic_fog_large);

            //Tornado
            case 781:
            case 900:
                return (R.drawable.ic_tornado_large);

            //Windy
            case 905:
                return (R.drawable.ic_windy_large);

            //Hail
            case 906:
                return (R.drawable.ic_hail_large);


            default:
                return (R.drawable.ic_thunderstorm_large);
        }
    }



}
