package example.expense.user.app.expense;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import example.expense.user.app.R;
import example.expense.user.app.common.AccountTitleSpinnerList;
import example.expense.user.app.common.CommNetwork;
import example.expense.user.app.common.ErrorUtils;
import example.expense.user.app.common.SharedPref;
import example.expense.user.app.common.listener.onNetworkResponseListener;

/**
 * Created by user on 2016-09-01.
 */
public class ModifyExpense extends AppCompatActivity implements onNetworkResponseListener {

    public static EditText etPaymentDate;
    private Spinner spnAccountTitle;
    AccountTitleSpinnerList spinnerList;
    String seqJson;
    String seq;
    public static String selectYear, selectMonth, selectDay;
    String selectTTLCd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_new_expense);

        Intent intent = getIntent();
        seqJson = intent.getStringExtra("EXPENSE_SEQ_JSON");
        seq = intent.getStringExtra("EXPENSE_SEQ");
        Log.d("seqTestCreate",seqJson);


        CommNetwork network = new CommNetwork(this, this);
        try {

            addToolBar();
            JSONObject object = new JSONObject(seqJson);

            network.requestToServer("EXPENSE_R001", object);



            etPaymentDate = (EditText) findViewById(R.id.et_PaymentDate);
            // 클릭시 데이트피커 보여주기
            etPaymentDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog(v);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAccounttitleCodes() throws Exception {

        CommNetwork network = new CommNetwork(this, this);

        JSONObject requestObject = new JSONObject();
        network.requestToServer("ACCOUNT_L001", requestObject);
    }

    private void addToolBar() throws Exception {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.text_new_expense);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    // 사용일자 클릭
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private boolean emptyCheck(EditText editText) {
        if (TextUtils.isEmpty(editText.getText())) {
            Toast.makeText(this, getString(R.string.error_msg_required_values_are_missing), Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    public void toolbarRightButtonClick(View v) {
        try {
            EditText paymentStoreNm = (EditText) findViewById(R.id.et_PaymentStoreName);
            EditText paymentAmount = (EditText) findViewById(R.id.et_PaymentAmount);
            EditText summary = (EditText) findViewById(R.id.et_Summary);

            if ( emptyCheck(paymentStoreNm)
                    || emptyCheck(paymentAmount)
                    || emptyCheck(summary)
                    || emptyCheck(etPaymentDate) ) {
                return;
            }

            // 서버에 저장하기
            JSONObject requestObject = new JSONObject();
            requestObject.put("PAYMENT_STORE_NM", paymentStoreNm.getText().toString());
            requestObject.put("PAYMENT_AMT", paymentAmount.getText().toString());

            /*
            *  로그
            */
            //Log.d("sangjaeDateTest","년 : "+etPaymentDate.getTag(R.id.tag_year).toString()+" 월 : "+((Integer)etPaymentDate.getTag(R.id.tag_month))+" 일 : "+etPaymentDate.getTag(R.id.tag_day));
            requestObject.put("PAYMENT_DTTM", String.format("%4s%02d%2s", etPaymentDate.getTag(R.id.tag_year), ((Integer)etPaymentDate.getTag(R.id.tag_month)), etPaymentDate.getTag(R.id.tag_day)));
            requestObject.put("SUMMARY", summary.getText().toString());
            requestObject.put("ACCOUNT_TTL_CD", spinnerList.getAccountTitleCd(spnAccountTitle.getSelectedItemPosition()));
            requestObject.put("USER_ID", SharedPref.getUserId(this));
            Log.d("seqTest",seqJson);
            requestObject.put("EXPENSE_SEQ", seq);
            Log.d("sangjae", SharedPref.getUserId(this));

            Log.d("dilky", requestObject.toString());

            CommNetwork network = new CommNetwork(this, this);
            network.requestToServer("EXPENSE_U001", requestObject);
        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year, month, day;
            if (etPaymentDate.getTag(R.id.tag_year) == null) {
                // Use the current date as the default date in the picker
                final Calendar c = Calendar.getInstance();
//                year = Integer.parseInt(selectYear);
//                month = Integer.parseInt(selectMonth);
//                day = Integer.parseInt(selectDay);

                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                //creat Tag because modify
//                etPaymentDate.setTag(R.id.tag_year, year);
//                etPaymentDate.setTag(R.id.tag_month, month);
//                etPaymentDate.setTag(R.id.tag_day, day);
            } else {
                year = (Integer) etPaymentDate.getTag(R.id.tag_year);
                month = (Integer) etPaymentDate.getTag(R.id.tag_month) - 1;
                day = (Integer) etPaymentDate.getTag(R.id.tag_day);
            }

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            String date = String.format("%04d년 %2d월 %2d일", year, month + 1, day);
            etPaymentDate.setText(date);
            etPaymentDate.setTag(R.id.tag_year, year);
            etPaymentDate.setTag(R.id.tag_month, month+1);
            etPaymentDate.setTag(R.id.tag_day, day);
        }
    }

    @Override
    public void onSuccess(String api_key, JSONObject response) {

        try {

        if(api_key.equals("ACCOUNT_L001")){
            JSONArray array = response.getJSONArray("REC");
            spinnerList = new AccountTitleSpinnerList(array);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ModifyExpense.this, android.R.layout.simple_spinner_item, spinnerList.getArrayList());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spnAccountTitle.setAdapter(adapter);

            for(int i=0; i< spnAccountTitle.getCount(); i++){

                if(spinnerList.getAccountTitleCd(i).equals(selectTTLCd)){
                    spnAccountTitle.setSelection(i);
                }
            }


        }else if(api_key.equals("EXPENSE_U001")){
            Toast.makeText(ModifyExpense.this, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            finish();

        }else if(api_key.equals("EXPENSE_R001")){
            EditText paymentStoreNm = (EditText) findViewById(R.id.et_PaymentStoreName);
            EditText paymentAmount = (EditText) findViewById(R.id.et_PaymentAmount);
            EditText summary = (EditText) findViewById(R.id.et_Summary);
            EditText paymentDate = (EditText) findViewById(R.id.et_PaymentDate);

            selectTTLCd = response.getString("ACCOUNT_TTL_CD");

            spnAccountTitle = (Spinner)findViewById(R.id.spn_AccountTitle);
            getAccounttitleCodes();

            ImageView iv_ReceiptPhoto = (ImageView) findViewById(R.id.iv_ReceiptPhoto);

            paymentStoreNm.setText(response.getString("PAYMENT_STORE_NM"));
            paymentAmount.setText(response.getString("PAYMENT_AMT"));
            summary.setText(response.getString("SUMMARY"));
            String dateString = response.getString("PAYMENT_DTTM");
            paymentDate.setText( String.format("%4s년 %2s월 %2s일", dateString.substring(0, 4), dateString.substring(4, 6), dateString.substring(6, 8)));
            //Dialog need parameter
            selectYear = dateString.substring(0, 4);
            selectMonth = dateString.substring(4, 6);
            selectDay = dateString.substring(6, 8);

            //creat Tag because modify
            etPaymentDate.setTag(R.id.tag_year, Integer.parseInt(dateString.substring(0, 4)));
            etPaymentDate.setTag(R.id.tag_month, Integer.parseInt(dateString.substring(4, 6)));
            etPaymentDate.setTag(R.id.tag_day, Integer.parseInt(dateString.substring(6, 8)));

            Log.d("@@@@", String.valueOf(etPaymentDate.getTag(R.id.tag_month)));


//TODO:영수증이미지 처리하기
//            //영수증 이미지불러오기
//            String path = response.getString("RECEIPT_PHOTO").replace("\\","");
//            Glide.with(this).load(path).into(iv_ReceiptPhoto);
//
        }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFailure(String api_key, String error_cd, String error_msg) {

        try {
            if ("ACCOUNT_L001".equals(api_key)) {
                Log.d("dilky", String.format("onFailure (error_cd : %s, error_msg: %s)", error_cd, error_msg) );
            } else if ("EXPENSE_I001".equals(api_key)) {

            }
        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }
}
