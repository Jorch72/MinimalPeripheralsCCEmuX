package club.krist.minimalperipherals.ccemux.sound;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import club.krist.minimalperipherals.ccemux.OggInputStream;
import club.krist.minimalperipherals.ccemux.ResourceIndex;
import org.apache.commons.io.IOUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SoundSystem {
    public static SoundSystem instance;
    
    private ResourceIndex resourceIndex;

    public SoundSystem(File assetsFolder, String indexName) {
        resourceIndex = new ResourceIndex(assetsFolder, indexName);

        instance = this;
    }
    
    /**
     * Plays a sound.
     * @param sound {@link ResourceIndex Index} path to the sound to play
     * @param pitch The pitch to shift by, between 0.5 and 2.
     * @param volume The volume of the sound between 0 and 1.
     */
    public void playSound(String sound, float pitch, float volume) {
        //TODO: implement
        
        File soundFile = resourceIndex.getFile(sound);
        
        if (!soundFile.exists()) {
            throw new RuntimeException("Sound " + sound + " doesn't exist");
        }
    
        try (
            OggInputStream is = new OggInputStream(new FileInputStream(soundFile));
        ) {
            int sampleRate = is.getSampleRate();
            int channels = is.getChannels();
            
            AudioFormat oggFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sampleRate,
                16,
                channels,
                channels * 2,
                sampleRate,
                true
            );
            
            AudioDispatcher dsp = AudioDispatcherFactory.fromByteArray(
                IOUtils.toByteArray(is),
                oggFormat,
                1024,
                1024 / 2
            );
            
            AudioPlayer audioPlayer = new AudioPlayer(oggFormat);
            dsp.addAudioProcessor(audioPlayer);
            dsp.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean soundExists(String sound) {
        return resourceIndex.doesFileExist(sound);
    }
}
