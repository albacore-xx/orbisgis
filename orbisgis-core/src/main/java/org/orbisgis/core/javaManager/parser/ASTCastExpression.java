/* Generated By:JJTree: Do not edit this line. ASTCastExpression.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.orbisgis.core.javaManager.parser;

public class ASTCastExpression extends SimpleNode {
  public ASTCastExpression(int id) {
    super(id);
  }

  public ASTCastExpression(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=e1bb1cea2a66b2874491d4c122a29f22 (do not edit this line) */
