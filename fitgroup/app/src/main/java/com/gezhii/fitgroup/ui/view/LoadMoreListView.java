package com.gezhii.fitgroup.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.network.OnRequestEnd;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.gezhii.fitgroup.tools.Screen;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xianrui on 15/10/30.
 */
public class LoadMoreListView extends RecyclerView {

    public static final String TAG = LoadMoreListView.class.getSimpleName();

    LayoutManager mLayoutManager;
    OnScrollListener mOnScrollListener;
    OnLoadMoreListener mOnLoadMoreListener;
    APIAutoInvoker apiAutoInvoker;
    boolean isLoadingMore;
    boolean isHasMore = true;
    LoadMoreListViewAdapter mLoadMoreListViewAdapter;

    public void setApiAutoInvoker(String api_name, Object[] p, final Class<?> dto)
    {
        apiAutoInvoker = new APIAutoInvoker();
        apiAutoInvoker.setAPI(api_name, p, dto);
    }

    public LoadMoreListView(Context context) {
        this(context, null);
    }

    public LoadMoreListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mOnScrollListener = new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isHasMore) {
                    int lastVisibleItem = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
                    int totalItemCount = mLayoutManager.getItemCount();
                    //lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载，各位自由选择
                    // dy>0 表示向下滑动
                    Log.i("xianrui", "lastVisibleItem " + lastVisibleItem + " totalItemCount " + totalItemCount + " dy = " + dy);
                    if (lastVisibleItem >= totalItemCount - 4 && dy >= 0) {
                        if (isLoadingMore) {
                            Log.d(TAG, "ignore manually update!");
                        } else {
                            if(apiAutoInvoker != null)
                            {
                                isLoadingMore = true;
                                apiAutoInvoker.execute();
                            }
                            else if (mOnLoadMoreListener != null) {
                                mOnLoadMoreListener.onLoadMore(LoadMoreListView.this, getAdapter());
                            }

//                            if (mLoadMoreListViewAdapter != null) {
//                                mLoadMoreListViewAdapter.setIsHasMore(isHasMore);
//                            }

                        }
                    }
                }
            }
        };
        this.addOnScrollListener(mOnScrollListener);
    }

    public boolean isHasMore() {
        return isHasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.isHasMore = hasMore;
    }

    public void beginLoadingData()
    {
        this.isLoadingMore = true;
    }

    public void endLoadingData()
    {
        this.isLoadingMore = false;
    }


    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        mLayoutManager = layout;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }


    public void setLoadMoreListViewAdapter(LoadMoreListViewAdapter adapter) {
        mLoadMoreListViewAdapter = adapter;
        super.setAdapter(mLoadMoreListViewAdapter);
    }


    public interface OnLoadMoreListener {
        void onLoadMore(LoadMoreListView loadMoreListView, RecyclerView.Adapter adapter);
    }


    class APIAutoInvoker
    {
        String api_name;
        Object[] params;
        Class<?> dto;

        Map<String, Method> apiMethodsMap = new HashMap<String, Method>();

        APIAutoInvoker()
        {
            Method[] methods = API.class.getMethods();
            for(int i = 0; i < methods.length; i++)
            {
                apiMethodsMap.put(methods[i].getName(), methods[i]);
            }
        }

        public void setAPI(String api_name, Object[] p, final Class<?> d)
        {
            this.api_name = api_name;
            this.dto = d;

            this.params = new Object[p.length + 1];
            System.arraycopy(p,0,this.params,0,p.length);

            params[0] = (Integer)params[0] + 1;
            params[this.params.length - 1] = new OnRequestEnd() {
                @Override
                public void onRequestSuccess(String response) {
                    isLoadingMore = false;
                    Method parseJsonMethod = getMethod("parserJson", dto);
                    try {
                        Object result = parseJsonMethod.invoke(null, response);
                        Field f = result.getClass().getField("data_list");
                        List<Object> temp_data_list = (List<Object>)f.get(result);

                        if(temp_data_list.size() == Config.loadPageCount)
                        {
                            isHasMore = true;
                        }
                        else
                        {
                            isHasMore = false;
                        }

                        List<Object> all_data_list = mLoadMoreListViewAdapter.getTotal_data_list();
                        all_data_list.addAll(temp_data_list);

                        params[0] = (Integer)params[0] + 1;
                        mLoadMoreListViewAdapter.setIsHasMore(isHasMore);
                        mLoadMoreListViewAdapter.setTotal_data_list(all_data_list);
                        mLoadMoreListViewAdapter.notifyDataSetChanged();

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onRequestFail(VolleyError error) {
                    isLoadingMore = false;
                }
            };
        }

        private Method getMethod(String method_name, Class<?> clazz)
        {
            Method[] methods = clazz.getMethods();
            Method m = null;
            for(int i = 0; i < methods.length; i++)
            {
                if(methods[i].getName().equals(method_name))
                {
                    m = methods[i];
                    break;
                }
            }
            return m;
        }

        private Method getAPIMethod()
        {
            return apiMethodsMap.get(api_name);
        }

        public void execute()
        {
            try {
                Method m = getAPIMethod();
                m.invoke(null, params);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}
