#import "FlutterPaynimoPlugin.h"
#if __has_include(<flutter_paynimo/flutter_paynimo-Swift.h>)
#import <flutter_paynimo/flutter_paynimo-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_paynimo-Swift.h"
#endif

@implementation FlutterPaynimoPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterPaynimoPlugin registerWithRegistrar:registrar];
}
@end
