package org.uml2choco.atlocl2choco.context;

import org.oclinchoco.source.Source;
import org.uml2choco.EMFCSP;

public class UContext extends Context {
    Source source;

    public UContext(EMFCSP csp, Source src){
        this.csp=csp;
        this.source=src;
    }

    public Source getSource(){return source;}
}
