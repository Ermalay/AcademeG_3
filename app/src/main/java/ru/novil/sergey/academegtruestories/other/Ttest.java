package ru.novil.sergey.academegtruestories.other;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import ru.novil.sergey.academegtruestories.sqlite.DatabaseHelper;
import ru.novil.sergey.navigationdraweractivity.R;

public class Ttest extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    SQLiteDatabase mSqLiteDatabase;
    Cursor cursor;

    int setNumberJson;
    int iCount, iCountSQLite;

    boolean bAca = true;
    boolean bAca2nd;

    String[] saUrls = {"UU0lT9K8Wfuc1KPqm6YjRf1A","UUL1C1f9HWf3Hyct4aqBJi1A", "UUEVNTzTFSGkZGTjVE9ipXpg"};

    String title, description, url, videoId, publishedAt, nextPageToken, prevPageToken, channelTitle;

    ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        checkJsonCount();
        checkSQLiteCount();

        MyApplication myApplication = (MyApplication) getApplication();
        if (myApplication.getJsonCount() > myApplication.getSQLiteCount()){
            for (int i = 0; i < saUrls.length; i++){
                new ParseTask2().execute(saUrls[i]);
            }
        } else {
            Intent intent = new Intent(Ttest.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }

        Toast.makeText(this, "SQLite = " + Integer.toString(myApplication.getSQLiteCount()), Toast.LENGTH_SHORT).show();
    }

    private void checkSQLiteCount(){
        mDatabaseHelper = new DatabaseHelper(getBaseContext());
        mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.DATABASE_TABLE_ACAGEMEG;
        cursor = mSqLiteDatabase.rawQuery(query, null);
        MyApplication myApplication = (MyApplication) getApplication();
        myApplication.setSQLiteCount(cursor.getCount());
        cursor.close();
        mSqLiteDatabase.close();
    }

    private void checkJsonCount(){
        MyApplication myApplication = (MyApplication) getApplication();
        myApplication.setJsonCountAsZero();
        for (int i = 0; i < saUrls.length; i++){
            new ParseTask1().execute(saUrls[i]);
        }
    }

    private void myToast(){
        MyApplication myApplication = (MyApplication) getApplication();
        Toast.makeText(this, "JSON = " + Integer.toString(myApplication.getJsonCount()), Toast.LENGTH_SHORT).show();
        iCountSQLite = 0;
        for (int i = 0; i < saUrls.length; i++){
            myApplication.setPageTokenAca("");
            new ParseTask2().execute(saUrls[i]);
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
                myApplication.setJsonCount(Integer.parseInt(pageInfo.getString("totalResults")));
            }catch (JSONException e) {e.printStackTrace();}

            iCount = iCount + 1;
            if (iCount == saUrls.length){
                myToast();
            }

//            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(intent);
//            finish();
        }
    }//Конец ParseTask1

    private class ParseTask2 extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(String... playlistIdKey) {

            String sPlayList = playlistIdKey[0];
            // получаем данные с внешнего ресурса
            try {
                String firstPartURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet";
                String maxResults = "&maxResults=";
                String maxResultsKey = "1";
                String prePageToken = "&pageToken=";
                String playlistId = "&playlistId=";
                String lastPartURL = "&key=";
                String developerKey = "AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk";

                MyApplication myApplication = ((MyApplication) getApplicationContext());

                URL url = new URL(firstPartURL + maxResults + maxResultsKey + prePageToken + myApplication.getPageTokenAca()
                        + playlistId + sPlayList + lastPartURL + developerKey);

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

                if (dataJsonObj.has("nextPageToken")) {

                    final MyApplication myApplication = (MyApplication) getApplication();
                    myApplication.setPageTokenAca(dataJsonObj.getString("nextPageToken"));

                }

                JSONArray items = dataJsonObj.getJSONArray("items");

                mDatabaseHelper = new DatabaseHelper(getApplication());
                mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
                contentValues = new ContentValues();

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);//берём каждый пункт из массива items
                    JSONObject snippet = item.getJSONObject("snippet");//из пункта берём объект по ключу "snippet"
                    title = snippet.getString("title");//из объекта snippet берём строку по ключу title
                    description = snippet.getString("description");
                    publishedAt = snippet.getString("publishedAt");
                    channelTitle = snippet.getString("channelTitle");
                    JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                    JSONObject medium = thumbnails.getJSONObject("medium");
                    url = medium.getString("url");
                    JSONObject resourceId = snippet.getJSONObject("resourceId");
                    videoId = resourceId.getString("videoId");

                    //Если такого videoId ещё нет в Базе или База пустая
                    fillSQLite();
//                    if (compareSQLiteAndJSON(videoId)){
//                        fillSQLite();
//                    }
                }

                cursor.close();
                mSqLiteDatabase.close();


            }catch (JSONException e) {e.printStackTrace();}

            iCountSQLite = iCountSQLite + 1;
            if (iCountSQLite == saUrls.length){
                Intent intent = new Intent(Ttest.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }

//            new ParseTaskAka2().execute();

//            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(intent);
//            finish();
        }
    }//Конец ParseTask2













    // Этот метод вызывается из главного потока GUI.
    private void mainProcessing() {
        // Здесь трудоемкие задачи переносятся в дочерний поток.
        Thread thread = new Thread(null, doBackgroundThreadProcessing,
                "Background");
        thread.start();
    }
    // Объект Runnable, который запускает метод для выполнения задач
