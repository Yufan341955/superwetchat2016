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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.db.SuperWeChatDBManager;
import cn.ucai.superwechat.db.UserDao;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MD5;
import cn.ucai.superwechat.utils.MFGT;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseCommonUtils;

/**
 * Login screen
 * 
 */
public class LoginActivity extends BaseActivity {
	private static final String TAG = "LoginActivity";
	public static final int REQUEST_CODE_SETNICK = 1;
	private EditText mEtUserName;
	private EditText mEtPassWord;
	private ImageView mIvBack;
	private TextView mTvTitle;
	Context mContext;
	private boolean progressShow;
	private boolean autoLogin = false;
	ProgressDialog pd=null;
	String currentUsername;
	String currentPassword;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// enter the main activity if already logged in
		if (SuperWeChatHelper.getInstance().isLoggedIn()) {
			autoLogin = true;
			startActivity(new Intent(LoginActivity.this, MainActivity.class));

			return;
		}
		setContentView(R.layout.em_activity_login);
		mContext=this;
		mEtUserName = (EditText) findViewById(R.id.et_username);
		mEtPassWord = (EditText) findViewById(R.id.et_password);
		mIvBack= (ImageView) findViewById(R.id.img_back);
		mTvTitle= (TextView) findViewById(R.id.txt_title);
		setListener();
		initView();


	}

	private void initView() {
		if (SuperWeChatHelper.getInstance().getCurrentUsernName() != null) {
			mEtUserName.setText(SuperWeChatHelper.getInstance().getCurrentUsernName());
		}
		mIvBack.setVisibility(View.VISIBLE);
		mTvTitle.setVisibility(View.VISIBLE);
		mTvTitle.setText(R.string.login_btn_action);
	}

	private void setListener() {
		mIvBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MFGT.finish(LoginActivity.this);
			}
		});
		// if user changed, clear the password
		mEtUserName.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mEtPassWord.setText(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	/**
	 * login
	 * 
	 * @param view
	 */
	public void login(View view) {
		if (!EaseCommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}
		currentUsername = mEtUserName.getText().toString().trim();
		currentPassword = mEtPassWord.getText().toString().trim();

		if (TextUtils.isEmpty(currentUsername)) {
			Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(currentPassword)) {
			Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}

		progressShow = true;
		pd = new ProgressDialog(LoginActivity.this);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				Log.d(TAG, "EMClient.getInstance().onCancel");
				progressShow = false;
			}
		});
		pd.setMessage(getString(R.string.Is_landing));
		pd.show();
		loginEmServer();
	}

	private void loginEmServer() {
		// After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
		// close it before login to make sure DemoDB not overlap
		SuperWeChatDBManager.getInstance().closeDB();

		// reset current user name before login
		SuperWeChatHelper.getInstance().setCurrentUserName(currentUsername);

		final long start = System.currentTimeMillis();
		// call login method
		Log.d(TAG, "EMClient.getInstance().login");
		EMClient.getInstance().login(currentUsername, MD5.getMessageDigest(currentPassword), new EMCallBack() {

			@Override
			public void onSuccess() {
				Log.d(TAG, "login: onSuccess");
				loginAppServer();

			}

			@Override
			public void onProgress(int progress, String status) {
				Log.d(TAG, "login: onProgress");
			}

			@Override
			public void onError(final int code, final String message) {
				Log.d(TAG, "login: onError: " + code);
				if (!progressShow) {
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void loginAppServer() {
		NetDao.login(mContext, currentUsername, currentPassword, new OkHttpUtils.OnCompleteListener<Result>() {
			@Override
			public void onSuccess(Result result) {
				L.e(TAG,"result="+result.toString());
				if(result!=null&&result.isRetMsg()){
					String json=result.getRetData().toString();
					Gson gson=new Gson();
					User user= gson.fromJson(json,User.class);
					L.e(TAG,"user="+user.toString());
					if(user!=null) {
						UserDao dao = new UserDao(mContext);
						dao.savaUser(user);
						SuperWeChatHelper.getInstance().setCurrentUser(user);
						loginEMSuccess();
					}else{
						pd.dismiss();
					}

				}else {
					pd.dismiss();
				}

			}

			@Override
			public void onError(String error) {
				L.e(TAG,"error="+error);
				pd.dismiss();
			}
		});

	}

	private void loginEMSuccess() {
		// ** manually load all local groups and conversation
		EMClient.getInstance().groupManager().loadAllGroups();
		EMClient.getInstance().chatManager().loadAllConversations();

		// update current user's display name for APNs
		boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
				SuperWeChatApplication.currentUserNick.trim());
		if (!updatenick) {
			Log.e("LoginActivity", "update current user nick fail");
		}

		if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
			pd.dismiss();
		}
		// get user's info (this should be get from App's server or 3rd party service)
		SuperWeChatHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

		Intent intent = new Intent(LoginActivity.this,
				MainActivity.class);
		startActivity(intent);

		finish();
	}


	/**
	 * register
	 * 
	 * @param view
	 */
	public void register(View view) {
		MFGT.gotoRegister(LoginActivity.this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (autoLogin) {
			return;
		}
		if(SuperWeChatHelper.getInstance().getCurrentUsernName()!=null){
			mEtUserName.setText(SuperWeChatHelper.getInstance().getCurrentUsernName());
		}
	}
}
