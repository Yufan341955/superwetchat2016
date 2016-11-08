package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.MFGT;

/**
 * Created by Administrator on 2016/11/8.
 */
public class AddFriendActivity extends BaseActivity implements View.OnClickListener{
    private ImageView mImgback;
    private Button mbtnSend;
    private EditText metMsg;
    private ProgressDialog progressDialog;
    String username;
    String Msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        username = getIntent().getStringExtra(I.User.USER_NAME);
        if(username==null){
            MFGT.finish(this);
        }
        initView();
        setListener();
    }

    private void setListener() {
        mbtnSend.setOnClickListener(this);
        mImgback.setOnClickListener(this);
    }

    private void initView() {
        mImgback= (ImageView) findViewById(R.id.img_back);
        mbtnSend= (Button) findViewById(R.id.btn_send);
        metMsg= (EditText) findViewById(R.id.et_msg);
        mImgback.setVisibility(View.VISIBLE);
        mbtnSend.setVisibility(View.VISIBLE);
        Msg=getString(R.string.addcontact_send_msg_prefix)+ EaseUserUtils.getCurrentAppUserInfo().getMUserNick();
        metMsg.setText(Msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send:
                sendMessage();
            break;
            case R.id.img_back:
                MFGT.finish(this);
                break;
        }
    }

    private void sendMessage() {
        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.addcontact_adding);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Thread(new Runnable() {
            public void run() {

                try {
                    //demo use a hardcode reason here, you need let user to input if you like

                    EMClient.getInstance().contactManager().addContact(username, Msg);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
                            MFGT.finish(AddFriendActivity.this);
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                            MFGT.finish(AddFriendActivity.this);
                        }
                    });
                }
            }
        }).start();
    }
}
