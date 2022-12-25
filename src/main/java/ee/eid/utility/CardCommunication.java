package ee.eid.utility;

import lombok.extern.slf4j.Slf4j;
import net.sf.scuba.smartcards.*;
import org.jmrtd.PassportService;
import org.jmrtd.protocol.SecureMessagingAPDUSender;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
public class CardCommunication {

    public static Optional<CardTerminal> getTerminal() {
        CardTerminal cardTerminal = chooseTerminal();
        if (cardTerminal != null) {
            return Optional.of(cardTerminal);
        } else {
            log.error("No terminals found!");
        }
        return Optional.empty();

    }

    private static CardTerminal chooseTerminal() {
        List<CardTerminal> terminals;

        try {
            terminals = getTerminalList();
        } catch (CardException e) {
            log.error("Error while checking for terminals");
            return null;
        }

        CardTerminal cardTerminal = null;
        switch (terminals.size()) {
            case 0 -> log.error("No terminal");
            case 1 -> cardTerminal = terminals.get(0);
            default -> cardTerminal = chooseTerminalSeveral(terminals);
        }
        return cardTerminal;
    }

    private static List<CardTerminal> getTerminalList() throws CardException {
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = factory.terminals().list();

        for (int count = 0; count < terminals.size(); count++) {
            log.info("Terminal " + count + ": " + terminals.get(count));
        }

        return terminals;
    }

    private static CardTerminal chooseTerminalSeveral(List<CardTerminal> terminals) {
        Scanner sc = new Scanner(System.in);
        int usedTerminal;
        do {
            log.info("Enter terminal number: ");
            while (!sc.hasNextInt()) sc.next();
            usedTerminal = sc.nextInt();
            if (usedTerminal >= terminals.size() || usedTerminal < 0) {
                log.error("No such terminal");
            }
        } while (usedTerminal >= terminals.size() || usedTerminal < 0);
        return terminals.get(usedTerminal);
    }
}
