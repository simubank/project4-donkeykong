package com.levelup.td.tdlevelupquest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CreateGoalsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_goals);

        LaunchActivity.initDrawer(CreateGoalsActivity.this,this);
    }
}
