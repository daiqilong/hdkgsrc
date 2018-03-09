package com.jiuqi.kgj.workitem.send.webservice.todoservice;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.7.18
 * 2017-08-08T17:25:44.790+08:00
 * Generated source version: 2.7.18
 * 
 */
@WebServiceClient(name = "ISynTodoInfoWs", 
                  wsdlLocation = "http://10.1.100.44:9797/dna_ws/SynTodoInfoWsImpl?wsdl",
                  targetNamespace = "http://todoservice.webservice.send.workitem.kgj.jiuqi.com/") 
public class ISynTodoInfoWs_Service extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://todoservice.webservice.send.workitem.kgj.jiuqi.com/", "ISynTodoInfoWs");
    public final static QName SynTodoInfoWsImplPort = new QName("http://todoservice.webservice.send.workitem.kgj.jiuqi.com/", "SynTodoInfoWsImplPort");
    static {
        URL url = null;
        try {
            url = new URL("http://10.1.100.44:9797/dna_ws/SynTodoInfoWsImpl?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(ISynTodoInfoWs_Service.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://10.1.100.44:9797/dna_ws/SynTodoInfoWsImpl?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public ISynTodoInfoWs_Service(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public ISynTodoInfoWs_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ISynTodoInfoWs_Service() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    /**
     *
     * @return
     *     returns ISynTodoInfoWs
     */
    @WebEndpoint(name = "SynTodoInfoWsImplPort")
    public ISynTodoInfoWs getSynTodoInfoWsImplPort() {
        return super.getPort(SynTodoInfoWsImplPort, ISynTodoInfoWs.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ISynTodoInfoWs
     */
    @WebEndpoint(name = "SynTodoInfoWsImplPort")
    public ISynTodoInfoWs getSynTodoInfoWsImplPort(WebServiceFeature... features) {
        return super.getPort(SynTodoInfoWsImplPort, ISynTodoInfoWs.class, features);
    }

}
