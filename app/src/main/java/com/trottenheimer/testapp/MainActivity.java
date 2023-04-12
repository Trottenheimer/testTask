package com.trottenheimer.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private RequestQueue mQueue;
    private LinearLayout revLayout;
    private EditText searchField, dateField;
    public String url = "https://api.nytimes.com/svc/movies/v2/reviews/search.json?";//Адрес начальной страницы
    private String apiKey = "&api-key=fwNafshNktDS9CHtjvqDKXpL3UGjcTsd";
    private GestureDetectorCompat gd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        revLayout = findViewById(R.id.critLayout);
        searchField = findViewById(R.id.searchField);
        dateField = findViewById(R.id.dateField);
        mQueue = Volley.newRequestQueue(this);

        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    String searchText = "query=" + searchField.getText().toString().replaceAll(" ","%20");
                    jsonParse(url, searchText, "", apiKey);
                    return true;
                }
                return false;
            }
        });
        dateField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    String searchText = "query=" + searchField.getText().toString();
                    String dateText =dateField.getText().toString();
                    dateText = dateText.replaceAll("-",":");
                    dateText = dateText.replaceAll("\\.","-");
                    dateText = "&opening-date=" + dateText;
                    jsonParse(url, searchText, dateText, apiKey);
                    return true;
                }
                return false;
            }
        });
        jsonParse(url, "", "", apiKey);
    }
    //-----------------------------Функция перехода на другую страницу
    public void goToCriticsActivity(View v) {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }
    //-----------------------------Функция для создания блоков ревью
    public void createReviewObject(String title, String summary, String by, String date, String imageURL) {
        final View revBlock = getLayoutInflater().inflate(R.layout.row_add_rev, null, false);
        TextView titleAdd = (TextView) revBlock.findViewById(R.id.titleText);
        titleAdd.setText(title);
        TextView summaryAdd = (TextView) revBlock.findViewById(R.id.summaryText);
        summaryAdd.setText(summary);
        TextView byAdd = (TextView) revBlock.findViewById(R.id.byText);
        byAdd.setText(by);
        TextView dateAdd = (TextView) revBlock.findViewById(R.id.dateText);
        dateAdd.setText(date);
        ImageView imageAdd = (ImageView) revBlock.findViewById(R.id.imageField);
        loadImageFromURL(imageURL, imageAdd);
        revLayout.addView(revBlock);
    }
    //Функция загрузки изображений с интернета
    public void loadImageFromURL(String url, ImageView imageAdd) {
        Picasso.with(this).load(url).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(imageAdd, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
            }
        });
    }
    //Функция получения и парсинга джсона через апи NYT
    private void jsonParse(String url, String searchText, String searchDate, String apiKey) {
        url = url + searchText + searchDate + apiKey;
        System.out.println(url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                revLayout.removeAllViewsInLayout();
                try {
                    JSONArray jsonArray = response.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject results = jsonArray.getJSONObject(i);

                        String title = results.getString("display_title");
                        String summary = results.getString("summary_short");
                        String byline = results.getString("byline");
                        String date = results.getString("publication_date");
                        JSONObject jO = results.getJSONObject("multimedia");
                        String imageURL = jO.getString("src");

                        createReviewObject(title, summary, byline, date, imageURL);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

}