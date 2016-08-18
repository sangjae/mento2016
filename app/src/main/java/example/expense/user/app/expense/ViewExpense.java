package example.expense.user.app.expense;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import example.expense.user.app.R;
import example.expense.user.app.common.CommNetwork;
import example.expense.user.app.common.ErrorUtils;
import example.expense.user.app.common.listener.onNetworkResponseListener;

/**
 * Created by dilky on 2016-07-20.
 * 상세보기 화면
 */
public class ViewExpense extends AppCompatActivity implements onNetworkResponseListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String seq = intent.getStringExtra("EXPENSE_SEQ");

        CommNetwork net = new CommNetwork(this, this);
        JSONObject obj = new JSONObject();
        try {
            obj.put("EXPENSE_SEQ", seq);
            net.requestToServer("EXPENSE_R001", obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            setContentView(R.layout.content_view_expense);

            addToolBar();


        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }

    private void addToolBar() throws Exception {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.text_detail_view);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_expense, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 메뉴의 항목을 선택(클릭)했을 때 호출되는 콜백메서드
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_modify:
                Toast.makeText(getApplicationContext(), "수정 메뉴 클릭",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_delete:
                Toast.makeText(getApplicationContext(), "삭제 메뉴 클릭",
                        Toast.LENGTH_SHORT).show();

                Intent intent = getIntent();
                String seq = intent.getStringExtra("EXPENSE_SEQ");

                CommNetwork net = new CommNetwork(this, this);
                JSONObject obj = new JSONObject();
                try {
                    obj.put("EXPENSE_SEQ", seq);
                    net.requestToServer("EXPENSE_D001", obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(String api_key, JSONObject response) {

        if(api_key.equals("EXPENSE_R001")){
            String a =  response.toString();
            TextView tv_Status = (TextView) findViewById(R.id.tv_Status);
            TextView tv_PaymentStoreName = (TextView) findViewById(R.id.tv_PaymentStoreName);
            TextView tv_PaymentAmount = (TextView) findViewById(R.id.tv_PaymentAmount);
            TextView tv_PaymentDate = (TextView) findViewById(R.id.tv_PaymentDate);
            TextView tv_Summary = (TextView) findViewById(R.id.tv_Summary);
            TextView tv_UserName = (TextView) findViewById(R.id.tv_UserNmae);
            TextView tv_AccountTitle = (TextView) findViewById(R.id.tv_AccountTitle);
            ImageView iv_ReceiptPhoto = (ImageView) findViewById(R.id.iv_ReceiptPhoto);

            try {
                tv_Status.setText(response.getString("ADMISSION_STATUS_NM"));
                tv_PaymentStoreName.setText(response.getString("PAYMENT_STORE_NM"));
                tv_PaymentAmount.setText(response.getString("PAYMENT_AMT"));

                String dateString = response.getString("PAYMENT_DTTM");
                tv_PaymentDate.setText( String.format("%4s년 %2s월 %2s일", dateString.substring(0, 4), dateString.substring(4, 6), dateString.substring(6, 8)));
                tv_Summary.setText(response.getString("SUMMARY"));
                tv_UserName.setText(response.getString("INSERT_USERID"));
                tv_AccountTitle.setText(response.getString("ACCOUNT_TTL_NM"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(api_key.equals("EXPENSE_D001")){
            try {
                if(response.getInt("DELETE_RSLT") == 1){
                    Toast.makeText(ViewExpense.this, response.getString("DELETE_RSLT_MSG"), Toast.LENGTH_SHORT).show();
                    finish();
                } else if(response.getInt("DELETE_RSLT") == 0){
                    Toast.makeText(ViewExpense.this, response.getString("DELETE_RSLT_MSG") + " 삭제할 데이터를 찾을 수 없음 (이미 삭제 되었거나, 일련번호 오류)", Toast.LENGTH_SHORT).show();
                } else if(response.getInt("DELETE_RSLT") == -1){
                    Toast.makeText(ViewExpense.this, response.getString("DELETE_RSLT_MSG")+" 삭제할 수 없는 데이터임", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(String api_key, String error_cd, String error_msg) {

    }
}
