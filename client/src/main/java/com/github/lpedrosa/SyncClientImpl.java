package com.github.lpedrosa;

public class SyncClientImpl implements SyncClient {

    @Override
    public String getValue(int argument) throws ClientException {
        if (argument == 42)
            throw new ClientException("42 is not the answer for everything...");
        return String.valueOf(argument);
    }

}
