package ru.novil.sergey.academegtruestories;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.novil.sergey.academegtruestories.other.MyApplication;
import ru.novil.sergey.academegtruestories.sqlite.DatabaseHelper;
import ru.novil.sergey.academegtruestories.view.SlidingTabLayout;
import ru.novil.sergey.navigationdraweractivity.R;

public class FirstFragment extends Fragment {

    int iCheckCountRefresh;

    public String[] itemName, itemImage, itemDescription, itemPublished, itemChannelTitle;
    String title, publishedAt, channelTitle, description, url, videoId, videoIdPre, nextPageToken, prevPageToken, cursorVideoID, itemVideoID;
    String pageToken = "";
//    String[] saUrls = {"UUM0RSbJnk0nAUvfH4Pp7mjQ", "UU0lT9K8Wfuc1KPqm6YjRf1A","UUL1C1f9HWf3Hyct4aqBJi1A", "UUEVNTzTFSGkZGTjVE9ipXpg"};
    String[] saUrls = {"UU0lT9K8Wfuc1KPqm6YjRf1A","UUL1C1f9HWf3Hyct4aqBJi1A", "UUEVNTzTFSGkZGTjVE9ipXpg"};

    boolean loadingMore = true;
    boolean bNextUrl = true;

    AdapterListVideo adapterListVideo, adapterListVideo1;

    DatabaseHelper mDatabaseHelper;
    SQLiteDatabase mSqLiteDatabase;
    Cursor cursor;
    ListView lv1, lv2, lv3, lv4;
    public TextView tv, tv1, tv2, tv22, tv23, tv3, tv33, tv333;
    ContentValues contentValues;

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    SwipeRefreshLayout swipeRefreshLayout_All,
            swipeRefreshLayout_AcademeG,
            swipeRefreshLayout_AcademeG_2nd_CH,
            swipeRefreshLayout_AcademeG_DailyStream;

    public FirstFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_first, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());

        // Give the SlidingTabLayout the ViewPager, this must be
        // done AFTER the ViewPager has had it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    // Adapter
    class SamplePagerAdapter extends PagerAdapter {
        // implements SwipeRefreshLayout.OnRefreshListener
        /**
         * Вернуть Количество страниц для отображения
         */
        @Override
        public int getCount() {
            return 4;
        }

        /**
         * Возвратите True, если значение, возвращенное из такой же объект, как вид
         * добавлены в ViewPager.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        /**
         * Return the title of the item at position. This is important as what
         * this method returns is what is displayed in the SlidingTabLayout.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0){
                return "Лента";
            } else if (position == 1) {
                return "AcademeG";
            } else if (position == 2) {
                return "AcademeG 2hd";
            } else if (position == 3) {
                return "DailyStream";
            } else {
                return "Вкладка " + (position + 1);
            }
        }

        /**
         * Создать представление, которое будет отображаться в позиции. Здесь мы
         * надуть макет из ресурсов приложения, а затем измените текст
         * целью обозначить позицию.
         */
        @Override
        public Object instantiateItem (ViewGroup container, int position) {
            if (position == 0) {
                // Надуть новый макет из наших ресурсов
                View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item_1,
                        container, false);
                lv1 = (ListView) view.findViewById(R.id.lv1);

                //Вывод ListView
                fillListView("video_academeg", lv1);

//                Parcelable state = lv1.onSaveInstanceState();
//                lv1.setAdapter(adapterListVideo_02);
//                lv1.onRestoreInstanceState(state);

                swipeRefreshLayout_All = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_1);
                swipeRefreshLayout_All.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        iCheckCountRefresh = 0;
                        for (int i = 0; i < saUrls.length; i++){
                            new ParseTaskLenta().execute(saUrls[i]);
                        }
                    }
                });
                swipeRefreshLayout_All.setColorSchemeResources(
                        android.R.color.holo_blue_bright,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light);

//                View footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layot, null, false);
//                lv1.addFooterView(footerView);

                lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        returnCursor();
                        cursor.moveToPosition(position);
                        String vId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.VIDEO_ID_COLUMN));
                        cursor.close();

                        Intent intent = new Intent(getActivity(), YTActivity.class);
                        intent.putExtra("pushkin", vId);
                        startActivity(intent);
