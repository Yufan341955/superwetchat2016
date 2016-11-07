package cn.ucai.superwechat.ui;

import java.io.ByteArrayOutputStream;

import com.alipay.security.mobile.module.commonutils.CommonUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfileActivity extends BaseActivity implements OnClickListener{
	private static final String TAG=UserProfileActivity.class.getSimpleName();
	private static final int REQUESTCODE_PICK = 1;
	private static final int REQUESTCODE_CUTTING = 2;

	private TextView tvNickName;
	private TextView tvUsername;
	private ProgressDialog dialog;

	private ImageView mImgback,mImgAvatar;
	private TextView mtvTitle;
	private RelativeLayout mRlUserAvatar;
	private LinearLayout mLNick,mLUserName;
	private User user=null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.em_activity_user_profile);
		initView();
		initListener();
		user = EaseUserUtils.getCurrentAppUserInfo();
	}
	
	private void initView() {
		mImgAvatar= (ImageView) findViewById(R.id.iv_profile_avatar);
		tvUsername = (TextView) findViewById(R.id.tv_profile_weixinhao);
		tvNickName = (TextView) findViewById(R.id.tv_profile_nickname);
		mRlUserAvatar = (RelativeLayout) findViewById(R.id.layout_title);
		mLNick= (LinearLayout) findViewById(R.id.layout_profile_nick);
		mLUserName= (LinearLayout) findViewById(R.id.layout_weixinhao);
		mImgback= (ImageView) findViewById(R.id.img_back);
		mtvTitle= (TextView) findViewById(R.id.txt_title);
		mImgback.setVisibility(View.VISIBLE);
		mtvTitle.setVisibility(View.VISIBLE);
		mtvTitle.setText(getString(R.string.title_user_profile));
	}
	
	private void initListener() {
		EaseUserUtils.setCurrentAppUserAvatar(this,mImgAvatar);
		EaseUserUtils.setCurrentAppNick(tvNickName);
		EaseUserUtils.setCurrentAppUserName(tvUsername);
		mRlUserAvatar.setOnClickListener(this);
		mLNick.setOnClickListener(this);
		mLUserName.setOnClickListener(this);
		mImgback.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_title:
			uploadHeadPhoto();
			break;
		case R.id.layout_profile_nick:
			final EditText editText = new EditText(this);
			editText.setText(user.getMUserNick());
			new Builder(this).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
					.setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String nickString = editText.getText().toString().trim();
							if (TextUtils.isEmpty(nickString)) {
								Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
								return;
							}
							if(nickString.equals(user.getMUserNick())){
								Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_be_modify), Toast.LENGTH_SHORT).show();
								return;
							}
							updateRemoteNick(nickString);
						}
					}).setNegativeButton(R.string.dl_cancel, null).show();
			break;
		case R.id.layout_weixinhao:
			Toast.makeText(UserProfileActivity.this, R.string.User_name_cannot_be_modify, Toast.LENGTH_SHORT).show();
			break;
		case R.id.img_back:
			MFGT.finish(this);
			break;

		default:
			break;
		}

	}
	
	public void asyncFetchUserInfo(String username){
		SuperWeChatHelper.getInstance().getUserProfileManager().asyncGetUserInfo(username, new EMValueCallBack<EaseUser>() {
			
			@Override
			public void onSuccess(EaseUser user) {
				if (user != null) {
				    SuperWeChatHelper.getInstance().saveContact(user);
				    if(isFinishing()){
				        return;
				    }
					tvNickName.setText(user.getNick());
					if(!TextUtils.isEmpty(user.getAvatar())){
						 Glide.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.default_hd_avatar).into(mImgAvatar);
					}else{
					    Glide.with(UserProfileActivity.this).load(R.drawable.default_nor_avatar).into(mImgAvatar);
					}
				}
			}
			
			@Override
			public void onError(int error, String errorMsg) {
			}
		});
	}
	
	
	
	private void uploadHeadPhoto() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.dl_title_upload_photo);
		builder.setItems(new String[] { getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload) },
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0:
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_no_support),
									Toast.LENGTH_SHORT).show();
							break;
						case 1:
							Intent pickIntent = new Intent(Intent.ACTION_PICK,null);
							pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
							startActivityForResult(pickIntent, REQUESTCODE_PICK);
							break;
						default:
							break;
						}
					}
				});
		builder.create().show();
	}
	
	

	private void updateRemoteNick(final String nickName) {
		dialog = ProgressDialog.show(this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean updatenick = SuperWeChatHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(nickName);
				if (UserProfileActivity.this.isFinishing()) {
					return;
				}
				if (!updatenick) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
									.show();
							dialog.dismiss();
						}
					});
				} else {
					updateAppNick(nickName);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dialog.dismiss();
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
									.show();
							tvNickName.setText(nickName);
						}
					});
				}
			}
		}).start();
	}

	private void updateAppNick(String nick) {
		NetDao.updateNick(this, user.getMUserName(), nick, new OkHttpUtils.OnCompleteListener<Result>() {
			@Override
			public void onSuccess(Result result) {
				  L.e(TAG,"result="+result);
				  if(result!=null&&result.isRetMsg()){
					  Gson gson=new Gson();
					  String json=result.getRetData().toString();
                        User u= gson.fromJson(json,User.class);
					    updateLocatUser(u);
				  }else {
					  Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT).show();
					  dialog.dismiss();
				  }

			}

			@Override
			public void onError(String error) {
               L.e(TAG,"error="+error);
				dialog.dismiss();
			}
		});
	}

	private void updateLocatUser(User u) {
		user=u;
		SuperWeChatHelper.getInstance().saveAppContact(u);
		EaseUserUtils.setCurrentAppNick(tvNickName);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUESTCODE_PICK:
			if (data == null || data.getData() == null) {
				return;
			}
			startPhotoZoom(data.getData());
			break;
		case REQUESTCODE_CUTTING:
			if (data != null) {
				setPicToView(data);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", true);
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, REQUESTCODE_CUTTING);
	}
	
	/**
	 * save the picture data
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(getResources(), photo);
			mImgAvatar.setImageDrawable(drawable);
			uploadUserAvatar(Bitmap2Bytes(photo));
		}

	}
	
	private void uploadUserAvatar(final byte[] data) {
		dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
		new Thread(new Runnable() {

			@Override
			public void run() {
				final String avatarUrl = SuperWeChatHelper.getInstance().getUserProfileManager().uploadUserAvatar(data);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
						if (avatarUrl != null) {
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_fail),
									Toast.LENGTH_SHORT).show();
						}

					}
				});

			}
		}).start();

		dialog.show();
	}
	
	
	public byte[] Bitmap2Bytes(Bitmap bm){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
}
