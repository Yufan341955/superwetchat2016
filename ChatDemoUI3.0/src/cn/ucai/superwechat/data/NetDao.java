package cn.ucai.superwechat.data;

import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.io.File;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.utils.MD5;


/**
 * Created by Administrator on 2016/10/17.
 */
public class NetDao {

    public static void register(Context context, String username, String nick, String password, OkHttpUtils.OnCompleteListener<Result> listener){
        OkHttpUtils<Result> utils=new OkHttpUtils<Result>(context);
        utils.setRequestUrl(I.REQUEST_REGISTER)
                .addParam(I.User.USER_NAME,username)
                .addParam(I.User.NICK,nick)
                .addParam(I.User.PASSWORD, MD5.getMessageDigest(password))
                .post()
                .targetClass(Result.class)
                .execute(listener);

    }
    public static void unregister(Context context, String username,OkHttpUtils.OnCompleteListener<Result> listener){
        OkHttpUtils<Result> utils=new OkHttpUtils<Result>(context);
        utils.setRequestUrl(I.REQUEST_UNREGISTER)
                .addParam(I.User.USER_NAME,username)
                .targetClass(Result.class)
                .execute(listener);

    }
    public static void login(Context context, String username, String password, OkHttpUtils.OnCompleteListener<Result> listener){
        OkHttpUtils<Result> utils=new OkHttpUtils<Result>(context);
        utils.setRequestUrl(I.REQUEST_LOGIN)
                .addParam(I.User.USER_NAME,username)
                .addParam(I.User.PASSWORD,MD5.getMessageDigest(password))
                .targetClass(Result.class)
                .execute(listener);

    }
    public static void updateNick(Context context, String username, String nick, OkHttpUtils.OnCompleteListener<Result> listener){
        OkHttpUtils<Result> utils=new OkHttpUtils<Result>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_USER_NICK)
                .addParam(I.User.USER_NAME,username)
                .addParam(I.User.NICK,nick)
                .targetClass(Result.class)
                .execute(listener);

    }
    public static void updateAvatar(Context context, String username, File file, OkHttpUtils.OnCompleteListener<Result> listener){
        OkHttpUtils<Result> utils=new OkHttpUtils<Result>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_AVATAR)
                .addParam(I.NAME_OR_HXID,username)
                .addParam(I.AVATAR_TYPE,"user_avatar")
                .addFile2(file)
                .post()
                .targetClass(Result.class)
                .execute(listener);
    }
    public static void searchUser(Context context, String username, OkHttpUtils.OnCompleteListener<Result> listener){
        OkHttpUtils<Result> utils=new OkHttpUtils<Result>(context);
        utils.setRequestUrl(I.REQUEST_FIND_USER)
                .addParam(I.User.USER_NAME,username)
                .targetClass(Result.class)
                .execute(listener);

    }
    public static void addContact(Context context, String username, String cusername, OkHttpUtils.OnCompleteListener<Result> listener){
        OkHttpUtils<Result> utils=new OkHttpUtils<Result>(context);
        utils.setRequestUrl(I.REQUEST_ADD_CONTACT)
                .addParam(I.Contact.USER_NAME,username)
                .addParam(I.Contact.CU_NAME,cusername)
                .targetClass(Result.class)
                .execute(listener);

    }
    public static void deleteContact(Context context,String username,String cusername, OkHttpUtils.OnCompleteListener<Result> listener){
        OkHttpUtils<Result> utils=new OkHttpUtils<Result>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_CONTACT)
                .addParam(I.Contact.USER_NAME,username)
                .addParam(I.Contact.CU_NAME,cusername)
                .targetClass(Result.class)
                .execute(listener);
    }
    public static void downloadContact(Context context, String username, OkHttpUtils.OnCompleteListener<Result> listener){
        OkHttpUtils<Result> utils=new OkHttpUtils<Result>(context);
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME,username)
                .targetClass(Result.class)
                .execute(listener);
    }
    public static void createGroup(Context context, EMGroup emGroup, File file, OkHttpUtils.OnCompleteListener<Result> listener){
        OkHttpUtils<Result> utils=new OkHttpUtils<Result>(context);
        utils.setRequestUrl(I.REQUEST_CREATE_GROUP)
                .addParam(I.Group.HX_ID,emGroup.getGroupId())
                .addParam(I.Group.NAME,emGroup.getGroupName())
                .addParam(I.Group.DESCRIPTION,emGroup.getDescription())
                .addParam(I.Group.OWNER,emGroup.getOwner())
                .addParam(I.Group.IS_PUBLIC,String.valueOf(emGroup.isPublic()))
                .addParam(I.Group.ALLOW_INVITES,String.valueOf(emGroup.isAllowInvites()))
                .addFile2(file)
                .post()
                .targetClass(Result.class)
                .execute(listener);
    }
    public static void createGroup(Context context, EMGroup emGroup, OkHttpUtils.OnCompleteListener<Result> listener){
        OkHttpUtils<Result> utils=new OkHttpUtils<Result>(context);
        utils.setRequestUrl(I.REQUEST_CREATE_GROUP)
                .addParam(I.Group.HX_ID,emGroup.getGroupId())
                .addParam(I.Group.NAME,emGroup.getGroupName())
                .addParam(I.Group.DESCRIPTION,emGroup.getDescription())
                .addParam(I.Group.OWNER,emGroup.getOwner())
                .addParam(I.Group.IS_PUBLIC,String.valueOf(emGroup.isPublic()))
                .addParam(I.Group.ALLOW_INVITES,String.valueOf(emGroup.isAllowInvites()))
                .post()
                .targetClass(Result.class)
                .execute(listener);
    }

    public static void addGroupMembers(Context context, EMGroup emGroup, OkHttpUtils.OnCompleteListener<String> listener){
        String members="";
        for(String m:emGroup.getMembers()){
            if(!m.equals(EMClient.getInstance().getCurrentUser())){
                members+=m+",";
            }
        }
        members=members.substring(0,members.length()-1);
        OkHttpUtils<String> utils=new OkHttpUtils<String>(context);
        utils.setRequestUrl(I.REQUEST_ADD_GROUP_MEMBERS)
                .addParam(I.Member.GROUP_HX_ID,emGroup.getGroupId())
                .addParam(I.Member.USER_NAME,members)
                .targetClass(String.class)
                .execute(listener);
    }
    public static void deleteMembers(Context context, String GroupId,String username, OkHttpUtils.OnCompleteListener<String> listener){

        OkHttpUtils<String> utils=new OkHttpUtils<String>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_GROUP_MEMBER)
                .addParam(I.Member.GROUP_HX_ID,GroupId)
                .addParam(I.Member.USER_NAME,username)
                .targetClass(String.class)
                .execute(listener);
    }
}
