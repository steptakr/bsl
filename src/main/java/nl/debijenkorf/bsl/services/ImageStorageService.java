package nl.debijenkorf.bsl.services;

import java.io.InputStream;

/**
 * Created by Daniel on 18/04/15.
 *
 * This interface contains all necessary methods to manage image on the storage
 */
public interface ImageStorageService {

    InputStream getImage (String folder, String filename);

    void saveImage (String folder, String filename, InputStream image, Long contentLength);

    void flushObject (String folder, String filename);

}