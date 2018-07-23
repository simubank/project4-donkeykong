package com.levelup.td.tdlevelupquest;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

import com.levelup.td.tdlevelupquest.Utils.PlanDisplay;

public class card_display extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_display);

        LaunchActivity.initDrawer(card_display.this, this );


        View view1 = findViewById(R.id.card_view_food);
        View view2 = findViewById(R.id.card_view_electronics);
        View view3 = findViewById(R.id.card_view_coffee);
        View view4 = findViewById(R.id.card_view_ride);

        //Food Savings Below
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PlanDisplay.class);


                Resources res = getResources();
                String text = String.format(res.getString(R.string.title_savings_plan), "Electronics");


                intent.putExtra("PLAN_TYPE", "Electronics");
                intent.putExtra("TOP_COST", "486.69");
                intent.putExtra("TOTAL_COST", "602.44");


                startActivity(intent);
                finish();
            }
        });

        //Electronic Savings Below
        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PlanDisplay.class);
                startActivity(intent);
                finish();
            }
        });

        //Coffee Savings Below
        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PlanDisplay.class);
                startActivity(intent);
                finish();
            }
        });

        //Ride Savings Below
        view4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PlanDisplay.class);
                startActivity(intent);
                finish();
            }
        });










    }

}
