package example.expense.user.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

import example.expense.user.app.common.CommNetwork;
import example.expense.user.app.common.ErrorUtils;
import example.expense.user.app.common.SharedPref;
import example.expense.user.app.common.listener.onNetworkResponseListener;
import example.expense.user.app.expense.NewExpense;
import example.expense.user.app.expense.ViewExpense;

/**
 * Created by dilky on 2016-07-20.
 * 신청 경비 목록 화면
 */
public class ExpenseList extends AppCompatActivity implements onNetworkResponseListener{


    private SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<ExpenseListModel> listDatas;
    ListView listView;
    ExpenseListAdapter adapter;

    Activity activity;
    onNetworkResponseListener onNetworkResponseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.enableDefaults();
        super.onCreate(savedInstanceState);
        activity = this;
        onNetworkResponseListener = this;

        try {
            CommNetwork net = new CommNetwork(this, this);
            JSONObject requestObject = new JSONObject();
            if(SharedPref.getUserId(activity).equals("")){
                return;
            }else {
                requestObject.put("USER_ID", SharedPref.getUserId(this));
                net.requestToServer("EXPENSE_L001", requestObject);
            }

            setContentView(R.layout.content_main);

            listView = (ListView) findViewById(R.id.expenseListView);
            adapter = new ExpenseListAdapter(this);
            listDatas = new ArrayList<>();

            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    try {
                        CommNetwork net = new CommNetwork(activity, onNetworkResponseListener);
                        JSONObject requestObject = new JSONObject();
                        requestObject.put("USER_ID", SharedPref.getUserId(activity));
                        net.requestToServer("EXPENSE_L001", requestObject);
                    } catch (Exception e) {
                        ErrorUtils.AlertException(activity, getString(R.string.error_msg_default_with_activity), e);
                    }


                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ExpenseList.this, ViewExpense.class);
                    intent.putExtra("EXPENSE_SEQ", listDatas.get(position).expenseSeq);
                    Log.d("sangjaePutTest", listDatas.get(position).expenseSeq);

                    startActivity(intent);
                }
            });

            addToolBar();

        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }


    public void toolbarRightButtonClick(View view) {
        Intent intent = new Intent(this, NewExpense.class);
        startActivity(intent);
        //finish();
    }

    private void addToolBar() throws Exception {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSuccess(String api_key, JSONObject response) {
        try {
            JSONArray array = response.getJSONArray("REC");

            listDatas.clear();
            for(int i = 0;  i< array.length(); i++){
                JSONObject object = array.getJSONObject(i);

                ExpenseListModel data = new ExpenseListModel();
                data.expenseSeq = object.getString("EXPENSE_SEQ");
                data.state = object.getString("ADMISSION_STATUS_NM");
                data.storeName = object.getString("PAYMENT_STORE_NM");
                data.summary = object.getString("SUMMARY");
                data.amount = object.getString("PAYMENT_AMT");
                data.paymentDate = object.getString("PAYMENT_DTTM");

                listDatas.add(data);
            }

            adapter.setList(listDatas);
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String api_key, String error_cd, String error_msg) {

    }
}
