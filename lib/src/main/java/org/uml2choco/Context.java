package org.uml2choco;

import org.oclinchoco.CSP;
import org.oclinchoco.types.Source;

public class Context {
    //Downward passage
    EObject obj;
    CSP csp;
    public Context(EObject e,CSP c){
        self.obj=e;
        self.csp=c;
    }

    //Upward passage
    Object node;
    public Context(Context c, Object n){
        self.obj=c.obj;
        self.csp=c.csp;
        self.node=n;
    }

    public EObject obj(){return obj;}
    public CSP csp(){return csp;}
    public Source getSource(){ return (Source)node; }
}
