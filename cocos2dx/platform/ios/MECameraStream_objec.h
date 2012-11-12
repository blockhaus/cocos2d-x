//
//  GEButton.h
//  newgame
//
//  Created by Michael Degn on 31.05.11.
//  Copyright 2011 Blockhaus Medienagentur OG. All rights reserved.
//
#import <AVFoundation/AVFoundation.h>
#import <UIKit/UIKit.h>

@protocol MECameraStreamDelegate
-(void) updateTextureWithSampleBuffer:(unsigned char*)linebase width:(unsigned long)width height:(unsigned long)height;
-(void) didTakePicture:(NSString*)imagePath;
@end

@interface MECameraStream_objec : NSObject<AVCaptureVideoDataOutputSampleBufferDelegate> {
    
    id <MECameraStreamDelegate> delegate;
    
}

+ (MECameraStream_objec *)sharedCameraStream;


@property (nonatomic, assign) id delegate;


@property (nonatomic, retain) AVCaptureSession* _session;

- (BOOL) hasCamera;
- (void) takePicture;
- (void) startPreview;
- (void) stopPreview;

@end
