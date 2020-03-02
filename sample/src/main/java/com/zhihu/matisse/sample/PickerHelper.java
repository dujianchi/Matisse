package com.zhihu.matisse.sample;

import java.io.File;
import java.util.List;

/**
 * 图片选择工具类
 */
public class PickerHelper {

    public static interface OnCompressedCallback {
        void onCompressed(List<File> result);
    }

    private PickerHelper() {
    }


}
