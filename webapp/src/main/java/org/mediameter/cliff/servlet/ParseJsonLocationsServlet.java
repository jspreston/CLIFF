package org.mediameter.cliff.servlet;

import com.google.gson.Gson;
import org.mediameter.cliff.ParseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Created by spreston on 9/15/17.
 */
public class ParseJsonLocationsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ParseNlpJsonServlet.class);

    private static Gson gson = new Gson();

    public ParseJsonLocationsServlet() {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request,response);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

        logger.info("JSON Parse Request from "+request.getRemoteAddr());
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        HashMap results = null;
        String text = request.getParameter("q");
        if(text==null){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            try {
                results = ParseManager.parseFromLocationsJson(text);
            } catch(Exception e){   // try to give the user something useful
                logger.error(e.toString());
                results = ParseManager.getErrorText(e.toString());
            }
            String jsonResults = gson.toJson(results);
            logger.info(jsonResults);
            response.getWriter().write(jsonResults);
        }
    }
}
