package cn.ucai.superwechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.hyphenate.easeui.domain.User;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.ui.AddContactActivity;
import cn.ucai.superwechat.ui.FirendProfileActivity;
import cn.ucai.superwechat.ui.LoginActivity;
import cn.ucai.superwechat.ui.MainActivity;
import cn.ucai.superwechat.ui.RegisterActivity;
import cn.ucai.superwechat.ui.SettingsActivity;
import cn.ucai.superwechat.ui.UserProfileActivity;


public class MFGT {
    public static void finish(Activity activity){
        activity.finish();
        activity.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
    }
    public static void gotoMainActivity(Activity context){
        startActivity(context, MainActivity.class);
    }
    public static void startActivity(Activity context,Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(context,cls);
        context.startActivity(intent);
    }
    public static void startActivity(Context context, Intent intent){
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
    }
    public static void gotoLogin(Activity context){
        startActivity(context, LoginActivity.class);
    }
    public static void gotoRegister(Activity context){
        startActivity(context, RegisterActivity.class);
    }
    public static void startActivityForResult(Activity context,Intent intent,int requestCode){
        context.startActivityForResult(intent,requestCode);
        context.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
    }
    public static void gotoSettingsActivity(Activity context){
        startActivity(context, SettingsActivity.class);

    }
    public static void gotoUserProfileActivity(Activity context){
        startActivity(context, UserProfileActivity.class);

    }
    public static void gotoAddFirend(Activity context){
        startActivity(context, AddContactActivity.class);
    }
    public static void gotoFirendProfile(Activity context, User user){
         Intent intent=new Intent();
        intent.setClass(context, FirendProfileActivity.class);
        intent.putExtra(I.User.USER_NAME,user);
        startActivity(context,intent);
    }
}
