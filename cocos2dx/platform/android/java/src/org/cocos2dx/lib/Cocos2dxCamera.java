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

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class Cocos2dxCamera implements SurfaceHolder.Callback {
	// =============================================================================
	// Constants
	// =============================================================================

	private static final String TAG = Cocos2dxCamera.class.getSimpleName();

	/**
	 * Maximum number of pixels that are supported for camera preview width.
	 */
	private static final int MAX_CAMERA_PREVIEW_WIDTH = 1024;

	/**
	 * Maximum number of pixels that are supported for camera preview height.
	 */
	private static final int MAX_CAMERA_PREVIEW_HEIGHT = 1024;

	/**
	 * Camera ID used when device has no suitable camera;
	 */
	private static final int INVALID_CAMERA_ID = -1;

	/**
	 * Internal const for using YV12 preview format.
	 */
	private static final int PREVIEW_FORMAT_YV12 = 0;

	/**
	 * Internal const for using NV21 preview format.
	 */
	private static final int PREVIEW_FORMAT_NV21 = 1;

	/**
	 * Internal const for unsupported preview format.
	 */
	private static final int PREVIEW_FORMAT_UNSUPPORTED = -1;

	private static final int PREFERRED_IMAGE_WIDTH = 960;
	private static final int PREFERRED_IMAGE_HEIGHT = 640;
	// =============================================================================
	// Fields
	// =============================================================================

	/**
	 * Camera device once opened.
	 */
	private Camera mCamera;

	/**
	 * ID of the camera to use. Or -1 if there is no suitable camera.
	 */
	private int mCameraId;

	/**
	 * Camera preview width
	 */
	private int mCameraWidth, mCameraHeight;

	/**
	 * Camera preview height
	 */
	private int mPreviewFormat;

	/**
	 * The preview format constant passed to the underlying converter.
	 */
	private int mUsedPreviewFormat;

	private int mPictureWidth;
	
	private int mPictureHeight;
	
	/**
	 * Surface view for preview (should be hidden). This is needed by some
	 * devices.
	 */
	private SurfaceHolder mHolder;

	private SurfaceView mSurfaceView;

	private boolean mSurfaceAvailable;

	private boolean mStartCameraPreviewWhenSurfaceReady;

	// =============================================================================
	// Constructor
	// =============================================================================

	public Cocos2dxCamera(Context pContext) {
		this.mCamera = null;
		this.mUsedPreviewFormat = PREVIEW_FORMAT_UNSUPPORTED;
		this.mCameraId = this.setupCamera(pContext);
		this.mSurfaceAvailable = false;
	}

	// =============================================================================
	// Public Methods
	// =============================================================================

	/**
	 * Tells wheter a camera preview is currently running or not. This is used
	 * by the helper to restart the camera after the app was paused.
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return this.mCamera != null;
	}

	/**
	 * Tells the app wheter the device has a "suitable" camera or not. Only
	 * cameras that are facing backwards and have a supported preview mode are
	 * counted.
	 * 
	 * @return
	 */
	public boolean hasCamera() {
		return this.mCameraId != INVALID_CAMERA_ID;
	}

	/**
	 * Allows the activity to set a dummy preview surface needed to start
	 * preview mode on certain device.
	 * 
	 * @param sv
	 */
	public void setPreviewSurface(SurfaceView pSurfaceView) {
		Log.d(TAG, "Got Previewsurface");
		this.mSurfaceView = pSurfaceView;
		this.mHolder = pSurfaceView.getHolder();
		this.mHolder.addCallback(this);
	}

	/**
	 * Stops camera preview and releases the camera device.
	 */
	public void stopCameraPreview() {
		if (this.mCamera != null) {
			try {
				this.mCamera.stopPreview();
				this.mCamera.setPreviewDisplay(null);
				this.mCamera.setPreviewCallbackWithBuffer(null);
				this.mCamera.release();
				this.mCamera = null;
				this.freeNativeResources();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Now we can hide the surface
			((Activity) this.mSurfaceView.getContext()).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Log.d(TAG, "Hiding surface view now!");
					Cocos2dxCamera.this.mSurfaceView.setVisibility(View.GONE);
				}
			});
		}
	}

	public void finish() {
		stopCameraPreview();
	};

	/**
	 * Starts camera preview.
	 */
	public void startCameraPreview() {

		// if (this.mSurfaceAvailable == true) {
		// Log.i(TAG, "Start camera preview with surface available!");

		try {
			Log.d(TAG, "Opening camera device");
			// Try to open camera
			this.mCamera = Camera.open(this.mCameraId);
		} catch (Exception ex) {
			Log.d(TAG, Cocos2dxCamera.class.toString()
					+ " could not open the camera device. Device busy?");
		}

		if (mCamera != null) {
			try {
				// Setup camera with pre-configured parameters
				Camera.Parameters params = this.mCamera.getParameters();
				params.setPreviewFormat(this.mPreviewFormat);
				params.setPreviewSize(this.mCameraWidth, this.mCameraHeight);
				params.set("jpeg-quality", 70);
				params.setPictureFormat(PixelFormat.JPEG);
				params.setPictureSize(this.mPictureWidth, this.mPictureHeight);
				this.mCamera.setParameters(params);

				final int dataBufferSize = (int) (this.mCameraWidth
						* this.mCameraHeight * (ImageFormat.getBitsPerPixel(this.mPreviewFormat) / 8.0));

				// Create buffer
				final byte[] previewBuffer = new byte[dataBufferSize];
				// Pass buffer to camera for preview
				this.mCamera.addCallbackBuffer(previewBuffer);

				// Register callback to receive filled preview buffer
				this.mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {

					@Override
					public void onPreviewFrame(byte[] data, Camera camera) {
						// Pass preview data to native code
						Cocos2dxCamera.this.onUpdateCameraFrame(data, Cocos2dxCamera.this.mCameraWidth, Cocos2dxCamera.this.mCameraHeight, Cocos2dxCamera.this.mUsedPreviewFormat);
						// Return the frame to the camera
						camera.addCallbackBuffer(data);
					}
				});

				Log.d(TAG, "Registering callback for making view visible");
				((Activity) mSurfaceView.getContext()).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Log.d(TAG, "Making surface view visible now");
						mSurfaceView.setVisibility(View.VISIBLE);
					}
				});

			} catch (Exception ex) {
				Log.e(TAG, Cocos2dxCamera.class.toString()
						+ " could not start camera preview.", ex);
			}
		}
		// } else {
		// Log.i(TAG, "Enabled start after surface ready!");
		// this.mStartCameraPreviewWhenSurfaceReady = true;
		// }

		synchronized (this) {

			try {
				this.wait();
			} catch (InterruptedException e) {

			}
		}
	}

	public void takePicture(final String path) {
		if (this.mCamera != null) {
			this.mCamera.takePicture(null, null, new PictureCallback() {

				@Override
				public void onPictureTaken(byte[] data, Camera camera) {
					Log.d(TAG, String.format("width %d,  height %d", Cocos2dxCamera.this.mPictureWidth, mPictureHeight));
					Cocos2dxCamera.this.stopCameraPreview();
					Cocos2dxCamera.this.onPictureTaken(data, data.length, Cocos2dxCamera.this.mPictureWidth, Cocos2dxCamera.this.mPictureHeight);
					
//					Log.d(TAG, "onPictureTaken()");
//					File sdcard = Environment.getExternalStorageDirectory();
//					File outputDir = new File(sdcard, Cocos2dxHelper.getCocos2dxPackageName());
//					outputDir.mkdirs();
//					File outputFile = new File(outputDir, "camera_image.jpg");
//					
//					FileOutputStream outStream = null;
//					try {
//						
//						outStream = new FileOutputStream(outputFile);
//						outStream.write(data);
//						outStream.close();
//						Log.d(TAG, "onPictureTaken - wrote bytes: "
//								+ data.length);
//						Cocos2dxCamera.this.onPictureTaken(outputFile.getAbsolutePath());
//					} catch (FileNotFoundException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					} finally {
//					}
//					Log.d(TAG, "onPictureTaken - jpeg");
				}
			});
		}
	}

	// =============================================================================
	// Methods
	// =============================================================================

	private int setupCamera(Context pContext) {
		int cameraId = INVALID_CAMERA_ID;

		if (pContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			// Get number of available cams
			final int numberOfCameras = Camera.getNumberOfCameras();

			CameraInfo cameraInfo = new CameraInfo();
			Camera cam = null;

			// Iterate through all cams (label this "cameraLoop")
			cameraLoop: for (int i = 0; i <= numberOfCameras; i++) {
				// Get camera info
				Camera.getCameraInfo(i, cameraInfo);
				// Skip cam if it is frontfacing
				if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT)
					continue;

				try {
					// Try to open the camera for further inspection
					cam = Camera.open(i);
				} catch (RuntimeException ex) {
					// Could not open camera. Move on to next.
					continue cameraLoop;
				}

				Camera.Parameters params = cam.getParameters();

				// Init preview format as not supported
				this.mUsedPreviewFormat = PREVIEW_FORMAT_UNSUPPORTED;

				// Get the preview image formats supported
				previewFormatLoop: for (int previewFormat : params.getSupportedPreviewFormats()) {
					// Check if preview format is supported
					if (previewFormat == ImageFormat.NV21) {
						// Choose supported preview format NV21
						this.mUsedPreviewFormat = PREVIEW_FORMAT_NV21;
						this.mPreviewFormat = previewFormat;
						break previewFormatLoop;
					}
					// else if (previewFormat == ImageFormat.YV12) {
					// // Choose supported preview format YV12
					// this.mUsedPreviewFormat = PREVIEW_FORMAT_YV12;
					// this.mPreviewFormat = previewFormat;
					// break previewFormatLoop;
					// }
				}

				// Skip camera if no supported preview format was found
				if (this.mUsedPreviewFormat == PREVIEW_FORMAT_UNSUPPORTED) {
					// Release unused camera
					cam.release();
					// Skip cams with non supported preview format
					continue cameraLoop;
				}

				Size usedSize = null;
				// Iterate through all supported preview sizes
				previewSizeLoop: for (Size previewSize : params.getSupportedPreviewSizes()) {
					// Does the preview size fit into our maximum boundaries?
					if (previewSize.width < MAX_CAMERA_PREVIEW_WIDTH
							&& previewSize.height < MAX_CAMERA_PREVIEW_HEIGHT) {
						if (usedSize == null
								|| (usedSize.width < previewSize.width && usedSize.height < previewSize.height)) {
							usedSize = previewSize;
							if (usedSize.width == 640 && usedSize.height == 480) {
								break previewSizeLoop;
							}
						}
					}
				}

				if (usedSize != null) {
					// If a size fits, use it.
					this.mCameraWidth = usedSize.width;
					this.mCameraHeight = usedSize.height;
				} else {
					// Release unused camera
					cam.release();
					// If not skip camera.
					continue cameraLoop;
				}


				this.mPictureWidth = 0;
				this.mPictureHeight = 0;
//				float currentWidthCorrelation = 0;
				float currentWidthCorrelationDifference = 100;
//				float currentAspectCorrelation = 0;
//				float currentAspectCorrelationDifference = 100;
				
				// Get the preview image formats supported
				for (Size imageSize : params.getSupportedPictureSizes()) {
					float widthCorrelation = imageSize.width / (float) PREFERRED_IMAGE_WIDTH; 
//					float aspectCorrelation = (imageSize.width / imageSize.height) / (PREFERRED_IMAGE_WIDTH / PREFERRED_IMAGE_HEIGHT);
					
					float newWidthCorrelationDifference = Math.abs(1-widthCorrelation);
					if(newWidthCorrelationDifference < currentWidthCorrelationDifference) {
						this.mPictureWidth = imageSize.width;
						this.mPictureHeight = imageSize.height;
						currentWidthCorrelationDifference = newWidthCorrelationDifference;
					}
				}
				
				Log.d(TAG, String.format("Calculated picture size is %d x %d", this.mPictureWidth, this.mPictureHeight));

				// Release camera
				cam.release();
				// Store this camera
				cameraId = i;
				// Camera found! Stop searching
				break cameraLoop;
			}
		}

		// Return the found camera ID
		return cameraId;
	}

	public Camera.Size getBestPreviewSize(Camera.Parameters parameters, int w, int h) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= w && size.height <= h) {
				if (null == result)
					result = size;
				else {
					int resultDelta = w - result.width + h - result.height;
					int newDelta = w - size.width + h - size.height;

					if (newDelta < resultDelta)
						result = size;
				}
			}
		}
		return result;
	}

	// =============================================================================
	// Interface implementations
	// =============================================================================

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.d(TAG, "Surface changed!");
		//
		// if (this.mStartCameraPreviewWhenSurfaceReady == true) {
		// this.mStartCameraPreviewWhenSurfaceReady = false;
		// Log.i(TAG, "Starting former delayed preview!");
		// this.startCameraPreview();
		// }
		// Camera.Parameters params = mCamera.getParameters();
		// Camera.Size size = getBestPreviewSize(params, w, h);
		//
		// if (size != null)
		// params.setPreviewSize(size.width, size.height);

		// mCamera.startPreview();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "Surface was created!");

		// Provide surface holder
		try {
			this.mCamera.setPreviewDisplay(this.mHolder);
			// Make the preview surface visible (needed by some
			// cams)
			Log.d(TAG, "Passed surface view to camera!");
		} catch (Exception e) {
			Log.d(TAG, "Could not add preview surface.");
			;
		}
		// Now (after setup) start preview mode
		Cocos2dxCamera.this.mCamera.startPreview();

		synchronized (this) {
			this.notify();
		}

		// this.mSurfaceAvailable = true;
		// this.mHolder = holder;
		//
		// if (this.mStartCameraPreviewWhenSurfaceReady == true) {
		// Log.i(TAG, "Starting former delayed preview!");
		// this.mStartCameraPreviewWhenSurfaceReady = false;
		// this.startCameraPreview();
		// }
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface was destroyed!");
		// this.mSurfaceAvailable = false;
		this.stopCameraPreview();
	}

	// =============================================================================
	// JNI Methods
	// =============================================================================

	public native void onUpdateCameraFrame(final byte[] pX, final int width, final int height, final int previewFormat);

	public native void onPictureTaken(byte[] data, int dataLen, int width, int height);
	
	public native void freeNativeResources();
}
