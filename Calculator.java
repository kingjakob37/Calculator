import java.util.Scanner;

class Node {
	
	//Here are the variables needed.
	String data;
	Node next;
	Node prev;
	
	Node(String data) {
		this.data = data;
		this.next = null;
		this.prev = null;
	}
	
}

class Stack {
	//Here we have our variables for our stack
	//The originHead helps guide the stack back to the original value
	//The stackHead keeps the head at the last value
	//The tempHead is used for a temporary head when needed
	private Node originHead;
	private Node stackHead;
	private Node tempHead;
	private int capacity;
	private boolean nodeInitialized = false;
	
	//The stack class sets its values, creating the originHead
	//setting the stackHead to the originHead, and creating a capacity.
	public Stack(int size) {
		originHead = new Node(null);
		stackHead = originHead;
		capacity = size;
	}
	
	//This checks whether the stack is empty.
	public boolean isEmpty() {
		boolean empty = false;
		if (originHead.next == null)
			//We only need to check one value, the origin value. If it is empty, then we return "true."
			if (originHead.data == null || originHead.data.equals("")) {
				empty = true;
			}
		
		return empty;
	}
	
	//This checks whether it is full.
	public boolean isFull() {
		//This resets the stack to the originHead.
		stackHead = originHead;
		
		//Now we loop through the .next values until it is no longer possible.
		for (int i = 0; i < capacity; i++) {
			if (stackHead.next == null) {

				//If the loop completes, then the stack should be full.
				if (i == capacity - 1)
					return true;
				else
					//If the last value is equal to 0, then it is not full.
					return false;
			}
			else
				//If the loop was returned before the loop was finished, then it is returned as "not full."
				if (stackHead.data == "")
					return false;
				else
					stackHead = stackHead.next;
		}
		
		return true;
	}
	
	//This inserts a value into the stack.
	public void push(String data) {
		if (!nodeInitialized) {
			originHead.data = data;
			stackHead = originHead;
			nodeInitialized = true;
		}
		else {
			if (isEmpty()) {
				Node newNode = new Node(data);
				originHead = newNode;
				stackHead = originHead;
			}
			else if (!isFull()) {
				Node newNode = new Node(data);
				tempHead = stackHead;
				stackHead.next = newNode;
				stackHead = newNode;
				stackHead.prev = tempHead;
			}
			else
				//This checks if the stack is full.
				System.out.println("Stack is full.");
		}
	}
	
	//This takes out the last value inputed into the stack.
	public String pop() {
		if (!isEmpty()) {
			//If not empty, check if the last node has a value.
			String returnValue = stackHead.data;
			if (stackHead.prev != null) {
				stackHead = stackHead.prev;
				stackHead.next = null;
				return returnValue;
			}
			else {
				//If it is the last value, make the value equal to 0.
				stackHead.data = "";
				return returnValue;
			}
		}
		else
			//If empty, then return zero and indicate empty.
			return "";
	}
	
	//Return the most recent value.
	public String peek() {
		return stackHead.data;
	}
	
	//Set the originHead.data to zero and take away its .next value.
	//Set stackHead and tempHead to originHead.
	public void MakeEmpty() {
		originHead.next = null;
		originHead.data = "";
		stackHead = originHead;
		tempHead = originHead; 
	}

	//This just prints out the stack contents for testing purposes.	
    public void printOut() {
        tempHead = stackHead;
        
        while(tempHead.prev != null) {
        	System.out.print(tempHead.data + " ");
        	tempHead = tempHead.prev;
        }
    	System.out.println(tempHead.data);
    }
    
    public int getCapacity() {
    	return capacity;
    }
}





public class Calculator {

	public static void main(String[] args) {
		
		//Here we use scanner to take user input
		Scanner scnr = new Scanner(System.in);
		System.out.println("Type your mathematical equation.");
		
		//Gather user input
		String input = scnr.nextLine();
		
		//Now we create a stack by sending our input into our infixToPostfix
		Stack stack = infixToPostfix(input);
		
		if (stack != null) {
			System.out.println("\nPostifx stack:");
			stack.printOut();
			//By getting the result, we can do final checks on the result before providing the official output to the user
			String result = evaluate(stack);
			if (result != null) {
				System.out.println("The final result is: " + result);
			}
		}
	}
	
