package com.inuker.bluetooth;

public class Constants {
	/** 下拉刷新列表的各个状态 */
    public static final int LIST = 0;
    public static final int EMPTY = 1;
    public static final int ERROR = 2;
    public static final int LOADING = 3;
    public static final int ALLOW_PULL_IN_EMPTY_PAGE = 4; // 没有内容，但是允许下拉刷新
    
    public static final int FROM_TEST_SERVER = 0;
    public static final int FROM_ONLINE_SERVER = 1;
    
    public static final int PAGE_SIZE = 10;
    
    public static final String ARTISAN_STATUS_TEST = "0";
    public static final String ARTISAN_STATUS_ONLINE = "1";
    
    public static final String ARTICLE_ACTION_ONLINE = "com.dingjikerbo.article.online";
    public static final String ARTICLE_ACTION_UPDATE = "com.dingjikerbo.article.update";
}
