package com.acme.cxf;

import com.acme.cxf.impl.HelloServiceImpl;
import com.acme.cxf.security.UTPasswordCallback;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngineFactory;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngine;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;

import java.util.HashMap;
import java.util.Map;

public class SecureServer {
    public static void main(String[] args) throws Exception {

        // --- 1. Créer le Bus CXF
        Bus bus = BusFactory.newInstance().createBus();
        BusFactory.setDefaultBus(bus);

        // --- 2. Activer Jetty manuellement
        JettyHTTPServerEngineFactory factoryEngine = new JettyHTTPServerEngineFactory(bus);

        JettyHTTPServerEngine engine = factoryEngine.createJettyHTTPServerEngine(
                8080,
                "http"
        );

        // --- 3. Config WS-Security
        Map<String, Object> inProps = new HashMap<>();
        inProps.put("action", "UsernameToken");
        inProps.put("passwordType", "PasswordText");
        inProps.put("passwordCallbackRef",
                new UTPasswordCallback(Map.of("student", "secret123"))
        );

        WSS4JInInterceptor wssIn = new WSS4JInInterceptor(inProps);

        // --- 4. Publier le service
        JaxWsServerFactoryBean serverFactory = new JaxWsServerFactoryBean();
        serverFactory.setServiceClass(HelloServiceImpl.class);
        serverFactory.setAddress("http://localhost:8080/services/hello-secure");
        serverFactory.setBus(bus);

        serverFactory.getFeatures().add(new LoggingFeature());

        Server server = serverFactory.create();
        server.getEndpoint().getInInterceptors().add(wssIn);

        System.out.println("Secure SOAP server running");
        System.out.println("WSDL: http://localhost:8080/services/hello-secure?wsdl");
    }
}
