package com.dofull.huadong.g51;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the com.dofull.huadong.g51 package.
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

	private final static QName _GetTodoItemsResponse_QNAME = new QName(
			"http://g51.huadong.dofull.com/", "getTodoItemsResponse");
	private final static QName _GetTodoItems_QNAME = new QName(
			"http://g51.huadong.dofull.com/", "getTodoItems");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: com.dofull.huadong.g51
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link GetTodoItems }
	 * 
	 */
	public GetTodoItems createGetTodoItems() {
		return new GetTodoItems();
	}

	/**
	 * Create an instance of {@link GetTodoItemsResponse }
	 * 
	 */
	public GetTodoItemsResponse createGetTodoItemsResponse() {
		return new GetTodoItemsResponse();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link GetTodoItemsResponse }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://g51.huadong.dofull.com/", name = "getTodoItemsResponse")
	public JAXBElement<GetTodoItemsResponse> createGetTodoItemsResponse(
			GetTodoItemsResponse value) {
		return new JAXBElement<GetTodoItemsResponse>(
				_GetTodoItemsResponse_QNAME, GetTodoItemsResponse.class, null,
				value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link GetTodoItems }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://g51.huadong.dofull.com/", name = "getTodoItems")
	public JAXBElement<GetTodoItems> createGetTodoItems(GetTodoItems value) {
		return new JAXBElement<GetTodoItems>(_GetTodoItems_QNAME,
				GetTodoItems.class, null, value);
	}

}
