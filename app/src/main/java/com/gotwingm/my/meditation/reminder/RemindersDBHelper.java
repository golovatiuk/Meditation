package com.gotwingm.my.meditation.reminder;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RemindersDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "reminders";
    public static final String TABLE_NAME = "reminders";
    private static final String TIMING = "timing";
    private static final String DAYS = "days";
    private static final String TIME_OF_A_DAY = "time";

    public RemindersDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        int i = 0;
        String[] taming = new String[]{"1min", "5min", "10min", "20min", "kids"};
        ContentValues values = new ContentValues();

        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TIMING + " TEXT, " + DAYS + " TEXT, " + TIME_OF_A_DAY + " TEXT);");

        while (i < 5) {
            values.put(TIMING, taming[i]);
            values.put(DAYS, "0000000");
            values.put(TIME_OF_A_DAY, "0");
            db.insert(TABLE_NAME, null, values);
            values.clear();
            i++;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
