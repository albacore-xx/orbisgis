/* Generated By:JJTree: Do not edit this line. ASTSQLAlter.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.gdms.sql.parser;

public
class ASTSQLAlter extends SimpleNode {
  public ASTSQLAlter(int id) {
    super(id);
  }

  public ASTSQLAlter(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=9b9573e1e5422424c1471e4c6cc0a6f8 (do not edit this line) */
