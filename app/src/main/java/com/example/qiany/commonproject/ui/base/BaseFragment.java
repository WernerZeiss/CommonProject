package com.example.qiany.commonproject.ui.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.qiany.commonproject.R;
import com.example.qiany.commonproject.utils.DialogHelper;
import com.example.qiany.commonproject.utils.ToastUtils;
import com.example.qiany.commonproject.widget.MultiStateView;
import com.trello.rxlifecycle2.LifecycleTransformer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author caiwenqing
 * @data 2018/5/11
 * description:
 */
public abstract class BaseFragment<T1 extends BaseContract.Presenter> extends MySupportFragment implements IBase<T1>, BaseContract.View {
    private static final String TAG = "BaseFragment测试";

    protected View mRootView;
    protected Dialog mLoadingDialog = null;

    @Nullable
    @BindView(R.id.MultiStateView)
    MultiStateView multiStateView;
    Unbinder unbinder;

    @Nullable
    protected T1 mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                parent.removeView(mRootView);
            }
        } else {
            mRootView = createView(inflater, container, savedInstanceState);
        }
        mLoadingDialog = DialogHelper.getLoadingDialog(getContext());
        return mRootView;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getContentLayout(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPresenter();
        initMultiStatusView();
        initView(view, savedInstanceState);
    }

    private void initMultiStatusView() {
        if (multiStateView != null) {
            multiStateView.addStateView(MultiStateView.STATE_EMPTY, R.layout.item_state_empty);
            multiStateView.addStateView(MultiStateView.STATE_NONET, R.layout.item_state_nonet);
            multiStateView.addStateView(MultiStateView.STATE_FAIL, R.layout.item_state_fali);
            multiStateView.setOnClickRetryListener(new MultiStateView.onClickRetryListener() {
                @Override
                public void onClickRetry(int status) {
                    /**
                     * 重试，
                     * state的值：MultiStateView.STATE_NONET（无网络）、MultiStateView.STATE_FAIL（加载失败）
                     */
                    onRetry(status);
                }
            });
        }
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initData();
    }

    private void initPresenter() {
        if (null != createPresenter()) {
            mPresenter = createPresenter();
            mPresenter.attachView(this);
        } else {
            Log.i(TAG, "createPresenter()方法返回为null");
        }
    }

    @Nullable
    @Override
    public android.view.View getView() {
        return mRootView;
    }

    @Override
    public void showLoading(String msg) {
        if (mLoadingDialog != null) {
            if (!TextUtils.isEmpty(msg)) {
                TextView tv = (TextView) mLoadingDialog.findViewById(R.id.tv_load_dialog);
                tv.setText(msg);
            }
            mLoadingDialog.show();
        }
    }

    @Override
    public void hideLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void toastMsg(String msg) {
        ToastUtils.ToastCenter(msg);
    }

    @Override
    public void onFail() {
        if (multiStateView != null) {
            multiStateView.setViewStatus(MultiStateView.STATE_FAIL);
        }
    }

    @Override
    public void onNoNet() {
        if (multiStateView != null) {
            multiStateView.setViewStatus(MultiStateView.STATE_NONET);
        }
    }

    @Override
    public void onEmpty() {
        if (multiStateView != null) {
            multiStateView.setViewStatus(MultiStateView.STATE_EMPTY);
        }
    }

    @Override
    public void onSuccess() {
        if (multiStateView != null) {
            multiStateView.setShow(false);
        }
    }

    @Override
    public <T1> LifecycleTransformer<T1> bindToLife() {
        return bindToLifecycle();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}
