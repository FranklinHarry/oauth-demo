package cloudbees.oauth;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Vivek Pandey
 */
public class TokenRequest{
    private  GrantType grantType;
    private  String scope;
    private  String redirectUri;

    public TokenRequest(String scope, String redirectUri, GrantType grantType) {
        this.scope = scope;
        this.redirectUri = redirectUri;
        this.grantType = grantType;
    }

    public TokenRequest() {
    }

    @JsonProperty("grant_type")
    public GrantType getGrantType() {
        return grantType;
    }

    @JsonProperty("scope")
    public String getScope() {
        return scope;
    }

    @JsonProperty("redirect_uri")
    public String getRedirectUri() {
        return redirectUri;
    }

    enum GrantType{
        AUTHORIZATION_CODE("authorization_code"), CLIENT_CREDENTIALS("client_credentials"), PASSWORD("password");

        private final String grantType;
        private GrantType(String grantType) {
            this.grantType = grantType;
        }

        public static GrantType get(String grantType){
            if(grantType.equals(AUTHORIZATION_CODE)){
                return AUTHORIZATION_CODE;
            }
            if(grantType.equals(CLIENT_CREDENTIALS)){
                return CLIENT_CREDENTIALS;
            }

            if(grantType.equals(PASSWORD)){
                return PASSWORD;
            }

            return null;
        }

        public String toString(){
            return grantType;
        }
    }
}

