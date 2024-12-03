package org.uml2choco.atlocl2choco;

import org.uml2choco.Context;
import org.oclinchoco.CSP;
import org.oclinchoco.NavCSP;


import org.eclipse.m2m.atl.common.OCL.BooleanType;
import org.eclipse.m2m.atl.common.OCL.IntegerExp;
import org.eclipse.m2m.atl.common.OCL.NavigationOrAttributeCallExp;
import org.eclipse.m2m.atl.common.OCL.OclAnyType;
import org.eclipse.m2m.atl.common.OCL.OclExpression;
import org.eclipse.m2m.atl.common.OCL.OperatorCallExp;
import org.eclipse.m2m.atl.common.OCL.OperationCallExp;
import org.eclipse.m2m.atl.common.OCL.VariableExp;

public class OCL2Choco {
    static public Context compile(OclExpression e, Context ctxt){
        swtich(e){
            case NavigationOrAttributeCallExp n -> compile(n, ctxt);
            case VariableExp v -> compile(v, ctxt);
            case IntegerExp i -> compile(i,ctxt);
            case BooleanType t -> compile(t, ctxt);
            case OclAnyType t -> compile(t,ctxt);
            case OperatorCallExp op -> compile(op,ctxt);
            case OperationCallExp op -> compile(op,ctxt);
        }
    }

    static private Context compile(NavigationOrAttributeCallExp n, Context c){
        //compile source & get prop name
        Context cc = compile(n.getSource());
        String prop = n.getName();

        CSP csp = c.csp();
        //Property Access
        if(cc.isSelf()) return new Context(c, csp.getRefTable(prop).AdjList(cc.getID()));

        //Variable Navigation
        NavCSP nav = new NavCSP(csp,cc.getSource(),csp.getNavTable(prop));
        //cc.getSource() get the result as Source from the context below, top of stack? but only need to keep last Object so.. acc?
        //c.NavProp(prop) finds in the csp the Property Table of Variable or Constant IntVars, this shouldn't change from the the xmi2csp compilation so can use downward context

        //return new context
        return new Context(c, nav);
    }

    static private Context compileVar(OperatorCallExp o, Context c){
        Context cc = compile(o.getSource());
        if(cc.isVariable() || cc.isSelf()) return compile(wrapVar(o),c); //calls above compile(NavOrAttibExpCall)

        //Run standard OCL interpreter
        //if output is objects find prop table?
        //think about it
    }
    static private NavigationOrAttributeCallExp wrapVar(OperatorCallExp o){
        NavigationOrAttributeCallExp out = new NavigationOrAttributeCallExp();
        out.setSource(o.getSource());
        out.setName(o.getArguments(0));
    }


    static private Context compile(OperatorCallExp o, Context c){
        if(o.getOperationName()=="var") return compileVar(o,c);
        // swtich(op.getOperationName()){
        //     case "var" : return compile()
        // }
    }
}
