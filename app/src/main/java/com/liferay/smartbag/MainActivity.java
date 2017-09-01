package com.liferay.smartbag;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button mudar = (Button)findViewById(R.id.mudar);

		mudar.setOnClickListener(view -> {
			RadioGroup grupo = (RadioGroup)findViewById(R.id.grupo);
			RadioButton radio = (RadioButton)findViewById(
				grupo.getCheckedRadioButtonId());

			if (radio != null) {
				Intent intent = new Intent(this, StatusActivity.class);
				intent.putExtra("status", radio.getText());
				startActivity(intent);
			}
		});

	}

}
