package pt.iflow.api.utils;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.xml.namespace.QName;
import javax.xml.rpc.Call;

import org.apache.axis.encoding.ser.ElementDeserializer;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.wsdl.gen.Parser;

import pt.iflow.api.core.RepositoryFile;
import pt.iknow.utils.wsdl.WSDLClassLoader;
import pt.iknow.utils.wsdl.WSDLField;
import pt.iknow.utils.wsdl.WSDLUtils;



public class WSDLUtilsV2  extends WSDLUtils{

	org.apache.axis.wsdl.gen.Parser parser= new Parser();
	
	public WSDLUtilsV2(RepositoryFile aisWsdl, String asUrl) throws Exception {
		super(aisWsdl.getResourceAsStream(), asUrl);
		
		 org.w3c.dom.Document doc = XMLUtils.newDocument(aisWsdl.getResourceAsStream());
         String _sRealUrl = asUrl;
         parser.run(_sRealUrl, doc);
	}
	
	public HashMap < String, Object > callService(String asService,
            String asPort, String asOperation, int anTimeOut,
            HashMap < String, Object > ahmInputs) {
        HashMap < String, Object > retObj = null;
        
        QName qnService = this.getQName(asService);
        QName qnPort = this.getQName(asPort);
        QName qnOperation = this.getQName(asOperation);
        Service sService = this.getService(qnService);
        Port pPort = this.getServicePort(sService, asPort);
        
        int nTimeOut = anTimeOut;
        if (nTimeOut <= 0) {
            nTimeOut = nDEF_CALL_TIMEOUT;
        }
        
        WSDLClassLoader classLoader = new WSDLClassLoader(Thread
                .currentThread().getContextClassLoader());
        try {
            org.apache.axis.client.Service axisService = new org.apache.axis.client.Service(
                    parser, qnService);
            
            Call call = axisService.createCall(qnPort, qnOperation);
            
            ((org.apache.axis.client.Call) call).setTimeout(new Integer(
                    anTimeOut * 1000));
            ((org.apache.axis.client.Call) call).setProperty(
                    ElementDeserializer.DESERIALIZE_CURRENT_ELEMENT,
                    Boolean.TRUE);
            
            WSDLField[] wfaInputs = this.getOperationFields(pPort, asOperation,
                    true);
            WSDLField[] wfaOutputs = this.getOperationFields(pPort,
                    asOperation, false);
            
            classLoader.setCall(call);
            classLoader.registerTypes(wfaInputs);
            classLoader.registerTypes(wfaOutputs);
            
            // Build input message
            ArrayList < Object > alInputs = new ArrayList < Object >();
            for (int i = 0; i < wfaInputs.length; i++) {
                fillRequest(call, wfaInputs[i], ahmInputs, alInputs,
                        classLoader, null);
            }
            
            // Build output objects
            for (int i = 0; i < wfaOutputs.length; i++) {
                createOutputClass(call, wfaOutputs[i], classLoader);
            }
            
            call.setTargetEndpointAddress(this.getUrl());            
            Object ret = call.invoke(alInputs.toArray());
            
            // Build output result
            @SuppressWarnings("unchecked")
            Map < String, Object > outputs = call.getOutputParams();
            retObj = new HashMap < String, Object >();
            
            for (int i = 0; i < wfaOutputs.length; i++) {
                String name = wfaOutputs[i].getQName().getLocalPart();
                Object value = outputs.get(name);
                
                if (value == null && i == 0) {
                    value = ret;
                }
                
                retObj = processOutputObject(value, retObj, wfaOutputs[i]);
            }
            
            call = null;
        } catch (Exception e) {
           int i=0;
        }
        classLoader.destroy();
        classLoader = null;
        
        return retObj;
    }
	
}
