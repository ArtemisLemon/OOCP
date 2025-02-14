package org.uml2choco.atlocl2choco.context;

import org.eclipse.emf.ecore.EObject;
import org.uml2choco.EMFCSP;

public class DContext {
    EMFCSP csp;
    EObject self;

    public DContext(EMFCSP csp, EObject slf){
        this.csp=csp;
        this.self=slf;
    }

    public EMFCSP getCSP(){return csp;}
    public EObject getSelf(){return self;}
}
