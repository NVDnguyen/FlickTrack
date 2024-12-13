package com.example.myapplication.domain.usecase.base;

import androidx.annotation.Nullable;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import kotlinx.coroutines.CoroutineScope;

public abstract class FlowableUseCase<T> extends UseCase {

    protected abstract Flowable<T> buildUseCaseFlowable(@Nullable CoroutineScope viewmodelScope);

    public void execute(
            final SuccessCallback<T> onSuccess,
            final ErrorCallback onError,
            final Action onFinished,
            final CoroutineScope viewmodelScope) {
        disposeLast();

        // Build the Flowable và subscribe
        lastDisposable = buildUseCaseFlowable(viewmodelScope)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(onFinished)
                .subscribe(onSuccess::onSuccess, onError::onError);

        compositeDisposable.add(lastDisposable);
    }

    // Callback interface cho thành công
    public interface SuccessCallback<T> {
        void onSuccess(T result);
    }

    // Callback interface cho lỗi
    public interface ErrorCallback {
        void onError(Throwable throwable);
    }
}

