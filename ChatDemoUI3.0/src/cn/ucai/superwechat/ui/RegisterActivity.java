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

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.MD5;
import cn.ucai.superwechat.utils.MFGT;

import com.hyphenate.exceptions.HyphenateException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * register screen
 * 
 */
public class RegisterActivity extends BaseActivity {
	private EditText mEtUserName;
	private EditText mEtPassWord;
	private EditText mEtconfirmPwd;
	private EditText mEtNick;
	private ImageView mIvBack;
    private TextView mTvTitle;
	Context mContext;
	ProgressDialog pd=null;
	String username;
	String nickname;
	String pwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_register);
		mEtUserName = (EditText) findViewById(R.id.et_username);
		mEtPassWord = (EditText) findViewById(R.id.et_password);
		mEtNick= (EditText) findViewById(R.id.et_nick);
		mEtconfirmPwd = (EditText) findViewById(R.id.et_confirm_password);
		mIvBack= (ImageView) findViewById(R.id.img_back);
		mTvTitle= (TextView) findViewById(R.id.txt_title);
        mContext=RegisterActivity.this;
		initView();
	}

	private void initView() {
		mIvBack.setVisibility(View.VISIBLE);
		mTvTitle.setVisibility(View.VISIBLE);
		mTvTitle.setText(R.string.register);
		mIvBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MFGT.finish(RegisterActivity.this);
			}
		});
	}

	public void register(View view) {
		username = mEtUserName.getText().toString().trim();
		nickname=mEtNick.getText().toString().trim();
		pwd = mEtPassWord.getText().toString().trim();
		String confirm_pwd = mEtconfirmPwd.getText().toString().trim();
		if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
			mEtUserName.requestFocus();
			return;
		}else if(TextUtils.isEmpty(nickname)){
			Toast.makeText(this, getResources().getString(R.string.Nick_cannot_be_empty), Toast.LENGTH_SHORT).show();
			mEtNick.requestFocus();
			return;
		}

		else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
			mEtPassWord.requestFocus();
			return;
		} else if (TextUtils.isEmpty(confirm_pwd)) {
			Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
			mEtconfirmPwd.requestFocus();
			return;
		} else if (!pwd.equals(confirm_pwd)) {
			Toast.makeText(this, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
			return;
		}

		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			pd = new ProgressDialog(this);
			pd.setMessage(getResources().getString(R.string.Is_the_registered));
			pd.show();
			registerAppServer();



		}
	}

	private void registerAppServer() {
		NetDao.register(mContext, username, nickname, pwd, new OkHttpUtils.OnCompleteListener<Result>() {
			@Override
			public void onSuccess(Result result) {
				if(result==null){
					pd.dismiss();
				}else {
					if (result.isRetMsg()) {
						registerEMServer();
					} else {

						if(result.getRetCode()== I.MSG_REGISTER_USERNAME_EXISTS){
							Toast.makeText(RegisterActivity.this, R.string.MSG_101, Toast.LENGTH_SHORT).show();
							pd.dismiss();
						}
						else {
							unregisterAppServer();
						}
					}
				}
			}

			@Override
			public void onError(String error) {
              pd.dismiss();
			}
		});

	}

	private void unregisterAppServer() {
         NetDao.unregister(mContext, username, new OkHttpUtils.OnCompleteListener<Result>() {
			 @Override
			 public void onSuccess(Result result) {
				 pd.dismiss();
			 }

			 @Override
			 public void onError(String error) {
                 pd.dismiss();
			 }
		 });
	}

	private void registerEMServer() {
		new Thread(new Runnable() {
			public void run() {
				try {
					// call method in SDK
					EMClient.getInstance().createAccount(username, MD5.getMessageDigest(pwd));
					runOnUiThread(new Runnable() {
						public void run() {
							if (!RegisterActivity.this.isFinishing())
								pd.dismiss();
							// save current user
							SuperWeChatHelper.getInstance().setCurrentUserName(username);
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
							finish();
						}
					});
				} catch (final HyphenateException e) {
					unregisterAppServer();
					runOnUiThread(new Runnable() {
						public void run() {
							if (!RegisterActivity.this.isFinishing())
								pd.dismiss();
							int errorCode=e.getErrorCode();
							if(errorCode==EMError.NETWORK_ERROR){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.USER_ALREADY_EXIST){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.USER_AUTHENTICATION_FAILED){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.USER_ILLEGAL_ARGUMENT){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed), Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			}
		}).start();
	}

	public void back(View view) {
		MFGT.finish(this);
	}

}
