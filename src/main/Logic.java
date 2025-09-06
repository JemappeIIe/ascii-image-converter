package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Logic {
    public static final String ABSOLUTE_PROJECT_PATH = System.getProperty("user.dir");
    public static final String IMAGE_PATH = ABSOLUTE_PROJECT_PATH + "/src/main/resources/images/";
    public static final String ASCII_PATH = ABSOLUTE_PROJECT_PATH + "/src/main/resources/ascii/";
    // https://stackoverflow.com/a/74186686
    public static final char[] ASCII = " `.-':_,^=;><+!rc*/z?sLTv)J7(|Fi{C}fI31tlu[neoZ5Yxjya]2ESwqkP6h9d4VpOGbUAKXHm8RD#$Bg0MNWQ%&@".toCharArray();
    public static final Map<Character, Integer> ASCII_CHAR_INDEX = new HashMap<>();

    static {
        for (int i = 0; i < ASCII.length; ++i) {
            ASCII_CHAR_INDEX.put(ASCII[i], i);
        }
    }

    UserInterface userInterface;

    public Logic(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public File readASCII(String filename) {
        return new File(ASCII_PATH + filename);
    }

    public void writeASCII(BufferedImage image, String outputFileName) {
        File file = new File(ASCII_PATH + outputFileName);
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(new FileWriter(file));
        } catch (IOException e) {
            throw new RuntimeException("(Error) Could not read ASCII");
        }
        for (int y = 0; y < image.getHeight(); ++y) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < image.getWidth(); ++x) {
                int color = image.getRGB(x, y);
                int red = (color & 0x00ff0000) >> 16;
                int green = (color & 0x0000ff00) >> 8;
                int blue = color & 0x000000ff;
                double luminance = this.colorToLuminance(red, green, blue);
                row.append(ASCII[luminanceToCharIndex(luminance)]);
            }
            printWriter.println(row);
        }
        printWriter.close();
    }

    public BufferedImage readImage(String filename) throws IOException {
        File file = new File(IMAGE_PATH + filename);
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new IOException("(Error) Could not read image");
        }
        return image;
    }

    public void writeImage(String ASCIIFilename, String imageFilename) {
        String imageFormat;
        try {
            imageFormat = imageFilename.split("\\.")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("(Error) Missing file extension");
        }
        File ascii = readASCII(ASCIIFilename);
        ArrayList<ArrayList<Integer>> ASCIIRowsConvertedToLuminance = new ArrayList<>();
        int height = 0;
        int width = 0;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(ascii))) {
            String row;
            while ((row = bufferedReader.readLine()) != null) {
                ASCIIRowsConvertedToLuminance.add(new ArrayList<>());
                for (char character : row.toCharArray()) {
                    ASCIIRowsConvertedToLuminance.get(height).add(charIndexToLuminance(Logic.ASCII_CHAR_INDEX.get(character)));
                }
                ++height;
                if (width < row.length()) {
                    width = row.length();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("(Error) Could not read ASCII");
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                image.setRGB(x, y, ASCIIRowsConvertedToLuminance.get(y).get(x));
            }
        }
        File outputFile = new File(Logic.IMAGE_PATH + imageFilename);
        try {
            ImageIO.write(image, imageFormat, outputFile);
        } catch (IOException e) {
            throw new RuntimeException("(Error) Could not write image");
        }
    }

    public double colorToLuminance(int R, int G, int B) {
        // https://stackoverflow.com/a/596243, Rec. 709
        return (0.2126 * R + 0.7152 * G + 0.0722 * B);
    }

    public int luminanceToCharIndex(double Y) {
        return (int) (ASCII.length * Y / 255);
    }

    public int charIndexToLuminance(int I) {
        int Y = 255 / (ASCII.length - 1) * I;
        // R << 16 | G << 8 | B
        return 0xff000000 | (Y << 16) | (Y << 8) | Y;
    }
}
