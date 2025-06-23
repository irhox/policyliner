package de.tub.dima.policyliner.database.data;

public class UserDefinedFunction {
    private String functionName;
    private String functionArguments;

    public UserDefinedFunction(String functionName, String functionArguments) {
        this.functionName = functionName;
        this.functionArguments = functionArguments;
    }

    public UserDefinedFunction() {
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getFunctionArguments() {
        return functionArguments;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public void setFunctionArguments(String functionArguments) {
        this.functionArguments = functionArguments;
    }

    @Override
    public String toString() {
        return "UserDefinedFunction{" +
                "functionName='" + functionName + '\'' +
                ", functionArguments='" + functionArguments + '\'' +
                '}';
    }
}
