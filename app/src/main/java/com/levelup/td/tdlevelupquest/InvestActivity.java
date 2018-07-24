package com.levelup.td.tdlevelupquest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.alespero.expandablecardview.ExpandableCardView;

public class InvestActivity extends AppCompatActivity {

    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest);

        LaunchActivity.initDrawer(InvestActivity.this, this);

        b1 = findViewById(R.id.bookappointment);

        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),WebViewActivity.class);
                startActivity(intent);
            }
        });



    }
}


