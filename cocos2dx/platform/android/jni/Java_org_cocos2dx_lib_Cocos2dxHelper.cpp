#include <stdlib.h>
#include <jni.h>
#include <android/log.h>
#include <string>
#include "JniHelper.h"
#include "cocoa/CCString.h"
#include "Java_org_cocos2dx_lib_Cocos2dxHelper.h"
#include "cocos2d.h"
#include "../../../../../Alcar-Confi-x/Classes/platform/android/MEAlertView.h"

#define  LOG_TAG    "Java_org_cocos2dx_lib_Cocos2dxHelper.cpp"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

#define  CLASS_NAME "org/cocos2dx/lib/Cocos2dxHelper"

static EditTextCallback s_pfEditTextCallback = NULL;
static void* s_ctx = NULL;

using namespace cocos2d;
using namespace std;

extern "C" {
    string g_apkPath;
    
    void Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetApkPath(JNIEnv*  env, jobject thiz, jstring apkPath) {
        g_apkPath = JniHelper::jstring2string(apkPath);
    }

    const char * getApkPath() {
        return g_apkPath.c_str();
    }

    void Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetExternalAssetPath(JNIEnv*  env, jobject thiz, jstring externalAssetPath) {
        const char* externalAssetPathChars = env->GetStringUTFChars(externalAssetPath, NULL);
        cocos2d::JniHelper::setExternalAssetPath(externalAssetPathChars);
        env->ReleaseStringUTFChars(externalAssetPath, externalAssetPathChars);
    }

    void showDialogJNI(const char * pszMsg, const char * pszTitle) {
        if (!pszMsg) {
            return;
        }

        JniMethodInfo t;
        if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "showDialog", "(Ljava/lang/String;Ljava/lang/String;)V")) {
            jstring stringArg1;

            if (!pszTitle) {
                stringArg1 = t.env->NewStringUTF("");
            } else {
                stringArg1 = t.env->NewStringUTF(pszTitle);
            }

            jstring stringArg2 = t.env->NewStringUTF(pszMsg);
            t.env->CallStaticVoidMethod(t.classID, t.methodID, stringArg1, stringArg2);

            t.env->DeleteLocalRef(stringArg1);
            t.env->DeleteLocalRef(stringArg2);
            t.env->DeleteLocalRef(t.classID);
        }
    }

    void showEditTextDialogJNI(const char* pszTitle, const char* pszMessage, int nInputMode, int nInputFlag, int nReturnType, int nMaxLength, EditTextCallback pfEditTextCallback, void* ctx) {
        if (pszMessage == NULL) {
            return;
        }

        s_pfEditTextCallback = pfEditTextCallback;
        s_ctx = ctx;

        JniMethodInfo t;
        if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "showEditTextDialog", "(Ljava/lang/String;Ljava/lang/String;IIII)V")) {
            jstring stringArg1;

            if (!pszTitle) {
                stringArg1 = t.env->NewStringUTF("");
            } else {
                stringArg1 = t.env->NewStringUTF(pszTitle);
            }

            jstring stringArg2 = t.env->NewStringUTF(pszMessage);

            t.env->CallStaticVoidMethod(t.classID, t.methodID, stringArg1, stringArg2, nInputMode, nInputFlag, nReturnType, nMaxLength);

            t.env->DeleteLocalRef(stringArg1);
            t.env->DeleteLocalRef(stringArg2);
            t.env->DeleteLocalRef(t.classID);
        }
    }

    void Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetEditTextDialogResult(JNIEnv * env, jobject obj, jbyteArray text) {
        jsize  size = env->GetArrayLength(text);

        if (size > 0) {
            jbyte * data = (jbyte*)env->GetByteArrayElements(text, 0);
            char* pBuf = (char*)malloc(size+1);
            if (pBuf != NULL) {
                memcpy(pBuf, data, size);
                pBuf[size] = '\0';
                // pass data to edittext's delegate
                if (s_pfEditTextCallback) s_pfEditTextCallback(pBuf, s_ctx);
                free(pBuf);
            }
            env->ReleaseByteArrayElements(text, data, 0);
        } else {
            if (s_pfEditTextCallback) s_pfEditTextCallback("", s_ctx);
        }
    }

    void terminateProcessJNI() {
        JniMethodInfo t;

        if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "terminateProcess", "()V")) {
            t.env->CallStaticVoidMethod(t.classID, t.methodID);
            t.env->DeleteLocalRef(t.classID);
        }
    }

   const char* getPackageNameJNI() {
        JniMethodInfo t;

        if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getCocos2dxPackageName", "()Ljava/lang/String;")) {
            jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID);
            t.env->DeleteLocalRef(t.classID);
            CCString *ret = new CCString(JniHelper::jstring2string(str).c_str());
            ret->autorelease();
            t.env->DeleteLocalRef(str);

            return ret->m_sString.c_str();
        }

        return 0;
    }

    const char* getCurrentLanguageJNI() {
        JniMethodInfo t;

        if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getCurrentLanguage", "()Ljava/lang/String;")) {
            jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID);
            t.env->DeleteLocalRef(t.classID);
            CCString *ret = new CCString(JniHelper::jstring2string(str).c_str());
            ret->autorelease();
            t.env->DeleteLocalRef(str);

            return ret->m_sString.c_str();
        }

        return 0;
    }

    void enableAccelerometerJNI() {
        JniMethodInfo t;

        if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "enableAccelerometer", "()V")) {
            t.env->CallStaticVoidMethod(t.classID, t.methodID);
            t.env->DeleteLocalRef(t.classID);
        }
    }

    void disableAccelerometerJNI() {
        JniMethodInfo t;

        if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "disableAccelerometer", "()V")) {
            t.env->CallStaticVoidMethod(t.classID, t.methodID);
            t.env->DeleteLocalRef(t.classID);
        }
    }
    
    void startCameraPreviewJNI() {
        JniMethodInfo t;
        
        if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "startCameraPreview", "()V")) {
            t.env->CallStaticVoidMethod(t.classID, t.methodID);
            t.env->DeleteLocalRef(t.classID);
        }
    }
    
    void stopCameraPreviewJNI() {
        JniMethodInfo t;
        
        CCLog("stopCameraPreviewJNI()");

        if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "stopCameraPreview", "()V")) {
            t.env->CallStaticVoidMethod(t.classID, t.methodID);
            t.env->DeleteLocalRef(t.classID);
        }
        
    }
    
    bool hasCameraJNI() {
    	JniMethodInfo t;
    	bool hasCamera;

    	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "hasCamera", "()Z")) {
			hasCamera = t.env->CallStaticBooleanMethod(t.classID, t.methodID);
			t.env->DeleteLocalRef(t.classID);
		}

    	return hasCamera;
    }
    
    float getScreenDensityJNI() {
    	JniMethodInfo t;
    	float screenDensity = 1.0;
        //CCLog("screenDensity:%f",screenDensity);
    	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getScreenDensity", "()F")) {
			jfloat sd = t.env->CallStaticFloatMethod(t.classID, t.methodID);
            //CCLog("screenDensity:%f",screenDensity);
            screenDensity = (float)sd;
            //CCLog("screenDensity:%f",screenDensity);
			t.env->DeleteLocalRef(t.classID);
		}
        
    	return screenDensity;
    }

    void Java_org_cocos2dx_lib_Cocos2dxHelper_nativeAlertViewClickedButtonWithTagAtIndex(JNIEnv*  env, jobject thiz, jint externalTag, jint buttonIndex) {
        int tag = (int)externalTag;
        int index = (int)buttonIndex;
        MEAlertView::sharedView()->alertViewClickedButtonWithTagAtIndex(tag, index);
    }
    
    void showOptionDialogJNI(const char * pszTitle, const char * pszMsg, const char * optionYES, const char * optionNO) {
        
        JniMethodInfo t;
        if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "showOptionDialog", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")) {
            
            jstring stringArg1;
            jstring stringArg2;
            
            if (!pszTitle) {
                stringArg1 = t.env->NewStringUTF("xxxx");
            } else {
                stringArg1 = t.env->NewStringUTF(pszTitle);
            }

            if (!pszMsg) {
                stringArg2 = t.env->NewStringUTF("xxxxx");
            } else {
                stringArg2 = t.env->NewStringUTF(pszMsg);
            }



            jstring stringOptionYES = t.env->NewStringUTF(optionYES);
            jstring stringOptionNO = t.env->NewStringUTF(optionNO);
            
            t.env->CallStaticVoidMethod(t.classID, t.methodID, stringArg1, stringArg2, stringOptionYES, stringOptionNO);
            
            t.env->DeleteLocalRef(stringArg1);
            t.env->DeleteLocalRef(stringArg2);
            t.env->DeleteLocalRef(stringOptionYES);
            t.env->DeleteLocalRef(stringOptionNO);
            t.env->DeleteLocalRef(t.classID);
        }
    }
    
    void takePictureJNI(const char* path) {
        JniMethodInfo t;
        
        if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "takePicture",
                                           "(Ljava/lang/String;)V")) {
            
            jstring jpath = t.env->NewStringUTF(path);
            
            t.env->CallStaticVoidMethod(t.classID, t.methodID, jpath);
            t.env->DeleteLocalRef(t.classID);
            t.env->DeleteLocalRef(jpath);
        }
    }

    const char* getExternalStoragePictureFolderJNI() {
        const char* pathString = NULL;

        JniMethodInfo t;
        if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getExternalStoragePictureFolder", "()Ljava/lang/String;")) {
            jstring externalPicturePath = (jstring) t.env->CallStaticObjectMethod(t.classID, t.methodID);
            pathString = JniHelper::jstring2string(externalPicturePath).c_str();

            t.env->DeleteLocalRef(externalPicturePath);
            t.env->DeleteLocalRef(t.classID);
        }
        
       return pathString;
    }
    
    void setAccelerometerIntervalJNI(float interval) {
        
    }
    
    void exitWheelsConfiguratorJNI() {
        JniMethodInfo t;
        
        if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "exitWheelsConfigurator",
                                           "()V")) {
            
            t.env->CallStaticVoidMethod(t.classID, t.methodID);
            t.env->DeleteLocalRef(t.classID);
        
        }
    }

    void postOnFacebookJNI(const char* path) {
        JniMethodInfo t;
        
        if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "postImageToFacebook",
                                           "(Ljava/lang/String;)V")) {
            
            jstring jpath = t.env->NewStringUTF(path);
            
            t.env->CallStaticVoidMethod(t.classID, t.methodID, jpath);
            t.env->DeleteLocalRef(t.classID);
            t.env->DeleteLocalRef(jpath);
        }
    }
    
    void sendPerEmailJNI(const char* path) {
        JniMethodInfo t;
        
        if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "sendPerEmail",
                                           "(Ljava/lang/String;)V")) {
            
            jstring jpath = t.env->NewStringUTF(path);
            
            t.env->CallStaticVoidMethod(t.classID, t.methodID, jpath);
            t.env->DeleteLocalRef(t.classID);
            t.env->DeleteLocalRef(jpath);
        }

    }
    
}
