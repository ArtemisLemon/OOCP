package org.uml2choco;

import org.oclinchoco.CSP;
import org.oclinchoco.navigation.NavTable;
import org.oclinchoco.property.AttributeTable;
import org.oclinchoco.property.ReferenceTable;
import org.oclinchoco.property.SingleIntTable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.chocosolver.solver.variables.IntVar;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

//This class maps an EMF instance to an oclinchoco.CSP
public class EMFCSP extends CSP{
    // HashMap<String,EReference> nav2ref; //from NavOrAttribCallExp.getName()
    // HashMap<EReference,ReferenceTable> referencetables;
    List<ReferenceTable> justthetables;
    List<String> tablenames;

    //OCL View
    HashMap<String,ReferenceTable> referencetables; // NavOrAttribCallExp.getName() -> ReferenceTable
    HashMap<EObject,Integer> objPtrVals; //EObject -> int, useful when makeing self Node

    HashMap<String,AttributeTable> attributetables;
    HashMap<String,SingleIntTable> singleIntTable;

    HashMap<String,NavTable> navtables;
    //UML View
    List<EMFCSPObject> objects;
    Table<EClass,Integer,EObject> classPtr2Obj;

    public EMFCSP(){
        referencetables = new HashMap<>();
        attributetables = new HashMap<>();
        singleIntTable = new HashMap<>();
        navtables = new HashMap<>();
        objPtrVals = new HashMap<>();

        objects = new ArrayList<>();
        tablenames = new ArrayList<>();
        classPtr2Obj = HashBasedTable.create();
    }

    public void setClassPtr2Object(Table<EClass,Integer,EObject> table){classPtr2Obj=table;}

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
        return navtables.get(prop);
    }

    public void addSingleIntTable(EAttribute eattrib, int rows){
        SingleIntTable attribtable = new SingleIntTable(this, rows);
        System.out.println(eattrib.getName()+" "+attribtable);
        tablenames.add(eattrib.getName());
        navtables.put(eattrib.getName(), attribtable);
        singleIntTable.put(eattrib.getName(), attribtable);
        System.out.println(eattrib.getName()+" "+attributetables.get(eattrib.getName()));
    }

    public SingleIntTable getSingleIntTable(String prop){
        return singleIntTable.get(prop);
    }

    public void addAttributeTable(EAttribute eAttribute, int rows, int minCard, int maxCard){
        AttributeTable attribtable = new AttributeTable(this, rows, minCard, maxCard);
    }

    public AttributeTable getAttribTable(String prop){
        return attributetables.get(prop);
    }

    public void addReferenceTable(EReference eref,int src_count, int tgt_count){
        // System.out.println("EMFCSP: addReferenceTable");
        ReferenceTable refTable = new ReferenceTable(this,src_count,eref.getUpperBound(),eref.getLowerBound(),tgt_count);
        // nav2ref.put(eref.getName(),eref);
        // referencetables.put(eref, refTable);
        tablenames.add(eref.getName());
        navtables.put(eref.getName(), refTable);
        referencetables.put(eref.getName(),refTable);
    }

    public class EMFCSPObject {
        public EObject emfobject;
        public EList<EReference> references;
        public HashMap<EReference,EList<EObject>> links; //File Data
        public HashMap<EReference, ReferenceTable.AdjList> link_variables; //CSP Data
        
        public EList<EAttribute> attributes;
        public HashMap<EAttribute,Integer> fields; //File Data
        public HashMap<EAttribute, SingleIntTable.SingleIntAttribute> field_variables; //CSP Data

        public EClass getEClass(){return emfobject.eClass();}
        public EObject getEObject(){return emfobject;}

        private EMFCSPObject(EObject e,
        EList<EReference> r,HashMap<EReference,EList<EObject>> l,HashMap<EReference, ReferenceTable.AdjList> v,
        EList<EAttribute> a, HashMap<EAttribute,Integer> al,HashMap<EAttribute, SingleIntTable.SingleIntAttribute> av){
            this.emfobject=e;
            this.references=r;
            this.links=l;
            this.link_variables=v;

            this.attributes=a;
            this.fields=al;
            this.field_variables=av;
        }

        public void data2variables(boolean verifMode){
            int bufferEmpty;
            for(EReference r : references){
                bufferEmpty=-1;
                if(verifMode) bufferEmpty=0;
                EList<EObject> data = links.get(r);
                // System.out.println("Loading data for "+r.getName());
                ReferenceTable.AdjList variables = link_variables.get(r);
                int cols = variables.size();
                int[] dataBuffer = new int[cols];
                for(int i=0;i<cols;i++) dataBuffer[i]=bufferEmpty;//-1

                // System.out.println("Data to Load: "+data);
                for(int i=0;i<data.size();i++) {
                    // System.out.println("Ptr Val: "+objPtrVals.get(data.get(i)));
                    dataBuffer[i]=objPtrVals.get(data.get(i));
                }
                variables.loadData(dataBuffer);

            }

            for(EAttribute a : attributes){
                bufferEmpty=CSP.MIN_BOUND-1;
                if(verifMode) bufferEmpty=CSP.MIN_BOUND;
                // System.out.println("Loading "+a.getName());
                int[] data = {fields.get(a)};
                SingleIntTable.SingleIntAttribute variable = field_variables.get(a);
                variable.loadData(data);
            }
        }
        public void variables2data(){
            // System.out.println("moving data from solver to instance");
            for(EReference r : references){
                // System.out.println("Reference "+r.getName()+":"+r.getEReferenceType().getName());
                EList<EObject> buff = new BasicEList<>();
                int[] data = link_variables.get(r).getData();
                for(int i=0; i<data.length;i++){
                    if(data[i]==nullptr().getValue()) continue;
                    buff.add(classPtr2Obj.get(r.getEReferenceType(), data[i]));
                }
                // System.out.println(buff);
                // for(EObject o : buff) System.out.println(o);

                if(!buff.isEmpty()){
                    if(r.getUpperBound()==1) emfobject.eSet(r, buff.get(0));
                    else emfobject.eSet(r, buff);
                }
            }
        }
    }

    public void addEMFCSPObject(EObject e,
    EList<EReference> r,HashMap<EReference,EList<EObject>> l,HashMap<EReference, ReferenceTable.AdjList> v,
    EList<EAttribute> a, HashMap<EAttribute,Integer> al,HashMap<EAttribute, SingleIntTable.SingleIntAttribute> av){
        objects.add(new EMFCSPObject(e, r, l, v, a, al, av));
    }

    public List<EMFCSPObject> getEMFCSPObjects(){
        return objects;
    }

    public void printJustTheTables(){
        for(String r : tablenames){
            Object out = referencetables.get(r);
            if(out==null) out = attributetables.get(r);
            if(out==null) out = singleIntTable.get(r);
            System.out.println(r);
            System.out.println(out);
        }
    }
}
