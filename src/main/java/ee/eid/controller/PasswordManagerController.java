package ee.eid.controller;

import ee.eid.manager.Password;
import ee.eid.service.ReaderService;
import ee.eid.utility.Convertor;
import net.sf.scuba.smartcards.CardServiceException;
import net.sf.scuba.smartcards.CommandAPDU;
import net.sf.scuba.smartcards.ResponseAPDU;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class PasswordManagerController {
    private ReaderService readerService;

    public PasswordManagerController() throws CardServiceException {
        readerService = new ReaderService();
    }

    @PostMapping("/passwords/verify")
    String verifyPIN(@RequestBody String userPIN) throws CardServiceException {
        // Part in byte array is AID of the applet. Should be customizable.
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0xA4, 0x04, 0x00,
                new byte[] {0x31, 0x32, 0x33, 0x34, 0x35}, 0x00);
        // Select Applet
        readerService.transmit(commandAPDU, null);

        byte[] userPINArray = Convertor.convertIntToByteArray(Integer.parseInt(userPIN), 4);

        commandAPDU = new CommandAPDU(0x00, 0x50, 0x00, 0x00,
                userPINArray, 0x00);

        // Perform Verify user PIN
        ResponseAPDU responseAPDU = readerService.transmit(commandAPDU, null);

        if (responseAPDU.getSW() == 0x9000) {
            return "Verified";
        }

        return "Not verified";
    }

    @PostMapping("/passwords/change")
    String changePIN(@RequestBody String newPIN) throws CardServiceException {
        // Part in byte array is AID of the applet. Should be customizable.
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0xA4, 0x04, 0x00,
                new byte[] {0x31, 0x32, 0x33, 0x34, 0x35}, 0x00);
        // Select Applet
        readerService.transmit(commandAPDU, null);

        byte[] newPINArray = Convertor.convertIntToByteArray(Integer.parseInt(newPIN), 4);

        commandAPDU = new CommandAPDU(0x00, 0x51, 0x00, 0x00,
                newPINArray, 0x00);

        // Perform Change user PIN
        ResponseAPDU responseAPDU = readerService.transmit(commandAPDU, null);

        if (responseAPDU.getSW() == 0x9000) {
            return "New PIN set up";
        }

        return "New PIN NOT set up";
    }

    @PostMapping("/passwords/reset")
    String resetPIN(@RequestBody String adminPIN) throws CardServiceException {
        // Part in byte array is AID of the applet. Should be customizable.
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0xA4, 0x04, 0x00,
                new byte[] {0x31, 0x32, 0x33, 0x34, 0x35}, 0x00);
        // Select Applet
        readerService.transmit(commandAPDU, null);

        byte[] adminPINArray = Convertor.convertIntToByteArray(Integer.parseInt(adminPIN), 5);

        commandAPDU = new CommandAPDU(0x00, 0x52, 0x00, 0x00,
                adminPINArray, 0x00);

        // Perform Reset user PIN
        ResponseAPDU responseAPDU = readerService.transmit(commandAPDU, null);

        if (responseAPDU.getSW() == 0x9000) {
            return "User PIN reset successful";
        }

        return "User PIN reset failed";
    }

    @PostMapping("/passwords/add")
    String savePassword(@RequestBody Password newPassword) throws CardServiceException, IOException {

        // Part in byte array is AID of the applet. Should be customizable.
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0xA4, 0x04, 0x00,
                new byte[] {0x31, 0x32, 0x33, 0x34, 0x35}, 0x00);
        // Select Applet
        readerService.transmit(commandAPDU, null);

        commandAPDU = new CommandAPDU(0x00, 0x53, 0x00, 0x00,
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


    @GetMapping("/passwords/{origin}")
    String getPassword(@PathVariable String origin) throws CardServiceException, IOException, ClassNotFoundException {
        // Part in byte array is AID of the applet. Should be customizable.
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0xA4, 0x04, 0x00,
                new byte[] {0x31, 0x32, 0x33, 0x34, 0x35}, 0x00);
        // Select Applet
        readerService.transmit(commandAPDU, null);

        Password password = Convertor.serializeDataIn(origin);

        if (password != null) {
            commandAPDU = new CommandAPDU(0x00, 0x54, 0x00, 0x00,
                    password.getPosition(), 0xFF);
        } else {
            return "";
        }

        // Perform Save Password
        ResponseAPDU responseAPDU = readerService.transmit(commandAPDU, null);

        if (responseAPDU.getSW() == 0x9000) {
            return new String(responseAPDU.getData());
        }

        return "";
    }

    @DeleteMapping("/passwords/{origin}")
    String deletePassword(@PathVariable String origin) throws CardServiceException, IOException, ClassNotFoundException {
        // Part in byte array is AID of the applet. Should be customizable.
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0xA4, 0x04, 0x00,
                new byte[] {0x31, 0x32, 0x33, 0x34, 0x35}, 0x00);
        // Select Applet
        readerService.transmit(commandAPDU, null);

        Password password = Convertor.serializeDataIn(origin);

        if (password != null) {
            commandAPDU = new CommandAPDU(0x00, 0x55, 0x00, 0x00,
                    password.getPosition(), 0xFF);
        } else {
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
