#if __has_include("RCTBridge.h")
#import "RCTBridge.h"
#else
#import <React/RCTBridge.h>
#endif

#if __has_include("DTOpenKit.h")
#import "DTOpenKit.h"
#else
#import <DTShareKit/DTOpenKit.h>
#endif

@interface RNDingTalkShareModule : NSObject <RCTBridgeModule, DTOpenAPIDelegate>

@end
