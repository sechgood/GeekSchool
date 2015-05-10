package com.boredream.boreweibo.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.boredream.boreweibo.R;

public class StringUtils {

	public static SpannableString getWeiboContent(final Context context, String source) {
		SpannableString spannableString = new SpannableString(source);
		Resources res = context.getResources();
		
		String regexAt = "(@([\u4e00-\u9fa5\\w])+)|(#([\u4e00-\u9fa5\\w])+#)";
		Pattern patternAt = Pattern.compile(regexAt);
		Matcher matcherAt = patternAt.matcher(spannableString);
		int atBlue = res.getColor(R.color.txt_at_blue);
		
		String regexEmoji = "\\[([\u4e00-\u9fa5])+\\]";
		Pattern patternEmoji = Pattern.compile(regexEmoji);
		Matcher matcherEmoji = patternEmoji.matcher(spannableString);
		Map<String, Integer> emojiMap = new HashMap<String, Integer>();
		emojiMap.put("[爱你]", R.drawable.d_aini);
		emojiMap.put("[哈哈]", R.drawable.d_haha);
		emojiMap.put("[钱]", R.drawable.d_qian);
		emojiMap.put("[嘻嘻]", R.drawable.d_xixi);
		emojiMap.put("[最右]", R.drawable.d_zuiyou);
		emojiMap.put("[亲亲]", R.drawable.d_qinqin);
		
		while (true) { // 如果可以匹配到
			if(matcherAt.find()) {
				final String key = matcherAt.group(); // 获取匹配到的具体字符
				int start = matcherAt.start(); // 匹配字符串的开始位置
				
				TouchableSpan clickableSpan = new TouchableSpan(atBlue, atBlue, Color.CYAN) {
					@Override
					public void onClick(View widget) {
						if(key.startsWith("@")) {
							ToastUtils.showToast(context, "查看个人详情 :" + key, 0);
						} else if(key.startsWith("#")) {
							ToastUtils.showToast(context, "查看话题 :" + key, 0);
						}
					}
				};
				spannableString.setSpan(clickableSpan, start, start + key.length(), 
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				
			} else if(matcherEmoji.find()) {
				String key = matcherEmoji.group(); // 获取匹配到的具体字符
				int start = matcherEmoji.start(); // 匹配字符串的开始位置
				
				Integer imgRes = emojiMap.get(key);
				if(imgRes != null) {
					Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes);
					ImageSpan span = new ImageSpan(context, bitmap);
					spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			} else {
				break;
			}
		}
		return spannableString;
	}
	
	private abstract static class TouchableSpan extends ClickableSpan {

//		private Context context;
//		
//		public TouchableSpan(Context context) {
//			this.context = context;
//		}
//
//		@Override
//		public void onClick(View widget) {
//			
//		}
//
//	    /**
//	     * Makes the text underlined and in the link color.
//	     */
//	    @Override
//	    public void updateDrawState(TextPaint ds) {
//	    	ds.setColor(context.getResources().getColor(R.color.txt_at_blue));
//	        ds.setUnderlineText(false);
//	    }
		private boolean mIsPressed;
		private int mPressedBackgroundColor;
		private int mNormalTextColor;
		private int mPressedTextColor;

		public TouchableSpan(int normalTextColor, int pressedTextColor,
				int pressedBackgroundColor) {
			mNormalTextColor = normalTextColor;
			mPressedTextColor = pressedTextColor;
			mPressedBackgroundColor = pressedBackgroundColor;
		}

		public void setPressed(boolean isSelected) {
			mIsPressed = isSelected;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
			ds.bgColor = mIsPressed ? mPressedBackgroundColor : Color.TRANSPARENT;
			ds.setUnderlineText(false);
		}
	}
	
	public static class LinkTouchMovementMethod extends LinkMovementMethod {
	    private TouchableSpan mPressedSpan;

	    @Override
	    public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
	        if (event.getAction() == MotionEvent.ACTION_DOWN) {
	            mPressedSpan = getPressedSpan(textView, spannable, event);
	            if (mPressedSpan != null) {
	                mPressedSpan.setPressed(true);
	                Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan),
	                        spannable.getSpanEnd(mPressedSpan));
	            }
	        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
	            TouchableSpan touchedSpan = getPressedSpan(textView, spannable, event);
	            if (mPressedSpan != null && touchedSpan != mPressedSpan) {
	                mPressedSpan.setPressed(false);
	                mPressedSpan = null;
	                Selection.removeSelection(spannable);
	            }
	        } else {
	            if (mPressedSpan != null) {
	                mPressedSpan.setPressed(false);
	                super.onTouchEvent(textView, spannable, event);
	            }
	            mPressedSpan = null;
	            Selection.removeSelection(spannable);
	        }
	        return true;
	    }

	    private TouchableSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {

	        int x = (int) event.getX();
	        int y = (int) event.getY();

	        x -= textView.getTotalPaddingLeft();
	        y -= textView.getTotalPaddingTop();

	        x += textView.getScrollX();
	        y += textView.getScrollY();

	        Layout layout = textView.getLayout();
	        int line = layout.getLineForVertical(y);
	        int off = layout.getOffsetForHorizontal(line, x);

	        TouchableSpan[] link = spannable.getSpans(off, off, TouchableSpan.class);
	        TouchableSpan touchedSpan = null;
	        if (link.length > 0) {
	            touchedSpan = link[0];
	        }
	        return touchedSpan;
	    }

	}

}
