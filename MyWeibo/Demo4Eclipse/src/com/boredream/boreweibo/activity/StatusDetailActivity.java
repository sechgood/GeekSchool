package com.boredream.boreweibo.activity;

import android.os.Bundle;

import com.boredream.boreweibo.BaseActivity;
import com.boredream.boreweibo.R;
import com.boredream.boreweibo.api.SimpleRequestListener;
import com.boredream.boreweibo.entity.Status;

public class StatusDetailActivity extends BaseActivity {

	private Status status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_status_detail);
		
		status = (Status) intent.getSerializableExtra("status");
		
		progressDialog.show();
		weiboApi.commentsShow(status.getId(), 1, 
				new SimpleRequestListener(this, progressDialog){

					@Override
					public void onComplete(String response) {
						super.onComplete(response);
						
						showLog("status comments = " + response);
					}

					@Override
					public void onDone() {
						super.onDone();
					}
			
		});
	}
	
}


