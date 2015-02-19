package net.commote.scientaster.simpledice;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Timer;
import java.util.TimerTask;


public class DieRoller extends Activity {

    private boolean rollDone = true;
    private long lastPress = System.currentTimeMillis();
    private HashMap<Integer, View> counts = new HashMap<Integer, View>();
    private HashMap<View, Integer> vals = new HashMap<View, Integer>();
    private Animation fadeOut;
    private Animation fadeHalf;
    private Animation fadeIn;
    private Timer rollFader;
    private TextView total;
    private TextView BonusText;
    private String save;

    private class FadeTimer extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                   endRolls();
                }
            });

        }
    };

    private class Configurator implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View v) {
            Intent i = new Intent(getApplicationContext(), Configure.class);
            i.putExtra("dice", save);
            i.putExtra("selected", ((Button) v).getText());
            startActivity(i);
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.activity_die_roller, null);


        setContentView(v);
        initialize();
        loadAnimations();
        loadDie();
    }

    public void onClick(View v) {
        int sides = Integer.parseInt(((Button) v).getText().toString().replaceAll("[^\\d.]", ""));
        int bonus = ((int) (Math.random() * sides) + 1);

        BonusText.setText("+" + bonus);
        if (rollDone) {
            total.setText("" + bonus);
        } else {
            int roll = Integer.parseInt(total.getText().toString());
            total.setText("" + (roll + bonus));
        }
        addCounter(sides);
    }

    public void onTotalClick(View v) {
        if (rollDone) {
            wakeUp();
            restoreRolls();
        }
        else {
            clearTimer();
            endRolls();
        }
    }

    public void loadAnimations() {
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        fadeIn  = AnimationUtils.loadAnimation(this, R.anim.fadein);
        fadeHalf= AnimationUtils.loadAnimation(this, R.anim.fadetofifty);

    }

    public void wakeUp() {

        total.startAnimation(fadeIn);
        BonusText.startAnimation(fadeIn);

        clearTimer();
        rollDone = false;
        rollFader.schedule(new FadeTimer(), 5000);
    }

    public void addCounter(int side) {
        wakeUp();
        Button counter = (Button)counts.get(side);
        if (counter.getText().toString().isEmpty()) {
            counter.startAnimation(fadeIn);
            counter.setText("1");
        } else {
            counter.setText("" + (Integer.parseInt(counter.getText().toString())+1));
        }
    }

    public void clearTimer() {
        rollFader.cancel();
        rollFader.purge();
        rollFader = new Timer();
    }

    public void setText(Button b, String text) {
        b.setText(text);
    }

    public void loadDie() {
        // Load die save file
        String def = "d12 d0 d0 d20-d4 d6 d8 d10";
        save = "";
        File file = new File(getFilesDir(), "die.dl");
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            save = br.readLine();
            if (save.isEmpty()) throw new Exception("Making File");

        }
        catch(Exception e) {
            try {
                FileWriter fw = new FileWriter(file);
                fw.write(def);
                fw.flush();
                fw.close();
                save = def;
            }
            catch (Exception e2) {
                e.printStackTrace();
            }

        }

        LinearLayout die = (LinearLayout)findViewById(R.id.die);
        LinearLayout upperdie = (LinearLayout)findViewById(R.id.upperdie);
        LinearLayout tally = (LinearLayout)findViewById(R.id.count);
        LinearLayout uppertally = (LinearLayout)findViewById(R.id.uppercount);

        tally.removeAllViews();
        uppertally.removeAllViews();
        die.removeAllViews();
        upperdie.removeAllViews();

        String lower = save.split("-")[1];
        String upper = save.split("-")[0];

        for (String dice : upper.split(" ")) {
            if (dice.equals("d0")) {
                Button b = (Button) getLayoutInflater().inflate(R.layout.whitespace, upperdie, false);
                Button c = (Button) getLayoutInflater().inflate(R.layout.whitespace, uppertally, false);
                upperdie.addView(b);
                uppertally.addView(c);
            } else {
                Button b = (Button) getLayoutInflater().inflate(R.layout.diebutton, upperdie, false);
                b.setOnLongClickListener(new Configurator());
                Button c = (Button) getLayoutInflater().inflate(R.layout.countbutton, uppertally, false);
                b.setText(dice);
                upperdie.addView(b);
                uppertally.addView(c);
                c.setText("0");
                c.startAnimation(fadeOut);
                int sides = Integer.parseInt(b.getText().toString().replaceAll("[^\\d.]", ""));
                counts.put(sides, c);
                vals.put(c, 0);
            }
        }
        for (String dice : lower.split(" ")) {
            if (dice.equals("d0")) {
                Button b = (Button) getLayoutInflater().inflate(R.layout.whitespace, die, false);
                Button c = (Button) getLayoutInflater().inflate(R.layout.whitespace, tally, false);
                die.addView(b);
                tally.addView(c);
            }
            else {
                Button b = (Button) getLayoutInflater().inflate(R.layout.diebutton, die, false);
                b.setOnLongClickListener(new Configurator());
                Button c = (Button) getLayoutInflater().inflate(R.layout.countbutton, tally, false);
                b.setText(dice);
                die.addView(b);
                tally.addView(c);
                c.setText("0");
                c.startAnimation(fadeOut);
                int sides = Integer.parseInt(b.getText().toString().replaceAll("[^\\d.]", ""));
                counts.put(sides, c);
                vals.put(c, 0);
            }
        }
    }

    public void endRolls() {
        rollDone = true;

        for (View v : counts.values()) {
            Button count = ((Button) v);
            int num = Integer.parseInt(count.getText().toString());
            vals.put(v, num);
            if (count.getText().toString().length() > 0) { // if you need to clear it
                count.startAnimation(fadeOut);
            }
            setText(count, "0");
        }

        total.startAnimation(fadeHalf);
        BonusText.startAnimation(fadeHalf);
    }

    public void initialize() {
        rollFader = new Timer();
        total = (TextView) findViewById(R.id.roll);
        BonusText = (TextView) findViewById(R.id.bonus);
    }

    public void restoreRolls() {
        for (View v : counts.values()) {
            Button count = ((Button) v);
            if (vals.get(v) > 0) {
                v.startAnimation(fadeIn);
                count.setText(""+vals.get(v));
            }
        }
    }
}
