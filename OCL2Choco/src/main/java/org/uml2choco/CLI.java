package org.uml2choco;

import com.beust.jcommander.*;

public class CLI {

    @Parameter(names = { "-M", "--metamodel" }, description = "path/to/MetaModel.ecore")
    public String metamodel;

    @Parameter(names = { "-m", "--model" }, description = "path/to/Model.xmi")
    public String model;

    @Parameter(names = { "-c", "--ocl" }, description = "path/to/ObjectConstraints.atl")
    public String ocl;

    static public void main(String[] args){
        CLI cli = new CLI();
        JCommander.newBuilder().addObject(cli).build().parse(args);

        System.out.println(cli.metamodel);
        System.out.println(cli.model);
        System.out.println(cli.ocl);
    }
}
