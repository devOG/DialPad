package com.example.dialpad;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.dialpad.DownloadSound.DOWNLOADED_VOICES;

public class Settings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        ListPreference list = (ListPreference) findPreference(getResources().getString(R.string.choose_voice_key));

        getDownloadedVoices(list);
    }

    private void getDownloadedVoices(ListPreference list) {

        // Get all values from DOWNLOADED_VOICES
        SharedPreferences sharedPref = getSharedPreferences(DOWNLOADED_VOICES, 0);
        Map<String,?> voices = sharedPref.getAll();

        List<CharSequence> entries = new ArrayList<>();
        List<CharSequence> entryValues = new ArrayList<>();

        // Loop through and append to array lists
        for(Map.Entry<String,?> entry : voices.entrySet()){
            entries.add(entry.getKey().toString());
            entryValues.add(entry.getValue().toString());
        }

        CharSequence[] entriesArray = entries.toArray(new CharSequence[entries.size()]);
        CharSequence[] entryValuesArray = entryValues.toArray(new CharSequence[entryValues.size()]);

        list.setEntries(entriesArray);
        list.setEntryValues(entryValuesArray);
    }

}