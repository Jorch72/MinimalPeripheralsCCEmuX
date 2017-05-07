package club.krist.minimalperipherals.ccemux;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ResourceIndex {
    private final Map<String, File> resourceMap = new HashMap<>();

    public ResourceIndex(File assetsFolder, String indexName) {
        File file1 = new File(assetsFolder, "objects");
        File file2 = new File(assetsFolder, "indexes/" + indexName + ".json");
        try {
            JsonObject rootObject = new JsonParser().parse(Files.newReader(file2, Charsets.UTF_8)).getAsJsonObject();
            JsonObject objects = rootObject.get("objects").getAsJsonObject();

            if (objects != null) {
                for (Map.Entry<String, JsonElement> entry : objects.entrySet())
                {
                    JsonObject jsonobject = (JsonObject)entry.getValue();
                    String s = (String)entry.getKey();
                    String[] astring = s.split("/", 2);
                    String s1 = astring.length == 1 ? astring[0] : astring[0] + ":" + astring[1];
                    String s2 = jsonobject.get("hash").getAsString();
                    File file3 = new File(file1, s2.substring(0, 2) + "/" + s2);
                    this.resourceMap.put(s1, file3);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getFile(String name) {
        return this.resourceMap.get(name);
    }

    public boolean doesFileExist(String name) {
        return this.resourceMap.containsKey(name);
    }
}
