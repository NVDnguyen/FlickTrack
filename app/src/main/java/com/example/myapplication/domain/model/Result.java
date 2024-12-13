package com.example.myapplication.domain.model;

public class Result<T> {
    public enum Status { SUCCESS, ERROR }
    public Status status = Status.ERROR;
    public final T data;
    public String message ="";

    private Result(Status status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(Status.SUCCESS, data, "SUCCESS");
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(Status.ERROR, null, message);
    }


}
