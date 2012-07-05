package cloudbees.oauth;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * @author Vivek Pandey
 */
public class TokenResponse {
    private String accessToken;
    private String tokenType;
    private String expiresIn;
    private String uid;
    private String scope;
    private List<String> accounts;

    private String error;
    private String errorDescription;

    public TokenResponse() {
    }

    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty("token_type")
    public String getTokenType() {
        return tokenType;
    }

    @JsonProperty("expires_in")
    public String getExpiresIn() {
        return expiresIn;
    }

    @JsonProperty("scope")
    public String getScope() {
        return scope;
    }

    @JsonProperty("uid")
    public String getUid() {
        return uid;
    }


    @JsonProperty("accounts")
    public List<String> getAccounts() {
        return accounts;
    }

    @JsonProperty("error")
    public String getError() {
        return error;
    }

    @JsonProperty("error_description")
    public String getErrorDescription() {
        return errorDescription;
    }
}
