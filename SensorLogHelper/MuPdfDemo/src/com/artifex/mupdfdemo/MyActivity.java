package com.artifex.mupdfdemo;

import java.io.InputStream;

import com.artifex.mupdfdemo.MuPDFActivity.TopBarMode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class MyActivity extends Activity{
	private Integer[] mps;
	private MuPDFCore   core;
	private String  mFileName;
	private MuPDFReaderView mDocView;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mps=new Integer[]{
				R.drawable.v1,
				R.drawable.v2,
				R.drawable.v3,
				R.drawable.v4,
				R.drawable.v5,
				R.drawable.v6,
				R.drawable.v7,
				R.drawable.v8,
				R.drawable.v9,
		};
		
		if (core == null) {
			core = (MuPDFCore)getLastNonConfigurationInstance();

			if (savedInstanceState != null && savedInstanceState.containsKey("FileName")) {
				mFileName = savedInstanceState.getString("FileName");
			}
		}
		System.out.println(1);
		if (core == null) {//coreÈÔÎª¿ÕÔò£º
			Intent intent = getIntent();
			byte buffer[] = null;
			if (Intent.ACTION_VIEW.equals(intent.getAction())) {
				Uri uri = intent.getData();
				if (uri.toString().startsWith("content://")) {
					// Handle view requests from the Transformer Prime's file manager
					// Hopefully other file managers will use this same scheme, if not
					// using explicit paths.
					Cursor cursor = getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
					if (cursor.moveToFirst()) {
						String str = cursor.getString(0);
						String reason = null;
						if (str == null) {
							try {
								InputStream is = getContentResolver().openInputStream(uri);
								int len = is.available();
								buffer = new byte[len];
								is.read(buffer, 0, len);
								is.close();
							}
							catch (java.lang.OutOfMemoryError e)
							{
								System.out.println("Out of memory during buffer reading");
								reason = e.toString();
							}
							catch (Exception e) {
								reason = e.toString();
							}
							if (reason != null)
							{
								buffer = null;
								Resources res = getResources();
								setTitle(String.format(res.getString(R.string.cannot_open_document_Reason), reason));
								return;
							}
						} else {
							uri = Uri.parse(str);
						}
					}
				}
				if (buffer != null) {
					core = openBuffer(buffer);
				} else {
					core = openFile(Uri.decode(uri.getEncodedPath()));
					System.out.println("uri.getEncodedPath()===>>"+uri.getEncodedPath());
				}
				SearchTaskResult.set(null);
				if (core.countPages() == 0)
					core = null;
			}
		}
		System.out.println(2);
		createUI();
	}
	
	private void createUI(){
		if(core==null){
			return;
		}
		System.out.println(3);
		mDocView=new MuPDFReaderView(this){
			protected void onMoveToChild(int i) {
				if (core == null)
					return;
				super.onMoveToChild(i);
			}
			protected void onTapMainDocArea() {
				/*
				 * 
				 */
			}
			protected void onDocMotion() {
				/*
				 * 
				 */
			}
		};
		
		mDocView.setAdapter(new MuPDFPageAdapter(this, core));
		SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
		mDocView.setDisplayedViewIndex(prefs.getInt("page"+mFileName, 0));
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(mDocView);
		layout.setBackgroundResource(R.drawable.tiled_background);
		//layout.setBackgroundResource(R.color.canvas);
		setContentView(layout);
	}
	
	private MuPDFCore openBuffer(byte buffer[])
	{
		System.out.println("Trying to open byte buffer");
		try
		{
			core = new MuPDFCore(this, buffer);
			// New file: drop the old outline data
			OutlineActivityData.set(null);
		}
		catch (Exception e)
		{
			System.out.println(e);
			return null;
		}
		return core;
	}
	
	private MuPDFCore openFile(String path)
	{
		int lastSlashPos = path.lastIndexOf('/');
		mFileName = new String(lastSlashPos == -1
					? path
					: path.substring(lastSlashPos+1));
		System.out.println("Trying to open "+path);
		try
		{
			core = new MuPDFCore(this, path);
			// New file: drop the old outline data
			OutlineActivityData.set(null);
		}
		catch (Exception e)
		{
			System.out.println(e);
			return null;
		}
		return core;
	}
	
}
