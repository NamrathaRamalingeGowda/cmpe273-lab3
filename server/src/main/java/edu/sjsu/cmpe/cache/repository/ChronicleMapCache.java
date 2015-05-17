package edu.sjsu.cmpe.cache.repository;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import net.openhft.chronicle.map.ChronicleMapBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.sjsu.cmpe.cache.domain.Entry;

public class ChronicleMapCache implements CacheInterface {
    /** In-memory map cache. (Key, Value) -> (Key, Entry) */
    private final Map<Long, String> cMap;

    private static String getPID() {
        String processName =
                java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        Long l = Long.parseLong(processName.split("@")[0]);
        return l.toString();
    }

    static Map<Long, String> createChronicleMap(int port) {
        try {
            if (port == 3000) {
                File file = new File("src/main/resources/Map_A.dat");
                ChronicleMapBuilder<Long, String> builder = ChronicleMapBuilder.of(Long.class, String.class).entries(1000);
                return builder.createPersistedTo(file);
            }
            if (port == 3001) {
                File file = new File("src/main/resources/Map_B.dat");
                ChronicleMapBuilder<Long, String> builder = ChronicleMapBuilder.of(Long.class, String.class).entries(1000);
                return builder.createPersistedTo(file);
            }
            if (port == 3002) {
                File file = new File("src/main/resources/Map_C.dat");
                ChronicleMapBuilder<Long, String> builder = ChronicleMapBuilder.of(Long.class, String.class).entries(1000);
                return builder.createPersistedTo(file);
            }

            System.out.println("Invalid port!!" + port);
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public ChronicleMapCache(int port) {
        //System.out.println("Starting server " + args[2]);
        cMap = createChronicleMap(port);
    }

    @Override
    public Entry save(Entry newEntry) {
        checkNotNull(newEntry, "newEntry instance must not be null");
        System.out.println("Storing: " + newEntry.getValue() + " at " + newEntry.getKey());
        cMap.putIfAbsent(newEntry.getKey(), newEntry.getValue());

        return newEntry;
    }

    @Override
    public Entry get(Long key) {
        checkArgument(key > 0,
                "Key was %s but expected greater than zero value", key);
        Entry entry = new Entry();
        entry.setKey(key);
        entry.setValue(cMap.get(key));
        return entry;
    }

    @Override
    public List<Entry> getAll() {
        ArrayList<Entry> ale = new ArrayList<Entry> ();
        for (Long i : cMap.keySet()) {
            Entry e = new Entry();
            e.setValue(cMap.get(i));
            e.setKey(i);
            ale.add(e);
        }
        return ale;
    }
}