// в фоновом режиме.
    private Runnable doBackgroundThreadProcessing = new Runnable() {
        public void run() {
//            backgroundThreadProcessing(saUrls);

//            for (int j = 0; j < 10; j++){
//                for (int i = 0; i < saUrls.length; i++){
//                    fillSQLiteFromJSON(saUrls[i]);
//                }
//            }
            for (int i = 0; i < saUrls.length; i++){
                final MyApplication myApplication = (MyApplication) getApplication();
                myApplication.setPageTokenAca("");
                for (int j = 0; j < 10; j++){
                    fillSQLiteFromJSON(saUrls[i]);
                }
            }




            Intent intent = new Intent(Ttest.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();

        }
    };
    // Метод, который выполняет какие-то действия в фоновом режиме.
    private void backgroundThreadProcessing(String[] saUrls) {
        //[ ... Трудоемкие операции ... ]
    }

    private void fillSQLiteFromJSON(String sUrl){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        // получаем данные с внешнего ресурса
        try {
            String firstPartURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet";
            String maxResults = "&maxResults=";
            String maxResultsKey = "50";
            String prePageToken = "&pageToken=";
            String playlistId = "&playlistId=";
//                String playlistIdKey = "UUM0RSbJnk0nAUvfH4Pp7mjQ";        //мой канал
//                String playlistIdKey = "UUQeaXcwLUDeRoNVThZXLkmw";      //Big Test Drive
//            String playlistIdKey = "UU0lT9K8Wfuc1KPqm6YjRf1A";      //AcademeG
//                String playlistIdKey = "UUL1C1f9HWf3Hyct4aqBJi1A";      //AcademeG2ndCH

            //AcademeG DailyStream
//                https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=3&playlistId=UUEVNTzTFSGkZGTjVE9ipXpg&key=AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk


            String lastPartURL = "&key=";
            String developerKey = "AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk";

            MyApplication myApplication = ((MyApplication) getApplicationContext());
            URL url = new URL(firstPartURL + maxResults + maxResultsKey + prePageToken + myApplication.getPageTokenAca()
                    + playlistId + sUrl + lastPartURL + developerKey);

//            URL url = new URL(firstPartURL + maxResults + maxResultsKey + prePageToken + myApplication.getPageTokenAca()
//                    + playlistId + playlistIdKey + lastPartURL + developerKey);


//                    URL url = new URL("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=1&pageToken=&playlistId=UUM0RSbJnk0nAUvfH4Pp7mjQ&key=AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk");

//            URL url = new URL(sUrl);

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
        //---------------------------------------------------------------------------------

        JSONObject dataJsonObj = null;
//            Toast.makeText(getBaseContext(), "myApplication.getPageTokenAca() - " + myApplication.getPageTokenAca(), Toast.LENGTH_SHORT).show();
        try {
            dataJsonObj = new JSONObject(resultJson);

            if (dataJsonObj.has("nextPageToken")) {
                final MyApplication myApplication = (MyApplication) getApplication();
                myApplication.setPageTokenAca(dataJsonObj.getString("nextPageToken"));
            }

//            if (dataJsonObj.has("nextPageToken")) {
//
//                final MyApplication myApplication = (MyApplication) getApplication();
//                myApplication.setPageTokenAca(dataJsonObj.getString("nextPageToken"));
//
////                    nextPageToken = dataJsonObj.getString("nextPageToken");
//            } else if (dataJsonObj.has("prevPageToken")) {
//                prevPageToken = dataJsonObj.getString("prevPageToken");
//            }



//            JSONArray pageInfo = dataJsonObj.getJSONArray("pageInfo");
//            iTotalResults = pageInfo.getInt(Integer.parseInt("totalResults"));




            JSONArray items = dataJsonObj.getJSONArray("items");

            mDatabaseHelper = new DatabaseHelper(getApplication());
            mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
            contentValues = new ContentValues();

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);//берём каждый пункт из массива items
                JSONObject snippet = item.getJSONObject("snippet");//из пункта берём объект по ключу "snippet"
                title = snippet.getString("title");//из объекта snippet берём строку по ключу title
                description = snippet.getString("description");
                publishedAt = snippet.getString("publishedAt");
                channelTitle = snippet.getString("channelTitle");
                JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                JSONObject medium = thumbnails.getJSONObject("medium");
                url = medium.getString("url");
                JSONObject resourceId = snippet.getJSONObject("resourceId");
                videoId = resourceId.getString("videoId");

                //Если такого videoId ещё нет в Базе или База пустая
                if (compareSQLiteAndJSON(videoId)){
                    fillSQLite();
                }
//                    fillSQLite();
            }

            cursor.close();
            mSqLiteDatabase.close();


        }catch (JSONException e) {e.printStackTrace();}

//        new ParseTaskAka2().execute();

//            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(intent);
//            finish();
    }



    private class ParseTaskAka2 extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                String firstPartURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet";
                String maxResults = "&maxResults=";
                String maxResultsKey = "6";
                String prePageToken = "&pageToken=";
                String playlistId = "&playlistId=";
