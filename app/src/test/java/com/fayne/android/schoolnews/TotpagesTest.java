package com.fayne.android.schoolnews;

import com.fayne.android.schoolnews.biz.NewsItemBiz;

import org.junit.Test;

/**
 * Created by fan on 2017/11/15.
 */

public class TotpagesTest {

    @Test
    public void totPages() {
        System.out.println(NewsItemBiz.getNewsTotal(0));
    }
}
