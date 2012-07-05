package cloudbees.oauth;

import play.mvc.Scope;

/**
 * @author Vivek Pandey
 */
public class AuthorizationResponse{
    private final String code;
    private final String state;

    public AuthorizationResponse(Scope.Params params){
        this.code = (String) params.get("code");
        this.state = (String)params.get("state");
    }

    public String getCode() {
        return code;
    }

    public String getState() {
        return state;
    }
}

