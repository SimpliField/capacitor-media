package com.simplifield.capacitor.mediaplugin;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import java.io.File;

@CapacitorPlugin(
    name = "MediaPlugin",
    permissions = {
        @Permission(
                strings = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                alias = "publicStorage"
        )
    }
)
public class MediaPluginPlugin extends Plugin {
    private static final String PERMISSION_DENIED_ERROR = "Unable to do file operation, user denied permission request";

    private MediaPlugin implementation = new MediaPlugin();

    @PluginMethod()
    public void getAlbums(PluginCall call) {
        Logger.debug(getLogTag(), "GET ALBUMS");
        if (!isStoragePermissionGranted()) {
            Logger.debug(getLogTag(), "NOT ALLOWED");
            requestAllPermissions(call, "permissionCallback");
        } else {
            Logger.debug(getLogTag(), "HAS PERMISSION");
            ContentResolver contentResolver = getActivity().getContentResolver();
            JSObject albums = implementation.getAlbums(contentResolver);
            call.resolve(albums);
        }
    }

    @PluginMethod()
    public void createAlbum(PluginCall call) {
        Logger.debug(getLogTag(), "CREATE ALBUM");
        if (!isStoragePermissionGranted()) {
            Logger.debug(getLogTag(), "NOT ALLOWED");
            requestAllPermissions(call, "permissionCallback");
        } else {
            Logger.debug(getLogTag(), "HAS PERMISSION");

            Context context = getContext();
            String albumName = call.getString("name");

            Integer result = implementation.createAlbum(context, albumName);
            if (result.equals(0)) {
                call.resolve();
            } else {
                call.reject("Can't create an album");
            }
        }
    }

    @PluginMethod()
    public void savePhoto(PluginCall call) {
        Logger.debug(getLogTag(), "SAVE PHOTO TO ALBUM");

        if (!isStoragePermissionGranted()) {
            Logger.debug(getLogTag(), "NOT ALLOWED");
            requestAllPermissions(call, "permissionCallback");
        } else {
            Logger.debug(getLogTag(), "HAS PERMISSION");

            String path = call.getString("path");
            if (path == null) {
                call.reject("Input file path is required");
                return;
            }
            String album = call.getString("album");
            Context context = getContext();

            try {
                File photo = implementation.savePhoto(context, path, album);

                scanPhoto(photo);

                JSObject result = new JSObject();
                result.put("filePath", photo.toString());

                call.resolve(result);
            } catch (RuntimeException e) {
                call.reject("RuntimeException occurred", e);
            }
        }
    }

    @PermissionCallback
    private void permissionCallback(PluginCall call) {
        if (!isStoragePermissionGranted()) {
            Logger.debug(getLogTag(), "User denied storage permission");
            call.reject(PERMISSION_DENIED_ERROR);
            return;
        }

        switch (call.getMethodName()) {
            case "getAlbums":
                getAlbums(call);
                break;
            case "savePhoto":
                savePhoto(call);
                break;
            case "createAlbum":
                createAlbum(call);
                break;
        }
    }

    /**
     * Checks the the given permission is granted or not
     * @return Returns true if the permission is granted and false if it is denied.
     */
    private boolean isStoragePermissionGranted() {
        return getPermissionState("publicStorage") == PermissionState.GRANTED;
    }

    /**
     * Reads the directory parameter from the plugin call
     * @param call the plugin call
     */
    private String getDirectoryParameter(PluginCall call) {
        return call.getString("directory");
    }

    /**
     * True if the given directory string is a public storage directory, which is accessible by the user or other apps.
     * @param directory the directory string.
     */
    private boolean isPublicDirectory(String directory) {
        return "DOCUMENTS".equals(directory) || "EXTERNAL_STORAGE".equals(directory);
    }

    private void scanPhoto(File imageFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        bridge.getActivity().sendBroadcast(mediaScanIntent);
    }
}
