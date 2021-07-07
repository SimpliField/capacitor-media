import Foundation
import Capacitor
import Photos

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(MediaPluginPlugin)
public class MediaPluginPlugin: CAPPlugin {
  private let implementation = MediaPlugin()

  static let DEFAULT_QUANTITY = 25
  static let DEFAULT_TYPES = "photos"
  static let DEFAULT_THUMBNAIL_WIDTH = 256
  static let DEFAULT_THUMBNAIL_HEIGHT = 256
  
  // Must be lazy here because it will prompt for permissions on instantiation without it
  lazy var imageManager = PHCachingImageManager()
  
  @objc func getAlbums(_ call: CAPPluginCall) {
      checkAuthorization(allowed: {
        let loadSharedAlbums = call.getBool("loadShared", false)
        let albums = self.implementation.fetchAlbumsToJs(loadSharedAlbums)
        call.resolve([
            "albums": albums
            ])
      }, notAllowed: {
          call.reject("Access to photos not allowed by user")
      })
  }
  
  @objc func createAlbum(_ call: CAPPluginCall) {
      guard let name = call.getString("name") else {
          call.reject("Must provide a name")
          return
      }
      
      checkAuthorization(allowed: {
          PHPhotoLibrary.shared().performChanges({
              PHAssetCollectionChangeRequest.creationRequestForAssetCollection(withTitle: name)
          }, completionHandler: { success, error in
              if !success {
                call.reject("Unable to create album", error?.localizedDescription)
                  return
              }
              call.resolve()
          })
      }, notAllowed: {
          call.reject("Access to photos not allowed by user")
      })
  }
  
  @objc func savePhoto(_ call: CAPPluginCall) {
      guard let data = call.getString("path") else {
          call.reject("Must provide the data path")
          return
      }
      
      let albumId = call.getString("album")
      var targetCollection: PHAssetCollection?
      
      if albumId != nil {
          let albumFetchResult = PHAssetCollection.fetchAssetCollections(withLocalIdentifiers: [albumId!], options: nil)
          albumFetchResult.enumerateObjects({ (collection, count, _) in
              targetCollection = collection
          })
          if targetCollection == nil {
              call.reject("Unable to find that album")
              return
          }
          if !targetCollection!.canPerform(.addContent) {
              call.reject("Album doesn't support adding content (is this a smart album?)")
              return
          }
      }
      
      checkAuthorization(allowed: {
          // Add it to the photo library.
          PHPhotoLibrary.shared().performChanges({
              
              let url = URL(string: data)
              let data = try? Data(contentsOf: url!)
              if (data != nil) {
                  let image = UIImage(data: data!)
                  let creationRequest = PHAssetChangeRequest.creationRequestForAsset(from: image!)
                  
                  if let collection = targetCollection {
                      let addAssetRequest = PHAssetCollectionChangeRequest(for: collection)
                      addAssetRequest?.addAssets([creationRequest.placeholderForCreatedAsset! as Any] as NSArray)
                  }
              } else {
                  call.reject("Could not convert fileURL into Data");
              }
              
          }, completionHandler: {success, error in
              if !success {
                call.reject("Unable to save image to album", error?.localizedDescription)
              } else {
                  call.resolve()
              }
          })
      }, notAllowed: {
          call.reject("Access to photos not allowed by user")
      })

  }
  
  @objc func saveVideo(_ call: CAPPluginCall) {
      guard let data = call.getString("path") else {
          call.reject("Must provide the data path")
          return
      }
      
      let albumId = call.getString("album")
      var targetCollection: PHAssetCollection?
      
      if albumId != nil {
          let albumFetchResult = PHAssetCollection.fetchAssetCollections(withLocalIdentifiers: [albumId!], options: nil)
          albumFetchResult.enumerateObjects({ (collection, count, _) in
              targetCollection = collection
          })
          if targetCollection == nil {
              call.reject("Unable to find that album")
              return
          }
          if !targetCollection!.canPerform(.addContent) {
              call.reject("Album doesn't support adding content (is this a smart album?)")
              return
          }
      }
      
      checkAuthorization(allowed: {
          // Add it to the photo library.
          PHPhotoLibrary.shared().performChanges({
              let creationRequest = PHAssetChangeRequest.creationRequestForAssetFromVideo(atFileURL: URL(string: data)!)
              
              if let collection = targetCollection {
                  let addAssetRequest = PHAssetCollectionChangeRequest(for: collection)
                  addAssetRequest?.addAssets([creationRequest?.placeholderForCreatedAsset! as Any] as NSArray)
              }
          }, completionHandler: {success, error in
              if !success {
                call.reject("Unable to save video to album", error?.localizedDescription)
              } else {
                  call.resolve()
              }
          })
      }, notAllowed: {
          call.reject("Access to photos not allowed by user")
      })
      
  }
  
  func checkAuthorization(allowed: @escaping () -> Void, notAllowed: @escaping () -> Void) {
      let status = PHPhotoLibrary.authorizationStatus()
      if status == PHAuthorizationStatus.authorized {
          allowed()
      } else {
          PHPhotoLibrary.requestAuthorization({ (newStatus) in
              if newStatus == PHAuthorizationStatus.authorized {
                  allowed()
              } else {
                  notAllowed()
              }
          })
      }
  }
}
