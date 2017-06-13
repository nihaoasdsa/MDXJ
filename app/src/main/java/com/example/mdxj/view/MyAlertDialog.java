package com.example.mdxj.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mdxj.R;
import com.example.mdxj.util.Tool;

/**
 * Created by 008 on 2017/6/13 0013.
 * 正常弹出的dialog
 */
public class MyAlertDialog extends Dialog implements View.OnClickListener {
    private  Context context;
    private TextView tv_title;
    private TextView tv_content;
    private Button rightBtn;
    private Button leftBtn;
  private String strTitle;
    private String str_content;
    private CallAlertListerent alertListerent;
    private String leftBtnStr;
    private String rightBtnStr;


    public interface CallAlertListerent{
        void rightBtnListerent();
        void leftBtnListerent();
    }

    /**
     * cotent 承接上下文的
     * 标题名字
     * edittext 文本信息
     * left左侧的按钮
     * right右侧的按钮
     * listener 回调监听
     * ****/
    public MyAlertDialog(Context context,String tv_title,String tv_content,String leftButton,String rightButton,CallAlertListerent listerent) {
        super(context, R.style.alertdialog);
        this.context=context;
        this.strTitle=tv_title;
        this.str_content=tv_content;
        this.leftBtnStr=leftButton;
        this.rightBtnStr=rightButton;
        this.alertListerent=listerent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog);
initView();
    }

    private void initView() {
        leftBtn = (Button) findViewById(R.id.ll_doing_ng);
        leftBtn.setOnClickListener(this);
        leftBtn.setText(leftBtnStr);
        if(Tool.isEmpty(leftBtnStr)){
            leftBtn.setVisibility(View.GONE);
        }
        rightBtn = (Button) findViewById(R.id.ll_doing_ok);
        rightBtn.setOnClickListener(this);
        rightBtn.setText(rightBtnStr);
        if(Tool.isEmpty(rightBtnStr)){
            rightBtn.setVisibility(View.GONE);
        }
        tv_title= (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content.setText(str_content);
        tv_title.setText(strTitle);
        setCancelable(false);
    }
    @Override
    public void onClick(View v) {
  switch (v.getId()){
      case R.id.ll_doing_ok:
          alertListerent.rightBtnListerent();
          dismiss();
          break;
      case R.id.ll_doing_ng:
          alertListerent.leftBtnListerent();
          dismiss();
          break;
  }
    }
}