	private static String evaluate(Stack stack) {
		if (checkIfLastValue(stack)) {
			return stack.peek();
		}
		
		Stack tempStack = new Stack(stack.getCapacity());
		
		while(!checkForOperator(stack.peek()) && !stack.isEmpty()) {
			tempStack.push(stack.pop());
		}
		
		String string2 = tempStack.pop();
		String string1 = tempStack.pop();
		String string3;
		String operator = stack.pop();
		
		int num1 = Integer.parseInt(string1);
		int num2 = Integer.parseInt(string2);
		int num3 = 0;
		
		if (operator.equals("+")) {
			num3 = num1 + num2;
			//System.out.println(num3 + " = " + num1 + "+" + num2);
		}
		else if (operator.equals("-")) {
			num3 = num1 - num2;
			//System.out.println(num3 + " = " + num1 + "-" + num2);
		}
		else if (operator.equals("*")) {
			//System.out.println(num3 + " = " + num1 + "/" + num2);
			num3 = num1 * num2;
		}
		else if (operator.equals("/")) {
			if (num2 == 0) {
				errorCase("DIVIDE BY 0");
				return null;
			}
			else {
				num3 = num1 / num2;
				
				//Dividing with integers is not very nice, especially when something like 5/5 should equal 1
				//In a scenario like 5/5, using int values would give 0
				//So I made a special case so that a number dividing itself would equal 1.
				if (string1.equals(string2))
					num3 = 1;
				//System.out.println(num3 + " = " + num1 + "*" + num2);
			}
		}
		
		string3 = String.valueOf(num3);
		stack.push(string3);
		
		while (!tempStack.isEmpty()) {
			stack.push(tempStack.pop());
		}
		
		return evaluate(stack);
	}

