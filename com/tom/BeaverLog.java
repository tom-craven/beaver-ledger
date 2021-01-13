package com.tom;

import java.util.Objects;

public class BeaverLog {

    private final byte[] payload;
    private final String lastHash;
    private final String hash;

    public BeaverLog(byte[] payload, String lastHash) {
        this.payload = payload;
        this.lastHash = lastHash;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return HashUtils.doHash(payload, lastHash);
    }

    public String getLastHash() {
        return lastHash;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "BeaverLog{" +
                ", lastHash='" + lastHash + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }

    protected static int compare(BeaverLog o1, BeaverLog o2) {
        return Objects.equals(o1.getHash(), o2.getLastHash()) ? 1 : 0;
    }
}
