//
//  MEMenuItem.h
//  alcar
//
//  Created by Michael Degn on 03.10.12.
//
//

#ifndef __PLATFORM_IPHONE_MECAMERASTREAM_H__
#define __PLATFORM_IPHONE_MECAMERASTREAM_H__

#include "platform/MECameraStreamDelegate.h"
#include "cocos2d.h"
#include <stddef.h>
#include <typeinfo>
#include <ctype.h>
#include <string.h>

NS_CC_BEGIN

class CC_DLL MECameraStream : CCObject //, public cocos2d::MECameraStreamDelegate

{
public:
    MECameraStream();
    ~MECameraStream();
    
    static MECameraStream* sharedStream();
    
    static void end();
    
    void takePicture();
    
    void startPreview();
    
    void stopPreview();
    
    void setDelegate(MECameraStreamDelegate* pDelegate);
    
    //virtual void updateTextureWithSampleBuffer(unsigned char*linebase, unsigned long width, unsigned long height);
    
private:
    
    //CCTexture2D *m_pTexture;

};

NS_CC_END

#endif