package com.levelup.td.tdlevelupquest.Utils;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.levelup.td.tdlevelupquest.LaunchActivity;
import com.levelup.td.tdlevelupquest.R;

public class AccountInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        LaunchActivity.initDrawer(AccountInfo.this,this);

    }

}
