package com.wos.play.rootdir.model_universal.jsonBeanArray;

/**
 * Created by user on 2016/11/17.
 */

public class Box<T>{
    private T data;
    public Box() {

    }
    public Box(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}

