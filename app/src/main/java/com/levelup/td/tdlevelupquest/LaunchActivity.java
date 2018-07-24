package com.levelup.td.tdlevelupquest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.levelup.td.tdlevelupquest.Utils.AccountInfo;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONObject;

public class LaunchActivity extends AppCompatActivity implements View.OnClickListener {

    TextView t1;
    Button b1;

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.firstbutton:
                Intent intent = new Intent(LaunchActivity.this, MainScreenActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        final Activity activity = this;

        //String url = "https://api.uclassify.com/v1/uClassify/Topics/classify/?readKey=sXvXN9MJP0DE&text=macbook";
        //JsonCall(url);

        b1 = findViewById(R.id.firstbutton);
        b1.setOnClickListener(this);
    }

    public void JsonCall (String URL){
        RequestQueue requestQueue = Volley.newRequestQueue(LaunchActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ok",response.toString());
                t1 = findViewById(R.id.textView);
                t1.setText(response.toString());
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public static void initDrawer (final Activity activity, Context context) {

         Drawer result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(new Toolbar(context))
                .withActionBarDrawerToggle(false)
                .withTranslucentStatusBar(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(1).withName("      Galen's Account").withSelectable(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Back Home").withIcon(GoogleMaterial.Icon.gmd_home),
                        new SecondaryDrawerItem().withIdentifier(2).withName("Create List").withIcon(GoogleMaterial.Icon.gmd_folder_open),
                        new SecondaryDrawerItem().withName("Smart Recommendations").withIcon(GoogleMaterial.Icon.gmd_fiber_smart_record),
                        new SecondaryDrawerItem().withName("Analyze Spending").withIcon(GoogleMaterial.Icon.gmd_multiline_chart),
                        new SecondaryDrawerItem().withName("Invest").withIcon(GoogleMaterial.Icon.gmd_attach_money),
                        //new SecondaryDrawerItem().withName("Test Button | Card View").withIcon(GoogleMaterial.Icon.gmd_home),
                        new SecondaryDrawerItem().withName("Account Information").withIcon(GoogleMaterial.Icon.gmd_settings)

                )
                .withDrawerWidthPx(850)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch(position){
                            case(2):
                                Intent intent = new Intent(view.getContext(), MainScreenActivity.class);
                                view.getContext().startActivity(intent);
                                break;
                            case(3):
                                Intent intent2 = new Intent(view.getContext(), CreateGoalsActivity.class);
                                view.getContext().startActivity(intent2);
                                break;
                            case(4):
                                Intent intent3 = new Intent(view.getContext(), Recommendation.class);
                                view.getContext().startActivity(intent3);
                                break;
                            case(5):
                                Intent intent4 = new Intent(view.getContext(), AnalyzeSpendingActivity.class);
                                view.getContext().startActivity(intent4);
                                break;
                            case (6):
                                Intent intent5 = new Intent(view.getContext(), InvestActivity.class);
                                view.getContext().startActivity(intent5);
                                break;
                            case (7):
                                Intent intent6 = new Intent(view.getContext(), AccountInfo.class);
                                view.getContext().startActivity(intent6);
                                break;
                        }
                        return true;
                    }
                })
                .build();
    }

}







