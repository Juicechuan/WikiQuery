import java.util.LinkedList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public class ExpParser {

	public String parse(String exp) {
		LinkedList<String> outputQueue = new LinkedList<String>();
		Stack<String> opStack = new Stack<String>();

		Pattern p = Pattern
				.compile("\\(|\\)|[^\\(\\)(OR)(AND)(NOT)\\s]+|((OR)|(AND)|(NOT))");
		Matcher m = p.matcher(exp);
		while (m.find()) {
			if (m.group().matches("\\(")) {
				opStack.push(m.group());
			} else if (m.group().matches("[^\\(\\)(OR)(AND)(NOT)\\s]+")) {
				outputQueue.add(m.group());
			} else if (m.group().matches("((OR)|(AND)|(NOT))")) {
				opStack.push(m.group());
			} else if (m.group().matches("\\)")) {
				while ((!opStack.isEmpty()) && (!opStack.peek().matches("\\("))) {
					outputQueue.add(opStack.pop());
				}
				if (!opStack.isEmpty()) {
					opStack.pop();
					if (!opStack.isEmpty()
							&& opStack.peek().matches("((OR)|(AND)|(NOT))")) {
						outputQueue.add(opStack.pop());
					}
				} else {
					System.err.println("Invalid boolean expression format! "
							+ exp);
				}
			}

			// System.out.println(m.group());
		}
		while ((!opStack.isEmpty())) {
			outputQueue.add(opStack.pop());
		}

		return StringUtils.join(outputQueue, " ");

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String exp = "(madding OR crowd) AND (ignoble OR strife) AND (killed OR slain)";
		String exp1 = "coke AND diet";
		ExpParser e = new ExpParser();
		String result = e.parse(exp);
		String result1 = e.parse(exp1);
		System.out.println(result1);
	}
}
