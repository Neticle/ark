package pt.neticle.ark.servlet;

import pt.neticle.ark.base.WebApplication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletAdapter extends HttpServlet
{
    private final WebApplication application;

    public ServletAdapter (WebApplication application)
    {
        this.application = application;
    }

    @Override
    protected void service (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        try
        {
            application.dispatch(new ServletHttpRequest(req), new ServletHttpResponse(resp));
        } catch(Throwable e)
        {
            e.printStackTrace();
        }
    }
}
