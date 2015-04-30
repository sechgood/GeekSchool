package com.boredream.boreweibo.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boredream.boreweibo.R;
import com.boredream.boreweibo.activity.ImageBrowerActivity;
import com.boredream.boreweibo.entity.PicUrls;
import com.boredream.boreweibo.entity.Status;
import com.boredream.boreweibo.entity.User;
import com.boredream.boreweibo.utils.DateUtils;
import com.boredream.boreweibo.utils.DialogUtils;
import com.boredream.boreweibo.utils.ImageOptHelper;
import com.boredream.boreweibo.utils.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class StatusAdapter extends BaseAdapter {

	private Context context;
	private List<Status> datas;
	private ImageLoader imageLoader;
	private AnimateFirstDisplayListener animateFirstDisplayListener;
	
	private Dialog progressDialog;

	public StatusAdapter(Context context, List<Status> datas) {
		this.context = context;
		this.datas = datas;
		imageLoader = ImageLoader.getInstance();
		progressDialog = DialogUtils.createLoadingDialog(context);
		animateFirstDisplayListener = new AnimateFirstDisplayListener();
	}
	
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Status getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_status, null);
			holder.ll_top_content = (LinearLayout) convertView.findViewById(R.id.ll_top_content);
			holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
			holder.rl_content = (RelativeLayout) convertView.findViewById(R.id.rl_content);
			holder.tv_subhead = (TextView) convertView.findViewById(R.id.tv_subhead);
			holder.tv_body = (TextView) convertView.findViewById(R.id.tv_body);
			holder.fl_imageview = (FrameLayout) convertView.findViewById(R.id.fl_imageview);
			holder.gv_images = (GridView) convertView.findViewById(R.id.gv_images);
			holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.iv_location = (ImageView) convertView.findViewById(R.id.iv_location);
			holder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
			
			holder.include_retweeted_status = (LinearLayout) convertView
					.findViewById(R.id.include_retweeted_status);
			holder.tv_retweeted_content = (TextView) convertView
					.findViewById(R.id.tv_retweeted_content);
			holder.fl_retweeted_imageview = (FrameLayout) convertView
					.findViewById(R.id.fl_retweeted_imageview);
			holder.gv_retweeted_images = (GridView) convertView
					.findViewById(R.id.gv_retweeted_images);
			holder.iv_retweeted_image = (ImageView) convertView
					.findViewById(R.id.iv_retweeted_image);
			
			holder.ll_share_bottom = (LinearLayout) convertView
					.findViewById(R.id.ll_share_bottom);
			holder.iv_share_bottom = (ImageView) convertView
					.findViewById(R.id.iv_share_bottom);
			holder.tv_share_bottom = (TextView) convertView
					.findViewById(R.id.tv_share_bottom);
			holder.ll_comment_bottom = (LinearLayout) convertView
					.findViewById(R.id.ll_comment_bottom);
			holder.iv_comment_bottom = (ImageView) convertView
					.findViewById(R.id.iv_comment_bottom);
			holder.tv_comment_bottom = (TextView) convertView
					.findViewById(R.id.tv_comment_bottom);
			holder.ll_like_bottom = (LinearLayout) convertView
					.findViewById(R.id.ll_like_bottom);
			holder.cb_like_bottom = (CheckBox) convertView
					.findViewById(R.id.cb_like_bottom);
			holder.tv_like_bottom = (TextView) convertView
					.findViewById(R.id.tv_like_bottom);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		// set data
		final Status item = getItem(position);
		User user = item.getUser();
		imageLoader.displayImage(user.getProfile_image_url(), holder.iv_avatar,
				ImageOptHelper.getAvatarOptions());
		holder.tv_subhead.setText(user.getName());
		holder.tv_body.setText(DateUtils.getShortTime(item.getCreated_at()) + 
				"  来自" + Html.fromHtml(item.getSource()));

		setImages(item, holder.fl_imageview, holder.gv_images, holder.iv_image);
		
		if(TextUtils.isEmpty(item.getText())) {
			holder.tv_content.setVisibility(View.GONE);
		} else {
			holder.tv_content.setVisibility(View.VISIBLE);
			holder.tv_content.setText(StringUtils.getWeiboContent(context, item.getText()));
		}
		
		// retweeted
		Status retweetedStatus = item.getRetweeted_status();
		if(retweetedStatus != null) {
			holder.include_retweeted_status.setVisibility(View.VISIBLE);
			holder.tv_retweeted_content.setText("@" + retweetedStatus.getUser().getName()
					+ ":" + retweetedStatus.getText());
			setImages(retweetedStatus, holder.fl_retweeted_imageview, 
					holder.gv_retweeted_images, holder.iv_retweeted_image);
		} else {
			holder.include_retweeted_status.setVisibility(View.GONE);
		}
		
		// bottom bar
		holder.tv_share_bottom.setText(item.getReposts_count() == 0 ?
				"转发" : item.getReposts_count()+"");
		holder.cb_like_bottom.setChecked(item.isLiked());
		holder.ll_share_bottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		holder.tv_comment_bottom.setText(item.getComments_count() == 0 ?
				"评论" : item.getComments_count()+"");
		holder.ll_comment_bottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(context, InfoDetailActivity.class);
