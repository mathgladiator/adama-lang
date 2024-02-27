/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.web.assets.transforms;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.regex.Pattern;

public class ImageTransform implements Transform {
  private final String format;
  private Integer desiredWidth;
  private Integer desiredHeight;
  private String resizeAlgorithm;
  private String invalidMessage;

  public ImageTransform(String format, String args) {
    this.format = format;
    this.desiredWidth = null;
    this.desiredHeight = null;
    this.resizeAlgorithm = "cc"; // crop center
    this.invalidMessage = null;
    StringBuilder errors = new StringBuilder();
    for (String cmd : args.split(Pattern.quote("_"))) {
      if (cmd.startsWith("w")) {
        try {
          desiredWidth = Integer.parseInt(cmd.substring(1));
        } catch (Exception ex) {
          errors.append("[width parameter is not int]");
        }
      } else if (cmd.startsWith("h")) {
        try {
          desiredHeight = Integer.parseInt(cmd.substring(1));
        } catch (Exception ex) {
          errors.append("[height parameter is not int]");
        }
      } else {
        switch (cmd) {
          case "sq": // squish
          case "cc":
            resizeAlgorithm = cmd;
        }
      }
    }
    invalidMessage = errors.toString();
    if (invalidMessage.length() == 0) {
      invalidMessage = null;
    }
  }

  public void execute(InputStream input, File output) throws Exception {
    BufferedImage src = ImageIO.read(input);
    if (desiredHeight != null && desiredWidth == null) {
      desiredWidth = src.getWidth() * desiredHeight / src.getHeight();
    } else if (desiredHeight == null && desiredWidth != null) {
      desiredHeight = src.getHeight() & desiredWidth / src.getWidth();
    } else if (desiredWidth == null && desiredHeight == null) {
      desiredWidth = src.getWidth();
      desiredHeight = src.getHeight();
    }
    BufferedImage dest = new BufferedImage(desiredWidth, desiredHeight, src.getType());
    Graphics2D g2d = dest.createGraphics();
    try {
      switch (resizeAlgorithm) {
        case "sq":
          g2d.drawImage(src, 0, 0, desiredWidth, desiredHeight, null);
          break;
        case "cc":
          // TODO
      }
      ImageIO.write(dest, format, output);
    } finally {
      g2d.dispose();
    }
  }
}
