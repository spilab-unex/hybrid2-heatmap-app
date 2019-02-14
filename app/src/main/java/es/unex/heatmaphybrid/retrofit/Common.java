package es.unex.heatmaphybrid.retrofit;

public class Common {

    private static String baseUrl="https://fcm.googleapis.com";

    private static String serverUrl="https://2dvuxld7v9.execute-api.us-east-1.amazonaws.com";

    public static APIService getFCM(){
        return RetrofitClient.getClient(baseUrl).create(APIService.class);
    }

    public static APIService getServer(){
        return RetrofitClient.getClient(serverUrl).create(APIService.class);
    }
}
