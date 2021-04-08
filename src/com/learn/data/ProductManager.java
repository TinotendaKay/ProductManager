package com.learn.data;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProductManager {
    private static final Logger LOGGER = Logger.getLogger(ProductManager.class.getName());
    private final ResourceBundle config = ResourceBundle.getBundle("com.learn.data.config");
    private final MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));
    private final MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));
    //    private ResourceFormatter formatter;
    private static final Map<String, ResourceFormatter> formatters =
            Map.of("en-GB", new ResourceFormatter(Locale.UK),
                    "en-US", new ResourceFormatter(Locale.US),
                    "fr-FR", new ResourceFormatter(Locale.FRANCE),
                    "ru-RU", new ResourceFormatter(new Locale("ru", "RU")),
                    "zh-CN", new ResourceFormatter(Locale.CHINA));

    private final Path reportsFolder = Path.of(config.getString("reports.folder"));
    private final Path dataFolder = Path.of(config.getString("data.folder"));
    private final Path tempFolder = Paths.get(config.getString("temp.folder"));
    private Map<Product, List<Review>> products = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private static final ProductManager pm = new ProductManager();


//    public ProductManager(Locale locale) {
//        this(locale.toLanguageTag());
//    }

    private ProductManager() {
//        changeLocale(languageTag);
        loadAllData();
    }

