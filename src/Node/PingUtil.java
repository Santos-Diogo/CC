package Node;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PingUtil {
    
    private double rtt;

    public PingUtil(String host) throws Exception
    {
        Process process = new ProcessBuilder("ping", "-c", "10", "-i", "0,01", host).start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            // Parse the output to get round-trip time
            if (line.contains("time=")) {
                int startIndex = line.indexOf("time=") + 5;
                int endIndex = line.indexOf(" ms", startIndex);
                String rttString = line.substring(startIndex, endIndex);
                this.rtt = Double.parseDouble(rttString);
            }
        }
    }

    public double getRtt() {
        return rtt;
    }
}