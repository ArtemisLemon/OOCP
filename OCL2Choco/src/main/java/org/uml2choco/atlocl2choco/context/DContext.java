package org.uml2choco.atlocl2choco.context;

import org.eclipse.emf.ecore.EObject;
import org.uml2choco.EMFCSP;

public class DContext extends Context {
    EObject self;

    public DContext(EMFCSP csp, EObject slf){
        this.csp=csp;
        this.self=slf;
    }

    public EObject getSelf(){return self;}
}
