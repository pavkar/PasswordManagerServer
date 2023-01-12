package ee.eid.utility;

import ee.eid.manager.Password;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class Convertor {
    public static byte[] convertIntToByteArray(int toConvert, int allocate) {
        byte[] bytes = ByteBuffer.allocate(allocate).putInt(toConvert).array();

//        for (byte b : bytes) {
//            System.out.format("0x%x ", b);
//        }

        return bytes;

    }

    public static String convertByteToHex(byte[] byteData) {
        StringBuilder toReturn = new StringBuilder();
        for (byte b : byteData) {
            toReturn.append(String.format("%02X ", b));
        }
        return toReturn.toString().replace(" ", "");
    }

    public static String convertByteToHexWithSpacing(byte[] byteData) {
        StringBuilder toReturn = new StringBuilder();
        for (byte b : byteData) {
            toReturn.append(String.format("%02X ", b));
        }
        return toReturn.toString();
    }

    public static void serializeDataOut(Password password)throws IOException {
        String fileName= password.getOrigin();
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(password);
        oos.close();
    }

    public static Password serializeDataIn(String origin) throws IOException, ClassNotFoundException {
        try {
            FileInputStream fin = new FileInputStream(origin);
            ObjectInputStream ois = new ObjectInputStream(fin);
            Password password= (Password) ois.readObject();
            ois.close();
            return password;
        } catch (FileNotFoundException e) {
            return null;
        }


    }

    public static void deleteFile(String origin) {
        try {
            Files.deleteIfExists(
                    Paths.get(origin));
        }
        catch (NoSuchFileException e) {
            System.out.println(
                    "No such file/directory exists");
        }
        catch (DirectoryNotEmptyException e) {
            System.out.println("Directory is not empty.");
        }
        catch (IOException e) {
            System.out.println("Invalid permissions.");
        }

        System.out.println("Deletion successful.");
    }
}
