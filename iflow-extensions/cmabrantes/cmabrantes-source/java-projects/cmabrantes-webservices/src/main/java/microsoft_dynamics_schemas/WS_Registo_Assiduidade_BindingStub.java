/**
 * WS_Registo_Assiduidade_BindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

import javax.xml.soap.SOAPElement;

import org.apache.axis.message.SOAPHeaderElement;

public class WS_Registo_Assiduidade_BindingStub extends org.apache.axis.client.Stub implements microsoft_dynamics_schemas.WS_Registo_Assiduidade_Port {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[9];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Read");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "No"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade"));
        oper.setReturnClass(microsoft_dynamics_schemas.WS_Registo_Assiduidade.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ReadMultiple");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "filter"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade_Filter"), microsoft_dynamics_schemas.WS_Registo_Assiduidade_Filter[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "bookmarkKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "setSize"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade_List"));
        oper.setReturnClass(microsoft_dynamics_schemas.WS_Registo_Assiduidade[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "ReadMultiple_Result"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("IsUpdated");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Key"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        oper.setReturnClass(boolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "IsUpdated_Result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Create");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade"), microsoft_dynamics_schemas.WS_Registo_Assiduidade.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade"));
        oper.setReturnClass(microsoft_dynamics_schemas.WS_Registo_Assiduidade.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CreateMultiple");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade_List"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade_List"), microsoft_dynamics_schemas.WS_Registo_Assiduidade[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade"));
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade_List"));
        oper.setReturnClass(microsoft_dynamics_schemas.WS_Registo_Assiduidade[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade_List"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Update");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade"), microsoft_dynamics_schemas.WS_Registo_Assiduidade.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade"));
        oper.setReturnClass(microsoft_dynamics_schemas.WS_Registo_Assiduidade.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("UpdateMultiple");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade_List"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade_List"), microsoft_dynamics_schemas.WS_Registo_Assiduidade[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade"));
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade_List"));
        oper.setReturnClass(microsoft_dynamics_schemas.WS_Registo_Assiduidade[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade_List"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Delete");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Key"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        oper.setReturnClass(boolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Delete_Result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Delete_SubformLinhas");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "SubformLinhas_Key"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        oper.setReturnClass(boolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Delete_SubformLinhas_Result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

    }

    public WS_Registo_Assiduidade_BindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public WS_Registo_Assiduidade_BindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public WS_Registo_Assiduidade_BindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Assiduity_Doc_Line_Line");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.Assiduity_Doc_Line_Line.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Assiduity_Doc_Line_Line_List");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.Assiduity_Doc_Line_Line[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Assiduity_Doc_Line_Line");
            qName2 = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Assiduity_Doc_Line_Line");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Post_Type");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.Post_Type.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Status");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.Status.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Type");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.Type.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.WS_Registo_Assiduidade.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade_Fields");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.WS_Registo_Assiduidade_Fields.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade_Filter");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.WS_Registo_Assiduidade_Filter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade_List");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.WS_Registo_Assiduidade[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade");
            qName2 = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "WS_Registo_Assiduidade");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public microsoft_dynamics_schemas.WS_Registo_Assiduidade read(java.lang.String no) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade:Read");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Read"));

        setAttachments(_call);
        try {
          java.lang.Object _resp = _call.invoke(new java.lang.Object[] {no});

          if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (microsoft_dynamics_schemas.WS_Registo_Assiduidade) _resp;
            } catch (java.lang.Exception _exception) {
                return (microsoft_dynamics_schemas.WS_Registo_Assiduidade) org.apache.axis.utils.JavaUtils.convert(_resp, microsoft_dynamics_schemas.WS_Registo_Assiduidade.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public microsoft_dynamics_schemas.WS_Registo_Assiduidade[] readMultiple(microsoft_dynamics_schemas.WS_Registo_Assiduidade_Filter[] filter, java.lang.String bookmarkKey, int setSize) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade:ReadMultiple");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "ReadMultiple"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {filter, bookmarkKey, new java.lang.Integer(setSize)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (microsoft_dynamics_schemas.WS_Registo_Assiduidade[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (microsoft_dynamics_schemas.WS_Registo_Assiduidade[]) org.apache.axis.utils.JavaUtils.convert(_resp, microsoft_dynamics_schemas.WS_Registo_Assiduidade[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public boolean isUpdated(java.lang.String key) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade:IsUpdated");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "IsUpdated"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {key});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Boolean) _resp).booleanValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_resp, boolean.class)).booleanValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public microsoft_dynamics_schemas.WS_Registo_Assiduidade create(microsoft_dynamics_schemas.WS_Registo_Assiduidade WS_Registo_Assiduidade) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade:Create");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Create"));
        
        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {WS_Registo_Assiduidade});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (microsoft_dynamics_schemas.WS_Registo_Assiduidade) _resp;
            } catch (java.lang.Exception _exception) {
                return (microsoft_dynamics_schemas.WS_Registo_Assiduidade) org.apache.axis.utils.JavaUtils.convert(_resp, microsoft_dynamics_schemas.WS_Registo_Assiduidade.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public microsoft_dynamics_schemas.WS_Registo_Assiduidade[] createMultiple(microsoft_dynamics_schemas.WS_Registo_Assiduidade[] WS_Registo_Assiduidade_List) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade:CreateMultiple");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "CreateMultiple"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {WS_Registo_Assiduidade_List});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (microsoft_dynamics_schemas.WS_Registo_Assiduidade[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (microsoft_dynamics_schemas.WS_Registo_Assiduidade[]) org.apache.axis.utils.JavaUtils.convert(_resp, microsoft_dynamics_schemas.WS_Registo_Assiduidade[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public microsoft_dynamics_schemas.WS_Registo_Assiduidade update(microsoft_dynamics_schemas.WS_Registo_Assiduidade WS_Registo_Assiduidade) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade:Update");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Update"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {WS_Registo_Assiduidade});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (microsoft_dynamics_schemas.WS_Registo_Assiduidade) _resp;
            } catch (java.lang.Exception _exception) {
                return (microsoft_dynamics_schemas.WS_Registo_Assiduidade) org.apache.axis.utils.JavaUtils.convert(_resp, microsoft_dynamics_schemas.WS_Registo_Assiduidade.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public microsoft_dynamics_schemas.WS_Registo_Assiduidade[] updateMultiple(microsoft_dynamics_schemas.WS_Registo_Assiduidade[] WS_Registo_Assiduidade_List) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade:UpdateMultiple");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "UpdateMultiple"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {WS_Registo_Assiduidade_List});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (microsoft_dynamics_schemas.WS_Registo_Assiduidade[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (microsoft_dynamics_schemas.WS_Registo_Assiduidade[]) org.apache.axis.utils.JavaUtils.convert(_resp, microsoft_dynamics_schemas.WS_Registo_Assiduidade[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public boolean delete(java.lang.String key) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade:Delete");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Delete"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {key});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Boolean) _resp).booleanValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_resp, boolean.class)).booleanValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public boolean delete_SubformLinhas(java.lang.String subformLinhas_Key) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade:Delete_SubformLinhas");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_registo_assiduidade", "Delete_SubformLinhas"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {subformLinhas_Key});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Boolean) _resp).booleanValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_resp, boolean.class)).booleanValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void setUser(String username, String password){
        this.setUsername(username);
        this.setPassword(password);
    }
}
