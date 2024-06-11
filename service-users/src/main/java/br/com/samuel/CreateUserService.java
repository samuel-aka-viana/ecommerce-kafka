package br.com.samuel;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class CreateUserService {

    private final Connection connection;

    CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        connection.createStatement().execute("create table if not exists Users (" +
                "uuid varchar(200) primary key ," +
                "email varchar(200))");
    }

    public static void main(String[] args) throws SQLException {
        var createUserService = new CreateUserService();
        try (var service = new KafkaService<>(
                CreateUserService.class.getSimpleName(),
                "loja_pedidos",
                createUserService::parse,
                Order.class,
                Map.of())
        ) {
            service.run();
        }
    }

    private final KafkaDispatcher<Order> dispatcher = new KafkaDispatcher<>();

    private void parse(ConsumerRecord<String, Order> record) throws Exception {
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.value());
        var order = record.value();
        if (isNewUser(order.getEmail())) {
            insertNewUser(order.getEmail());

        }
    }

    private void insertNewUser(String email) throws SQLException {
        var conn = connection.prepareStatement("insert into Users(uuid, email) values (?,?)");
        conn.setString(1, UUID.randomUUID().toString());
        conn.setString(2, email);
        conn.execute();
        System.out.println("Usuario uuid e " + email + "email");
    }

    private boolean isNewUser(String email) throws SQLException {
        var exists = connection.prepareStatement("select uuid from Users where email = ? limit 1");
        exists.setString(1, email);
        var results = exists.executeQuery();
        return !results.next();
    }


}
