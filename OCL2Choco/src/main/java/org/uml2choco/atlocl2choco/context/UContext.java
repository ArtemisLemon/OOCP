package org.uml2choco.atlocl2choco.context;

import org.chocosolver.solver.variables.IntVar;
import org.oclinchoco.source.Source;
import org.uml2choco.EMFCSP;

public class UContext {
    Source source;
    IntVar[] interestingVariables;

    public UContext(EMFCSP csp, Source src){
        this.source=src;
    }

    public UContext(EMFCSP csp, Source src, IntVar[] vars){
        this.source=src;
        this.interestingVariables=vars;
    }

    public Source getSource(){return source;}
}
