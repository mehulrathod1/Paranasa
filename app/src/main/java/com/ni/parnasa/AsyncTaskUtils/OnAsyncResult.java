package com.ni.parnasa.AsyncTaskUtils;

public interface OnAsyncResult {
    void OnSuccess(String result);

    void OnFailure(String result);
}