//                String playlistIdKey = "UUM0RSbJnk0nAUvfH4Pp7mjQ";        //мой канал
//                String playlistIdKey = "UUQeaXcwLUDeRoNVThZXLkmw";      //Big Test Drive
//                String playlistIdKey = "UU0lT9K8Wfuc1KPqm6YjRf1A";      //AcademeG
                String playlistIdKey = "UUL1C1f9HWf3Hyct4aqBJi1A";      //AcademeG2ndCH

                //AcademeG DailyStream
//                https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=3&playlistId=UUEVNTzTFSGkZGTjVE9ipXpg&key=AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk


                String lastPartURL = "&key=";
                String developerKey = "AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk";

                final MyApplication myApplication = (MyApplication) getApplication();
                URL url = new URL(firstPartURL + maxResults + maxResultsKey + prePageToken + myApplication.getPageTokenAca2nd()
                        + playlistId + playlistIdKey + lastPartURL + developerKey);
//                    URL url = new URL("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=1&pageToken=&playlistId=UUM0RSbJnk0nAUvfH4Pp7mjQ&key=AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk");



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

                if (dataJsonObj.has("nextPageToken")) {
                    final MyApplication myApplication = (MyApplication) getApplication();
                    myApplication.setPageTokenAca2nd(dataJsonObj.getString("nextPageToken"));
//                    nextPageToken = dataJsonObj.getString("nextPageToken");
                } else if (dataJsonObj.has("prevPageToken")) {
                    prevPageToken = dataJsonObj.getString("prevPageToken");
                }

                JSONArray items = dataJsonObj.getJSONArray("items");


                mDatabaseHelper = new DatabaseHelper(getApplication());
                mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
                contentValues = new ContentValues();

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);//берём каждый пункт из массива items
                    JSONObject snippet = item.getJSONObject("snippet");//из пункта берём объект по ключу "snippet"
                    title = snippet.getString("title");//из объекта snippet берём строку по ключу title
                    description = snippet.getString("description");
                    publishedAt = snippet.getString("publishedAt");
                    channelTitle = snippet.getString("channelTitle");
                    JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                    JSONObject medium = thumbnails.getJSONObject("medium");
                    url = medium.getString("url");
                    JSONObject resourceId = snippet.getJSONObject("resourceId");
                    videoId = resourceId.getString("videoId");

//                    //Если такого videoId ещё нет в Базе или База пустая
//                    if (compareSQLiteAndJSON(videoId)){
//                        fillSQLite();
//                    }
                    fillSQLite();
                }

                cursor.close();
                mSqLiteDatabase.close();


            }catch (JSONException e) {e.printStackTrace();}

            Intent intent = new Intent(Ttest.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }//Конец ParseTask2

    public void fillSQLite (){
        contentValues.put(DatabaseHelper.TITLE_COLUMN, title);
        contentValues.put(DatabaseHelper.URL_COLUMN, url);
        contentValues.put(DatabaseHelper.DESCRIPTION_COLUMN, description);
        contentValues.put(DatabaseHelper.VIDEO_ID_COLUMN, videoId);
        contentValues.put(DatabaseHelper.PUBLISHEDAT_COLUMN, publishedAt);
        contentValues.put(DatabaseHelper.CHANNEL_TITLE_COLUMN, channelTitle);
        mSqLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_ACAGEMEG, null, contentValues); //добавляем contentValues в SQLite
    }

    public boolean compareSQLiteAndJSON (String videoId){
        returnCursor();
        if (cursor.getCount() > 0){                             // Если в Базе что-то есть
            for (int i = 0; i < cursor.getCount(); i++){        // Перебираем cursor
                cursor.moveToPosition(i);                       // cursor на позицию i
                String c = cursor.getString(cursor.getColumnIndex(DatabaseHelper.VIDEO_ID_COLUMN));
                if (c.equals(videoId)){                     // если cursor на позиции i равен строке videoId
                    return false;                           // возвращаем false
                }
            }  return true;                                     //если нет совпадений в Базе - возвращаем true
        } else {
            return true;                                        //Если в Базе ничего нет - возвращаем true
        }

    }

    public Cursor returnCursor (){
        mDatabaseHelper = new DatabaseHelper(getBaseContext());
        mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();
        String query = "select * from " + DatabaseHelper.DATABASE_TABLE_ACAGEMEG;
        cursor = mSqLiteDatabase.rawQuery(query, null);
        return cursor;
    }

    public Cursor returnCursorAca (){
        mDatabaseHelper = new DatabaseHelper(getBaseContext());
        mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();
        String query = "SELECT * FROM "
                + DatabaseHelper.DATABASE_TABLE_ACAGEMEG
                + " WHERE "
                + DatabaseHelper.CHANNEL_TITLE_COLUMN
                + "='AcademeG'";
        cursor = mSqLiteDatabase.rawQuery(query, null);
        return cursor;
    }
}
