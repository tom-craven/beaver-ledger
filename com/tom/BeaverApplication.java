package com.tom;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BeaverApplication {

    private static final int TEST_SAMPLE_SIZE = 50;
    /**
     * Beaver Ledger fingerprints the data to identify missing logs,
     * validate the migrations integrity and provide a mechanism for recovery.
     *
     * @apiNote The test scenario
     * 1. Make a repository to simulate data that needs to be migrated
     * 2. make a unique signature for the migration
     * 3. Create a new ledger instance with the signature
     * 4. Populate the ledge by building logs with data repository
     * <p>
     * Some time later on the receiving end of the migration...
     * 5. Oh no.. We received the data but something is missing!
     * 6. Run the logs through a ledger instance using the same migration signature
     * 7. Any logs not ingested means something is lost
     * 8. We can retrieve the missing logs from the sender
     * 9. OR process the remaining subset to validate the remaining migration
     */
    public static void main(String[] args) {
        //1
        ArrayList<byte[]> dataRepository = new ArrayList<>();
        for (int i = 0; i < TEST_SAMPLE_SIZE; i++) {
            String uuid = UUID.randomUUID().toString();
            byte[] payload = uuid.getBytes(StandardCharsets.UTF_8);
            dataRepository.add(i, payload);
        }
        System.out.println("sample created.. size: " + dataRepository.size());
        //2
        String signature = "the unique signature for the migration";
        //3
        final BeaverLedger sender = new BeaverLedger(signature);
        //4
        dataRepository.forEach(sender::create);
        //5
        List<BeaverLog> migratedData = sender.getAll();
        migratedData.remove((migratedData.size() - 2));
        //6
        final BeaverLedger receiver = new BeaverLedger(signature);
        int pass = 0;
        while (!migratedData.isEmpty() && pass < 2) {
            migratedData.removeIf(receiver::read);
            pass++;
        }
        //7
        migratedData.sort(BeaverLog::compare);
        BeaverLog nextReceived = migratedData.iterator().next();
        System.out.println("\nthe chain is migration was broken between the receivers last: " + receiver.getLast() + " and next remaining in migration set" + nextReceived.getLastHash());
        //8
        Optional<BeaverLog> missingLog = sender.get(migratedData.iterator().next().getLastHash());
        missingLog.ifPresent(beaverLog -> System.out.println("missing log identified! " + beaverLog.toString()));
        //9
        final BeaverLedger receiveRemaining = new BeaverLedger(nextReceived.getLastHash());
        pass = 0;
        while (!migratedData.isEmpty() && pass < 2) {
            migratedData.removeIf(receiveRemaining::read);
            pass++;
        }
        System.out.println("The remain sample is " + (migratedData.isEmpty() ? "OK" : "ERRONEOUS"));
    }

}
