package com.example.stige.articoli;

import android.provider.BaseColumns;

/**
 * Created by Stige on 24/03/2018.
 */

class dbArticoli {
    public final class ReaderArticles {
        // To prevent someone from accidentally instantiating the contract class,
        // make the constructor private.
        private ReaderArticles() {}

        /* Inner class that defines the table contents */
        public class FeedEntry implements BaseColumns {
            public static final String TABLE_NAME = "NotaSpese";
           // public static final String _ID = "idArticolo";
            public static final String DESCRIZIONE = "descrizione";
            public static final String PREZZO = "prezzo";
            public static final String STAGIONE = "stagione";
        }
    }


}
