#include "cocoa/CCGeometry.h"
#include "platform/android/MECameraStream.h"
#include "../CCEGLView.h"
#include "JniHelper.h"
#include <jni.h>
#include "MECameraStream.h"

using namespace cocos2d;

extern "C" {
    
    void startPreviewCameraJNI() {
        JniMethodInfo t;
        
        if (JniHelper::getStaticMethodInfo(t, "org/cocos2dx/lib/Cocos2dxCamera", "startVideo", "()V")) {
            t.env->CallStaticVoidMethod(t.classID, t.methodID);
            t.env->DeleteLocalRef(t.classID);
        }
    }
    
    void stopPreviewCameraJNI() {
        JniMethodInfo t;
        
        if (JniHelper::getStaticMethodInfo(t, "org/cocos2dx/lib/Cocos2dxCamera", "stopVideo", "()V")) {
            t.env->CallStaticVoidMethod(t.classID, t.methodID);
            t.env->DeleteLocalRef(t.classID);
        }
        
    }

    int* rgbData;
    int rgbDataSize = 0;
    
    JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxCamera_onUpdateCameraFrame(JNIEnv*  env, jobject thiz, jbyteArray yuv420sp, jint width, jint height) {
        
        int             sz;
        int             i;
        int             j;
        int             Y;
        int             Cr = 0;
        int             Cb = 0;
        int             pixPtr = 0;
        int             jDiv2 = 0;
        int             R = 0;
        int             G = 0;
        int             B = 0;
        int             cOff;
        int w = width;
        int h = height;
        sz = w * h;
        
        jboolean isCopy;
        jbyte* yuv = env->GetByteArrayElements(yuv420sp, &isCopy);

        if(rgbDataSize < sz) {
            int tmp[sz];
            rgbData = &tmp[0];
            rgbDataSize = sz;
            //__android_log_write(ANDROID_LOG_INFO, "JNI", "alloc");
        }
        
        for(j = 0; j < h; j++) {
            pixPtr = j * w;
            jDiv2 = j >> 1;
            for(i = 0; i < w; i++) {
                Y = yuv[pixPtr];
                if(Y < 0) Y += 255;
                if((i & 0x1) != 1) {
                    cOff = sz + jDiv2 * w + (i >> 1) * 2;
                    Cb = yuv[cOff];
                    if(Cb < 0) Cb += 127; else Cb -= 128;
                    Cr = yuv[cOff + 1];
                    if(Cr < 0) Cr += 127; else Cr -= 128;
                }
                R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
                if(R < 0) R = 0; else if(R > 255) R = 255;
                G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1) + (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
                if(G < 0) G = 0; else if(G > 255) G = 255;
                B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
                if(B < 0) B = 0; else if(B > 255) B = 255;
                rgbData[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
            }
        }

        
        MECameraStream* cameraStream = MECameraStream::sharedStream();
        cameraStream->update(&rgbData[0], w, h);
        
        env->ReleaseByteArrayElements(yuv420sp, yuv, JNI_ABORT);
        

    }    
   
}