	public static Stack infixToPostfix(String input) {
		//We get rid of all the spaces within the input
		input = input.replaceAll(" ", "");
		
		//now we create variables
		char[] acceptedOperations = acceptedOperators();
		char[] acceptedNumbers = new char[10];
		
		//Tokenize the input by putting it into an array, eqArray
		char[] eqArray = input.toCharArray();
		
		//This continues setting up accepted values, but it inserts 0-9 by converting ASCII value to char.		
		for (int i = 0; i < acceptedNumbers.length; i++) {
			acceptedNumbers[i] = (char)(48 + i);
		}
		
		//We go through each value in the 
		for (int i = 0; i < eqArray.length; i++) {
			boolean acceptedValue = false;
			
			//Take the value and search through accepted numbers
			//If in accepted numbers, then set the parameter to true so that it will accept the value
			for (int j = 0; j < acceptedNumbers.length; j++) {
				if (eqArray[i] == acceptedNumbers[j])
					acceptedValue = true;
			}
			
			//Take the value and search through operators numbers
			//If in accepted operators, then set the parameter to true so that it will accept the value
			for (int j = 0; j < acceptedOperations.length; j++) {
				if (eqArray[i] == acceptedOperations[j])
					acceptedValue = true;
			}
			
			//If the value is neither in accepted operators or accepted numbers, then return null
			//Returning null provides a syntax error to the user
			if (!acceptedValue)
				return errorCase("INVALID");
		}
		
		//This cycles through eqArray to perform last minute analysis and changes
		//We use lastValue to record the last value of the previous iteration
		char lastValue = eqArray[0];
		
		for (int i = 1; i < eqArray.length; i++) {
			//Here we check for syntax error
			//If the previous value was an operator and the current value is an operator, then trigger syntax error
			if (checkForOperator(String.valueOf(lastValue))) {
				if (getRank(String.valueOf(lastValue)) > 0 && checkForOperator(String.valueOf(eqArray[i]))) {
					if (getRank(String.valueOf(eqArray[i])) > 0) {
						return errorCase("SYNTAX");
					}
				}
			}
			
			lastValue = eqArray[i];
		}
		
		//Here we check if the initial value is an operator
		if (checkForOperator(String.valueOf(eqArray[0]))) {
			
			//If it is an operator, check to make sure it is not parenthesis
			if (getRank(String.valueOf(eqArray[0])) > 0) {

				//We make a special case to check if the beginning number is negative
				//If the length is less than or equal to 1, then it is impossible for the input to be negative
				if (eqArray.length > 1) {

					//If it is not negative, then send create a syntax error
					if (!(String.valueOf(eqArray[0]).equals("-") && !checkForOperator(String.valueOf(eqArray[1])))) {
						return errorCase("SYNTAX");
					}
				}
				else {
					return errorCase("SYNTAX");
				}
			}
		}
		//Here we check if the initial value is an operator
		if (checkForOperator(String.valueOf(eqArray[eqArray.length - 1]))) {
			
			//If it is an operator, check to make sure it is not parenthesis
			if (getRank(String.valueOf(eqArray[eqArray.length - 1])) > 0) {
				return errorCase("SYNTAX");
			}
		}
		
		//Now we check to see if closing parenthesis match opening parenthesis
		int parenthesisCount = 0;
		for (int i = 0; i < eqArray.length; i++) {
			if (eqArray[i] == '(')
				parenthesisCount--;
			if (eqArray[i] == ')')
				parenthesisCount++;
		}
		if (parenthesisCount > 0) {
			return errorCase("SYNTAX");
		}
		

		eqArray = impliedParenthesis(eqArray);
		
		//Here we check to see if a number is next to a parenthesis without any operation
		//Normally, this would mean that there is an implied multiplicative operation
		//So this function adds in a multiplicative operator if it detects a digit next to a parenthesis
		eqArray = parenthesisMultiplication(eqArray);
			
		//Now we create a string array to put the eqArray values into stringArray
		//This is so we can have multiple digit values
		String[] stringArray = new String[eqArray.length];
		
		//We put values directly into the stringArray from eqArray
		for (int i = 0; i < eqArray.length; i++) {
			stringArray[i] = String.valueOf(eqArray[i]);
		}
		
		stringArray = createNegatives(stringArray);
		
		//We create int values to help analyze stringArray
		
		int startIndex = 0;
		int endIndex = 0;
		//Set segments = 1 because there should always be at least 1 term
		int segments = 1;
		
		//This loop helps to create multiple digit values
		//It will combine consecutive digits to be one single number
		//ex) [1,2,3] becomes [123, null, null]
		for (int i = 0; i < stringArray.length; i++) {
			//This boolean value helps to keep track of whether we find an operator
			boolean operatorDetected = false;
			
			//If we are on the last value of the stringArray, then run this
			if (i == stringArray.length - 1) {
				operatorDetected = true;
				//We set a new value for endIndex to tell it when to stop combining values
				endIndex = i + 1;
			}
			
			if (checkForOperator(stringArray[i])) {
				if (!(i == 0 && stringArray[i].equals("-"))){
					//We add two segments because every operator must also include another term
					segments = segments + 2;
					operatorDetected = true;
					//We set a new value for endIndex to tell it when to stop combining values
					endIndex = i;
				}
			}
			
			//Now we combine values, depending on if operatorDetected is set to true
			if (operatorDetected) {
				//We loop through the index of stringArray, provided by start index, all the way to end index
				for (int j = startIndex + 1; j < endIndex; j++) {
					stringArray[startIndex] = stringArray[startIndex] + stringArray[j];
				}
				//Now we set the values that were added to null, that way we get rid of them
				for (int j = startIndex + 1; j < endIndex; j++) {
					stringArray[j] = null;
				}
				
				//we now set start index to be the next iteration of i
				startIndex = i + 1;
			}
		}
		
		//Now we create our stacks
		//All values will eventually be put into stack
		Stack stack = new Stack(segments);
		//Put numbers in the numberStack
		Stack numberStack = new Stack(segments);
		//Put operators in the operatorStack
		Stack operatorStack = new Stack(segments);
		
		for (int i = 0; i < stringArray.length; i++) {
			//This checks to ensure that the value at index i is not null
			//This allows multiple digits to be inputed as one value
			if (stringArray[i] != null) {
				
				//If it is an operator, then do this
				if (checkForOperator(stringArray[i])) {
					//If the index reads "(" then put it into the stack
					if (stringArray[i].equals("(")) {
						operatorStack.push(stringArray[i]);
					}
	
					//if it sees ")" then pop all values in the stack into a tempStack1
					//When it sees a "(", then stop and pop the "("
					else if (stringArray[i].equals(")")) {
						Stack tempStack1 = new Stack(segments);
						Stack tempStack2 = new Stack(segments);
						while (!operatorStack.isEmpty()) {
							if (operatorStack.peek().equals("(")) {
								operatorStack.pop();
	
								//Pop all values in tempStack1 to tempStack2 and then pop those values into the numberStack
								//This is so that it flips the stack order back into correct position							
								while (!tempStack1.isEmpty()) {
									tempStack2.push(tempStack1.pop());
								}
								
								while (!tempStack2.isEmpty()) {
									numberStack.push(tempStack2.pop());
								}
								
								break;
							}
							tempStack1.push(operatorStack.pop());
						}
					}
					
					//If the operatorStack is empty
					//Or if the the top value in operator stack is not greater than the stringArray[i]
					//Or if there is parenthesis in the stack
					//Then push the value into operatorStack
					else if (operatorStack.isEmpty() || !compareOperator(operatorStack.peek(), stringArray[i]) || parenthesisInStack(operatorStack)){
						operatorStack.push(stringArray[i]);
					}
					
					//Or if none apply, then do this
					else {
						Stack tempStack = new Stack(operatorStack.getCapacity());
						boolean loopBroken = false;
						
						//Pop all values from operator stack into the String array
						while (!operatorStack.isEmpty()) {
							
							//If operator precedence in stack is less than the stringArray, then stop the while loop and do this
							if (!compareOperator(operatorStack.peek(), stringArray[i])) {
								operatorStack.push(stringArray[i]);
								loopBroken = true;
								break;
							}
							
							//Push the popped values from operatorStack into numberStack
							numberStack.push(operatorStack.pop());
						}
						
						//If the loop wasn't broken, then push the stringArray[i] value into operatorStack
						if (!loopBroken) {
							operatorStack.push(stringArray[i]);
						}
					}
				}
				
				//If it is not an operator, push the number into the stack
				else {
					numberStack.push(stringArray[i]);
				}
			}
			
		}
		
		//Here we push all the operatorStack values into the numberStack to put all statements into a single stack
		while(!operatorStack.isEmpty()) {
			numberStack.push(operatorStack.pop());
		}
		
		//But since the single stack is backwards, we can pop all values and push them into a new stack to basically flip the stack around in the correct orientation
		while(!numberStack.isEmpty()) {
			stack.push(numberStack.pop());
		}
		
		return stack;
	}

