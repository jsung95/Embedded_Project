package com.lee.embedded;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "@@@@@@@@@@"; //log.d 에만 사용 삭제해도 무관

    private static final String TAG_JSON="result";
    private static final String TAG_SENSOR = "sensor";
    private static final String TAG_DATE = "collect_time";
    private static final String TAG_VALUE1 ="value1";
    private static final String TAG_VALUE2 ="value2";

    private TextView mTextViewResult, finalresult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    String mJsonString;
    ImageView img;
    Button ip_btn;
    EditText main_label;
    TextView v1,v2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        v1 = (TextView)findViewById(R.id.v1);
        v2 = (TextView)findViewById(R.id.v2);
        ip_btn = (Button) findViewById(R.id.ip_btn);
        main_label = (EditText)findViewById(R.id.main_label);
        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        finalresult = (TextView)findViewById(R.id.final_result);
        mlistView = (ListView) findViewById(R.id.listView_main_list);
        img = (ImageView) findViewById(R.id.img);
        mArrayList = new ArrayList<>();


        main_label.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i){
                    case EditorInfo.IME_ACTION_SEARCH:
                        ip_btn.performClick();
                        main_label.clearFocus();
                        imm.hideSoftInputFromWindow(main_label.getWindowToken(),0);
                        break;
                    default:

                        return false;
                }
                return true;
            }
        });

        ip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArrayList.clear();
                GetData task = new GetData();
                String ip = main_label.getText().toString();
                String address = "http://"+ip+"/conn.php";
                task.execute(address);
            }
        });





    }

    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);

            if (result == null){

                mTextViewResult.setTextColor(getResources().getColor(R.color.black));

                mTextViewResult.setText(errorString);
                finalresult.setText(" ");

                img.setBackground(null);

            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }




    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String sensor = item.getString(TAG_SENSOR);
                String date = item.getString(TAG_DATE);
                String value1 = item.getString(TAG_VALUE1);
                String value2 = item.getString(TAG_VALUE2);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_SENSOR, sensor);
                hashMap.put(TAG_DATE, date);
                hashMap.put(TAG_VALUE1, value1);
                hashMap.put(TAG_VALUE2, value2);

                int temp = Integer.parseInt(value1);
                int humm = Integer.parseInt(value2);
                double result = (0.81*temp) + 0.01 * humm*(0.99*temp-14.3) + 46.3;
                mTextViewResult.setText(String.format("%.2f",result));

                /*
                switch ((int)result/10){
                    case 8:
                        finalresult.setText("전원 불쾌감을 느낌");
                        mTextViewResult.setTextColor(getResources().getColor(R.color.four));
                        img.setBackgroundResource(R.drawable.four);
                        break;
                    case 7:
                        finalresult.setText("50% 정도 불쾌감을 느낌");
                        mTextViewResult.setTextColor(getResources().getColor(R.color.three));
                        img.setBackgroundResource(R.drawable.three);
                        break;
                    case 6:
                        finalresult.setText("불쾌감을 나타내기 시작함");
                        mTextViewResult.setTextColor(getResources().getColor(R.color.two));
                        img.setBackgroundResource(R.drawable.two);
                        break;
                    default:
                        finalresult.setText("전원 쾌적함을 느낌");
                        mTextViewResult.setTextColor(getResources().getColor(R.color.one));
                        img.setBackgroundResource(R.drawable.one);
                        break;
                }
                */



                if(result >= 80){
                    finalresult.setText("전원 불쾌감을 느낌");
                    mTextViewResult.setTextColor(getResources().getColor(R.color.four));
                    finalresult.setTextColor(getResources().getColor(R.color.four));
                    img.setBackgroundResource(R.drawable.four);
                }
                else if(result < 80 && result >= 75){
                    finalresult.setText("50% 정도 불쾌감을 느낌");
                    mTextViewResult.setTextColor(getResources().getColor(R.color.three));
                    finalresult.setTextColor(getResources().getColor(R.color.three));
                    img.setBackgroundResource(R.drawable.three);
                }
                else if(result < 75 && result >= 68){
                    finalresult.setText("불쾌감을 나타내기 시작함");
                    mTextViewResult.setTextColor(getResources().getColor(R.color.two));
                    finalresult.setTextColor(getResources().getColor(R.color.two));
                    img.setBackgroundResource(R.drawable.two);
                }
                else {
                    finalresult.setText("전원 쾌적함을 느낌");
                    mTextViewResult.setTextColor(getResources().getColor(R.color.one));
                    finalresult.setTextColor(getResources().getColor(R.color.one));
                    img.setBackgroundResource(R.drawable.one);
                }


                mArrayList.add(hashMap);
            }


            final ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, mArrayList, R.layout.item_list, new String[]{TAG_SENSOR, TAG_DATE, TAG_VALUE1, TAG_VALUE2},
                    new int[]{R.id.textView_list_sensor, R.id.textView_list_date, R.id.textView_list_value1, R.id.textView_list_value2}
            );



            ((SimpleAdapter) adapter).notifyDataSetChanged(); //새로고침
            mlistView.setAdapter(adapter);
            mlistView.setSelection(adapter.getCount() - 1); //맨아래로 스크롤
        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_btn1:
                mArrayList.clear();
                GetData task = new GetData();
                String ip = main_label.getText().toString();
                String address = "http://"+ip+"/conn.php";
                task.execute(address);
                return true;
            case R.id.action_btn2:
                CustomDialog customDialog = new CustomDialog(MainActivity.this);
                customDialog.callFunction();
                return true;
            case R.id.action_btn3:
                Intent intent = new Intent(MainActivity.this, BTActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }
}
