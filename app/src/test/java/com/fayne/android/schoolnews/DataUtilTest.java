package com.fayne.android.schoolnews;

import com.fayne.android.schoolnews.bean.CommonException;
import com.fayne.android.schoolnews.util.DataUtil;

import org.junit.Test;

/**
 * Created by fan on 2017/11/14.
 */

public class DataUtilTest {

    @Test
    public  void main() {
        try {
            String ans = DataUtil.doGet("http://www.csdn.net/article/2017-10-27/2826771");
            System.out.println(ans);
        } catch (CommonException e) {
            e.printStackTrace();
        }
    }
}
