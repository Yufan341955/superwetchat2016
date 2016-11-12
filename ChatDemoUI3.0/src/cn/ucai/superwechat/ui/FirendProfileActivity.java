package cn.ucai.superwechat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.io.Serializable;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;

/**
 * Created by Administrator on 2016/11/8.
 *
 */

public class FirendProfileActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG=FirendProfileActivity.class.getSimpleName();
    private ImageView mImgback;
    private TextView mtvTitle;
    private ImageView mImgAvatar;
    private TextView mtvUserName;
    private TextView mtvNickName;
    private Button mbtnAddFriend;
    private Button mbtnSendMSG;
    private Button mbtnSendVdeio;
    User user=null;
    String username=null;
   boolean isFirend=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        username = getIntent().getStringExtra(I.User.USER_NAME);
        if(username==null){
            syncFail();
        }
        user=SuperWeChatHelper.getInstance().getAppContactList().get(username);
        if(user==null){
            isFirend=false;
        }else {
            initView();
            isFirend=true;
        }
        isFirend(isFirend);
        syncUserInfo();
        initView();

    }
    private void syncFail(){
        if(!isFirend) {
            MFGT.finish(this);
            return;
        }
    }
    private void syncUserInfo() {
        NetDao.searchUser(this, username, new OkHttpUtils.OnCompleteListener<Result>() {
            @Override
            public void onSuccess(Result result) {
                if(result!=null&&result.isRetMsg()){
                    Gson gson=new Gson();
                    String json=result.getRetData().toString();
                   User u=gson.fromJson(json,User.class);
                    if(u!=null){
                        if(isFirend){
                            SuperWeChatHelper.getInstance().saveAppContact(u);
                        }
                        user=u;
                        setUserInfo();
                    }
                }else {
                    syncFail();
                }
            }

            @Override
            public void onError(String error) {
                syncFail();
            }
        });
    }


    private void initView() {
        mImgback = (ImageView) findViewById(R.id.img_back);
        mtvTitle = (TextView) findViewById(R.id.txt_title);
        mImgAvatar = (ImageView) findViewById(R.id.Iv_Frient_Profile);
        mtvNickName = (TextView) findViewById(R.id.tv_Frient_Profile_Nick);
        mtvUserName = (TextView) findViewById(R.id.tv_Frient_Profile_Name);
        mImgback.setVisibility(View.VISIBLE);
        mtvTitle.setVisibility(View.VISIBLE);
        mtvTitle.setText(getString(R.string.userinfo_txt_profile));
        mbtnAddFriend = (Button) findViewById(R.id.m_Frient_Profile_Add_Btn);
        mbtnSendMSG = (Button) findViewById(R.id.m_Frient_Profile_SendMessage_Btn);
        mbtnSendVdeio = (Button) findViewById(R.id.m_Frient_Profile_MP4e_Btn);
        setUserInfo();
        setListener();

    }

    private void setListener() {
        mImgback.setOnClickListener(this);
        mbtnAddFriend.setOnClickListener(this);
        mbtnSendMSG.setOnClickListener(this);
        mbtnSendVdeio.setOnClickListener(this);
    }

    private void setUserInfo() {
        L.e(TAG,"user="+user.getAvatar());

            EaseUserUtils.setAppUserAvatar(this, user, mImgAvatar);
            EaseUserUtils.setAppUserNick(user.getMUserNick(), mtvNickName);
            EaseUserUtils.setAppUserNameWithNo(user.getMUserName(), mtvUserName);


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                MFGT.finish(FirendProfileActivity.this);
                break;
            case R.id.m_Frient_Profile_Add_Btn:
                 MFGT.gotoAddFirend(this,user.getMUserName());
                break;
            case R.id.m_Frient_Profile_SendMessage_Btn:
                 MFGT.gotoChat(this,user.getMUserName());
                break;
            case R.id.m_Frient_Profile_MP4e_Btn:
                  if(!EMClient.getInstance().isConnected()){
                      Toast.makeText(FirendProfileActivity.this, R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
                  }else {
                     startActivity(new Intent(this,VideoCallActivity.class).putExtra("username",user.getMUserName())
                             .putExtra("isComingCall",false));

                  }
                break;
        }
    }

    private void isFirend(boolean isfirend) {
        if(isfirend){
            mbtnSendMSG.setVisibility(View.VISIBLE);
            mbtnSendVdeio.setVisibility(View.VISIBLE);
        }else {
           mbtnAddFriend.setVisibility(View.VISIBLE);
        }
    }
}