//                        Toast.makeText(getActivity(), "onItemClick - " + position, Toast.LENGTH_SHORT).show();
                    }
                });

                container.addView(view);
                return view;

                //Вторая вкладка
            } if (position == 1){
                View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item_2,
                        container, false);

                lv2 = (ListView) view.findViewById(R.id.lv2);

                swipeRefreshLayout_AcademeG = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_2);
                swipeRefreshLayout_AcademeG.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new ParseTaskAcademeG().execute();
                    }
                });
                swipeRefreshLayout_AcademeG.setColorSchemeResources(
                        android.R.color.holo_blue_bright,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light);

                fillListView("video_academeg WHERE channelTitle='AcademeG'", lv2);
//                fillListView("video_academeg WHERE channelTitle='ErmalayKa'", lv2);


                lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        returnCursorForClick("AcademeG");
                        cursor.moveToPosition(position);
                        String vId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.VIDEO_ID_COLUMN));
                        cursor.close();

                        Intent intent = new Intent(getActivity(), YTActivity.class);
                        intent.putExtra("pushkin", vId);
                        startActivity(intent);
                    }
                });

                container.addView(view);
                return view;


            } if (position == 2){
                View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item_3,
                        container, false);

                lv3 = (ListView) view.findViewById(R.id.lv3);

                fillListView("video_academeg WHERE channelTitle='AcademeG 2nd CH'", lv3);
                swipeRefreshLayout_AcademeG_2nd_CH = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_3);
                swipeRefreshLayout_AcademeG_2nd_CH.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new ParseTaskAcademeG_2nd_CH().execute();
                    }
                });
                swipeRefreshLayout_AcademeG_2nd_CH.setColorSchemeResources(
                        android.R.color.holo_blue_bright,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light);

                lv3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                        returnCursorForClick("AcademeG 2nd CH");
                        cursor.moveToPosition(position);
                        String vId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.VIDEO_ID_COLUMN));
                        cursor.close();

                        Intent intent = new Intent(getActivity(), YTActivity.class);
                        intent.putExtra("pushkin", vId);
                        startActivity(intent);
                    }
                });

                container.addView(view);
                return view;
            }
            if (position == 3){
                View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item_4,
                        container, false);

//                Toast.makeText(getActivity(), "Вкладка №4", Toast.LENGTH_SHORT).show();

                lv4 = (ListView) view.findViewById(R.id.lv4);

                fillListView("video_academeg WHERE channelTitle='AcademeG DailyStream'", lv4);

                swipeRefreshLayout_AcademeG_DailyStream = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_4);
                swipeRefreshLayout_AcademeG_DailyStream.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new ParseTaskAcademeG_DailyStream().execute();
                    }
                });
                swipeRefreshLayout_AcademeG_DailyStream.setColorSchemeResources(
                        android.R.color.holo_blue_bright,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light);

                lv4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        returnCursorForClick("AcademeG DailyStream");
                        cursor.moveToPosition(position);
                        String vId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.VIDEO_ID_COLUMN));
                        cursor.close();

                        Intent intent = new Intent(getActivity(), YTActivity.class);
                        intent.putExtra("pushkin", vId);
                        startActivity(intent);
                    }
                });

                container.addView(view);
                return view;
            }
            //Другие вкладки справа
            else {
                // Надуть новый макет с наших ресурсов
                View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item,
                        container, false);
                // Добавить созданный вид на ViewPager
                container.addView(view);

                // Retrieve a TextView from the inflated View, and update it's text
                TextView title = (TextView) view.findViewById(R.id.item_title);
                title.setText(String.valueOf(position + 1));

                // Return the View
                return view;
            }
        }

        /**
         * Destroy the item from the ViewPager. In our case this is simply
         * removing the View.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * Начало ParseTaskLenta
     */
    private class ParseTaskLenta extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(String... params) {
            String str2 = params[0];
            // получаем данные с внешнего ресурса
            try {
                String firstPartURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&pageToken=&playlistId=";
                String thirdPartUrl = "&key=AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk";
                URL url = new URL(firstPartURL + str2 + thirdPartUrl);
//https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=3&pageToken=&playlistId=UUM0RSbJnk0nAUvfH4Pp7mjQ&key=AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk
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

                JSONArray items = dataJsonObj.getJSONArray("items");
                mDatabaseHelper = new DatabaseHelper(getActivity());
                mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
                contentValues = new ContentValues();

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
                    if (compareSQLiteAndJSON(videoId)){
                        fillSQLiteAll();
                    }
                }

                mSqLiteDatabase.close();

//                    Parcelable state = lv1.onSaveInstanceState();
//                    lv1.setAdapter(adapterListVideo);
//                    lv1.onRestoreInstanceState(state);
//                    bNextUrl = true;

            }catch (JSONException e) {e.printStackTrace();}

            iCheckCountRefresh = iCheckCountRefresh + 1;
            if (iCheckCountRefresh >= saUrls.length){
                swipeRefreshLayout_All.setRefreshing(false);//указываем об окончании обновления страницы
                fillListView("video_academeg", lv1);
                fillListView("video_academeg WHERE channelTitle='ErmalayKa'", lv2);
//                Toast.makeText(getActivity().getApplication(), "Обновление завершено", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Конец ParseTaskLenta
     */
    /**
     * Начало ParseTaskAcademeG
     */
    private class ParseTaskAcademeG extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&pageToken=&playlistId=UU0lT9K8Wfuc1KPqm6YjRf1A&key=AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk");
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

                JSONArray items = dataJsonObj.getJSONArray("items");
                mDatabaseHelper = new DatabaseHelper(getActivity());
                mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
                contentValues = new ContentValues();

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
                    if (compareSQLiteAndJSON(videoId)){
                        fillSQLiteAll();
                    }
                }

                mSqLiteDatabase.close();

