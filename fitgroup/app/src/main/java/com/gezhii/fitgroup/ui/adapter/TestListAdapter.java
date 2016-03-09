package com.gezhii.fitgroup.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gezhii.fitgroup.MyApplication;
import com.gezhii.fitgroup.R;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by y on 2015/10/21.
 */
public class TestListAdapter extends BaseListAdapter<RecyclerView.ViewHolder> {
    private List<String> users;

    public TestListAdapter(List<String> users)
    {
        this.users = users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_list_item, null);
        TestViewHolder testViewHolder = new TestViewHolder(rootView, getOnListItemClickListener());
        return testViewHolder;
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        TestViewHolder testViewHolder = (TestViewHolder)holder;
        testViewHolder.testBtn.setText(users.get(position));
        testViewHolder.testTxt.setText(users.get(position));

        testViewHolder.testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MyApplication.getApplication(),"item " + position + "is clicked", Toast.LENGTH_SHORT).show();

            }
        });
    } //绑数据用的，根据数据的不同状态，控制这个ViewHolder的显示方式


    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'test_view_holder.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class TestViewHolder extends BaseViewHolder{
        @InjectView(R.id.test_btn)
        Button testBtn;
        @InjectView(R.id.test_txt)
        TextView testTxt;

        TestViewHolder(View view, OnListItemClickListener onListItemClickListener) {
            super(view, onListItemClickListener);
        }
    }
}
