/****************************************************************************
Copyright (c) 2010 cocos2d-x.org

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
#include "MECameraStream.h"
#include "jni/Java_org_cocos2dx_lib_Cocos2dxHelper.h"
#include "jni/Java_org_cocos2dx_lib_Cocos2dxCamera.h"
#include <stdio.h>
#include <android/log.h>
#include "platform/CCFileUtils.h"

#define  LOG_TAG    "MECameraStream_android"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

namespace cocos2d
{

    static void static_end()
    {
     
    }
    
    static bool static_hasCamera() {
    	return hasCameraJNI();
    }
    
    static void static_takePicture()
    {
    	std::string path = CCFileUtils::sharedFileUtils()->getWriteablePath() + "camera_image.jpg";

      takePictureJNI(path.c_str());
    }
    
    static void static_startPreview()
    {
       
        startCameraPreviewJNI();
    }
    
    static void static_stopPreview()
    {
        stopCameraPreviewJNI();
    }
    
    
    static void static_setDelegate(cocos2d::MECameraStreamDelegate* pDelegate)
    {
        
    }
    
    static cocos2d::MECameraStream *s_pStream;
    
    MECameraStream::MECameraStream()
    {
        
    }
    
    MECameraStream::~MECameraStream()
    {
        
    }
    
    MECameraStream* MECameraStream::sharedStream()
    {
        if (! s_pStream)
        {
            s_pStream = new MECameraStream();
        }
        
        return s_pStream;
    }
    
    void MECameraStream::end()
    {
        if (s_pStream)
        {
            delete s_pStream;
            s_pStream = NULL;
        }
        
        static_end();
    }
    
    bool MECameraStream::hasCamera()
    {
    	bool hasCamera = static_hasCamera();
        return hasCamera;
    }
    
    void MECameraStream::takePicture()
    {
        static_takePicture();
    }
    
    void MECameraStream::startPreview()
    {
        static_startPreview();
    }
    
    void MECameraStream::stopPreview()
    {
        static_stopPreview();
    }

    void MECameraStream::setDelegate(MECameraStreamDelegate* pDelegate)
    {
    	m_pCameraStreamDelegate = pDelegate;
    }

    void MECameraStream::update(int* rgb, int width, int height)
    {
        if (m_pCameraStreamDelegate) {
        	unsigned long w = width;
        	unsigned long h = height;

            m_pCameraStreamDelegate->updateTextureWithSampleBuffer((unsigned char*)rgb, w, h);
        }
    }

    void MECameraStream::onPictureTaken(const char* filePath) {
    	if(m_pCameraStreamDelegate) {
    		CCLog("MECameraStream::onPictureTaken(%s)", filePath);
    		m_pCameraStreamDelegate->didTakePicture(filePath);
    	}
    }

}
