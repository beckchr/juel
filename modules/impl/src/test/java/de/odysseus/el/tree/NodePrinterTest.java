package de.odysseus.el.tree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import de.odysseus.el.TestCase;
import de.odysseus.el.tree.impl.Builder;
import de.odysseus.el.tree.impl.Builder.Feature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NodePrinterTest extends TestCase {

	@Test
	public void testDump() throws IOException {
		Tree tree = new Builder(Feature.METHOD_INVOCATIONS).build("${foo.bar[baz] + foobar}");
		StringWriter writer = new StringWriter();
		NodePrinter.dump(new PrintWriter(writer), tree.getRoot());
		String[] expected = {
				"+- ${...}",
				"   |",
				"   +- '+'",
				"      |",
				"      +- [...]",
				"      |  |",
				"      |  +- . bar",
				"      |  |  |",
				"      |  |  +- foo",
				"      |  |",
				"      |  +- baz",
				"      |",
				"      +- foobar",
				null
		};
		BufferedReader reader = new BufferedReader(new StringReader(writer.toString()));
		for (String line : expected) {
			assertEquals(line, reader.readLine());
		}
	}

}