	//This function creates implied parenthesis
	//ex) input 1*(2+3 is entered
	//This function assumes there should be a closing parenthesis on the end by putting them there
	//The output would be 1*(2+3)
	private static char[] impliedParenthesis(char[] oldArray) {
		int parenthesisCount = 0;
		
		//Here we count how many '(''s there are
		for (int i = 0; i < oldArray.length; i++) {
			if (oldArray[i] == '(') {
				parenthesisCount++;
			}
			if (oldArray[i] == ')') {
				parenthesisCount--;
			}
		}

		//now we create a newArray with the added closing parenthesis
		char[] newArray = new char[oldArray.length + parenthesisCount];
		
		//Now we plug in the old values into the new array
		for (int i = 0; i < oldArray.length; i++) {
			newArray[i] = oldArray[i];
		}
		
		//And we create the final closing parenthesis
		for (int i = oldArray.length; i < newArray.length; i++) {
			newArray[i] = ')';
		}
		
		//And we return the new array
		return newArray;
	}

	//This method finds rogue minus operations to turn them into negatives
	private static String[] createNegatives(String[] oldArray) {
		//We can keep track of the previous value using this
		String lastValue = oldArray[0];
		
		//Now loop through the array
		for (int i = 1; i < oldArray.length; i++) {
			//To ensure the intention of the '-' sign was to be negative, the following conditions should be met
			//The previous value was an operator
			//The current value is a '-' sign
			//The next value is an operand
			if (checkForOperator(lastValue)) {
				if (checkForOperator(oldArray[i])) {
					if (oldArray[i].equals("-")) {
						if (!checkForOperator(oldArray[i+1])) {
							//If all conditions are met, then create a new array and put all the old array values in
							//Except the minus sign is deleted and the digit following the minus sign is turned negative
							
							String[] newArray = new String[oldArray.length - 1];
							
							int offset = 0;
							for (int j = 0; j < newArray.length; j++) {
								newArray[j] = oldArray[j + offset];
								
								if (j == i) {
									newArray[j] = String.valueOf(-1 * Integer.parseInt(oldArray[j+1]));
									offset++;
								}
							}
							
							//Call the function again with the new array
							return createNegatives(newArray);
						}
					}
				}
			}
			lastValue = oldArray[i];
		}
		
		return oldArray;
	}

