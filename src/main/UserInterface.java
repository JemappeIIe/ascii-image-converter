package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
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
                System.out.println("(Error) Not a number");
                continue;
            }
            if (!commandMap.containsKey(command)) {
                System.out.println("(Error) No such command");
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
        System.out.println("(1) Show ASCII art");
        System.out.println("(2) Convert image to ASCII art");
        System.out.println("(3) Convert ASCII art to image");
        System.out.println("(0) Stop program");
        System.out.println("Select a command: ");
    }

    public static void showASCII() {
        File file = logic.readASCII(readInput(GET_ASCII_FILENAME_MESSAGE));
        try (Scanner scannerASCII = new Scanner(file)) {
            while (scannerASCII.hasNextLine()) {
                System.out.println(scannerASCII.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("(Error) No such file");
        }
    }

    public static void convertToASCII() {
        BufferedImage image;
        try {
            image = logic.readImage(readInput(GET_IMAGE_FILENAME_MESSAGE));
        } catch (IOException e) {
            System.out.println("(Error) Could not read image");
            return;
        }
        try {
            logic.writeASCII(image, readInput(GET_ASCII_FILENAME_MESSAGE));
        } catch (RuntimeException e) {
            System.out.println("(Error) Could not write ASCII");
        }
    }

    public static void convertToImage() {
        try {
            logic.writeImage(readInput(GET_ASCII_FILENAME_MESSAGE), readInput(GET_IMAGE_FILENAME_MESSAGE));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void stop() {
        System.exit(0);
    }
}
