#include <jni.h>
#include "cocoa/CCGeometry.h"
#include "platform/android/CCEGLView.h"
#include "platform/android/MECameraStream.h"
#include "Java_org_cocos2dx_lib_Cocos2dxCamera.h"
#include "JniHelper.h"
#include "MECameraStream.h"
#include "../../CCImage.h"
#include "../../CCFileUtils.h"

using namespace cocos2d;

#define  CLASS_NAME "org/cocos2dx/lib/Cocos2dxCamera"

extern "C" {

int* rgbData = NULL;

JNIEXPORT void JNICALL
Java_org_cocos2dx_lib_Cocos2dxCamera_onPictureTaken(JNIEnv* env, jobject thiz, jbyteArray jdata, jint dataLen, jint  width, jint height) {
	std::string path = CCFileUtils::sharedFileUtils()->getWriteablePath();
	path.append("camera_image.jpg");

	CCLog("native width %d height %d", width, height);

	jbyte* data = env->GetByteArrayElements(jdata, 0);

    CCImage *pImage = new CCImage();
    pImage->initWithImageData(data, dataLen, CCImage::kFmtJpg, width, height);

    //pImage->saveToFile("/sdcard/at.blockhausmedien.alcarconfi/camera_image.jpg", 0);
    pImage->saveToFile(path.c_str(), 0);
    CC_SAFE_DELETE(pImage);

	MECameraStream::sharedStream()->onPictureTaken(path.c_str());
}

JNIEXPORT void JNICALL
Java_org_cocos2dx_lib_Cocos2dxCamera_freeNativeResources(JNIEnv* env,
		jobject thiz) {
	//free(rgbData);
	//rgbData = NULL;
}

JNIEXPORT void JNICALL
Java_org_cocos2dx_lib_Cocos2dxCamera_onUpdateCameraFrame(JNIEnv* env,
		jobject thiz, jbyteArray yuv420sp, jint width, jint height,
		jint cameraFormat) {

	//CCLog("Inside native Cocos2dxCamera::onUpdateCameraFrame()");

	int sz;
	int i;
	int j;
	int Y;
	int Cr = 0;
	int Cb = 0;
	int pixPtr = 0;
	int jDiv2 = 0;
	int R = 0;
	int G = 0;
	int B = 0;
	int cOff;
	int w = width;
	int h = height;
	sz = w * h;

	jboolean isCopy;

	//CCLog("jint -> w: %i, h: %o", width, height);
	//CCLog("Retrieving native byte array (width: %d, height: %d)", w, h);

	jbyte* yuv = env->GetByteArrayElements(yuv420sp, &isCopy);

	//CCLog("Calculated size (width x height) = %d", w * h);

	// Init buffer if needed
	if (rgbData == NULL) {
		rgbData = (int*) malloc(1024 * 1024 * sizeof(int));
	}

	//CCLog("Converting preview data to native RGB fomat");

	if (cameraFormat == CAMERA_PREVIEW_FORMAT_NV21) {
		for (j = 0; j < h; j++) {
			pixPtr = j * w;
			jDiv2 = j >> 1;
			for (i = 0; i < w; i++) {
				Y = yuv[pixPtr];
				if (Y < 0)
					Y += 255;
				if ((i & 0x1) != 1) {
					cOff = sz + jDiv2 * w + (i >> 1) * 2;
					Cb = yuv[cOff];
					if (Cb < 0)
						Cb += 127;
					else
						Cb -= 128;
					Cr = yuv[cOff + 1];
					if (Cr < 0)
						Cr += 127;
					else
						Cr -= 128;
				}
				R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
				if (R < 0)
					R = 0;
				else if (R > 255)
					R = 255;
				G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1)
						+ (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
				if (G < 0)
					G = 0;
				else if (G > 255)
					G = 255;
				B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
				if (B < 0)
					B = 0;
				else if (B > 255)
					B = 255;

				rgbData[j * 1024 + i] = 0xff000000 + (R << 16) + (G << 8) + (B);
				pixPtr++;
			}
		}

	} else if (cameraFormat == CAMERA_PREVIEW_FORMAT_YV12) {
		for (j = 0; j < h; j++) {
			pixPtr = j * w;
			jDiv2 = j >> 1;
			for (i = 0; i < w; i++) {
				Y = yuv[pixPtr];
				if (Y < 0)
					Y += 255;
				if ((i & 0x1) != 1) {
					cOff = sz + jDiv2 * w + (i >> 1) * 2;
					Cb = yuv[cOff];
					if (Cb < 0)
						Cb += 127;
					else
						Cb -= 128;
					Cr = yuv[cOff + 1];
					if (Cr < 0)
						Cr += 127;
					else
						Cr -= 128;
				}
				R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
				if (R < 0)
					R = 0;
				else if (R > 255)
					R = 255;
				G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1)
						+ (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
				if (G < 0)
					G = 0;
				else if (G > 255)
					G = 255;
				B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
				if (B < 0)
					B = 0;
				else if (B > 255)
					B = 255;

				rgbData[j * 1024 + i] = 0xff000000 + (R << 16) + (G << 8) + (B);
				pixPtr++;
			}
		}

	}

	//CCLog("Getting shared camera stream");

	MECameraStream* cameraStream = MECameraStream::sharedStream();

	//CCLog("Updating camera stream with RGB data");

	cameraStream->update(rgbData, w, h);
	env->ReleaseByteArrayElements(yuv420sp, yuv, JNI_ABORT);

}

}
