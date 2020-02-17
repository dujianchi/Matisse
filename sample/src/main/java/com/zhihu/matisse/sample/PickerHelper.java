package com.zhihu.matisse.sample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.internal.utils.PathUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import top.zibin.luban.InputStreamAdapter;
import top.zibin.luban.Luban;

/**
 * 图片选择工具类
 */
public class PickerHelper {

    public static interface OnCompressedCallback {
        void onCompressed(List<File> result);
    }

    private PickerHelper() {
    }


    private static void appendUri(Context context, Luban.Builder builder, Uri uri) {
        try {
            builder.load(new UriStreamProvider(context, uri));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return builder;
    }

    /**
     * 获取返回结果，未压缩版本
     *
     * @deprecated 此版本是未压缩版本，最好不要使用
     */
    @Deprecated
    private static List<Uri> getResult(Intent data) {
        return Matisse.obtainResult(data);
    }

    /**
     * 获取返回结果，带压缩版本
     */
    public static void getResult(Context context, Intent data, @NonNull OnCompressedCallback callback) {
        List<Uri> paths = getResult(data);
        //LogUtil.d2(paths);
        if (paths != null) {
            //Loading.show(context, "图片压缩中……");
            Uri[] strings = new Uri[paths.size()];
            new AsyncTask<Uri, Void, List<File>>() {
                @Override
                protected List<File> doInBackground(Uri... lists) {
                    if (lists != null && lists.length > 0) {
                        Luban.Builder builder = Luban.with(context);
                        for (Uri uri : lists) {
                            appendUri(context, builder, uri);
                        }
                        try {
                            return builder.get();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return Collections.emptyList();
                        }
                    }
                    return Collections.emptyList();
                }

                @Override
                protected void onPostExecute(List<File> strings) {
                    //Loading.hide(context);
                    callback.onCompressed(strings);
                }
            }.execute(paths.toArray(strings));
        }
    }

    private static class UriStreamProvider extends InputStreamAdapter {

        private Context mContext;
        private Uri mUri;

        public UriStreamProvider(Context context, Uri uri) {
            mContext = context;
            mUri = uri;
        }

        @Override
        public InputStream openInternal() throws IOException {
            ParcelFileDescriptor pfd = mContext.getContentResolver().openFileDescriptor(mUri, "r");
            return new FileInputStream(pfd.getFileDescriptor());
        }

        @Override
        public Uri outputUri(File outFile) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return FileProvider.getUriForFile(mContext, "com.zhihu.matisse.sample.fileprovider", outFile);
            }
            return super.outputUri(outFile);
        }

        @Override
        public String getPath() {
            return PathUtils.getPath(mContext, mUri);
        }
    }

}
