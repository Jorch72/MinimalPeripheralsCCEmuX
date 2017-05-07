package club.krist.minimalperipherals.ccemux.sound;

import be.tarsos.dsp.*;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import club.krist.minimalperipherals.ccemux.MinimalPluginConfig;
import club.krist.minimalperipherals.ccemux.OggInputStream;
import club.krist.minimalperipherals.ccemux.ResourceIndex;
import org.apache.commons.io.IOUtils;

import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class SoundSystem {
    public static SoundSystem instance;
    
    private ResourceIndex resourceIndex;
    private Map<String, Sound> soundCache = new HashMap<>();
    
    private int bufferSize = 1024;
    private int bufferOverlap = 1024 - 128;

    public SoundSystem(MinimalPluginConfig config, File assetsFolder, String indexName) {
        resourceIndex = new ResourceIndex(assetsFolder, indexName);

        instance = this;
        
        bufferSize = config.soundBufferSize;
        bufferOverlap = config.soundBufferOverlap;
    }
    
    /**
     * Plays a sound.
     * @param soundName {@link ResourceIndex Index} path to the sound to play
     * @param pitch The pitch to shift by, between 0.5 and 2.
     * @param volume The volume of the sound between 0 and 1.
     */
    public void playSound(String soundName, float pitch, float volume) {
        Sound sound;
        
        if (soundCache.containsKey(soundName)) {
            sound = soundCache.get(soundName);
        } else {
            sound = new Sound(soundName);
            soundCache.put(soundName, sound);
        }
        
        Thread audioThread = new Thread(() -> {
            try {
                AtomicReference<float[]> buffer = new AtomicReference<>();
                
                PitchShifter pitchShifter = new PitchShifter(pitch, sound.getFormat().getSampleRate(),
                    bufferSize, bufferOverlap
                );
                
                AudioDispatcher dsp = AudioDispatcherFactory.fromByteArray(
                    sound.getData(),
                    sound.getFormat(),
                    bufferSize,
                    bufferOverlap
                );
    
                dsp.addAudioProcessor(new AudioProcessor() {
                    @Override
                    public boolean process(AudioEvent audioEvent) {
                        buffer.set(audioEvent.getFloatBuffer());
                        return true;
                    }
    
                    @Override
                    public void processingFinished() {
        
                    }
                });
                dsp.addAudioProcessor(pitchShifter);
                dsp.addAudioProcessor(new GainProcessor(volume));
                dsp.addAudioProcessor(new AudioPlayer(sound.getFormat()));
                dsp.addAudioProcessor(new AudioProcessor() {
                    @Override
                    public void processingFinished() {
                        if (!dsp.isStopped()) {
                            dsp.stop();
                            
                            try {
                                Thread.currentThread().join();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
            
                    @Override
                    public boolean process(AudioEvent audioEvent) {
                        dsp.setStepSizeAndOverlap(bufferSize, bufferOverlap);
                        dsp.setAudioFloatBuffer(buffer.get());
                        audioEvent.setFloatBuffer(buffer.get());
                        audioEvent.setOverlap(bufferOverlap);
                        
                        return true;
                    }
                });
        
                dsp.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        
        audioThread.start();
    }

    public boolean soundExists(String sound) {
        return resourceIndex.doesFileExist(sound);
    }
    
    private class Sound {
        private int sampleRate;
        private int channels;
        private AudioFormat format;
        private byte[] data;
        
        public Sound(String soundName) {
            File soundFile = resourceIndex.getFile(soundName);
    
            if (!soundFile.exists()) {
                throw new RuntimeException("Sound " + soundName + " doesn't exist");
            }
    
            try (
                OggInputStream is = new OggInputStream(new FileInputStream(soundFile));
            ) {
                sampleRate = is.getSampleRate();
                channels = is.getChannels();
                format = new AudioFormat((float) sampleRate, 16, channels, true, false);
                data = IOUtils.toByteArray(is);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    
        public byte[] getData() {
            return data;
        }
    
        public AudioFormat getFormat() {
            return format;
        }
    }
}
