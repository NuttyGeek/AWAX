package com.example.bhati.routeapplication.helpers;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bhati.routeapplication.Activities.Config;
import com.example.bhati.routeapplication.Interface.OnKeywordsReady;
import com.example.bhati.routeapplication.Pojo.KeywordsPOJO;
import com.google.api.gax.batching.RequestBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;

public class KeywordsHelper {

    Context context;

    public KeywordsHelper(Context context) {
        this.context = context;

    }

    /**
     * this private fxn  makes the rest api call to the keywords endpoint to get the keywords and notify throght the interface method
     * @param sentence sentence from which the keyword is to be extracted
     * @param callback callback to be notified
     */
    private void getKeywords(String sentence, OnKeywordsReady callback) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        MediaType JSON = MediaType.parse("application/json");
        JSONObject bodyParams = new JSONObject();
        try{
            bodyParams.put("sentence", sentence);
        }
        catch(Exception e) {
            Log.v("keywords", e.getMessage());
        }
        RequestBody body = RequestBody.create(bodyParams.toString(), JSON);
        Request request = new Request.Builder()
                .url(Config.KEYWORDS_URL)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String jsonData = response.body().string();
            Log.v("keywords","Response: "+ jsonData);

            Gson gson = new Gson();
            KeywordsPOJO pojo = gson.fromJson(jsonData, KeywordsPOJO.class);
            // succesfully sending keywords back to calling actvity
            callback.onSuccess(pojo.getKeywords());

        }catch (Exception e) {
            // sending error indication back to the calling activity
            callback.onFailure();
            Log.v("keywords", "error getting response: "+e.getMessage()+ e.getCause());
            e.printStackTrace();
        }

    }

    /**
     * public function for getting the keywords asynchronously with the help of another private function
     * @param sentence
     * @param callback
     */
    public void getKeywordsAsync(String sentence, OnKeywordsReady callback){
        // on a new thread call the keywords endpoint
        new Thread(new Runnable() {
            @Override
            public void run() {
                getKeywords(sentence, callback);
            }
        }).start();
    }


    /**
     * just to test if the library is working correctly or not
     * calling a url and getting the html data which is available on it's homepage
     */
    public void testLibrary(){
        OkHttpClient client = new OkHttpClient();
        MediaType type = MediaType.parse("text/html");
        Request  request = new Request.Builder()
                .url("http://info.cern.ch")
                .build();
        try{
            Response res = client.newCall(request).execute();
            try{
                JSONObject obj = new JSONObject(res.body().string());
                Log.v("json", (String) obj.get("keywords"));
            }catch(JSONException e){
                Log.e("error", "cannot parse response into JSON Array");
            }
            Log.v("res", res.body().string());
            Toast.makeText(context, res.body().string(), Toast.LENGTH_SHORT).show();
        }catch(IOException e){
            Log.v("err", e.getMessage()+ "\n" +e.getCause());
        }
    }

}