	private static char[] parenthesisMultiplication(char[] oldArray) {
		//We create a char value called lastValue to track the previous iteration's value
		char lastValue = oldArray[0];
		
		//Now we loop through the old array
		for (int i = 1; i < oldArray.length; i++) {
			
			//If we see that the current value is an operator, then start the other checks
			if (checkForOperator(String.valueOf(oldArray[i]))) {
				
				//If the previous value was a digit, and the next value is a '(', then do this
				if (!checkForOperator(String.valueOf(lastValue)) && String.valueOf(oldArray[i]).equals("(") ) {
					
					//We create a new array, which has a length of 1 greater than the old array length
					char[] newArray = new char[oldArray.length + 1];
					
					//We use a value called offset to insert the correct values into newArray when needed
					int offset = 0;
					
					//Now we just put values into newArray
					for (int j = 0; j < oldArray.length; j++) {
						//If we have now reached the point where the last value was a digit and the current i value is a '('
						//Then we will manually set the current value to '*' and set offset to 1
						//This basically adds in a '*' value in between the digit and the '('
						if (j == i) {
							newArray[j] = '*';
							offset++;
						}
						
						newArray[j + offset] = oldArray[j];
					}
					
					//Now we recall the method with the new array to check if there are any other digits next to '('
					return parenthesisMultiplication(newArray);
				}
			}
			
			//This basically does the exact same thing as the last if statement
			//But instead it checks for if the last value was a ')' and the current value is a digit
			if (checkForOperator(String.valueOf(lastValue))) {
				
				//If the previous value was a ')', and the current value is a digit, then do this
				if (!checkForOperator(String.valueOf(oldArray[i])) && String.valueOf(lastValue).equals(")") ) {
					
					//We create a new array, which has a length of 1 greater than the old array length
					char[] newArray = new char[oldArray.length + 1];
					
					//We use a value called offset to insert the correct values into newArray when needed
					int offset = 0;
					
					//Now we just put values into newArray
					for (int j = 0; j < oldArray.length; j++) {
						//If we have now reached the point where the last value was ')' and the current i value is a digit
						//Then we will manually set the current value to '*' and set offset to 1
						//This basically adds in a '*' value in between the digit and the ')'
						if (j == i) {
							newArray[j] = '*';
							offset++;
						}
						
						newArray[j + offset] = oldArray[j];
					}
					
					//Now we recall the method with the new array to check if there are any other digits next to ')'
					return parenthesisMultiplication(newArray);
				}
			}
			lastValue = oldArray[i];
		}
		
		return oldArray;
	}

