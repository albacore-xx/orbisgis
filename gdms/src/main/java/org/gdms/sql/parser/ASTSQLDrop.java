/* Generated By:JJTree: Do not edit this line. ASTSQLDrop.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.gdms.sql.parser;

public
class ASTSQLDrop extends SimpleNode {
  public ASTSQLDrop(int id) {
    super(id);
  }

  public ASTSQLDrop(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=fdac8c50282dcc554c2f6eb471e4ebd0 (do not edit this line) */
