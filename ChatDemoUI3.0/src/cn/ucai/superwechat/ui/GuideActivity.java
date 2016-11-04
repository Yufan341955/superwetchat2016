package cn.ucai.superwechat.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.MFGT;

public class GuideActivity extends BaseActivity {

    Button mbtnLogin,mbtnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
        setListener();
    }

    private void setListener() {
        mbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MFGT.gotoLogin(GuideActivity.this);
            }
        });
        mbtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MFGT.gotoRegister(GuideActivity.this);
            }
        });
    }

    private void initView() {
       mbtnLogin= (Button) findViewById(R.id.btn_Login);
       mbtnRegister= (Button) findViewById(R.id.btn_Register);
    }
}
