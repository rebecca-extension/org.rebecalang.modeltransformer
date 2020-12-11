package org.rebecalang.modeltransformer;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;

public abstract class AbstractExpressionTransformer {
	
	protected final static String NEW_LINE = "\r\n";
	protected final static String TAB = "\t";

	protected Set<CompilerFeature> cFeatures; 
	protected ExceptionContainer container;
	
	public AbstractExpressionTransformer(Set<CompilerFeature> cFeatures, 
			ExceptionContainer container) {
		this.cFeatures = cFeatures;
		this.container = container;
	}
	public abstract String translate(Expression expression,
			ExceptionContainer container);

	public abstract void initialize();
}
