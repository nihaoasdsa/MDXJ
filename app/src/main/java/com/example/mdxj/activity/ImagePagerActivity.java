package com.example.mdxj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import java.util.ArrayList;

import com.example.mdxj.R;
import com.example.mdxj.util.StorageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImagePagerActivity extends Activity {

	private ImageLoader mImageLoader;
	private ImageView mImageView;
    private int current = 0;
    private ArrayList<String> uriList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);

		mImageLoader = ImageLoader.getInstance();

		current = getIntent().getIntExtra("current", 0);
		uriList = getIntent().getStringArrayListExtra("uriList");

        String curPath = "";
		if (uriList != null && uriList.size() > 0) {
			curPath = uriList.get(current);
		}
		
		mImageView = (ImageView) findViewById(R.id.bigPic);
		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mImageView.setOnTouchListener(new View.OnTouchListener() {
		      private float mPosX = -1, mPosY = -1, mCurPosX, mCurPosY, mOffsetX, mOffsetY;
		      private boolean result = false;
		      
		      @Override
		      public boolean onTouch(View v, MotionEvent event) {
	              result = false;
		          switch (event.getAction()) {
		          case MotionEvent.ACTION_DOWN:
		              mPosX = event.getX();
		              mPosY = event.getY();
		              break;
		          case MotionEvent.ACTION_MOVE:
		              mCurPosX = event.getX();
		              mCurPosY = event.getY();
		
		              if (mPosX == -1) {
		                  mPosX = mCurPosX;
		              }
		              if (mPosY == -1) {
		                  mPosY = mCurPosY;
		              }
		              break;
		          case MotionEvent.ACTION_UP:
		              if (mCurPosX - mPosX > 0
		                      && (Math.abs(mCurPosX - mPosX) > 120)) { 
		            	  toLeft();
		            	  result = true;
		              } else if (mCurPosX - mPosX < 0
		                      && (Math.abs(mCurPosX - mPosX) > 120)) {
		            	  toRight();
		            	  result = true;
		              }		              
		              break;
		          }
		          
		          return result;
		      }
		  });
		mImageLoader.displayImage(StorageUtil.PATH_PREFIX + curPath, mImageView);
	}

	private void toLeft() {
		if (current <= 0) {
			current = 0;
			return;
		}
		
		current--;

		mImageLoader.displayImage(
				StorageUtil.PATH_PREFIX + uriList.get(current), mImageView);		
	}

	private void toRight() {
		if (current >= (uriList.size()-1)) {
			current = uriList.size()-1;
			return;
		}
		
		current++;

		mImageLoader.displayImage(
				StorageUtil.PATH_PREFIX + uriList.get(current), mImageView);	
	}
}
