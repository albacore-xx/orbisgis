/* Generated By:JJTree: Do not edit this line. ASTUnaryExpressionNotPlusMinus.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.orbisgis.core.javaManager.parser;

public class ASTUnaryExpressionNotPlusMinus extends SimpleNode {
  public ASTUnaryExpressionNotPlusMinus(int id) {
    super(id);
  }

  public ASTUnaryExpressionNotPlusMinus(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=3c020917147b50a673b09865144935bf (do not edit this line) */
