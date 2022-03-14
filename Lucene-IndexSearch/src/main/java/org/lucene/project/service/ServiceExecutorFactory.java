package org.lucene.project.service;

public class ServiceExecutorFactory {

    private static volatile ServiceExecutorFactory singletonInstance;
    private static final String INDEX_COMMAND = "INDEX";
    private static final String QUERY_COMMAND = "QUERY";
    private static final String NOT_SUPPORTED_COMMAND = "NOT_SUPPORTED_COMMAND";

    public static ServiceExecutorFactory getInstance() {
        if (singletonInstance == null)
            synchronized (ServiceExecutorFactory.class) {
                singletonInstance = new ServiceExecutorFactory();
            }
        return singletonInstance;
    }

    public IServiceExecutor getService(String commandToBeExecuted){
        IServiceExecutor serviceExecutor = null;
        switch (commandToBeExecuted) {
            case INDEX_COMMAND:
                serviceExecutor = new IndexServiceExecutor();
                break;
            case QUERY_COMMAND:
                serviceExecutor = new QueryServiceExecutor();
                break;
            case NOT_SUPPORTED_COMMAND:
                serviceExecutor = new NotSupportedServiceExecutor();
                break;
        }

        return serviceExecutor;
    }

}