//				intent.putExtra("info", item);
//				context.startActivity(intent);
			}
		});
		
		holder.tv_like_bottom.setText(item.getAttitudes_count() == 0 ?
				"赞" : item.getAttitudes_count()+"");
		holder.ll_like_bottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				sendLike(item);
				
				
				final ScaleAnimation scaleAnimation2 = new ScaleAnimation(1.5f, 1f, 1.5f, 1f, 
						Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				scaleAnimation2.setDuration(150);
				
				ScaleAnimation scaleAnimation1 = new ScaleAnimation(1f, 1.5f, 1f, 1.5f, 
						Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				scaleAnimation1.setDuration(200);
				scaleAnimation1.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
						
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						holder.cb_like_bottom.setChecked(!holder.cb_like_bottom.isChecked());
						holder.cb_like_bottom.setAnimation(scaleAnimation2);
					}
				});
				holder.cb_like_bottom.setAnimation(scaleAnimation1);
			}
		});
		
		holder.ll_top_content.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				onAdapterMultiClickListener.onItemClick(
//						OnAdapterMultiClickListener.TYPE_INFO_DETAIL, item);
			}
		});
		
		return convertView;
	}

	private void setImages(final Status status, ViewGroup vgContainer, GridView gvImgs, final ImageView ivImg) {
		ArrayList<PicUrls> picUrls = status.getPic_urls();
		String picUrl = status.getBmiddle_pic();
		
		if(picUrls.size() == 1) {
			vgContainer.setVisibility(View.VISIBLE);
			gvImgs.setVisibility(View.GONE);
			ivImg.setVisibility(View.VISIBLE);
			
			imageLoader.displayImage(picUrl, ivImg, animateFirstDisplayListener);
			
			ivImg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, ImageBrowerActivity.class);
					intent.putExtra("status", status);
					intent.putExtra("position", -1);
					context.startActivity(intent);
				}
			});
		} else if(picUrls.size() > 1) {
			vgContainer.setVisibility(View.VISIBLE);
			gvImgs.setVisibility(View.VISIBLE);
			ivImg.setVisibility(View.GONE);
			
			StatusGridImgsAdapter imagesAdapter = new StatusGridImgsAdapter(context, picUrls);
			gvImgs.setAdapter(imagesAdapter);
			
			gvImgs.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(context, ImageBrowerActivity.class);
					intent.putExtra("status", status);
					intent.putExtra("position", position);
					context.startActivity(intent);
				}
			});
		} else {
			vgContainer.setVisibility(View.GONE);
		}
	}

	public static class ViewHolder{
		public LinearLayout ll_top_content;
		public ImageView iv_avatar;
		public RelativeLayout rl_content;
		public TextView tv_subhead;
		public TextView tv_body;
		public FrameLayout fl_imageview;
		public GridView gv_images;
		public ImageView iv_image;
		public TextView tv_content;
		public ImageView iv_location;
		public TextView tv_location;
		
		public LinearLayout include_retweeted_status;
		public TextView tv_retweeted_content;
		public FrameLayout fl_retweeted_imageview;
		public GridView gv_retweeted_images; 
		public ImageView iv_retweeted_image;

		public LinearLayout ll_share_bottom;
		public ImageView iv_share_bottom;
		public TextView tv_share_bottom;
		public LinearLayout ll_comment_bottom;
		public ImageView iv_comment_bottom;
		public TextView tv_comment_bottom;
		public LinearLayout ll_like_bottom;
		public CheckBox cb_like_bottom;
		public TextView tv_like_bottom;
	}

//	private void sendLike(Status info) {
//		progressDialog.show();
//		BmobApi.likeInfo(context, info, 
//				new UpdateSimpleListener(context, progressDialog){
//			@Override
//			public void onSuccess() {
//				super.onSuccess();
////				setLikeState();
//			}
//		});
//	}
	
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}