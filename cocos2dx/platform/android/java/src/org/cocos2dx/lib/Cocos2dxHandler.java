/****************************************************************************
Copyright (c) 2010-2011 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ****************************************************************************/

package org.cocos2dx.lib;

import java.io.File;
import java.lang.ref.WeakReference;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Cocos2dxHandler extends Handler {
	// ===========================================================
	// Constants
	// ===========================================================
	public final static int HANDLER_SHOW_DIALOG = 1;
	public final static int HANDLER_SHOW_EDITBOX_DIALOG = 2;
	public final static int HANDLER_SHOW_OPTIONDIALOG = 3;
	public final static int HANDLER_SHOW_SHAREDIALOG = 4;
	
	// ===========================================================
	// Fields
	// ===========================================================
	private WeakReference<Cocos2dxActivity> mActivity;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	public Cocos2dxHandler(Cocos2dxActivity activity) {
		this.mActivity = new WeakReference<Cocos2dxActivity>(activity);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	// ===========================================================
	// Methods
	// ===========================================================

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case Cocos2dxHandler.HANDLER_SHOW_DIALOG:
			showDialog(msg);
			break;
		case Cocos2dxHandler.HANDLER_SHOW_EDITBOX_DIALOG:
			showEditBoxDialog(msg);
			break;
		case Cocos2dxHandler.HANDLER_SHOW_OPTIONDIALOG:
			showOptionDialog(msg);
			break;
		case Cocos2dxHandler.HANDLER_SHOW_SHAREDIALOG:
			showShareDialog(msg);
			break;
		}
		
	}
	
	private void showOptionDialog(Message msg) {
		
		Cocos2dxActivity theActivity = this.mActivity.get();
		DialogOptionMessage dialogMessage = (DialogOptionMessage)msg.obj;

		new AlertDialog.Builder(theActivity)
		.setTitle(dialogMessage.titile)
		.setMessage(dialogMessage.message)
		.setPositiveButton(dialogMessage.optionYES, 
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Cocos2dxHelper.setAlertViewClickedButtonWithTagAtIndex(1, 0);
					}
				})
		.setNegativeButton(dialogMessage.optionNO,
				new DialogInterface.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Cocos2dxHelper.setAlertViewClickedButtonWithTagAtIndex(1, 1);
					}
				}
		).create().show();
		
	}
	
	private void showDialog(Message msg) {
		Cocos2dxActivity theActivity = this.mActivity.get();
		DialogMessage dialogMessage = (DialogMessage)msg.obj;
		new AlertDialog.Builder(theActivity)
		.setTitle(dialogMessage.titile)
		.setMessage(dialogMessage.message)
		.setPositiveButton("Ok", 
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				}).create().show();
	}
	
	private void showEditBoxDialog(Message msg) {
		EditBoxMessage editBoxMessage = (EditBoxMessage)msg.obj;
		new Cocos2dxEditBoxDialog(this.mActivity.get(),
				editBoxMessage.title,
				editBoxMessage.content,
				editBoxMessage.inputMode,
				editBoxMessage.inputFlag,
				editBoxMessage.returnType,
				editBoxMessage.maxLength).show();
	}
	
	private void showShareDialog(final Message msg) {
		
		//final Message newMsg = msg;
		Cocos2dxActivity theActivity = this.mActivity.get();
		final ShareMessage dialogMessage = (ShareMessage)msg.obj;
		final EditText input = new EditText(theActivity);
		//input.setTy
		//input.setInputType(EditorInfo.TYPE_NULL);
		final InputMethodManager imm = (InputMethodManager)theActivity.getSystemService(Service.INPUT_METHOD_SERVICE);
		imm.showSoftInput(input, 0);
		
		input.setOnKeyListener(new View.OnKeyListener() {
	
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				
				// TODO Auto-generated method stub
				if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
	                       (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) 
				{
					imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
	            }
	            
				// Returning false allows other listeners to react to the press.
	            return false;
			}
		});
		
		File imgFile = new  File(dialogMessage.imagePath);
		Bitmap shareBitmap = null;
		if(imgFile.exists()){
			shareBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		}
		BitmapDrawable icon = new BitmapDrawable(Bitmap.createScaledBitmap(shareBitmap, 240, 160, true));

		ImageView image = new ImageView(theActivity);
		//image.setImageBitmap(shareBitmap);
		image.setImageDrawable(icon);
		//image.getLayoutParams().width = 210;
		//image.getLayoutParams().height = 140;
		TextView title = new TextView(theActivity);
		// You Can Customise your Title here 
		title.setText("facebook");
		title.setBackgroundColor(Color.argb(255, 59, 89, 152));
		title.setPadding(10, 10, 10, 10);
		title.setGravity(Gravity.CENTER);
		title.setTextColor(Color.WHITE);
		title.setTextSize(20);
		
		LinearLayout shareLayout= new LinearLayout(theActivity);
		shareLayout.setOrientation(1); //1 is for vertical orientation
		shareLayout.addView(title);
		shareLayout.addView(image);
		shareLayout.addView(input);
		
		AlertDialog shareAlert = new AlertDialog.Builder(theActivity)
		.setView(shareLayout)
		.setPositiveButton(dialogMessage.optionYES, 
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Cocos2dxHelper.setAlertViewClickedButtonWithTagAtIndex(dialogMessage.tag, 0, dialogMessage.imagePath, input.getText().toString());
					}
				})
		.setNegativeButton(dialogMessage.optionNO,
				new DialogInterface.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Cocos2dxHelper.setAlertViewClickedButtonWithTagAtIndex(dialogMessage.tag, 1);
					}
				}
		).create();
		
		//shareAlert.getLayoutInflater().
		//ColorDrawable cd = new ColorDrawable(Color.argb(255, 59, 89, 152));
		
		//shareAlert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
		shareAlert.show();
		
		
		/*
		ShareMessage shareMessage = (ShareMessage)msg.obj;
		new Cocos2dxShareDialog(this.mActivity.get(),
				shareMessage.title,
				shareMessage.imagePath
				).show();
				
				*/
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	public static class DialogMessage {
		public String titile;
		public String message;
		
		public DialogMessage(String title, String message) {
			this.titile = title;
			this.message = message;
		}
	}
	
	public static class DialogOptionMessage {
		
		public String titile;
		public String message;
		public String optionYES;
		public String optionNO;
		
		public DialogOptionMessage(String title, String message, String optionYES, String optionNO) {
			this.titile = title;
			this.message = message;
			this.optionYES = optionYES;
			this.optionNO = optionNO;
		}
	}
	
	public static class EditBoxMessage {
		public String title;
		public String content;
		public int inputMode;
		public int inputFlag;
		public int returnType;
		public int maxLength;
		
		public EditBoxMessage(String title, String content, int inputMode, int inputFlag, int returnType, int maxLength){
			this.content = content;
			this.title = title;
			this.inputMode = inputMode;
			this.inputFlag = inputFlag;
			this.returnType = returnType;
			this.maxLength = maxLength;
		}
	}
	
	public static class ShareMessage {
		public int tag;
		public String title;
		public String imagePath;
		public String optionYES;
		public String optionNO;
		
		public ShareMessage(int tag, String title, String imagePath, String optionYES, String optionNO) {
			this.tag = tag;
			this.title = title;
			this.imagePath = imagePath;
			this.optionYES = optionYES;
			this.optionNO = optionNO;
		}
	}
	
}
