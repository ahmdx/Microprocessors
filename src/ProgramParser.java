import java.util.Arrays;
import java.util.regex.*;

public class ProgramParser {
	private static String jump = "(JMP)\\s+(R[0-7]),\\s+(([0-9]|[1-5][0-9]|6[0-3])|(\\-[0-9]|\\-[1-5][0-9]|\\-6[0-4]))(?:\\s*\\#.*)?";
	private static String load = "(LW)\\s+(R[1-7]),\\s+(R[0-7]),\\s+(([0-9]|[1-5][0-9]|6[0-3])|(\\-[0-9]|\\-[1-5][0-9]|\\-6[0-4]))(?:\\s*\\#.*)?";
	private static String store = "(SW)\\s+(R[0-7]),\\s+(R[0-7]),\\s+(([0-9]|[1-5][0-9]|6[0-3])|(\\-[0-9]|\\-[1-5][0-9]|\\-6[0-4]))(?:\\s*\\#.*)?";
	private static String branch = "(BEQ)\\s+(R[0-7]),\\s+(R[0-7]),\\s+(([0-9]|[1-5][0-9]|6[0-3])|(\\-[0-9]|\\-[1-5][0-9]|\\-6[0-4]))(?:\\s*\\#.*)?";
	private static String call = "(JALR)\\s+(R[1-7]),\\s+(R[0-7])(?:\\s*\\#.*)?";
	private static String returnCall = "(RET)\\s+(R[0-7])(?:\\s*\\#.*)?";
	private static String arithmetic = "(ADD|SUB|NAND|MUL)\\s+(R[1-7]),\\s+(R[0-7]),\\s+(R[0-7])(?:\\s*\\#.*)?";
	private static String immediate = "(ADDI)\\s+(R[1-7]),\\s+(R[0-7]),\\s+(([0-9]|[1-5][0-9]|6[0-3])|(\\-[0-9]|\\-[1-5][0-9]|\\-6[0-4]))(?:\\s*\\#.*)?";

	public static void main(String[] args) {
		System.out.println(Arrays.toString(match("JMP R4, 10")));
		System.out.println(Arrays.toString(match("LW R1, R5, -11")));
		System.out.println(Arrays.toString(match("SW R0, R5, -10")));
		System.out.println(Arrays.toString(match("BEQ R0, R0, -40")));
		System.out.println(Arrays.toString(match("JALR R1, R5")));
		System.out.println(Arrays.toString(match("RET R0")));
		System.out.println(Arrays.toString(match("ADD R1, R6, R0")));
		System.out.println(Arrays.toString(match("ADDI R3, R5, -40# this is a comment")));
		System.out.println(Arrays.toString(match("ADDI R3, R5, -40; this is not a comment"))); // returns null
		System.out.println(Arrays.toString(match("ADDI R3, R5, -65"))); // returns null: Immediate value beyond range [-64, 63]
		System.out.println(Arrays.toString(match("ADDI R3, R5, 64"))); // returns null: Immediate value beyond range [-64, 63]
	}

	public static String[] match(String string) {
		Pattern pattern = Pattern
				.compile("(JMP|LW|SW|BEQ|JALR|RET|ADDI|ADD|SUB|NAND|MUL).*");
		Matcher matcher = pattern.matcher(string);
		Matcher m;
		if (matcher.matches()) {
			if (matcher.group(1).equals("JMP")) {
				m = matcher(string, jump);
				if (m.matches()) {
					return new String[] {m.group(1), m.group(2), m.group(3)};
				}
			} else if (matcher.group(1).equals("LW")) {
				m = matcher(string, load);
				if (m.matches()) {
					return new String[] {m.group(1), m.group(2), m.group(3), m.group(4)};
				}
			} else if (matcher.group(1).equals("SW")) {
				m = matcher(string, store);
				if (m.matches()) {
					return new String[] {m.group(1), m.group(2), m.group(3), m.group(4)};
				}
			} else if (matcher.group(1).equals("BEQ")) {
				m = matcher(string, branch);
				if (m.matches()) {
					return new String[] {m.group(1), m.group(2), m.group(3), m.group(4)};
				}
			} else if (matcher.group(1).equals("JALR")) {
				m = matcher(string, call);
				if (m.matches()) {
					return new String[] {m.group(1), m.group(2), m.group(3)};
				}
			} else if (matcher.group(1).equals("RET")) {
				m = matcher(string, returnCall);
				if (m.matches()) {
					return new String[] {m.group(1), m.group(2)};
				}
			} else if (matcher.group(1).equals("ADDI")) {
				m = matcher(string, immediate);
				if (m.matches()) {
					return new String[] {m.group(1), m.group(2), m.group(3), m.group(4)};
				}
			} else if (matcher.group(1).equals("ADD")
					|| matcher.group(1).equals("SUB")
					|| matcher.group(1).equals("NAND")
					|| matcher.group(1).equals("MUL")) {
				m = matcher(string, arithmetic);
				if (m.matches()) {
					return new String[] {m.group(1), m.group(2), m.group(3), m.group(4)};
				}
			}
		}
		return null;
	}

	public static Matcher matcher(String string, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(string);
		return matcher;
	}
}
