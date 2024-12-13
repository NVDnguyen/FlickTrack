package com.example.myapplication.domain.usecase.base;

import kotlinx.coroutines.flow.Flow;

public abstract class FlowUseCase<T> extends UseCase {
    protected abstract Flow<T> buildUseCaseFlow();

    public Flow<T> execute() {
        return buildUseCaseFlow();
    }
}
