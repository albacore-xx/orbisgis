/* Generated By:JJTree: Do not edit this line. ASTSQLDataTypeConstraint.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.gdms.sql.parser;

public
class ASTSQLDataTypeConstraint extends SimpleNode {
  public ASTSQLDataTypeConstraint(int id) {
    super(id);
  }

  public ASTSQLDataTypeConstraint(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=6f1dac4c81473ad2ee035059720dfde2 (do not edit this line) */
