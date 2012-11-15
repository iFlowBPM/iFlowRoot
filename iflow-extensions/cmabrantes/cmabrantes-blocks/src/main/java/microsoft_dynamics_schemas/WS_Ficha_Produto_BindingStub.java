/**
 * WS_Ficha_Produto_BindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package microsoft_dynamics_schemas;

public class WS_Ficha_Produto_BindingStub extends org.apache.axis.client.Stub implements microsoft_dynamics_schemas.WS_Ficha_Produto_Port {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[8];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Read");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "No"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto"));
        oper.setReturnClass(microsoft_dynamics_schemas.WS_Ficha_Produto.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ReadMultiple");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "filter"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto_Filter"), microsoft_dynamics_schemas.WS_Ficha_Produto_Filter[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "bookmarkKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "setSize"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto_List"));
        oper.setReturnClass(microsoft_dynamics_schemas.WS_Ficha_Produto[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "ReadMultiple_Result"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("IsUpdated");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Key"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        oper.setReturnClass(boolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "IsUpdated_Result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Create");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto"), microsoft_dynamics_schemas.WS_Ficha_Produto.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto"));
        oper.setReturnClass(microsoft_dynamics_schemas.WS_Ficha_Produto.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CreateMultiple");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto_List"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto_List"), microsoft_dynamics_schemas.WS_Ficha_Produto[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto"));
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto_List"));
        oper.setReturnClass(microsoft_dynamics_schemas.WS_Ficha_Produto[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto_List"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Update");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto"), microsoft_dynamics_schemas.WS_Ficha_Produto.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto"));
        oper.setReturnClass(microsoft_dynamics_schemas.WS_Ficha_Produto.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("UpdateMultiple");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto_List"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto_List"), microsoft_dynamics_schemas.WS_Ficha_Produto[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto"));
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto_List"));
        oper.setReturnClass(microsoft_dynamics_schemas.WS_Ficha_Produto[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto_List"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Delete");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Key"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        oper.setReturnClass(boolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Delete_Result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

    }

    public WS_Ficha_Produto_BindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public WS_Ficha_Produto_BindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public WS_Ficha_Produto_BindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Costing_Method");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.Costing_Method.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Flushing_Method");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.Flushing_Method.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Manufacturing_Policy");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.Manufacturing_Policy.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Order_Tracking_Policy");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.Order_Tracking_Policy.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Price_Profit_Calculation");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.Price_Profit_Calculation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Reordering_Policy");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.Reordering_Policy.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Replenishment_System");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.Replenishment_System.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Reserve");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.Reserve.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.WS_Ficha_Produto.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto_Fields");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.WS_Ficha_Produto_Fields.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto_Filter");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.WS_Ficha_Produto_Filter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto_List");
            cachedSerQNames.add(qName);
            cls = microsoft_dynamics_schemas.WS_Ficha_Produto[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto");
            qName2 = new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "WS_Ficha_Produto");
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

    public microsoft_dynamics_schemas.WS_Ficha_Produto read(java.lang.String no) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_ficha_produto:Read");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Read"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {no});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (microsoft_dynamics_schemas.WS_Ficha_Produto) _resp;
            } catch (java.lang.Exception _exception) {
                return (microsoft_dynamics_schemas.WS_Ficha_Produto) org.apache.axis.utils.JavaUtils.convert(_resp, microsoft_dynamics_schemas.WS_Ficha_Produto.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public microsoft_dynamics_schemas.WS_Ficha_Produto[] readMultiple(microsoft_dynamics_schemas.WS_Ficha_Produto_Filter[] filter, java.lang.String bookmarkKey, int setSize) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_ficha_produto:ReadMultiple");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "ReadMultiple"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {filter, bookmarkKey, new java.lang.Integer(setSize)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (microsoft_dynamics_schemas.WS_Ficha_Produto[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (microsoft_dynamics_schemas.WS_Ficha_Produto[]) org.apache.axis.utils.JavaUtils.convert(_resp, microsoft_dynamics_schemas.WS_Ficha_Produto[].class);
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
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_ficha_produto:IsUpdated");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "IsUpdated"));

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

    public microsoft_dynamics_schemas.WS_Ficha_Produto create(microsoft_dynamics_schemas.WS_Ficha_Produto WS_Ficha_Produto) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_ficha_produto:Create");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Create"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {WS_Ficha_Produto});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (microsoft_dynamics_schemas.WS_Ficha_Produto) _resp;
            } catch (java.lang.Exception _exception) {
                return (microsoft_dynamics_schemas.WS_Ficha_Produto) org.apache.axis.utils.JavaUtils.convert(_resp, microsoft_dynamics_schemas.WS_Ficha_Produto.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public microsoft_dynamics_schemas.WS_Ficha_Produto[] createMultiple(microsoft_dynamics_schemas.WS_Ficha_Produto[] WS_Ficha_Produto_List) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_ficha_produto:CreateMultiple");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "CreateMultiple"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {WS_Ficha_Produto_List});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (microsoft_dynamics_schemas.WS_Ficha_Produto[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (microsoft_dynamics_schemas.WS_Ficha_Produto[]) org.apache.axis.utils.JavaUtils.convert(_resp, microsoft_dynamics_schemas.WS_Ficha_Produto[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public microsoft_dynamics_schemas.WS_Ficha_Produto update(microsoft_dynamics_schemas.WS_Ficha_Produto WS_Ficha_Produto) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_ficha_produto:Update");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Update"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {WS_Ficha_Produto});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (microsoft_dynamics_schemas.WS_Ficha_Produto) _resp;
            } catch (java.lang.Exception _exception) {
                return (microsoft_dynamics_schemas.WS_Ficha_Produto) org.apache.axis.utils.JavaUtils.convert(_resp, microsoft_dynamics_schemas.WS_Ficha_Produto.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public microsoft_dynamics_schemas.WS_Ficha_Produto[] updateMultiple(microsoft_dynamics_schemas.WS_Ficha_Produto[] WS_Ficha_Produto_List) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_ficha_produto:UpdateMultiple");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "UpdateMultiple"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {WS_Ficha_Produto_List});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (microsoft_dynamics_schemas.WS_Ficha_Produto[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (microsoft_dynamics_schemas.WS_Ficha_Produto[]) org.apache.axis.utils.JavaUtils.convert(_resp, microsoft_dynamics_schemas.WS_Ficha_Produto[].class);
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
        _call.setSOAPActionURI("urn:microsoft-dynamics-schemas/page/ws_ficha_produto:Delete");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:microsoft-dynamics-schemas/page/ws_ficha_produto", "Delete"));

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

}
