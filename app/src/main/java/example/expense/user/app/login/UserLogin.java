package example.expense.user.app.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import example.expense.user.app.ExpenseList;
import example.expense.user.app.R;
import example.expense.user.app.common.CommNetwork;
import example.expense.user.app.common.ErrorUtils;
import example.expense.user.app.common.SharedPref;
import example.expense.user.app.common.listener.onNetworkResponseListener;

/**
 * Created by dilky on 2016-07-20.
 * 로그인 화면
 */
public class UserLogin extends AppCompatActivity implements onNetworkResponseListener{
    private EditText etUserId;
    private EditText etPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.content_login);

            if(SharedPref.getUserId(this).equals("")&&SharedPref.getPwd(this).equals("")){

            }else{
                Intent intent = new Intent(this, ExpenseList.class);
                startActivity(intent);
                finish();
            }


            addToolBar();

            etUserId = (EditText) findViewById(R.id.et_UserId);
            etPwd = (EditText) findViewById(R.id.et_Pwd);
        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }

    private void addToolBar() throws Exception {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.text_login);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    // 로그인 버튼 클릭
    public void loginClick(View view) {
        try {
            // Check Empty Value
            if (TextUtils.isEmpty(etUserId.getText())
                    || TextUtils.isEmpty(etPwd.getText())) {
                return;
            }

            CommNetwork network = new CommNetwork(this, this);

            JSONObject object = new JSONObject();
            object.put("USER_ID", etUserId.getText().toString());
            object.put("PWD", etPwd.getText().toString());
            network.requestToServer("LOGIN_R001",object);



            // TODO : ID, PWD 검증한다.


        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }

    @Override
    public void onSuccess(String api_key, JSONObject response) {
        try {

                SharedPref.putUserId(this, etUserId.getText().toString());
                SharedPref.putPwd(this, etPwd.getText().toString());
                Intent intent = new Intent(this, ExpenseList.class);
                startActivity(intent);
                finish();

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onFailure(String api_key, String error_cd, String error_msg) {

    }
}