//    public void changeLocale(String languageTag) {
//        ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
//    }

    public static ProductManager getInstance() {
        return pm;
    }

    public static Set<String> getSupportedLocales() {
        return formatters.keySet();
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        Product product = null;

        try {
            writeLock.lock();
            product = new Food(id, name, price, rating, bestBefore);
            products.putIfAbsent(product, new ArrayList<>());
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Error adding product " + ex.getMessage());
            return null;
        } finally {
            writeLock.unlock();
        }

        return product;
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating) {
        Product product = null;

        try {
            writeLock.lock();
            product = new Drink(id, name, price, rating);
            products.putIfAbsent(product, new ArrayList<>());
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Error adding product " + ex.getMessage());
            return null;
        } finally {
            writeLock.unlock();
        }

        return product;
    }

    public Product findProduct(int productId) throws ProductManagerException {
        try {
            readLock.lock();
            return this.products.keySet().stream()
                    .filter(p -> p.getId() == productId)
                    .findFirst()
                    .orElseThrow(() -> new ProductManagerException("Product with id " + productId + " not found."));
        } finally {
            readLock.unlock();
        }

    }

    public Product reviewProduct(int productId, Rating rating, String comments) {
        try {
            writeLock.lock();
            Product product = this.findProduct(productId);
            return this.reviewProduct(product, rating, comments);
        } catch (ProductManagerException pme) {
            LOGGER.log(Level.INFO, pme.getMessage());
            return null;
        } finally {
            writeLock.unlock();
        }
    }

    private Product reviewProduct(Product product, Rating rating, String comments) {

        List<Review> reviews = products.get(product);
        products.remove(product, reviews);
        reviews.add(new Review(rating, comments));

        /*int sum = 0;

        for (Review review : reviews) {
            sum += review.getRating().ordinal();
        }

        product = product.applyRating(Ratable.convert(Math.round((float) sum / reviews.size())));*/

        product = product.applyRating(
                Ratable.convert(
                        (int) Math.round(
                                reviews.stream()
                                        .mapToInt(r -> r.getRating().ordinal())
                                        .average()
                                        .orElse(0))));
        products.put(product, reviews);

        return product;
    }

    public void printProductReport(int productId, String languageTag, String client) {
        try {
            readLock.lock();
            Product product = this.findProduct(productId);
            printProductReport(product, languageTag, client);
        } catch (ProductManagerException pme) {
            LOGGER.log(Level.INFO, pme.getMessage());
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, "Error printing product report " + ioe.getMessage(), ioe);
        } finally {
            readLock.unlock();
        }

    }

    private void printProductReport(Product product, String languageTag, String client) throws IOException {
        ResourceFormatter formatter = formatters.getOrDefault(languageTag, new ResourceFormatter(Locale.UK));
        // StringBuilder builder = new StringBuilder();
        Files.createDirectories(reportsFolder);
        Path productReportFile = reportsFolder.resolve(MessageFormat.format(config.getString("report.file"), product.getId(), client));

        try (PrintWriter writer = new PrintWriter(// move this to the printProductReport method
                new OutputStreamWriter(
                        Files.newOutputStream(productReportFile,
                                StandardOpenOption.CREATE),
                        "UTF-8"))) {

            this.printProduct(writer, product, formatter);
            List<Review> reviews = products.get(product);
            Collections.sort(reviews);
            this.printReviews(writer, reviews, formatter);

        }


    }

    public void printProducts(Predicate<Product> filter, Comparator<Product> sorter, String languageTag) {
        try {
            readLock.lock();
            ResourceFormatter formatter = formatters.getOrDefault(languageTag, new ResourceFormatter(Locale.UK));
            StringBuilder builder = new StringBuilder();
            products.keySet().stream()
                    .sorted(sorter)
                    .filter(filter)
                    .forEach(p -> builder.append(formatter.formatProduct(p) + '\n'));

            System.out.println(builder);
        } finally {
            readLock.unlock();
        }

    }

    private void dumpData() {
        try {
            if (Files.notExists(tempFolder)) {
                Files.createDirectories(tempFolder);
            }

            Path tempFile = tempFolder.resolve(
                    MessageFormat.format(config.getString("temp.file"), LocalDate.now()));
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(tempFile, StandardOpenOption.CREATE))) {
                out.writeObject(this.products);
//                products = new HashMap<>();
            }

        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, "Error dumping data " + ioe.getMessage(), ioe);
        }
    }

    @SuppressWarnings("unchecked")
    private void restoreData() {
        try {
            Path tempFile = Files.list(tempFolder)
                    .filter(path -> path.getFileName().toString().endsWith("tmp"))
                    .findFirst().orElseThrow();
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(tempFile, StandardOpenOption.DELETE_ON_CLOSE))) {
                products = (HashMap) in.readObject();
            }

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error restoring data " + ex.getMessage(), ex);
        }
    }

    private void loadAllData() {
        // bulk loading of data
        try {
            products = Files.list(dataFolder)
                    .filter(file -> file.getFileName().toString().startsWith("product"))
                    .map(file -> loadProduct(file))
                    .filter(product -> product != null)
                    .collect(Collectors.toMap(product -> product,
                            product -> loadReviews(product)));
            System.out.println("Is products empty  " + products.isEmpty());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading data " + e.getMessage(), e);
        }
    }

    private Product loadProduct(Path file) {
        Product product = null;
        Path resolve = dataFolder.resolve(file);
        try {
            //  parseProduct(Files.lines(dataFolder.resolve(file), Charset.forName("UTF-8")).findFirst().orElseThrow());
            product = Files.lines(resolve, Charset.forName("UTF-8"))
                    .map(text -> parseProduct(text))
                    .findFirst().orElseThrow();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error loading product " + e.getMessage());
        }

        return product;
    }

    private List<Review> loadReviews(Product product) {
        List<Review> reviews = null;
        Path file = dataFolder.resolve(
                MessageFormat.format(
                        config.getString("reviews.data.file"), product.getId()));
        if (Files.notExists(file)) {
            reviews = new ArrayList<>();
        } else {
            try {
                reviews = Files.lines(file, Charset.forName("UTF-8"))
                        .map(text -> parseReview(text))
                        .filter(review -> review != null)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error loading reviews " + e.getMessage());
            }
        }
        return reviews;
    }

    private Review parseReview(String text) {
        Review review = null;
        try {
            Object[] values = reviewFormat.parse(text);

            review = new Review(
                    Ratable.convert(Integer.parseInt(((String) values[0]).trim())),
                    (String) values[1]);
        } catch (ParseException | NumberFormatException e) {
            LOGGER.log(Level.WARNING,
                    "Error parsing review " + text + " " + e.getMessage());
        }

        return review;
    }

    private Product parseProduct(String text) {
        Product product = null;
        try {
            Object[] values = productFormat.parse(text);
            int id = Integer.parseInt((String) values[1]);
            String name = (String) values[2];
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble((String) values[3]));
            Rating rating = Ratable.convert(Integer.parseInt((String) values[4]));
            LocalDate bestBefore = LocalDate.parse((String) values[5]);
            switch ((String) values[0]) {
                case "F":
                    //create a food product
                    product = new Food(id, name, price, rating, bestBefore);//createProduct(id, name, price, rating, bestBefore);
                    break;
                case "D":
                    // create a drink product
                    product = new Drink(id, name, price, rating);// createProduct(id, name, price, rating);
                    break;
            }
        } catch (ParseException | NumberFormatException | DateTimeParseException e) {
            LOGGER.log(Level.WARNING,
                    "Error parsing product " + text + " " + e);
        }
        return product;
    }

    private void printProduct(PrintWriter writer, Product product, ResourceFormatter formatter) {
        writer.append(formatter.formatProduct(product) + System.lineSeparator());
    }

    private void printReviews(PrintWriter writer, List<Review> reviews, ResourceFormatter formatter) {

        String[] arr = {"Tea", "Cake"};
        List<String> texts = Arrays.asList(arr);
        // texts.add("six");
        //  arr[2] = "New";
        texts.set(0, "Coffee");

        Deque<String> deque = new ArrayDeque<>();
        Set<String> set = new HashSet<>();
        set.add("one");
        set.add("one");
        set.add(null);
        set.add(null);


        if (reviews.isEmpty()) {
            writer.append(formatter.getText("no.reviews") + System.lineSeparator());
        } else {
            writer.append(reviews.stream()
                    .map(r -> formatter.formatReview(r) + System.lineSeparator())
                    .collect(Collectors.joining()));
        }

    }

    private static class ResourceFormatter {
        private ResourceBundle resourceBundle;
        private Locale locale;
        private DateTimeFormatter dateTimeFormatter;
        private NumberFormat moneyFormat;

        private ResourceFormatter(Locale locale) {
            this.locale = locale;
            resourceBundle = ResourceBundle.getBundle("com.learn.data.resources", locale);
            dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
            moneyFormat = NumberFormat.getCurrencyInstance(locale);
        }

        private String formatProduct(Product product) {
            return MessageFormat.format(resourceBundle.getString("product"),
                    product.getName(),
                    moneyFormat.format(product.getPrice()),
                    product.getRating().getStars(),
                    dateTimeFormatter.format(product.getBestBefore()));
        }

        private String formatReview(Review review) {
            return MessageFormat.format(resourceBundle.getString("review"),
                    review.getRating().getStars(),
                    review.getComments());
        }

        private String getText(String key) {
            return resourceBundle.getString(key);
        }
    }

    /**
     * @return map of stars and discount value
     */
    public Map<String, String> getDiscounts(String languageTag) {
        try {
            readLock.lock();
            ResourceFormatter formatter = formatters.getOrDefault(languageTag, new ResourceFormatter(Locale.UK));
            return this.products.keySet().stream()
                    .collect(
                            Collectors.groupingBy(
                                    product -> product.getRating().getStars(),
                                    Collectors.collectingAndThen(
                                            Collectors.summingDouble(
                                                    product -> product.getDiscount().doubleValue()),
                                            discount -> formatter.moneyFormat.format(discount))));
        } finally {
            readLock.unlock();
        }

    }
}
