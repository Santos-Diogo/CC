package FSTracker;
import java.util.HashMap;
import java.util.Map;


public class Store_Hash {
    public int hash(String fileName) {
        int keyValue = fileName.hashCode(); // Corrected from filename.hashCode()

        if (keyValue < 0) {
            keyValue = -keyValue;
        }

        return keyValue;
    }

    public static void main(String[] args) {
        Store_Hash storeHash = new Store_Hash();
        String fileName = "exemplo.txt";
        int key = storeHash.hash(fileName);

        // Now you have the key, and you can use it to store and retrieve data
        storeKeyClient client = new storeKeyClient();

        // Storing data
        String fileData = "This is the content of the file."; // Replace with actual data
        client.put(key, fileData);

        // Retrieving data
        String retrievedData = client.get(key);
        System.out.println("Retrieved Data: " + retrievedData);
    }
}

class storeKeyClient {
    private Map<Integer, String> keyValueStore;

    public storeKeyClient() {
        keyValueStore = new HashMap<>();
    }

    public void put(int key, String value) {
        keyValueStore.put(key, value);
    }

    public String get(int key) {
        return keyValueStore.get(key);
    }
}


