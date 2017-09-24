package com.example.dialpad;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import static com.example.dialpad.R.id.numbTextField;

public class MainActivity extends AppCompatActivity {

    public static final String CALL_LIST = "CallList"; // preferences file
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check if external memory is available.
        // If not, there's no reason to ask the user for permission
        if (isExternalStorageWritable()) {
            requestReadExternalPermission();
        } else {
            Toast.makeText(this, "External memory not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        /* Since SoundPool resources are released when the activity
           call onStop(), the app will crash when we return to main
           activity. Therefor we have to instantiate the SoundPool
           object again.
        */
        DialPad dp = (DialPad) findViewById(R.id.dialPad);
        dp.instantiateButtonSound();
    }

    // Checks if external storage is available for read and write
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // Checks if external storage is available to at least read
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void requestReadExternalPermission() {
        // Check if permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Show explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Toast.makeText(this, "Permission is needed to play sound", Toast.LENGTH_SHORT).show();
            } else {
                // Request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Did the user granted permission or not?
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Make call
                    makeCall();
                } else {
                    Toast.makeText(this, "Permission to make calls was not granted!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate menu in toolbar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Menu actions
        switch (item.getItemId()) {
            case R.id.showCallList:
                showCallList();
                return true;
            case R.id.showDownloadSound:
                showDownloadSound();
                return true;
            case R.id.saveToCallList:
                saveToCallList();
                return true;
            case R.id.settings:
                showSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showCallList() {
        // Go to CallList activity
        Intent intent = new Intent(this, CallList.class);
        this.startActivity(intent);
    }

    private void showDownloadSound() {

        String dialPadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + "dialpad" + File.separator;
        String soundsDirPath = dialPadPath + "sounds" + File.separator;

        // Create directories if they do not exists
        File dialPadDirectory = new File(dialPadPath);
        File soundsDirectory = new File(soundsDirPath);
        if (!dialPadDirectory.exists()) {
            dialPadDirectory.mkdir();
        } else if (!soundsDirectory.exists()) {
            soundsDirectory.mkdir();
        }

        // Go to Download Sound activity
        Intent intent = new Intent(this, DownloadSound.class);
        intent.putExtra("urlAddress", this.getResources().getString(R.string.url_address));
        intent.putExtra("destinationDir", soundsDirPath);
        this.startActivity(intent);
    }

    private void saveToCallList() {
        EditText numbTextField = (EditText) findViewById(R.id.numbTextField);

        // If the text field is empty, return
        if (numbTextField.getText().toString().isEmpty()) {
            return;
        }

        // Get SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences(CALL_LIST, 0);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Generate random key to not overwrite existing preferences
        String randomKey = UUID.randomUUID().toString();

        // Put number from text field in shared preferences
        editor.putString(randomKey, numbTextField.getText().toString());
        editor.commit(); // save
        Toast.makeText(this, "Number saved...", Toast.LENGTH_SHORT).show();
    }

    private void showSettings() {
        // Go to Settings activity
        Intent intent = new Intent(this, Settings.class);
        this.startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Release SoundPool resources
        DialPad dp = (DialPad) findViewById(R.id.dialPad);
        dp.releaseRes();
    }

    public void callButtonClicked(View v) {
        makeCall();
    }

    private void makeCall() {

        // Get number from text field
        EditText textField = (EditText) findViewById(R.id.numbTextField);
        String telNumb = textField.getText().toString();

        // If empty, there is no number to call
        if (telNumb.isEmpty()) {
            Toast.makeText(this, "Enter a number to call", Toast.LENGTH_SHORT).show();
            return;
        }

        // Encode to handle # sign
        try {
            telNumb = URLEncoder.encode(telNumb, "utf-8");
        } catch (UnsupportedEncodingException e) {
        }

        // Check for call permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            // Show explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                Toast.makeText(this, "Permission is needed to make calls.", Toast.LENGTH_SHORT).show();
            } else {
                // Request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 2);
            }
        } else {
            // Start calling
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", telNumb, null));
            startActivity(intent);
        }
    }

}
