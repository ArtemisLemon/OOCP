package org.uml2choco.xmi2choco;

import org.uml2choco.Context;
import org.oclinchoco.ReferenceTable;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
//import EObject

public class XMI2Choco {
    static public Context compile_root(EObject root, Context ctxt){
        // get .var() Declaration
        
        // get objects
            // list of root contents EClasses
            // make <EClass, list<EObject> class2obj
        // get properties
            // list of properties for EClasses (according to .var())
            // get cardinalities, opposites, containement, etc...
        
        // for class2obj, for Props
            // compile_PropTables
            // make <EClass, EObject, Prop>
        // for RefxRef if opposite
            // Opposite CSP

        // add this to context
    }

    //make <EObject, Property> 
    static public Context compile_PropTables(EObject o, Context c);
    
}
