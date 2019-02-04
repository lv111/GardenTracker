package com.gardentracker;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.gardentracker.adapters.DailyMaintenancesAdapter;
import com.gardentracker.adapters.NotesAdapter;
import com.gardentracker.adapters.PhotoGalleryAdapter;
import com.gardentracker.adapters.SimpleDailyMaintenancesAdapter;
import com.gardentracker.classes.DailyMaintenance;
import com.gardentracker.classes.Maintenance;
import com.gardentracker.classes.Note;
import com.gardentracker.classes.Photo;
import com.gardentracker.classes.Shared;
import com.gardentracker.fragment.FragmentFirst;
import com.gardentracker.fragment.FragmentSecond;
import com.gardentracker.fragment.FragmentThird;
import com.gardentracker.notifications.AlarmReceiver;
import com.gardentracker.notifications.LocalData;
import com.gardentracker.notifications.NotificationScheduler;
import com.gardentracker.provider.Contract;

import java.util.ArrayList;
import java.util.Calendar;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //todo: tu su nedolezite todo, ktore spravim na konci
    //pre ikonky a vsetky blbosti vyrobit ldpi,mdpi,atd.
    //pridat jazyky
    //upravit layouty pre landscape
    //zjednotit vsade velkost pisma
    //skontrolovat, ci vsade funguju tlacidla spet v hornej liste

    //pridat pre maintenance moznost odlozenia o den, dva dni atd

//pokomentovat kazdu funkciu podobne
    /**
     * @param data data
     * @throws Exception throws exception
     * @return return miniature
     * */

// pre SettingsActivity.java : POCASIE nastavenie predvoleneho mesta pocasia, popripade podla aktualnej polohy alebo vyber polohy, nastavenie jazyka aplikacie a tiez aj pre pocasie jazyk
// pre WeatherActivity.java : v metode setUI() v listeneri pre fab ma byt metoda pre otvor dialog s hladanim mesta
// pre WeatherActivity.java : vo WeatherTask v url jednotky ako stupen celzia nacitavat z nastaveni, z databazy, takze toto musim potom aj pridat do nastaveni

// naprogramovat aktivitu pre fotenie
    //v buducnosti pridat kategorie pre poznamky, potom ich v zobrazeni poznamok mozno triedit podla kategorii, taktiez moznost pripominat poznamky bud pravidelne alebo nastavit pre nejaky konkretny datum
