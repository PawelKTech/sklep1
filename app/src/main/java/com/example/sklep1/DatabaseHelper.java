package com.example.sklep1;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_COMPUTER_DESCRIPTION = "computer_description";
    public static final String COLUMN_COMPUTER_IMAGE = "computer_image";
    public static final String COLUMN_KEYBOARD_DESCRIPTION = "keyboard_description";
    public static final String COLUMN_KEYBOARD_IMAGE = "keyboard_image";
    public static final String COLUMN_MOUSE_DESCRIPTION = "mouse_description";
    public static final String COLUMN_MOUSE_IMAGE = "mouse_image";
    public static final String COLUMN_CAMERA_DESCRIPTION = "camera_description";
    public static final String COLUMN_CAMERA_IMAGE = "camera_image";
    public static final String COLUMN_SUM = "suma";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_COMPUTER_DESCRIPTION + " TEXT, " +
                COLUMN_COMPUTER_IMAGE + " TEXT, " +
                COLUMN_KEYBOARD_DESCRIPTION + " TEXT, " +
                COLUMN_KEYBOARD_IMAGE + " TEXT, " +
                COLUMN_MOUSE_DESCRIPTION + " TEXT, " +
                COLUMN_MOUSE_IMAGE + " TEXT, " +
                COLUMN_CAMERA_DESCRIPTION + " TEXT, " +
                COLUMN_CAMERA_IMAGE + " TEXT," +
                COLUMN_SUM + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertItem(String name, String phone, String computerDescription, String computerImage,
                           String keyboardDescription, String keyboardImage, String mouseDescription,
                           String mouseImage, String cameraDescription, String cameraImage, Integer suma) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_PHONE, phone);
        contentValues.put(COLUMN_COMPUTER_DESCRIPTION, computerDescription);
        contentValues.put(COLUMN_COMPUTER_IMAGE, computerImage);
        contentValues.put(COLUMN_KEYBOARD_DESCRIPTION, keyboardDescription.isEmpty() ? "Bez Klawiatury" : keyboardDescription);
        contentValues.put(COLUMN_KEYBOARD_IMAGE, keyboardImage.isEmpty() ? "Bez zdjęcia Klawiatury" : keyboardImage);
        contentValues.put(COLUMN_MOUSE_DESCRIPTION, mouseDescription.isEmpty() ? "Bez Myszki" : mouseDescription);
        contentValues.put(COLUMN_MOUSE_IMAGE, mouseImage.isEmpty() ? "Bez zdjęci Myszki" : mouseImage);
        contentValues.put(COLUMN_CAMERA_DESCRIPTION, cameraDescription.isEmpty() ? "Bez Kamery" : cameraDescription);
        contentValues.put(COLUMN_CAMERA_IMAGE, cameraImage.isEmpty() ? "Bez zdjęci Kamery" : cameraImage);
        contentValues.put(COLUMN_SUM, suma);

        db.insert(TABLE_NAME, null, contentValues);
    }

    public void deleteAllRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public Cursor getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id AS _id, * FROM " + TABLE_NAME, null);
    }

}
