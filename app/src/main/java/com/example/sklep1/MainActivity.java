package com.example.sklep1;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    private Spinner spinner, spinnerMouse, spinnerKeyboard, spinnerWebcam;

    private EditText name, phonenumber;
    private Button button;
    private CheckBox checkboxForKeyboard, checkboxForMouse, checkboxForWebcam;
    SmsManager smsManager;
    private TextView textView;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.listaZamowien:
                Intent intent = new Intent(this, ListaZamowien.class);
                startActivity(intent);
                break;

            case R.id.udostepnijKoszyk:
                udostepnijKoszyk();
            break;

            case R.id.zapiszUstawienia:
                String koszyk = getkoszyk();
                Intent intent3 = new Intent(this, ZapisanyKoszyk.class);
                intent3.putExtra("koszyk", koszyk);
                startActivity(intent3);
                break;

            case R.id.oAutorzeProgramu:
                Intent intent2 = new Intent(this, Informacjeoautorze.class);
                startActivity(intent2);
                break;

            case R.id.sendSms:
                String formattedDateTime = getDate();
                String nameValue = name.getText().toString();
                String phoneNumberValue = phonenumber.getText().toString();
                if (nameValue != null && phoneNumberValue != null) {
                    try {
                        sendSmsManager(nameValue, phoneNumberValue, formattedDateTime);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error while trying to send sms!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Imie i numer telefonu nie mogą być puste!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.sendMail:
                sendEmail();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) { new AddDynamicShortcutTask().execute(); }
        databaseHelper = new DatabaseHelper(this);


        //spiners
        spinner = findViewById(R.id.computer);
        spinnerMouse = findViewById(R.id.mouse);
        spinnerKeyboard = findViewById(R.id.keyboard);
        spinnerWebcam = findViewById(R.id.webcam);

        //checkbox
        checkboxForMouse = findViewById(R.id.checkboxForMouse);
        checkboxForKeyboard = findViewById(R.id.checkboxForKeyboard);
        checkboxForWebcam = findViewById(R.id.checkboxForWebcam);

        checkCheckbox();

        //name and phone number
        name = findViewById(R.id.name);
        phonenumber = findViewById(R.id.phoneNumber);
        textView = findViewById(R.id.textView);


        button = findViewById(R.id.button);

        //display data
        display(DeviceData.computers, DeviceData.computersimageNames, spinner);
        display(DeviceData.mouses, DeviceData.mousesimageNames, spinnerMouse);
        display(DeviceData.keyboards, DeviceData.keyboardsimageNames, spinnerKeyboard);
        display(DeviceData.webcams, DeviceData.webcamsimageNames, spinnerWebcam);

        int selectedHiddenValue1 = DeviceData.computersValues[spinner.getSelectedItemPosition()];
        textView.setText(getResources().getString(R.string.welcome_message)+ String.valueOf(selectedHiddenValue1) + "PLN");


        //listeners for action on spinner or checkbox
        setListeners(checkboxForMouse, spinnerMouse);
        setListeners(checkboxForKeyboard, spinnerKeyboard);
        setListeners(checkboxForWebcam, spinnerWebcam);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { updateTextView();} @Override public void onNothingSelected(AdapterView<?> parent) { } });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String formattedDateTime = getDate();

                //computers
                int selectedPosition1 = spinner.getSelectedItemPosition();
                String selectedItem1 = DeviceData.computers[selectedPosition1];
                String selectedImageName1 = DeviceData.computersimageNames[selectedPosition1];
                int selectedHiddenValue1 = DeviceData.computersValues[selectedPosition1];
                //mouse
                int selectedHiddenValue;
                String selectedItem;
                String selectedImageName;
                if (checkboxForMouse.isChecked()) {
                    int selectedPosition = spinnerMouse.getSelectedItemPosition();
                    selectedItem = DeviceData.mouses[selectedPosition];
                    selectedImageName = DeviceData.mousesimageNames[selectedPosition];
                    selectedHiddenValue = DeviceData.mousesValues[selectedPosition];
                } else {
                    selectedItem = "";
                    selectedImageName = "";
                    selectedHiddenValue = 0;
                }

                //keyboard
                String selectedItem2;
                String selectedImageName2;
                int selectedHiddenValue2;
                if (checkboxForKeyboard.isChecked()) {
                    int selectedPosition2 = spinnerKeyboard.getSelectedItemPosition();
                    selectedItem2 = DeviceData.keyboards[selectedPosition2];
                    selectedImageName2 = DeviceData.keyboardsimageNames[selectedPosition2];
                    selectedHiddenValue2 = DeviceData.keyboardsValues[selectedPosition2];
                } else {
                    selectedItem2 = "";
                    selectedImageName2 = "";
                    selectedHiddenValue2 = 0;
                }

                //webcam
                String selectedItem3;
                String selectedImageName3;
                int selectedHiddenValue3;
                if (checkboxForWebcam.isChecked()) {
                    int selectedPosition3 = spinnerWebcam.getSelectedItemPosition();
                    selectedItem3 = DeviceData.webcams[selectedPosition3];
                    selectedImageName3 = DeviceData.webcamsimageNames[selectedPosition3];
                    selectedHiddenValue3 = DeviceData.webcamsValues[selectedPosition3];
                } else {
                    selectedItem3 = "";
                    selectedImageName3 = "";
                    selectedHiddenValue3 = 0;
                }
                int suma = selectedHiddenValue1 + selectedHiddenValue + selectedHiddenValue2 + selectedHiddenValue3;

                editor.putBoolean("checkboxForMouse", false);
                editor.putBoolean("checkboxForKeyboard", false);
                editor.putBoolean("checkboxForWebcam", false);
                editor.apply();
                editor.commit();
                checkCheckbox();
                if (!name.getText().toString().isEmpty() && !phonenumber.getText().toString().isEmpty()) {
                    try {
                        databaseHelper.insertItem(name.getText().toString(), phonenumber.getText().toString(),
                                selectedItem1, selectedImageName1,
                                selectedItem, selectedImageName,
                                selectedItem2, selectedImageName2,
                                selectedItem3, selectedImageName3, suma);
                    } catch (Exception e) {
                        Log.e("DatabaseInsert", "Error inserting item", e);
                    }

                    String nameValue = name.getText().toString();
                    String phoneNumberValue = phonenumber.getText().toString();
                    String sumaValue = String.valueOf(suma);
                    String message = "Zamówinie złożone przez: " + nameValue + "\n" +
                            "O godzinie: " + formattedDateTime + "\n" +
                            "O wartości: " + sumaValue + "\n" +
                            "Na Number tel.: " + phoneNumberValue;
                    showAlertDialog(message);
                    updateTextView();
                    phonenumber.setText("");
                    name.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Imie i numer telefonu nie mogą być puste!", Toast.LENGTH_SHORT).show();
                }
                Log.v("Godzinnna", formattedDateTime);

            }
        });

    }

    public void display(String[] name, String[] images, Spinner spinner) {
        int[] image = new int[images.length];
        for (int i = 0; i < images.length; i++) {
            image[i] = getResources().getIdentifier(images[i], "drawable", getPackageName());
        }
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, R.layout.spinner_item, name, image);
        spinner.setAdapter(adapter);
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Zamowienie zostało złożone");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendSmsManager(String text, String number, String data) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            if (!number.isEmpty() && !text.isEmpty()) {
                int suma = getKoszykPrice();
                String message = "Name: " + text + "\nCena: " + String.valueOf(suma) + "\nData: " + data;
                smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, message, null, null);
                Toast.makeText(this, "SMS sent", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Enter phone number and message:", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void setListeners(CheckBox checkBox, Spinner spinner) { checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            saveCheckBoxState(checkBox);
            updateTextView(); }
    });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                updateTextView();  }
            @Override public
            void onNothingSelected(AdapterView<?> parent) { }
        }); }
    private void updateTextView() {
        int total = getKoszykPrice();
        textView.setText(getResources().getString(R.string.welcome_message) + " " + total + "PLN"); }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private class AddDynamicShortcutTask extends AsyncTask<Void, Void, Void> {
        @Override protected Void doInBackground(Void... voids) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
             Intent intent1 = new Intent(MainActivity.this, MainActivity.class);
             intent1.setAction(Intent.ACTION_VIEW);
             ShortcutInfo shortcut1 = new ShortcutInfo.Builder(MainActivity.this, "dynamic_shortcut_id1") .setShortLabel("Złóż zamowienie") .setLongLabel("Złóż zamowienie") .setIcon(Icon.createWithResource(MainActivity.this, R.drawable.zamow)) .setIntent(intent1) .build();

             Intent intent2 = new Intent(MainActivity.this, ListaZamowien.class);
             intent2.setAction(Intent.ACTION_VIEW);
             ShortcutInfo shortcut2 = new ShortcutInfo.Builder(MainActivity.this, "dynamic_shortcut_id2") .setShortLabel("Lista zamówień") .setLongLabel("Lista zamówień") .setIcon(Icon.createWithResource(MainActivity.this, R.drawable.listazamowien)) .setIntent(intent2) .build();

             Intent intent3 = new Intent(MainActivity.this, Informacjeoautorze.class);
             intent3.setAction(Intent.ACTION_VIEW);
             ShortcutInfo shortcut3 = new ShortcutInfo.Builder(MainActivity.this, "dynamic_shortcut_id3") .setShortLabel("Informacje o autorze") .setLongLabel("Informacje o autorze") .setIcon(Icon.createWithResource(MainActivity.this, R.drawable.info)) .setIntent(intent3) .build();

            Intent intent4 = new Intent(MainActivity.this, MainActivity.class);
            intent4.setAction("com.example.sklep1.ACTION_SHARE_BASKET");
            ShortcutInfo shortcut4 = new ShortcutInfo.Builder(MainActivity.this, "dynamic_shortcut_id4") .setShortLabel("Udostępnij koszyk") .setLongLabel("Otwórz MainActivity i udostępnij koszyk") .setIcon(Icon.createWithResource(MainActivity.this, R.drawable.share)) .setIntent(intent4) .build();

            shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut1, shortcut2, shortcut3, shortcut4));
            return null; } }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && "com.example.sklep1.ACTION_SHARE_BASKET".equals(intent.getAction()))
        { udostepnijKoszyk(); } }
    private void udostepnijKoszyk(){
        String message = getkoszyk();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(intent, "Share using"));
    }

    public void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"adres@przyklad.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Informacje o zamowieniu");
        String message = getkoszyk();
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Wybierz aplikację do wysłania e-maila..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Nie znaleziono aplikacji do wysyłania e-maili.", Toast.LENGTH_SHORT).show();
        }
    }

    String getkoszyk(){
        String koszyk = "";
        koszyk += "Komputer: " + DeviceData.computers[spinner.getSelectedItemPosition()] + "\n";;
        if (checkboxForMouse.isChecked()) {
            koszyk += "Myszka: " + DeviceData.mouses[spinnerMouse.getSelectedItemPosition()] + "\n";
            //editor.putBoolean("checkbox1", true);
           // editor.putString("saved_text2", DeviceData.mouses[spinnerMouse.getSelectedItemPosition()]);
        }
        if (checkboxForKeyboard.isChecked()) {
            koszyk += "Klawiatura: " + DeviceData.keyboards[spinnerKeyboard.getSelectedItemPosition()] + "\n";}
        if (checkboxForWebcam.isChecked()) {
            koszyk += "Kamerka: " + DeviceData.webcams[spinnerWebcam.getSelectedItemPosition()] + "\n"; }
        return koszyk;
    }

    String getDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(calendar.getTime());
    }

    int getKoszykPrice(){
        int total = 0; int selectedHiddenValue1 = DeviceData.computersValues[spinner.getSelectedItemPosition()]; total += selectedHiddenValue1;
        if (checkboxForMouse.isChecked()) {
            total += DeviceData.mousesValues[spinnerMouse.getSelectedItemPosition()]; }
        if (checkboxForKeyboard.isChecked()) {
            total += DeviceData.keyboardsValues[spinnerKeyboard.getSelectedItemPosition()]; }
        if (checkboxForWebcam.isChecked()) {
            total += DeviceData.webcamsValues[spinnerWebcam.getSelectedItemPosition()]; }
        return  total;
    }

    private void saveCheckBoxState(CheckBox checkBox) {
        String checkBoxName = getResources().getResourceEntryName(checkBox.getId());
        boolean isChecked = checkBox.isChecked();
        editor.putBoolean(checkBoxName, isChecked);
        editor.apply();
        editor.commit();
        ;
    }

    private void checkCheckbox(){
        if(loadCheckBoxState("checkboxForMouse")){
            checkboxForMouse.setChecked(true);
        }
        else {
            checkboxForMouse.setChecked(false);
        }

        if(loadCheckBoxState("checkboxForKeyboard")){
            checkboxForKeyboard.setChecked(true);
        }
        else{
            checkboxForKeyboard.setChecked(false);
        }

        if(loadCheckBoxState("checkboxForWebcam")){
            checkboxForWebcam.setChecked(true);
        }
        else{
            checkboxForWebcam.setChecked(false);
        }

    }

    private boolean loadCheckBoxState(String checkBoxName) {
        boolean isChecked = sharedPreferences.getBoolean(checkBoxName, false);
        return isChecked; }

}