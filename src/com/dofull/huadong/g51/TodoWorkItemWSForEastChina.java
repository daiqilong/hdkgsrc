package com.dofull.huadong.g51;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.1.3-hudson-390-
 * Generated source version: 2.0
 * 
 */
@WebService(name = "TodoWorkItemWSForEastChina", targetNamespace = "http://g51.huadong.dofull.com/")
public interface TodoWorkItemWSForEastChina {

	/**
	 * 
	 * @param input
	 * @return returns java.lang.String
	 */
	@WebMethod
	@WebResult(targetNamespace = "")
	@RequestWrapper(localName = "getTodoItems", targetNamespace = "http://g51.huadong.dofull.com/", className = "com.dofull.huadong.g51.GetTodoItems")
	@ResponseWrapper(localName = "getTodoItemsResponse", targetNamespace = "http://g51.huadong.dofull.com/", className = "com.dofull.huadong.g51.GetTodoItemsResponse")
	public String getTodoItems(
			@WebParam(name = "input", targetNamespace = "") String input);

}
