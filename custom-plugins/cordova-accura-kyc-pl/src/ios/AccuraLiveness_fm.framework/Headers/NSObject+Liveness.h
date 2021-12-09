//
//  NSObject+Liveness.h
//  AccuraOCR
//
//  Created by Technozer on 17/02/21.
//  Copyright © 2021 Technozer. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "LivenessVC.h"
#import "NSObject+userDefaults.h"

NS_ASSUME_NONNULL_BEGIN

@interface Liveness : NSObject

-(void) setLiveness:(UIViewController*)livenessView;


-(void)setBackGroundColor:(NSString*)backGroundColor;

-(void) setCloseIconColor:(NSString*)closeIconColor;

-(void)setFeedbackBackGroundColor:(NSString*)feedbackBackGroundColor;

-(void) setFeedbackTextColor:(NSString*)feedbackTextColor;

-(void) setFeedbackTextSize:(float)feedbackTextSize;

-(void) setFeedBackframeMessage:(NSString*)feedBackframeMessage;

-(void) setFeedBackAwayMessage:(NSString*)feedBackAwayMessage;

-(void) setFeedBackOpenEyesMessage:(NSString*)feedBackOpenEyesMessage;

-(void) setFeedBackCloserMessage:(NSString*)feedBackCloserMessage;

-(void) setFeedBackCenterMessage:(NSString*)feedBackCenterMessage;

-(void) setLivenessURL:(NSString*)livenessURL;

-(void) setFeedbackMultipleFaceMessage:(NSString*)feedBackMultipleFaceMessage;

-(void) setFeedBackFaceSteadymessage:(NSString*)feedBackFaceSteadymessage;
       
-(void) setFeedBackLowLightMessage:(NSString*)feedBackLowLightMessage;

-(void) setFeedBackBlurFaceMessage:(NSString*)feedBackBlurFaceMessage;
-(void) setFeedBackGlareFaceMessage:(NSString*)feedBackGlareFaceMessage;

-(void) setBlurPercentage:(int)blurPercentage;

-(void) setGlarePercentage:(int) glaremin :(int) glareMax;
-(void)evaluateServerTrustWIthSSLPinning:(bool)isServerTrust;

//-(void) configure;

@end

NS_ASSUME_NONNULL_END
