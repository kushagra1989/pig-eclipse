package org.apache.pig.contrib.eclipse.editors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

public class PigScriptScanner extends RuleBasedScanner {

	private static Set<String> KEYWORDS;
	private static Set<String> BUILTIN_FUN;
	private static Set<String> DATA_TYPES;


	static {
		KEYWORDS = readFromResources("keywords.txt");
		BUILTIN_FUN = readFromResources("builtin_fun.txt");
		DATA_TYPES = readFromResources("data_types.txt");
	}

	private static Set<String> readFromResources(String path) {
		Set<String> result= new HashSet<String>();
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(FileLocator.openStream(Platform.getBundle("org.apache.pig.contrib.eclipse"), new Path("data/" + path ), false)));

			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() != 0) {
					result.add(line);
				}
			}
			
		} catch (IOException e) {
			System.err.println("Failed to read resources: " + path);
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					System.err.println("Unable to close data files");
					e.printStackTrace();
				}
			}
		}

		return result;
	}
	
	public PigScriptScanner(PigColorProvider colorProvider) {
		
		
		IToken keywordToken = new Token(new TextAttribute(colorProvider
				.getColor(PigColorProvider.KEYWORD), null, SWT.BOLD));
		IToken commentToken = new Token(new TextAttribute(colorProvider
				.getColor(PigColorProvider.COMMENT)));
		IToken builtinFunToken = new Token(new TextAttribute(colorProvider
				.getColor(PigColorProvider.BUILTIN_FUN)));
		IToken stringToken = new Token(new TextAttribute(colorProvider
				.getColor(PigColorProvider.RAWSTRING)));
		IToken defaultToken = new Token(new TextAttribute(colorProvider
				.getColor(PigColorProvider.DEFAULT)));
		IToken dataTypeToken = new Token(new TextAttribute(colorProvider
				.getColor(PigColorProvider.DATA_TYPE)));

		List<IRule> rules = new ArrayList<IRule>();

		rules.add(new EndOfLineRule("--", commentToken)); //$NON-NLS-1$

		rules.add(new SingleLineRule("'", "'", stringToken, '\\')); //$NON-NLS-2$ //$NON-NLS-1$

		rules.add(new MultiLineRule("/*", "*/", commentToken)); //$NON-NLS-2$ //$NON-NLS-1$
		
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new PigWhiteSpaceDetector()));

		// Add word rule for keywords, built in functions, and data types
		WordRule wordRule = new WordRule(new PigWordDetector(), defaultToken, true);

		for (String keyword : KEYWORDS)
			wordRule.addWord(keyword, keywordToken);

		for (String func : BUILTIN_FUN)
			wordRule.addWord(func, builtinFunToken);

		for (String datatype : DATA_TYPES)
			wordRule.addWord(datatype, dataTypeToken);

		rules.add(wordRule);
		
		setRules(rules.toArray(new IRule[rules.size()]));
	}
}