import java.util.LinkedList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpParser {

	public String parse(String exp) {
		LinkedList<String> outputQueue = new LinkedList<String>();
		Stack<String> opStack = new Stack<String>();

		Pattern p = Pattern
				.compile("\\(|\\)|[^\\(\\)(OR)(AND)(NOT)\\s]+|((OR)|(AND)|(NOT))");
		Matcher m = p.matcher(exp);
		while (m.find()) {
			if (m.group().matches("\\(")){
				opStack.push(m.group());
			}
			else if (m.group().matches("[^\\(\\)(OR)(AND)(NOT)\\s]+)")){
				outputQueue.add(m.group());
			}
			else if (m.group().matches("((OR)|(AND)|(NOT))")){
				opStack.push(m.group());
			}
			else if (m.group().matches("\\)")){
				while (!opStack.peek().matches("\\(")){
					outputQueue.add(opStack.pop());
				}
			}
					
			System.out.println(m.group());
		}
		return null;

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String exp = "(madding OR crowd) AND (ignoble OR strife) AND (killed OR slain)";
		ExpParser e = new ExpParser();
		String result = e.parse(exp);
	}
}
