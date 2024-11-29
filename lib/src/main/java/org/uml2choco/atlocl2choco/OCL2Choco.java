package org.uml2choco.atlocl2choco;

import org.uml2choco.Context;
import org.oclinchoco.NavCSP;

public class OCL2Choco {
    static public Context compile(Expression e, Context ctxt){
        swtich(e){
            case NavigationOrAttributeCall n -> compile(n, ctxt);
            case VariableExp v -> compile(v, ctxt);
            case IntegerExp i -> compile(i,ctxt);
            case BooleanType t -> compile(t, ctxt);
            case OclAnyType t -> compile(t,ctxt);
            case OperatorCallExp op -> compile(op,ctxt);
            case OperationCallExp op -> compile(op,ctxt);
        }
    }

    static public Context compile(NavigationOrAttributeCall n, Context c);
}
