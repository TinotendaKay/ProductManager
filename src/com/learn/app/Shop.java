package com.learn.app;

import com.learn.data.*;

import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@code Shop} class represents an application that manages Products
 *
 * @author TKanyandura
 * @version 1.0
 */
public class Shop {
    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Welcome to Java 11!");
        ProductManager pm = ProductManager.getInstance();
        AtomicInteger clientCount = new AtomicInteger(0);
        Callable<String> client = () -> {
            String clientId = "Client" + clientCount.incrementAndGet();
            String threadName = Thread.currentThread().getName();
            int productId = ThreadLocalRandom.current().nextInt(3)+104;
            String languageTag = ProductManager.getSupportedLocales().stream()
                    .skip(ThreadLocalRandom.current().nextInt(4))
                    .findFirst().get();
            StringBuilder log = new StringBuilder();
            log.append(clientId).append(" ").append(threadName).append("\n-\tstart of log\t-\n");
            log.append(pm.getDiscounts(languageTag)
                    .entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + "\t" + entry.getValue())
                    .collect(Collectors.joining("\n")));

            Product product = pm.reviewProduct(productId, Rating.FOUR_STAR, "Yet another review");
            log.append((product != null)
                    ? "\nProduct " + productId + "reviewed \n"
                    : "\nProduct " + productId + " not reviewed\n");
            pm.printProductReport(productId, languageTag, clientId);

            log.append(clientId + " generated report for " + productId + " product");

            log.append("\n-\tend of log\t-\n");
            return log.toString();
        };
        List<Callable<String>> clients = Stream.generate(() -> client)
                .limit(3)
                .collect(Collectors.toList());
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            List<Future<String>> results = executor.invokeAll(clients);
            executor.shutdown();
            results.stream().forEach(result -> {

                try {
                    System.out.println(result.get());
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, "Error retrieving client log", ex);
                }
            });
        } catch (InterruptedException e) {
            Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, "Error invoking clients", e);
        }

//        pm.printProductReport(104, "en-ZA");
//        pm.printProductReport(107, "en-US");
//        pm.createProduct(107, "Kombucha", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
//        pm.reviewProduct(107, Rating.TWO_STAR, "Looks like tea but is it?");
//        pm.reviewProduct(107, Rating.FOUR_STAR, "Fine tea.");
//        pm.reviewProduct(107, Rating.FOUR_STAR, "This is not tea");
//        pm.reviewProduct(107, Rating.FIVE_STAR, "Perfect!");
        //pm.printProductReport(107);
        //   pm.dumpData();
        // pm.restoreData();

        //Product product = pm.createProduct(101, "Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
//        pm.parseProduct("D,101,Teaee,1.99,0,2021-03-26");
//        pm.printProductReport(101);
//        pm.parseReview("101,4,Just my cup of tea");
//        pm.parseReview("101,3,So so");
//        pm.parseReview("101,5,Best tea ever");
//        pm.parseReview("101,2,Just tea");
        // pm.reviewProduct(product, Rating.FOUR_STAR, "I love your tea!");
        //   pm.printProductReport(104);
        //   pm.reviewProduct(101, Rating.FIVE_STAR, "Gotcha");
//        pm.printProductReport(101);

        //Product product2 = pm.createProduct(102, "Coffee", BigDecimal.valueOf(1.99), Rating.FOUR_STAR);
        //pm.reviewProduct(102, Rating.FOUR_STAR, "Best coffee ever");

        //  Product product3 = pm.createProduct(103, "Cake", BigDecimal.valueOf(3.99), Rating.FIVE_STAR, LocalDate.now().plusDays(3));
//        pm.parseProduct("F,103,Cake,3.99,5,2021-03-29");
        //  pm.reviewProduct(103, Rating.FIVE_STAR, "Best cake I have ever had");
        // pm.printProductReport(103);
        //Product product4 = pm.createProduct(105, "Cookie", BigDecimal.valueOf(3.99), Rating.TWO_STAR, LocalDate.now());
        // pm.reviewProduct(105, Rating.TWO_STAR, "Could do better");

        //   pm.printProductReport(104);
        // pm.printProductReport(105);
        //pm.printProductReport(106);
