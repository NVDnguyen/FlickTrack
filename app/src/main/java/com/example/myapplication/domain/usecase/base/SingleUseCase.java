package com.example.myapplication.domain.usecase.base;



import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * This abstract class is shared among several closely related UseCase classes
 * that classes extend this abstract class to use common methods & fields.
 */
public abstract class SingleUseCase<T> extends UseCase {

    protected abstract Single<T> buildUseCaseSingle();

    public void execute(
            final SuccessCallback<T> onSuccess,
            final ErrorCallback onError,
            final Action onFinished) {
        disposeLast();

        // Build the Single and subscribe
        lastDisposable = buildUseCaseSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(onFinished)
                .subscribe(onSuccess::onSuccess, onError::onError);

        compositeDisposable.add(lastDisposable);
    }

    // Define the callback interface for success
    public interface SuccessCallback<T> {
        void onSuccess(T result);
    }

    // Define the callback interface for error
    public interface ErrorCallback {
        void onError(Throwable throwable);
    }
}
