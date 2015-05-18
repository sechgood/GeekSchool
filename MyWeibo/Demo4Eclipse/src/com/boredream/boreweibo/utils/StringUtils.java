package com.boredream.boreweibo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boredream.boreweibo.R;
import com.boredream.boreweibo.activity.UserInfoActivity;
import com.boredream.boreweibo.entity.Emotion;

public class StringUtils {
	
	public static SpannableString getWeiboContent(final Context context, final TextView tv, String source) {
		return getWeiboContent(context, tv, source, true);
	}

	public static SpannableString getWeiboContent(final Context context, final TextView tv, String source, boolean clickable) {
		SpannableString spannableString = new SpannableString(source);
		Resources res = context.getResources();
		
		String regexLink = "(@([\u4e00-\u9fa5\\w])+)|(#([\u4e00-\u9fa5\\w])+#)";
		Pattern patternLink = Pattern.compile(regexLink);
		Matcher matcherLink = patternLink.matcher(spannableString);
		
		String regexEmoji = "\\[([\u4e00-\u9fa5a-zA-Z0-9])+\\]";
		Pattern patternEmoji = Pattern.compile(regexEmoji);
		Matcher matcherEmoji = patternEmoji.matcher(spannableString);
		
		if(matcherLink.find() && clickable) {
			tv.setMovementMethod(new LinkTouchMovementMethod());
			matcherLink.reset();
		}
		
		while (true) { // 如果可以匹配到
			if(matcherLink.find()) {
				final String key = matcherLink.group(); // 获取匹配到的具体字符
				int start = matcherLink.start(); // 匹配字符串的开始位置
				
				if(clickable) {
					// @和#可点击
					TouchableSpan clickableSpan = new TouchableSpan(context) {
						@Override
						public void onClick(View widget) {
							if(key.startsWith("@")) {
								Intent intent = new Intent(context, UserInfoActivity.class);
								intent.putExtra("userName", key.substring(1));
								context.startActivity(intent);
							} else if(key.startsWith("#")) {
								ToastUtils.showToast(context, "查看话题 :" + key, 0);
							} else if(tv.getParent() instanceof LinearLayout){
								((LinearLayout) tv.getParent()).performClick();
							}
						}
					};
					spannableString.setSpan(clickableSpan, start, start + key.length(), 
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				} else {
					// @和#不可点击
					int blueColor = context.getResources().getColor(R.color.txt_at_blue);
					ForegroundColorSpan colorSpan = new ForegroundColorSpan(blueColor);
					spannableString.setSpan(colorSpan, start, start + key.length(), 
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				
			} else if(matcherEmoji.find()) {
				String key = matcherEmoji.group(); // 获取匹配到的具体字符
				int start = matcherEmoji.start(); // 匹配字符串的开始位置
				
				Integer imgRes = Emotion.getImgByName(key);
				if(imgRes != null) {
					Options options = new Options();
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeResource(res, imgRes, options);
					
					int scale = (int) (options.outWidth / tv.getTextSize());
					options.inJustDecodeBounds = false;
					options.inSampleSize = scale;
					Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes, options);
					
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

		private Context context;
		
		public TouchableSpan(Context context) {
			this.context = context;
		}

	    /**
	     * Makes the text underlined and in the link color.
	     */
	    @Override
	    public void updateDrawState(TextPaint ds) {
	    	ds.setColor(context.getResources().getColor(R.color.txt_at_blue));
	        ds.setUnderlineText(false);
	    }
//		private boolean mIsPressed;
//		private int mPressedBackgroundColor;
//		private int mNormalTextColor;
//		private int mPressedTextColor;
//
//		public TouchableSpan(int normalTextColor, int pressedTextColor,
//				int pressedBackgroundColor) {
//			mNormalTextColor = normalTextColor;
//			mPressedTextColor = pressedTextColor;
//			mPressedBackgroundColor = pressedBackgroundColor;
//		}
//
//		public void setPressed(boolean isSelected) {
//			mIsPressed = isSelected;
//		}
//
//		@Override
//		public void updateDrawState(TextPaint ds) {
//			super.updateDrawState(ds);
//			ds.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
//			ds.bgColor = mIsPressed ? mPressedBackgroundColor : Color.TRANSPARENT;
//			ds.setUnderlineText(false);
//		}
	}
	
	public static class LinkTouchMovementMethod extends LinkMovementMethod {

		@Override
		public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
			int action = event.getAction();

			if (action == MotionEvent.ACTION_UP ||
					action == MotionEvent.ACTION_DOWN) {
				int x = (int) event.getX();
				int y = (int) event.getY();

				x -= widget.getTotalPaddingLeft();
				y -= widget.getTotalPaddingTop();

				x += widget.getScrollX();
				y += widget.getScrollY();

				Layout layout = widget.getLayout();
				int line = layout.getLineForVertical(y);
				int off = layout.getOffsetForHorizontal(line, x);

				ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

				if (link.length != 0) {
					if (action == MotionEvent.ACTION_UP) {
						link[0].onClick(widget);
						Selection.removeSelection(buffer);
					} else if (action == MotionEvent.ACTION_DOWN) {
						Selection.setSelection(buffer,
								buffer.getSpanStart(link[0]),
								buffer.getSpanEnd(link[0]));
					}

					return true;
				} else {
					ViewGroup parent = (ViewGroup) widget.getParent();
					parent.onTouchEvent(event);
					return true;
				}
			}

			return super.onTouchEvent(widget, buffer, event);
		}

	}
	
//	public static class LinkTouchMovementMethod extends LinkMovementMethod {
//	    private TouchableSpan mPressedSpan;
//
//	    @Override
//	    public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
//	        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//	            mPressedSpan = getPressedSpan(textView, spannable, event);
//	            if (mPressedSpan != null) {
//	                mPressedSpan.setPressed(true);
//	                Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan),
//	                        spannable.getSpanEnd(mPressedSpan));
//	            }
//	        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//	            TouchableSpan touchedSpan = getPressedSpan(textView, spannable, event);
//	            if (mPressedSpan != null && touchedSpan != mPressedSpan) {
//	                mPressedSpan.setPressed(false);
//	                mPressedSpan = null;
//	                Selection.removeSelection(spannable);
//	            }
//	        } else {
//	            if (mPressedSpan != null) {
//	                mPressedSpan.setPressed(false);
//	                super.onTouchEvent(textView, spannable, event);
//	            }
//	            mPressedSpan = null;
//	            Selection.removeSelection(spannable);
//	        }
//	        return true;
//	    }
//
//	    private TouchableSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {
//
//	        int x = (int) event.getX();
//	        int y = (int) event.getY();
//
//	        x -= textView.getTotalPaddingLeft();
//	        y -= textView.getTotalPaddingTop();
//
//	        x += textView.getScrollX();
//	        y += textView.getScrollY();
//
//	        Layout layout = textView.getLayout();
//	        int line = layout.getLineForVertical(y);
//	        int off = layout.getOffsetForHorizontal(line, x);
//
//	        TouchableSpan[] link = spannable.getSpans(off, off, TouchableSpan.class);
//	        TouchableSpan touchedSpan = null;
//	        if (link.length > 0) {
//	            touchedSpan = link[0];
//	        }
//	        return touchedSpan;
//	    }
//
//	}

}
