/* Generated By:JJTree: Do not edit this line. ASTConditionalExpression.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.orbisgis.core.javaManager.parser;

public class ASTConditionalExpression extends SimpleNode {
  public ASTConditionalExpression(int id) {
    super(id);
  }

  public ASTConditionalExpression(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=e2b4cb9e8f37dcf909094bde088081ac (do not edit this line) */
