package org.uml2choco;

import org.oclinchoco.CSP;
import org.oclinchoco.navigation.NavTable;
import org.oclinchoco.property.AttributeTable;
import org.oclinchoco.property.ReferenceTable;
import org.oclinchoco.property.SingleIntTable;
import org.uml2choco.utilities.PropertyWrapper;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

//This class maps an EMF instance to an oclinchoco.CSP
public class EMFCSP extends CSP{
    
    HashMap<EObject,Integer> objPtrVals; //EObject -> int, useful when makeing self Node
    HashMap<String,NavTable> navtables;
    
    //UML View
    List<EMFCSPObject> objects;
    Table<EClass,Integer,EObject> classPtr2Obj;

    public EMFCSP(){
        navtables = new HashMap<>();
        objPtrVals = new HashMap<>();

        objects = new ArrayList<>();
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
        return (ReferenceTable)getTable(prop);
    }

    public NavTable getNavTable(String prop){
        // return referencetables.get(nav2ref.get(prop));
        return getTable(prop);
    }

    public void addSingleIntTable(EAttribute eattrib, int rows){
        SingleIntTable attribtable = new SingleIntTable(this, rows);
        addTable(eattrib.getName(), attribtable);
    }

    // public SingleIntTable getSingleIntTable(String prop){
    //     return singleIntTable.get(prop);
    // }

    public void addAttributeTable(EAttribute eAttribute, int rows, int minCard, int maxCard){
        AttributeTable attribtable = new AttributeTable(this, rows, minCard, maxCard);
        addTable(eAttribute.getName(), attribtable);
    }

    // public AttributeTable getAttribTable(String prop){
    //     return attributetables.get(prop);
    // }

    public void addReferenceTable(EReference eref,int src_count, int tgt_count){
        ReferenceTable refTable = new ReferenceTable(this,src_count,eref.getUpperBound(),eref.getLowerBound(),tgt_count);
        addTable(eref.getName(),refTable);
    }

    public class EMFCSPObject {
        public EObject emfobject;
        public EList<EReference> references;
        public EList<EAttribute> attributes;

        public EClass getEClass(){return emfobject.eClass();}
        public EObject getEObject(){return emfobject;}

        private EMFCSPObject(EObject e, EList<EReference> r, EList<EAttribute> a){
            this.emfobject=e;
            this.references=r;
            this.attributes=a;
        }

        EList<EObject> getReferenceLinks(EReference r){
            return PropertyWrapper.Links(emfobject, r);
        }

        EList<Integer> getAttributeValues(EAttribute a){
            return PropertyWrapper.Ints(emfobject, a);
        }

        ReferenceTable.AdjList getReferenceVars(EReference r){
            return ((ReferenceTable)getTable(r.getName())).adjList(getObjPtr(emfobject));
        }

        AttributeTable.AttributeTableRow getAttributeVars(EAttribute a){
            return ((AttributeTable)getTable(a.getName())).getAttribute(getObjPtr(emfobject));
        }


        public void data2variables(boolean verifMode){
            int bufferEmpty;

            for(EReference r : references){
                bufferEmpty=-1;
                if(verifMode) bufferEmpty=0;

                EList<EObject> data = getReferenceLinks(r);
                if(data.size()==0) continue;

                ReferenceTable.AdjList variables = getReferenceVars(r);
                int cols = variables.size();
                int[] dataBuffer = new int[cols];
                for(int i=0;i<cols;i++) dataBuffer[i]=bufferEmpty;
                
                for(int i=0;i<data.size();i++) {
                    dataBuffer[i]=objPtrVals.get(data.get(i));
                }
                
                variables.loadData(dataBuffer);
            }

            for(EAttribute a : attributes){
                bufferEmpty=CSP.MIN_BOUND-1;
                if(verifMode) bufferEmpty=CSP.MIN_BOUND;
                
                EList<Integer> data = getAttributeValues(a);
                if(data.size()==0) continue;

                AttributeTable.AttributeTableRow variables = getAttributeVars(a);
                int cols = variables.size();
                int[] dataBuffer = new int[cols];
                for(int i=0;i<cols;i++) dataBuffer[i]=bufferEmpty;
                
                for(int i=0;i<data.size();i++) {
                    dataBuffer[i]=data.get(i);
                }

                variables.loadData(dataBuffer);
            }
        }


        public void variables2data(){
            for(EReference r : references){
                EList<EObject> buff = new BasicEList<>();
                int[] data = getReferenceVars(r).getData();
                for(int i=0; i<data.length;i++){
                    if(data[i]==nullptr().getValue()) continue;
                    buff.add(classPtr2Obj.get(r.getEReferenceType(), data[i]));
                }
                if(!buff.isEmpty()){
                    if(r.getUpperBound()==1) emfobject.eSet(r, buff.get(0));
                    else emfobject.eSet(r, buff);
                }
            }
            for(EAttribute a : attributes){
                EList<Integer> buff = new BasicEList<>();
                int[] data = getAttributeVars(a).getData();
                for(int i=0; i<data.length;i++){
                    if(data[i]==nullattrib().getValue()) continue;
                    buff.add(data[i]);
                }
                if(!buff.isEmpty()){
                    if(a.getUpperBound()==1) emfobject.eSet(a, buff.get(0));
                    else emfobject.eSet(a, buff);
                }
            }
        }
    }

    public void addEMFCSPObject(EObject e, EList<EReference> r, EList<EAttribute> a){
        objects.add(new EMFCSPObject(e, r, a));
    }

    public List<EMFCSPObject> getEMFCSPObjects(){
        return objects;
    }
}
