import Foundation
import Photos

public typealias JSObject = [String:Any]

@objc public class MediaPlugin: NSObject {
 
  @objc public func fetchAlbumsToJs(_ loadSharedAlbums: Bool) -> [JSObject]  {
      var albums = [JSObject]()
            
      // Load our smart albums
      var fetchResult = PHAssetCollection.fetchAssetCollections(with: .smartAlbum, subtype: .albumRegular, options: nil)
      fetchResult.enumerateObjects({ (collection, count, stop: UnsafeMutablePointer<ObjCBool>) in
          var o = JSObject()
          o["name"] = collection.localizedTitle
          o["identifier"] = collection.localIdentifier
          o["type"] = "smart"
          albums.append(o)
      })
      
      if loadSharedAlbums {
          fetchResult = PHAssetCollection.fetchAssetCollections(with: .album, subtype: .albumCloudShared, options: nil)
          fetchResult.enumerateObjects({ (collection, count, stop: UnsafeMutablePointer<ObjCBool>) in
              var o = JSObject()
              o["name"] = collection.localizedTitle
              o["identifier"] = collection.localIdentifier
              o["type"] = "shared"
              albums.append(o)
          })
      }
      
      // Load our user albums
      PHCollectionList.fetchTopLevelUserCollections(with: nil).enumerateObjects({ (collection, count, stop: UnsafeMutablePointer<ObjCBool>) in
          var o = JSObject()
          o["name"] = collection.localizedTitle
          o["identifier"] = collection.localIdentifier
          o["type"] = "user"
          albums.append(o)
      })
      
      return albums
  }
}
