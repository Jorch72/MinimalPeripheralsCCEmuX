package club.krist.minimalperipherals.ccemux.sound;

import club.krist.minimalperipherals.ccemux.ResourceIndex;

import java.io.File;

public class SoundSystem {
    public static SoundSystem instance;
    private ResourceIndex resourceIndex;

    public SoundSystem(File assetsFolder, String indexName) {
        resourceIndex = new ResourceIndex(assetsFolder, indexName);

        instance = this;
    }

    public void playSound(String sound, float pitch, float volume) {
        //TODO: implement
    }

    public boolean soundExists(String sound) {
        return resourceIndex.doesFileExist(sound);
    }
}
