package com.levelup.td.tdlevelupquest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.levelup.td.tdlevelupquest.Utils.APICallback;
import com.levelup.td.tdlevelupquest.Utils.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Recommendation extends Activity {
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private ArrayList<Place> placeArrayList = new ArrayList();
    private HashMap<String,Integer> transactionsMap = new HashMap<String,Integer>();
    private List<Place> myPlaceList = new ArrayList();
    public class Pair {
        private Integer integer;
        private Place place;
    }
    List<Recommendation.Pair> displayedPlaces = new ArrayList<Recommendation.Pair>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        Button btn = findViewById(R.id.placesButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkThisOut();
            }
        });
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        LaunchActivity.initDrawer(Recommendation.this,this);
        ListView listView = findViewById(R.id.recommendationList);
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.header_recommendation, listView, false);
        listView.addHeaderView(header, null, false);

        NetworkHelper.getInstance().botAPIGetRequest("https://dev.botsfinancial.com/api/simulatedaccounts/873a24c9-1852-432a-8185-fb6e94d52ad1_b59fad0d-24ea-464c-a4cb-c2c1ee9702d9/simulatedtransactions",
                getApplicationContext(), new APICallback() {
                    @Override
                    public void onResponse(boolean success, JSONObject object) {
                        Log.d("tsest",""+success);
                        setupView(object);
                    }
                });
    }
    public void setupView(JSONObject jsonObject){
        try{
            Log.d("www",jsonObject.getJSONArray("result").toString());
            JSONArray transactions = jsonObject.getJSONArray("result");
            int max = 0;
            String maxMerchant = "hello";
            for(int x = 0 ;x <transactions.length();x++){
                String merchantName = transactions.getJSONObject(x).getString("merchantName");
                Log.d("printing","merchantName " + merchantName);
                if(transactionsMap.containsKey(merchantName)){
                    int merchantCount = transactionsMap.get(merchantName);
                    ++ merchantCount;
                    maxMerchant = (max < merchantCount)? merchantName :maxMerchant;
                    max = (max < merchantCount)? merchantCount :max;
                    transactionsMap.put(merchantName,merchantCount);
                }else{
                    maxMerchant = (max == 0)? merchantName :maxMerchant;
                    max = (max == 0)? 1 :max;
                    transactionsMap.put(merchantName,1);
                }
            }
            TextView merchant = findViewById(R.id.merchantTextView);
            merchant.setText("Your most frequent purchase was from the merchant: "
                    + maxMerchant + ". Please select the " + maxMerchant + " location you visit.");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void checkThisOut(){
        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try{
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }catch (Exception e) {
            Log.d("error", e.getMessage());
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        BufferedReader reader;
        HashMap<Integer,String> placeTypeMap = new HashMap<>();

        try{
            Context context = getApplicationContext();
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("keyValueForGooglePlaces.txt")));

            String line;

            while((line = reader.readLine()) != null){
                String value = line;
                Integer key = Integer.parseInt(reader.readLine());
                placeTypeMap.put(key,value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                final Place place = PlacePicker.getPlace(data, this);

                List<Integer> placeTypesList = place.getPlaceTypes();

                final Integer pickedPlacePriceLevel = place.getPriceLevel();
                String toastMsg = String.format("Place Name: %s", placeTypesList);
                Log.d("wwe", "Place Category Index:"+place.getPlaceTypes());
                Log.d("wwe", "Place ID:"+place.getId());
                Log.d("wwe", "Place Price Level:"+place.getPriceLevel());
                Log.d("wwe", "Place Location:"+place.getLatLng());
                Log.d("wwe", "Place Rating:"+place.getRating());


                Double NELat = place.getLatLng().latitude + 1;
                Double NELong = place.getLatLng().longitude + 1;
                Double SWLat = place.getLatLng().latitude - 1;
                Double SWLong = place.getLatLng().longitude - 1;
                LatLng NELatlng = new LatLng(NELat, NELong);
                LatLng SWLatlng = new LatLng(SWLat, SWLong);
                LatLngBounds newbounds = new LatLngBounds(SWLatlng,NELatlng);
                Integer placeMainType = place.getPlaceTypes().get(0);
                AutocompleteFilter placeFilter = new AutocompleteFilter.Builder().setTypeFilter(placeMainType).build();
                Task<AutocompletePredictionBufferResponse> results =
                        mGeoDataClient.getAutocompletePredictions("coffee", newbounds,null);
                results.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {
                        AutocompletePredictionBufferResponse autocompletePredictions = task.getResult();
                        Log.d("wwf", "Count: " + autocompletePredictions.getCount());
                        Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
                        int counter = 0;
                        while(iterator.hasNext()){
                            AutocompletePrediction prediction = iterator.next();
                            Log.d("wwe", "Query completed. Received " + prediction.getFullText(null)+ " predictions.");
                            String placeID = prediction.getPlaceId();
                            placeIDCreate(placeID,counter,pickedPlacePriceLevel);
                            counter++;

                        }
                        autocompletePredictions.release();
                    }
                });

                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void placeIDCreate(final String placeID,final int counter, final int pickedPlacePriceLevel){
        Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeID);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                Pair newPair = new Pair();
                PlaceBufferResponse places = task.getResult();
                Place place = places.get(0);
                int priceLevel = place.getPriceLevel();
                newPair.integer = priceLevel;
                newPair.place = place;
                displayedPlaces.add(newPair);
                Log.d("wwe", "Displayed Places:"+displayedPlaces.get(0).integer);
                Log.d("wwe", "Place Category Index:"+place.getPlaceTypes());
                Log.d("wwe", "Place ID:"+place.getId());
                Log.d("wwe", "Place Price Level:"+place.getPriceLevel());
                Log.d("wwe", "Place Location:"+place.getLatLng());
                Log.d("wwe", "Place Rating:"+place.getRating());
                myPlaceList.add(newPair.place);
                if(newPair.integer <= pickedPlacePriceLevel){
                    RecommendationAdapter adapter = new RecommendationAdapter(myPlaceList,transactionsMap);
                    ListView listView = findViewById(R.id.recommendationList);
                    listView.setAdapter(adapter);
                }
            }
        });
    }
    private class RecommendationAdapter extends BaseAdapter {
        private List <Place>arrayData;
        public RecommendationAdapter(List<Place> myPlace, HashMap<String,Integer> transactionMap){
            arrayData = myPlace;
        }

        @Override
        public int getCount() {
            return arrayData.size();
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public Place getItem(int position) {
            return arrayData.get(position);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final View result;

            if (view == null) {
                result = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_recommendation, viewGroup, false);
            } else {
                result = view;
            }

           Place myNewPlace = getItem(i);
            Log.d("look here","eejwekewwej");

            //set textvalue
            ((TextView)result.findViewById(R.id.recommendationPosition)).setText(i + 1 + ".");
            ((TextView)result.findViewById(R.id.recommendationName)).setText(myNewPlace.getName());
            ((TextView)result.findViewById(R.id.recommendationPrice)).setText("" + ((myNewPlace.getPriceLevel() < 0) ? "  N/A" : myNewPlace.getPriceLevel() ));
            ((TextView)result.findViewById(R.id.recommendationRating)).setText("" + ((myNewPlace.getRating() < 0) ? "N/A" : myNewPlace.getRating() ));
            Log.d("Loading","here");
            return result;
        }
    }
}
