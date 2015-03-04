package co.uk.rushorm.rushserver.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import co.uk.rushorm.core.RushSearch;
import co.uk.rushorm.rushserver.RushServer;
import co.uk.rushorm.rushserver.ServerRushConfig;
import co.uk.rushorm.rushserver.example.modal.TestClass;
import co.uk.rushorm.rushserver.example.modal.TestClass2;

public class ExampleServlet extends HttpServlet {

    private String message;

    public void init() throws ServletException
    {
        // Do required initialization
        message = "Hello World";
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException
    {

        TestClass testClass = new TestClass();
        testClass.testClass2List = new ArrayList<>();
        
        for(int i = 0; i < 1000; i ++) {
            testClass.testClass2List.add(new TestClass2());
        }
        
        testClass.save();
        
        TestClass loaded = new RushSearch().findSingle(TestClass.class);
        
        // Set response content type
        response.setContentType("text/html");

        // Actual logic goes here.
        PrintWriter out = response.getWriter();
        out.println("<h1>" + message + "</h1>");
    }

    public void destroy()
    {
        // do nothing.
    }
}