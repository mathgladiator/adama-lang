/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

public class GenerateLanguageTestsTests {
    @Test
    public void empty() throws Exception {
        File testdata = new File("./test_data");
        testdata.mkdir();
        File testdataCode = new File("./test_data/code1");
        testdataCode.mkdir();
        File javaOut = new File("./test_data/java-out1");
        javaOut.mkdir();
        GenerateLanguageTests.main(new String[] {"--input", "./test_data/code1", "--output", "./test_data/java-out1", "--what", "ok"});
        javaOut.delete();
        testdataCode.delete();
    }

    @Test
    public void something() throws Exception {
        File testdata = new File("./test_data");
        testdata.mkdir();
        File testdataCode = new File("./test_data/code2");
        testdataCode.mkdir();
        Files.writeString(new File(testdataCode, "Clazz_X_success.a").toPath(), "#sm {}");
        File javaOut = new File("./test_data/java-out2");
        javaOut.mkdir();
        GenerateLanguageTests.main(new String[] {"--input", "./test_data/code2", "--output", "./test_data/java-out2"});
        File testExists = new File(javaOut, "GeneratedClazzTests.java");
        Assert.assertTrue(testExists.exists());
        testExists.delete();
        javaOut.delete();
        new File(testdataCode, "Clazz_X_success.a").delete();
        testdataCode.delete();
    }
}
