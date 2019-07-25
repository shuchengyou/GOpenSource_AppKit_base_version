package com.gizwits.opensource.appkit.DeviceModule;

import java.util.List;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.opensource.appkit.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class GosDeviceListAdapter extends BaseAdapter {

	Handler handler = new Handler();
	protected static final int UNBOUND = 99;

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	Context context;
	List<GizWifiDevice> deviceList;

	//临时的字符串显示有水或无水文字
	private String temMessage;

	public GosDeviceListAdapter(Context context, List<GizWifiDevice> deviceList,String temMessage) {
		super();
		this.context = context;
		this.deviceList = deviceList;
		this.temMessage = temMessage;
	}

	@Override
	public int getCount() {
		return deviceList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		Holder holder;

		if (view == null) {
			//设备log
			view = LayoutInflater.from(context).inflate(
					R.layout.item_gos_device_list, null);
			holder = new Holder(view);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		final GizWifiDevice device = deviceList.get(position);
		String LAN, noLAN, unbind;
		LAN = (String) context.getText(R.string.lan);
		noLAN = (String) context.getText(R.string.no_lan);
		unbind = (String) context.getText(R.string.unbind);
		String deviceAlias = device.getAlias();
		String devicePN = device.getProductName();
		if (device.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceOnline
				|| device.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceControlled) {
			if (device.isBind()) {// 已绑定设备
				//如果临时的文字不为空则显示传过来的文字，否则显示设备的mac地址
				if(temMessage != null){
					holder.getTvDeviceMac().setText(temMessage);
					if(temMessage.contains("有水")){
						holder.getllRelativeLayout().setBackgroundResource(R.color.white);
					}else{
						holder.getllRelativeLayout().setBackgroundResource(R.color.tomato);
					}
				}
				else {
					holder.getTvDeviceMac().setText(device.getMacAddress());
				}

				if (device.isLAN()) {
					holder.getTvDeviceStatus().setText(LAN);
				} else {
					holder.getTvDeviceStatus().setText(noLAN);
				}
				if (TextUtils.isEmpty(deviceAlias)) {
					holder.getTvDeviceName().setText(devicePN);
				} else {
					holder.getTvDeviceName().setText(deviceAlias);
				}
			} else {// 未绑定设备
				holder.getllRelativeLayout().setBackgroundResource(R.color.tomato);
				if(temMessage != null){
					holder.getTvDeviceMac().setText(temMessage);
				}
				else {
					holder.getTvDeviceMac().setText(device.getMacAddress());
				}
				holder.getTvDeviceStatus().setText(unbind);
				if (TextUtils.isEmpty(deviceAlias)) {
					holder.getTvDeviceName().setText(devicePN);
				} else {
					holder.getTvDeviceName().setText(deviceAlias);
				}
			}
		} else {// 设备不在线

			holder.getTvDeviceMac().setText(device.getMacAddress());
			holder.getTvDeviceMac().setTextColor(
					context.getResources().getColor(R.color.gray));
			holder.getTvDeviceStatus().setText("");
			holder.getTvDeviceStatus().setTextColor(
					context.getResources().getColor(R.color.gray));
			holder.getImgRight().setVisibility(View.GONE);
			holder.getLlLeft().setBackgroundResource(
					R.drawable.btn_getcode_shape_gray);
			if (TextUtils.isEmpty(deviceAlias)) {
				holder.getTvDeviceName().setText(devicePN);
			} else {
				holder.getTvDeviceName().setText(deviceAlias);
			}
			holder.getTvDeviceName().setTextColor(
					context.getResources().getColor(R.color.gray));
		}
		holder.getDelete2().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Message message = new Message();
				message.what = UNBOUND;
				message.obj = device.getDid().toString();
				handler.sendMessage(message);
			}
		});
		return view;
	}
}

class Holder {
	View view;

	public Holder(View view) {
		this.view = view;
	}

	private TextView tvDeviceMac, tvDeviceStatus, tvDeviceName;

	private RelativeLayout delete2,llRelativeLayout;

	private ImageView imgRight;

	private LinearLayout llLeft;

	//这是整个子项目的背景
	public RelativeLayout getllRelativeLayout(){
		if(llRelativeLayout == null){
			llRelativeLayout = (RelativeLayout) view.findViewById(R.id.llRelativeLayout);
		}
		return llRelativeLayout;
	}

	public LinearLayout getLlLeft() {
		if (null == llLeft) {
			llLeft = (LinearLayout) view.findViewById(R.id.llLeft);
		}
		return llLeft;
	}

	public ImageView getImgRight() {
		if (null == imgRight) {
			imgRight = (ImageView) view.findViewById(R.id.imgRight);
		}
		return imgRight;
	}

	public RelativeLayout getDelete2() {
		if (null == delete2) {
			delete2 = (RelativeLayout) view.findViewById(R.id.delete2);
		}
		return delete2;
	}

	public TextView getTvDeviceMac() {
		if (null == tvDeviceMac) {
			tvDeviceMac = (TextView) view.findViewById(R.id.tvDeviceMac);
		}
		return tvDeviceMac;
	}

	public TextView getTvDeviceStatus() {
		if (null == tvDeviceStatus) {
			tvDeviceStatus = (TextView) view.findViewById(R.id.tvDeviceStatus);
		}
		return tvDeviceStatus;
	}

	public TextView getTvDeviceName() {
		if (null == tvDeviceName) {
			tvDeviceName = (TextView) view.findViewById(R.id.tvDeviceName);
		}
		return tvDeviceName;
	}

}
