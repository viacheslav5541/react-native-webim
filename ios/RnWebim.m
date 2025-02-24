#import <React/RCTBridge.h>
#import <React/RCTEventEmitter.h>
#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(RnWebim, RCTEventEmitter)


RCT_EXTERN_METHOD(resume:
                  (NSDictionary *)params
                  withAppVersion: (NSString *)appVersion
                  withResolver: (RCTPromiseResolveBlock)resolve
                  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(pause:
                  (RCTPromiseResolveBlock)resolve
                  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(destroy:
                  (RCTPromiseResolveBlock)resolve
                  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(sendMessage:
                  (NSString *)message
                  withResolver: (RCTPromiseResolveBlock)resolve
                  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(getLastMessages:
                  (nonnull NSNumber *)limit
                  withResolver: (RCTPromiseResolveBlock)resolve
                  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(getNextMessages:
                  (nonnull NSNumber *)limit
                  withResolver: (RCTPromiseResolveBlock)resolve
                  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(sendFile:
                  (NSString *)uri
                  withName: (NSString *)name
                  withMime: (NSString *)mime
                  withExtension: (NSString *)extension
                  withResolver: (RCTPromiseResolveBlock)resolve
                  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(getUnreadByVisitorMessageCount:
                  (RCTPromiseResolveBlock)resolve
                  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(setChatRead:
                  (RCTPromiseResolveBlock)resolve
                  withRejecter: (RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(supportedEvents)

+ (BOOL) requiresMainQueueSetup {
  return YES;
}

@end
