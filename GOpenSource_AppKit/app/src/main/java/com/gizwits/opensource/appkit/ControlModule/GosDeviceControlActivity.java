package com.gizwits.opensource.appkit.ControlModule;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.opensource.appkit.R;
import com.gizwits.opensource.appkit.CommonModule.GosBaseActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ConcurrentHashMap;

public class GosDeviceControlActivity extends GosBaseActivity {
	/** The GizWifiDevice device */
	private GizWifiDevice mDevice;

	/** The ActionBar actionBar */
	ActionBar actionBar;

	//图片显示
	private ImageView iv_show;

	//文字控件
	private TextView tv_show;

	//复选框
	private CheckBox cb_send;

	//进度条
	private SeekBar sb_send;

	//临时存储回调结果
	ConcurrentHashMap<String, Object> mtempDataSend;

	private boolean isWater;
	private boolean open_off;
	private int mprogress;

	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message message) {
			//更新UI
			if(message.what == 101){
				if(isWater){
					iv_show.setBackgroundResource(R.drawable.ic_water);
				}else{
					iv_show.setBackgroundResource(R.drawable.ic_nowater);
				}

				if(open_off){
					tv_show.setText("有水啦！");
					tv_show.setTextColor(getResources().getColor(R.color.black));
				}else{
					tv_show.setText("没水喽！");
					tv_show.setTextColor(getResources().getColor(R.color.tomato));
				}
				sb_send.setProgress(mprogress);

				cb_send.setChecked(open_off);
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gos_device_control);
		initDevice();
		setActionBar(true, true, mDevice.getProductName());
		initView();
	}

	private void initView() {
		//绑定图片id
		iv_show = (ImageView) findViewById(R.id.iv_show);

		//绑定文字id
		tv_show = (TextView) findViewById(R.id.tv_show);
		tv_show.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mtempDataSend = new ConcurrentHashMap<>();
				mtempDataSend.put("open_off",true);
				mDevice.write(mtempDataSend,0);
			}
		});

		cb_send = (CheckBox) findViewById(R.id.cb_send);
		cb_send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mtempDataSend = new ConcurrentHashMap<>();
				//事先判断这个复选框是否为true
				if(cb_send.isChecked()){
					mtempDataSend.put("open_off",true);
				}else{
					mtempDataSend.put("open_off",false);
				}
				mDevice.write(mtempDataSend,0);
			}
		});

		sb_send = (SeekBar) findViewById(R.id.sb_send);
		sb_send.setMax(100);
		sb_send.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			//拖动条数值被改变触发的方法
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
			}

			//拖动条数值被人为开始改变触发的方法
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			//拖动条数值被人为停止改变触发的方法
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

	}

	private void initDevice() {
		Intent intent = getIntent();
		mDevice = (GizWifiDevice) intent.getParcelableExtra("GizWifiDevice");
		mDevice.setListener(mListener);
	}

	private GizWifiDeviceListener mListener = new GizWifiDeviceListener(){
		@Override
		public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, Object> dataMap, int sn) {
			super.didReceiveData(result, device, dataMap, sn);
			Log.e("==w","显示从云端发送过来的数据：" + dataMap.toString());
			//先判断是否为正确回调
			if(result == GizWifiErrorCode.GIZ_SDK_SUCCESS){
				//首先从回调的数据中判断这个回调设备是否为当前界面的设备，通过唯一的mac地址
				if(device.getMacAddress().equals(mDevice.getMacAddress())){
					if(dataMap.get("data") != null){
						ConcurrentHashMap<String ,Object> mtempData = (ConcurrentHashMap<String ,Object>)dataMap.get("data");
						if(mtempData.get("iswater") != null){
							isWater = (boolean) mtempData.get("iswater");
						}
						if(mtempData.get("open_off") != null){
							open_off = (boolean) mtempData.get("open_off");
						}
						if(mtempData.get("waterprogress") != null)
							mprogress = (int) mtempData.get("waterprogress");

						mHandler.sendEmptyMessage(101);
					}
				}
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.devices_control, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_getMessage:
            Toast.makeText(GosDeviceControlActivity.this,"设备的PK值：" + mDevice.getProductKey(),Toast.LENGTH_SHORT).show();
			break;

        case R.id.item_Rename:
            View inflate = LayoutInflater.from(GosDeviceControlActivity.this).inflate(R.layout.dialog_rename,null);
            break;
		}
		return super.onOptionsItemSelected(item);
	}

}
