package hapibd;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import blbutil.Utilities;

public class SplitFileWriterManager implements AutoCloseable {
    private final String outputPrefix;
    private final String splitFilename;

    private final Map<Integer, PrintWriter> ibdWriters;
    private final Map<Integer, PrintWriter> hbdWriters;

    public SplitFileWriterManager(HapIbdPar par) {
        this.outputPrefix = par.out();
        this.splitFilename = par.splitFilename();
        this.ibdWriters = new ConcurrentHashMap<>();
        this.hbdWriters = new ConcurrentHashMap<>();
    }

    public PrintWriter getWriterForProxyKey(int proxyKey, String type) {
        Map<Integer, PrintWriter> writers = type.equals("ibd") ? ibdWriters : hbdWriters;
        return writers.computeIfAbsent(proxyKey, key -> {
            try {
                String dirPath = outputPrefix + "/" + key;
                File dir = new File(dirPath);
                if (!dir.mkdirs() && !dir.isDirectory()) {
                    Utilities.exit("ERROR: Failed to create directory " + dirPath);
                }
                String filename = dirPath + "/" + splitFilename + "." + type;
                return new PrintWriter(new File(filename));
            }
            catch (IOException e) {
                Utilities.exit("ERROR creating " + type + " file for proxy key " + key + ": ", e);
                return null; // This will never be reached due to Utilities.exit
            }
        });
    }

    public synchronized void close() {
        for (PrintWriter writer : ibdWriters.values()) {
            writer.close();
        }
        for (PrintWriter writer : hbdWriters.values()) {
            writer.close();
        }
    }
}
