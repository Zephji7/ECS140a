import java.io.IOError;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NRDatalog{
    
    private String dataPath = null;
    private boolean noExecute = false;
    private boolean verbose = false;
    private boolean wallclockTime = false;
    private boolean parseTreeOutput = false;
    private boolean executionTreeOutput = false;
    private String datalogFile = null;
    private boolean argumentError = false;
    private int threadCount = 1;
    
    public NRDatalog(String [] args){
        boolean ShowHelp = false;
        int Index = 0;
        Map<String,String> Options = new HashMap<String,String>();
        Options.put("-d", "-d");
        Options.put("--datapath", "-d");
        Options.put("-h", "-h");
        Options.put("--help", "-h");
        Options.put("-m","-m");
        Options.put("--multithread","-m");
        Options.put("-n","-n");
        Options.put("--noexecute","-n");
        Options.put("-p","-p");
        Options.put("--parsedtree","-p");
        Options.put("-t","-t");
        Options.put("--outputtree","-t");
        Options.put("-v","-v");
        Options.put("--verbose","-v");
        Options.put("-w","-w");
        Options.put("--walltime","-w");

        while(Index < args.length){
            String Argument = args[Index];
            if(Argument.charAt(0) == '-'){
                if(!Options.containsKey(Argument)){
                    System.out.format("Syntax Error: Unknown option \"%s\"%n",Argument);
                    ShowHelp = true;
                    break;
                }
                switch(Options.get(Argument)){
                    case "-d":  if(dataPath != null){
                                    System.out.println("Syntax Error: Multiple data paths defined.");
                                    ShowHelp = true;
                                    break;
                                }
                                if(Index + 1 >= args.length){
                                    System.out.println("Syntax Error: No data path specified with \"\"");
                                    ShowHelp = true;
                                    break;
                                }
                                Index++;
                                dataPath = args[Index];
                                break;
                    case "-h":  ShowHelp = true;
                                break;
                    case "-m":  if(threadCount != 1){
                                    System.out.println("Syntax Error: Multiple thread counts defined.");
                                    ShowHelp = true;
                                    break;
                                }
                                if(Index + 1 >= args.length){
                                    System.out.println("Syntax Error: No thread count specified");
                                    ShowHelp = true;
                                    break;
                                }
                                Index++;
                                threadCount = Integer.parseInt(args[Index]);
                                break;
                    case "-n":  noExecute = true;
                                break;
                    case "-p":  parseTreeOutput = true;
                                break;
                    case "-t":  executionTreeOutput = true;
                                break;
                    case "-v":  verbose = true;
                                break;
                    case "-w":  wallclockTime = true;
                                break;
                    default:    break;
                }
            }
            else{
                if(datalogFile != null){
                    System.out.println("Syntax Error: Multiple query files specified.");
                    ShowHelp = true;
                    break;
                }
                datalogFile = Argument;
            }
            if(ShowHelp){
                break;
            }
            Index++;
        }
        if(datalogFile == null){
            if(!ShowHelp){
                System.out.println("Syntax Error: No query file specified.");
            }
            ShowHelp = true;
        }
        if(ShowHelp){
            argumentError = true;
        }
        if(dataPath == null){
            dataPath = ".";
        }
    }

    public String getDataPath(){
        return dataPath;
    }

    public int getThreadCount(){
        return threadCount;
    }

    public boolean getNoExecute(){
        return noExecute;
    }

    public boolean getVerbose(){
        return verbose;
    }

    public boolean getWallclockTime(){
        return wallclockTime;
    }

    public boolean getParseTreeOutput(){
        return parseTreeOutput;
    }

    public boolean getExecutionTreeOutput(){
        return executionTreeOutput;
    }

    public String getDatalogFile(){
        return datalogFile;
    }
    
    public boolean hasArgumentError(){
        return argumentError;
    }

    public void showHelp(){
        System.out.println(String.join(System.getProperty("line.separator"),
        "OVERVIEW: Non-Recursive Datalog",
        "USAGE: NRDatalog [-d data] [-h] [-m cnt] [-n] [-p] [-t] [-v] file",
        "",
        "OPTIONS:",
        "  -d (--datapath)    Specify the data directory",
        "  -h (--help)        Display this help message",
        "  -m (--multithread) Specify the number of threads to use",
        "  -n (--noexecute)   Only validate the query, do not execute",
        "  -p (--parsedtree)  Output the parsed query as a tree",
        "  -t (--outputtree)  Output the query as an execution tree",
        "  -v (--verbose)     Verbose output",
        "  -w (--walltime)    Measure wallclock time"));
    }

    public static void main(String [] args) throws IOException{
        NRDatalog DatalogOptions = new NRDatalog(args);

        if(DatalogOptions.hasArgumentError()){
            DatalogOptions.showHelp();
        }
        else{
            PeekableCharacterStream InStream = new PeekableCharacterFileStream(DatalogOptions.getDatalogFile());
            NRDatalogExecutionTree MyExecutionTree = new NRDatalogExecutionTree(InStream);
            
            MyExecutionTree.setDataPath(DatalogOptions.getDataPath());
            MyExecutionTree.setVerbose(DatalogOptions.getVerbose());            
            if(MyExecutionTree.parseQuery()){
                if(DatalogOptions.getParseTreeOutput()){
                    MyExecutionTree.outputParseTree(System.out);
                }
                if(DatalogOptions.getExecutionTreeOutput()){
                    MyExecutionTree.outputExecutionTree(System.out);
                }
                if(DatalogOptions.getNoExecute()){
                    System.out.format("%s is a valid NR Datalog file!%n", DatalogOptions.getDatalogFile());
                }
                else{
                    MyExecutionTree.setThreadCount(DatalogOptions.getThreadCount());
                    long startTime = System.nanoTime();
                    MyExecutionTree.executeQuery();
                    long stopTime = System.nanoTime();
                    if(DatalogOptions.getWallclockTime()){
                        System.out.format("Time: %.3f%n",(stopTime - startTime)/1e9);
                    }
                }
            }
            else{
                MyExecutionTree.printError(System.out);   
            }
        }

    }
}