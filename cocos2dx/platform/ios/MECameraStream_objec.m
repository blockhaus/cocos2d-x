//
//  GEButton.m
//  newgame
//
//  Created by Michael Degn on 31.05.11.
//  Copyright 2011 Blockhaus Medienagentur OG. All rights reserved.
//

#import "MECameraStream_objec.h"
#import "UIImage+ProportionalFill.h"
#import "UIImage+Tint.h"
#import "UIDevice-hardware.h"
//#include <mach/mach_time.h>

@implementation MECameraStream_objec {
    
    AVCaptureSession* session;
    AVCaptureStillImageOutput *stillImageOutput;

}

@synthesize delegate;


static MECameraStream_objec *sharedStream_=nil;

+ (MECameraStream_objec *)sharedCameraStream
{
	if (!sharedStream_)
		sharedStream_ = [[MECameraStream_objec alloc] init];
    
	return sharedStream_;
}

+(id)alloc
{
	NSAssert(sharedStream_ == nil, @"Attempted to allocate a second instance of a singleton.");
	return [super alloc];
}

-(id) init
{
	if( (self=[super init]) ) {
                
        NSError *error;
        
        //-- Setup our Capture Session.
        session = [[[AVCaptureSession alloc] init] retain];
        
        //[session beginConfiguration];
        
        //-- Set a preset session size.
        
        
        //-- Creata a video device and input from that Device.  Add the input to the capture session.
        AVCaptureDevice *videoDevice = nil;
        NSArray *devices = [AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo];
        for (AVCaptureDevice *device in devices)
        {
            if ([device position] == AVCaptureDevicePositionBack)
            {
                videoDevice = device;
            }
        }
        
		//AVCaptureDevice * videoDevice = [[AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo] retain];
		if(videoDevice == nil)
			return nil;
		
		//-- Add the device to the session.
		AVCaptureDeviceInput *input = [[AVCaptureDeviceInput deviceInputWithDevice:videoDevice error:&error] retain];
		if(error)
			return nil;
		
		[session addInput:input];
        
        
        // Make a still image output
        stillImageOutput = [[[AVCaptureStillImageOutput alloc] init] retain];
        
    
        if ( [session canAddOutput:stillImageOutput] ) {
            [session addOutput:stillImageOutput];
        }

        
        //[stillImageOutput addObserver:self forKeyPath:@"capturingStillImage" options:NSKeyValueObservingOptionNew context:AVCaptureStillImageIsCapturingStillImageContext];
        if ( [session canAddOutput:stillImageOutput] )
            [session addOutput:stillImageOutput];

        
		
		//-- Create the output for the capture session.  We want 32bit BRGA
		AVCaptureVideoDataOutput * dataOutput = [[[AVCaptureVideoDataOutput alloc] init] retain];
		[dataOutput setAlwaysDiscardsLateVideoFrames:YES]; // Probably want to set this to NO when we're recording
		[dataOutput setVideoSettings:[NSDictionary dictionaryWithObject:[NSNumber numberWithInt:kCVPixelFormatType_32BGRA] forKey:(id)kCVPixelBufferPixelFormatTypeKey]]; // Necessary for manual preview
        
		// we want our dispatch to be on the main thread so OpenGL can do things with the data
		[dataOutput setSampleBufferDelegate:self queue:dispatch_get_main_queue()];
		
        [UIImagePickerController isSourceTypeAvailable: UIImagePickerControllerSourceTypeCamera];
		
		[session addOutput:dataOutput];
        
        
        [self setPreviewDimension];
            
        
        if (error) {
            UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:[NSString stringWithFormat:@"Failed with error %d", (int)[error code]]
                                                                message:[error localizedDescription]
                                                               delegate:nil
                                                      cancelButtonTitle:@"Dismiss"
                                                      otherButtonTitles:nil];
            [alertView show];
            [alertView release];
            [self teardownAVCapture];
        }


    }
    return self;
}
-(id) initWithResourceFile:(NSString *)_file
{
    if( (self=[self init]) )
    {
        
        [self performSelector:@selector(tick) withObject:nil afterDelay:0.1];
    
    }
    return self;
}



