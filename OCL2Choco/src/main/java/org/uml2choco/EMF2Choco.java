package org.uml2choco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.m2m.atl.common.ATL.Helper;
import org.eclipse.m2m.atl.common.ATL.Module;
import org.eclipse.m2m.atl.common.OCL.Operation;
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
    
    EList<EClass> classes;

    public void run(String mm, String m, String c){
        EMF2ChocoIO.initResourceSet();
        csp = new EMFCSP();
        
        // UMLCSP
        metamodel = EMF2ChocoIO.loadECORE(mm);
        model = EMF2ChocoIO.loadXMI(metamodel, m);
        rootObject = model.getContents().getFirst();
        XMI2Choco.buildUMLCSP(rootObject,csp);

        csp.printJustTheTables();

        // OCLCSP
        if(c!=null){
            System.out.println("Building OCL CSP");
            constraints = EMF2ChocoIO.loadATL(c);
            // for(var e : constraints.getElements()) if (e instanceof Helper h){
            //     System.out.println(h.getDefinition().getContext_().getContext_().getName());
            // }
    
            for(EMFCSP.EMFCSPObject o : csp.getEMFCSPObjects()){
                String tomatch = o.getEClass().getName();
                System.out.println("Looking for constraints for "+tomatch+csp.getObjPtr(o.getEObject()));
    
                for (var e : constraints.getElements()) if (e instanceof Helper h) {
                    String classStr = h.getDefinition().getContext_().getContext_().getName();
                    if(tomatch.equals(classStr)){
                        DContext start = new DContext(csp, o.getEObject());
                        Operation op = (Operation)h.getDefinition().getFeature();
                        System.out.println("applying "+ op.getName() +" to "+classStr+csp.getObjPtr(o.getEObject()));
                        UContext result = OCL2Choco.compile(op.getBody(), start);
                    }
                } 
            }
            System.out.println("Building OCL CSP Finished");
        }


        // eClass2Helpers
        // For all EObjects, apply relevant OCL expressions
            // EObject self = null;
            // DContext start = new DContext(csp, self);
            // UContext result = OCL2Choco.compile(null, start);

        System.out.println("Solving");
        csp.model().getSolver().solve();
        csp.model().getSolver().printStatistics();
        // System.out.println(csp.model());
        csp.printJustTheTables();
        // csp.model().getSolver().printStatistics();

        // EMF2ChocoIO.saveXMI("solved"+m,rootObject);
    }
}
