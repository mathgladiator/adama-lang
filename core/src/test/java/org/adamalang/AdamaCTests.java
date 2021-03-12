/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang;

import org.adamalang.translator.env.CompilerOptions;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class AdamaCTests {
    @Test
    public void testCompile() throws Exception {
        AdamaC.main(new String[] { "translate", "--input", "./test_code/Demo_Bomb_success.a", "--add-search-path", "./test_code"});
        AdamaC.main(new String[] { "translate", "--input", "./test_code/Demo_Bomb_success.a", "--output", "./test_data/AGame.java", "--package", "foop"});
        File file = new File("./test_data/AGame.java");
        Assert.assertTrue(file.exists());
        file.exists();
        AdamaC.main(new String[] {});
    }

    @Test
    public void help() {
        AdamaC.help(System.out);
    }
}