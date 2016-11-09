package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.io.Serializable;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
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
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        user = (User) getIntent().getSerializableExtra(I.User.USER_NAME);
        if(user==null){
          MFGT.finish(this);
            return;
        }
        initView();

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
        isFirend();
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

                break;
        }
    }

    private void isFirend() {
        if(SuperWeChatHelper.getInstance().getAppContactList().containsKey(user.getMUserName())){
            mbtnSendMSG.setVisibility(View.VISIBLE);
            mbtnSendVdeio.setVisibility(View.VISIBLE);
        }else {
           mbtnAddFriend.setVisibility(View.VISIBLE);
        }
    }
}
