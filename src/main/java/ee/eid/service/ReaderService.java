package ee.eid.service;

import ee.eid.utility.CardCommunication;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.scuba.smartcards.*;
import org.jmrtd.PassportService;
import org.jmrtd.protocol.SecureMessagingAPDUSender;

import javax.smartcardio.CardTerminal;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Getter
@Setter
public class ReaderService {

    private PassportService passportService;
    private SecureMessagingAPDUSender secureMessagingAPDUSender;

    public ReaderService() throws CardServiceException {
        createPassportService();
    }

    private void createPassportService() throws CardServiceException {
        Optional<CardTerminal> cardTerminalOptional = CardCommunication.getTerminal();

        if (cardTerminalOptional.isPresent()) {
            CardTerminal cardTerminal = cardTerminalOptional.get();
            CardService cardService = CardService.getInstance(cardTerminal);
            PassportService passportService = new PassportService(cardService,
                    256, 256, true, false);

            passportService.open();

            this.passportService = passportService;
            this.secureMessagingAPDUSender = new SecureMessagingAPDUSender(passportService);
        }

    }

    public ResponseAPDU transmit(CommandAPDU commandAPDU, APDUWrapper wrapper) throws CardServiceException {
        if (passportService.isOpen()) {
            ResponseAPDU responseAPDU = secureMessagingAPDUSender.transmit(wrapper, commandAPDU);

            log.info("Response: " + Arrays.toString(responseAPDU.getBytes()));

            return responseAPDU;
        }
        return null;
    }
}
