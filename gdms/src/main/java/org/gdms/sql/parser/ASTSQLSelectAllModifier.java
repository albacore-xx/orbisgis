/* Generated By:JJTree: Do not edit this line. ASTSQLSelectAllModifier.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.gdms.sql.parser;

public
class ASTSQLSelectAllModifier extends SimpleNode {
  public ASTSQLSelectAllModifier(int id) {
    super(id);
  }

  public ASTSQLSelectAllModifier(SQLEngine p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SQLEngineVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=fdb0d3f0d719630207552ce0e52b1a1c (do not edit this line) */
