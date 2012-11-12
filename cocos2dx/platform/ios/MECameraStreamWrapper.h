//
//  MEMenuItem.h
//  alcar
//
//  Created by Michael Degn on 03.10.12.
//
//

#import <Foundation/Foundation.h>
#import "MECameraStreamDelegate.h"
#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>
#import "MECameraStream_objec.h"

@interface MECameraStreamDisptacher : NSObject<MECameraStreamDelegate>
{
    cocos2d::MECameraStreamDelegate *delegate_;
    

}

@property (readwrite) cocos2d::MECameraStreamDelegate *delegate_;

+ (id) sharedSream;
- (id) init;
- (BOOL) hasCamera;
- (void) takePicture;
- (void) startPreview;
- (void) stopPreview;
- (void) addDelegate: (cocos2d::MECameraStreamDelegate *) delegate;

@end