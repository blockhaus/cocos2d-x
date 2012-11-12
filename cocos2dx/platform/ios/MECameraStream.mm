//
//  MECameraStream.cpp
//  alcar
//
//  Created by Michael Degn on 03.10.12.
//
//


#include "MECameraStream.h"
#include "MECameraStreamWrapper.h"

NS_CC_BEGIN

static void static_end()
{
    //[MECameraStreamDisptacher end];
}

static bool static_hasCamera() {
    return [[MECameraStreamDisptacher sharedSream] hasCamera];
}

static void static_takePicture()
{
    [[MECameraStreamDisptacher sharedSream] takePicture];
    
}

static void static_startPreview()
{
    [[MECameraStreamDisptacher sharedSream] startPreview];
    
}

static void static_stopPreview()
{
    [[MECameraStreamDisptacher sharedSream] stopPreview];
    
}


static void static_setDelegate(cocos2d::MECameraStreamDelegate* pDelegate)
{
    [[MECameraStreamDisptacher sharedSream] addDelegate:pDelegate];
}

static cocos2d::MECameraStream *s_pStream;

MECameraStream::MECameraStream()
{
    /*
    m_pTexture = NULL;
    
    CCSize textureSize = CCSize(1024,1024);
    
    m_pTexture = new CCTexture2D();
    m_pTexture->initWithData(NULL, kCCTexture2DPixelFormat_RGBA8888, textureSize.width, textureSize.height, textureSize);
    m_pTexture->retain();
     */
    
}

MECameraStream::~MECameraStream()
{
    //if (m_pTexture) {
        //m_pTexture->
    //}
}

MECameraStream* MECameraStream::sharedStream()
{
    if (! s_pStream)
    {
        s_pStream = new MECameraStream();
        //s_pStream->setDelegate(s_pStream);
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
    return false;
    //return static_hasCamera();
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
    static_setDelegate(pDelegate);
}
/*
void MECameraStream::updateTextureWithSampleBuffer(unsigned char*linebase, unsigned long width, unsigned long height)
{
    
    if (m_pTexture)
    {
        
        //CCLOG("frame %f %f",width,height);
    
        CCRect newRect = CCRectMake(0, 0, width, height);
    
        //cameraSprite->setVertexRect(newRect);
        //cameraSprite->setContentSize(newRect.size);
    
        glBindTexture(GL_TEXTURE_2D, m_pTexture->getName());
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, (GLsizei)width, (GLsizei)height, 0, GL_BGRA_EXT, GL_UNSIGNED_BYTE, linebase);
    
    }
}
*/
NS_CC_END
