package com.wuxianyingke.property.activities;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;

public class TextInputActivity extends BaseActivity {
	private TextView topbar_txt,topbar_right;
    private Button topbar_left;
    private String intentcontent=null;
    
	private EditText content;	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.activity_text_input);
		setImmerseLayout(findViewById(R.id.IncludeMainTopBarInclude));
		intentcontent = getIntent().getStringExtra("content");
		initWidgets();

		Timer timer = new Timer();  
	    timer.schedule(new TimerTask()  
		     {  
		         public void run()  
		         {  
		             InputMethodManager inputManager =  
		                 (InputMethodManager)content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
		             inputManager.showSoftInput(content, 0);  
		         }  
		     },  
     	998);  
		
	}
	
private void initWidgets() {
		
		content = (EditText)findViewById(R.id.content);
		if(intentcontent!=null)
			content.setText(intentcontent);
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("描述");
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.GONE);
		topbar_left.setEnabled(false);
		topbar_right = (TextView) findViewById(R.id.topbar_right);
		topbar_right.setText("确定");
		topbar_right.setTextColor(Color.parseColor("#FFFFFF"));
		topbar_right.setVisibility(View.VISIBLE);
		topbar_right.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View v) {
	            	String result = content.getText().toString().trim();
	                Intent intent = new Intent();
	                intent.putExtra("textInputResult", result);

	                setResult(-1, intent);
	                
	                finish();
	            }
	        });

		
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
}
