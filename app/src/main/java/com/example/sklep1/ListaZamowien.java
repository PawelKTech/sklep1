package com.example.sklep1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ListaZamowien extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView listView;
    private SimpleCursorAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menupowrotne, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.powrot:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_zamowien);

        dbHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.listView);

        // Wyświetlanie rekordów
        displayRecords();

        // Obsługa przycisku usuwania
        Button btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteAllRecords();
                displayRecords(); // Odświeżenie listy po usunięciu
            }
        });
    }

    private void displayRecords() {
        Cursor cursor = dbHelper.getAllItems();
        String[] from = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_PHONE,
                DatabaseHelper.COLUMN_COMPUTER_DESCRIPTION,
                DatabaseHelper.COLUMN_COMPUTER_IMAGE,
                DatabaseHelper.COLUMN_KEYBOARD_DESCRIPTION,
                DatabaseHelper.COLUMN_KEYBOARD_IMAGE,
                DatabaseHelper.COLUMN_MOUSE_DESCRIPTION,
                DatabaseHelper.COLUMN_MOUSE_IMAGE,
                DatabaseHelper.COLUMN_CAMERA_DESCRIPTION,
                DatabaseHelper.COLUMN_CAMERA_IMAGE,
                DatabaseHelper.COLUMN_SUM
        };
        int[] to = {
                R.id.test,
                R.id.name,
                R.id.phone,
                R.id.computer_description,
                R.id.computer_image,
                R.id.keyboard_description,
                R.id.keyboard_image,
                R.id.mouse_description,
                R.id.mouse_image,
                R.id.camera_description,
                R.id.camera_image,
                R.id.sum
        };

        adapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor, from, to, 0);
        listView.setAdapter(adapter);
    }
}
