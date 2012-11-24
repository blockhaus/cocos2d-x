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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;


import android.util.Log;

//@SuppressWarnings("unused")
public class Cocos2dxCamera {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String TAG = Cocos2dxCamera.class.getSimpleName();

	// ===========================================================
	// Fields
	// ===========================================================

	private static final int MIN_FRAME_WIDTH = 240;
	  private static final int MIN_FRAME_HEIGHT = 240;
	  private static final int MAX_FRAME_WIDTH = 600;
	  private static final int MAX_FRAME_HEIGHT = 400;

	  //private final CameraConfigurationManager configManager;
	  private Camera	mCamera;
	  private byte[]	mBuffer;
	  private int dataBufferSize;
	  //private AutoFocusManager autoFocusManager;
	  private Rect framingRect;
	  private Rect framingRectInPreview;
	  private boolean initialized;
	  private boolean previewing;
	  private int requestedFramingRectWidth;
	  private int requestedFramingRectHeight;

	// ===========================================================
	// Constructors
	// ===========================================================

	public Cocos2dxCamera() {
		
		startVideo();


	}

    public void startVideo() {
        //SurfaceHolder videoCaptureViewHolder = null;
        try {
            mCamera = Camera.open();
            Log.e("CameraTest", "Camera Opend");
        } catch (RuntimeException e) {
            Log.e("CameraTest", "Camera Open filed");
            return;
        }
        mCamera.setErrorCallback(new ErrorCallback() {
            public void onError(int error, Camera camera) {
            }
        }); 
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewFrameRate(35);
        parameters.setPreviewFpsRange(35000,35000);
      
        List<int[]> supportedPreviewFps=parameters.getSupportedPreviewFpsRange();
        Iterator<int[]> supportedPreviewFpsIterator=supportedPreviewFps.iterator();
        while(supportedPreviewFpsIterator.hasNext()){
            int[] tmpRate=supportedPreviewFpsIterator.next();
            StringBuffer sb=new StringBuffer();
            sb.append("supportedPreviewRate: ");
            for(int i=tmpRate.length,j=0;j<i;j++){
                sb.append(tmpRate[j]+", ");
            }
            Log.v("CameraTest",sb.toString());
        }

        List<Size> supportedPreviewSizes=parameters.getSupportedPreviewSizes();
        Iterator<Size> supportedPreviewSizesIterator=supportedPreviewSizes.iterator();
        while(supportedPreviewSizesIterator.hasNext()){
            Size tmpSize=supportedPreviewSizesIterator.next();
            Log.v("CameraTest","supportedPreviewSize.width = "+tmpSize.width+"supportedPreviewSize.height = "+tmpSize.height);
        }

        mCamera.setParameters(parameters);
        /*
        if (null != mVideoCaptureView)
            videoCaptureViewHolder = mVideoCaptureView.getHolder();
        try {
            mCamera.setPreviewDisplay(videoCaptureViewHolder);
        } catch (Throwable t) {
        }
        */
        Log.v("CameraTest","Camera PreviewFrameRate = "+mCamera.getParameters().getPreviewFrameRate());
        Size previewSize=mCamera.getParameters().getPreviewSize();
        dataBufferSize=(int)(previewSize.height*previewSize.width*(ImageFormat.getBitsPerPixel(mCamera.getParameters().getPreviewFormat())/8.0));
        mBuffer = new byte[dataBufferSize];
        mCamera.addCallbackBuffer(mBuffer);

        mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            private long timestamp=0;
            public synchronized void onPreviewFrame(byte[] data, Camera camera) {
                Log.v("CameraTest","Time Gap = "+(System.currentTimeMillis()-timestamp));
                timestamp=System.currentTimeMillis();
                
                camera.addCallbackBuffer(mBuffer);
                
                try{
                    //camera.addCallbackBuffer(data);
                	Size previewSize=camera.getParameters().getPreviewSize();
                	//Cocos2dxCamera.onUpdateCameraFrame(data, previewSize.width, previewSize.height);
                	
                } catch (Exception e) {
                    Log.e("CameraTest", "addCallbackBuffer error");
                    return;
                }
                
                return;
            }
        });
        try {
            mCamera.startPreview();
        } catch (Throwable e) {
            mCamera.release();
            mCamera = null;
            return;
        }
    }
    public void stopVideo() {
        if(null==mCamera)
            return;
        try {
            mCamera.stopPreview();
            mCamera.setPreviewDisplay(null);
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.release();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        mCamera = null;
    }
    public void finish(){
        stopVideo();
        //super.finish();
    };
    
    public static native void onUpdateCameraFrame(final byte[] pX, final float width, final float height);

    
}
