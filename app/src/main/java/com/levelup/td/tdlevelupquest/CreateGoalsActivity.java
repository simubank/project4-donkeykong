package com.levelup.td.tdlevelupquest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

public class CreateGoalsActivity extends AppCompatActivity {

    private int count = 0;
    ImageView iv;
    CardView c1;
    CardView c2;
    CardView c3;
    CardView c4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardview_create_goals);

        LaunchActivity.initDrawer(CreateGoalsActivity.this,this);

        iv = findViewById(R.id.addButton);
        c1 = findViewById(R.id.card1);
        c2 = findViewById(R.id.card2);
        c3 = findViewById(R.id.card3);
        c4 = findViewById(R.id.card4);

        iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (count==0){
                    count=1;
                    c2.setVisibility(View.VISIBLE);
                }
                else if (count==1){
                    count=2;
                    c3.setVisibility(View.VISIBLE);
                }
                else if (count==2){
                    count=3;
                    c4.setVisibility(View.VISIBLE);
                }
            }

        });

    }





}
