package com.example.stige.articoli;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.Toast;

import com.example.stige.myapplication.R;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int PICK_CONTACT_REQUEST = 1;  // The request code
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private SimpleCursorAdapter dataAdapter;
    private int SelectedItem = -1;
    private Cursor cursor;
    private FeedReaderDbHelper mDbHelper;
    private ListView listView;
    private String[] columns = new String[]{
            dbArticoli.ReaderArticles.FeedEntry.DESCRIZIONE,
            dbArticoli.ReaderArticles.FeedEntry.PREZZO,
            dbArticoli.ReaderArticles.FeedEntry.STAGIONE
    };
    private DrawerLayout drawer;
    private int[] to = new int[]{
            R.id.txtDescrizione,
            R.id.txtPrezzo,
            R.id.txtStagione
    };
    Switch switchEstivo,switchInvernale,switchCalze;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu =navigationView.getMenu();
        View actionView = menu.findItem(R.id.nav_calze).getActionView();
        switchCalze =actionView.findViewById(R.id.switchCalze);
        actionView = menu.findItem(R.id.nav_estivo).getActionView();
        switchEstivo =actionView.findViewById(R.id.switchEstivo);
        actionView = menu.findItem(R.id.nav_invernale).getActionView();
        switchInvernale =actionView.findViewById(R.id.switchInvernale);


        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        displayListView();
        // Gets the data repository in write mode
        final ListView listView = findViewById(R.id.listView1);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, NewArticle.class), 1);

            }
        });
