package br.com.samuel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<Order>();
    private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<Email>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var email = req.getParameter("email");
            var orderId = UUID.randomUUID().toString();
            var amount = new BigDecimal(req.getParameter("amount"));

            var order = new Order(orderId, amount, email);
            orderDispatcher.send("loja_pedidos", email, order);

            var emailCode = new Email(
                    "Thank you for your order! We are processing your order!",
                    "Thank you for your order! We are processing your order!" + UUID.randomUUID().toString()
            );
            emailDispatcher.send("loja_order_pedidos", email, emailCode);
            System.out.println("New order processed, send succesfully");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("New order processed, send succesfully");
        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException ex) {
            throw new ServletException(ex);
        }


    }
}
