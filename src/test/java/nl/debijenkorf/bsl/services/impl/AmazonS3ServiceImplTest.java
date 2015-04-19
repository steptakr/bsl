package nl.debijenkorf.bsl.services.impl;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * Created by Daniel on 18/04/15.
 *
 *
 */
public class AmazonS3ServiceImplTest {

    @Test
    public void testPath1CharFilename() throws Exception {
        String filename = "a.jpg";
        String folder   = "thumbnail";
        String expectedResult = "thumbnail/a.jpg";

        test(filename, folder, expectedResult);
    }

    @Test
    public void testPath3CharFilename() throws Exception {
        String filename = "123.jpg";
        String folder   = "thumbnail";
        String expectedResult = "thumbnail/123.jpg";

        test(filename, folder, expectedResult);
    }

    @Test
    public void testPath4CharFilename() throws Exception {
        String filename = "a234.jpg";
        String folder   = "thumbnail";
        String expectedResult = "thumbnail/a234/a234.jpg";

        test(filename, folder, expectedResult);
    }

    @org.junit.Test
    public void testPath5CharFilename() throws Exception {
        String filename = "a2345.jpg";
        String folder   = "thumbnail";
        String expectedResult = "thumbnail/a234/a2345.jpg";

        test(filename, folder, expectedResult);
    }

    @Test
    public void testPath7CharFilename() throws Exception {
        String filename = "1234567.jpg";
        String folder   = "thumbnail";
        String expectedResult = "thumbnail/1234/1234567.jpg";

        test(filename, folder, expectedResult);
    }

    @Test
    public void testPath8CharFilename() throws Exception {
        String filename = "a2345678.jpg";
        String folder   = "thumbnail";
        String expectedResult = "thumbnail/a234/5678/a2345678.jpg";

        test(filename, folder, expectedResult);
    }

    @Test
    public void testPath9CharFilename() throws Exception {
        String filename = "123456789.jpg";
        String folder   = "thumbnail";
        String expectedResult = "thumbnail/1234/5678/123456789.jpg";

        test(filename, folder, expectedResult);
    }

    @Test
    public void testPathLongFilename() throws Exception {
        String filename = "abcdefghijklmnopqrstuvwxyz1234567890.jpg";
        String folder   = "thumbnail";
        String expectedResult = "thumbnail/abcd/efgh/abcdefghijklmnopqrstuvwxyz1234567890.jpg";

        test(filename, folder, expectedResult);
    }

    @Test
    public void testPathLongFilenameWithForwardSlashes() throws Exception {
        String filename = "ab/c/def/ghi/jklmno/pqrst/uvwxyz123456789/0.jpg";
        String folder   = "thumbnail";
        String expectedResult = "thumbnail/ab_c/_def/ab_c_def_ghi_jklmno_pqrst_uvwxyz123456789_0.jpg";

        test(filename, folder, expectedResult);
    }

    @Test(expected = RuntimeException.class)
    public void testPathInvalidFilename() throws Exception {
        String filename = "a";
        String folder   = "thumbnail";

        test(filename, folder, null);
    }

    private void test(String filename, String folder, String expectedResult) {
        AmazonS3ServiceImpl testInstance = new AmazonS3ServiceImpl();
        assertEquals(expectedResult, testInstance.getPath(folder, filename));
    }


}