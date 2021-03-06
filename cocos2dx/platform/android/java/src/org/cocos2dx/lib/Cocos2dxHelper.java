/****************************************************************************
 * Copyright (c) 2010-2011 cocos2d-x.org
 * 
 * http://www.cocos2d-x.org
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ****************************************************************************/
package org.cocos2dx.lib;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.SurfaceView;

public class Cocos2dxHelper {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static Cocos2dxMusic sCocos2dMusic;
	private static Cocos2dxSound sCocos2dSound;
	private static AssetManager sAssetManager;
	private static Cocos2dxAccelerometer sCocos2dxAccelerometer;
	private static boolean sAccelerometerEnabled;
	private static String sPackageName;

	private static Cocos2dxHelperListener sCocos2dxHelperListener;

	private static Cocos2dxCamera sCocos2dxCamera;
	private static boolean sCameraWasPaused;
	
	private static float sScreenPhysicalWidth;
	private static float sScreenPhysicalHeight;
	private static float sScreenDensity;

	private static Context sContext; 
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public static void init(final Context pContext, final Cocos2dxHelperListener pCocos2dxHelperListener) {
		final ApplicationInfo applicationInfo = pContext.getApplicationInfo();

		Cocos2dxHelper.sCocos2dxHelperListener = pCocos2dxHelperListener;

		Cocos2dxHelper.sPackageName = applicationInfo.packageName;
		Cocos2dxHelper.nativeSetApkPath(applicationInfo.sourceDir);
		Cocos2dxHelper.nativeSetExternalAssetPath(Cocos2dxHelper.getAbsolutePathOnExternalStorage(applicationInfo, "assets/"));

		Cocos2dxHelper.sCocos2dxAccelerometer = new Cocos2dxAccelerometer(pContext);
		Cocos2dxHelper.sCocos2dMusic = new Cocos2dxMusic(pContext);
		Cocos2dxHelper.sCocos2dSound = new Cocos2dxSound(pContext);
		Cocos2dxHelper.sAssetManager = pContext.getAssets();
		Cocos2dxBitmap.setContext(pContext);

		Cocos2dxHelper.sCocos2dxCamera = new Cocos2dxCamera(pContext);
		sCameraWasPaused = false;
		
		sScreenDensity = pContext.getResources().getDisplayMetrics().density;
		
		sContext = pContext;
		
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

	private static native void nativeSetApkPath(final String pApkPath);

	private static native void nativeSetExternalAssetPath(final String pExternalAssetPath);

	private static native void nativeSetEditTextDialogResult(final byte[] pBytes);
	
	private static native void nativeAlertViewClickedButtonWithTagAtIndex(final int tag, final int buttonIndex);
	
	public static String getCocos2dxPackageName() {
		return Cocos2dxHelper.sPackageName;
	}

	public static String getCurrentLanguage() {
		return Locale.getDefault().getLanguage();
	}

	public static String getDeviceModel() {
		return Build.MODEL;
	}

	public static AssetManager getAssetManager() {
		return Cocos2dxHelper.sAssetManager;
	}

	public static void enableAccelerometer() {
		Cocos2dxHelper.sAccelerometerEnabled = true;
		Cocos2dxHelper.sCocos2dxAccelerometer.enable();
	}

	public static void setAccelerometerInterval(float interval) {
		Cocos2dxHelper.sCocos2dxAccelerometer.setInterval(interval);
	}

	public static void disableAccelerometer() {
		Cocos2dxHelper.sAccelerometerEnabled = false;
		Cocos2dxHelper.sCocos2dxAccelerometer.disable();
	}

	public static void preloadBackgroundMusic(final String pPath) {
		Cocos2dxHelper.sCocos2dMusic.preloadBackgroundMusic(pPath);
	}

	public static void playBackgroundMusic(final String pPath, final boolean isLoop) {
		Cocos2dxHelper.sCocos2dMusic.playBackgroundMusic(pPath, isLoop);
	}

	public static void resumeBackgroundMusic() {
		Cocos2dxHelper.sCocos2dMusic.resumeBackgroundMusic();
	}

	public static void pauseBackgroundMusic() {
		Cocos2dxHelper.sCocos2dMusic.pauseBackgroundMusic();
	}

	public static void stopBackgroundMusic() {
		Cocos2dxHelper.sCocos2dMusic.stopBackgroundMusic();
	}

	public static void rewindBackgroundMusic() {
		Cocos2dxHelper.sCocos2dMusic.rewindBackgroundMusic();
	}

	public static boolean isBackgroundMusicPlaying() {
		return Cocos2dxHelper.sCocos2dMusic.isBackgroundMusicPlaying();
	}

	public static float getBackgroundMusicVolume() {
		return Cocos2dxHelper.sCocos2dMusic.getBackgroundVolume();
	}

	public static void setBackgroundMusicVolume(final float volume) {
		Cocos2dxHelper.sCocos2dMusic.setBackgroundVolume(volume);
	}

	public static void preloadEffect(final String path) {
		Cocos2dxHelper.sCocos2dSound.preloadEffect(path);
	}

	public static int playEffect(final String path, final boolean isLoop) {
		return Cocos2dxHelper.sCocos2dSound.playEffect(path, isLoop);
	}

	public static void resumeEffect(final int soundId) {
		Cocos2dxHelper.sCocos2dSound.resumeEffect(soundId);
	}

	public static void pauseEffect(final int soundId) {
		Cocos2dxHelper.sCocos2dSound.pauseEffect(soundId);
	}

	public static void stopEffect(final int soundId) {
		Cocos2dxHelper.sCocos2dSound.stopEffect(soundId);
	}

	public static float getEffectsVolume() {
		return Cocos2dxHelper.sCocos2dSound.getEffectsVolume();
	}

	public static void setEffectsVolume(final float volume) {
		Cocos2dxHelper.sCocos2dSound.setEffectsVolume(volume);
	}

	public static void unloadEffect(final String path) {
		Cocos2dxHelper.sCocos2dSound.unloadEffect(path);
	}

	public static void pauseAllEffects() {
		Cocos2dxHelper.sCocos2dSound.pauseAllEffects();
	}

	public static void resumeAllEffects() {
		Cocos2dxHelper.sCocos2dSound.resumeAllEffects();
	}

	public static void stopAllEffects() {
		Cocos2dxHelper.sCocos2dSound.stopAllEffects();
	}

	public static void end() {
		Cocos2dxHelper.sCocos2dMusic.end();
		Cocos2dxHelper.sCocos2dSound.end();
	}

	public static void startCameraPreview() {
		Cocos2dxHelper.sCocos2dxCamera.startCameraPreview();
	}

	public static void stopCameraPreview() {
		Cocos2dxHelper.sCocos2dxCamera.stopCameraPreview();
	}

	public static boolean hasCamera() {
		return Cocos2dxHelper.sCocos2dxCamera.hasCamera();
//		return false;
	}
	
	public static void takePicture(String path) {
		Cocos2dxHelper.sCocos2dxCamera.takePicture(path);
	}

	public static float getScreenDensity() {
		return sScreenDensity;
	}
	
	public static void setCameraPreviewSurface(SurfaceView sv) {
		Cocos2dxHelper.sCocos2dxCamera.setPreviewSurface(sv);
	}

	public static void onResume() {
		if (Cocos2dxHelper.sAccelerometerEnabled) {
			Cocos2dxHelper.sCocos2dxAccelerometer.enable();
		}

		if (Cocos2dxHelper.sCameraWasPaused == true) {
			Cocos2dxHelper.sCocos2dxCamera.startCameraPreview();
		}
	}

	public static void onPause() {
		if (Cocos2dxHelper.sAccelerometerEnabled) {
			Cocos2dxHelper.sCocos2dxAccelerometer.disable();
		}

		if (Cocos2dxHelper.sCocos2dxCamera.isRunning()) {
			Cocos2dxHelper.sCocos2dxCamera.stopCameraPreview();
			sCameraWasPaused = true;
		}
	}

	public static void terminateProcess() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	private static void showDialog(final String pTitle, final String pMessage) {
		Cocos2dxHelper.sCocos2dxHelperListener.showDialog(pTitle, pMessage);
	}
	
	private static void showShareDialog(final int pTag, final String pTitle, final String pImagePath, final String optionYES, final String optionNO) {
		Cocos2dxHelper.sCocos2dxHelperListener.showShareDialog(pTag, pTitle, pImagePath, optionYES, optionNO);
	}
	
	private static void showOptionDialog(final String pTitle, final String pMessage, final String optionYES, final String optionNO) {
		Cocos2dxHelper.sCocos2dxHelperListener.showOptionDialog(pTitle, pMessage, optionYES, optionNO);
	}

	private static void showEditTextDialog(final String pTitle, final String pMessage, final int pInputMode, final int pInputFlag, final int pReturnType, final int pMaxLength) {
		Cocos2dxHelper.sCocos2dxHelperListener.showEditTextDialog(pTitle, pMessage, pInputMode, pInputFlag, pReturnType, pMaxLength);
	}

	public static void setEditTextDialogResult(final String pResult) {
		try {
			final byte[] bytesUTF8 = pResult.getBytes("UTF8");

			Cocos2dxHelper.sCocos2dxHelperListener.runOnGLThread(new Runnable() {
				@Override
				public void run() {
					Cocos2dxHelper.nativeSetEditTextDialogResult(bytesUTF8);
				}
			});
		} catch (UnsupportedEncodingException pUnsupportedEncodingException) {
			/* Nothing. */
		}
	}

	private static String getAbsolutePathOnExternalStorage(final ApplicationInfo pApplicationInfo, final String pPath) {
		return Environment.getExternalStorageDirectory() + "/Android/data/"
				+ pApplicationInfo.packageName + "/files/" + pPath;
	}

	
	public static void setAlertViewClickedButtonWithTagAtIndex(final int tag, final int buttonIndex) {
		
		Cocos2dxHelper.nativeAlertViewClickedButtonWithTagAtIndex(tag,buttonIndex);
	}
	
	public static void setAlertViewClickedButtonWithTagAtIndex(final int tag, final int buttonIndex,final String imagePath, final String shareText) {
		
		//facebook share
		if (tag==2) {
			
			Intent intent = new Intent("postImageToFacebook");
			intent.putExtra("imagePath", imagePath);
			intent.putExtra("shareText", shareText);
			LocalBroadcastManager.getInstance(sContext).sendBroadcast(intent);
	
			//final ShareMessage dialogMessage = (ShareMessage)msg.obj;
			Log.i("Cocos2dxHelper", "dialogMessage: "+imagePath + "text: "+shareText);
		}
		
		Cocos2dxHelper.setAlertViewClickedButtonWithTagAtIndex(tag, buttonIndex);
	}
	
	public static String getExternalStoragePictureFolder() {
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
	}
	
	public static void exitWheelsConfigurator() {
		Intent intent = new Intent("exitWheelsConfigurator");
		intent.putExtra("exitWheelsConfigurator", true);
		LocalBroadcastManager.getInstance(sContext).sendBroadcast(intent);
	}
	
	public static void postImageToFacebook(final String pShareText,final String pPath,final String pOptionYES,final String pOptionNO) {
		Log.i("Cocos2dxHelper", "pPath: " + pPath);
		Cocos2dxHelper.sCocos2dxHelperListener.showShareDialog(2, pShareText, pPath, pOptionYES, pOptionNO);
	}
	
	public static void sendPerEmail(final String imagePath) {
		
		Intent intent = new Intent("sendPerEmail");
		intent.putExtra("imagePath", imagePath);
		LocalBroadcastManager.getInstance(sContext).sendBroadcast(intent);
		
		Log.i("Cocos2dxHelper", "pPath: " + imagePath);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static interface Cocos2dxHelperListener {
		public void showDialog(final String pTitle, final String pMessage);
		
		public void showShareDialog(final int pTag, final String pTitle, final String pImagePath, final String optionYES, final String optionNO);
		
		public void showOptionDialog(final String pTitle, final String pMessage, final String optionYES, final String optionNO);

		public void showEditTextDialog(final String pTitle, final String pMessage, final int pInputMode, final int pInputFlag, final int pReturnType, final int pMaxLength);

		public void runOnGLThread(final Runnable pRunnable);

	}
}
