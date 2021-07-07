package com.simplifield.capacitor.mediaplugin;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MediaPlugin {

    public JSObject getAlbums(ContentResolver contentResolver) {
        JSObject response = new JSObject();
        JSArray albums = new JSArray();
        StringBuffer list = new StringBuffer();

        if (Build.VERSION.SDK_INT >= 29) {
            Logger.debug("Media Plugin", "___GET ALBUMS Android 10+ no albums - saving to the external storage");
            response.put("albums", albums);
            return response;
        }

        String[] projection = new String[]{"DISTINCT " + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME};
        //String[] projection = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATE_TAKEN}; doesn't return empty Dirs
        Cursor cur = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

        while (cur.moveToNext()) {
            String albumName = cur.getString((cur.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)));
            JSObject album = new JSObject();

            list.append(albumName + "\n");

            album.put("name", albumName);
            albums.put(album);
        }

        response.put("albums", albums);
        Logger.debug("Media Plugin", String.valueOf(response));

        return response;
    }

    public Integer createAlbum(Context context, String albumName) {
        String folder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            folder = context.getExternalMediaDirs()[0].getAbsolutePath()+"/"+albumName;
        }else{
            folder = Environment.getExternalStoragePublicDirectory(albumName).toString();
        }

        File f = new File(folder);

        if (!f.exists()) {
            if (!f.mkdir()) {
                Logger.debug("Media Plugin", "___ERROR ALBUM");
                return 1;
            } else {
                Logger.debug("Media Plugin", "___SUCCESS ALBUM CREATED");
                return 0;
            }
        } else {
            Logger.debug("Media Plugin", "___WARN ALBUM ALREADY EXISTS");
            return 0;
        }
    }

    public File savePhoto(Context context, String path, String album) {
        Uri inputUri = Uri.parse(path);
        File inputFile = new File(inputUri.getPath());

        String dest = getAlbumFolderPath(context, album, "PHOTOS");

        File albumDir = new File(dest);
        File file = copyFile(inputFile, albumDir);

        return file;
    }

    private String getAlbumFolderPath(Context context, String folderName, String destination) {
        String albumFolderPath;

        if (destination == "MOVIES") {
            albumFolderPath = Environment.DIRECTORY_MOVIES;
        } else {
            albumFolderPath = Environment.DIRECTORY_PICTURES;
        }

        String baseFolderPath;// = Environment.getExternalStoragePublicDirectory(albumFolderPath).getPath();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            baseFolderPath = context.getExternalMediaDirs()[0].getAbsolutePath();
        } else {
            baseFolderPath = Environment.getExternalStoragePublicDirectory(albumFolderPath).getAbsolutePath();
        }
        String dirPath = TextUtils.isEmpty(folderName) ? baseFolderPath : baseFolderPath  + File.separator + folderName;

        albumFolderPath = createDirIfNotExist(dirPath);

        return albumFolderPath != null ? albumFolderPath : baseFolderPath;
    }

    private String createDirIfNotExist(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                return dir.getPath();
            } else {
                return null;
            }
        } else {
            return dir.getPath();
        }
    }


    private File copyFile(File inputFile, File albumDir) {

        // if destination folder does not exist, create it
        if (!albumDir.exists()) {
            if (!albumDir.mkdir()) {
                throw new RuntimeException("Destination folder does not exist and cannot be created.");
            }
        }

        String absolutePath = inputFile.getAbsolutePath();
        String extension = absolutePath.substring(absolutePath.lastIndexOf("."));

        // generate image file name using current date and time
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        File newFile = new File(albumDir, "IMG_" + timeStamp + extension);

        // Read and write image files
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            inChannel = new FileInputStream(inputFile).getChannel();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Source file not found: " + inputFile + ", error: " + e.getMessage());
        }
        try {
            outChannel = new FileOutputStream(newFile).getChannel();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Copy file not found: " + newFile + ", error: " + e.getMessage());
        }

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            throw new RuntimeException("Error transfering file, error: " + e.getMessage());
        } finally {
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    Logger.debug("Media Plugin", "Error closing input file channel: " + e.getMessage());
                    // does not harm, do nothing
                }
            }
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    Logger.debug("Media Plugin", "Error closing output file channel: " + e.getMessage());
                    // does not harm, do nothing
                }
            }
        }

        return newFile;
    }
}
