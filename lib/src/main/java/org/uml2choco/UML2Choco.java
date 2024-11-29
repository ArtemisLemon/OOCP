package org.uml2choco;

import org.uml2choco.Context;
import org.uml2choco.atlocl2choco.OCL2Choco;
import org.uml2choco.xmi2choco.XMI2Choco;

public class UML2Choco {
    static void Load(String path2xmi);
    static void Load(String path2xmi, String path2ocl);
    static void Save(String path2xmi);
    static void Solve();
}
