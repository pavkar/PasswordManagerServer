package ee.eid;

import ee.eid.manager.Password;
import ee.eid.service.ReaderService;
import ee.eid.utility.Convertor;
import lombok.extern.slf4j.Slf4j;
import net.sf.scuba.smartcards.CardServiceException;
import net.sf.scuba.smartcards.CommandAPDU;
import net.sf.scuba.smartcards.ResponseAPDU;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class Testing {
    public static void main(String[] args) throws CardServiceException, IOException, ClassNotFoundException {
        ReaderService readerService = new ReaderService();

        // Part in byte array is AID of the applet. Should be customizable.
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0xA4, 0x04, 0x00,
                new byte[] {0x31, 0x32, 0x33, 0x34, 0x35}, 0x00);
        // Select Applet
        readerService.transmit(commandAPDU, null);

        // Test verify PIN functionality
        log.info("\n Verify PIN functionality");
        verifyPINTest("0000", readerService);

        // Test change PIN functionality
        log.info("\n Change PIN functionality");
        changePINTest("1234", readerService);
        verifyPINTest("0000", readerService);
        verifyPINTest("1234", readerService);
        changePINTest("0000", readerService);
        verifyPINTest("0000", readerService);

        // Test reset functionality
        log.info("\n Reset functionality");
        changePINTest("1234", readerService);
        verifyPINTest("1234", readerService);
        resetPINTest("00000", readerService);
        verifyPINTest("1234", readerService);
        verifyPINTest("0000", readerService);

        // Test save password functionality
        log.info("\n Save password functionality");
        Password newPassword = new Password("Youtube", "YoutubePW");
        Password newPassword2 = new Password("Facebook", "FacebookPW");
        savePasswordTest(newPassword, readerService);
        savePasswordTest(newPassword2, readerService);

        // Test get password functionality
        log.info("\n Get password functionality");
        getPasswordTest("Facebook", readerService);
        getPasswordTest("Youtube", readerService);

        // Test delete password functionality
        log.info("\n Delete password functionality");
        deletePasswordTest("Facebook", readerService);
        getPasswordTest("Facebook", readerService);
        getPasswordTest("Youtube", readerService);
        deletePasswordTest("Youtube", readerService);
        getPasswordTest("Youtube", readerService);
    }

    private static String verifyPINTest(String userPIN, ReaderService readerService) throws CardServiceException {
        byte[] userPINArray = Convertor.convertIntToByteArray(Integer.parseInt(userPIN), 4);

        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x50, 0x00, 0x00,
                userPINArray, 0x00);

        // Perform Verify user PIN
        ResponseAPDU responseAPDU = readerService.transmit(commandAPDU, null);

        if (responseAPDU.getSW() == 0x9000) {
            return "Verified";
        }

        return "Not verified";
    }

    private static String changePINTest(String newPIN, ReaderService readerService) throws CardServiceException {
        byte[] newPINArray = Convertor.convertIntToByteArray(Integer.parseInt(newPIN), 4);

        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x51, 0x00, 0x00,
                newPINArray, 0x00);

        // Perform Change user PIN
        ResponseAPDU responseAPDU = readerService.transmit(commandAPDU, null);

        if (responseAPDU.getSW() == 0x9000) {
            return "New PIN set up";
        }

        return "New PIN NOT set up";

    }

    private static String resetPINTest(String adminPIN, ReaderService readerService) throws CardServiceException {
        byte[] adminPINArray = Convertor.convertIntToByteArray(Integer.parseInt(adminPIN), 5);

        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x52, 0x00, 0x00,
                adminPINArray, 0x00);

        // Perform Reset user PIN
        ResponseAPDU responseAPDU = readerService.transmit(commandAPDU, null);

        if (responseAPDU.getSW() == 0x9000) {
            return "User PIN reset successful";
        }

        return "User PIN reset failed";
    }

    private static String savePasswordTest(Password newPassword, ReaderService readerService) throws CardServiceException, IOException {

        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x53, 0x00, 0x00,
                newPassword.getData(), 0xFF);

        // Perform Save Password
        ResponseAPDU responseAPDU = readerService.transmit(commandAPDU, null);

        if (responseAPDU.getSW() == 0x9000) {
            newPassword.setPosition(responseAPDU.getData());
            Convertor.serializeDataOut(newPassword);

            return "Password saved to " + Convertor.convertByteToHex(responseAPDU.getData());
        }

        return "Password not saved";
    }


    private static String getPasswordTest(String origin, ReaderService readerService) throws CardServiceException, IOException, ClassNotFoundException {
        Password password = Convertor.serializeDataIn(origin);
        CommandAPDU commandAPDU;

        if (password != null) {
            commandAPDU = new CommandAPDU(0x00, 0x54, 0x00, 0x00,
                    password.getPosition(), 0xFF);
        } else {
            log.error("No such saved data.");
            return "";
        }


        // Perform Save Password
        ResponseAPDU responseAPDU = readerService.transmit(commandAPDU, null);

        if (responseAPDU.getSW() == 0x9000) {
            return new String(responseAPDU.getData());
        }

        return "";
    }

    private static String deletePasswordTest(String origin, ReaderService readerService) throws CardServiceException, IOException, ClassNotFoundException {
        Password password = Convertor.serializeDataIn(origin);

        CommandAPDU commandAPDU;

        if (password != null) {
            commandAPDU = new CommandAPDU(0x00, 0x55, 0x00, 0x00,
                    password.getPosition(), 0xFF);
        } else {
            log.error("No such saved data.");
            return "";
        }



        // Perform Delete Password
        ResponseAPDU responseAPDU = readerService.transmit(commandAPDU, null);

        if (responseAPDU.getSW() == 0x9000) {
            Convertor.deleteFile(origin);
            return new String(responseAPDU.getData());
        }

        return "";
    }
}
