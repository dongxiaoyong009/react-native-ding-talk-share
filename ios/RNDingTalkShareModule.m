
#import "RNDingTalkShareModule.h"

static NSString *const DING_TALK_NOT_INSTALLED_CODE = @"NOT_INSTALLED";
static NSString *const DING_TALK_NOT_SUPPORTED_CODE = @"NOT_SUPPORTED";
static NSString *const DING_TALK_SHARE_FAILED_CODE = @"SHARE_FAILED";

@interface RNDingTalkShareModule ()

@property (nonatomic, strong) NSString *appId;
@property (nonatomic, copy) RCTPromiseResolveBlock resolveBlock;
@property (nonatomic, copy) RCTPromiseRejectBlock rejectBlock;

@end

@implementation RNDingTalkShareModule

RCT_EXPORT_MODULE()

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

- (instancetype)init {
    self = [super init];
    if (self) {
        [self initDingTalkShare];
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(handleOpenURLNotification:)
                                                     name:@"RCTOpenURLNotification"
                                                   object:nil];
    }
    return self;
}

RCT_EXPORT_METHOD(isInstalled
                  : (RCTPromiseResolveBlock)resolve
                  : (RCTPromiseRejectBlock)reject) {
    resolve(@([DTOpenAPI isDingTalkInstalled]));
}

RCT_EXPORT_METHOD(isSupported
                  : (RCTPromiseResolveBlock)resolve
                  : (RCTPromiseRejectBlock)reject) {
    resolve(@([DTOpenAPI isDingTalkSupportOpenAPI]));
}

RCT_EXPORT_METHOD(registerApp
                  : (NSString *)appId
                  : (NSString *)appDescription
                  : (RCTPromiseResolveBlock)resolve
                  : (RCTPromiseRejectBlock)reject) {
    BOOL result;
    if (appDescription) {
        result = [DTOpenAPI registerApp:appId appDescription:appDescription];
    } else {
        result = [DTOpenAPI registerApp:appId];
    }
    resolve(@(result));
}

RCT_EXPORT_METHOD(shareImage
                  : (NSString *)image
                  : (RCTPromiseResolveBlock)resolve
                  : (RCTPromiseRejectBlock)reject) {
    self.resolveBlock = resolve;
    self.rejectBlock = reject;
    if (![self checkSupport]) {
        return;
    }
    DTSendMessageToDingTalkReq *sendMessageReq = [[DTSendMessageToDingTalkReq alloc] init];
    DTMediaMessage *mediaMessage = [[DTMediaMessage alloc] init];
    DTMediaImageObject *imageObject = [[DTMediaImageObject alloc] init];
    imageObject.imageURL = image;
    mediaMessage.mediaObject = imageObject;
    sendMessageReq.message = mediaMessage;

    if (![DTOpenAPI sendReq:sendMessageReq]) {
        reject(DING_TALK_SHARE_FAILED_CODE, @"分享失败", nil);
    }
}

RCT_EXPORT_METHOD(shareWebPage
                  : (NSString *)url
                  : (NSString *)thumbImage
                  : (NSString *)title
                  : (NSString *)content
                  : (RCTPromiseResolveBlock)resolve
                  : (RCTPromiseRejectBlock)reject) {
    self.resolveBlock = resolve;
    self.rejectBlock = reject;
    if (![self checkSupport]) {
        return;
    }
    DTSendMessageToDingTalkReq *sendMessageReq = [[DTSendMessageToDingTalkReq alloc] init];
    DTMediaMessage *mediaMessage = [[DTMediaMessage alloc] init];
    DTMediaWebObject *webObject = [[DTMediaWebObject alloc] init];
    webObject.pageURL = url;
    mediaMessage.title = title;
    mediaMessage.thumbURL = thumbImage;
    mediaMessage.messageDescription = content;
    mediaMessage.mediaObject = webObject;
    sendMessageReq.message = mediaMessage;

    if (![DTOpenAPI sendReq:sendMessageReq]) {
        reject(DING_TALK_SHARE_FAILED_CODE, @"分享失败", nil);
    }
}

#pragma mark - DTOpenAPIDelegate

/**
 收到一个来自钉钉的请求, 第三方APP处理完成后要调用 +[DTOpenAPI sendResp:] 将处理结果返回给钉钉.
 
 @param req 来自钉钉具体的请求.
 */
- (void)onReq:(DTBaseReq *)req {
}

/**
 第三方APP使用 +[DTOpenAPI sendReq:] 向钉钉发送消息后, 钉钉会处理完请求后会回调该接口.
 
 @param resp 来自钉钉具体的响应.
 */
- (void)onResp:(DTBaseResp *)resp {
    if (resp.errorCode == DTOpenAPISuccess) {
        self.resolveBlock(@YES);
    } else {
        self.rejectBlock([NSString stringWithFormat:@"%@", @(resp.errorCode)], resp.errorMessage, nil);
    }
}

#pragma mark - Private

- (void)initDingTalkShare {
    NSArray *urlTypes = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleURLTypes"];
    for (id type in urlTypes) {
        NSArray *urlSchemes = [type objectForKey:@"CFBundleURLSchemes"];
        for (id scheme in urlSchemes) {
            if ([scheme isKindOfClass:[NSString class]]) {
                NSString *value = (NSString *)scheme;
                if ([value hasPrefix:@"ding"] && (nil == _appId)) {
                    _appId = value;
                    [DTOpenAPI registerApp:_appId];
                    break;
                }
            }
        }
    }
}

- (void)handleOpenURLNotification:(NSNotification *)notification {
    NSURL *url = [NSURL URLWithString:[notification userInfo][@"url"]];
    NSString *schemaPrefix = _appId;
    if ([url isKindOfClass:[NSURL class]] && [[url absoluteString] hasPrefix:[schemaPrefix stringByAppendingString:@"://"]]) {
        [DTOpenAPI handleOpenURL:url delegate:self];
    }
}

- (BOOL)checkSupport {
    if (![DTOpenAPI isDingTalkInstalled] && self.rejectBlock) {
        self.rejectBlock(DING_TALK_NOT_INSTALLED_CODE, @"请安装钉钉客户端", nil);
        return NO;
    }
    if (![DTOpenAPI isDingTalkSupportOpenAPI] && self.rejectBlock) {
        self.rejectBlock(DING_TALK_NOT_INSTALLED_CODE, @"请升级钉钉客户端", nil);
        return NO;
    }
    return YES;
}

@end
