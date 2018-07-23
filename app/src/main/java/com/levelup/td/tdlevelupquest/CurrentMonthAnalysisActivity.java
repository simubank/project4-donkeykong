package com.levelup.td.tdlevelupquest;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * Created by Mikeb on 7/21/2018.
 */

public class CurrentMonthAnalysisActivity extends AppCompatActivity{
    private HashMap<String,TransactionObject> transactionsMap = new HashMap();
    private HashMap<String,TransactionObject> setGoals;
    private HashMap<String,Double> dateMap = new HashMap();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_analyze);
        setGoals =  (HashMap<String, TransactionObject>)getIntent().getSerializableExtra("map");
        NetworkHelper.getInstance().botAPIGetRequest("https://dev.botsfinancial.com/api/simulatedaccounts/873a24c9-1852-432a-8185-fb6e94d52ad1_b59fad0d-24ea-464c-a4cb-c2c1ee9702d9/simulatedtransactions",
                getApplicationContext(), new APICallback() {
                    @Override
                    public void onResponse(boolean success, JSONObject object) {
                        Log.d("tsest",""+success);
                        setupData(object);
                        setupListView();
                        setupLineGraph();
                    }
                });
        LaunchActivity.initDrawer(CurrentMonthAnalysisActivity.this,this);
    }
    private void setupData(JSONObject jsonObject){
        try {
            Log.d("www", jsonObject.getJSONArray("result").toString());
            JSONArray transactions = jsonObject.getJSONArray("result");
            for (int x = 0; x < transactions.length(); x++) {
                String merchantName = transactions.getJSONObject(x).getString("merchantName");
                Double amount = -1 * transactions.getJSONObject(x).getDouble("currencyAmount");
                String category = transactions.getJSONObject(x).getJSONArray("categoryTags").get(0).toString();

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String dateStr = transactions.getJSONObject(x).getString("postDate").substring(0, 10);
                Date date = format.parse(dateStr);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                Log.d("month", "Month" + calendar.get(Calendar.MONTH));
                //postDate

                if (merchantName.equals("Star Bucks")) continue;
                if (merchantName.equals("Apple")) continue;
                if (calendar.get(Calendar.MONTH) != 8) continue;
                Log.d("printing new", merchantName + " " + amount + " " + category+" "+transactions.getJSONObject(x).getString("postDate").substring(0, 10));
                if (transactionsMap.containsKey(merchantName)) {
                    TransactionObject object = transactionsMap.get(merchantName);
                    object.totalAmount += amount;
                    object.numberOfTimes++;
                } else {
                    transactionsMap.put(merchantName, new TransactionObject(amount, category));
                }
                dateMap.put(dateStr,(dateMap.containsKey(dateStr)) ? dateMap.get(dateStr)+amount : amount) ;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setupListView(){
        TransactionMapAdapter mapAdapter = new TransactionMapAdapter(transactionsMap,setGoals);
        ListView listView = findViewById(R.id.monthAnalyzeListView);
        listView.setAdapter(mapAdapter);
    }

    private void setupLineGraph(){
        LineChart lineChart = findViewById(R.id.analyzeLineChart);
        Description d = new Description();
        d.setText("");
        lineChart.setDescription(d);
        SortedSet<String> keys = new TreeSet<>(dateMap.keySet());
        final ArrayList <String> xValues = new ArrayList();
        ArrayList <Entry> yValues = new ArrayList<Entry>();
        int counter = 0;
        for (String key : keys) {
            xValues.add(key);
            yValues.add(new Entry(counter,dateMap.get(key).floatValue()));
            counter++;
        }

        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getXAxis().setLabelRotationAngle(-70);
        lineChart.getXAxis().setTextColor(Color.GRAY);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getLegend().setEnabled(false);
        lineChart.getXAxis().setLabelCount(keys.size());
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setTextColor(Color.GRAY);
        lineChart.setExtraOffsets(10, 10, 10, 10);
        lineChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if((value > xValues.size()-1) ||(value < 0.0)) return "";
                return xValues.get((int) value);
            }
        });
        LineDataSet dataSet =new LineDataSet(yValues, "");
        dataSet.setValueTextColor(Color.GRAY);
        dataSet.setFillColor(ColorTemplate.getHoloBlue());
        dataSet.setColor(ColorTemplate.getHoloBlue());
        dataSet.setDrawFilled(true);
        dataSet.setCircleColor(ColorTemplate.getHoloBlue());
        dataSet.setCircleColorHole(Color.WHITE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleSize(5f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setHighLightColor(Color.GRAY);


        LineData data = new LineData(dataSet);

        // set data
        lineChart.setData(data);
        lineChart.invalidate();
    }

    private class TransactionMapAdapter extends BaseAdapter {
        private ArrayList arrayData;
        private Map<String, TransactionObject> map;

        public TransactionMapAdapter(Map<String, TransactionObject> newMap, Map<String, TransactionObject> oldMap) {
            arrayData = new ArrayList();
            map = newMap;
            arrayData.addAll(oldMap.entrySet());
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

        private void setupPie(double spend, double remaining, PieChart pieChart){
            pieChart.setDrawHoleEnabled(true);
            pieChart.setTransparentCircleAlpha(0);
            pieChart.setHoleRadius(60);
            pieChart.setTransparentCircleRadius(10);
            pieChart.setUsePercentValues(true);
            Description d = new Description();
            d.setText("");
            pieChart.setDescription(d);

            pieChart.setRotationAngle(0);
            pieChart.setRotationEnabled(true);
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.getLegend().setEnabled(false);
            pieChart.setExtraOffsets(10,0,10,0);

            List<PieEntry> pieChartEntries = new ArrayList<>();
            if(spend <= remaining){
                pieChartEntries.add(new PieEntry((float) spend, "Spent"));
                pieChartEntries.add(new PieEntry((float) remaining, "Remaining"));
            }else{
                pieChartEntries.add(new PieEntry(1, "Spent"));
                pieChartEntries.add(new PieEntry(0, "Remaining"));
            }



            PieDataSet set = new PieDataSet(pieChartEntries, "Transaction Details");
            PieData data = new PieData(set);
            pieChart.setData(data);
            set.setSliceSpace(3);
            set.setSelectionShift(2);

            //set colors
            ArrayList<Integer> colors = new ArrayList<Integer>();
            colors.add(ColorTemplate.VORDIPLOM_COLORS[4]);
            colors.add(ColorTemplate.VORDIPLOM_COLORS[0]);
            set.setColors(colors);

            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.BLACK);
            pieChart.invalidate();
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final View result;
            if (view == null) {
                result = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_analyze_progress, viewGroup, false);
            } else {
                result = view;
            }

            Map.Entry<String, TransactionObject> item = getItem(i);
            TextView merchantName = result.findViewById(R.id.cellMerchantTextView);
            TextView spent = result.findViewById(R.id.cellSpentTextview);
            TextView remaining = result.findViewById(R.id.cellRemainingTextView);
            PieChart pieChart = result.findViewById(R.id.cellProgessChart);
            merchantName.setText(item.getKey());
            if (map.containsKey(item.getKey())) {
                spent.setText("$" + map.get(item.getKey()).totalAmount);
                remaining.setText("$" + (item.getValue().totalAmount - map.get(item.getKey()).totalAmount));
                setupPie(map.get(item.getKey()).totalAmount,(item.getValue().totalAmount - map.get(item.getKey()).totalAmount),pieChart);
            } else {
                spent.setText("$0");
                remaining.setText("$" + (item.getValue().totalAmount));
                setupPie(0,item.getValue().totalAmount,pieChart);
            }

            return result;
        }
    }
}
