package com.levelup.td.tdlevelupquest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.location.places.Places;
import com.levelup.td.tdlevelupquest.Utils.APICallback;
import com.levelup.td.tdlevelupquest.Utils.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AnalyzeSpendingActivity extends AppCompatActivity {
    private HashMap<String,TransactionObject> transactionsMap = new HashMap<String,TransactionObject>();
    private Double ogAmount =0.0;
    private Boolean pieChartVisiable = true;
    private PieChart pieChart;
    private BarChart barChart;
    private ListView transactionListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_spending);
        transactionListView = findViewById(R.id.analyzeTransactionList);
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.header_analyze_spend, transactionListView, false);
        transactionListView.addHeaderView(header, null, false);
        pieChart =  findViewById(R.id.analzyePieChart);
        barChart = findViewById(R.id.analzyeBarGraph);
        barChart.setVisibility(View.GONE);

        Button pieGraphBtn = findViewById(R.id.switchToPieChartButton);
        pieGraphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pieChartVisiable = true;
                pieChart.setVisibility(View.VISIBLE);
                barChart.setVisibility(View.GONE);
                setPieGraph();
            }
        });

        Button barGraphBtn = findViewById(R.id.switchToBarChartButton);
        barGraphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pieChartVisiable = false;
                pieChart.setVisibility(View.GONE);
                barChart.setVisibility(View.VISIBLE);
                setupBarGraph();
            }
        });

        Button makeItAGoalBtn = findViewById(R.id.makeGoalButton);
        makeItAGoalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AnalyzeSpendingActivity.this, CurrentMonthAnalysisActivity.class);
                myIntent.putExtra("map",transactionsMap);
                AnalyzeSpendingActivity.this.startActivity(myIntent);
            }
        });

        LaunchActivity.initDrawer(AnalyzeSpendingActivity.this,this);
        NetworkHelper.getInstance().botAPIGetRequest("https://dev.botsfinancial.com/api/simulatedaccounts/873a24c9-1852-432a-8185-fb6e94d52ad1_b59fad0d-24ea-464c-a4cb-c2c1ee9702d9/simulatedtransactions",
                getApplicationContext(), new APICallback() {
                    @Override
                    public void onResponse(boolean success, JSONObject object) {
                        Log.d("tsest",""+success);
                        setupListView(object);
                        setPieGraph();
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

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date date = format.parse(transactions.getJSONObject(x).getString("postDate").substring(0,10));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                Log.d("month","Month"+calendar.get(Calendar.MONTH));
                //postDate
                Log.d("printing",merchantName+" "+amount+" "+category);
                if(merchantName.equals("Star Bucks")) continue;
                if(merchantName.equals("Apple")) continue;
                if(calendar.get(Calendar.MONTH)==8)continue;
                if(transactionsMap.containsKey(merchantName)){
                    TransactionObject object = transactionsMap.get(merchantName);
                    object.totalAmount += amount;
                    ogAmount += amount;
                    object.numberOfTimes ++;
                }else{
                    ogAmount += amount;
                    transactionsMap.put(merchantName,new TransactionObject(amount,category));
                }
            }
            TransactionMapAdapter adapter = new TransactionMapAdapter(transactionsMap, new ListViewCallback() {
                @Override
                public void onButtonClicked() {
                    if(pieChartVisiable){
                        Log.d("ww", "pie");
                        setPieGraph();
                    }else {
                        Log.d("ww", "bar");
                        setupBarGraph();
                    }
                    percentChange();
                }
            });
            transactionListView.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void percentChange(){
        Double newTotal = 0.0;
        for(Map.Entry<String, TransactionObject> obj : transactionsMap.entrySet()){
            newTotal += obj.getValue().totalAmount;
            Log.d("amount:","merchant: "+obj.getKey()+ " v: "+obj.getValue().totalAmount);
        }
        Log.d("og amount:",ogAmount+"");
        Log.d("amount:",""+newTotal);
        TextView savingAmountTextView = findViewById(R.id.savingAmountTextView);
        TextView savingPercentTextView = findViewById(R.id.savingPercentTextView);
        savingAmountTextView.setText("Amount Saved: $"+(ogAmount-newTotal));
        savingPercentTextView.setText("Percentage Cut: "+Math.round((1-(newTotal/ogAmount)) *100)+"%");
    }

    private void setupBarGraph(){
        Description d = new Description();
        d.setText("");
        barChart.setDescription(d);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        List<BarEntry> barEntries = new ArrayList<>();
        final List<String> labels = new ArrayList<>();
        float counter = 0;
        for(Map.Entry<String, TransactionObject> obj : transactionsMap.entrySet()){
            barEntries.add(new BarEntry(counter,obj.getValue().totalAmount.floatValue()));
            counter++;
            labels.add(obj.getKey());
        }
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setTextColor(Color.GRAY);
        barChart.getAxisLeft().setTextColor(Color.GRAY);
        barChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels.get((int) value);
            }
        });
        BarDataSet dataSet = new BarDataSet(barEntries, "");
        dataSet.setValueTextColor(Color.GRAY);
        BarData data = new BarData(dataSet);

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        barChart.setData(data);
        barChart.invalidate();
    }


    private void setPieGraph(){
        PieChart mChart =  findViewById(R.id.analzyePieChart);

        //setup chart image
        mChart.setDrawHoleEnabled(true);
        mChart.setTransparentCircleAlpha(0);
        mChart.setHoleRadius(7);
        mChart.setTransparentCircleRadius(10);
        mChart.setUsePercentValues(true);
        Description d = new Description();
        d.setText("");
        mChart.setDescription(d);

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setEntryLabelColor(Color.GRAY);
        mChart.getLegend().setEnabled(false);

        List<PieEntry> pieChartEntries = new ArrayList<>();
        for(Map.Entry<String, TransactionObject> obj : transactionsMap.entrySet()){
            pieChartEntries.add(new PieEntry(obj.getValue().totalAmount.floatValue(), obj.getKey()));
        }

        PieDataSet set = new PieDataSet(pieChartEntries, "Transaction Details");
        PieData data = new PieData(set);
        mChart.setData(data);
        set.setSliceSpace(3);
        set.setSelectionShift(2);

        //set colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        set.setColors(colors);

        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);
        mChart.invalidate();
    }

    private interface ListViewCallback {
        void onButtonClicked();
    }

    private class TransactionMapAdapter extends BaseAdapter{
        private ArrayList arrayData;
        private Map<String,TransactionObject> map;
        private ListViewCallback callback;
        public TransactionMapAdapter(Map<String,TransactionObject> newMap, ListViewCallback newCallback){
            callback = newCallback;
            arrayData = new ArrayList();
            map = newMap;
            arrayData.addAll(newMap.entrySet());
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

        private void changeAmount(int position, int delta, View view){
            TextView numberOfTimesTextView = view.findViewById(R.id.numberOfTimesTextView);
            TextView amountTextView = view.findViewById(R.id.amountTextView);
            Map.Entry<String, TransactionObject> item = getItem(position);
            TransactionObject obj =map.get(item.getKey());
            Double amount = obj.totalAmount/obj.numberOfTimes;
            obj.numberOfTimes += delta;
            obj.totalAmount += (int)(delta* amount);

            numberOfTimesTextView.setText("#"+item.getValue().numberOfTimes);
            amountTextView.setText("$"+item.getValue().totalAmount);
            callback.onButtonClicked();
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final View result;
            if (view == null) {
                result = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_analyze_spend, viewGroup, false);
            } else {
                result = view;
            }

            Map.Entry<String, TransactionObject> item = getItem(i);

            //set textvalue
            TextView numberOfTimesTextView = result.findViewById(R.id.numberOfTimesTextView);
            ((TextView)result.findViewById(R.id.amountTextView)).setText("$"+item.getValue().totalAmount);
            numberOfTimesTextView.setText("#"+item.getValue().numberOfTimes);
            ((TextView)result.findViewById(R.id.merchantTextView)).setText(item.getKey());
            ImageButton addBtn = result.findViewById(R.id.plusButton);
            ImageButton minusBtn =result.findViewById(R.id.minusButton);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeAmount(i,1,result);
                }
            });
            minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeAmount(i,-1,result);
                }
            });
            Log.d("Loading","here");

            return result;
        }
    }

}
