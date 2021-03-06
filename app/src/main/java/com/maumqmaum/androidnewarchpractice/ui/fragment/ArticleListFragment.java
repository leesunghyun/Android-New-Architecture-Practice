package com.maumqmaum.androidnewarchpractice.ui.fragment;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.maumqmaum.androidnewarchpractice.R;
import com.maumqmaum.androidnewarchpractice.model.Article;
import com.maumqmaum.androidnewarchpractice.ui.adapter.ArticleListAdapter;
import com.maumqmaum.androidnewarchpractice.viewmodel.ArticleListViewModel;

import java.util.List;

public class ArticleListFragment extends BaseFragment implements LifecycleRegistryOwner {

    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    private ArticleListAdapter adapter;
    private RecyclerView articleList;
    private ArticleListViewModel viewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        articleList = (RecyclerView) view.findViewById(R.id.article_list);
        Button deleteButton = (Button) view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MutableLiveData<Article>> list = viewModel.getArticleList().getValue();
                if (list != null && !list.isEmpty()) {
                    list.remove(0).removeObservers(ArticleListFragment.this);
                    viewModel.getArticleList().postValue(list);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initArticleList();
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }

    private void initArticleList() {
        initViewModel();
        adapter = new ArticleListAdapter(this, navigationController);
        viewModel.getArticleList().observe(this, new Observer<List<MutableLiveData<Article>>>() {
            @Override
            public void onChanged(@Nullable List<MutableLiveData<Article>> list) {
                // TODO : Use DiffUtil
                Log.d("ArticleListFragment", "onChanged > " + list);
                adapter.setArticleList(list);
            }
        });
        articleList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        articleList.setAdapter(adapter);
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(getActivity()).get(ArticleListViewModel.class);
    }
}