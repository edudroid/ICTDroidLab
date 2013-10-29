package ict.edudroid.appengine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;

public class GCMBroadcast extends HttpServlet {
    private static final String myApiKey = "AIzaSyCx3nQWdbCvQK5UjCmOi9OfDCiiWZOo3l4";
    private List<String> androidTargets = new ArrayList<String>();
    private static final long serialVersionUID = 1L;

    /*protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

            doPost( req, resp);*/


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String userMessage = "http://ictdroidlab.appspot.com/serveModule?blob-key="+req.getParameter("modules");
        String reg_id = req.getParameter("devices");
        
        androidTargets.add(reg_id);

        Sender sender = new Sender(myApiKey);
        Message message = new Message.Builder().addData("message", userMessage)
                .build();

        MulticastResult result = sender.send(message, androidTargets, 1);

        if (result.getResults() != null) {
        	log(result.getResults().toString());
            int canonicalRegId = result.getCanonicalIds();
            if (canonicalRegId != 0) {

            }
            log("GCM sent succesfully");
        } else {
            int error = result.getFailure();
            System.out.println("Broadcast failure: " + error);
        }
        
    }

}