package com.example.mdxj.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mdxj.R;

public class UpdateNameActivity extends Activity{
    private String oriName = "";
    private EditText et_nick;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nick);        

        Intent intent = getIntent();
        if (intent.hasExtra("OriName")) {

            oriName = (String) intent.getSerializableExtra("OriName");
        }
        
        et_nick= (EditText) this.findViewById(R.id.et_nick);
        et_nick.setText(oriName);
        
        TextView  tv_save= (TextView) this.findViewById(R.id.tv_save);
        tv_save.setOnClickListener(new OnClickListener(){

        @Override
        public void onClick(View v) {
            String newNick = et_nick.getText().toString().trim();
            if(oriName.equals(newNick)||newNick.equals("")||newNick.equals("0")) {
                return;
            }  
            
            Intent intent = new Intent();
            intent.putExtra("NewName", newNick);
            setResult(RESULT_OK, intent);
            finish();
        }
      }); 
    }
    
    public void back(View view) {
        finish();
    }
}
