package com.tom;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;


public class BeaverLedger {

    private final ConcurrentLinkedDeque<String> chain;
    private final ConcurrentHashMap<String, BeaverLog> pool;

    public BeaverLedger(String genesis) {
        this.pool = new ConcurrentHashMap<>();
        this.chain = new ConcurrentLinkedDeque<>();
        this.chain.add(genesis);
    }

    public void create(byte[] payload) {
        final BeaverLog created = new BeaverLog(payload, this.chain.getLast());
        this.chain.add(created.getHash());
        this.pool.put(created.getHash(), created);
    }

    public Boolean read(BeaverLog nextBeaverLog) {
        if (Objects.equals(
                nextBeaverLog.getLastHash(), chain.getLast()) &&
                Objects.equals(nextBeaverLog.getHash(), nextBeaverLog.calculateHash())) {
            chain.add(nextBeaverLog.getHash());
            pool.put(nextBeaverLog.getHash(), nextBeaverLog);
            return true;
        }
        return false;
    }

    public Optional<BeaverLog> get(String hash) {
        return Optional.ofNullable(pool.get(hash));
    }

    public List<BeaverLog> getAll() {
        ArrayList<BeaverLog> logs = new ArrayList<>();
        this.chain.forEach(s -> {
            if (get(s).isPresent()) {
                logs.add(get(s).get());
            }
        });
        return logs;
    }

    public String getLast() {
        return this.chain.getLast();
    }
}
