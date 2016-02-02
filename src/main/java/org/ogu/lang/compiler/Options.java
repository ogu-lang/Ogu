package org.ogu.lang.compiler;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Compiler Options
 * Created by ediaz on 20-01-16.
 */
public class Options {

    public String getDestinationDir() {
        return destinationDir;
    }

    public void setDestinationDir(String destinationDir) {
        this.destinationDir = destinationDir;
    }

    public List<String> getClassPathElements() {
        return classPathElements;
    }

    public void setClassPathElements(List<String> classPathElements) {
        this.classPathElements = classPathElements;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public boolean isParseOnly() { return parseOnly; }

    public void setParseOnly(boolean parseOnly) { this.parseOnly = parseOnly; }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    // use to compile without core libs
    @Parameter(names = {"-nc", "--nocore"})
    private boolean nocore = true;

    @Parameter(names = {"-p", "--parse"})
    private boolean parseOnly = false;

    @Parameter(names = {"-o", "--output"})
    private String destinationDir = "ogu_classes";

    @Parameter(names = {"-cp", "--classpath"}, variableArity = true)
    private List<String> classPathElements = new ArrayList<>();

    @Parameter(names = {"-v", "--verbose"})
    private boolean verbose = false;

    @Parameter(names = {"-d", "--debug"})
    private boolean debug = false;

    @Parameter(names = {"-h", "--help"})
    private boolean help = false;

    @Parameter(description = "Files or directories to compile")
    private List<String> sources = new ArrayList<>();
}