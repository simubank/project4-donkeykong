package com.levelup.td.tdlevelupquest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alespero.expandablecardview.ExpandableCardView;

public class InvestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest);

        LaunchActivity.initDrawer(InvestActivity.this, this);

        ExpandableCardView card = findViewById(R.id.profile);
    }
}


