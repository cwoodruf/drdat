package com.android;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class Screen2 extends Activity {
	public void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.screen2);
		Button b = (Button) findViewById(R.id.btnClick2);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});
	}
}
