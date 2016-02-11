package com.uoft.journey.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.uoft.journey.R;

/**
 * Created by jenna on 16-01-19.
 */
public class AddNewAssessmentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "AddNewAssess";
    private Spinner spinner;
    private int iCurrentSelection;
    private Button startButton;
    private int isClicked = 1;
    private TextSwitcher mTextSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_new_assessment);

        spinner = (Spinner) findViewById(R.id.assessments_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.assessments_array, android.R.layout.preference_category);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
        textSwitcher();

        startButton = (Button) findViewById(R.id.button_start_assessment);


    }



    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        iCurrentSelection = spinner.getSelectedItemPosition();
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (iCurrentSelection == 0 && isClicked == 1) {
                    mTextSwitcher.setText("This is where the instructions for setting up the accelerometer will go.");
                    // Only let the user click once
                    startButton.setEnabled(false);
                    isClicked = 0;
                }

                else {
                    mTextSwitcher.setText("");
                }

            }
        });

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void textSwitcher() {

        mTextSwitcher = (TextSwitcher)findViewById(R.id.text_switcher_instructions);
        mTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView instructionsText = new TextView(AddNewAssessmentActivity.this);
                instructionsText.setTextSize(16);
                return instructionsText;
            }
        });

        // Declare the in and out animations and initialize them
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

        mTextSwitcher.setInAnimation(in);

    }


}
