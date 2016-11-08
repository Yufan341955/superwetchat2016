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

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.adapter.NewFriendsMsgAdapter;
import cn.ucai.superwechat.db.InviteMessgeDao;
import cn.ucai.superwechat.domain.InviteMessage;
import cn.ucai.superwechat.utils.MFGT;

import java.util.List;

/**
 * Application and notification
 *
 */
public class NewFriendsMsgActivity extends BaseActivity {
     private ImageView mImgback;
	 private TextView mtvTiltle;
	 private ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_new_friends_msg);
		initView();

		InviteMessgeDao dao = new InviteMessgeDao(this);
		List<InviteMessage> msgs = dao.getMessagesList();

		NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs); 
		listView.setAdapter(adapter);
		dao.saveUnreadMessageCount(0);
		
	}

	private void initView() {
		mImgback= (ImageView) findViewById(R.id.img_back);
		mtvTiltle= (TextView) findViewById(R.id.txt_title);
		listView = (ListView) findViewById(R.id.list);
		mImgback.setVisibility(View.VISIBLE);
		mtvTiltle.setVisibility(View.VISIBLE);
		mtvTiltle.setText(getString(R.string.recommended_friends));
		mImgback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MFGT.finish(NewFriendsMsgActivity.this);
			}
		});
	}


}
