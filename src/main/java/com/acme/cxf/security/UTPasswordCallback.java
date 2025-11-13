package com.acme.cxf.security;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import javax.security.auth.callback.*;
import java.io.IOException;
import java.util.Map;

public class UTPasswordCallback implements CallbackHandler {
    private final Map<String,String> users;

    public UTPasswordCallback(Map<String,String> users) {
        this.users = users;
    }

    @Override
    public void handle(Callback[] callbacks)
            throws IOException, UnsupportedCallbackException {

        for (Callback cb : callbacks) {
            if (cb instanceof WSPasswordCallback pc) {
                String password = users.get(pc.getIdentifier());
                if (password != null) {
                    pc.setPassword(password);
                }
            }
        }
    }
}
