package org.uml2choco;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.m2m.atl.common.ATL.Module;
import org.uml2choco.atlocl2choco.OCL2Choco;
import org.uml2choco.atlocl2choco.context.DContext;
import org.uml2choco.atlocl2choco.context.UContext;
import org.uml2choco.utilities.EMF2ChocoIO;
import org.uml2choco.xmi2choco.XMI2Choco;



/*
 * EMF2Choco
 * 
 * Conductor, outlines the main logic of building and solving
 * 
 */
public class EMF2Choco {
    EMFCSP csp;
    EPackage metamodel;
    Resource model;
    EObject rootObject;
    Module constraints;
    
    // List<EMFCSPObject> objects;

    public void run(String mm, String m, String c){
        EMF2ChocoIO.initResourceSet();
        csp = new EMFCSP();
        
        // UMLCSP
        metamodel = EMF2ChocoIO.loadECORE(mm);
        model = EMF2ChocoIO.loadXMI(metamodel, m);
        rootObject = model.getContents().getFirst();
        XMI2Choco.buildUMLCSP(rootObject,csp);

        // OCLCSP
        constraints = EMF2ChocoIO.loadATL(c);
        // eClass2Helpers
        // For all EObjects, apply relevant OCL expressions
            EObject self = null;
            DContext start = new DContext(csp, self);
            UContext result = OCL2Choco.compile(null, start);


        EMF2ChocoIO.saveXMI("solved"+m,rootObject);
    }
}
