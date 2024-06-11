package br.com.samuel;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<Email>()) {
                for (var i = 0; i < 10; i++) {

                    var orderId = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 5000 + 1);
                    var email = Math.random() + "@email.com";

                    var order = new Order(orderId, amount, email);
                    orderDispatcher.send("loja_pedidos", email, order);

                    var emailCode = new Email(
                            "Thank you for your order! We are processing your order!",
                            "Thank you for your order! We are processing your order!" + UUID.randomUUID().toString()
                    );
                    emailDispatcher.send("loja_order_pedidos", email, emailCode);
                }
            }
        }
    }

}
