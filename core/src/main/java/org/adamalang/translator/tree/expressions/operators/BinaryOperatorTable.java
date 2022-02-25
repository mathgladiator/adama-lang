package org.adamalang.translator.tree.expressions.operators;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.*;

import java.util.HashMap;

/** This is the table of how types use operators */
public class BinaryOperatorTable {
  private final HashMap<String, BinaryOperatorResult> table;
  private BinaryOperatorTable() {
    this.table = new HashMap<>();
    TyType tyInt = new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("int"));
    TyType tyLong = new TyNativeLong(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("long"));
    TyType tyDouble = new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("double"));
    TyType tyBoolean = new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("boolean"));
    TyType tyString = new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("string"));
    TyType tyComplex = new TyNativeComplex(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("complex"));

    TyType tyMaybeInt = new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("maybe"), new TokenizedItem<>(tyInt));
    TyType tyMaybeLong = new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("maybe"), new TokenizedItem<>(tyLong));
    TyType tyMaybeDouble = new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("maybe"), new TokenizedItem<>(tyDouble));
    TyType tyMaybeComplex = new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("maybe"), new TokenizedItem<>(tyComplex));

    // division of real numbers
    insert(tyInt, "/", tyInt, tyMaybeDouble, "LibArithmetic.Divide.II(%s, %s)", false);
    insert(tyInt, "/", tyLong, tyMaybeDouble, "LibArithmetic.Divide.IL(%s, %s)", false);
    insert(tyInt, "/", tyDouble, tyMaybeDouble, "LibArithmetic.Divide.ID(%s, %s)", false);
    insert(tyInt, "/", tyMaybeDouble, tyMaybeDouble, "LibArithmetic.Divide.ImD(%s, %s)", false);
    insert(tyInt, "/", tyComplex, tyMaybeComplex, "LibArithmetic.Divide.IC(%s, %s)", false);
    insert(tyInt, "/", tyMaybeComplex, tyMaybeComplex, "LibArithmetic.Divide.ImC(%s, %s)", false);

    insert(tyLong, "/", tyInt, tyMaybeDouble, "LibArithmetic.Divide.LI(%s, %s)", false);
    insert(tyLong, "/", tyLong, tyMaybeDouble, "LibArithmetic.Divide.LL(%s, %s)", false);
    insert(tyLong, "/", tyDouble, tyMaybeDouble, "LibArithmetic.Divide.LD(%s, %s)", false);
    insert(tyLong, "/", tyMaybeDouble, tyMaybeDouble, "LibArithmetic.Divide.LmD(%s, %s)", false);
    insert(tyLong, "/", tyComplex, tyMaybeComplex, "LibArithmetic.Divide.LC(%s, %s)", false);
    insert(tyLong, "/", tyMaybeComplex, tyMaybeComplex, "LibArithmetic.Divide.LmC(%s, %s)", false);

    insert(tyDouble, "/", tyInt, tyMaybeDouble, "LibArithmetic.Divide.DI(%s, %s)", false);
    insert(tyDouble, "/", tyLong, tyMaybeDouble, "LibArithmetic.Divide.DL(%s, %s)", false);
    insert(tyDouble, "/", tyDouble, tyMaybeDouble, "LibArithmetic.Divide.DD(%s, %s)", false);
    insert(tyDouble, "/", tyMaybeDouble, tyMaybeDouble, "LibArithmetic.Divide.DmD(%s, %s)", false);
    insert(tyDouble, "/", tyComplex, tyMaybeComplex, "LibArithmetic.Divide.DC(%s, %s)", false);
    insert(tyDouble, "/", tyMaybeComplex, tyMaybeComplex, "LibArithmetic.Divide.DmC(%s, %s)", false);

    insert(tyMaybeDouble, "/", tyInt, tyMaybeDouble, "LibArithmetic.Divide.mDI(%s, %s)", false);
    insert(tyMaybeDouble, "/", tyLong, tyMaybeDouble, "LibArithmetic.Divide.mDL(%s, %s)", false);
    insert(tyMaybeDouble, "/", tyDouble, tyMaybeDouble, "LibArithmetic.Divide.mDD(%s, %s)", false);
    insert(tyMaybeDouble, "/", tyMaybeDouble, tyMaybeDouble, "LibArithmetic.Divide.mDmD(%s, %s)", false);
    insert(tyMaybeDouble, "/", tyComplex, tyMaybeComplex, "LibArithmetic.Divide.mDC(%s, %s)", false);
    insert(tyMaybeDouble, "/", tyMaybeComplex, tyMaybeComplex, "LibArithmetic.Divide.mDmC(%s, %s)", false);

    insert(tyComplex, "/", tyInt, tyMaybeComplex, "LibArithmetic.Divide.CI(%s, %s)", false);
    insert(tyComplex, "/", tyLong, tyMaybeComplex, "LibArithmetic.Divide.CL(%s, %s)", false);
    insert(tyComplex, "/", tyDouble, tyMaybeComplex, "LibArithmetic.Divide.CD(%s, %s)", false);
    insert(tyComplex, "/", tyMaybeDouble, tyMaybeComplex, "LibArithmetic.Divide.CmD(%s, %s)", false);
    insert(tyComplex, "/", tyComplex, tyMaybeComplex, "LibArithmetic.Divide.CC(%s, %s)", false);
    insert(tyComplex, "/", tyMaybeComplex, tyMaybeComplex, "LibArithmetic.Divide.CmC(%s, %s)", false);


  }

  private void insert(TyType left, String operator, TyType right, TyType result, String java, boolean reverse) {
    table.put(left.getAdamaType() + operator + right.getAdamaType(), new BinaryOperatorResult(result, java, reverse));
  }

  public BinaryOperatorResult find(TyType left, String operator, TyType right, Environment environment) {
    if (left != null && right != null) {
      String leftAdamaType = left.getAdamaType();
      String rightAdamaType = right.getAdamaType();
      BinaryOperatorResult result = table.get(leftAdamaType + operator + rightAdamaType);
      if (result == null) {
        environment.document.createError(DocumentPosition.sum(left, right), String.format("Could not find a meaning for '%s' %s '%s'", leftAdamaType, operator, rightAdamaType), "OperatorTable");
      }
      return result;
    }
    return null;
  }

  public static BinaryOperatorTable INSTANCE = new BinaryOperatorTable();
}