- (void) dealloc
{
    if ([session isRunning])
    {
        [session stopRunning];
    }
    
    [session release];
    [stillImageOutput release];
    
    [super dealloc];
}

- (CGFloat) getScreenSize
{
    
    CGFloat width = [UIScreen mainScreen].bounds.size.width*[UIScreen mainScreen].scale;
    CGFloat height = [UIScreen mainScreen].bounds.size.height*[UIScreen mainScreen].scale;
    
    CGFloat size;
    if (width>height) {
        size = height;
    } else {
        size = width;
    }
    return size;
    
}

- (void) setPreviewDimension
{
    
    CGFloat size = [self getScreenSize];
    
    if (size==320.0) {
        //iPhone 3GS
        [session setSessionPreset:AVCaptureSessionPreset352x288];
    } else if (size==640.0) {
        //iPhone 4/4s/5
        [session setSessionPreset:AVCaptureSessionPreset640x480];
    } else if (size==768.0 || size==1536.0) {
        //IPad
        [session setSessionPreset:AVCaptureSessionPresetMedium];
    }

}

- (void) setPhotoDimension
{

    CGFloat size = [self getScreenSize];
    
    if (size==320.0) {
        //iPhone 3GS
        [session setSessionPreset:AVCaptureSessionPresetPhoto];
    } else if (size==640.0) {
        //iPhone 4/4s/5
        [session setSessionPreset:AVCaptureSessionPreset640x480];
    } else if (size==768.0 || size==1536.0) {
        //IPad
        [session setSessionPreset:AVCaptureSessionPresetPhoto];
    }

}

- (CGSize) getFinalImageSize
{
    
    CGFloat size = [self getScreenSize];
    
    if (size==320.0) {
        return CGSizeMake(854, 640);
    } else if (size==640.0) {
        return CGSizeMake(854*2.0, 640*2.0);
    } else if (size==768.0 || size==1536.0) {
        return CGSizeMake(854*2.0, 640*2.0);
    } else {
        return CGSizeMake(854, 640);
    }
    
}



- (void)teardownAVCapture
{
    /*
	[_videoDataOutput release];
	if (videoDataOutputQueue)
		dispatch_release(videoDataOutputQueue);
	[stillImageOutput removeObserver:self forKeyPath:@"isCapturingStillImage"];
	[stillImageOutput release];
	[previewLayer removeFromSuperlayer];
	[previewLayer release];
    */
}

- (void) captureOutput:(AVCaptureOutput *)captureOutput didOutputSampleBuffer:(CMSampleBufferRef)sampleBuffer fromConnection:(AVCaptureConnection *)connection
{
    
    CVImageBufferRef pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer);
    CVPixelBufferLockBaseAddress( pixelBuffer, 0 );
    
    size_t width = CVPixelBufferGetWidth(pixelBuffer);
    size_t height = CVPixelBufferGetHeight(pixelBuffer);

    unsigned char* linebase = (unsigned char *)CVPixelBufferGetBaseAddress( pixelBuffer );
    
    [delegate updateTextureWithSampleBuffer:linebase width:width height:height];
    
    CVPixelBufferUnlockBaseAddress( pixelBuffer, 0 );
}


- (void)displayErrorOnMainQueue:(NSError *)error withMessage:(NSString *)message
{
	dispatch_async(dispatch_get_main_queue(), ^(void) {
		UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:[NSString stringWithFormat:@"%@ (%d)", message, (int)[error code]]
															message:[error localizedDescription]
														   delegate:nil
												  cancelButtonTitle:@"Dismiss"
												  otherButtonTitles:nil];
		[alertView show];
		[alertView release];
	});
}

- (AVCaptureVideoOrientation)avOrientationForDeviceOrientation:(UIDeviceOrientation)deviceOrientation
{
	AVCaptureVideoOrientation result = deviceOrientation;
	if ( deviceOrientation == UIDeviceOrientationLandscapeLeft )
		result = AVCaptureVideoOrientationLandscapeRight;
	else if ( deviceOrientation == UIDeviceOrientationLandscapeRight )
		result = AVCaptureVideoOrientationLandscapeLeft;
	return result;
}

- (BOOL)hasCamera
{
    return [UIImagePickerController isSourceTypeAvailable: UIImagePickerControllerSourceTypeCamera];
}

