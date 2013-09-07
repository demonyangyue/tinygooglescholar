package com.nju.app.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nju.app.search.SearchManager;

/**
 * This servlet is used to deal with the search request
 * and return the search results to the client
 */
public class SearchController extends HttpServlet{
    
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
                      throws IOException, ServletException{
    	String searchWord = request.getParameter("searchWord");
    	SearchManager searchManager = new SearchManager(searchWord);
    	List searchResult = null;
    	searchResult = searchManager.search();
    	RequestDispatcher dispatcher = request.getRequestDispatcher("search.jsp");
    	request.setAttribute("searchResult",searchResult);
    	dispatcher.forward(request, response);
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
                     throws IOException, ServletException{
        doPost(request, response);
    }   
}
