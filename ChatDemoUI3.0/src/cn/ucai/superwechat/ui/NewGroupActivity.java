/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager.EMGroupOptions;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.L;

import com.hyphenate.easeui.domain.Group;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.exceptions.HyphenateException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class NewGroupActivity extends BaseActivity {
	private static final int REQUESTCODE_PICK = 1;
	private static final int REQUESTCODE_CUTTING = 2;
	private static final int REQUESTCODE_PICK_MEMBER = 3;
	private static final String TAG=NewGroupActivity.class.getSimpleName();
	private EditText metGroupName;
	private ProgressDialog progressDialog;
	private EditText metIntroduction;
	private CheckBox mcbPublic;
	private CheckBox mcbMermber;
	private TextView secondTextView;
	private LinearLayout mLayout;
	private ImageView mIvGroupAvatar;
	private File Avatarfile=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_new_group);
		initView();
		setListener();
	}

	private void setListener() {
		mcbPublic.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					secondTextView.setText(R.string.join_need_owner_approval);
				}else{
					secondTextView.setText(R.string.Open_group_members_invited);
				}
			}
		});
		mLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				uploadHeadPhoto();
			}
		});
	}

	private void initView() {
		metGroupName = (EditText) findViewById(R.id.edit_group_name);
		metIntroduction = (EditText) findViewById(R.id.edit_group_introduction);
		mcbPublic = (CheckBox) findViewById(R.id.cb_public);
		mcbMermber = (CheckBox) findViewById(R.id.cb_member_inviter);
		secondTextView = (TextView) findViewById(R.id.second_desc);
		mLayout= (LinearLayout) findViewById(R.id.layout_group_avatar);
		mIvGroupAvatar= (ImageView) findViewById(R.id.iv_Group);
	}

	/**
	 * @param v
	 */
	public void save(View v) {
		String name = metGroupName.getText().toString();
		if (TextUtils.isEmpty(name)) {
		    new EaseAlertDialog(this, R.string.Group_name_cannot_be_empty).show();
		} else {
			// select from contact list
			startActivityForResult(new Intent(this, GroupPickContactsActivity.class).putExtra("groupName", name), REQUESTCODE_PICK_MEMBER);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
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
			case REQUESTCODE_PICK_MEMBER:
				if (resultCode == RESULT_OK) {
					creatEMGroup(data);
				}
				break;
			default:
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void creatEMGroup(final Intent data) {
		String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
		final String st2 = getResources().getString(R.string.Failed_to_create_groups);
		//new group
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(st1);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				final String groupName = metGroupName.getText().toString().trim();
				String desc = metIntroduction.getText().toString();
				String[] members = data.getStringArrayExtra("newmembers");
				try {
					EMGroupOptions option = new EMGroupOptions();
					option.maxUsers = 200;

					String reason = NewGroupActivity.this.getString(R.string.invite_join_group);
					reason  = EMClient.getInstance().getCurrentUser() + reason + groupName;

					if(mcbPublic.isChecked()){
						option.style = mcbMermber.isChecked() ? EMGroupStyle.EMGroupStylePublicJoinNeedApproval : EMGroupStyle.EMGroupStylePublicOpenJoin;
					}else{
						option.style = mcbMermber.isChecked()?EMGroupStyle.EMGroupStylePrivateMemberCanInvite:EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
					}
					EMGroup Group = EMClient.getInstance().groupManager().createGroup(groupName, desc, members, reason, option);
					createAppGroup(Group);

				} catch (final HyphenateException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}

			}
		}).start();
	}

	private void createGroupSuccess(){
		 runOnUiThread(new Runnable() {
			 public void run() {
				 progressDialog.dismiss();
				 setResult(RESULT_OK);
				 finish();
			 }
		 });
	 }
	private void createAppGroup(EMGroup group) {
		if(Avatarfile==null){
            NetDao.createGroup(this, group, new OkHttpUtils.OnCompleteListener<Result>() {
				@Override
				public void onSuccess(Result result) {
					aftercreateGroup(result);
				}

				@Override
				public void onError(String error) {
					Toast.makeText(NewGroupActivity.this, error, Toast.LENGTH_SHORT).show();
				}
			});
		}else {
			NetDao.createGroup(this, group, Avatarfile,new OkHttpUtils.OnCompleteListener<Result>() {
				@Override
				public void onSuccess(Result result) {
					aftercreateGroup(result);
				}

				@Override
				public void onError(String error) {
					Toast.makeText(NewGroupActivity.this, error, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	private void aftercreateGroup(Result result) {
		if(result!=null&&result.isRetMsg()){
			String json=result.getRetData().toString();
			Gson gson=new Gson();
			Group group=gson.fromJson(json,Group.class);
			L.e(TAG,"group="+group.toString());
			createGroupSuccess();
		}else {
			Toast.makeText(NewGroupActivity.this,"创建群组失败！", Toast.LENGTH_SHORT).show();
		}
	}

	public void back(View view) {
		finish();
	}
	private void uploadHeadPhoto() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dl_title_upload_photo);
		builder.setItems(new String[] { getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload) },
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
							case 0:
								Toast.makeText(NewGroupActivity.this, getString(R.string.toast_no_support),
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

	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(getResources(), photo);
			mIvGroupAvatar.setImageDrawable(drawable);
            saveBitmapFile(picdata);
		}

	}
	public void saveBitmapFile(Intent picData){
		Bundle extras = picData.getExtras();
		if (extras != null) {
			Bitmap bitmap=extras.getParcelable("data");
			String imagepath= EaseImageUtils.getImagePath(System.currentTimeMillis()+ I.AVATAR_SUFFIX_PNG);
			File file=new File(imagepath);
			L.e(TAG,"imagePath="+file.getAbsolutePath());
			try {
				BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file));
				bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
				bos.flush();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Avatarfile=file;
		}

	}
}
