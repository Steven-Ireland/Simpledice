package net.commote.scientaster.simpledice;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

import java.util.HashMap;


public class DieRoller extends Activity {

    private boolean rollDone = true;
    private long lastPress = System.currentTimeMillis();
    private HashMap<Integer, View> counts = new HashMap<Integer, View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_die_roller);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_die_roller, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_die_roller, container, false);
            return rootView;
        }
    }

    public void onClick(View v) {
        TextView total = (TextView) findViewById(R.id.roll);
        TextView BonusText = (TextView) findViewById(R.id.bonus);
        int sides = Integer.parseInt(((Button)v).getText().toString().replaceAll("[^\\d.]", ""));
        findViewById(R.id.d4count);
        int bonus = ((int)(Math.random()*sides)+1);


        if (System.currentTimeMillis() - lastPress > 3000) {
            rollDone = true;
            lastPress = System.currentTimeMillis();
        }
        else {
            rollDone = false;
            lastPress = System.currentTimeMillis();
        }

        BonusText.setText("+"+bonus);
        if (rollDone) {
            total.setText(""+bonus);
        }
        else {
            int roll = Integer.parseInt(total.getText().toString());
            total.setText(""+(roll+bonus));
        }
    }
}
