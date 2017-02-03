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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.novil.sergey.academegtruestories.MainActivity;
import ru.novil.sergey.navigationdraweractivity.R;
import ru.novil.sergey.academegtruestories.other.MyApplication;
import ru.novil.sergey.academegtruestories.sqlite.DatabaseHelper;

public class SplashScreenN extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    SQLiteDatabase mSqLiteDatabase;
    Cursor cursor;

    private static final String TAG = "TAG";

    JSONObject myObject;

    int iCount = 0;

    String[] saUrls = {"UU0lT9K8Wfuc1KPqm6YjRf1A","UUL1C1f9HWf3Hyct4aqBJi1A", "UUEVNTzTFSGkZGTjVE9ipXpg"};
//    String[] saUrls = {"UUM0RSbJnk0nAUvfH4Pp7mjQ"};


//    String title, description, url, videoId, publishedAt, prevPageToken, channelTitle;

    ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_n);

        final ProgressBar progressBarSpScN = (ProgressBar) findViewById(R.id.progressBarSpScN);
        final int color = getResources().getColor(R.color.colorOrange);
        progressBarSpScN.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

//        fillSqlite();
        checkJsonCount();
    }

    /*Проверяем сколько есть записей в JSON файлах всех каналов
    * поочерёдно скармливаем AsynkTask'у ID каждого канала,
    * где запрашиваем по 1 записи*/
    private void checkJsonCount(){
        MyApplication myApplication = (MyApplication) getApplication();
        //Обнуляем int iJsons
        myApplication.setJsonCountAsZero();
        for (int i = 0; i < saUrls.length; i++){
            new ParseTask1().execute(saUrls[i]);
        }
    }

    private class ParseTask1 extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(String... str) {
            // получаем данные с внешнего ресурса
            String str2 = str[0];
            try {
                String firstPartURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=1&pageToken=&playlistId=";
                String thirdPartUrl = "&key=AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk";
                URL url = new URL(firstPartURL + str2 + thirdPartUrl);
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
            JSONObject dataJsonObj = null;
            try {
                dataJsonObj = new JSONObject(strJson);
                JSONObject pageInfo = dataJsonObj.getJSONObject("pageInfo");
                MyApplication myApplication = (MyApplication) getApplication();
                //Плюсуем в MyApplication iJsons количество записей в каждом JSON файле
                myApplication.setJsonCount(Integer.parseInt(pageInfo.getString("totalResults")));
            }catch (JSONException e) {e.printStackTrace();}

            //Считаем количество посчитанных JSON файлов, после окончания, идём дальше
            iCount = iCount + 1;
            if (iCount == saUrls.length){
                myToast();
            }
        }
    }//Конец ParseTask1

    private void myToast(){
        MyApplication myApplication = (MyApplication) getApplication();
        Toast.makeText(getApplication(), "Количество записей в JSON = " + myApplication.getJsonCount(), Toast.LENGTH_SHORT).show();
        //Если записей в JSON больше, чем записей в SQLite, то заполняем SQLite,
        // иначе идём дальше в MainActivity
        if ((myApplication.getJsonCount() + 1) > myApplication.getSQLiteCount()){
            fillSqlite();
        } else {
            Intent intent = new Intent(SplashScreenN.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void fillSqlite(){
        //В AsynkTask скармливаем по одному каналу
        MyApplication myApplication = (MyApplication) getApplication();
        for (int i = 0; i < saUrls.length; i++){
            myApplication.setbPageToken(true);
            for (int j = 0; j < 10; j++){
                new ParseTask2().execute(saUrls[i]);
            }
//            if (myApplication.getbPageToken()){
//                new ParseTask2().execute(saUrls[i]);
////                myApplication.setbPageToken(false);
//            }
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
                MyApplication myApplication = (MyApplication) getApplication();
                String firstPartURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&pageToken=";
                String pageToken = myApplication.getPageToken();
                String playlistId = "&playlistId=";
                String thirdPartUrl = "&key=AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk";
                URL url = new URL(firstPartURL + pageToken + playlistId + str2 + thirdPartUrl);
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
            JSONObject dataJsonObj = null;
            JSONObject dataJsonObj2 = null;
            String jsonChannelTitle, jsonVideoId, sqliteVideoId = null, jsonDescription, jsonPublishedAt, jsonUrl, jsonTitle;
            MyApplication myApplication = (MyApplication) getApplication();
            try {
                myObject = new JSONObject(strJson);

                dataJsonObj = new JSONObject(strJson);
                if (dataJsonObj.has("nextPageToken")) {
                    myApplication.setPageToken(dataJsonObj.getString("nextPageToken"));
                    myApplication.setbPageToken(true);
                } else {
                    myApplication.setbPageToken(false);
                }


                JSONArray items = dataJsonObj.getJSONArray("items");
                JSONObject item = items.getJSONObject(0);
                JSONObject snippet = item.getJSONObject("snippet");
                jsonChannelTitle = snippet.getString("channelTitle");

                //вытаскиваем курсор по названию канала, переданного сюда JSON
                mDatabaseHelper = new DatabaseHelper(getBaseContext());
                mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
                contentValues = new ContentValues();
                String query = "SELECT * FROM "
                        + DatabaseHelper.DATABASE_TABLE_ACAGEMEG
                        + " WHERE "
                        + DatabaseHelper.CHANNEL_TITLE_COLUMN
                        + "='"
                        + jsonChannelTitle
                        + "'"
                        + " ORDER BY "
                        + DatabaseHelper.PUBLISHEDAT_COLUMN
                        + " DESC";
                cursor = mSqLiteDatabase.rawQuery(query, null);
                //пишем в переменную sqliteVideoId значвение самого свежего VideoId из SQLite
                if (cursor.getCount() > 0){
                    cursor.moveToFirst();
                    sqliteVideoId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.VIDEO_ID_COLUMN));
                    //перебираем все записи в items
                    for (int i = 0; i < items.length(); i++){
                        item = items.getJSONObject(i);
                        snippet = item.getJSONObject("snippet");
                        JSONObject resourceId = snippet.getJSONObject("resourceId");
                        jsonVideoId = resourceId.getString("videoId");
                        //если последний VideoId из SQLite и предложенный из JSON совпадают, то пропустить,
                        // если нет, то записать в SQLite
                        if (jsonVideoId.equals(sqliteVideoId)){
                        } else {
                            jsonTitle = snippet.getString("title");
                            jsonDescription = snippet.getString("description");
                            jsonPublishedAt = snippet.getString("publishedAt");
                            JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                            JSONObject medium = thumbnails.getJSONObject("high");
                            jsonUrl = medium.getString("url");
                            resourceId = snippet.getJSONObject("resourceId");
                            jsonVideoId = resourceId.getString("videoId");

                            contentValues.put(DatabaseHelper.TITLE_COLUMN, jsonTitle);
                            contentValues.put(DatabaseHelper.URL_COLUMN, jsonUrl);
                            contentValues.put(DatabaseHelper.DESCRIPTION_COLUMN, jsonDescription);
                            contentValues.put(DatabaseHelper.VIDEO_ID_COLUMN, jsonVideoId);
                            contentValues.put(DatabaseHelper.PUBLISHEDAT_COLUMN, jsonPublishedAt);
                            contentValues.put(DatabaseHelper.CHANNEL_TITLE_COLUMN, jsonChannelTitle);
                            mSqLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_ACAGEMEG, null, contentValues); //добавляем contentValues в SQLite
                        }
                    }
                    Intent intent = new Intent(SplashScreenN.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    dataJsonObj2 = new JSONObject(strJson);
                    JSONArray items2 = dataJsonObj2.getJSONArray("items");
                    for (int i = 0; i < items2.length(); i++) {
                        JSONObject item2 = items2.getJSONObject(i);
                        JSONObject snippet2 = item2.getJSONObject("snippet");
                        String jsonTitle2 = snippet2.getString("title");
                        String jsonDescription2 = snippet2.getString("description");
                        String jsonPublishedAt2 = snippet2.getString("publishedAt");
                        JSONObject thumbnails = snippet2.getJSONObject("thumbnails");
                        JSONObject medium = thumbnails.getJSONObject("high");
                        String jsonUrl2 = medium.getString("url");
                        JSONObject resourceId = snippet2.getJSONObject("resourceId");
                        String jsonVideoId2 = resourceId.getString("videoId");

                        contentValues.put(DatabaseHelper.TITLE_COLUMN, jsonTitle2);
                        contentValues.put(DatabaseHelper.URL_COLUMN, jsonUrl2);
                        contentValues.put(DatabaseHelper.DESCRIPTION_COLUMN, jsonDescription2);
                        contentValues.put(DatabaseHelper.VIDEO_ID_COLUMN, jsonVideoId2);
                        contentValues.put(DatabaseHelper.PUBLISHEDAT_COLUMN, jsonPublishedAt2);
                        contentValues.put(DatabaseHelper.CHANNEL_TITLE_COLUMN, jsonChannelTitle);
                        mSqLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_ACAGEMEG, null, contentValues); //добавляем contentValues в SQLite
                    }
                    Intent intent = new Intent(SplashScreenN.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }


                cursor.close();
                mSqLiteDatabase.close();

            }catch (JSONException e) {e.printStackTrace();}

            //
        }
    }//Конец ParseTask2
}
