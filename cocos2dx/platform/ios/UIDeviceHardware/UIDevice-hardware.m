/* Thanks to Emanuele Vulcano, Kevin Ballard/Eridius, Ryandjohnson */

/*
 - Bluetooth? Screen pixels? Dot pitch? Accelerometer? GPS disabled in Egypt (and others?). - @halm
*/

#import "UIDevice-hardware.h"
#include <sys/types.h>
#include <sys/sysctl.h>

@implementation UIDevice (Hardware)

/*
 Platforms
 iPhone1,1 -> iPhone 1G
 iPhone1,2 -> iPhone 3G 
 iPod1,1   -> iPod touch 1G 
 iPod2,1   -> iPod touch 2G 
*/

- (NSString *) platform
{
	size_t size;
    sysctlbyname("hw.machine", NULL, &size, NULL, 0);
    char *machine = malloc(size);
	sysctlbyname("hw.machine", machine, &size, NULL, 0);
	NSString *platform = [NSString stringWithCString:machine encoding: NSUTF8StringEncoding];
	free(machine);
	return platform;
}

- (NSString *) platformType
{
	NSString *platformString = [self platform];
    
	if ([platformString isEqual:@"i386"])      return @"Simulator";  //iPhone Simulator
    if ([platformString isEqual:@"iPhone1,1"]) return @"iPhone1G";   //iPhone 1G
    if ([platformString isEqual:@"iPhone1,2"]) return @"iPhone3G";   //iPhone 3G
    if ([platformString isEqual:@"iPhone2,1"]) return @"iPhone3GS";  //iPhone 3GS
    if ([platformString isEqual:@"iPhone3,1"]) return @"iPhone4 AT&T";  //iPhone 4 - AT&T
    if ([platformString isEqual:@"iPhone3,2"]) return @"iPhone4 Other";  //iPhone 4 - Other carrier
    if ([platformString isEqual:@"iPhone3,3"]) return @"iPhone4";    //iPhone 4 - Other carrier
    if ([platformString isEqual:@"iPhone4,1"]) return @"iPhone4S";   //iPhone 4S
    if ([platformString isEqual:@"iPhone5,1"]) return @"iPhone5";    //iPhone 5 (GSM)
    if ([platformString isEqual:@"iPod1,1"])   return @"iPod1stGen"; //iPod Touch 1G
    if ([platformString isEqual:@"iPod2,1"])   return @"iPod2ndGen"; //iPod Touch 2G
    if ([platformString isEqual:@"iPod3,1"])   return @"iPod3rdGen"; //iPod Touch 3G
    if ([platformString isEqual:@"iPod4,1"])   return @"iPod4thGen"; //iPod Touch 4G
    if ([platformString isEqual:@"iPad1,1"])   return @"iPadWiFi";   //iPad Wifi
    if ([platformString isEqual:@"iPad1,2"])   return @"iPad3G";     //iPad 3G
    if ([platformString isEqual:@"iPad2,1"])   return @"iPad2";      //iPad 2 (WiFi)
    if ([platformString isEqual:@"iPad2,2"])   return @"iPad2";      //iPad 2 (GSM)
    if ([platformString isEqual:@"iPad2,3"])   return @"iPad2";      //iPad 2 (CDMA)
    
    NSString *aux = [[platformString componentsSeparatedByString:@","] objectAtIndex:0];
    
    //If a newer version exist
    if ([aux rangeOfString:@"iPhone"].location!=NSNotFound) {
        int version = [[aux stringByReplacingOccurrencesOfString:@"iPhone" withString:@""] intValue];
        if (version == 3) return @"iPhone4";
        if (version >= 4) return @"iPhone4s";
    }
    if ([aux rangeOfString:@"iPod"].location!=NSNotFound) {
        int version = [[aux stringByReplacingOccurrencesOfString:@"iPod" withString:@""] intValue];
        if (version >=4) return @"iPod4thGen";
    }
    if ([aux rangeOfString:@"iPad"].location!=NSNotFound) {
        int version = [[aux stringByReplacingOccurrencesOfString:@"iPad" withString:@""] intValue];
        if (version ==1) return @"iPad3G";
        if (version >=2) return @"iPad2";
    }
    //If none was found, send the original string
    return platformString;

}


@end
