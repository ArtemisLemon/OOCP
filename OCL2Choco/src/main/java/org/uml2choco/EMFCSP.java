package org.uml2choco;

import org.oclinchoco.CSP;
import org.oclinchoco.ReferenceTable;
import org.oclinchoco.property.NavTable;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

//This class maps an EMF instance to an oclinchoco.CSP
public class EMFCSP extends CSP{
    // HashMap<String,EReference> nav2ref; //from NavOrAttribCallExp.getName()
    // HashMap<EReference,ReferenceTable> referencetables;
    List<ReferenceTable> justthetables;
    List<String> tablenames;

    //OCL View
    HashMap<String,ReferenceTable> referencetables; // NavOrAttribCallExp.getName() -> ReferenceTable
    HashMap<EObject,Integer> objPtrVals; //EObject -> int, useful when makeing self Node

    //UML View
    List<EMFCSPObject> objects;

    public EMFCSP(){
        // nav2ref = new HashMap<>();
        referencetables = new HashMap<>();
        objPtrVals = new HashMap<>();

        objects = new ArrayList<>();
        tablenames = new ArrayList<>();
    }

    public int getObjPtr(EObject o){
        return objPtrVals.get(o);
    }
    
    public void addObjPtr(EObject o, int p){
        objPtrVals.put(o,p);
    }
    
    public ReferenceTable getRefTable(String prop){
        return referencetables.get(prop);
    }

    public NavTable getNavTable(String prop){
        // return referencetables.get(nav2ref.get(prop));
        return referencetables.get(prop);
    }

    public void addReferenceTable(EReference eref,int src_count, int tgt_count){
        ReferenceTable refTable = new ReferenceTable(this,src_count,eref.getUpperBound(),eref.getLowerBound(),tgt_count);
        // nav2ref.put(eref.getName(),eref);
        // referencetables.put(eref, refTable);
        tablenames.add(eref.getName());
        referencetables.put(eref.getName(),refTable);
    }

    public class EMFCSPObject {
        public EObject emfobject;
        public EList<EReference> references;
        public HashMap<EReference,EList<EObject>> links; //File Data
        public HashMap<EReference, ReferenceTable.AdjList> link_variables; //CSP Data

        public EClass getEClass(){return emfobject.eClass();}
        public EObject getEObject(){return emfobject;}

        private EMFCSPObject(EObject e,EList<EReference> r,HashMap<EReference,EList<EObject>> l,HashMap<EReference, ReferenceTable.AdjList> v){
            this.emfobject=e;
            this.references=r;
            this.links=l;
            this.link_variables=v;
        }

        public void data2variables(){
            for(EReference r : references){
                EList<EObject> data = links.get(r);
                // System.out.println("Loading data for "+r.getName());
                ReferenceTable.AdjList variables = link_variables.get(r);
                int cols = variables.maxCard();
                int[] dataBuffer = new int[cols];
                for(int i=0;i<cols;i++) dataBuffer[i]=-1;//-1

                // System.out.println("Data to Load: "+data);
                for(int i=0;i<data.size();i++) {
                    // System.out.println("Ptr Val: "+objPtrVals.get(data.get(i)));
                    dataBuffer[i]=objPtrVals.get(data.get(i));
                }
                variables.loadData(dataBuffer);

            }
        }
        public void variables2data(){}
    }

    public void addEMFCSPObject(EObject e,EList<EReference> r,HashMap<EReference,EList<EObject>> l,HashMap<EReference, ReferenceTable.AdjList> v){
        objects.add(new EMFCSPObject(e, r, l, v));
    }

    public List<EMFCSPObject> getEMFCSPObjects(){
        return objects;
    }

    void printJustTheTables(){
        for(String r : tablenames){
            System.out.println(r);
            System.out.println(referencetables.get(r));
        }
    }
}
