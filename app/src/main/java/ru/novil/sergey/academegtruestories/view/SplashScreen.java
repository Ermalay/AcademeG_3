package ru.novil.sergey.academegtruestories.view;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import ru.novil.sergey.navigationdraweractivity.R;
import ru.novil.sergey.academegtruestories.other.MyApplication;
import ru.novil.sergey.academegtruestories.sqlite.DatabaseHelper;

public class SplashScreen extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    SQLiteDatabase mSqLiteDatabase;
    Cursor cursor;

    boolean bAca = true;
    boolean bAca2nd;

    int iTotalResults = 1;
    int iCount, iCountSQLite;

//    String[] saUrls = {"UU0lT9K8Wfuc1KPqm6YjRf1A","UUL1C1f9HWf3Hyct4aqBJi1A", "UUEVNTzTFSGkZGTjVE9ipXpg", "UUM0RSbJnk0nAUvfH4Pp7mjQ"};
    String[] saUrls = {"UU0lT9K8Wfuc1KPqm6YjRf1A","UUL1C1f9HWf3Hyct4aqBJi1A", "UUEVNTzTFSGkZGTjVE9ipXpg"};

    String title, description, url, videoId, publishedAt, publishedAtFormat, nextPageToken, prevPageToken, channelTitle;
    String pageToken = "";

    ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final ProgressBar progressBarSpSc = (ProgressBar) findViewById(R.id.progressBarSpSc);
        final int color = getResources().getColor(R.color.colorOrange);
        progressBarSpSc.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);


        checkJsonCount();
        checkSQLiteCount();
    }

    private void checkJsonCount(){
        MyApplication myApplication = (MyApplication) getApplication();
        myApplication.setJsonCountAsZero();
        for (int i = 0; i < saUrls.length; i++){
            new ParseTask1().execute(saUrls[i]);
        }
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

    private void myToast(){
        MyApplication myApplication = (MyApplication) getApplication();
//        Toast.makeText(getApplication(), "Количество записей в JSON = " + myApplication.getJsonCount(), Toast.LENGTH_SHORT).show();
        if ((myApplication.getJsonCount() + 1) > myApplication.getSQLiteCount()){
            mainProcessing();
        } else {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
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

            for (int i = 0; i < saUrls.length; i++){
                final MyApplication myApplication = (MyApplication) getApplication();
                myApplication.setPageTokenAca("");
                for (int j = 0; j < 10; j++){
                    fillSQLiteFromJSON(saUrls[i]);
                }
            }

            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
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
            String maxResultsKey = "3";
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
            String query = "SELECT * FROM " + DatabaseHelper.DATABASE_TABLE_ACAGEMEG;
            cursor = mSqLiteDatabase.rawQuery(query, null);

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);//берём каждый пункт из массива items
                JSONObject snippet = item.getJSONObject("snippet");//из пункта берём объект по ключу "snippet"
                title = snippet.getString("title");//из объекта snippet берём строку по ключу title
                description = snippet.getString("description");
                publishedAt = snippet.getString("publishedAt");
//                publishedAtFormat = myDateFormat(publishedAt);
                channelTitle = snippet.getString("channelTitle");
                JSONObject thumbnails = snippet.getJSONObject("thumbnails");
//                JSONObject medium = thumbnails.getJSONObject("medium");
                JSONObject medium = thumbnails.getJSONObject("high");
                url = medium.getString("url");
                JSONObject resourceId = snippet.getJSONObject("resourceId");
                videoId = resourceId.getString("videoId");

                //Если такого videoId ещё нет в Базе или База пустая
                if (cursor.getCount() > 0){
                    for (int j = 0; j < cursor.getCount(); j++){        // Перебираем cursor
                        cursor.moveToPosition(i);                       // cursor на позицию i
                        String c = cursor.getString(cursor.getColumnIndex(DatabaseHelper.VIDEO_ID_COLUMN));
                        if (c.equals(videoId)){
                        } else {
                            contentValues.put(DatabaseHelper.TITLE_COLUMN, title);
                            contentValues.put(DatabaseHelper.URL_COLUMN, url);
                            contentValues.put(DatabaseHelper.DESCRIPTION_COLUMN, description);
                            contentValues.put(DatabaseHelper.VIDEO_ID_COLUMN, videoId);
                            contentValues.put(DatabaseHelper.PUBLISHEDAT_COLUMN, publishedAt);
                            contentValues.put(DatabaseHelper.CHANNEL_TITLE_COLUMN, channelTitle);
                            mSqLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_ACAGEMEG, null, contentValues); //добавляем contentValues в SQLite
                        }
                    }
                }


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

//    private String myDateFormat(String dateFromSQLie){
//        Locale locale = new Locale("ru_RU");
//        String dateStr = "2016-11-24T19:22:14.000Z";
//        String ddd = "24.11.2016";
//        String formattedDate = "";
//
//        SimpleDateFormat sqlDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.000Z");
////        SimpleDateFormat outputDate = new SimpleDateFormat("'Опубликовано в 'EEEE '\n'dd MMMM yyyy'г. в 'HH.mm", locale);
////        SimpleDateFormat outputDate = new SimpleDateFormat("'Опубликовано в 'HH:mm'\n'dd MMMM yyy'г. - 'EEEE", locale);
//        SimpleDateFormat outputDate = new SimpleDateFormat("HH:mm", locale);
//        outputDate.setTimeZone(android.icu.util.TimeZone.getTimeZone("GMT+03"));
//        try {
//            Date res = sqlDate.parse(dateFromSQLie);
//            formattedDate = outputDate.format(res);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return formattedDate;
//    }

//    private class ParseTaskAka extends AsyncTask<Void, Void, String> {
//
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader = null;
//        String resultJson = "";
//
//        @Override
//        protected String doInBackground(Void... params) {
//            // получаем данные с внешнего ресурса
//            try {
//                String firstPartURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet";
//                String maxResults = "&maxResults=";
//                String maxResultsKey = "6";
//                String prePageToken = "&pageToken=";
//                String playlistId = "&playlistId=";
////                String playlistIdKey = "UUM0RSbJnk0nAUvfH4Pp7mjQ";        //мой канал
////                String playlistIdKey = "UUQeaXcwLUDeRoNVThZXLkmw";      //Big Test Drive
//                String playlistIdKey = "UU0lT9K8Wfuc1KPqm6YjRf1A";      //AcademeG
////                String playlistIdKey = "UUL1C1f9HWf3Hyct4aqBJi1A";      //AcademeG2ndCH
//
//                    //AcademeG DailyStream
////                https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=3&playlistId=UUEVNTzTFSGkZGTjVE9ipXpg&key=AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk
//
//
//                String lastPartURL = "&key=";
//                String developerKey = "AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk";
//
//                MyApplication myApplication = ((MyApplication) getApplicationContext());
//
//                URL url = new URL(firstPartURL + maxResults + maxResultsKey + prePageToken + myApplication.getPageTokenAca()
//                        + playlistId + playlistIdKey + lastPartURL + developerKey);
////                    URL url = new URL("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=1&pageToken=&playlistId=UUM0RSbJnk0nAUvfH4Pp7mjQ&key=AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk");
//
//
//
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//
//                resultJson = buffer.toString();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return resultJson;
//        }
//
//        @Override
//        protected void onPostExecute(String strJson) {
//            super.onPostExecute(strJson);
//            JSONObject dataJsonObj = null;
//            try {
//                dataJsonObj = new JSONObject(strJson);
//
//                if (dataJsonObj.has("nextPageToken")) {
//
//                    final MyApplication myApplication = (MyApplication) getApplication();
//                    myApplication.setPageTokenAca(dataJsonObj.getString("nextPageToken"));
//
////                    nextPageToken = dataJsonObj.getString("nextPageToken");
//                } else if (dataJsonObj.has("prevPageToken")) {
//                    prevPageToken = dataJsonObj.getString("prevPageToken");
//                }
//
//                JSONArray items = dataJsonObj.getJSONArray("items");
//
//
//                mDatabaseHelper = new DatabaseHelper(getApplication());
//                mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
//                contentValues = new ContentValues();
//
//                for (int i = 0; i < items.length(); i++) {
//                    JSONObject item = items.getJSONObject(i);//берём каждый пункт из массива items
//                    JSONObject snippet = item.getJSONObject("snippet");//из пункта берём объект по ключу "snippet"
//                    title = snippet.getString("title");//из объекта snippet берём строку по ключу title
//                    description = snippet.getString("description");
//                    publishedAt = snippet.getString("publishedAt");
//                    channelTitle = snippet.getString("channelTitle");
//                    JSONObject thumbnails = snippet.getJSONObject("thumbnails");
//                    JSONObject medium = thumbnails.getJSONObject("medium");
//                    url = medium.getString("url");
//                    JSONObject resourceId = snippet.getJSONObject("resourceId");
//                    videoId = resourceId.getString("videoId");
//
//                    //Если такого videoId ещё нет в Базе или База пустая
//                    if (compareSQLiteAndJSON(videoId)){
//                        fillSQLite();
//                    }
////                    fillSQLite();
//                }
//
////                cursor.close();
//                mSqLiteDatabase.close();
//
//
//            }catch (JSONException e) {e.printStackTrace();}
//
//            new ParseTaskAka2().execute();
//
////            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
////            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
////            startActivity(intent);
////            finish();
//        }
//    }//Конец ParseTask

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

            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
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
//        contentValues.put(DatabaseHelper.PUBLISHEDAT_COLUMN, publishedAtFormat);
        contentValues.put(DatabaseHelper.CHANNEL_TITLE_COLUMN, channelTitle);
        mSqLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_ACAGEMEG, null, contentValues); //добавляем contentValues в SQLite
    }

    public boolean compareSQLiteAndJSON (String videoId){
        returnCursor();
        if (cursor.getCount() > 0){                             // Если в Базе что-то есть
            for (int i = 0; i < cursor.getCount(); i++){        // Перебираем cursor
                cursor.moveToPosition(i);                       // cursor на позицию i
                String c = cursor.getString(cursor.getColumnIndex(DatabaseHelper.VIDEO_ID_COLUMN));
//                cursor.close();
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
        String query = "SELECT * FROM " + DatabaseHelper.DATABASE_TABLE_ACAGEMEG;
        cursor = mSqLiteDatabase.rawQuery(query, null);
//        mSqLiteDatabase.close();
        return cursor;
    }
}
