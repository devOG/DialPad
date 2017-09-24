package com.example.dialpad;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

// Custom component for a dial pad
public class DialPad extends LinearLayout {

    private EditText numbTextField;

    private Button delete;
    private Button call;

    private Button one, two, three, four, five, six, seven, eight, nine, zero, asterisc, hashtag;

    private ButtonSound buttonSound; // handles sound when a button is pressed

    public DialPad(Context context, Boolean pSound) {
        super(context);
    }

    public DialPad(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Inflate xml file for the component
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.dial_pad, this);

        instantiateButtonSound();

        numbTextField = (EditText) findViewById(R.id.numbTextField);

        setUpButtons();

        this.requestFocus();
        this.setFocusableInTouchMode(true);
    }

    public void instantiateButtonSound() {
        buttonSound = new ButtonSound(getContext());
    }

    private void setUpButtons() {
        delete = (Button) findViewById(R.id.deleteButton);
        call = (Button) findViewById(R.id.callButton);

        one = (Button) findViewById(R.id.one);
        two = (Button) findViewById(R.id.two);
        three = (Button) findViewById(R.id.three);
        four = (Button) findViewById(R.id.four);
        five = (Button) findViewById(R.id.five);
        six = (Button) findViewById(R.id.six);
        seven = (Button) findViewById(R.id.seven);
        eight = (Button) findViewById(R.id.eight);
        nine = (Button) findViewById(R.id.nine);
        zero = (Button) findViewById(R.id.zero);
        asterisc = (Button) findViewById(R.id.asterisc);
        hashtag = (Button) findViewById(R.id.hashtag);

        delete.setOnClickListener(delButtonPressed);
        delete.setOnLongClickListener(delButtonLongPressed);

        one.setOnClickListener(buttonPressed);
        two.setOnClickListener(buttonPressed);
        three.setOnClickListener(buttonPressed);
        four.setOnClickListener(buttonPressed);
        five.setOnClickListener(buttonPressed);
        six.setOnClickListener(buttonPressed);
        seven.setOnClickListener(buttonPressed);
        eight.setOnClickListener(buttonPressed);
        nine.setOnClickListener(buttonPressed);
        zero.setOnClickListener(buttonPressed);
        asterisc.setOnClickListener(buttonPressed);
        hashtag.setOnClickListener(buttonPressed);
    }

    private void handleButtonPressed(View v) {
        int color;
        Button tmpButton = (Button) findViewById(v.getId());

        // Get current text color of the button that was clicked and change "color" variable
        if (tmpButton.getCurrentTextColor() == getResources().getColor(R.color.colorUnclicked)) {
            color = getResources().getColor(R.color.colorClicked);
        } else {
            color = getResources().getColor(R.color.colorUnclicked);
        }

        String textField = numbTextField.getText().toString();
        // Change text color of the button that was clicked and play corresponding sound
        switch (v.getId()) {
            case R.id.one:
                one.setTextColor(color);
                buttonSound.playSound(1);
                numbTextField.setText(textField + getResources().getString(R.string.one));
                break;
            case R.id.two:
                two.setTextColor(color);
                buttonSound.playSound(2);
                numbTextField.setText(textField + getResources().getString(R.string.two));
                break;
            case R.id.three:
                three.setTextColor(color);
                buttonSound.playSound(3);
                numbTextField.setText(textField + getResources().getString(R.string.three));
                break;
            case R.id.four:
                four.setTextColor(color);
                buttonSound.playSound(4);
                numbTextField.setText(textField + getResources().getString(R.string.four));
                break;
            case R.id.five:
                five.setTextColor(color);
                buttonSound.playSound(5);
                numbTextField.setText(textField + getResources().getString(R.string.five));
                break;
            case R.id.six:
                six.setTextColor(color);
                buttonSound.playSound(6);
                numbTextField.setText(textField + getResources().getString(R.string.six));
                break;
            case R.id.seven:
                seven.setTextColor(color);
                buttonSound.playSound(7);
                numbTextField.setText(textField + getResources().getString(R.string.seven));
                break;
            case R.id.eight:
                eight.setTextColor(color);
                buttonSound.playSound(8);
                numbTextField.setText(textField + getResources().getString(R.string.eight));
                break;
            case R.id.nine:
                nine.setTextColor(color);
                buttonSound.playSound(9);
                numbTextField.setText(textField + getResources().getString(R.string.nine));
                break;
            case R.id.zero:
                zero.setTextColor(color);
                buttonSound.playSound(10);
                numbTextField.setText(textField + getResources().getString(R.string.zero));
                break;
            case R.id.asterisc:
                asterisc.setTextColor(color);
                buttonSound.playSound(11);
                numbTextField.setText(textField + getResources().getString(R.string.asterisc));
                break;
            case R.id.hashtag:
                hashtag.setTextColor(color);
                buttonSound.playSound(12);
                numbTextField.setText(textField + getResources().getString(R.string.hashtag));
                break;
            default:
                break;
        }
    }

    View.OnClickListener buttonPressed = new View.OnClickListener() {
        public void onClick(View v) {
            handleButtonPressed(v);
        }
    };

    View.OnClickListener delButtonPressed = new View.OnClickListener() {
        public void onClick(View v) {

            // Get current text in the text field
            String currentText = numbTextField.getText().toString();

            // If empty, there is nothing to delete
            if (currentText.isEmpty()) {
                return;
            }

            // Turn string into array of char
            char[] charArray = currentText.toCharArray();

            // Remove last element
            charArray = Arrays.copyOf(charArray, charArray.length - 1);

            // Update text field
            numbTextField.setText(String.valueOf(charArray));
        }
    };

    View.OnLongClickListener delButtonLongPressed = new View.OnLongClickListener() {
        public boolean onLongClick(View v) {
            numbTextField.setText("");
            return true;
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        //Toast.makeText(getContext(), String.valueOf(keyCode),Toast.LENGTH_SHORT).show();

        Button buttonPressed;

        // tried to use a switch, but for some reason it only worked with 1 case
        if (keyCode == KeyEvent.KEYCODE_0) {
            buttonPressed = (Button) findViewById(R.id.zero);
            handleButtonPressed(buttonPressed);
            return  true;
        } else if (keyCode == KeyEvent.KEYCODE_1) {
            buttonPressed = (Button) findViewById(R.id.one);
            handleButtonPressed(buttonPressed);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_2) {
            buttonPressed = (Button) findViewById(R.id.two);
            handleButtonPressed(buttonPressed);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_3) {
            buttonPressed = (Button) findViewById(R.id.three);
            handleButtonPressed(buttonPressed);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_4) {
            buttonPressed = (Button) findViewById(R.id.four);
            handleButtonPressed(buttonPressed);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_5) {
            buttonPressed = (Button) findViewById(R.id.five);
            handleButtonPressed(buttonPressed);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_6) {
            buttonPressed = (Button) findViewById(R.id.six);
            handleButtonPressed(buttonPressed);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_7) {
            buttonPressed = (Button) findViewById(R.id.seven);
            handleButtonPressed(buttonPressed);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_8) {
            buttonPressed = (Button) findViewById(R.id.eight);
            handleButtonPressed(buttonPressed);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_9) {
            buttonPressed = (Button) findViewById(R.id.nine);
            handleButtonPressed(buttonPressed);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_STAR) {
            buttonPressed = (Button) findViewById(R.id.asterisc);
            handleButtonPressed(buttonPressed);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_POUND) {
            buttonPressed = (Button) findViewById(R.id.hashtag);
            handleButtonPressed(buttonPressed);
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    public void releaseRes() {
        buttonSound.releaseRes();;
    }

}
