package com.kc.kimageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 缓存图片到磁盘.  android官方的libcore/io/DiskLruCache.java更健壮高效,该实现相对简单
 */
public class DiskLruCache {
    private static final int INITIAL_CAPACITY = 32;
    private static final float LOAD_FACTOR = 0.75f;

    private static final String CACHE_FILENAME_PREFIX = "cache_";
    private static final String DEFAULT_URL_ENCODING = "UTF-8";

    private final File mCacheDir;
    private long maxCacheByteSize = 1024 * 1024 * 5; // 5MB default
    private int cacheSize = 0;
    private int cacheByteSize = 0;


    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    private int mCompressQuality = 70;

    private final Map<String ,String> mLinkedHashMap = Collections.synchronizedMap(
            new LinkedHashMap<String, String>(INITIAL_CAPACITY, LOAD_FACTOR, true));


    /**
     * A filename filter to use to identify the cache filenames which have
     * CACHE_FILENAME_PREFIX prepended.
     */
    private static final FilenameFilter cacheFileFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            return filename.startsWith(CACHE_FILENAME_PREFIX);
        }
    };

    public static DiskLruCache openCache(Context context, File cacheDir, long maxBytesSize) {
        if (! cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        if (cacheDir.isDirectory() && cacheDir.canWrite() && Utils.getUsableSpace(cacheDir) > maxBytesSize) {
            return new DiskLruCache(cacheDir, maxBytesSize);
        }
        return null;
    }


    private DiskLruCache(File cacheDir, long maxByteSize) {
        mCacheDir = cacheDir;
        maxCacheByteSize = maxByteSize;

        repopulateFromDisk();

    }

    /**
     *  Puts entries in the map of URL -> file path based off of what is on disk.
     */
    private void repopulateFromDisk() {
        synchronized (mLinkedHashMap) {
            for (File file : mCacheDir.listFiles(cacheFileFilter)) {
                final String path = mCacheDir.getPath() + File.separator + file.getName();
                final String encoded = file.getName().substring(CACHE_FILENAME_PREFIX.length());

                final String key;
                try {
                    key = URLDecoder.decode(encoded, DEFAULT_URL_ENCODING);
                    put(key, path);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
            flushCache();
        }

    }


    /**
     * 保存bitmap到磁盘
     * @param key 唯一key
     * @param data bitmap
     */
    public void put(String key, Bitmap data) {
        synchronized (mLinkedHashMap) {
            if (mLinkedHashMap.get(key) == null) {

                try {
                    final String file = createFilePath(mCacheDir, key);
                    if (writeBItmapToFile(data, file)) {
                        put(key, file);
                        flushCache();
                    }
                } catch (IOException e) {
                    Log.e("kcc", "Error in put: " + e.getMessage());
                }
            }
        }
    }



    private void put(String key, String file) {
        mLinkedHashMap.put(key, file);
        cacheSize = mLinkedHashMap.size();
        cacheByteSize += new File(file).length();
    }

    private static final int MAX_REMOVALS = 4;
    private final int maxCacheItemSize = 64; // 64 item default
    /**
     * 刷新缓存; 如果超出大小则删除最老的文件.  如果image和key改变了,则不会追踪了
     */
    private void flushCache() {
        Map.Entry<String, String> eldestEntry;
        File eldestFile;
        long eldestFileSize;
        int count = 0;

        while (count < MAX_REMOVALS && (cacheSize > maxCacheItemSize || cacheByteSize > maxCacheByteSize)) {
            eldestEntry = mLinkedHashMap.entrySet().iterator().next();
            eldestFile = new File(eldestEntry.getValue());
            eldestFileSize = eldestFile.length();

            mLinkedHashMap.remove(eldestEntry.getKey());
            eldestFile.delete();
            cacheSize = mLinkedHashMap.size();
            cacheByteSize -= eldestFileSize;
            count++;
            if (BuildConfig.DEBUG) {
                Log.d("kcc", "flushCache - Removed cache file, " + eldestFile
                        + ", " + eldestFileSize);
            }
        }


    }


    void putFromFetch(String url) {
        put(url, createFilePath(url));
    }

    public String createFilePath(String key) {
        return createFilePath(mCacheDir, key);
    }


    public static String createFilePath(File cacheDir, String key) {
        try {
            return cacheDir.getAbsolutePath()
                    + File.separator
                    + CACHE_FILENAME_PREFIX
                    + URLEncoder.encode(key.replace("*", ""), DEFAULT_URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            Log.e("kcc", "createFilePath - " + e);
        }
        return null;
    }




    public void setCompressParams(Bitmap.CompressFormat compressFormat, int quality) {
        mCompressFormat = compressFormat;
        mCompressQuality = quality;
    }

    private boolean writeBItmapToFile(Bitmap bitmap, String file) throws IOException {
        OutputStream out = null;

        try {
            out = new BufferedOutputStream(new FileOutputStream(file), Utils.IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }


    }


    public Bitmap get(String key) {
        synchronized (mLinkedHashMap) {
            final String file = mLinkedHashMap.get(key);
            if (file != null) {
                Log.i("kcc", "disk cache hit");
                return BitmapFactory.decodeFile(file);
            } else {
                final String existingFile = createFilePath(mCacheDir, key);
                if (existingFile != null && new File(existingFile).exists()) {
                    put(key, existingFile);
                    Log.d("kcc", "Disk cache hit (existing file)");
                    return BitmapFactory.decodeFile(existingFile);
                }
            }
        }
        return null;
    }

    public boolean containKey(String key) {
        if (mLinkedHashMap.containsKey(key)) {
            return true;
        }

        final String existingFile = createFilePath(mCacheDir, key);
        if (existingFile != null && new File(existingFile).exists()) {
            put(key, existingFile);
            return true;
        }
        return false;
    }

    public void clearCache() {
        DiskLruCache.clearCache(mCacheDir);
    }

    private static void clearCache(File mCacheDir) {
        final File[] files = mCacheDir.listFiles(cacheFileFilter);
        for (int i=0; i<files.length; i++) {
            files[i].delete();
        }
    }

    /**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @param context
     *          The context to use
     * @param uniqueName
     *          A unique directory name to append to the cache dir
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {

        final String cachePath =
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                || !Utils.isExternalStorageRemovable() ?
                        Utils.getExternalCacheDir(context).getPath() : context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }
}
