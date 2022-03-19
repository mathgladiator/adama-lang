/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.common;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.parser.token.MajorTokenType;
import org.adamalang.translator.parser.token.Token;

/** Defines a position within a document. Usually, this is a construct within the document */
public class DocumentPosition {
  public static final DocumentPosition ZERO = new DocumentPosition().ingest(0, 0);
  private int endLineIndex;
  private int endLinePosition;
  private int startLineIndex;
  private int startLinePosition;

  /** initialize with a non-sense position */
  public DocumentPosition() {
    startLineIndex = Integer.MAX_VALUE;
    startLinePosition = Integer.MAX_VALUE;
    endLineIndex = 0;
    endLinePosition = 0;
  }

  /** convert the document position to a token with an identifier type */
  public Token asIdentiferToken(String sourceName, String name) {
    return new Token(sourceName, name, MajorTokenType.Identifier, null, startLineIndex, startLinePosition,  endLineIndex, endLinePosition);
  }

  /** aggregate the positions together */
  public static DocumentPosition sum(DocumentPosition... positions) {
    DocumentPosition result = new DocumentPosition();
    for (DocumentPosition position : positions) {
      if (position != null) {
        result.ingest(position);
      }
    }
    return result;
  }

  /** @param other another document position to ingest */
  public DocumentPosition ingest(final DocumentPosition other) {
    if (other != null) {
      ingest(other.startLineIndex, other.startLinePosition);
      ingest(other.endLineIndex, other.endLinePosition);
    }
    return this;
  }

  /** ingest the given (line, position) pair */
  public DocumentPosition ingest(final int line, final int position) {
    if (line < startLineIndex) {
      startLineIndex = line;
      startLinePosition = position;
    } else if (line == startLineIndex && position < startLinePosition) {
      startLinePosition = position;
    }
    if (line > endLineIndex) {
      endLineIndex = line;
      endLinePosition = position;
    } else if (line == endLineIndex && position > endLinePosition) {
      endLinePosition = position;
    }
    return this;
  }

  /**
   * ingest the tokens and the bounds of the tokens
   * @param tokens an array of tokens
   */
  public DocumentPosition ingest(final Token... tokens) {
    if (tokens != null) {
      for (final Token token : tokens) {
        if (token != null) {
          ingest(token.lineStart, token.charStart);
          ingest(token.lineEnd, token.charEnd);
        }
      }
    }
    return this;
  }

  public void reset() {
    startLineIndex = Integer.MAX_VALUE;
    startLinePosition = Integer.MAX_VALUE;
    endLineIndex = 0;
    endLinePosition = 0;
  }

  /** return the position as trailing arguments */
  public String toArgs(final boolean first) {
    final var sb = new StringBuilder();
    if (!first) {
      sb.append(", ");
    }
    sb.append(startLineIndex);
    sb.append(", ").append(startLinePosition);
    sb.append(", ").append(endLineIndex);
    sb.append(", ").append(endLinePosition);
    return sb.toString();
  }

  public void dump(JsonStreamWriter writer) {
    writer.beginObject();

    writer.writeObjectFieldIntro("start");
    writer.beginObject();
    writer.writeObjectFieldIntro("line");
    writer.writeInteger(startLineIndex);
    writer.writeObjectFieldIntro("character");
    writer.writeInteger(startLinePosition);
    writer.endObject();

    writer.writeObjectFieldIntro("end");
    writer.beginObject();
    writer.writeObjectFieldIntro("line");
    writer.writeInteger(endLineIndex);
    writer.writeObjectFieldIntro("character");
    writer.writeInteger(endLinePosition);
    writer.endObject();

    writer.endObject();
  }
}
