package com.boredream.boreweibo1.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	
	private static Toast toast;
	
	/**
	 * 显示Toast,默认SHORT持续时间
	 * @param context
	 * @param text
	 */
	public static void showToast(Context context, CharSequence text) {
		if(toast == null) {
			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		} else {
			toast.setText(text);      
			toast.setDuration(Toast.LENGTH_SHORT);  
		}
		toast.show();
	}

	/**
	 * 显示Toast
	 * @param context
	 * @param text
	 * @param duration
	 */
	public static void showToast(Context context, CharSequence text, int duration) {
		if(toast == null) {
			toast = Toast.makeText(context, text, duration);
		} else {
			toast.setText(text);      
			toast.setDuration(duration);  
		}
		toast.show();
	}

}
