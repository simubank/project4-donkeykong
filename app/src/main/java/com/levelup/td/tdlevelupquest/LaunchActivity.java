package com.levelup.td.tdlevelupquest;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.ContactsContract;
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

        View someView = findViewById(R.id.textView);
        View root = someView.getRootView();
        root.getBackground().setColorFilter(Color.parseColor("#35B234"), PorterDuff.Mode.DARKEN);


        //String url = "https://api.uclassify.com/v1/uClassify/Topics/classify/?readKey=sXvXN9MJP0DE&text=macbook";
        //JsonCall(url);
        initDrawer();

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

    public void initDrawer (){

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(new Toolbar(LaunchActivity.this))
                .withActionBarDrawerToggle(false)
                .withTranslucentStatusBar(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(1).withName("number 1"),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withIdentifier(2).withName("number 2").withIcon(GoogleMaterial.Icon.gmd_cake),
                        new SecondaryDrawerItem().withName("number 3").withIcon(GoogleMaterial.Icon.gmd_games)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return true;
                    }
                })
                .build();
    }



}







