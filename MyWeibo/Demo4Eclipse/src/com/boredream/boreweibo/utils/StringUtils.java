package com.boredream.boreweibo.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;

import com.boredream.boreweibo.R;

public class StringUtils {

	public static SpannableString getWeiboContent(Context context, String source) {
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
				String key = matcherAt.group(); // 获取匹配到的具体字符
				int start = matcherAt.start(); // 匹配字符串的开始位置
				
				ForegroundColorSpan span = new ForegroundColorSpan(atBlue);
				spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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

}
