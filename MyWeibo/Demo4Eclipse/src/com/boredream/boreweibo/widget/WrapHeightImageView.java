package com.boredream.boreweibo.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class WrapHeightImageView extends ImageView {

	public WrapHeightImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public WrapHeightImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WrapHeightImageView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		Drawable drawable = getDrawable();
		if(drawable != null) {
			Rect rect = drawable.getBounds();
			System.out.println((rect.right - rect.left) + ":" + (rect.bottom - rect.top));
		}
		
		int heightSpec = heightMeasureSpec;
//
//		if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
//			heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
//		} else {
//			heightSpec = heightMeasureSpec;
//		}

		super.onMeasure(widthMeasureSpec, heightSpec);
	}
}
