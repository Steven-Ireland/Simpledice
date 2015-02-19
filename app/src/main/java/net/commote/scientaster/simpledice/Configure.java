package net.commote.scientaster.simpledice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;


public class Configure extends Activity {

    private EditText editor;
    private Button currentlyEditing;
    private ArrayList<Button> upperVals;
    private ArrayList<Button> lowerVals;


    private class Updater implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (currentlyEditing!=null) {
                currentlyEditing.setText("d"+editor.getText());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        Intent intent = getIntent();
        String dice = intent.getStringExtra("dice");
        int selected = intent.getIntExtra("selected", -1);

        upperVals = new ArrayList<Button>();
        lowerVals = new ArrayList<Button>();

        initializeUI(dice);
    }

    public void initializeUI(String diee) {
        LinearLayout die = (LinearLayout)findViewById(R.id.configdie);
        LinearLayout upperdie = (LinearLayout)findViewById(R.id.configupperdie);
        editor = (EditText) findViewById(R.id.input);
        editor.addTextChangedListener(new Updater());
        editor.setInputType(InputType.TYPE_NULL);

        die.removeAllViews();
        upperdie.removeAllViews();

        String lower = diee.split("-")[1];
        String upper = diee.split("-")[0];

        for (String dice : upper.split(" ")) {

            Button b;
            if (dice.equals("d0")) {
                b = (Button) getLayoutInflater().inflate(R.layout.whitespace, upperdie, false);
                upperdie.addView(b);
            } else {
                b = (Button) getLayoutInflater().inflate(R.layout.diebutton, upperdie, false);
                b.setText(dice);
                upperdie.addView(b);
            }
            upperVals.add(b);
        }
        for (String dice : lower.split(" ")) {
            Button b;
            if (dice.equals("d0")) {
                b = (Button) getLayoutInflater().inflate(R.layout.whitespace, die, false);
                die.addView(b);
            }
            else {
                b = (Button) getLayoutInflater().inflate(R.layout.diebutton, die, false);
                b.setText(dice);
                die.addView(b);
            }
            lowerVals.add(b);
        }
    }

    public void onClick(View v) {
        if (currentlyEditing!=null)
            currentlyEditing.setBackgroundColor(getResources().getColor(R.color.dark));
        currentlyEditing = (Button) v;
        editor.setInputType(InputType.TYPE_CLASS_NUMBER);
        v.setBackgroundColor(getResources().getColor(R.color.light));
        int sides = Integer.parseInt(currentlyEditing.getText().toString().replaceAll("[^\\d.]", ""));
        editor.setText(""+sides);
    }

    public void submitChanges(View v) {
        File file = new File(getFilesDir(), "die.dl");
        String newSave = "";
        for (Button die : upperVals) {
            String val = die.getText().toString();
            if (val.length() <= 1) val = "d0";
            newSave = newSave + val + " ";
        }
        newSave = newSave.substring(0, newSave.length() - 1); // cutoff " "
        newSave += "-";
        for (Button die : lowerVals) {
            String val = die.getText().toString();
            if (val.length() <= 1) val = "d0";
            newSave = newSave + val + " ";
        }
        newSave = newSave.substring(0, newSave.length() - 1); // cutoff " "
        System.out.println(newSave);
        try {

            FileWriter fw = new FileWriter(file);
            fw.write(newSave);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent i = new Intent(getApplicationContext(), DieRoller.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();

    }
}
