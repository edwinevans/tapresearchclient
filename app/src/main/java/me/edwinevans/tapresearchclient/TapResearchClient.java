package me.edwinevans.tapresearchclient;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

// if mock version
// result={"has_offer":true,"offer_reason":0,"reason_comment":"N\/A","offer_url":"https:\/\/www.tapresearch.com\/router\/offers\/961a6174f17a7a4f1290504390b2e3fd\/pre_entry?sdk=false&uid=codetest123&did=d93ffa86-a970-4b06-8cbe-f6de3d87b406&api_tid=061619663550bfb2f22fcb7c321f3ac6","message_hash":{"min":"33","max":"3250","currency":"coins"},"abandon_url":"https:\/\/www.tapresearch.com\/router\/offers\/961a6174f17a7a4f1290504390b2e3fd?tid=061619663550bfb2f22fcb7c321f3ac6&uid=codetest123&did=d93ffa86-a970-4b06-8cbe-f6de3d87b406"

public class TapResearchClient {
    private static final String BASE_URL = "https://www.tapresearch.com/supply_api";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void getOffers(RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.post(getAbsoluteUrl("surveys/offer"), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
