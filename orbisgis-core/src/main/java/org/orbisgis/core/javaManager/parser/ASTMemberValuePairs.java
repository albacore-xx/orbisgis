/* Generated By:JJTree: Do not edit this line. ASTMemberValuePairs.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.orbisgis.core.javaManager.parser;

public class ASTMemberValuePairs extends SimpleNode {
  public ASTMemberValuePairs(int id) {
    super(id);
  }

  public ASTMemberValuePairs(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=ec1d4b9ecc7e46e860a90226d342f526 (do not edit this line) */
