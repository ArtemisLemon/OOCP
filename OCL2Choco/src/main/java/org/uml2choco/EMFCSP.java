package org.uml2choco;

import org.oclinchoco.CSP;
import org.oclinchoco.ReferenceTable;
import org.oclinchoco.property.NavTable;

import java.lang.ref.Reference;
import java.util.HashMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

//This class maps an EMF instance to an oclinchoco.CSP view
public class EMFCSP extends CSP{
    HashMap<String,EReference> nav2ref; //from NavOrAttribCallExp.getName()
    HashMap<EReference,ReferenceTable> referencetables;
    
    HashMap<EObject,Integer> objPtrVals;

    public NavTable getNavTable(String prop){
        return referencetables.get(nav2ref.get(prop));
    }

    public int getObjPtr(EObject o){
        return objPtrVals.get(o);
    }

    public void addObjPtr(EObject o, int p){
        objPtrVals.put(o,p);
    }

    public void addReferenceTable(EReference eref,int src_count, int tgt_count){
        ReferenceTable refTable = new ReferenceTable(this,src_count,eref.getUpperBound(),eref.getLowerBound(),tgt_count);
        nav2ref.put(eref.getName(),eref);
        referencetables.put(eref, refTable);
    }

    // public class EMFCSPObject {}
}
