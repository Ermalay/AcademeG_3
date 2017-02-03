package ru.novil.sergey.academegtruestories;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.Settings;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.novil.sergey.navigationdraweractivity.R;
import ru.novil.sergey.academegtruestories.sqlite.DatabaseHelper;

public class YTActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, YouTubePlayer.OnFullscreenListener {

    TextView tvYTTitle, tvYTDescription, tvYTDate;
    ImageView ivYTArrow;
    LinearLayout llYTDate, llYTDesc;

//    RelativeLayout activity_yt;
    LinearLayout activity_yt;

    boolean bOnPress = true;

    @SuppressLint("InlinedApi")
    private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9
            ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;

    @SuppressLint("InlinedApi")
    private static final int LANDSCAPE_ORIENTATION = Build.VERSION.SDK_INT < 9
            ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            : ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;

    private YouTubePlayer mPlayer = null;
    private boolean mAutoRotation = false;

    String DEVELOPER_KEY = "AIzaSyD7VSUJPszW-64AZ4t_9EO90sUHXrkOzHk";

    DatabaseHelper mDatabaseHelper;
    SQLiteDatabase mSqLiteDatabase;
    Cursor cursor;

    Date date;
    String sdfsdf;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yt);

        mAutoRotation = Settings.System.getInt(getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1;

        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.player_view_yt);
        youTubePlayerView.initialize(DEVELOPER_KEY, this);

        activity_yt = (LinearLayout) findViewById(R.id.activity_yt);
        activity_yt.setGravity(Gravity.CENTER_VERTICAL);

        tvYTTitle = (TextView) findViewById(R.id.tvYTTitle);
        tvYTTitle.setTextSize(22);
        tvYTTitle.setPadding(10, 0, 10, 20);

        llYTDate = (LinearLayout) findViewById(R.id.llYTDate);
        tvYTDate = (TextView) findViewById(R.id.tvYTDate);

        ivYTArrow = (ImageView) findViewById(R.id.ivYTArrow);
        ivYTArrow.setImageResource(R.drawable.ic_arrow_drop_down_circle_white_24dp);

        llYTDesc = (LinearLayout) findViewById(R.id.llYTDesc);
        tvYTDescription = (TextView) findViewById(R.id.tvYTDescription);
        tvYTDescription.setVisibility(View.GONE);

////        Date date = Calendar.getInstance().getTime();
////        SimpleDateFormat sfd = new SimpleDateFormat("dd.mm.yyyy hh:mm:ss");
////        sdfsdf = sfd.format(date);
//        Date date = Calendar.getInstance().getTime();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//        sdfsdf = sdf.format(date);

        returnCursorVId(getIntent().getStringExtra("pushkin"));
        if (cursor.moveToFirst()){
            tvYTTitle.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE_COLUMN)));
            tvYTDate.setText(myDateFormat(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PUBLISHEDAT_COLUMN))));
            tvYTDescription.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DESCRIPTION_COLUMN)));
//            tvDesc.setText("Описание: \n\n" + cursor.getString(cursor.getColumnIndex(DatabaseHelper.DESCRIPTION_COLUMN)));
        } else {
            tvYTTitle.setText("курсор пустой");
        }

        llYTDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bOnPress){
                    activity_yt.setGravity(Gravity.TOP);
                    tvYTTitle.setTextSize(16);
                    tvYTTitle.setPadding(10, 5, 10, 5);
                    tvYTDescription.setVisibility(View.VISIBLE);
                    ivYTArrow.setImageResource(R.drawable.ic_arrow_drop_up_circle_white_24dp);
                    bOnPress = false;
                } else {
                    activity_yt.setGravity(Gravity.CENTER_VERTICAL);
                    tvYTTitle.setTextSize(22);
                    tvYTTitle.setPadding(10, 0, 10, 20);
                    tvYTDescription.setVisibility(View.GONE);
                    ivYTArrow.setImageResource(R.drawable.ic_arrow_drop_down_circle_white_24dp);
                    bOnPress = true;
                }

            }
        });
    }

    private String myDateFormat (String dateFromSQLie) {
        Locale locale = new Locale("ru_RU");
        String formattedDate = "";
        SimpleDateFormat sqlDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
//        SimpleDateFormat sqlDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDate = new SimpleDateFormat("'Опубликовано 'dd.MM.yyyy'г. в 'HH:mm");
        Date res = null;
        try {
            res = sqlDate.parse(dateFromSQLie);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formattedDate = outputDate.format(res);
        return formattedDate;
    }

    public Cursor returnCursorVId(String sVId) {
        mDatabaseHelper = new DatabaseHelper(getBaseContext());
        mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();
        String query = "SELECT * FROM "
                + DatabaseHelper.DATABASE_TABLE_ACAGEMEG
                + " WHERE "
                + DatabaseHelper.VIDEO_ID_COLUMN
                + "='"
                + sVId
                + "'";

        cursor = mSqLiteDatabase.rawQuery(query, null);
        return cursor;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        mPlayer = player;
        player.loadVideo(getIntent().getStringExtra("pushkin"));
        player.setOnFullscreenListener(this);

        if (mAutoRotation) {
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION
                    | YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
                    | YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE
                    | YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        } else {
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION
                    | YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
                    | YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (mPlayer != null)
                mPlayer.setFullscreen(true);
            tvYTTitle.setVisibility(View.GONE);
            llYTDate.setVisibility(View.GONE);
            llYTDesc.setVisibility(View.GONE);
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (mPlayer != null)
                mPlayer.setFullscreen(false);
            tvYTTitle.setVisibility(View.VISIBLE);
            llYTDate.setVisibility(View.VISIBLE);
            llYTDesc.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFullscreen(boolean fullsize) {
        if (fullsize) {
            setRequestedOrientation(LANDSCAPE_ORIENTATION);
        } else {
            setRequestedOrientation(PORTRAIT_ORIENTATION);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
