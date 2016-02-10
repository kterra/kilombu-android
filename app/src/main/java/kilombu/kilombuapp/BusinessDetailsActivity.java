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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        Intent intent = getIntent();
        Business chosenBusiness = intent.getex

    }


    public void setupBusinessDetails(Business chosenBusiness){
        TextView currentText = (TextView) findViewById(R.id.business_name_detail);
        currentText.setText(chosenBusiness.getName());

        currentText = (TextView) findViewById(R.id.business_category_detail);
        currentText.setText(chosenBusiness.getCategory());

        currentText = (TextView) findViewById(R.id.business_description_detail);
        currentText.setText(chosenBusiness.getDescription());

        currentText = (TextView) findViewById(R.id.business_address);
        currentText.setText(chosenBusiness.getAddress());

        currentText = (TextView) findViewById(R.id.business_working_hours);
        currentText.setText(chosenBusiness.getBusiness_hours());

        /**Must setup the images so they can send an intent for each app**/
    }
}
