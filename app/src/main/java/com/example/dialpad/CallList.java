package com.example.dialpad;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.Map;

import static com.example.dialpad.MainActivity.CALL_LIST;

public class CallList extends AppCompatActivity {

    EditText callList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        callList = (EditText) findViewById(R.id.callList);
        getCallList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate call_list_menu in toolbar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.call_list_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Menu action
        switch (item.getItemId()) {
            case R.id.clearCallList:
                clearCallList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearCallList() {

        // Get SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences(CALL_LIST, 0);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Clear all data
        editor.clear();
        editor.commit();

        // Set callList to empty string for instant update
        callList.setText("");
    }

    private void getCallList() {

        // Get all values from CALL_LIST
        SharedPreferences sharedPref = getSharedPreferences(CALL_LIST, 0);
        Map<String,?> numbers = sharedPref.getAll();

        // Loop through and append to callList (view that lists all numbers)
        for(Map.Entry<String,?> entry : numbers.entrySet()){
            callList.append(entry.getValue().toString() + "\n");
        }
    }
}