- (void)takePicture
{
    
    
    [self setPhotoDimension];
	// Find out the current orientation and tell the still image output.
	AVCaptureConnection *stillImageConnection = [stillImageOutput connectionWithMediaType:AVMediaTypeVideo];
	UIDeviceOrientation curDeviceOrientation = [[UIDevice currentDevice] orientation];
	AVCaptureVideoOrientation avcaptureOrientation = [self avOrientationForDeviceOrientation:curDeviceOrientation];
	[stillImageConnection setVideoOrientation:avcaptureOrientation];
	//[stillImageConnection setVideoScaleAndCropFactor:effectiveScale];
	
    
    
    [stillImageOutput setOutputSettings:[NSDictionary dictionaryWithObject:[NSNumber numberWithInt:kCMPixelFormat_32BGRA]
																		forKey:(id)kCVPixelBufferPixelFormatTypeKey]];
    NSLog(@"start taking picture - %i - %i - %i",[stillImageConnection isEnabled],[stillImageOutput isCapturingStillImage],[session isRunning]);
    //[stillImageOutput setOutputSettings:[NSDictionary dictionaryWithObject:AVVideoCodecJPEG forKey:AVVideoCodecKey]];
	
    
	[stillImageOutput captureStillImageAsynchronouslyFromConnection:stillImageConnection
        completionHandler:^(CMSampleBufferRef imageDataSampleBuffer, NSError *error) {
            if (error) {
                [self displayErrorOnMainQueue:error withMessage:@"Take picture failed"];
            } else {
                
                [self performSelectorOnMainThread:@selector(stopPreview) withObject:nil waitUntilDone:FALSE];
                
                UIImage *original=[[UIImage imageWithCGImage:[self imageFromSampleBuffer:imageDataSampleBuffer]] retain];
            
                NSLog(@"image size: %f %f",original.size.width,original.size.height);
                
                CGSize newSize = CGSizeMake(960, 640);
                UIImage *cropedImage = [original imageCroppedToFitSize:newSize];
                
                NSData *data = UIImageJPEGRepresentation(cropedImage, 0.8);
                NSString *storePath = [[NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) lastObject] stringByAppendingPathComponent:@"camera_image.jpg"];
                NSLog(@"save to path:%@",storePath);
                if(![data writeToFile:storePath atomically:YES]) {
                    NSLog(@"COULD NOT STORE FILE5 %@", storePath);
                }
                
                [original release];
                
                [delegate didTakePicture:storePath];
                
                

            }
        }
    ];
    
    //[self stopPreview];
}



- (CGImageRef) imageFromSampleBuffer:(CMSampleBufferRef) sampleBuffer // Create a CGImageRef from sample buffer data
{
    CVImageBufferRef imageBuffer = CMSampleBufferGetImageBuffer(sampleBuffer);
    CVPixelBufferLockBaseAddress(imageBuffer,0);        // Lock the image buffer
    
    uint8_t *baseAddress = (uint8_t *)CVPixelBufferGetBaseAddressOfPlane(imageBuffer, 0);   // Get information of the image
    size_t bytesPerRow = CVPixelBufferGetBytesPerRow(imageBuffer);
    size_t width = CVPixelBufferGetWidth(imageBuffer);
    size_t height = CVPixelBufferGetHeight(imageBuffer);
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    
    CGContextRef newContext = CGBitmapContextCreate(baseAddress, width, height, 8, bytesPerRow, colorSpace, kCGBitmapByteOrder32Little | kCGImageAlphaPremultipliedFirst);
    CGImageRef newImage = CGBitmapContextCreateImage(newContext);
    CGContextRelease(newContext);
    
    CGColorSpaceRelease(colorSpace);
    CVPixelBufferUnlockBaseAddress(imageBuffer,0);
    /* CVBufferRelease(imageBuffer); */  // do not call this!
    
    return newImage;
}


- (void) startPreview
{

    if (![session isRunning])
    {
        [self setPreviewDimension];
        [session startRunning];
    };

}

- (void) stopPreview
{
    
    if ([session isRunning])
    {
        [session stopRunning];
    };
    
}


@end
