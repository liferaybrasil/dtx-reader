package com.liferay.smartbag;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class StatusActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status_activity);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			String status = extras.getString("status");
			TextView textView = (TextView)findViewById(R.id.status);
			textView.setText(status);
		}
	}
}
