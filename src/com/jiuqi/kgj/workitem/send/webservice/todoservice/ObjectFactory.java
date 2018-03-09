package com.jiuqi.kgj.workitem.send.webservice.todoservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the
 * com.jiuqi.kgj.workitem.send.webservice.todoservice package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _GetPurchaseData_QNAME = new QName(
			"http://todoservice.webservice.send.workitem.kgj.jiuqi.com",
			"getPurchaseData");
	private final static QName _GetAlterDataResponse_QNAME = new QName(
			"http://todoservice.webservice.send.workitem.kgj.jiuqi.com",
			"getAlterDataResponse");
	private final static QName _GetDisposeData_QNAME = new QName(
			"http://todoservice.webservice.send.workitem.kgj.jiuqi.com",
			"getDisposeData");
	private final static QName _GetDisposeDataResponse_QNAME = new QName(
			"http://todoservice.webservice.send.workitem.kgj.jiuqi.com",
			"getDisposeDataResponse");
	private final static QName _GetPurchaseDataResponse_QNAME = new QName(
			"http://todoservice.webservice.send.workitem.kgj.jiuqi.com",
			"getPurchaseDataResponse");
	private final static QName _GetAlterData_QNAME = new QName(
			"http://todoservice.webservice.send.workitem.kgj.jiuqi.com",
			"getAlterData");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package:
	 * com.jiuqi.kgj.workitem.send.webservice.todoservice
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link GetDisposeDataResponse }
	 * 
	 */
	public GetDisposeDataResponse createGetDisposeDataResponse() {
		return new GetDisposeDataResponse();
	}

	/**
	 * Create an instance of {@link GetPurchaseData }
	 * 
	 */
	public GetPurchaseData createGetPurchaseData() {
		return new GetPurchaseData();
	}

	/**
	 * Create an instance of {@link GetAlterData }
	 * 
	 */
	public GetAlterData createGetAlterData() {
		return new GetAlterData();
	}

	/**
	 * Create an instance of {@link GetDisposeData }
	 * 
	 */
	public GetDisposeData createGetDisposeData() {
		return new GetDisposeData();
	}

	/**
	 * Create an instance of {@link GetPurchaseDataResponse }
	 * 
	 */
	public GetPurchaseDataResponse createGetPurchaseDataResponse() {
		return new GetPurchaseDataResponse();
	}

	/**
	 * Create an instance of {@link GetAlterDataResponse }
	 * 
	 */
	public GetAlterDataResponse createGetAlterDataResponse() {
		return new GetAlterDataResponse();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link GetPurchaseData }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://todoservice.webservice.send.workitem.kgj.jiuqi.com", name = "getPurchaseData")
	public JAXBElement<GetPurchaseData> createGetPurchaseData(
			GetPurchaseData value) {
		return new JAXBElement<GetPurchaseData>(_GetPurchaseData_QNAME,
				GetPurchaseData.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link GetAlterDataResponse }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://todoservice.webservice.send.workitem.kgj.jiuqi.com", name = "getAlterDataResponse")
	public JAXBElement<GetAlterDataResponse> createGetAlterDataResponse(
			GetAlterDataResponse value) {
		return new JAXBElement<GetAlterDataResponse>(
				_GetAlterDataResponse_QNAME, GetAlterDataResponse.class, null,
				value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link GetDisposeData }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://todoservice.webservice.send.workitem.kgj.jiuqi.com", name = "getDisposeData")
	public JAXBElement<GetDisposeData> createGetDisposeData(GetDisposeData value) {
		return new JAXBElement<GetDisposeData>(_GetDisposeData_QNAME,
				GetDisposeData.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link GetDisposeDataResponse }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://todoservice.webservice.send.workitem.kgj.jiuqi.com", name = "getDisposeDataResponse")
	public JAXBElement<GetDisposeDataResponse> createGetDisposeDataResponse(
			GetDisposeDataResponse value) {
		return new JAXBElement<GetDisposeDataResponse>(
				_GetDisposeDataResponse_QNAME, GetDisposeDataResponse.class,
				null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link GetPurchaseDataResponse }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://todoservice.webservice.send.workitem.kgj.jiuqi.com", name = "getPurchaseDataResponse")
	public JAXBElement<GetPurchaseDataResponse> createGetPurchaseDataResponse(
			GetPurchaseDataResponse value) {
		return new JAXBElement<GetPurchaseDataResponse>(
				_GetPurchaseDataResponse_QNAME, GetPurchaseDataResponse.class,
				null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link GetAlterData }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://todoservice.webservice.send.workitem.kgj.jiuqi.com", name = "getAlterData")
	public JAXBElement<GetAlterData> createGetAlterData(GetAlterData value) {
		return new JAXBElement<GetAlterData>(_GetAlterData_QNAME,
				GetAlterData.class, null, value);
	}

}