//                    Parcelable state = lv1.onSaveInstanceState();
//                    lv1.setAdapter(adapterListVideo);
//                    lv1.onRestoreInstanceState(state);
//                    bNextUrl = true;

            }catch (JSONException e) {e.printStackTrace();}


            swipeRefreshLayout_AcademeG.setRefreshing(false);//указываем об окончании обновления страницы
            fillListView("video_academeg", lv1);
            fillListView("video_academeg WHERE channelTitle='AcademeG'", lv2);
            fillListView("video_academeg WHERE channelTitle='AcademeG 2nd CH'", lv3);

//            Toast.makeText(getActivity().getApplication(), "Обновление lv2 завершено", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Конец ParseTaskAcademeG
     */
    /**
     * Начало ParseTaskAcademeG_2nd_CH
     */
    private class ParseTaskAcademeG_2nd_CH extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&pageToken=CAMQAA&playlistId=UUL1C1f9HWf3Hyct4aqBJi1A&key=AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk");
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

                JSONArray items = dataJsonObj.getJSONArray("items");
                mDatabaseHelper = new DatabaseHelper(getActivity());
                mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
                contentValues = new ContentValues();

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
                    if (compareSQLiteAndJSON(videoId)){
                        fillSQLiteAll();
                    }
                }

                mSqLiteDatabase.close();

//                    Parcelable state = lv1.onSaveInstanceState();
//                    lv1.setAdapter(adapterListVideo);
//                    lv1.onRestoreInstanceState(state);
//                    bNextUrl = true;

            }catch (JSONException e) {e.printStackTrace();}


            swipeRefreshLayout_AcademeG_2nd_CH.setRefreshing(false);//указываем об окончании обновления страницы
            fillListView("video_academeg WHERE channelTitle='ErmalayKa'", lv2);
            fillListView("video_academeg WHERE channelTitle='AcademeG 2nd CH'", lv3);
            fillListView("video_academeg WHERE channelTitle='AcademeG DailyStream'", lv4);

//            Toast.makeText(getActivity().getApplication(), "Обновление lv3 завершено", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Конец ParseTaskAcademeG_2nd_CH
     */
    /**
     * Начало ParseTaskAcademeG_2nd_CH
     */
    private class ParseTaskAcademeG_DailyStream extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&pageToken=&playlistId=UUEVNTzTFSGkZGTjVE9ipXpg&key=AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk");
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

                JSONArray items = dataJsonObj.getJSONArray("items");
                mDatabaseHelper = new DatabaseHelper(getActivity());
                mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
                contentValues = new ContentValues();

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
                    if (compareSQLiteAndJSON(videoId)){
                        fillSQLiteAll();
                    }
                }

                mSqLiteDatabase.close();

