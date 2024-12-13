package com.example.myapplication.domain.usecase.base;


import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * This class is extended by UseCase classes to use common methods & fields.
 */
public abstract class UseCase {

    protected Disposable lastDisposable;
    protected final CompositeDisposable compositeDisposable = new CompositeDisposable();

    // Dispose the last Disposable if it is not already disposed
    public void disposeLast() {
        if (lastDisposable != null && !lastDisposable.isDisposed()) {
            lastDisposable.dispose();
        }
    }

    // Clear all disposables
    public void dispose() {
        compositeDisposable.clear();
    }
}

