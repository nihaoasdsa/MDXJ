package com.example.mdxj.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mdxj.R;
import com.example.mdxj.util.Tool;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by 008 on 2017/6/13 0013.
 */
public class MyEditTextDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private String str;//内容信息
    private EditText editText;//输入文本框
    private TextView tv_name;//标题名字
    private String titlestr;
    private String hintstr;
    private Button btn_ok, btn_cancel;//确定，取消按钮
    private CallOnClickListener onClickListener;
    private String leftBtnStr;
    private String rightBtnStr;


    //定义接口用来回调确定监听
    public interface CallOnClickListener {
        void RightBtnOnClick(String result);
    }

    /**
     * cotent 承接上下文的
     * 标题名字
     * edittext 文本信息
     * left左侧的按钮
     * right右侧的按钮
     * listener 回调监听
     ****/
    public MyEditTextDialog(Context context, String titlestr, String hintstr, String leftButton, String rightButton, CallOnClickListener listener) {
        super(context, R.style.alertdialog);
        this.context = context;
        this.titlestr = titlestr;
        this.onClickListener = listener;
        this.context = context;
        this.leftBtnStr = leftButton;
        this.rightBtnStr = rightButton;
        this.hintstr = hintstr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.code_dialog);
        initView();
    }

    private void initView() {
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        btn_cancel.setText(leftBtnStr);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
        btn_ok.setText(rightBtnStr);
        tv_name = (TextView) findViewById(R.id.tv_title);

        tv_name.setText(titlestr);
        editText = (EditText) findViewById(R.id.et_content);
        editText.setHint(hintstr);
        //输入系统时间
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)
                        editText.getContext().getSystemService(context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }, 500);
        setCancelable(false);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String editable = editText.getText().toString();
                String str = stringFilter(editable.toString());
                if(!editable.equals(str)){
                    editText.setText(str);
                    //设置新的光标所在位置
                    editText.setSelection(str.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                String inputstr = editText.getText().toString().trim();
                //输入是否null
                if (!Tool.isEmpty(inputstr)) {
                    onClickListener.RightBtnOnClick(inputstr);
                    dismiss();
                } else {
                    editText.setError(context.getString(R.string.inputyourname));
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    public static String stringFilter(String str) throws PatternSyntaxException {
        // 只允许字母、数字和汉字
        String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
