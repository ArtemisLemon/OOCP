package org.uml2choco.atlocl2choco;

import org.uml2choco.EMFCSP;
import org.uml2choco.atlocl2choco.context.DContext;
import org.uml2choco.atlocl2choco.context.UContext;

// OCL Symbols
import org.eclipse.m2m.atl.common.OCL.IntegerExp;
import org.eclipse.m2m.atl.common.OCL.NavigationOrAttributeCallExp;
import org.eclipse.m2m.atl.common.OCL.OclExpression;
import org.eclipse.m2m.atl.common.OCL.OperatorCallExp;
import org.eclipse.m2m.atl.common.OCL.OperationCallExp;
import org.eclipse.m2m.atl.common.OCL.VariableExp;
import org.oclinchoco.navigation.NavCSP;
import org.oclinchoco.nodecsp.RelationalNode;
import org.oclinchoco.nodecsp.ArithmeticNode;
import org.oclinchoco.nodecsp.AsSetNode;
import org.oclinchoco.nodecsp.SizeNode;
import org.oclinchoco.nodecsp.SumNode;
import org.oclinchoco.nodecsp.VarNode;
import org.oclinchoco.nodecsp.VariableExpNode;
import org.oclinchoco.source.PtrSource;
import org.oclinchoco.source.VarSource;
import org.oclinchoco.source.VarsSource;


// This Class provides the methods to interpret OCL Expressions using OCLinChoco models
// For each object in the OCL AST, an OCLinChoco object is instanciated
public class ATLOCL2Choco {

    //SwitchBoard

    static public UContext compile(OclExpression e, DContext ctxt){
        return switch(e){
            case NavigationOrAttributeCallExp n -> compile(n, ctxt);
            case VariableExp v -> compile(v, ctxt);
            case IntegerExp i -> compile(i,ctxt);
            case OperatorCallExp op -> compile(op,ctxt);
            case OperationCallExp op -> compile(op,ctxt);
            // case BooleanType t -> compile(t, ctxt);
            // case OclAnyType t -> compile(t,ctxt);
            default -> throw new UnsupportedOperationException("don't support " + e);

        };
    }

    static private UContext compile(OperatorCallExp o, DContext c){
        // if(o.getOperationName()=="var") return compileVar(o,c);
        switch(o.getOperationName()){
            case "<=" :
            case ">=" :
            case "<" :
            case ">" :
            case "=" :
            case "<>" :
                return compileRelational(o,c);
            case "+" :
            case "-" :
            case "/" :
            case "*" :
            // case "min" :
            // case "max" :
            // case "mod" :
            // case "pow" :
            // case "dist" :
                return compileArithmetic(o,c);
            case "var" : 
            default :
                throw new UnsupportedOperationException("don't support Operator " + o.getOperationName());
        }
    }

    static private UContext compile(OperationCallExp o, DContext c){
        switch(o.getOperationName()){
            case "size" :
                return compileSize(o,c);
            case "asSet" :
                return compileAsSet(o,c);
            case "sum" :
                return compileSum(o,c);

            case "min" :
            case "max" :
            case "mod" :
            case "pow" :
            case "dist" :
                return compileArithmetic(o,c);
            case "abs":
            case "neg":
            // case "sqr":
                return compileArithmeticOne(o, c);
            default :
                throw new UnsupportedOperationException("don't support Operation " + o.getOperationName());
        }
    }

    static private UContext compile(VariableExp v, DContext c){
        String varName = v.getReferredVariable().getVarName();
        switch(varName){
            case "self" : 
                return compileSelf(v,c);
            default :
                throw new UnsupportedOperationException("don't support " + varName);
        }
    }





    
    // Compile Rule Definitions

    static private UContext compile(NavigationOrAttributeCallExp n, DContext c){
        //compile source & get prop name
        UContext cc = compile(n.getSource(),c);
        String prop = n.getName();
        // System.out.println("compiling NavOrAttribCallExp "+cc.getSource()+"->"+prop);
        EMFCSP csp = c.getCSP();

        //Property Access (TODO? it can maybe be skiped);

        //Variable Navigation
        NavCSP nav = NavCSP.makeNavCSP(csp,(PtrSource)cc.getSource(),csp.getNavTable(prop));
        //cc.getSource() get the result as Source from the context below, top of stack? but only need to keep last Object so.. acc?
        //c.NavProp(prop) finds in the csp the Property Table of Variable or Constant IntVars, this shouldn't change from the the xmi2csp compilation so can use downward context

        return new UContext(csp, nav);
    }

    static private UContext compile(IntegerExp i, DContext c){
        return new UContext(c.getCSP(), new VarNode(c.getCSP(), i.getIntegerSymbol()));
    }


    static private UContext compileSum(OperationCallExp o, DContext c){
        UContext cc = compile(o.getSource(),c);
        VarsSource src = (VarsSource)cc.getSource();

        SumNode node = new SumNode(c.getCSP(), src);

        return new UContext(null, node);
    }

    static private UContext compileSize(OperationCallExp o, DContext c){
        UContext cc = compile(o.getSource(),c);

        SizeNode node = SizeNode.create(c.getCSP(), cc.getSource());
        return new UContext(c.getCSP(), node);
    }


    static private UContext compileSelf(VariableExp self, DContext c){
        int self_value=c.getCSP().getObjPtr(c.getSelf());
        return new UContext(c.getCSP(), new VariableExpNode(c.getCSP(), self_value));
    }

    static private UContext compileRelational(OperatorCallExp o,DContext c){
        UContext ccleft = compile(o.getSource(),c);
        UContext ccright = compile(o.getArguments().get(0),c);
        
        String opString = o.getOperationName();
        if(opString.equals("<>")) opString="!=";
        
        RelationalNode node = new RelationalNode(c.getCSP(), (VarSource)ccleft.getSource(), (VarSource)ccright.getSource(), opString);

        return new UContext(c.getCSP(), node);
    }

    static private UContext compileArithmeticOne(OperationCallExp o,DContext c){
        UContext ccleft = compile(o.getSource(),c);
        
        ArithmeticNode node = new ArithmeticNode(c.getCSP(), (VarSource)ccleft.getSource(), o.getOperationName());

        return new UContext(c.getCSP(), node);
    }

    static private UContext compileArithmetic(OperationCallExp o,DContext c){
        UContext ccleft = compile(o.getSource(),c);
        UContext ccright = compile(o.getArguments().get(0),c);
        
        ArithmeticNode node = new ArithmeticNode(c.getCSP(), (VarSource)ccleft.getSource(), (VarSource)ccright.getSource(), o.getOperationName());

        return new UContext(c.getCSP(), node);
    }


    static private UContext compileAsSet(OperationCallExp o, DContext c){
        UContext cc = compile(o.getSource(), c);

        AsSetNode node = new AsSetNode(c.getCSP(), (PtrSource)cc.getSource());        

        return new UContext(c.getCSP(), node);
    }


    // static private Context compileVar(OperatorCallExp o, Context c){
    //     Context cc = compile(o.getSource());
    //     if(cc.isVariable() || cc.isSelf()) return compile(wrapVar(o),c); //calls above compile(NavOrAttibExpCall)

    //     //Run standard OCL interpreter
    //     //if output is objects find prop table?
    //     //think about it
    // }
    // static private NavigationOrAttributeCallExp wrapVar(OperatorCallExp o){
    //     NavigationOrAttributeCallExp out = new NavigationOrAttributeCallExp();
    //     out.setSource(o.getSource());
    //     out.setName(o.getArguments(0));
    // }


}
