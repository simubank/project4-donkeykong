package com.levelup.td.tdlevelupquest.Utils;

import android.content.res.Resources;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import com.levelup.td.tdlevelupquest.AnalyzeSpendingActivity;
import com.levelup.td.tdlevelupquest.InvestActivity;
import com.levelup.td.tdlevelupquest.LaunchActivity;
import com.levelup.td.tdlevelupquest.MainScreenActivity;
import com.levelup.td.tdlevelupquest.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class PlanDisplay extends AppCompatActivity {

    Button b1;
    Button b2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_display);
        LaunchActivity.initDrawer(PlanDisplay.this,this);

        b1 = findViewById(R.id.pickerButtonSaveNow);

        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AnalyzeSpendingActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        b2 = findViewById(R.id.pickerButtonInvest);

        b2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), InvestActivity
                        .class);
                view.getContext().startActivity(intent);
            }
        });



        //intent.putExtra("PLAN_TYPE", "Electronics");
        //intent.putExtra("TOP_COST", "486.69");
        //intent.putExtra("TOTAL_COST", "602.44");

        String plan = getIntent().getStringExtra("PLAN_TYPE");
        String topcost = getIntent().getStringExtra("TOP_COST");
        String totalcost = getIntent().getStringExtra("TOTAL_COST");
        String balance = getIntent().getStringExtra("BALANCE");
        String balanceAfterCost = getIntent().getStringExtra("BALANCE_AFTER_COST");
        String itemname = getIntent().getStringExtra("ITEM_NAME");

        Resources res = getResources();

        String text1 = String.format(res.getString(R.string.title_savings_plan), plan);
        TextView plan_name = findViewById(R.id.plantitle);
        plan_name.setText(text1);

        //////////////////////////////

        String text2 = String.format(res.getString(R.string.dollar_amount_1), topcost);
        TextView top_item = findViewById(R.id.top_item_amount);
        top_item.setText(text2);

        ///////////////////////////////

        String text3 = String.format(res.getString(R.string.dollar_amount_3), totalcost);
        TextView list_cost = findViewById(R.id.total_balance_amount);
        list_cost.setText(text3);

        /////////////////////////////

        String text4 = String.format(res.getString(R.string.item_get_now), itemname);
        TextView item_name = findViewById(R.id.item_get_now_title);
        item_name.setText(text4);

        ////////////////////////////
        String text5 = String.format(res.getString(R.string.item_get_save), itemname);
        TextView item_name_save = findViewById(R.id.item_get_save);
        item_name_save.setText(text5);

        ////////////////////////////
        String text6 = String.format(res.getString(R.string.dollar_amount_5), balance);
        TextView current_balance_amount = findViewById(R.id.current_balance_amount);
        current_balance_amount.setText(text6);

        ////////////////////////////
        String text7 = String.format(res.getString(R.string.dollar_amount_6), balanceAfterCost);
        TextView balance_after_amount = findViewById(R.id.balance_after_amount);
        balance_after_amount.setText(text7);
    }
}