//POCASIE tabulku weather spravit tak, aby sa uchovavali posledne stiahnute informacie o pocasi


    Shared shared;
    ArrayList<DailyMaintenance> dailyMaintenances;
    ArrayList<Photo> photos;
    ArrayList<Note> notes;
    private static final int PERMISSION_REQUEST_CODE_PHOTO = 222;
    private static final int PERMISSION_REQUEST_CODE_GALLERY = 223;
    private static final int PERMISSION_REQUEST_CODE_WEATHER = 224;
    private static final int COUNT_OF_IMAGES_IN_ROW = 2;
    LocalData localData;
    ClipboardManager myClipboard;
    BottomNavigationView bottomNavigationView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMainLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_add_new_maintenance) {
            addNewMaintenance();
        } else if (id == R.id.nav_see_maintenances) {
            seeMaintenances();
        } else if (id == R.id.nav_add_new_photo) {
            addNewPhoto();
        } else if (id == R.id.nav_photo_gallery) {
            seePhotoGallery();
        } else if (id == R.id.nav_add_new_note) {
            addNewNote();
        } else if (id == R.id.nav_see_notes) {
            seeNotes();
        } else if (id == R.id.nav_today_maintenances) {
            seeTodayMaintenances();
        } else if (id == R.id.nav_weather_forecast) {
            seeWeatherForecast();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUI() {
        shared = new Shared();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);

                localData = new LocalData(getApplicationContext());
                cursor.moveToFirst();

                int notificationOn = cursor.getInt(cursor.getColumnIndex(Contract.Settings.NOTIFICATION_ON));
                if (notificationOn == 1) {
                    localData.setReminderStatus(true);
                    long notificationTime = cursor.getLong(cursor.getColumnIndex(Contract.Settings.NOTIFICATION_TIME));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(notificationTime * 1000);

                    myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    localData.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                    localData.setMin(calendar.get(Calendar.MINUTE));

                    NotificationScheduler.setReminder(MainActivity.this, AlarmReceiver.class, localData.getHour(), localData.getMin());
                }
                else
                    localData.setReminderStatus(false);
            }
        };
        asyncQueryHandler.startQuery(0,null,Contract.Settings.CONTENT_URI,null,null,null,null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_PHOTO:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && readAccepted && writeAccepted)
                        addNewPhoto();
                    else
                        Toast.makeText(MainActivity.this,getResources().getString(R.string.permission_denied),Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REQUEST_CODE_GALLERY:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (!cameraAccepted || !readAccepted || !writeAccepted)
                        Toast.makeText(MainActivity.this,getResources().getString(R.string.permission_denied),Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REQUEST_CODE_WEATHER:
                if (grantResults.length > 0) {
                    boolean internetAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean accessNetworkStateAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (internetAccepted && accessNetworkStateAccepted)
                        seeWeatherForecast();
                    else
                        Toast.makeText(MainActivity.this,getResources().getString(R.string.permission_denied),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private void setMainLayout() {
        Fragment selectedFragment = null;
        if (bottomNavigationView == null) {
            bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
            selectedFragment = FragmentFirst.newInstance();
            int positionSelectedFragment = 0;
            navigationItemSelected(bottomNavigationView.getMenu().getItem(positionSelectedFragment));
        }
        else {
            MenuItem selectedItem = getSelectedItem();
            navigationItemSelected(selectedItem);
            switch (selectedItem.getItemId()) {
                case R.id.action_item1:
                    selectedFragment = FragmentFirst.newInstance();
                    break;
                case R.id.action_item2:
                    selectedFragment = FragmentSecond.newInstance();
                    break;
                case R.id.action_item3:
                    selectedFragment = FragmentThird.newInstance();
                    break;
            }
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return navigationItemSelected(item);
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    private MenuItem getSelectedItem(){
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++){
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.isChecked()){
                return menuItem;
            }
        }
        return null;
    }

    private boolean navigationItemSelected(MenuItem item) {
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.action_item1:
                selectedFragment = FragmentFirst.newInstance();
                break;
            case R.id.action_item2:
                selectedFragment = FragmentSecond.newInstance();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && !shared.checkPermissionGallery(getApplicationContext()))
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE_GALLERY);
                break;
            case R.id.action_item3:
                selectedFragment = FragmentThird.newInstance();
                break;
        }
        if (selectedFragment == null)
            selectedFragment = FragmentFirst.newInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();

        switch (item.getItemId()) {
            case R.id.action_item1:
                selectFragmentFirst();
                break;
            case R.id.action_item2:
                selectFragmentSecond();
                break;
            case R.id.action_item3:
                selectFragmentThird();
                break;
        }

        return true;
    }

    private void selectFragmentFirst() {
        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                if (cursor != null && cursor.getCount() > 0) {
                    dailyMaintenances = shared.setMaintenancesArrayFromCursor(cursor);

                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutMaintenances);
                    linearLayout.removeAllViews();
                    ConstraintLayout layoutToInclude = (ConstraintLayout) View.inflate(MainActivity.this, R.layout.content_main_maintenances, null);
                    linearLayout.addView(layoutToInclude);

                    int countFirstMaintenances = 0;
                    int i = 0;
                    long nowStartDay = shared.atStartOfDay(System.currentTimeMillis())/1000;
                    do {
                        Maintenance maintenance = dailyMaintenances.get(i).getMaintenances().get(0);
                        long maintenanceStartDay = maintenance.getNextCheck();
                        if (maintenanceStartDay <= nowStartDay)
                            countFirstMaintenances++;
                        i++;
                    } while (i < dailyMaintenances.size());

                    final ArrayList<DailyMaintenance> firstDailyMaintenances = new ArrayList<>(dailyMaintenances.subList(0,countFirstMaintenances));
                    ListView listView = (ListView) findViewById(R.id.listViewClosestMaintenances);
                    DailyMaintenancesAdapter adapter = new DailyMaintenancesAdapter(MainActivity.this, firstDailyMaintenances);
                    listView.setAdapter(adapter);

                    if (dailyMaintenances.size() > countFirstMaintenances) {
                        ArrayList<DailyMaintenance> subDailyMaintenances = new ArrayList<>(dailyMaintenances.subList(countFirstMaintenances, dailyMaintenances.size()));
                        listView = (ListView) findViewById(R.id.listviewNextMaintenances);
                        SimpleDailyMaintenancesAdapter adapter1 = new SimpleDailyMaintenancesAdapter(MainActivity.this, subDailyMaintenances);
                        listView.setAdapter(adapter1);
                    }
                }
                else {
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutMaintenances);
                    linearLayout.removeAllViews();
                    ConstraintLayout layoutToInclude = (ConstraintLayout) View.inflate(MainActivity.this, R.layout.content_main_any_maintenances, null);
                    layoutToInclude.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                    linearLayout.addView(layoutToInclude);
                }
            }
        };
        asyncQueryHandler.startQuery(0,null, Contract.Maintenance.CONTENT_URI,null,null,null, Contract.Maintenance.NEXT_CHECK);

        (findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewMaintenance();
            }
        });
    }

    private void selectFragmentSecond() {
        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                if (cursor != null && cursor.getCount() > 0) {
                    photos = new ArrayList<>();
                    cursor.moveToFirst();
                    do {
                        photos.add(new Photo(cursor.getInt(cursor.getColumnIndex(Contract.Photo._ID)), null, null, null,
                                cursor.getBlob(cursor.getColumnIndex(Contract.Photo.MINIATURE)), -1, -1));
                    } while (cursor.moveToNext());

                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutPhotos);
                    linearLayout.removeAllViews();
                    ConstraintLayout layoutToInclude = (ConstraintLayout) View.inflate(MainActivity.this, R.layout.content_main_photos, null);
                    linearLayout.addView(layoutToInclude);

                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                    recyclerView.setHasFixedSize(false);
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MainActivity.this, COUNT_OF_IMAGES_IN_ROW);
                    recyclerView.setLayoutManager(layoutManager);
                    PhotoGalleryAdapter adapter = new PhotoGalleryAdapter(MainActivity.this, photos);
                    recyclerView.setAdapter(adapter);
                } else {
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutPhotos);
                    linearLayout.removeAllViews();
                    ConstraintLayout layoutToInclude = (ConstraintLayout) View.inflate(MainActivity.this, R.layout.content_main_any_photos, null);
                    layoutToInclude.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    linearLayout.addView(layoutToInclude);
                }
            }
        };
        asyncQueryHandler.startQuery(0, null, Contract.Photo.CONTENT_URI, null, null, null, null);

        (findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPhoto();
            }
        });
    }

    private void selectFragmentThird() {
        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                if (cursor != null && cursor.getCount() > 0) {
                    notes = new ArrayList<>();
                    cursor.moveToFirst();
                    do {
                        notes.add(new Note(cursor.getInt(cursor.getColumnIndex(Contract.Note._ID)), cursor.getString(cursor.getColumnIndex(Contract.Note.DESCRIPTION)),
                                cursor.getLong(cursor.getColumnIndex(Contract.Note.CHANGED))));
                    } while (cursor.moveToNext());

                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutNotes);
                    linearLayout.removeAllViews();
                    ConstraintLayout layoutToInclude = (ConstraintLayout) View.inflate(MainActivity.this, R.layout.content_main_notes, null);
                    linearLayout.addView(layoutToInclude);

                    ListView listView = (ListView) findViewById(R.id.listViewNotes);
                    NotesAdapter adapter = new NotesAdapter(MainActivity.this, notes);
                    listView.setAdapter(adapter);
                } else {
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutNotes);
                    linearLayout.removeAllViews();
                    ConstraintLayout layoutToInclude = (ConstraintLayout) View.inflate(MainActivity.this, R.layout.content_main_any_notes, null);
                    layoutToInclude.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    linearLayout.addView(layoutToInclude);
                }
            }
        };
        asyncQueryHandler.startQuery(0, null, Contract.Note.CONTENT_URI, null, null, null, null);

        (findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewNote();
            }
        });
    }

    private void addNewMaintenance() {
        Intent intent = new Intent(MainActivity.this,AddNewMaintenanceActivity.class);
        startActivity(intent);
    }

    private void seeMaintenances() {
        (bottomNavigationView.findViewById(R.id.action_item1)).performClick();
    }

    private void addNewPhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && !shared.checkPermissionPhoto(getApplicationContext()))
            ActivityCompat.requestPermissions(this,new String[]{CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE_PHOTO);
        else {
            Intent intent = new Intent(MainActivity.this, TakePictureActivity.class);
            intent.putExtra("photo", true);
            startActivity(intent);
        }
    }

    private void seePhotoGallery() {
        (bottomNavigationView.findViewById(R.id.action_item2)).performClick();
    }

    private void addNewNote() {
        Intent intent = new Intent(MainActivity.this,AddNewNoteActivity.class);
        startActivity(intent);
    }

    private void seeNotes() {
        (bottomNavigationView.findViewById(R.id.action_item3)).performClick();
    }

    private void seeTodayMaintenances() {
        String selection = Contract.Maintenance.NEXT_CHECK + "<=?";
        String[] selectionArgs = new String[] {String.valueOf( shared.atEndOfDay(System.currentTimeMillis())/1000 )};
        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                if (cursor != null && cursor.getCount() > 0) {
                    Intent intent = new Intent(MainActivity.this,NotificationActivity.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.any_today_maintenances),Toast.LENGTH_SHORT).show();
            }
        };
        asyncQueryHandler.startQuery(0,null,Contract.Maintenance.CONTENT_URI,null,selection,selectionArgs,null);
    }

    private void seeWeatherForecast() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && !shared.checkPermissionGallery(getApplicationContext()))
            ActivityCompat.requestPermissions(this,new String[]{INTERNET, ACCESS_NETWORK_STATE},PERMISSION_REQUEST_CODE_WEATHER);
        else {
            Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
            startActivity(intent);
        }*/

        Toast.makeText(MainActivity.this, getResources().getString(R.string.weather_not_implemented), Toast.LENGTH_SHORT).show();
    }
}