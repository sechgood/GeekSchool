package com.boredream.boreweibo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.boredream.boreweibo.R;

public class StringUtils {

	public static SpannableString getWeiboContent(final Context context, final TextView tv, String source) {
		SpannableString spannableString = new SpannableString(source);
		Resources res = context.getResources();

		String regexAt = "@[\u4e00-\u9fa5\\w]+";
		String regexTopic = "#[\u4e00-\u9fa5\\w]+#";
		String regexEmoji = "\\[[\u4e00-\u9fa5\\w]+\\]";
		String regex = "("+regexAt+")|("+regexTopic+")|("+regexEmoji+")";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(spannableString);

		if (matcher.find()) {
			tv.setMovementMethod(LinkMovementMethod.getInstance());
			matcher.reset();
		}

		while (matcher.find()) { // 如果可以匹配到
			final String atStr = matcher.group(1);
			final String topicStr = matcher.group(2);
			final String emojiStr = matcher.group(3);

			if(atStr != null) {
				int start = matcher.start(1); // 匹配字符串的开始位置

				// @和#可点击
				BoreClickableSpan clickableSpan = new BoreClickableSpan(context) {
					@Override
					public void onClick(View widget) {
						ToastUtils.showToast(context, "查看用户 :" + atStr, 0);
					}
				};
				spannableString.setSpan(clickableSpan, start, start + atStr.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			
			if(topicStr != null) {
				int start = matcher.start(2); // 匹配字符串的开始位置

				// @和#可点击
				BoreClickableSpan clickableSpan = new BoreClickableSpan(context) {
					@Override
					public void onClick(View widget) {
						ToastUtils.showToast(context, "查看话题 :" + topicStr, 0);
					}
				};
				spannableString.setSpan(clickableSpan, start, start + topicStr.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			if (emojiStr != null) {
				int start = matcher.start(3); // 匹配字符串的开始位置
				
				int imgRes = EmotionUtils.getImgByName(emojiStr);
				Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes);
				
				if(bitmap != null) {
					int size = (int) tv.getTextSize();
					bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);

					ImageSpan span = new ImageSpan(context, bitmap);
					spannableString.setSpan(span, start, start + emojiStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return spannableString;
	}

	private abstract static class BoreClickableSpan extends ClickableSpan {

		private Context context;

		public BoreClickableSpan(Context context) {
			this.context = context;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(context.getResources().getColor(R.color.txt_at_blue));
			ds.setUnderlineText(false);
		}
		
	}

}
