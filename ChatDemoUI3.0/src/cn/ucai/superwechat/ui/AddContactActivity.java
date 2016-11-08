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
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.widget.EaseAlertDialog;

public class AddContactActivity extends BaseActivity implements View.OnClickListener{
	private static final String TAG=AddContactActivity.class.getSimpleName();

	private EditText mEtUserName;
	private String toAddUsername;
	private ProgressDialog progressDialog;
	private ImageView mImgback;
	private TextView tvTitle;
	private TextView tvRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_add_contact);
		mEtUserName = (EditText) findViewById(R.id.edit_note);
		String strUserName = getResources().getString(R.string.user_name);
		mEtUserName.setHint(strUserName);

		mImgback= (ImageView) findViewById(R.id.img_back);
		tvTitle= (TextView) findViewById(R.id.txt_title);
		tvRight= (TextView) findViewById(R.id.txt_right);
		mImgback.setVisibility(View.VISIBLE);
		tvTitle.setVisibility(View.VISIBLE);
		tvRight.setVisibility(View.VISIBLE);
		tvTitle.setText(getString(R.string.menu_addfriend));
		tvRight.setText(getString(R.string.search));
		tvRight.setOnClickListener(this);
		mImgback.setOnClickListener(this);
	}
	
	
	/**
	 * search contact
	 *
	 */
	public void searchContact() {
		final String name = mEtUserName.getText().toString();

		toAddUsername = name;
			if(TextUtils.isEmpty(name)) {
				new EaseAlertDialog(this, R.string.Please_enter_a_username).show();
				return;
			}
		progressDialog = new ProgressDialog(this);
		String stri = getResources().getString(R.string.addcontact_search);
		progressDialog.setMessage(stri);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

        searchAppUser();
	}

	private void searchAppUser() {
		NetDao.searchUser(this, toAddUsername, new OkHttpUtils.OnCompleteListener<Result>() {
			@Override
			public void onSuccess(Result result) {
				L.e(TAG,"searchAppUser(),result="+result);
				progressDialog.dismiss();
				if(result!=null&&result.isRetMsg()){
					Gson gson=new Gson();
					String json=result.getRetData().toString();
					User user=gson.fromJson(json,User.class);
                   if(user!=null){
                      MFGT.gotoFirendProfile(AddContactActivity.this,user);
				   }
				}else {
					Toast.makeText(AddContactActivity.this, getString(R.string.search_user_fail), Toast.LENGTH_SHORT).show();
					progressDialog.dismiss();
				}
			}

			@Override
			public void onError(String error) {
                L.e(TAG,"error="+error);
				Toast.makeText(AddContactActivity.this, getString(R.string.search_user_fail), Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
			}
		});
	}

	/**
	 *  add contact
	 * @param view
	 */
	public void addContact(View view){
		if(EMClient.getInstance().getCurrentUser().equals(mEtUserName.getText().toString())){
			new EaseAlertDialog(this, R.string.not_add_myself).show();
			return;
		}
		
		if(SuperWeChatHelper.getInstance().getContactList().containsKey(mEtUserName.getText().toString())){
		    //let the user know the contact already in your contact list
		    if(EMClient.getInstance().contactManager().getBlackListUsernames().contains(mEtUserName.getText().toString())){
		        new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
		        return;
		    }
			new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
			return;
		}
		

		
		new Thread(new Runnable() {
			public void run() {
				
				try {
					//demo use a hardcode reason here, you need let user to input if you like
					String s = getResources().getString(R.string.Add_a_friend);
					EMClient.getInstance().contactManager().addContact(toAddUsername, s);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s1 = getResources().getString(R.string.send_successful);
							Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s2 = getResources().getString(R.string.Request_add_buddy_failure);
							Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();
	}
	
	public void back(View v) {
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.img_back:
				MFGT.finish(this);
			break;
			case R.id.txt_right:
				searchContact();
			break;
		}
	}
}
