package ee.eid.controller;

import ee.eid.manager.Password;
import ee.eid.service.ReaderService;
import ee.eid.utility.Convertor;
import net.sf.scuba.smartcards.CardServiceException;
import net.sf.scuba.smartcards.CommandAPDU;
import net.sf.scuba.smartcards.ResponseAPDU;
import org.springframework.web.bind.annotation.*;

@RestController
public class PasswordManagerController {
    private ReaderService readerService;

    public PasswordManagerController() throws CardServiceException {
        readerService = new ReaderService();
    }

    @PostMapping("/passwords/verify")
    private String verifyPIN(@RequestBody int userPIN) throws CardServiceException {
        byte[] userPINArray = Convertor.convertIntToByteArray(userPIN);

        // Part in byte array is AID of the applet. Should be customizable.
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0xA4, 0x04, 0x00,
                new byte[] {0x31, 0x32, 0x33, 0x34, 0x35}, 0x00);
        // Select Applet
        readerService.transmit(commandAPDU, null);

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
    private String changePIN(@RequestBody int newPIN) throws CardServiceException {
        byte[] newPINArray = Convertor.convertIntToByteArray(newPIN);

        // Part in byte array is AID of the applet. Should be customizable.
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0xA4, 0x04, 0x00,
                new byte[] {0x31, 0x32, 0x33, 0x34, 0x35}, 0x00);
        // Select Applet
        readerService.transmit(commandAPDU, null);

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
    private String resetPIN(@RequestBody int adminPIN) throws CardServiceException {
        byte[] adminPINArray = Convertor.convertIntToByteArray(adminPIN);

        // Part in byte array is AID of the applet. Should be customizable.
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0xA4, 0x04, 0x00,
                new byte[] {0x31, 0x32, 0x33, 0x34, 0x35}, 0x00);
        // Select Applet
        readerService.transmit(commandAPDU, null);

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
    private String savePassword(@RequestBody Password newPassword) throws CardServiceException {

        // Part in byte array is AID of the applet. Should be customizable.
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0xA4, 0x04, 0x00,
                new byte[] {0x31, 0x32, 0x33, 0x34, 0x35}, 0x00);
        // Select Applet
        readerService.transmit(commandAPDU, null);

        commandAPDU = new CommandAPDU(0x00, 0x53, 0x00, 0x00,
                newPassword.data, 0x00);

        // Perform Save Password
        ResponseAPDU responseAPDU = readerService.transmit(commandAPDU, null);

        if (responseAPDU.getSW() == 0x9000) {
            return "Password saved to " + responseAPDU.getData();
        }

        return "Password not saved";
    }


    @GetMapping("/passwords/{position}")
    private byte[] getPassword(@PathVariable int position) throws CardServiceException {
        byte[] positionArray = Convertor.convertIntToByteArray(position);

        // Part in byte array is AID of the applet. Should be customizable.
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0xA4, 0x04, 0x00,
                new byte[] {0x31, 0x32, 0x33, 0x34, 0x35}, 0x00);
        // Select Applet
        readerService.transmit(commandAPDU, null);

        commandAPDU = new CommandAPDU(0x00, 0x54, 0x00, 0x00,
                positionArray, 0x00);

        // Perform Save Password
        ResponseAPDU responseAPDU = readerService.transmit(commandAPDU, null);

        if (responseAPDU.getSW() == 0x9000) {
            return responseAPDU.getData();
        }

        return new byte[0];
    }

    @DeleteMapping("/passwords/{position}")
    private byte[] deletePassword(@PathVariable int position) throws CardServiceException {
        byte[] positionArray = Convertor.convertIntToByteArray(position);

        // Part in byte array is AID of the applet. Should be customizable.
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0xA4, 0x04, 0x00,
                new byte[] {0x31, 0x32, 0x33, 0x34, 0x35}, 0x00);
        // Select Applet
        readerService.transmit(commandAPDU, null);

        commandAPDU = new CommandAPDU(0x00, 0x55, 0x00, 0x00,
                positionArray, 0x00);

        // Perform Delete Password
        ResponseAPDU responseAPDU = readerService.transmit(commandAPDU, null);

        if (responseAPDU.getSW() == 0x9000) {
            return responseAPDU.getData();
        }

        return new byte[0];
    }

}