//UPDATE
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDbHelper = new FeedReaderDbHelper(getApplicationContext());
                Cursor c = mDbHelper.getAllArticles();
                c.moveToPosition(position);
                mDbHelper.getFilteredArticlesById(c.getInt(c.getColumnIndex(dbArticoli.ReaderArticles.FeedEntry._ID)));
                int row = c.getInt(c.getColumnIndex(BaseColumns._ID));
                Intent intent = new Intent(MainActivity.this, NewArticle.class);

                Bundle bundle = new Bundle();
                bundle.putInt("riga", row);

                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Cancellare l'articolo?")
                        .setPositiveButton(R.string.elimina, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                // FIRE ZE MISSILES!
                                mDbHelper = new FeedReaderDbHelper(getApplicationContext());
                                Cursor c = mDbHelper.getAllArticles();
                                c.moveToPosition(position);
                                mDbHelper.deleteArticle(c.getInt(c.getColumnIndex(dbArticoli.ReaderArticles.FeedEntry._ID)));
                                displayListView();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.show();

                return true;
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 0)
                    fab.hide();
                else
                    fab.show();
            }
        });


    // switch calze
        switchCalze.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchEstivo.setChecked(false);
                    switchInvernale.setChecked(false);
                    cursor = mDbHelper.getFilteredArticlesByCalze();

                } else {
                    switchCalze.setChecked(false);
                    cursor = mDbHelper.getAllArticles();
                }
                dataAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.content_main, cursor, columns, to, 0);
                // Assign adapter to ListView
                listView.setAdapter(null);
                listView.setAdapter(dataAdapter);
                Runnable r = new Runnable() {
                    @Override
                    public void run(){
                        drawer.closeDrawer(GravityCompat.START); //<-- put your code in here.
                    }
                };
                Handler h = new Handler();
                h.postDelayed(r, 500);

            }
        });
        // switch Estivo
        switchEstivo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    switchCalze.setChecked(false);
                    switchInvernale.setChecked(false);
                    cursor = mDbHelper.getFilteredArticlesByEstivo();

                } else {
                    switchEstivo.setChecked(false);
                    cursor = mDbHelper.getAllArticles();
                }
                dataAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.content_main, cursor, columns, to, 0);
                // Assign adapter to ListView
                listView.setAdapter(null);
                listView.setAdapter(dataAdapter);
                Runnable r = new Runnable() {
                    @Override
                    public void run(){
                        drawer.closeDrawer(GravityCompat.START); //<-- put your code in here.

                    }
                };
                Handler h = new Handler();
                h.postDelayed(r, 500);

            }
        });
        // switch Invernale

        switchInvernale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

               // switchInvernale.setChecked(true);
                if (isChecked) {
                    switchCalze.setChecked(false);
                    switchEstivo.setChecked(false);
                    cursor = mDbHelper.getFilteredArticlesByInvernale();

                } else {
                    switchInvernale.setChecked(false);
                    cursor = mDbHelper.getAllArticles();
                }
                dataAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.content_main, cursor, columns, to, 0);
                // Assign adapter to ListView
                listView.setAdapter(null);
                listView.setAdapter(dataAdapter);
                Runnable r = new Runnable() {
                    @Override
                    public void run(){

                        drawer.closeDrawer(GravityCompat.START); //<-- put your code in here.
                    }
                };

                Handler h = new Handler();
                h.postDelayed(r, 500);


            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {

            displayListView();
        }
    }

    private void displayListView() {
        mDbHelper = new FeedReaderDbHelper(getApplicationContext());


        ListView listView = findViewById(R.id.listView1);

        Cursor cursor = mDbHelper.getAllArticles();


        String[] columns = new String[]{
                dbArticoli.ReaderArticles.FeedEntry.DESCRIZIONE,
                dbArticoli.ReaderArticles.FeedEntry.PREZZO,
                dbArticoli.ReaderArticles.FeedEntry.STAGIONE
        };

        int[] to = new int[]{
                R.id.txtDescrizione,
                R.id.txtPrezzo,
                R.id.txtStagione
        };

        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.content_main,
                cursor,
                columns,
                to,
                0);


        // Assign adapter to ListView
        listView.setAdapter(null);
        listView.setAdapter(dataAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mDbHelper = new FeedReaderDbHelper(getApplicationContext());
                ListView listView = findViewById(R.id.listView1);
                Cursor cursor = mDbHelper.getFilteredArticlesByDescr(s);
                String[] columns = new String[]{
                        dbArticoli.ReaderArticles.FeedEntry.DESCRIZIONE,
                        dbArticoli.ReaderArticles.FeedEntry.PREZZO,
                        dbArticoli.ReaderArticles.FeedEntry.STAGIONE
                };

                int[] to = new int[]{
                        R.id.txtDescrizione,
                        R.id.txtPrezzo,
                        R.id.txtStagione
                };

                dataAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.content_main, cursor, columns, to, 0);


                // Assign adapter to ListView
                listView.setAdapter(null);
                listView.setAdapter(dataAdapter);
                return false;
            }

        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                displayListView();
                return false;
            }
        });
        return true;
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_search) {
            onSearchRequested();
            return true;
        }
        if (id == android.R.id.home) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        appData.putString("hello", "world");
        startSearch(null, false, appData, false);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.Import_db) {
            try {
                ImportCsv();
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else if (id == R.id.Export_db) {
            ExportCsv();
        } else if (id == R.id.nav_estivo) {

        } else if (id == R.id.nav_invernale) {

        } else if (id == R.id.nav_calze) {

        }
        /*else if (id == R.id.import_sd) {

        }
        else if (id == R.id.send_to) {
            String currentDBPath = "//data//" + "com.example.stige.myapplication"
                    + "//databases//" + "dbarticoli.db";
            File data = Environment.getDataDirectory();
            File currentDB = new File(data, currentDBPath);

            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.stige.myapplication", currentDB);
            //Uri uri = Uri.parse("file://"+currentDB);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("*");
            // Grant temporary read permission to the content URI
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(share, "Share via"));
        }*/


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadFromSD() {
        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        try {
            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "com.example.stige.myapplication"
                        + "//databases//" + "dbarticoli.db";
                String backupDBPath = "/Download/dbarticoli.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(),
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();

        }

    }

    private void ExportCsv() {


        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "BackupArticoli");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getApplicationContext());
            Cursor cursor = mDbHelper.getAllArticles();
            File file = null;
            File root = Environment.getExternalStorageDirectory();
            if (root.canWrite()) {
                File dir = new File(root.getAbsolutePath() + "/BackupArticoli");
                dir.mkdirs();
                file = new File(dir, "DatiArticoli.csv");
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                }
                try {
                    while (cursor.moveToNext()) {
                        String dataString = "\"" + cursor.getString(cursor.getColumnIndex("_id")) + "\";\"" +
                                cursor.getString(cursor.getColumnIndex("descrizione")) + "\";\"" +
                                cursor.getString(cursor.getColumnIndex("prezzo")) + "\";\"" +
                                cursor.getString(cursor.getColumnIndex("stagione")) + "\"";
                        String combinedString = dataString + "\n";
                        out.write(combinedString.getBytes());
                    }

                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                try {
                    out.close();
                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            file.setReadable(true, false);
            file = new File(new File(Environment.getExternalStorageDirectory(), "BackupArticoli"), "DatiArticoli.csv");
            Uri sharedFileUri = FileProvider.getUriForFile(this, getPackageName(), file);
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Backup dati articoli");
            sendIntent.putExtra(Intent.EXTRA_STREAM, sharedFileUri);
            sendIntent.setType("text/csv");
            startActivity(sendIntent);
        }else{
            Toast.makeText(getBaseContext(), "Non sono riuscito a creare la direcotry!!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void ImportCsv() throws IOException {


        File root = Environment.getExternalStorageDirectory();

        if (root.canWrite()) {
            try {

                Articolo articolo = new Articolo();
                FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getApplicationContext());
                mDbHelper.deleteAllArticles();
                CSVReader reader = new CSVReader(new FileReader(Environment.getExternalStorageDirectory() + "/BackupArticoli/DatiArticoli.csv"), ';');
                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    // nextLine[] is an array of values from the line
                    articolo.setId(Integer.parseInt(nextLine[0]));
                    articolo.setDescrizione(nextLine[1]);
                    articolo.setPrezzo(nextLine[2]);
                    articolo.setStagione(nextLine[3]);
                    mDbHelper.insertArticolo(articolo);
                    System.out.println(nextLine[0] + nextLine[1] + "etc...");
                }
                displayListView();
                Toast.makeText(getBaseContext(), "Importazzione avvenuta con successo",
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        }

       /* FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getApplicationContext());
        Cursor cursor = mDbHelper.getAllArticles();
        String columnString = "\"Id\",\"Descrizione\",\"Prezzo\",\"Stagione\"";
        String dataString = "\"" + cursor.getString(cursor.getColumnIndex("_id")) + "\",\"" +
                cursor.getString(cursor.getColumnIndex("descrizione")) + "\",\"" +
                cursor.getString(cursor.getColumnIndex("prezzo")) + "\",\"" +
                cursor.getString(cursor.getColumnIndex("stagione")) + "\"";


       // file.setReadable(true, false);
        file = new File(new File(Environment.getExternalStorageDirectory(), "BackupArticoli"), "DataArticoli.csv");
        Uri sharedFileUri = FileProvider.getUriForFile(this, getPackageName(),  file);
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Backup dati articoli");
        sendIntent.putExtra(Intent.EXTRA_STREAM, sharedFileUri);
        sendIntent.setType("text/csv");
        startActivity(sendIntent);
*/
    }
}
