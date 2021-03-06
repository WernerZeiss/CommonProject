package com.example.qiany.commonproject.ui.base;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * @author caiwenqing
 * @data 2018/5/10
 * description:
 */
public interface BaseContract {

    interface Presenter<T extends View>{

        void attachView(T view);

        void detachView();
    }


    interface View {

        void showLoading(String msg);

        void hideLoading();

        void toastMsg(String msg);

        void onFail();

        void onNoNet();

        void onEmpty();

        void onSuccess();

        //加载失败或无网络时重试
        void onRetry(int status);

        /**
         * 绑定生命周期
         * @param <T1>
         * @return
         */
        <T1> LifecycleTransformer<T1> bindToLife();
    }
}
