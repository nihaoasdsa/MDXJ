package com.example.mdxj.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.example.mdxj.DwpcApplication;
import com.example.mdxj.R;
import com.example.mdxj.adapter.CatagoryTwoAdapter;
import com.example.mdxj.adapter.ListDialogAdapter;
import com.example.mdxj.location.GpsLocation;
import com.example.mdxj.model.CatagoryOne;
import com.example.mdxj.model.CatagoryThree;
import com.example.mdxj.model.CatagoryTwo;

public class CatagoryTwoActivity extends Activity {
    private RelativeLayout re_operation;
    private TextView tv_title;
    
    private ListView listView;
    private CatagoryTwoAdapter adapter;
    
    private RelativeLayout re_selectall;
    private RelativeLayout re_selectall_cancel;
    private TextView tv_selectall;
    private ImageView iv_operation;
    
    private PopupWindow popInfo = null;
    
    private List<CatagoryTwo> cgList = new ArrayList<CatagoryTwo>();
    private CatagoryTwo curCg = null;
    private CatagoryOne parent = null;

    private static final int REQUEST_CATAGORY_THREE = 1;
    private static final int REQUEST_CATAGORY_THREE_EDIT = 2;
	
	private Timer gpsStatusTimer = null;
    private RelativeLayout re_gps;
    private ImageView iv_gps;
    private final Handler mHandler = new GpsHandler();
    private GpsLocation mCGL = new GpsLocation(this);
	private boolean mPositionFlag = false;
    public static final int FlASH_GPSSTATUS = 100;
    public static final int UNFlASH_GPSSTATUS = 101;
    public class GpsHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case GpsLocation.GPS_SUCCESS: {
                	updateLocation();
                    break;
                }
                case GpsLocation.GPS_STOP:{
    		    	mPositionFlag = false;
                    break;
                }
                case FlASH_GPSSTATUS: {
                	iv_gps.setBackgroundResource(R.drawable.bn_gps_blue);
                    break;
                }
                case UNFlASH_GPSSTATUS:{
                	iv_gps.setBackgroundResource(R.drawable.wn_gps);
                    break;
                }
                default:
                    break;
            }
        	return;
        }
    }

    private void startGps() {
    	showGpsView();
    	
    	if (!mPositionFlag) {
    		if (!mCGL.isGpsOpen()) {
    			Toast.makeText(CatagoryTwoActivity.this,"�뿪��GPS��λ����",Toast.LENGTH_SHORT).show();
    		}
    		
	    	mPositionFlag = true;
	    	if (mCGL.startLoaction()) {
	        	flashGpsStatus();
	    	} else {	    	
		    	mPositionFlag = false;
	    	}
    	}
    }

	public void stopGps() {
    	hideGpsView();
    	unflashGpsStatus();
    	
    	if (mPositionFlag) {
			mCGL.stopLocation();
		    mPositionFlag = false;
    	}
	}
	
	public void updateLocation() {
		if (!mCGL.isAllowedArea()) {
			Toast.makeText(CatagoryTwoActivity.this,"�ѳ�����Ȩ����ҵ��Χ",Toast.LENGTH_SHORT).show();
			return;
		}
		
        DecimalFormat df = new DecimalFormat("##0.000000"); 
        DecimalFormat df1 = new DecimalFormat("##0.00");
		
    	iv_gps.setBackgroundResource(R.drawable.bn_gps_blue);

		for (CatagoryTwo ct : cgList) {
			if (null == ct.getLat() || "".equals(ct.getLat())) {
				ct.setLat(df.format(mCGL.getLatitude()));
				ct.setLng(df.format(mCGL.getLongitude()));
				ct.setAlt(df1.format(mCGL.getAltitude()));
				ct.save();
			}
			
			ArrayList<CatagoryThree> childList = ct.getChildList();
			for (CatagoryThree c : childList) {
				if (null == c.getLat() || "".equals(c.getLat())) {
	    			c.setLat(df.format(mCGL.getLatitude()));
	    			c.setLng(df.format(mCGL.getLongitude()));
	    			c.setAlt(df1.format(mCGL.getAltitude()));
	    			c.save();
				}
			}
		}     	
    	adapter.notifyDataSetChanged();
    	
    	stopGps();
	}
	
	private void showGpsView() {
		re_gps.setVisibility(View.VISIBLE);
	}
	
	private void hideGpsView() {
		re_gps.setVisibility(View.GONE);
	}
	
	private void flashGpsStatus() {
		gpsStatusTimer = new Timer();  
		gpsStatusTimer.schedule(new TimerTask() {  
			private boolean first = true;
            public void run() { 
            	if (first) {
            		mHandler.sendEmptyMessage(FlASH_GPSSTATUS);
                	first = false;
            	} else {
            		mHandler.sendEmptyMessage(UNFlASH_GPSSTATUS);
                	first = true;
            	}
            }  
        }, 500, 500);  
	}
	

	private void unflashGpsStatus() {
		if (gpsStatusTimer != null) {
			gpsStatusTimer.cancel();
		}
		gpsStatusTimer = null;
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ctwo);
		
        mCGL.setMh(mHandler);

        Intent intent = getIntent();
        if (intent.hasExtra("CatagoryOne")) {
        	parent = (CatagoryOne)intent.getSerializableExtra("CatagoryOne");
        }        
        
        getList();

		initView();
		tv_title = (TextView) findViewById(R.id.tv_title);   
		tv_title.setText("���� (" + parent.getType() + ")");

        listView = (ListView) findViewById(R.id.list);     
        listView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				adapter.setSelecting(true);
				adapter.changeSelectItem(position);
				adapter.notifyDataSetChanged();

				if ("��".equals(DwpcApplication.getInstance().getSettingData().getAllowAllDelete())) {
					re_selectall.setVisibility(View.VISIBLE);
				}
				re_selectall_cancel.setVisibility(View.VISIBLE);
				iv_operation.setBackgroundResource(R.drawable.icon_delete);		
				
				return true;
			}});
        listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	if (adapter.isSelecting()) {
    				adapter.changeSelectItem(position);
    				adapter.notifyDataSetChanged();
            	} else {
	                stopGps();
	                
	                CatagoryTwo cg = cgList.get(position);
	                setCurCG(cg);
	                
	                Intent intent = new Intent(CatagoryTwoActivity.this, CatagoryThreeActivity.class);
	                intent.putExtra("CatagoryTwo", cg);
	                startActivityForResult(intent, REQUEST_CATAGORY_THREE_EDIT);	                
            	}
			}});
        
        adapter = new CatagoryTwoAdapter(this, cgList);
        listView.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		for (CatagoryTwo ct : cgList) {
			if (null == ct.getLat() || "".equals(ct.getLat())) {
				startGps();
				return;
			}

			ArrayList<CatagoryThree> childList = ct.getChildList();
			for (CatagoryThree c : childList) {
				if (null == c.getLat() || "".equals(c.getLat())) {
					startGps();
					return;
				}
			}
		}
	}
	
	private void initView() {
		re_operation = (RelativeLayout) this.findViewById(R.id.re_operation);
		re_operation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doOperation();
			}
		});
		
		re_selectall = (RelativeLayout) this.findViewById(R.id.re_selectall);
		re_selectall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				adapter.clearSelectItem(); 
				if ("ȫѡ".equals(tv_selectall.getText().toString().trim())) {
					int pos = 0;
					for (CatagoryTwo c : cgList) {
	    				adapter.addSelectItem(pos++);
					}
					tv_selectall.setText("ȫ��ѡ");
				} else {
					tv_selectall.setText("ȫѡ");
				}
				adapter.notifyDataSetChanged();
			}
		});
		re_selectall_cancel = (RelativeLayout) this.findViewById(R.id.re_selectall_cancel);
		re_selectall_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cancelSelectAll();				
			}
		});
		tv_selectall = (TextView) findViewById(R.id.tv_selectall);   
		tv_selectall.setText("ȫѡ");
		
		iv_operation = (ImageView) this.findViewById(R.id.iv_operation);
		
		iv_gps = (ImageView) this.findViewById(R.id.iv_gps);
		re_gps = (RelativeLayout) this.findViewById(R.id.re_gps);
	}
	
	private void cancelSelectAll() {
		adapter.clearSelectItem(); 
		adapter.setSelecting(false);
		adapter.notifyDataSetChanged();
		
		re_selectall.setVisibility(View.GONE);
		re_selectall_cancel.setVisibility(View.GONE);
		iv_operation.setBackgroundResource(R.drawable.operation_add);
	}
	
	private void getList() {
		cgList = parent.getChildList();
	}
	
	public void setCurCG(CatagoryTwo cg) {
		curCg = cg;
	}
	
	private void doOperation() {	
		if (adapter.isSelecting()) {
			if (adapter.getSelectCount() == 0) {
				Toast.makeText(CatagoryTwoActivity.this,"δѡ��ɾ����",Toast.LENGTH_SHORT).show();
				return;
			}
					
			showPopInfo();
		} else {			
			if ("��ѹ".equals(DwpcApplication.getInstance().getSettingData().getWorkType())) {
				showDiyaDialog();
			} else {
				showGaoyaDialog();
			}
		}
	}
	
	private void doDelete() {
	    List<CatagoryTwo> cgListDeleted = new ArrayList<CatagoryTwo>();
		
		for (int i = 0; i < adapter.getSelectCount(); i++) {
			int sel = adapter.getSelectItem(i);
			cgListDeleted.add(cgList.get(sel));
		}
		
		for (CatagoryTwo c : cgListDeleted) {
			c.delete();
			cgList.remove(c);
		}		

		cancelSelectAll();
    	popInfo.dismiss();
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case REQUEST_CATAGORY_THREE:
                if (data != null && data.hasExtra("CatagoryTwo")) {
                	CatagoryTwo cg = (CatagoryTwo) data.getSerializableExtra("CatagoryTwo");
                    
                    ArrayList<CatagoryThree> cldList = cg.getChildList();
                    curCg.setCurChildCode(cg.getCurChildCode());

                	curCg.getChildList().clear();
                    for (CatagoryThree c3 : cldList) {
                    	curCg.addChild(c3);
                    }
                    
                    if (cldList.size() > 0) {
	                    String date = cldList.get(cldList.size()-1).getUpdateTime();
	                    curCg.setUpdateTime(date);
	                    curCg.setLat(cldList.get(0).getLat());
	                    curCg.setLng(cldList.get(0).getLng());
                    } else {
	                    curCg.setUpdateTime("");
	                    curCg.setLat("");
	                    curCg.setLng("");
                    }
                    
                	cgList.add(curCg);
                    adapter.notifyDataSetChanged();
                    
                    curCg.save();
                    parent.save();
                } 
                break;
            case REQUEST_CATAGORY_THREE_EDIT:
                if (data != null && data.hasExtra("CatagoryTwo")) {
                	CatagoryTwo cg = (CatagoryTwo) data.getSerializableExtra("CatagoryTwo");
                    
                    ArrayList<CatagoryThree> cldList = cg.getChildList();
                    curCg.setCurChildCode(cg.getCurChildCode());
                    
                	curCg.getChildList().clear();
                    for (CatagoryThree c3 : cldList) {
                    	curCg.addChild(c3);
                    }
                    
                    if (cldList.size() > 0) {
	                    String date = cldList.get(cldList.size()-1).getUpdateTime();
	                    curCg.setUpdateTime(date);
	                    curCg.setLat(cldList.get(0).getLat());
	                    curCg.setLng(cldList.get(0).getLng());
                    } else {
	                    curCg.setUpdateTime("");
	                    curCg.setLat("");
	                    curCg.setLng("");
                    }
                    
                    adapter.notifyDataSetChanged();
                    
                    curCg.save();
                    parent.save();
                } 
                break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            switch (requestCode) {
            case REQUEST_CATAGORY_THREE:
	        	parent.rollbackChildCode();
	        	break;
            case REQUEST_CATAGORY_THREE_EDIT:
            	break;
            }
        	curCg = null;
        }
    }
    
    private void showDiyaDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();

        window.setContentView(R.layout.list_dialog);
        LinearLayout ll_title = (LinearLayout) window.findViewById(R.id.ll_title);
        ll_title.setVisibility(View.VISIBLE);
        
        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText("��ѡ��");
        
        final ListView list = (ListView) window.findViewById(R.id.list);
        
        final List<String> contentList = new ArrayList<String>();
        contentList.add("��ѹ��");
        contentList.add("��");
        contentList.add("�����");
        contentList.add("��֧��");
        
        ListDialogAdapter adapter = new ListDialogAdapter(this, contentList, -1);
        list.setAdapter(adapter);
        
        list.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> p, View view,
                    int position, long id) {

    			curCg = new CatagoryTwo();
    			curCg.setParentId(parent.getId());
    			curCg.setCode(parent.nextChildCode());
    			curCg.setPersonName(parent.getPersonName());
            	curCg.setType(contentList.get(position));   
            	curCg.setDestPath(parent.getFolderPath());

                Intent intent = new Intent(CatagoryTwoActivity.this, CatagoryThreeActivity.class);
                intent.putExtra("CatagoryTwo", curCg);
                CatagoryTwoActivity.this.startActivityForResult(intent, REQUEST_CATAGORY_THREE);
            	
                dlg.cancel();
            }
            
        });
    }

    private void showGaoyaDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();

        window.setContentView(R.layout.list_dialog);
        LinearLayout ll_title = (LinearLayout) window.findViewById(R.id.ll_title);
        ll_title.setVisibility(View.VISIBLE);
        
        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText("��ѡ��");
        
        final ListView list = (ListView) window.findViewById(R.id.list);
        
        final List<String> contentList = new ArrayList<String>();
        contentList.add("���վ");
        contentList.add("��");
        contentList.add("���¾�");
        contentList.add("����վ");
        contentList.add("��ѹ��");
        
        ListDialogAdapter adapter = new ListDialogAdapter(this, contentList, -1);
        list.setAdapter(adapter);
        
        list.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> p, View view,
                    int position, long id) {

    			curCg = new CatagoryTwo();
    			curCg.setParentId(parent.getId());
    			curCg.setCode(parent.nextChildCode());
    			curCg.setPersonName(parent.getPersonName());
            	curCg.setType(contentList.get(position));   
            	curCg.setDestPath(parent.getFolderPath());         	        	

                Intent intent = new Intent(CatagoryTwoActivity.this, CatagoryThreeActivity.class);
                intent.putExtra("CatagoryTwo", curCg);
                CatagoryTwoActivity.this.startActivityForResult(intent, REQUEST_CATAGORY_THREE);
            	
                dlg.cancel();
            }
            
        });
    }

	private void showPopInfo() { 
        if (popInfo != null && popInfo.isShowing()) {
            return;
        }
        
        View popInfoView = getLayoutInflater().inflate(R.layout.popup_info, null);
        
        popInfo = new PopupWindow(popInfoView, LayoutParams.MATCH_PARENT, 
        		LayoutParams.WRAP_CONTENT, true);  
        popInfo.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popInfo.setOutsideTouchable(false);
        popInfo.setAnimationStyle(R.style.AnimBottom);
        
        popInfo.showAtLocation(listView, Gravity.BOTTOM, 0, 0); 
        
        TextView tv_content1 = (TextView) popInfoView.findViewById(R.id.tv_content1);
        tv_content1.setText("ɾ��" + adapter.getSelectCount() + "��");
        
        LinearLayout ll_content1 = (LinearLayout) popInfoView.findViewById(R.id.ll_content1);
        ll_content1.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
            	doDelete();
            }
        });
        
        LinearLayout ll_content2 = (LinearLayout) popInfoView.findViewById(R.id.ll_content2);
        ll_content2.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
            	popInfo.dismiss();
            }
            
        });
    }
	
    public void back(View view) {
    	String tost = "���ڵȴ���λ�����Ժ�";
    	
		for (CatagoryTwo ct : cgList) {
			if (null == ct.getLat() || "".equals(ct.getLat())) {
	    		Toast.makeText(CatagoryTwoActivity.this, tost,Toast.LENGTH_SHORT).show();					
				return;
			}			
			
			ArrayList<CatagoryThree> childList = ct.getChildList();			
			for (CatagoryThree c : childList) {
				if (null == c.getLat() || "".equals(c.getLat())) {
		    		Toast.makeText(CatagoryTwoActivity.this, tost,Toast.LENGTH_SHORT).show();					
					return;
				}
			}
		}    	
    	
        stopGps();
		
        Intent intent = new Intent();
        intent.putExtra("CatagoryOne", parent);
        setResult(RESULT_OK, intent);
        
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
        	
        	back(null);
        	
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
