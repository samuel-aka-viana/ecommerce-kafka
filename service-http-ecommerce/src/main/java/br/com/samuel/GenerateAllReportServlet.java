package br.com.samuel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class GenerateAllReportServlet extends HttpServlet {

    private final KafkaDispatcher<String> batchDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        batchDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            batchDispatcher.send(
                    "SEND_MESSAGE_TO_ALL_USERS",
                    "user_generate_reading_report",
                    "user_generate_reading_report"
            );


            System.out.println("Send generete report to all users");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("New order processed, send succesfully");
        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException ex) {
            throw new ServletException(ex);
        }


    }
}
