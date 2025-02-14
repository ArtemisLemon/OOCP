package org.uml2choco;

import com.beust.jcommander.*;

public class CLI {

    @Parameter(names = { "-M", "--metamodel" }, description = "path/to/MetaModel.ecore")
    public String metamodel = "testmodels/zoo/zoo.ecore";

    @Parameter(names = { "-m", "--model" }, description = "path/to/Model.xmi")
    public String model = "testmodels/zoo/zoo.xmi";

    @Parameter(names = { "-c", "--ocl" }, description = "path/to/ObjectConstraints.atl")
    public String ocl  = "testmodels/zoo/zoo.atl";

    static public void main(String[] args){
        CLI cli = new CLI();
        JCommander.newBuilder().addObject(cli).build().parse(args);

        System.out.println(cli.metamodel);
        System.out.println(cli.model);
        System.out.println(cli.ocl);

        EMF2Choco compiler = new EMF2Choco();
        compiler.run(cli.metamodel,cli.model,cli.ocl);
    }
}
