package org.uml2choco.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
// Common
import java.nio.file.Path;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

// LoadXcore
// import org.eclipse.emf.ecore.xcore.XcoreStandaloneSetup;
// import org.eclipse.emf.ecore.EPackage;

// // LoadXMI
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

//LoadATL
import org.eclipse.m2m.atl.common.Problem.Problem;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.emftvm.Model;
import org.eclipse.m2m.atl.emftvm.compiler.AtlResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.util.OCLOperations;
import org.eclipse.m2m.atl.engine.parser.AtlParser;

import com.beust.jcommander.JCommander;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import org.eclipse.m2m.atl.common.ATL.Module;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.eclipse.m2m.atl.common.Problem.Problem;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.engine.parser.AtlParser;
import org.eclipse.m2m.atl.emftvm.compiler.AtlResourceFactoryImpl;




public class EMF2ChocoIO {
    static ResourceSetImpl rs;

    static public void initResourceSet(){
            rs = new ResourceSetImpl();
            rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
            rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
            rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("atl", new AtlResourceFactoryImpl());
    }

    // static void loadXCORE(String path){
    //         XcoreStandaloneSetup.doSetup();
    //         Resource r = rs.getResource(URI.createFileURI(path), true);
        
    //         // System.out.println(r);
        
    //         // return rs.getResource(URI.createFileURI(path), true);
    // }

    static public EPackage loadECORE(String path){
        return (EPackage) rs.getResource(URI.createURI(path), true).getContents().getFirst();
    }

    static public Resource loadXMI(EPackage pkg, String path) {
        rs.getPackageRegistry().put(pkg.getNsURI(),pkg);
        return rs.getResource(URI.createFileURI(path), true);
    }

    static public Module loadATL(String path){

        var parser = AtlParser.getDefault();
        var modelFactory = new EMFModelFactory();
        EMFModel problems = (EMFModel)modelFactory.newModel(parser.getProblemMetamodel());
        rs.getLoadOptions().put("problems", problems);

        try {
            var transfo = rs.getResource(URI.createURI(path), true);
            var fileName = Path.of(path).getFileName();

            for (var e : transfo.getErrors()) {
                System.err.println("error in " + fileName + ": " + e.getLine() + "-" + e.getColumn() + " - " + e.getMessage());
            }
            for (var e : transfo.getWarnings()) {
                System.err.println("warning in " + fileName + ": " + e.getLine() + "-" + e.getColumn() + " - " + e.getMessage());
            }

            return (Module)transfo.getContents().getFirst();

        } catch (Exception e) {
            System.err.println("Error reading input file '" + path + "' : " + e.getMessage());
            for (var p : problems.getResource().getContents()) {
                if (p instanceof Problem pb) {
                    System.out.println(pb.getSeverity() + ": " + pb.getLocation() + " - " + pb.getDescription());
                }
            }
        }
        return null;
    }


    static public void saveXMI(String path, EObject rootObj){
        Resource res = rs.createResource(URI.createFileURI(path));
        res.getContents().add(rootObj);
        try{
            res.save(null);
        } catch (Exception e) {
            System.out.println("Problem Saving:\n"+e);
        }
    }
}
