package com.fayne.android.schoolnews.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fayne.android.schoolnews.R;
import com.fayne.android.schoolnews.activity.NewsInfoActivity;
import com.fayne.android.schoolnews.adapter.NewsAdapter;
import com.fayne.android.schoolnews.adapter.OnItemClickListener;
import com.fayne.android.schoolnews.bean.CommonException;
import com.fayne.android.schoolnews.bean.NewsItem;
import com.fayne.android.schoolnews.biz.NewsItemBiz;
import com.fayne.android.schoolnews.db.NewsItemDao;
import com.fayne.android.schoolnews.util.Constant;
import com.fayne.android.schoolnews.util.NetUtil;

import java.util.List;

/**
 * Created by fan on 2017/11/15.
 */

public class MainFragment extends Fragment {

    public static final int LOAD_REFRESH = 0x01;
    public static final int LOAD_MORE = 0x02;
    public static final String TIP_ERROR_NO_NETWORK = "没有网络连接";
    public static final String TIP_ERROR_NO_SERVICE = "服务器错误";

    public static final String NEWS_TYPE = "NEWS_TYPE";

    private Context mContext;
    //默认新闻类型
    private int mNewsType = Constant.NEWS_TYPE_AKXW;
    //当前页面
    private int mCurPage = 1;
    //业务处理类
    private NewsItemBiz mNewsItemBiz;

    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mManager;
    private NewsAdapter mAdapter;

    //是否从服务器上下载数据
    private boolean isLoadFromService;
    //与数据库交互
    private NewsItemDao mNewsItemDao;

    public static MainFragment newInstance(int pos) {
        Bundle args = new Bundle();
        args.putInt(NEWS_TYPE, pos);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        mNewsItemDao = new NewsItemDao(mContext);
        mNewsItemBiz = new NewsItemBiz();
        initView();
        mSwipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefresh.setRefreshing(true);
            }
        });

        initData();
        initEvent();
        new DownloadTask().execute(LOAD_REFRESH);
    }

    class DownloadTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... integers) {
            switch (integers[0]) {
                case LOAD_REFRESH:
                    return refreshData();
                case LOAD_MORE:
                    return loadMoreData();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (null == s) {
                mAdapter.setLoading(false);
                mAdapter.setError(null);
            } else {
                mAdapter.setError(s);
                mAdapter.setLoading(true);
                Snackbar.make(mSwipeRefresh, s, Snackbar.LENGTH_LONG).show();
            }
            mSwipeRefresh.setRefreshing(false);
            mAdapter.notifyDataSetChanged();
        }
    }

    private String loadMoreData() {
        mAdapter.setLoading(true);
        if (isLoadFromService) {
            mCurPage++;
            try {
                List<NewsItem> items = mNewsItemBiz.getNewsItems(mNewsType, mCurPage);
                mAdapter.addDatas(items);
                mNewsItemDao.addNewsItems(items);
            } catch (CommonException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        } else {
            mCurPage++;
            List<NewsItem> items = mNewsItemDao.getNewsItems(mCurPage, mNewsType);
            mAdapter.addDatas(items);
            return TIP_ERROR_NO_NETWORK;
        }
        return null;
    }

    private String refreshData() {
        if (NetUtil.isOnline(mContext)) {
            mCurPage = 1;
            try {
                List<NewsItem> items = mNewsItemBiz.getNewsItems(mNewsType, mCurPage);
                if (!items.isEmpty()) {
                    mAdapter.setDatas(items);
                    mNewsItemDao.refreshData(mNewsType, items);
                }
                isLoadFromService = true;
            } catch (CommonException e) {
                e.printStackTrace();
                isLoadFromService = false;
                return TIP_ERROR_NO_SERVICE;
            }
        } else {
            List<NewsItem> items = mNewsItemDao.getNewsItems(mCurPage, mNewsType);
            if (!items.isEmpty()) {
                mAdapter.setDatas(items);
                isLoadFromService = false;
            }
            return TIP_ERROR_NO_NETWORK;
        }
        return null;
    }

    private void initEvent() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new DownloadTask().execute(LOAD_REFRESH);
            }
        });

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int last = mManager.findLastVisibleItemPosition();
                if (newState == recyclerView.SCROLL_STATE_IDLE && last + 1 == mAdapter.getItemCount() && mAdapter.getItemCount() > 1) {
                    new DownloadTask().execute(LOAD_MORE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NewsItem item = mAdapter.getDatas().get(position);
                NewsInfoActivity.actionStart(mContext, item.getLink());
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void initData() {
        Bundle bundle = getArguments();
        mNewsType = bundle.getInt(NEWS_TYPE, Constant.NEWS_TYPE_AKXW);
        mAdapter = new NewsAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initView() {
        mSwipeRefresh = getView().findViewById(R.id.id_swiperefresh);
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary,
                R.color.colorPrimary, R.color.colorPrimary);
        mRecyclerView = getView().findViewById(R.id.id_recycleview);
        mManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
