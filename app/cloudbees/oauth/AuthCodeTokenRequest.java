package cloudbees.oauth;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Vivek Pandey
 */
public class AuthCodeTokenRequest extends TokenRequest{

    private String authorizationCode;

    public AuthCodeTokenRequest() {
    }

    public AuthCodeTokenRequest(String code, String redirectUri) {
        super(null, redirectUri, GrantType.AUTHORIZATION_CODE);
        this.authorizationCode = code;
    }

    @JsonProperty("code")
    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public String toFormEncoded(){

        String data = "code="+getAuthorizationCode()+"&grant_type="+getGrantType().toString();

        if(getRedirectUri() != null){

            data += "&redirection_uri=";

            try {
                data += URLEncoder.encode(getRedirectUri(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                data += URLEncoder.encode(getRedirectUri());
            }

        }
        if(getScope() != null){
            data += "&scope="+getScope();
        }
        return data;
    }
}
