/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.common;

import org.adamalang.translator.parser.token.Token;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** Defines a position within a document. Usually, this is a construct within
 * the document */
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

  /** ingest the tokens and the bounds of the tokens
   *
   * @param tokens an array of tokens */
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

  /** write the error out into the given ObjectNode using the LSP format */
  public void writeAsLanguageServerDiagnostic(final ObjectNode range) {
    final var start = range.putObject("start");
    start.put("line", startLineIndex);
    start.put("character", startLinePosition);
    final var end = range.putObject("end");
    end.put("line", endLineIndex);
    end.put("character", endLinePosition);
  }
}
