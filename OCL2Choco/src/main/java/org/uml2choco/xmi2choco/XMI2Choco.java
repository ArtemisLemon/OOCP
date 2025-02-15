package org.uml2choco.xmi2choco;

import org.uml2choco.EMFCSP;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;
import org.oclinchoco.property.ReferenceTable;
import org.oclinchoco.property.SingleIntTable;

public class XMI2Choco {

    static public void buildUMLCSP(EObject rootObj, EMFCSP csp){
        System.out.println("Building UML CSP");
        int classCount = rootObj.eClass().getEReferences().size();


        // Identifiy the Objects in the Problem
        System.out.println("Loading Objects");

        List<EClass> eclasses = new ArrayList<>();
        List<EObject> eobjects = new ArrayList<>();
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
                classptr2object.put(iClass,j+1,iInstances.get(j));
                csp.addObjPtr(iInstances.get(j), j+1);
                eobjects.add(iInstances.get(j));
            }
        }
        csp.setClassPtr2Object(classptr2object);

        // Identify the Attributes of the objects
        System.out.println("Loading Attributes");
        List<EAttribute> eattributes = new ArrayList<>();
        HashMap<EClass,EList<EAttribute>> class2attributes = new HashMap<>();
        Table<EObject,String,Integer> objattrib2int = HashBasedTable.create();


        for(EClass c : eclasses){
            class2attributes.put(c,c.getEAttributes());
            for(EAttribute a : c.getEAttributes()){
                if(!a.getEAttributeType().getInstanceClassName().equals("int")) continue;
                eattributes.add(a);
                int rows = class2objects.get(c).size();
                // System.out.println("Building Attribute Table for "+a.getName());
                csp.addAttributeTable(a, rows);

                for(EObject e : class2objects.get(c)){
                    objattrib2int.put(e, a.getName(), (int)e.eGet(a));
                }
            }
        }

        // Identify the References between objects
        System.out.println("Loading Links");
        List<EReference> ereferences = new ArrayList<>();
        HashMap<EClass,EList<EReference>> class2references = new HashMap<>();
        Table<EObject,String,EList<EObject>> objref2objlist = HashBasedTable.create(); //Rows are strings, because that's the all the info the OCL gives us, could problably do a bit of typing in the future
        // Table<EClass,EReference,RefTableInfo> classref2reftableinfo = HashBasedTable.create();

        for(EClass c : eclasses){
            // System.out.println("Class: "+c+"\n");
            class2references.put(c, c.getEReferences());
            // System.out.println("References: "+class2references.get(c)+"\n");

            for(EReference r : c.getEReferences()){
                ereferences.add(r);
                int src_count = class2objects.get(c).size();
                int tgt_count = class2objects.get(r.getEReferenceType()).size();
                // System.out.println("Building RefTable for "+r.getName()+" : "+c.getName()+"->"+r.getEReferenceType().getName());
                csp.addReferenceTable(r, src_count, tgt_count);
                
                // classref2reftableinfo.put(c,r, info); //With this we can make the RefTables


                //But now I want to save the data for every object, so that I can instanciate the RefTable values
                //And pass the solution back
                for(EObject o : class2objects.get(c)){
                    // System.out.println("Object: "+o+"\nReference: "+r+"\n -> "+o.eGet(r));
                    // List<EObject> wrapper;
                    Object towrap = o.eGet(r);
                    EList<EObject> wrapper = new BasicEList<>();
                    if(towrap==null){
                        // System.out.println("Found a ref to null\n");
                    } else if(towrap instanceof EObject){
                        // System.out.println("Found a ref to a single Object\n");
                        wrapper.add((EObject)towrap);
                    } else if(towrap instanceof EList<?>){
                        // System.out.println("Found a ref to a List of Objects\n");
                        wrapper = (EList<EObject>)towrap;
                    }
                    objref2objlist.put(o, r.getName(), wrapper);
                }
            }
        }

        System.out.println("Applying Opposite");
        List<EReference> appliedOpposite = new ArrayList<>();
        for(EReference r : ereferences){
            if(r.getEOpposite()!=null)
                if(appliedOpposite.contains(r.getEOpposite())==false){
                    // System.out.println(r.getName()+" opposite "+r.getEOpposite().getName());
                    ReferenceTable.Opposites(csp, csp.getRefTable(r.getName()), csp.getRefTable(r.getEOpposite().getName()));
                    appliedOpposite.add(r);
                }
        }

        // csp.printJustTheTables();

        System.out.println("Recording EMFCSP Objects");
        for(EObject o : eobjects){
            // System.out.println(o.eClass().getName()+csp.getObjPtr(o));
            EList<EReference> erefs = class2references.get(o.eClass());
            HashMap<EReference,EList<EObject>> ref2objects = new HashMap<>();
            HashMap<EReference,ReferenceTable.AdjList> var2objects = new HashMap<>();
            for(EReference r : erefs){
                ref2objects.put(r,objref2objlist.get(o,r.getName()));
                var2objects.put(r,csp.getRefTable(r.getName()).adjList(csp.getObjPtr(o)));
            }

            EList<EAttribute> eatts = class2attributes.get(o.eClass());
            EList<EAttribute> modeledatts = new BasicEList<>();
            HashMap<EAttribute,Integer> att2int = new HashMap<>();
            HashMap<EAttribute,SingleIntTable.SingleIntAttribute> att2var =new HashMap<>();
            for(EAttribute a : eatts){
                if(!a.getEAttributeType().getInstanceClassName().equals("int")) continue;
                modeledatts.add(a);
                att2int.put(a, objattrib2int.get(o,a.getName()));
                att2var.put(a,csp.getAttribTable(a.getName()).singleattribute(csp.getObjPtr(o)));
            }

            csp.addEMFCSPObject(o,erefs,ref2objects,var2objects,modeledatts,att2int,att2var);
        }

        System.out.println("Loading data for EMFCSP Objects");
        for(EMFCSP.EMFCSPObject o : csp.getEMFCSPObjects()){
            // System.out.println("Loading data for "+o.emfobject);
            o.data2variables();
        }

        System.out.println("Building UML CSP Finished");
    }
}