//                    Parcelable state = lv1.onSaveInstanceState();
//                    lv1.setAdapter(adapterListVideo);
//                    lv1.onRestoreInstanceState(state);
//                    bNextUrl = true;

            }catch (JSONException e) {e.printStackTrace();}


            swipeRefreshLayout_AcademeG_DailyStream.setRefreshing(false);//указываем об окончании обновления страницы
            fillListView("video_academeg WHERE channelTitle='AcademeG DailyStream'", lv4);
            fillListView("video_academeg WHERE channelTitle='AcademeG 2nd CH'", lv3);

//            Toast.makeText(getActivity().getApplication(), "Обновление lv4 завершено", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Конец ParseTaskAcademeG_2nd_CH
     */







    private class ParseTaskAll extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(String... params) {
            String str2 = params[0];
            // получаем данные с внешнего ресурса
            try {
                String firstPartURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&pageToken=&playlistId=";
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


                    JSONArray items = dataJsonObj.getJSONArray("items");
                    mDatabaseHelper = new DatabaseHelper(getActivity());
                    mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
                    contentValues = new ContentValues();

//                    returnCursor();

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
                            fillSQLite(DatabaseHelper.DATABASE_TABLE_ACAGEMEG);
                        }
                    }

//                    cursor.close();
                    mSqLiteDatabase.close();

                    fillArrayItems(returnCursor());           //заполняем массивы для адаптера


//                    Parcelable state = lv1.onSaveInstanceState();
//                    lv1.setAdapter(adapterListVideo);
//                    lv1.onRestoreInstanceState(state);
//                    bNextUrl = true;


            }catch (JSONException e) {e.printStackTrace();}

//            iCheckCountRefresh = iCheckCountRefresh + 1;
//            if (iCheckCountRefresh >= saUrls.length){
//                mSwipeRefreshLayout.setRefreshing(false);//указываем об окончании обновления страницы
//                fillListView("video_academeg", lv1);
////                fillListView("video_academeg WHERE channelTitle='ErmalayKa'", lv2);
////                fillListView("video_academeg WHERE channelTitle='AcademeG 2nd CH'", lv3);
////                fillListView("video_academeg WHERE channelTitle='AcademeG DailyStream'", lv4);
//            }

        }
    }//Конец ParseTaskAll


