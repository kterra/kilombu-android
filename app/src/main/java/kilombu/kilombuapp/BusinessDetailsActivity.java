package kilombu.kilombuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class BusinessDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        setupBusinessDetails();
       // String toolbarTitle = setupBusinessDetails();
       // getSupportActionBar().setTitle(toolbarTitle);

    }


    public String setupBusinessDetails(){
        Intent intent = getIntent();
        TextView currentText = (TextView) findViewById(R.id.business_name_detail);
        String businessName = intent.getStringExtra("business_name");
        currentText.setText(intent.getStringExtra("business_name"));


        currentText = (TextView) findViewById(R.id.business_description_detail);
        currentText.setText(intent.getStringExtra("business_description"));

        currentText = (TextView) findViewById(R.id.business_address);
        currentText.setText("Rua das Coves, 145");

        currentText = (TextView) findViewById(R.id.business_phone_number);
        currentText.setText("Tel: 021 3234-5678");

        currentText = (TextView) findViewById(R.id.business_working_hours);
        currentText.setText("Segunda a Sexta: 08:00 — 12:00");

        /**Must setup the images so they can send an intent for each app**/

        return businessName;
    }
}
