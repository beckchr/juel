package de.odysseus.el.tree.impl.ast;

import de.odysseus.el.ObjectValueExpression;
import de.odysseus.el.TestCase;
import de.odysseus.el.TreeValueExpression;
import de.odysseus.el.misc.TypeConverter;
import de.odysseus.el.tree.TreeStore;
import de.odysseus.el.util.SimpleContext;
import de.odysseus.el.util.SimpleResolver;

import javax.el.MethodExpression;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by alexbrob on 2016-03-14.
 */
public class AstDateTest extends TestCase {

    SimpleContext context;

    @Override
    protected void setUp() throws Exception {
        context = new SimpleContext(new SimpleResolver());

        TypeConverter converter = TypeConverter.DEFAULT;

        context.setVariable("var_date_1", new TreeValueExpression(new TreeStore(BUILDER, null), null, context.getVariableMapper(), converter, "", Date.class));
        context.setVariable("var_string_1", new TreeValueExpression(new TreeStore(BUILDER, null), null, context.getVariableMapper(), converter, "", String.class));
    }

    public void testIsLiteralTextFalseForDate() throws Exception {
        assertFalse(context.getVariableMapper().resolveVariable("var_date_1").isLiteralText());
    }

    public void testIsLiteralTextTrueForString() throws Exception {
        assertTrue(context.getVariableMapper().resolveVariable("var_string_1").isLiteralText());
    }

}
