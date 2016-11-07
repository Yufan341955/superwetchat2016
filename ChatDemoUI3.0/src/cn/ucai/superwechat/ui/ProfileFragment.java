package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.redpacketui.utils.RedPacketUtil;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;

import cn.ucai.superwechat.Constant;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.MFGT;

/**
 * Created by Administrator on 2016/11/5.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{

    RelativeLayout mRelativeLayout,mRlMoney,mRlSetting;
    ImageView mIvAvatar;
    TextView mtvNick,mtvUserName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        mRelativeLayout= (RelativeLayout) view.findViewById(R.id.layout_profile);
        mIvAvatar= (ImageView) view.findViewById(R.id.profile_avatar);
        mtvNick= (TextView) view.findViewById(R.id.profile_nick);
        mtvUserName= (TextView) view.findViewById(R.id.profile_username);
        mRlMoney= (RelativeLayout) view.findViewById(R.id.profile_money);
        mRlSetting= (RelativeLayout) view.findViewById(R.id.profile_setting);
        setListener();
        return view;
    }

    private void setListener() {
        mRelativeLayout.setOnClickListener(this);
        mRlMoney.setOnClickListener(this);
        mRlSetting.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        setUserInfo();
    }

    private void setUserInfo() {
        EaseUserUtils.setCurrentAppUserAvatar(getActivity(),mIvAvatar);
        EaseUserUtils.setCurrentAppNick(mtvNick);
        EaseUserUtils.setCurrentAppUserNameWithNo(mtvUserName);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //red packet code : 进入零钱页面
            case R.id.profile_money:
                RedPacketUtil.startChangeActivity(getActivity());
            break;
            //end packet code
            case R.id.profile_setting:
                MFGT.gotoSettingsActivity(getActivity());
            break;
            case R.id.layout_profile:
            break;
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(((MainActivity)getActivity()).isConflict){
            outState.putBoolean("isConflict", true);
        }else if(((MainActivity)getActivity()).getCurrentAccountRemoved()){
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserInfo();
    }
}
