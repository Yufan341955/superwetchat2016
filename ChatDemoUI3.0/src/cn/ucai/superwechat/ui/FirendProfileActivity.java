package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.io.Serializable;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.MFGT;

/**
 * Created by Administrator on 2016/11/8.
 *
 */

public class FirendProfileActivity extends BaseActivity implements View.OnClickListener{
    private ImageView mImgback;
    private TextView mtvTitle;
    private ImageView mImgAvatar;
    private TextView mtvUserName;
    private TextView mtvNickName;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        user = (User) getIntent().getSerializableExtra(I.User.USER_NAME);
        if(user==null){
          MFGT.finish(this);
        }
        initView();

    }


    private void initView() {
        mImgback= (ImageView) findViewById(R.id.img_back);
        mtvTitle= (TextView) findViewById(R.id.txt_title);
        mImgAvatar= (ImageView) findViewById(R.id.Iv_Frient_Profile);
        mtvNickName= (TextView) findViewById(R.id.tv_Frient_Profile_Nick);
        mtvUserName= (TextView) findViewById(R.id.tv_Frient_Profile_Name);
         mImgback.setVisibility(View.VISIBLE);
        mtvTitle.setVisibility(View.VISIBLE);
        mtvTitle.setText(getString(R.string.userinfo_txt_profile));
        setUserInfo();
        mImgback.setOnClickListener(this);
    }
    private void setUserInfo() {
        EaseUserUtils.setAppUserAvatar(this,user.getMUserName(),mImgAvatar);
        EaseUserUtils.setAppUserNick(user.getMUserName(),mtvNickName);
        EaseUserUtils.setAppUserNameWithNo(user.getMUserName(),mtvUserName);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                MFGT.finish(FirendProfileActivity.this);
                break;
        }
    }
}
