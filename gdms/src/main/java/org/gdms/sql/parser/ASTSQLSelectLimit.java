/* Generated By:JJTree: Do not edit this line. ASTSQLSelectLimit.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.gdms.sql.parser;

public
class ASTSQLSelectLimit extends SimpleNode {
  public ASTSQLSelectLimit(int id) {
    super(id);
  }

  public ASTSQLSelectLimit(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=09fa5705205442c59db2b20a70a64560 (do not edit this line) */
