package org.uml2choco.xmi2choco;

import org.oclinchoco.ReferenceTable;
import org.uml2choco.EMFCSP;
import org.uml2choco.atlocl2choco.context.Context;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class XMI2Choco {

    static public void buildUMLCSP(EObject rootObj, EMFCSP csp){
// TODO
        int classCount = rootObj.eClass().getEReferences().size();


        // Identifiy the Objects in the Problem
        System.out.println("\nLoading Objects\n");

        List<EClass> eclasses = new ArrayList<>();
        HashMap<EClass,EList<EObject>> class2objects = new HashMap<>();
        Table<EClass,Integer,EObject> classptr2object = HashBasedTable.create();

        for(int i=0; i<classCount;i++){
            EClass iClass = rootObj.eClass().getEReferences().get(i).getEReferenceType();
            // System.out.println("Class: "+iClass+"\n");
            eclasses.add(iClass);
            
            EList<EObject> iInstances = (EList<EObject>)rootObj.eGet(rootObj.eClass().getEReferences().get(i));
            // System.out.println("Instances: "+iInstances+"\n");
            class2objects.put(iClass, iInstances);
            
            //Make Pointers Table
            for(int j=0;j<iInstances.size();j++){
                classptr2object.put(iClass,j,iInstances.get(j));
            }
        }
    }

    // static public Context loadInstance(EObject root, Context ctxt){
    //     // get .var() Declaration
        
    //     // get objects
    //         // list of root contents EClasses
        

    //         // make <EClass, list<EObject> class2obj
    //     // get properties
    //         // list of properties for EClasses (according to .var())
    //         // get cardinalities, opposites, containement, etc...
        
    //     // for class2obj, for Props
    //         // compile_PropTables
    //         // make <EClass, EObject, Prop>
    //     // for RefxRef if opposite
    //         // Opposite CSP

    //     // add this to context
    // }

    //make <EObject, Property> 
    // static public Context compile_PropTables(EObject o, Context c){}
    
}
