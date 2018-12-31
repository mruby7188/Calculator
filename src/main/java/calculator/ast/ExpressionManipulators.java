package calculator.ast;

import calculator.interpreter.Environment;
import calculator.errors.EvaluationError;
import datastructures.interfaces.IDictionary;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;

/**
 * All of the public static methods in this class are given the exact same parameters for
 * consistency. You can often ignore some of these parameters when implementing your
 * methods.
 *
 * Some of these methods should be recursive. You may want to consider using public-private
 * pairs in some cases.
 */
public class ExpressionManipulators {
    /**
     * Checks to make sure that the given node is an operation AstNode with the expected
     * name and number of children. Throws an EvaluationError otherwise.
     */
    private static void assertNodeMatches(AstNode node, String expectedName, int expectedNumChildren) {
        if (!node.isOperation()
                && !node.getName().equals(expectedName)
                && node.getChildren().size() != expectedNumChildren) {
            throw new EvaluationError("Node is not valid " + expectedName + " node.");
        }
    }

    /**
     * Accepts an 'toDouble(inner)' AstNode and returns a new node containing the simplified version
     * of the 'inner' AstNode.
     *
     * Preconditions:
     *
     * - The 'node' parameter is an operation AstNode with the name 'toDouble'.
     * - The 'node' parameter has exactly one child: the AstNode to convert into a double.
     *
     * Postconditions:
     *
     * - Returns a number AstNode containing the computed double.
     *
     * For example, if this method receives the AstNode corresponding to
     * 'toDouble(3 + 4)', this method should return the AstNode corresponding
     * to '7'.
     * 
     * This method is required to handle the following binary operations
     *      +, -, *, /, ^
     *  (addition, subtraction, multiplication, division, and exponentiation, respectively) 
     * and the following unary operations
     *      negate, sin, cos
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if any of the expressions uses an unknown operation.
     */
    public static AstNode handleToDouble(Environment env, AstNode node) {
        // To help you get started, we've implemented this method for you.
        // You should fill in the locations specified by "your code here"
        // in the 'toDoubleHelper' method.
        //
        // If you're not sure why we have a public method calling a private
        // recursive helper method, review your notes from CSE 143 (or the
        // equivalent class you took) about the 'public-private pair' pattern.

        assertNodeMatches(node, "toDouble", 1);
        AstNode exprToConvert = node.getChildren().get(0);
        return new AstNode(toDoubleHelper(env.getVariables(), exprToConvert));
    }

    private static double toDoubleHelper(IDictionary<String, AstNode> variables, AstNode node) {
        if (node.isNumber()) {
            return node.getNumericValue();
        } else if (node.isVariable()) {
            if (!variables.containsKey(node.getName())) {
                throw new EvaluationError(node.getName() + " is an undefined variable");
            }
            return toDoubleHelper(variables, variables.get(node.getName()));
        } else {
            // You may assume the expression node has the correct number of children.
            // If you wish to make your code more robust, you can also use the provided
            // "assertNodeMatches" method to verify the input is valid.z
           
            String name = node.getName();
            IList<AstNode> children = node.getChildren();
            IList<AstNode> tempChild = new DoubleLinkedList<AstNode>();
            tempChild.add(new AstNode(toDoubleHelper(variables, children.get(0))));
            if (children.size() > 1) {
                tempChild.add(new AstNode(toDoubleHelper(variables, children.get(1))));

            }
            if (isSimpleMath(name)) {
                return simpleMath(tempChild, name);
            } else if (isMath(name)) {
                return otherOperations(tempChild, name);
            }
            else {
                throw new EvaluationError(name + "is an unknown operator");
            }
        }
        
    }
    
    private static double otherOperations(IList<AstNode> children, String name) {
        if (name.equals("/")) {
            return children.get(0).getNumericValue() / children.get(1).getNumericValue();
        } else if (name.equals("^")) {
            return Math.pow(children.get(0).getNumericValue(), children.get(1).getNumericValue());
        } else if (name.equals("cos")) {
            return Math.cos(children.get(0).getNumericValue());
        } else if (name.equals("sin")) {
            return Math.sin(children.get(0).getNumericValue());
        } else {
            return (-1) * children.get(0).getNumericValue();
        }
    }

    /**
     * Accepts a 'simplify(inner)' AstNode and returns a new node containing the simplified version
     * of the 'inner' AstNode.
     *
     * Preconditions:
     *
     * - The 'node' parameter is an operation AstNode with the name 'simplify'.
     * - The 'node' parameter has exactly one child: the AstNode to simplify
     *
     * Postconditions:
     *
     * - Returns an AstNode containing the simplified inner parameter.
     *
     * For example, if we received the AstNode corresponding to the expression
     * "simplify(3 + 4)", you would return the AstNode corresponding to the
     * number "7".
     *
     * Note: there are many possible simplifications we could implement here,
     * but you are only required to implement a single one: constant folding.
     *
     * That is, whenever you see expressions of the form "NUM + NUM", or
     * "NUM - NUM", or "NUM * NUM", simplify them.
     */
    public static AstNode handleSimplify(Environment env, AstNode node) {
        // Try writing this one on your own!
        // Hint 1: Your code will likely be structured roughly similarly
        //         to your "handleToDouble" method
        // Hint 2: When you're implementing constant folding, you may want
        //         to call your "handleToDouble" method in some way
        // Hint 3: When implementing your private pair, think carefully about
        //         when you should recurse. Do you recurse after simplifying
        //         the current level? Or before?

        assertNodeMatches(node, "simplify", 1);
        return simplifyHelper(env.getVariables(), node.getChildren().get(0));
    }
    