//
//        Comparator<Product> ratingSorter = (p1, p2) -> p2.getRating().ordinal() - p1.getRating().ordinal();
//        Comparator<Product> priceSorter = (p1, p2) -> p2.getPrice().compareTo(p1.getPrice());
//        pm.printProducts(p -> p.getBestBefore().isBefore(LocalDate.now().plusDays(2)), ((p1, p2) -> p2.getRating().ordinal() - p1.getRating().ordinal()));
//        pm.printProducts(p -> p.getPrice().floatValue() < 2, ((p1, p2) -> p2.getPrice().compareTo(p1.getPrice())));
//        pm.getDiscounts().forEach(
//                (rating, discount) -> System.out.println(rating + "\t" + discount));
//        pm.printProducts(ratingSorter.thenComparing(priceSorter));
//        pm.printProducts(ratingSorter.thenComparing(priceSorter).reversed());
        // Product product5 = product3.applyRating(Rating.THREE_STAR);
        // Product p6 = pm.createProduct(104, "Chocolate", BigDecimal.valueOf(2.99), Rating.FIVE_STAR);
        //   Product p7 = pm.createProduct(104, "Chocolate", BigDecimal.valueOf(2.99), Rating.FIVE_STAR, LocalDate.now().plusDays(2));
//
//        System.out.println(product);
//        System.out.println(product2);
//        System.out.println(product3);
//        System.out.println(product4);
//        System.out.println(product5);
//        System.out.println("p6.equals(p7) : " + p6.equals(p7));

//        Shape tri1 = new Triangle(13, 8);
//        Shape tri2 = new Triangle(8, 4);
//        Shape tri3 = new Triangle(14, 9);
//
//        Shape[] shapes = new Shape[3];
//        shapes[0] = tri1;
//        shapes[1] = tri2;
//        shapes[2] = tri3;
//
//        Shape[] shapes1 = new Shape[3];
//
//        Shape rect = new Rectangle(4, 6);
//        Shape circ = new Circle(6);
//        Shape tri = new Triangle(5, 7);
//        // declare and initialize shapes array
//        shapes1[0] = rect;
//        shapes1[1] = circ;
//        shapes1[2] = tri;
//
//        Stream.of(shapes1)
//                .sorted(new ShapeCompareByArea())
//                .forEach(shape -> {
//                    System.out.println(shape + " Area: " + shape.getArea());
//                });
//
//        System.out.println();
//
//        Stream.of(shapes).
//                sorted()
//                .forEach(System.out::println);
//
//        int _money = 0;
//        int $money = 9;
//        int money = 2;
//        char ls = '\\';
//        float myf = 1.8f;
//
//        int myNum;
//
//        int a = 11, b = 3;
//
//        System.out.println("a/b = " + a / b);
//
//        System.out.println("Math.round(a/b) = " + Math.round(a / b));
//
//        System.out.println("Math.round((float)a/b) = " + Math.round((float) a / b));
//
//        // System.out.println(myNum);
//
//        LocalDateTime now = LocalDateTime.now();
//        System.out.println("Now datetime : " + now.toString());
//        LocalDate now1 = LocalDate.now();
//        System.out.println("Now date : " + now1.toString());
//        now1.atTime(LocalTime.now());
//        now1.plusDays(5);
//        System.out.println("Now date + 5: " + now1.plusDays(5).toString());
//
//        ZoneId london = ZoneId.of("Europe/London");
//        ZoneId havana = ZoneId.of("America/Los_Angeles");
//
//        ZonedDateTime londonTime = ZonedDateTime.of(now, london);
//        ZonedDateTime havanaTime = ZonedDateTime.of(now, havana);
//
//        System.out.println("London::: " + londonTime);
//        System.out.println("Havana::: " + havanaTime.toString());
//
//        Locale locale = new Locale("en", "US");
//        BigDecimal price = new BigDecimal("25.88");
//        price.add(new BigDecimal(26));
//        String format = NumberFormat.getCurrencyInstance(locale).format(price);
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("", locale);
//        String format1 = now.format(dateTimeFormatter);
//        System.out.println("American Price:: " + format);


    }
}