//    private class ParseTaskAca extends AsyncTask<Void, Void, String> {
//
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//            String resultJson = "";
//
//            @Override
//            protected String doInBackground(Void... params) {
//                // получаем данные с внешнего ресурса
//                try {
//                    String firstPartURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet";
//                    String maxResults = "&maxResults=";
//                    String maxResultsKey = "6";
//                    String prePageToken = "&pageToken=";
//                    String playlistId = "&playlistId=";
////                    String playlistIdKey = "UUM0RSbJnk0nAUvfH4Pp7mjQ";    //мой канал
////                    String playlistIdKey = "UUQeaXcwLUDeRoNVThZXLkmw";      //Big Test Drive
//                    String playlistIdKey = "UU0lT9K8Wfuc1KPqm6YjRf1A";      //AcademeG
//
//                    String lastPartURL = "&key=";
//                    String developerKey = "AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk";
//
//                    URL url = new URL(firstPartURL + maxResults + maxResultsKey + prePageToken + pageToken
//                            + playlistId + playlistIdKey + lastPartURL + developerKey);
////                    URL url = new URL("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=1&pageToken=&playlistId=UUM0RSbJnk0nAUvfH4Pp7mjQ&key=AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk");
//
////                    printURL(url.toString());       //показывает строку url
//
//
//                    urlConnection = (HttpURLConnection) url.openConnection();
//                    urlConnection.setRequestMethod("GET");
//                    urlConnection.connect();
//
//                    InputStream inputStream = urlConnection.getInputStream();
//                    StringBuffer buffer = new StringBuffer();
//
//                    reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        buffer.append(line);
//                    }
//
//                    resultJson = buffer.toString();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return resultJson;
//            }
//
//            @Override
//            protected void onPostExecute(String strJson) {
//                super.onPostExecute(strJson);
//                JSONObject dataJsonObj = null;
//
//                    try {
//                        dataJsonObj = new JSONObject(strJson);
//
//                        if (dataJsonObj.has("nextPageToken")) {
//                            nextPageToken = dataJsonObj.getString("nextPageToken");
//                        } else if (dataJsonObj.has("prevPageToken")) {
//                            prevPageToken = dataJsonObj.getString("prevPageToken");
//                        }
//
//                        JSONArray items = dataJsonObj.getJSONArray("items");
//
//
//                        mDatabaseHelper = new DatabaseHelper(getActivity());
//                        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
//                        contentValues = new ContentValues();
//
//                        returnCursor();
//
//                        for (int i = 0; i < items.length(); i++) {
//                            JSONObject item = items.getJSONObject(i);//берём каждый пункт из массива items
//                            JSONObject snippet = item.getJSONObject("snippet");//из пункта берём объект по ключу "snippet"
//                            title = snippet.getString("title");//из объекта snippet берём строку по ключу title
//                            description = snippet.getString("description");
//                            publishedAt = snippet.getString("publishedAt");
//                            channelTitle = snippet.getString("channelTitle");
//                            JSONObject thumbnails = snippet.getJSONObject("thumbnails");
//                            JSONObject medium = thumbnails.getJSONObject("medium");
//                            url = medium.getString("url");
//                            JSONObject resourceId = snippet.getJSONObject("resourceId");
//                            videoId = resourceId.getString("videoId");
//
//                            //Если такого videoId ещё нет в Базе или База пустая
//                            if (compareSQLiteAndJSON(videoId)){
//                                fillSQLite(DatabaseHelper.DATABASE_TABLE_ACAGEMEG);
//                            }
//                        }
//
//                        cursor.close();
//                        mSqLiteDatabase.close();
//
//                        fillArrayItems(returnCursor());           //заполняем массивы для адаптера
//
//                        Parcelable state = lv2.onSaveInstanceState();
//
////                        fillAdapterListVideo();     //заполняем ListView адаптером
//                        lv2.setAdapter(adapterListVideo);
//
//
//                        lv2.onRestoreInstanceState(state);
//                        mSwipeRefreshLayout.setRefreshing(false);//указываем об окончании обновления страницы
//                        loadingMore = true;             //Вызываем onScroll только один раз
//                        Toast.makeText(getActivity(), "!!! new ParseTask().execute(); !!!", Toast.LENGTH_SHORT).show();
//                    }catch (JSONException e) {e.printStackTrace();}
//            }
//        }//Конец ParseTask
//
//    private class ParseTask2nd extends AsyncTask<Void, Void, String> {
////        myApplication.getPageToken()
//
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader = null;
//        String resultJson = "";
//
//        @Override
//        protected String doInBackground(Void... params) {
//
//            // получаем данные с внешнего ресурса
//            try {
//                String firstPartURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet";
//                String maxResults = "&maxResults=";
//                String maxResultsKey = "6";
//                String prePageToken = "&pageToken=";
//                String playlistId = "&playlistId=";
////                    String playlistIdKey = "UUM0RSbJnk0nAUvfH4Pp7mjQ";    //мой канал
////                String playlistIdKey = "UUQeaXcwLUDeRoNVThZXLkmw";      //Big Test Drive
//                String playlistIdKey = "UUL1C1f9HWf3Hyct4aqBJi1A";      //AcademeG2nd
//
//                String lastPartURL = "&key=";
//                String developerKey = "AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk";
//
//
//                final MyApplication myApplication = (MyApplication) getActivity().getApplication();
//
//                URL url = new URL(firstPartURL + maxResults + maxResultsKey + prePageToken + myApplication.getPageTokenAca2nd() //+ pageToken
//                        + playlistId + playlistIdKey + lastPartURL + developerKey);
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
//
//            try {
//                dataJsonObj = new JSONObject(strJson);
//
//                if (dataJsonObj.has("nextPageToken")) {
//
//                    final MyApplication myApplication = (MyApplication) getActivity().getApplication();
//                    myApplication.setPageTokenAca2nd(dataJsonObj.getString("nextPageToken"));
////                    nextPageToken = dataJsonObj.getString("nextPageToken");
//                } else if (dataJsonObj.has("prevPageToken")) {
//                    prevPageToken = dataJsonObj.getString("prevPageToken");
//                }
//
//                JSONArray items = dataJsonObj.getJSONArray("items");
//
//
//                mDatabaseHelper = new DatabaseHelper(getActivity());
//                mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
//                contentValues = new ContentValues();
//
//                returnCursor();
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
//                        fillSQLite(DatabaseHelper.DATABASE_TABLE_ACAGEMEG);
//                    }
//                }
//
//                cursor.close();
//                mSqLiteDatabase.close();
//
//                fillArrayItems(returnCursor());           //заполняем массивы для адаптера
//
//                Parcelable state = lv3.onSaveInstanceState();
//
//                fillAdapterListVideo();     //заполняем ListView адаптером
//                fillListView("video_academeg WHERE channelTitle='AcademeG 2nd CH'", lv3);
//
//                lv3.onRestoreInstanceState(state);
//                mSwipeRefreshLayout.setRefreshing(false);//указываем об окончании обновления страницы
//                loadingMore = true;             //Вызываем onScroll только один раз
//                Toast.makeText(getActivity(), "lv3 ParseTask2nd lv3", Toast.LENGTH_SHORT).show();
//            }catch (JSONException e) {e.printStackTrace();}
//        }
//    }//Конец ParseTask2nd
//
//    private class ParseTaskDaily extends AsyncTask<Void, Void, String> {
////        myApplication.getPageToken()
//
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader = null;
//        String resultJson = "";
//
//        @Override
//        protected String doInBackground(Void... params) {
//
//            // получаем данные с внешнего ресурса
//            try {
//                String firstPartURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet";
//                String maxResults = "&maxResults=";
//                String maxResultsKey = "6";
//                String prePageToken = "&pageToken=";
//                String playlistId = "&playlistId=";
////                    String playlistIdKey = "UUM0RSbJnk0nAUvfH4Pp7mjQ";    //мой канал
////                String playlistIdKey = "UUQeaXcwLUDeRoNVThZXLkmw";      //Big Test Drive
////                String playlistIdKey = "UUL1C1f9HWf3Hyct4aqBJi1A";      //AcademeG2nd
//                String playlistIdKey = "UUEVNTzTFSGkZGTjVE9ipXpg";      //AcademeGDaily
//
//
//                String lastPartURL = "&key=";
//                String developerKey = "AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk";
//
//
//                final MyApplication myApplication = (MyApplication) getActivity().getApplication();
//
//                URL url = new URL(firstPartURL + maxResults + maxResultsKey + prePageToken + myApplication.getPageTokenAca2nd() //+ pageToken
//                        + playlistId + playlistIdKey + lastPartURL + developerKey);
////                    URL url = new URL("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=1&pageToken=&playlistId=UUM0RSbJnk0nAUvfH4Pp7mjQ&key=AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk");
//
////                    printURL(url.toString());       //показывает строку url
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
//
//            try {
//                dataJsonObj = new JSONObject(strJson);
//
//                if (dataJsonObj.has("nextPageToken")) {
//
//                    final MyApplication myApplication = (MyApplication) getActivity().getApplication();
//                    myApplication.setPageTokenAca2nd(dataJsonObj.getString("nextPageToken"));
////                    nextPageToken = dataJsonObj.getString("nextPageToken");
//                } else if (dataJsonObj.has("prevPageToken")) {
//                    prevPageToken = dataJsonObj.getString("prevPageToken");
//                }
//
//                JSONArray items = dataJsonObj.getJSONArray("items");
//
//
//                mDatabaseHelper = new DatabaseHelper(getActivity());
//                mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
//                contentValues = new ContentValues();
//
//                returnCursor();
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
//                        fillSQLite(DatabaseHelper.DATABASE_TABLE_ACAGEMEG);
//                    }
//                }
//
//                cursor.close();
//                mSqLiteDatabase.close();
//
//                fillArrayItems(returnCursor());           //заполняем массивы для адаптера
//
//                Parcelable state = lv4.onSaveInstanceState();
//
//                fillAdapterListVideo();     //заполняем ListView адаптером
//                fillListView("video_academeg WHERE channelTitle='AcademeG DailyStream'", lv4);
//
//                lv4.onRestoreInstanceState(state);
//                mSwipeRefreshLayout.setRefreshing(false);//указываем об окончании обновления страницы
//                loadingMore = true;             //Вызываем onScroll только один раз
//                Toast.makeText(getActivity(), "lv4 ParseTask2nd lv4", Toast.LENGTH_SHORT).show();
//            }catch (JSONException e) {e.printStackTrace();}
//        }
//    }//Конец ParseTask2nd

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
        mDatabaseHelper = new DatabaseHelper(getActivity());
        mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();
        String query = "SELECT * FROM "
                + DatabaseHelper.DATABASE_TABLE_ACAGEMEG
                + " ORDER BY "
                + DatabaseHelper.PUBLISHEDAT_COLUMN
                + " DESC";
        cursor = mSqLiteDatabase.rawQuery(query, null);
        return cursor;
    }

    public Cursor returnCursorForClick (String chTitle){
        mDatabaseHelper = new DatabaseHelper(getActivity());
        mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();
        String query = "SELECT * FROM "
                + DatabaseHelper.DATABASE_TABLE_ACAGEMEG
                + " WHERE "
                + DatabaseHelper.CHANNEL_TITLE_COLUMN
                + "='"
                + chTitle
                + "'"
                + " ORDER BY "
                + DatabaseHelper.PUBLISHEDAT_COLUMN
                + " DESC";
        cursor = mSqLiteDatabase.rawQuery(query, null);
        return cursor;
    }

    public void fillSQLite (String sDataBaseTable){
        contentValues.put(DatabaseHelper.TITLE_COLUMN, title);
        contentValues.put(DatabaseHelper.URL_COLUMN, url);
        contentValues.put(DatabaseHelper.DESCRIPTION_COLUMN, description);
        contentValues.put(DatabaseHelper.VIDEO_ID_COLUMN, videoId);
        contentValues.put(DatabaseHelper.PUBLISHEDAT_COLUMN, publishedAt);
        contentValues.put(DatabaseHelper.CHANNEL_TITLE_COLUMN, channelTitle);
//        mSqLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_ACAGEMEG, null, contentValues); //добавляем contentValues в SQLite
        mSqLiteDatabase.insert(sDataBaseTable, null, contentValues); //добавляем contentValues в SQLite
    }

    public void fillSQLiteAll (){
        contentValues.put(DatabaseHelper.TITLE_COLUMN, title);
        contentValues.put(DatabaseHelper.URL_COLUMN, url);
        contentValues.put(DatabaseHelper.DESCRIPTION_COLUMN, description);
        contentValues.put(DatabaseHelper.VIDEO_ID_COLUMN, videoId);
        contentValues.put(DatabaseHelper.PUBLISHEDAT_COLUMN, publishedAt);
//        contentValues.put(DatabaseHelper.PUBLISHEDAT_COLUMN, publishedAtFormat);
        contentValues.put(DatabaseHelper.CHANNEL_TITLE_COLUMN, channelTitle);
        mSqLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_ACAGEMEG, null, contentValues); //добавляем contentValues в SQLite
    }

    public void fillAdapterListVideo(){
//        AdapterListVideo adapterListVideo = new AdapterListVideo(getActivity(), itemName, itemImage, itemDescription);
        adapterListVideo = new AdapterListVideo(getActivity(), itemName, itemImage, itemDescription, itemPublished, itemChannelTitle);
//        lv2.setAdapter(adapterListVideo);
//        loadingMore = true;
    }

    public void fillArrayItems (Cursor cursor){

        if (cursor.getCount() > 0){

            if (cursor.moveToFirst()){
                itemName = new String[cursor.getCount()];
                itemImage = new String[cursor.getCount()];
                itemDescription = new String[cursor.getCount()];
                itemPublished = new String[cursor.getCount()];
                itemChannelTitle = new String[cursor.getCount()];
                for (int i = 0; i < cursor.getCount(); i++){
                    itemName[i] = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE_COLUMN));
                    itemImage[i] = cursor.getString(cursor.getColumnIndex(DatabaseHelper.URL_COLUMN));
                    itemDescription[i] = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                    itemPublished[i] = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PUBLISHEDAT_COLUMN));
                    itemChannelTitle[i] = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CHANNEL_TITLE_COLUMN));
                    cursor.moveToNext();
                }

            } else {
                tv2.setText("херня какая-то.");
            }

        } else {
            tv2.setText("в курсоре ничего нет - База пустая");
        }

        cursor.close();
        mSqLiteDatabase.close();
    }

    public void fillListView (String databaseTable, ListView someLv){
        mDatabaseHelper = new DatabaseHelper(getActivity());
        mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();
        String query =
                "SELECT * FROM "
                + databaseTable
                + " ORDER BY "
                + DatabaseHelper.PUBLISHEDAT_COLUMN
                + " DESC"
                ;

        cursor = mSqLiteDatabase.rawQuery(query, null);

        if (cursor.getCount() > 0){
            if (cursor.moveToFirst()){
                String[] itemName0 = new String[cursor.getCount()];
                String[] itemImage0 = new String[cursor.getCount()];
                String[] itemDescription0 = new String[cursor.getCount()];
                String[] itemPublished0 = new String[cursor.getCount()];
                String[] itemChannelTitle0 = new String[cursor.getCount()];
                for (int i = 0; i < cursor.getCount(); i++){
                    itemName0[i] = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE_COLUMN));
                    itemImage0[i] = cursor.getString(cursor.getColumnIndex(DatabaseHelper.URL_COLUMN));
                    itemDescription0[i] = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DESCRIPTION_COLUMN));
                    itemPublished0[i] = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PUBLISHEDAT_COLUMN));
                    itemChannelTitle0[i] = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CHANNEL_TITLE_COLUMN));
                    cursor.moveToNext();
                }
                AdapterListVideo adapterListVideo1 = new AdapterListVideo(
                        getActivity(),
                        itemName0,
                        itemImage0,
                        itemDescription0,
                        itemPublished0,
                        itemChannelTitle0);
                someLv.setAdapter(adapterListVideo1);
            }

        } else {
//            Toast.makeText(getActivity(), "Курсор пустой!!!", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        mSqLiteDatabase.close();
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

            String lastPartURL = "&key=";
            String developerKey = "AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk";

            MyApplication myApplication = ((MyApplication) getActivity().getApplication());
            URL url = new URL(firstPartURL + maxResults + maxResultsKey + prePageToken + myApplication.getPageTokenAca()
                    + playlistId + sUrl + lastPartURL + developerKey);

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
                final MyApplication myApplication = (MyApplication) getActivity().getApplication();
                myApplication.setPageTokenAca(dataJsonObj.getString("nextPageToken"));
            }

            JSONArray items = dataJsonObj.getJSONArray("items");

            mDatabaseHelper = new DatabaseHelper(getActivity());
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
                JSONObject medium = thumbnails.getJSONObject("high");
                url = medium.getString("url");
                JSONObject resourceId = snippet.getJSONObject("resourceId");
                videoId = resourceId.getString("videoId");

                //Если такого videoId ещё нет в Базе или База пустая
                if (compareSQLiteAndJSON(videoId)){
                    fillSQLiteNew();
                }
            }

            cursor.close();
            mSqLiteDatabase.close();

        }catch (JSONException e) {e.printStackTrace();}
    }

    // Этот метод вызывается из главного потока GUI.
    private void mainProcessing() {
        // Здесь трудоемкие задачи переносятся в дочерний поток.
        Thread thread = new Thread(null, doBackgroundThreadProcessing,
                "Background");
        thread.start();
    }

    private Runnable doBackgroundThreadProcessing = new Runnable() {
        public void run() {

            for (int i = 0; i < saUrls.length; i++){
                final MyApplication myApplication = (MyApplication) getActivity().getApplication();
                myApplication.setPageTokenAca("");
                for (int j = 0; j < 3; j++){
                    fillSQLiteFromJSON(saUrls[i]);
                }
            }
        }
    };

    public void fillSQLiteNew (){
        contentValues.put(DatabaseHelper.TITLE_COLUMN, title);
        contentValues.put(DatabaseHelper.URL_COLUMN, url);
        contentValues.put(DatabaseHelper.DESCRIPTION_COLUMN, description);
        contentValues.put(DatabaseHelper.VIDEO_ID_COLUMN, videoId);
        contentValues.put(DatabaseHelper.PUBLISHEDAT_COLUMN, publishedAt);
        contentValues.put(DatabaseHelper.CHANNEL_TITLE_COLUMN, channelTitle);
        mSqLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE_ACAGEMEG, null, contentValues); //добавляем contentValues в SQLite
    }

//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    private void startAsyncTaskInParallel(AsyncTask task) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        else
//            task.execute();
//    }
}
