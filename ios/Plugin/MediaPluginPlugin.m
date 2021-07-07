#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(MediaPluginPlugin, "MediaPlugin",
           CAP_PLUGIN_METHOD(getAlbums, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(createAlbum, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(savePhoto, CAPPluginReturnPromise);
)
