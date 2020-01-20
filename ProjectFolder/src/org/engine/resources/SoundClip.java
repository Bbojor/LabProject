package org.engine.resources;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *  This class provides the required functionality for using sounds and music.
 *  Only tested for compatibility with .wav sound files
 */
public class SoundClip
{
    private Clip clip;
    private FloatControl gainControl;

    /**
     *  Creates a SoundClip from the .wav audio file at the given path. If reading/processing the given file failed,
     *  an empty sound clip is created
     * @param path path relative to the game folder
     */
    public SoundClip (String path)
    {
        try
        {
            InputStream audioSrc = SoundClip.class.getResourceAsStream(path);
            InputStream bufferedInput = new BufferedInputStream(audioSrc);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedInput);

            //decode format
            AudioFormat baseFormat = ais.getFormat();

            AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,baseFormat.getSampleRate(),16,
                    baseFormat.getChannels(),baseFormat.getChannels()*2,baseFormat.getSampleRate(),false);

            AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat,ais);

            clip = AudioSystem.getClip();
            clip.open(dais);

            gainControl= (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);

        }
        catch(IOException | UnsupportedAudioFileException | LineUnavailableException e)
        {
            clip = null;
            gainControl = null;
        }
    }

    /**
     *  Plays the sound. Start from beginning if it is already being played
     */
    public void play()
    {
        if(clip == null) return;

        stop();
        clip.setFramePosition(0);

        while(!clip.isRunning()) //make sure the clip eventually starts in case something goes wrong in the start method
        {
            clip.start();
        }
    }

    /**
     * Stops the sound if it is currently being played
     */
    public void stop()
    {
        if(clip.isRunning())
            clip.stop();
    }

    /**
     * Closes the SoundCLip object
     */
    public void close()
    {
        stop();
        clip.close();
    }

    /**
     * Plays the sound in  a continuous loop
     */
    public void loop()
    {
        if(!clip.isRunning())
        {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            play();
        }
    }

    /**
     *  Adjusts the volume for the SoundClip. Negative values lower the volume, while positive values increase it.
     *  Setting the value to 0 changes nothing. Accounts for exceeding the maximum value
     * @param value desired change in volume
     */
    public void setVolume(float value)
    {
        if(gainControl == null)
            return;

        if(value > gainControl.getMaximum())
            gainControl.setValue(gainControl.getMaximum());
        else
        if(value < gainControl.getMinimum())
            gainControl.setValue(gainControl.getMinimum());
        else
            gainControl.setValue(value);
    }

    /**
     * Checks whether the clip is currently being played
     *
     */
    public boolean isRunning()
    {
        return clip.isRunning();
    }

}