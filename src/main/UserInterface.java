package main;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserInterface {
    public static final String GET_IMAGE_FILENAME_MESSAGE = "Enter image filename: ";
    public static final String GET_IMAGE_FORMAT_MESSAGE = "Enter image format: ";
    public static final String GET_ASCII_FILENAME_MESSAGE = "Enter ASCII filename: ";
    private static final Map<Integer, Runnable> commandMap = new HashMap<>();

    static {
        commandMap.put(0, UserInterface::stop);
        commandMap.put(1, UserInterface::showASCII);
        commandMap.put(2, UserInterface::convertToASCII);
        commandMap.put(3, UserInterface::convertToImage);
        commandMap.put(4, UserInterface::listAllASCII);
        commandMap.put(5, UserInterface::listAllImages);
    }

    private static Scanner scanner;
    private static Logic logic;


    public UserInterface(Scanner scanner) {
        UserInterface.scanner = scanner;
        logic = new Logic(this);
    }

    public void start() {
        while (true) {
            showCommands();
            int command;
            try {
                command = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                continue;
            }
            commandMap.get(command).run();
        }
    }

    public static String readInput(String message) {
        System.out.println(message);
        return scanner.nextLine();
    }

    public void showCommands() {
        System.out.println("(0) Stop program");
        System.out.println("(1) Show ASCII art");
        System.out.println("(2) Convert image to ASCII art");
        System.out.println("(3) Convert ASCII art to image");
        System.out.println("(4) List all ASCII artworks");
        System.out.println("(5) List all images");
        System.out.println("Select a command: ");
    }

    public static void showASCII() {
        String filename = readInput(GET_ASCII_FILENAME_MESSAGE);
        File file = logic.readASCIIFile(filename);
        try (Scanner scannerASCII = new Scanner(file)) {
            while (scannerASCII.hasNextLine()) {
                System.out.println(scannerASCII.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("(Error) No such ASCII file");
        }
    }

    public static void convertToASCII() {
        String imageFilename = readInput(GET_IMAGE_FILENAME_MESSAGE);
        BufferedImage image;
        try {
            image = logic.readImageFile(imageFilename);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        String ASCIIFilename = readInput(GET_ASCII_FILENAME_MESSAGE);
        try {
            logic.writeASCII(image, ASCIIFilename);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void convertToImage() {
        String ASCIIFilename = readInput(GET_ASCII_FILENAME_MESSAGE);
        String imageFilename = readInput(GET_IMAGE_FILENAME_MESSAGE);
        String imageFormat = readInput(GET_IMAGE_FORMAT_MESSAGE);
        try {
            logic.writeImage(ASCIIFilename, imageFilename, imageFormat);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void listAllASCII() {
        try {
            String[] ASCIIArray = logic.getAllASCII();
            if (ASCIIArray.length == 0) {
                System.out.println("No ASCII artworks found, create some!");
            } else {
                System.out.println("Number of ASCII artworks: " + ASCIIArray.length);
                for (String ASCII : ASCIIArray) {
                    System.out.println(ASCII);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void listAllImages() {
        try {
            String[] imageArray = logic.getAllImages();
            if (imageArray.length == 0) {
                System.out.println("No images found, upload some!");
            } else {
                System.out.println("Number of images: " + imageArray.length);
                for (String image : imageArray) {
                    System.out.println(image);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void stop() {
        System.exit(0);
    }
}
