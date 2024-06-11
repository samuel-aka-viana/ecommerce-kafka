package br.com.samuel;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReadingReportService {
    private static final Path SOURCE = new File("src/main/resources/reports.txt").toPath();
    public static void main(String[] args) {
        var readingReportService = new ReadingReportService();
        try (var service = new KafkaService<>(
                ReadingReportService.class.getSimpleName(),
                "user_generate_reading_report",
                readingReportService::parse,
                User.class,
                Map.of())
        ) {
            service.run();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void parse(ConsumerRecord<String, User> record) throws IOException {
        System.out.println("------------------------------------------");
        System.out.println("Processing report for: " + record.value());
        var user = record.value();
        var target = new File(user.getReportPath());
        IO.copyTo(SOURCE, target);
        IO.append(target, "Created for " + user.getUUID());
        System.out.println("File created: " + target.getAbsolutePath());
    }

}