    private static AstNode simplifyHelper(IDictionary<String, AstNode> variables, AstNode node) { 
        if (node.isNumber()) {
            return new AstNode(node.getNumericValue());
        }
        if (node.isVariable()) {
            if (!variables.containsKey(node.getName())) {
               return new AstNode(node.getName());
            }
            AstNode temp = simplifyHelper(variables, variables.get(node.getName()));
            if (!temp.isNumber()) {
                return simplifyHelper(variables, temp);
            } else {
                return new AstNode(temp.getNumericValue());
            }
        } else {
            IList<AstNode> tempChild = node.getChildren();
            IList<AstNode> out = new DoubleLinkedList<AstNode>();
            out.add(simplifyHelper(variables, tempChild.get(0)));
            if (tempChild.size() > 1) {
                out.add(simplifyHelper(variables, tempChild.get(1)));
            }
            if (isSimpleMath(node.getName())) {
                if (out.get(0).isNumber() && out.get(1).isNumber()) {
                    return new AstNode(simpleMath(out, node.getName()));
                }
            }            
            return new AstNode(node.getName(), out);
        }        
    }

    private static boolean isSimpleMath(String name) {
        return (name.equals("+") || name.equals("-") || name.equals("*"));
    }
    
    private static boolean isOtherMath(String name) {
        return (name.equals("sin") || name.equals("cos") || name.equals("^") || 
                name.equals("negate") || name.equals("/"));
    }

    private static boolean isMath(String name) {
        return (isSimpleMath(name) || isOtherMath(name));
    }
    
    private static double simpleMath(IList<AstNode> children, String operation) {
        if (operation.equals("+")) {
            return children.get(0).getNumericValue() + children.get(1).getNumericValue();
        } else if (operation.equals("-")) {
            return children.get(0).getNumericValue() - children.get(1).getNumericValue();
        } else {
            return children.get(0).getNumericValue() * children.get(1).getNumericValue();
        }
    }
    
    /**
     * Accepts an Environment variable and a 'plot(exprToPlot, var, varMin, varMax, step)'
     * AstNode and generates the corresponding plot on the ImageDrawer attached to the
     * environment. Returns some arbitrary AstNode.
     *
     * Example 1:
     *
     * >>> plot(3 * x, x, 2, 5, 0.5)
     *
     * This method will receive the AstNode corresponding to 'plot(3 * x, x, 2, 5, 0.5)'.
     * Your 'handlePlot' method is then responsible for plotting the equation
     * "3 * x", varying "x" from 2 to 5 in increments of 0.5.
     *
     * In this case, this means you'll be plotting the following points:
     *
     * [(2, 6), (2.5, 7.5), (3, 9), (3.5, 10.5), (4, 12), (4.5, 13.5), (5, 15)]
     *
     * ---
     *
     * Another example: now, we're plotting the quadratic equation "a^2 + 4a + 4"
     * from -10 to 10 in 0.01 increments. In this case, "a" is our "x" variable.
     *
     * >>> c := 4
     * 4
     * >>> step := 0.01
     * 0.01
     * >>> plot(a^2 + c*a + a, a, -10, 10, step)
     *
     * ---
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if varMin > varMax
     * @throws EvaluationError  if 'var' was already defined
     * @throws EvaluationError  if 'step' is zero or negative
     */
    public static AstNode plot(Environment env, AstNode node) {
        assertNodeMatches(node, "plot", 5);
        String var = node.getChildren().get(1).getName();        
        IDictionary<String, AstNode> variables = env.getVariables();
        if (variables.containsKey(var)) {
            throw new EvaluationError(var + "is already defined.");
        }
        IList<AstNode> plot = node.getChildren();
        double min = toDoubleHelper(variables, plot.get(2));
        double max = toDoubleHelper(variables, plot.get(3));
        if (min > max) {
            throw new EvaluationError("Minimum (" + min + 
                    ") is greater than the maximum (" + max + ").");
        }
        double step = toDoubleHelper(variables, plot.get(4));
        if (step <= 0) {
            throw new EvaluationError("step (" + step + ") must be greater than zero.");
        }
        AstNode function = node.getChildren().get(0);
        IList<Double> xValues = new DoubleLinkedList<Double>();        
        IList<Double> yValues = new DoubleLinkedList<Double>();
        for (double i = min; i <= max; i += step) {
            env.getVariables().put(var, new AstNode(i));
            xValues.add(i);
            yValues.add(toDoubleHelper(env.getVariables(), function));
        }

        env.getVariables().remove(var);
        env.getImageDrawer().drawScatterPlot("", var, "response", xValues, yValues);
        return new AstNode(1);
    }
    
    public static AstNode solve(AstNode node, String var) {
    	
    }
    
    private boolean equal(AstNode x, AstNode y) {
    	
    }
}