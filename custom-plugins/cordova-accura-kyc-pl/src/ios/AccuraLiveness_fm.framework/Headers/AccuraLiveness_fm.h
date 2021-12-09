//
//  AccuraOCR.h
//  AccuraOCR
//
//  Created by Technozer on 17/05/20.
//  Copyright © 2020 Technozer. All rights reserved.
//
#if !TARGET_OS_WATCH
#import <Foundation/Foundation.h>

//! Project version number for AccuraOCR.
FOUNDATION_EXPORT double AccuraLiveness_fmVersionNumber;

//! Project version string for AccuraOCR.
FOUNDATION_EXPORT const unsigned char AccuraLiveness_fmVersionString[];

#import <AccuraLiveness_fm/LivenessData.h>
#import <AccuraLiveness_fm/NSObject+Liveness.h>
#import <AccuraLiveness_fm/FacematchData.h>
#import <AccuraLiveness_fm/NSObject+Facematch.h>
#import "AccuraLiveness_fm/EngineWrapper.h"
#import "AccuraLiveness_fm/NSFaceRegion.h"
#import "AccuraLiveness_fm/FaceView.h"
#import "AccuraLiveness_fm/ImageHelper.h"

#endif

// In this header, you should import all the public headers of your framework using statements like #import <AccuraOCR/PublicHeader.h>


