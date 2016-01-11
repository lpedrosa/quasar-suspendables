package com.github.lpedrosa;

public interface Callback {

    void onSuccess(String value);

    void onError(ClientException ex);

}
