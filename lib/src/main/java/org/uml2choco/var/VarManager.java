package org.uml2choco.var;

public class VarManager {
    // finds Var annotations
    // finds implied var and cst annotations
    // can be an ATL transformation
    static Context compile(EObject root, Context ctxt,boolean meta); //from model or meta-model
    static Context compile(OclExpression exp, Context ctxt);
}
