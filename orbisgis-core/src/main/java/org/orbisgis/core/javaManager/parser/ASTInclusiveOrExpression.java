/* Generated By:JJTree: Do not edit this line. ASTInclusiveOrExpression.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.orbisgis.core.javaManager.parser;

public class ASTInclusiveOrExpression extends SimpleNode {
  public ASTInclusiveOrExpression(int id) {
    super(id);
  }

  public ASTInclusiveOrExpression(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=f4b1750c4a2c9fd657818fa571e34274 (do not edit this line) */
