package ru.novil.sergey.academegtruestories;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import ru.novil.sergey.navigationdraweractivity.R;
import ru.novil.sergey.academegtruestories.sqlite.DatabaseHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private WebView mWebView;

    protected OnBackPressedListener onBackPressedListener;

    Fragment fragment = null;
    Class fragmentClass = null;

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    FirstFragment start;

    TextView matv1;

    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Snackbar.make(view, "Что делать с этой кнопкой?", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //На главную страницу фрагмент первого пункта меню слева
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_camera));

//        mDatabaseHelper = new DatabaseHelper(this, "mydatabase.db", null, 1);
//        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(DatabaseHelper.CAT_NAME_COLUMN, "andrew@androiddocs.ruuOOOOOOOOOO");
//        values.put(DatabaseHelper.PHONE_COLUMN, "main_PHONE_COLUMN");
//        values.put(DatabaseHelper.AGE_COLUMN, "main_AGE_COLUMN");
//        mSqLiteDatabase.insert("cats", null, values);
//        mSqLiteDatabase.close();
//
//        start = new FirstFragment(getApplicationContext());
//        start.startParseTask();

    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Toast.makeText(this, "Тут будет страница об авторе каналов, контакты и прочее нужное", Toast.LENGTH_LONG).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;
        Class fragmentClass = null;

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            fragmentClass = FirstFragment.class;
        } else if (id == R.id.nav_gallery) {
            fragmentClass = SecondFragment.class;
//            fragmentClass = ThirdFragment.class;
        } else if (id == R.id.nav_slideshow) {
            fragmentClass = ThirdFragment.class;
        }
        else if (id == R.id.nav_vk) {
//            fragmentClass = FifthFragment.class;
            fragmentClass = FourthFragment.class;
        }
        else if (id == R.id.nav_instagram) {
//            fragmentClass = FourthFragment.class;
            fragmentClass = FifthFragment.class;
        }
        else if (id == R.id.nav_souvenirs) {
//            fragmentClass = FifthFragment.class;
            fragmentClass = SixthFragment.class;
        }

//        else if (id == R.id.nav_manage) {
//            onBackPressed();
//        } else if (id == R.id.nav_share) {
//        } else if (id == R.id.nav_send) {
//        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
        }

        // Вставляем фрагмент, заменяя текущий фрагмент
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
//                .addToBackStack(null)
                .commit();
        // Выделяем выбранный пункт меню в шторке
        item.setChecked(true);
        // Выводим выбранный пункт в заголовке
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

//        FourthFragment fourthFragment = (FourthFragment) getSupportFragmentManager()
//                .findFragmentByTag("fourthFragment");
//        if (fourthFragment == null || !fourthFragment.goBack()) {
//            super.onBackPressed();
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                if (back_pressed + 2000 > System.currentTimeMillis()){
                    super.onBackPressed();
                }
                else {
                    Toast.makeText(getBaseContext(), "Нажмите дважды для выхода",
                            Toast.LENGTH_SHORT).show();
                }

                back_pressed = System.currentTimeMillis();
            }


    }
}
