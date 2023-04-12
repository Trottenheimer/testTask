package com.trottenheimer.testapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import com.trottenheimer.testapp.ui.main.SecondFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SecondActivity extends AppCompatActivity {
    private RequestQueue mQueue;
    private LinearLayout critLayout;
    private EditText searchField;
    public String url = "https://api.nytimes.com/svc/movies/v2/critics/";//Адрес начальной страницы
    private String apiKey = "&api-key=fwNafshNktDS9CHtjvqDKXpL3UGjcTsd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, SecondFragment.newInstance())
                .commitNow();
        }
        critLayout = findViewById(R.id.critLayout);
        searchField = findViewById(R.id.searchField);
        mQueue = Volley.newRequestQueue(this);

        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    String searchText = searchField.getText().toString().replaceAll(" ", "%20");
                    searchText = searchText + ".json?";
                    jsonParse(url, searchText, apiKey);
                    return true;
                }
                return false;
            }
        });
        jsonParse(url,"all.json?", apiKey);
    }
    public void goToReviewesActivity(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void createCriticView(String title, String summary, String by, String date) {
        final View revBlock = getLayoutInflater().inflate(R.layout.row_add_rev, null, false);
        TextView titleAdd = (TextView) revBlock.findViewById(R.id.titleText);
        titleAdd.setText(title);
        TextView summaryAdd = (TextView) revBlock.findViewById(R.id.summaryText);
        summaryAdd.setText(summary);
        TextView byAdd = (TextView) revBlock.findViewById(R.id.byText);
        byAdd.setText(by);
        TextView dateAdd = (TextView) revBlock.findViewById(R.id.dateText);
        dateAdd.setText(date);
        ImageView imageAdd = (ImageView) revBlock.findViewById(R.id.imageField);//затычка для фото
        imageAdd.setImageResource(R.drawable.baseline_face_retouching_off_24);
        critLayout.addView(revBlock);
    }
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
    private void jsonParse(String url, String searchText, String apiKey) {
        url = url + searchText + apiKey;
        System.out.println(url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                critLayout.removeAllViewsInLayout();
                try {
                    JSONArray jsonArray = response.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject results = jsonArray.getJSONObject(i);

                        String name = results.getString("display_name");
                        String bio = results.getString("bio");
                        String status = results.getString("status");
                        String seo = results.getString("seo_name");

                        createCriticView(name, bio, status, seo);
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