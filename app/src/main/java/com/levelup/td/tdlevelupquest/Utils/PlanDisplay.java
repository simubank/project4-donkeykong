package com.levelup.td.tdlevelupquest.Utils;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.levelup.td.tdlevelupquest.R;

public class PlanDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_display);



        //intent.putExtra("PLAN_TYPE", "Electronics");
        //intent.putExtra("TOP_COST", "486.69");
        //intent.putExtra("TOTAL_COST", "602.44");

        String plan = getIntent().getStringExtra("PLAN_TYPE");
        String topcost = getIntent().getStringExtra("TOP_COST");
        String totalcost = getIntent().getStringExtra("TOTAL_COST");
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
    }

}
