package com.boredream.contacts;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BoreLetterBar extends LinearLayout {

	private CharSequence[] mItems = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

	private OnLetterChangedListener listener;

	public void setOnLetterChangedListener(OnLetterChangedListener listener) {
		this.listener = listener;
	}

	public BoreLetterBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setOrientation(VERTICAL);
		setBackgroundColor(Color.BLACK);

		for (CharSequence s : mItems) {
			TextView t = new TextView(context);
			t.setText(s);
			t.setTextSize(10);
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			t.setLayoutParams(params);
			t.setTextColor(Color.WHITE);
			addView(t);
		}

	}

	public BoreLetterBar(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public BoreLetterBar(Context context) {
		this(context, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		TextView child = null;

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			child = findChildByLocation(ev.getX(), ev.getY());
			if (listener != null) {
				listener.onLetterSelected(child==null?"":child.getText().toString());
			}
			break;
		case MotionEvent.ACTION_MOVE:
			child = findChildByLocation(ev.getX(), ev.getY());
			if (listener != null) {
				listener.onLetterSelected(child==null?"":child.getText().toString());
			}
			break;
		case MotionEvent.ACTION_UP:
			if (listener != null) {
				listener.onLetterSelected("");
			}
			break;
		}

		return true;
	}

	private TextView findChildByLocation(float x, float y) {
		TextView child = null;
		int mContentTop = getChildAt(0).getTop();
		int mContentBottom = getChildAt(getChildCount() - 1).getBottom();
		int defSize = (mContentBottom - mContentTop) / mItems.length;

		int index = (int) ((y - mContentTop) / defSize);
		if (index >= 0 && index < mItems.length
				&& x >= 0 && x <= getWidth()) {
			child = (TextView) getChildAt(index);
		}
		return child;
	}

	public interface OnLetterChangedListener {
		void onLetterSelected(String letter);
	}

}
