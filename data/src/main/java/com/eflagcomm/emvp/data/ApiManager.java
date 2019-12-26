package com.eflagcomm.emvp.data;

/**
 * Api的管理类，负责创建api接口对象
 */
public class ApiManager {

    private HttpProvider mHttpProvider;

    private ApiManager() {
        mHttpProvider = HttpProvider.getProvider();
    }

    public static ApiManager getManager() {
        return SingleHolder.INSTANCE;
    }

    private final static class SingleHolder {
        private final static ApiManager INSTANCE = new ApiManager();
    }

    /**
     * 获得api接口对象
     *
     * @param service
     * @param <T>
     * @return
     */
    public <T> T createApi(Class<T> service) {
        return mHttpProvider.createApi(service);
    }
}
