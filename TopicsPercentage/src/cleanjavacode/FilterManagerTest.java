package cleanjavacode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Simone Scalabrino.
 */
class FilterManagerTest {

    @org.junit.jupiter.api.Test
    void applyNonWordFilter() {
        String javaFileContent = new StringBuilder()
                .append("int a = 0;\n")
                .append("int b = 2;\n")
                .append("int a23 = 3;")
                .append("public static int test(List<c> b) {\n")
                .append("}\n")
                .append("String javaFileContent = new StringBuilder().toString();")
                .toString();

        String expected = "int a int b int a23 public static int test List c b String javaFileContent new StringBuilder toString";
        String result = FilterManager.applyNonWordFilter(javaFileContent);
        assertEquals(expected, result);
    }
}