package com.boredream.boreweibo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;


public class ImageUtils {
	
	public static final int REQUEST_CODE_FROM_CAMERA = 5001;
	public static final int REQUEST_CODE_FROM_ALBUM = 5002;
	
	/**
	 * 存放拍照图片的uri地址
	 */
	public static Uri imageUriFromCamera;

	/**
	 * 选择取照片的方法,结果在activity的onActivityResult()方法中,
	 * 利用ImageUtils.getImageOnActivityResult获取
	 * 
	 * @param activity
	 *            调用该方法的Activity
	 */
	public static void showImagePickDialog(final Activity activity) {
		String title = "选择获取图片的方式";
		String[] items = new String[] { "拍照", "从手机中选择" };
		new AlertDialog.Builder(activity)
			.setTitle(title)
			.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					switch (which) {
					case 0:
						// 点击第一个 Item
						ImageUtils.pickImageFromCamera(activity);
						break;
					case 1:
						// 点击第二个 Item
						ImageUtils.pickImageFromAlbum(activity);
						break;
					}
				}
			})
			.show();
	}
	
	/**
	 * 打开相机拍照获取图片
	 */
	public static void pickImageFromCamera(final Activity activity) {
		// 创建 Uri用于保存拍照图片
		ImageUtils.imageUriFromCamera = ImageUtils.createImageUri(activity);
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// MediaStore.EXTRA_OUTPUT参数不设置时,系统会自动生成一个uri,但是只会返回一个缩略图
		// 缩略图在onActivityResult中可以通过以下代码获取
		// Bitmap bitmap = (Bitmap) data.getExtras().get("data"); 
		intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.imageUriFromCamera);
		activity.startActivityForResult(intent, ImageUtils.REQUEST_CODE_FROM_CAMERA);
	}
	
	/**
	 * 打开本地相册选取图片
	 */
	public static void pickImageFromAlbum(final Activity activity) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		activity.startActivityForResult(intent, ImageUtils.REQUEST_CODE_FROM_ALBUM);
	}
	
	/**
	 * 打开本地相册选取图片2
	 */
	public static void pickImageFromAlbum2(final Activity activity) {
		Intent intent = new Intent();
		intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setAction(Intent.ACTION_PICK);
		activity.startActivityForResult(intent, ImageUtils.REQUEST_CODE_FROM_ALBUM);
	}
	
	/**
	 * 创建一条图片uri,用于保存拍照后的照片
	 */
	private static Uri createImageUri(Context context) {
		String name = "boreWbImg" + System.currentTimeMillis();
		ContentValues values = new ContentValues();
		values.put(Images.Media.TITLE, name);
		values.put(Images.Media.DISPLAY_NAME, name + ".jpeg");
		values.put(Images.Media.MIME_TYPE, "image/jpeg");
		System.out.println(Images.Media.EXTERNAL_CONTENT_URI);
		Uri uri = context.getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
		return uri;
	}
	
	/**
	 * 删除一条图片
	 */
	public static void deleteImageUri(Context context, Uri uri) {
		context.getContentResolver().delete(uri, null, null);
	}

	/**
	 * 获取图片文件路径
	 */
	public static String getImageAbsolutePath(Context context, Uri uri) {
		String filePath = null;
		Cursor cursor = MediaStore.Images.Media.query(context.getContentResolver(), uri, 
				new String[] { Images.Media.DATA });
		if(cursor.moveToFirst()) {
			filePath = cursor.getString(0);
		}
		return filePath;
	} 
}
