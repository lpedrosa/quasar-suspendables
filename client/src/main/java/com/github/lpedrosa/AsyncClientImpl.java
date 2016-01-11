package com.github.lpedrosa;

public class AsyncClientImpl implements AsyncClient {

    @Override
    public void getValue(int argument, Callback callback) {
        if(argument == 42)
            callback.onError(new ClientException("42 is not the answer for everything..."));
        else
            callback.onSuccess(String.valueOf(argument));
    }

}
