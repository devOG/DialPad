package com.example.dialpad;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.File;

import static com.example.dialpad.DownloadSound.DOWNLOADED_VOICES;

// ButtonSound class creates a SoundPool object and
// plays a specific sound when a button is clicked
public class ButtonSound {

    SoundPool sp;

    private int one;
    private int two;
    private int three;
    private int four;
    private int five;
    private int six;
    private int seven;
    private int eight;
    private int nine;
    private int zero;
    private int asterisc;
    private int hashtag;

    public ButtonSound(Context context) {
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

        one = sp.load(getFullFilePath(context, "one.mp3"), 1);
        two = sp.load(getFullFilePath(context, "two.mp3"), 1);
        three = sp.load(getFullFilePath(context, "three.mp3"), 1);
        four = sp.load(getFullFilePath(context, "four.mp3"), 1);
        five = sp.load(getFullFilePath(context, "five.mp3"), 1);
        six = sp.load(getFullFilePath(context, "six.mp3"), 1);
        seven = sp.load(getFullFilePath(context, "seven.mp3"), 1);
        eight = sp.load(getFullFilePath(context, "eight.mp3"), 1);
        nine = sp.load(getFullFilePath(context, "nine.mp3"), 1);
        zero = sp.load(getFullFilePath(context, "zero.mp3"), 1);
        asterisc = sp.load(getFullFilePath(context, "star.mp3"), 1);
        hashtag = sp.load(getFullFilePath(context, "pound.mp3"), 1);
    }


    private String getFullFilePath(Context context, String filename) {

        // Get correct voice file from shared pref
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String directory = sharedPref.getString(context.getResources().getString(R.string.choose_voice_key), null);

        // Create file
        File file = new File(directory + File.separator + filename);

        /*if (!file.canRead()) {
            Toast.makeText(context, "Couldn't read sound files from memory card", Toast.LENGTH_SHORT).show();
        }*/

        // Return its absolute path
        return file.getAbsolutePath();
    }

    public void playSound(int soundId) {

        switch (soundId) {
            case 1: sp.play(one, 1, 1, 0, 0, 1);
                break;
            case 2: sp.play(two, 1, 1, 0, 0, 1);
                break;
            case 3: sp.play(three, 1, 1, 0, 0, 1);
                break;
            case 4: sp.play(four, 1, 1, 0, 0, 1);
                break;
            case 5: sp.play(five, 1, 1, 0, 0, 1);
                break;
            case 6: sp.play(six, 1, 1, 0, 0, 1);
                break;
            case 7: sp.play(seven, 1, 1, 0, 0, 1);
                break;
            case 8: sp.play(eight, 1, 1, 0, 0, 1);
                break;
            case 9: sp.play(nine, 1, 1, 0, 0, 1);
                break;
            case 10: sp.play(zero, 1, 1, 0, 0, 1);
                break;
            case 11: sp.play(asterisc, 1, 1, 0, 0, 1);
                break;
            case 12: sp.play(hashtag, 1, 1, 0, 0, 1);
                break;
            default:
                break;
        }
    }

    public void releaseRes() {
        sp.release();
        sp = null;
    }

}
