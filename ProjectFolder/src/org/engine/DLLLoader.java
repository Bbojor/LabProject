package org.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *  This class handles the loading of the required native files for the JOGL libraries
 */
public class DLLLoader
{
    private static void loadJarDll(String name,String path) throws IOException
    {
        InputStream in = DLLLoader.class.getResourceAsStream(path + name);

        byte[] buffer = new byte[1024];
        int read;
        File temp = new File(System.getProperty("java.io.tmpdir"),name);
        FileOutputStream fos = new FileOutputStream(temp);

        while((read = in.read(buffer)) != -1) {
            fos.write(buffer, 0, read);
        }
        fos.close();
        in.close();

        System.load(temp.getAbsolutePath());
        System.out.println("Lodaded " + name);
    }

    /**
     *  Load required files based on system/architecture (currently only supports windows)
     */
    public static void run() throws IOException
    {

        try //loading 32 bit libraries
        {
            loadJarDll("gluegen-rt.dll","/natives/windows-i586/");
        }
        catch(UnsatisfiedLinkError e)
        {   //if that fails try load 64-bit libraries
            loadJarDll("gluegen-rt.dll","/natives/windows-amd64/");
        }

         try //loading 32 bit libraries
         {
             loadJarDll("nativewindow_awt.dll","/natives/windows-i586/");
         }
         catch(UnsatisfiedLinkError | IOException e)
         {   //if that fails try load 64-bit libraries
             loadJarDll("nativewindow_awt.dll","/natives/windows-amd64/");
         }

         try //loading 32 bit libraries
         {
             loadJarDll("newt.dll","/natives/windows-i586/");
         }
         catch(UnsatisfiedLinkError | IOException e)
         {   //if that fails try load 64-bit libraries
             loadJarDll("newt.dll","/natives/windows-amd64/");
         }

    }
}
