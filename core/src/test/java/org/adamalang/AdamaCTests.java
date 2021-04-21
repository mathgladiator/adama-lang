/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
