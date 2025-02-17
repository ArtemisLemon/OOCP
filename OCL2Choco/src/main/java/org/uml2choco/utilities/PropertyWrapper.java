package org.uml2choco.utilities;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class PropertyWrapper {

    static public EList<EObject> Links(EObject o,EReference r){
        Object towrap = o.eGet(r);
        EList<EObject> wrapper = new BasicEList<>();
        if(towrap==null){
            // System.out.println("Found a ref to null");
        } else if(towrap instanceof EObject){
            // System.out.println("Found a ref to a single Object");
            wrapper.add((EObject)towrap);
        } else if(towrap instanceof EList<?>){
            // System.out.println("Found a ref to a List of Objects");
            wrapper = (EList<EObject>)towrap;
        }
        return wrapper;
    }

    static public EList<Integer> Ints(EObject o,EAttribute a){
        Object towrap = o.eGet(a);
        // System.out.println("wrapping "+towrap);
        EList<Integer> wrapper = new BasicEList<>();
        if(towrap==null){
            // System.out.println("Found an unknown Attribute");
        } else if(towrap instanceof Integer){
            // System.out.println("Found a single Int");
            wrapper.add((Integer)towrap);
        } else if(towrap instanceof EList<?>){
            // System.out.println("Found a collection of Ints");
            wrapper = (EList<Integer>)towrap;
        }
        return wrapper;
    }
}
