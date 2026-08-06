package com.shivansh.cakes.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FeatureGenerator {

    private static final String BASE_PACKAGE = "com.shivansh.cakes";
    private static final String BASE_PATH =
            "src/main/java/com/shivansh/cakes";

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            System.out.println("Usage: java FeatureGenerator product,order,cart");
            return;
        }

        String[] features = args[0].split(",");

        for (String feature : features) {

            feature = feature.trim().toLowerCase();

            String className = Character.toUpperCase(feature.charAt(0))
                    + feature.substring(1);

            create(feature, "controller", className + "Controller.java");
            create(feature, "service", className + "Service.java");
            create(feature, "service/impl", className + "ServiceImpl.java");
            create(feature, "repository", className + "Repository.java");
            create(feature, "entity", className + ".java");
            create(feature, "mapper", className + "Mapper.java");
            create(feature, "exception", className + "Exception.java");
            create(feature, "specification", className + "Specification.java");
            create(feature, "validation", className + "Validator.java");
            create(feature, "constant", className + "Constants.java");

            create(feature, "dto/request", className + "Request.java");
            create(feature, "dto/response", className + "Response.java");

            System.out.println("Created feature: " + feature);
        }

        System.out.println("\nAll features generated successfully.");
    }

    private static void create(String feature,
                               String folder,
                               String fileName) throws IOException {

        Path directory = Path.of(BASE_PATH, feature, folder);

        Files.createDirectories(directory);

        Path file = directory.resolve(fileName);

        if (!Files.exists(file)) {

            String packageName = BASE_PACKAGE + "."
                    + feature + "."
                    + folder.replace("/", ".");

            Files.writeString(file,
                    "package " + packageName + ";\n");
        }
    }
}