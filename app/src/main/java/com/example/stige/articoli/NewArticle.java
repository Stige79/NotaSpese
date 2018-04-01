package com.example.stige.articoli;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stige.myapplication.R;

public class NewArticle extends AppCompatActivity {
    private int idRow=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_article);

        final TextView tDescr = findViewById(R.id.editDescr);
        final TextView tPrz = findViewById(R.id.editPrz);
        final TextView tStg = findViewById(R.id.editStag);
        final TextView tId = findViewById(R.id.txtId);

        final String[] resultText = {""};

        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Button btnSave = findViewById(R.id.btnSalva);
        Bundle extraBundle =getIntent().getExtras();
        if( extraBundle!=null && !extraBundle.isEmpty()) {
            idRow = extraBundle.getInt("riga",0);
            mDbHelper = new FeedReaderDbHelper(getApplicationContext());
            Cursor cursor = mDbHelper.getFilteredArticlesById(idRow);

            tPrz.setText(cursor.getString(2));
            tDescr.setText(cursor.getString(1));
            tStg.setText(cursor.getString(3));
            tId.setText(cursor.getString(0));
            resultText[0] ="Articolo aggiornato";
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Articolo articolo = new Articolo();


                tPrz.setInputType(InputType.TYPE_CLASS_NUMBER);
                articolo.setDescrizione(tDescr.getText().toString());
                articolo.setPrezzo(tPrz.getText().toString());
                articolo.setStagione(tStg.getText().toString());


                FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getApplicationContext());
                if(idRow<0) {
                    mDbHelper.insertArticolo(articolo);
                }else{
                    articolo.setId(Integer.parseInt( tId.getText().toString()));
                    mDbHelper.updateArticolo(articolo);
                }

                if(resultText[0] =="") {
                    resultText[0] = "Articolo inserito";
                }
                    Toast.makeText(getApplication(), resultText[0],
                            Toast.LENGTH_LONG).show();


                setResult(1,null);
                finish();
            }
        });

    }

}
