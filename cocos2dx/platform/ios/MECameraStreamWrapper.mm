//
//  MECameraStreamWrapper.cpp
//  alcar
//
//  Created by Michael Degn on 03.10.12.
//
//


#include "MECameraStreamWrapper.h"
#include <string>

@implementation MECameraStreamDisptacher


static MECameraStreamDisptacher* s_pMECameraStreamDisptacher;

@synthesize delegate_;

+ (id) sharedSream
{
    if (s_pMECameraStreamDisptacher == nil) {
        s_pMECameraStreamDisptacher = [[self alloc] init];
        
        
    }
    
    return s_pMECameraStreamDisptacher;
}

- (id) init
{
    //acceleration_ = new cocos2d::CCAcceleration();
    self = [super init];
    if (self) {
    
        delegate_ = NULL;
        //CvCapture* capture = AVCaptureConnection();
        
    }
    return self;
}

- (void) dealloc
{
    s_pMECameraStreamDisptacher = 0;
    delegate_ = 0;
    //delete acceleration_;
    [super dealloc];
}

- (void) addDelegate: (cocos2d::MECameraStreamDelegate *) delegate
{
    delegate_ = delegate;
    
    if (delegate_)
    {
        [[MECameraStream_objec sharedCameraStream] setDelegate:self];
    }
    else
    {
        [[MECameraStream_objec sharedCameraStream] setDelegate:nil];
    }
}


-(BOOL) hasCamera {
    return [[MECameraStream_objec sharedCameraStream] hasCamera];
}

-(void) takePicture {
    [[MECameraStream_objec sharedCameraStream] takePicture];
}


-(void) startPreview {
    [[MECameraStream_objec sharedCameraStream] startPreview];
}

-(void) stopPreview {
    [[MECameraStream_objec sharedCameraStream] stopPreview];
}

-(void) updateTextureWithSampleBuffer:(unsigned char*)linebase width:(unsigned long)width height:(unsigned long)height
{
    if (delegate_) {
    
        delegate_->updateTextureWithSampleBuffer(linebase,width,height);
        
    }
}

-(void) didTakePicture:(NSString*)imagePath
{
    if (delegate_) {
 
        std::string cImagePath = [imagePath UTF8String];
        delegate_->didTakePicture(cImagePath.c_str());
    }
}

@end



