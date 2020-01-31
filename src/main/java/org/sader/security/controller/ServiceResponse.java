package org.sader.security.controller;


import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ServiceResponse<T> {
    private int status;
    private T data;
    private LocalDateTime localDateTime;

    public ServiceResponse(HttpStatus status, T data){
        setStatus(status.value());
        setData(data);
        localDateTime = LocalDateTime.now();
    }
    public ServiceResponse(T data){
        setData(data);
        setStatus(HttpStatus.ACCEPTED.value());
        localDateTime = LocalDateTime.now();
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
