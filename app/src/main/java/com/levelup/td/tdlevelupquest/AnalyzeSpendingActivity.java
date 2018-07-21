package com.levelup.td.tdlevelupquest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.location.places.Places;
import com.levelup.td.tdlevelupquest.Utils.APICallback;
import com.levelup.td.tdlevelupquest.Utils.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyzeSpendingActivity extends AppCompatActivity {
    private HashMap<String,TransactionObject> transactionsMap = new HashMap<String,TransactionObject>();
    private ListView transactionListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_spending);
        transactionListView = findViewById(R.id.analyzeTransactionList);

        LaunchActivity.initDrawer(AnalyzeSpendingActivity.this,this);
        NetworkHelper.getInstance().botAPIGetRequest("https://dev.botsfinancial.com/api/simulatedaccounts/873a24c9-1852-432a-8185-fb6e94d52ad1_b59fad0d-24ea-464c-a4cb-c2c1ee9702d9/simulatedtransactions",
                getApplicationContext(), new APICallback() {
                    @Override
                    public void onResponse(boolean success, JSONObject object) {
                        Log.d("tsest",""+success);
                        setupListView(object);
                    }
                });
    }

    public void setupListView(JSONObject jsonObject){
        try{
            Log.d("www",jsonObject.getJSONArray("result").toString());
            JSONArray transactions = jsonObject.getJSONArray("result");
            for(int x = 0 ;x <transactions.length();x++){
                String merchantName = transactions.getJSONObject(x).getString("merchantName");
                Double amount = -1*transactions.getJSONObject(x).getDouble("currencyAmount");
                String category = transactions.getJSONObject(x).getJSONArray("categoryTags").get(0).toString();
                Log.d("printing",merchantName+" "+amount+" "+category);
                if(transactionsMap.containsKey(merchantName)){
                    TransactionObject object = transactionsMap.get(merchantName);
                    object.totalAmount += amount;
                    object.numberOfTimes ++;
                }else{
                    transactionsMap.put(merchantName,new TransactionObject(amount,category));
                }
            }
            TransactionMapAdapter adapter = new TransactionMapAdapter(transactionsMap);
            transactionListView.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setPieGraph(){
       //to do
    }

    private class TransactionObject{
        Double totalAmount;
        int numberOfTimes;
        String category;
        public TransactionObject(Double newAmount, String newCategory){
            this.totalAmount = newAmount;
            this.category = newCategory;
            numberOfTimes = 1;
        }
    }

    private class TransactionMapAdapter extends BaseAdapter{
        private ArrayList arrayData;
        public TransactionMapAdapter(Map<String,TransactionObject> map){
            arrayData = new ArrayList();
            arrayData.addAll(map.entrySet());
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
        public Map.Entry<String, TransactionObject> getItem(int position) {
            return (Map.Entry) arrayData.get(position);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final View result;

            if (view == null) {
                result = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_analyze_spend, viewGroup, false);
            } else {
                result = view;
            }

            Map.Entry<String, TransactionObject> item = getItem(i);

            //set textvalue
            ((TextView)result.findViewById(R.id.amountTextView)).setText("$"+item.getValue().totalAmount);
            ((TextView)result.findViewById(R.id.numberOfTimesTextView)).setText("#"+item.getValue().numberOfTimes);
            ((TextView)result.findViewById(R.id.merchantTextView)).setText(item.getKey());
            Log.d("Loading","here");

            return result;
        }
    }

}
