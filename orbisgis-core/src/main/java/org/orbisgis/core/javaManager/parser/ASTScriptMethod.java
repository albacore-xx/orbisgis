/* Generated By:JJTree: Do not edit this line. ASTScriptMethod.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.orbisgis.core.javaManager.parser;

public class ASTScriptMethod extends SimpleNode {
  public ASTScriptMethod(int id) {
    super(id);
  }

  public ASTScriptMethod(JavaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(JavaParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=3d772ee04534ab142ea9cb01fd3c64fb (do not edit this line) */