	//Heres our error messages
	//This just makes it a little more organized to create error messages and edit them if we put them in a separate method
	public static Stack errorCase(String error) {
		System.out.println("ERROR: " + error);
		
		//If the error was due to an invalid input, then put this message out
		if (error.equals("INVALID")) {
			System.out.println("Please only use either whole numbers or accepted operations [+,-,*,/,(,)]");
		}
		
		//If the error was due to an incorrect syntax, then put this message out
		else if (error.equals("SYNTAX")){
			System.out.println("Incorrect syntax entered.");
		}
		else if (error.equals("DIVIDE BY 0")) {
			System.out.println("Do not divide by 0.");
		}
		
		return null;
	}
	
	//This is a method to get the accepted operator values
	//It's easier to do it like this so we dont have to manually put in accepted operators for every method
	public static char[] acceptedOperators(){
		char[] acceptedOperations = new char[6];
		acceptedOperations[0] = '(';
		acceptedOperations[1] = ')';
		acceptedOperations[2] = '*';
		acceptedOperations[3] = '/';
		acceptedOperations[4] = '+';
		acceptedOperations[5] = '-';
		
		return acceptedOperations;
	}
	
	//We get a rank for the input, which should be an operator
	public static int getRank(String string) {
		char[] operations = acceptedOperators();
		char input = string.charAt(0);
		int index = 0;
		int rank = 0;
		
		//We search through the accepted operations array to find and index
		//We will then decide rank based on index
		for (int i = 0; i < operations.length; i++) {
			if (input == operations[i])
				index = i;
		}
		
		//"(" and ")" should be the lowest value, as in the highest precedence
		//"*" and "/" should have a middle value, so they have a precedence in between parenthesis and plus and minus
		//"+" and "-" should be the highest value, as in the lowest precedence
		if (index == 0)
			rank = 0;
		else if (index == 1)
			rank = 0;
		else if (index == 2)
			rank = 1;
		else if (index == 3)
			rank = 1;
		else if (index == 4)
			rank = 2;
		else if (index == 5)
			rank = 2;
		
		return rank;
	}
	
	public static boolean checkForOperator(String input) {
		char[] operationArray = acceptedOperators();
		
		//We search for our input within the operationArray
		//If it is within the array, return true
		//Otherwise, return false
		for (int i = 0; i < operationArray.length; i++) {
			if (input.equals(String.valueOf(operationArray[i]))) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean compareOperator(String value1, String value2) {
		//Get the ranks for value1 and value2
		int value1Rank = getRank(value1);
		int value2Rank = getRank(value2);
		
		if (value1Rank > value2Rank)
			//Value2 has greater precedence
			return false;
		
		//Value1 has greater precedence
		return true;
	}
	
	public static boolean parenthesisInStack(Stack stack1) {
		//Create temporary stack to store stack1 values in
		Stack tempStack = new Stack(stack1.getCapacity());
		
		//Set a value to return
		boolean parenthesisFound = false;
		
		//Pop values from stack1 into tempStack
		//If we find "(" then set parenthesisFound to true and stops the loops
		//This way, it returns a true value
		while (!stack1.isEmpty()) {
			if (stack1.peek().equals("(")) {
				parenthesisFound = true;
				break;
			}
			tempStack.push(stack1.pop());
		}
		
		//Send all values from tempStack back into stack
		while (!tempStack.isEmpty()) {
			stack1.push(tempStack.pop());
		}
		
		return parenthesisFound;
	}
	
	//This method helps to find if there is only one value left in the stack
	private static boolean checkIfLastValue(Stack stack) {
		//Get a popped value
		String value = stack.pop();
		boolean lastValue = false;
		
		//If the stack is empty with a popped value, then set lastValue to true
		if (stack.isEmpty()) {
			lastValue = true;
		}
		
		//Push the value back into the stack
		stack.push(value);
		
		return lastValue;
	}
}
