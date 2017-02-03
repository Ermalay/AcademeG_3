package ru.novil.sergey.academegtruestories.view;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.novil.sergey.academegtruestories.MainActivity;
import ru.novil.sergey.academegtruestories.sqlite.DatabaseHelper;
import ru.novil.sergey.navigationdraweractivity.R;

public class SplashScreenNN extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    SQLiteDatabase mSqLiteDatabase;
    Cursor cursor;

    JSONObject myObject;

    int iCount = 0;
    String sqliteVideoId;
    String[] saUrls = {"UU0lT9K8Wfuc1KPqm6YjRf1A","UUL1C1f9HWf3Hyct4aqBJi1A", "UUEVNTzTFSGkZGTjVE9ipXpg"};
//    String[] saUrls = {"UUM0RSbJnk0nAUvfH4Pp7mjQ"};

    ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_n);

        final ProgressBar progressBarSpScN = (ProgressBar) findViewById(R.id.progressBarSpScN);
        final int color = getResources().getColor(R.color.colorOrange);
        progressBarSpScN.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        fillSqlite();
//        checkJsonCount();
    }

    private void fillSqlite(){
        //В AsynkTask скармливаем по одному каналу
        for (int i = 0; i < saUrls.length; i++){
            new ParseTask2().execute(saUrls[i]);
            }

        }

    private class ParseTask2 extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(String... str) {
            // получаем данные с внешнего ресурса
            String str2 = str[0];
            try {
                String firstPartURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&pageToken=";
                String playlistId = "&playlistId=";
                String thirdPartUrl = "&key=AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk";
                URL url = new URL(firstPartURL + playlistId + str2 + thirdPartUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                resultJson = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            JSONObject dataJsonObj;
            String jsonChannelTitle_01, jsonChannelTitle_02, jsonVideoId, jsonDescription, jsonPublishedAt, jsonUrl, jsonTitle;
            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray items = dataJsonObj.getJSONArray("items");

                JSONObject item_01 = items.getJSONObject(0);
                JSONObject snippet_01 = item_01.getJSONObject("snippet");
                jsonChannelTitle_01 = snippet_01.getString("channelTitle");

                contentValues = new ContentValues();
                mDatabaseHelper = new DatabaseHelper(getBaseContext());
                mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
                String query = "SELECT * FROM "
                        + DatabaseHelper.DATABASE_TABLE_ACAGEMEG
                        + " WHERE "
                        + DatabaseHelper.CHANNEL_TITLE_COLUMN
                        + "='"
                        + jsonChannelTitle_01
                        + "'"
                        + " ORDER BY "
                        + DatabaseHelper.PUBLISHEDAT_COLUMN
                        + " DESC";
                cursor = mSqLiteDatabase.rawQuery(query, null);
                if (cursor.moveToFirst()){
                    sqliteVideoId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.VIDEO_ID_COLUMN));
                    cursor.close();
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item_02 = items.getJSONObject(i);//берём каждый пункт из массива items
                        JSONObject snippet_02 = item_02.getJSONObject("snippet");//из пункта берём объект по ключу "snippet"
                        jsonTitle = snippet_02.getString("title");//из объекта snippet берём строку по ключу title
                        jsonDescription = snippet_02.getString("description");
                        jsonPublishedAt = snippet_02.getString("publishedAt");
                        jsonChannelTitle_02 = snippet_02.getString("channelTitle");
                        JSONObject thumbnails = snippet_02.getJSONObject("thumbnails");
                        JSONObject medium = thumbnails.getJSONObject("high");
                        jsonUrl = medium.getString("url");
                        JSONObject resourceId = snippet_02.getJSONObject("resourceId");
                        jsonVideoId = resourceId.getString("videoId");

                        if (sqliteVideoId.equals(jsonVideoId)){
                            break;
                        }else {
                            contentValues.put(DatabaseHelper.TITLE_COLUMN, jsonTitle);
                            contentValues.put(DatabaseHelper.URL_COLUMN, jsonUrl);
                            contentValues.put(DatabaseHelper.DESCRIPTION_COLUMN, jsonDescription);
                            contentValues.put(DatabaseHelper.VIDEO_ID_COLUMN, jsonVideoId);
                            contentValues.put(DatabaseHelper.PUBLISHEDAT_COLUMN, jsonPublishedAt);
                            contentValues.put(DatabaseHelper.CHANNEL_TITLE_COLUMN, jsonChannelTitle_02);
                            mSqLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_ACAGEMEG, null, contentValues);
                        }
                        break;
                    }
                } else {
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item_02 = items.getJSONObject(i);//берём каждый пункт из массива items
                        JSONObject snippet_02 = item_02.getJSONObject("snippet");//из пункта берём объект по ключу "snippet"
                        jsonTitle = snippet_02.getString("title");//из объекта snippet берём строку по ключу title
                        jsonDescription = snippet_02.getString("description");
                        jsonPublishedAt = snippet_02.getString("publishedAt");
                        jsonChannelTitle_02 = snippet_02.getString("channelTitle");
                        JSONObject thumbnails = snippet_02.getJSONObject("thumbnails");
                        JSONObject medium = thumbnails.getJSONObject("high");
                        jsonUrl = medium.getString("url");
                        JSONObject resourceId = snippet_02.getJSONObject("resourceId");
                        jsonVideoId = resourceId.getString("videoId");

                            contentValues.put(DatabaseHelper.TITLE_COLUMN, jsonTitle);
                            contentValues.put(DatabaseHelper.URL_COLUMN, jsonUrl);
                            contentValues.put(DatabaseHelper.DESCRIPTION_COLUMN, jsonDescription);
                            contentValues.put(DatabaseHelper.VIDEO_ID_COLUMN, jsonVideoId);
                            contentValues.put(DatabaseHelper.PUBLISHEDAT_COLUMN, jsonPublishedAt);
                            contentValues.put(DatabaseHelper.CHANNEL_TITLE_COLUMN, jsonChannelTitle_02);
                            mSqLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_ACAGEMEG, null, contentValues);

                    }
                }



                mSqLiteDatabase.close();
                iCount = iCount + 1;
                if (iCount == saUrls.length){
                    Intent intent = new Intent(SplashScreenNN.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }

            }catch (JSONException e) {e.printStackTrace();}

            //
        }
    }//Конец ParseTask2
}
