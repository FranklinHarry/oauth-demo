package controllers;

import cloudbees.Utils;
import cloudbees.oauth.AuthCodeTokenRequest;
import cloudbees.oauth.AuthorizationResponse;
import cloudbees.oauth.TokenResponse;
import play.*;
import play.cache.Cache;
import play.libs.Codec;
import play.libs.WS;
import play.mvc.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
/**
 * @author Vivek Pandey
 **/
public class Application extends Controller {
    private static final String GC_ENDPOINT =(String) Play.configuration.get("gc.endpoint");
    private static final String CLIENT_ID=(String) Play.configuration.get("client_id");
    private static final String CLIENT_SECRET=(String) Play.configuration.get("client_secret");

    public static void index() {
        render();
    }

    public static void show(){
        String token = (String) Cache.get("token");
        if(session.get("uid") == null || token == null){
            redirectToOauth(CLIENT_ID,"user");
            return;
        }

        try {
            Map userInfo = getUserInfo(token,session.get("uid"));
            render(userInfo);
        } catch (IOException e) {
            error(e.getMessage());
        }
    }

    /**
     * Get token information
     * @param token access token
     * @return null if error TokenResponse otherwise
     */
    private static TokenResponse getTokenInfo(String token){
        WS.WSRequest req = WS.url(GC_ENDPOINT +"/oauth/tokens/" + token);
        req.authenticate(CLIENT_ID, CLIENT_SECRET);
        WS.HttpResponse resp = req.get();
        if(resp.getStatus() != 200){
            error(resp.getStatus()+":"+resp.getString());
            return null;
        }

        TokenResponse tokenResponse;
        try {
            tokenResponse = Utils.om.readValue(resp.getStream(), TokenResponse.class);
        } catch (IOException e) {
            error("Error: "+":"+e.getMessage());
            return null;
        }
        if(tokenResponse.getError() != null || resp.getStatus() != 200){
            error(tokenResponse.getError()+":"+tokenResponse.getErrorDescription());
            return null;
        }
        return tokenResponse;
    }

    /**
     * Get token given temporary authorization token
     */
    private static TokenResponse getTokenFromAuthCode(AuthCodeTokenRequest tokenReq){
        WS.WSRequest req = WS.url(GC_ENDPOINT +"/oauth/token").mimeType("application/x-www-form-urlencoded");
        req.authenticate(CLIENT_ID, CLIENT_SECRET);

        try {
            req.body(tokenReq.toFormEncoded());
            WS.HttpResponse resp = req.post();
            TokenResponse tokenResponse = Utils.om.readValue(resp.getStream(), TokenResponse.class);
            if(tokenResponse.getError() != null){
                error(tokenResponse.getError()+":"+tokenResponse.getErrorDescription());
                return null;
            }
            return tokenResponse;
        } catch (IOException e) {
            error("Error converting token request to JSON: "+e.getMessage());
            return null;
        }
    }



    /**
     *
     * @param clientId client_id obtained when you registed app with cloudbees
     * @param scopes space separated scope names. e.g. "user deploy_app"
     */
    private static void redirectToOauth(String clientId, String scopes){
        String url = String.format(GC_ENDPOINT +"/oauth/authorize?client_id=%s&response_type=code",clientId);
        if(scopes != null){
            try {
                url += "&scope="+ URLEncoder.encode(scopes,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                url += "&scope="+ URLEncoder.encode(scopes); //lets try with platform encoding!!!
            }
        }
        redirect(url);
    }

    private static Map getUserInfo(String token, String uid) throws IOException {
        //Call users API and get the user info, this example sends the token in the Authorization header with scheme derived after tokenType.
        // Alternate way to send access_token for authentication is to send access_token as query param or as access_token in body
        // for post/put/patch
        WS.WSRequest userReq = WS.url(GC_ENDPOINT +"/api/v2/users/" + uid);
        userReq.setHeader("Authorization", "Bearer" + " " + Codec.encodeBASE64(token));

        WS.HttpResponse userResp = userReq.get();
        if(userResp.getStatus() != 200){
            error(userResp.getStatus()+":"+userResp.getString());
            return null;
        }

        return Utils.om.readValue(userResp.getStream(), Map.class);

    }

    /**
     * Gets called after user is authenticated and grants scopes.
     */
    public static void callback(){
        if(params.get("error") != null){
            error(params.get("error") + ":" + params.get("error_description"));
            return;
        }

        session.clear();

        AuthorizationResponse authResp = new AuthorizationResponse(params);



        WS.WSRequest req = WS.url(GC_ENDPOINT +"/oauth/token").mimeType("application/x-www-form-urlencoded");
        req.authenticate(CLIENT_ID, CLIENT_SECRET);


        try {
            TokenResponse tokenResponse = getTokenFromAuthCode(new AuthCodeTokenRequest(authResp.getCode(), null));
            session.put("uid", tokenResponse.getUid());

            Logger.info("Token Response: "+Utils.om.writeValueAsString(tokenResponse));

            Cache.set("token", tokenResponse.getAccessToken());
            redirect("/show");
        } catch (IOException e) {
            error("Error converting token request to JSON: "+e.getMessage());
        }
    }

}