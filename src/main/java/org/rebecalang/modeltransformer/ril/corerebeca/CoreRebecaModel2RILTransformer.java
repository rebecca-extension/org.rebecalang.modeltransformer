package org.rebecalang.modeltransformer.ril.corerebeca;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.AbstractRILModelTransformer;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.Rebeca2RILStatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMethodInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.BlockStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.BreakStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.ConditionalStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.ContinueStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.FieldDeclarationTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.ForStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.ReturnStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.SwitchStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.WhileStatementTranslator;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("CORE_REBECA")
public class CoreRebecaModel2RILTransformer extends AbstractRILModelTransformer  {

	@Autowired
	public CoreRebecaModel2RILTransformer(
			Rebeca2RILStatementTranslatorContainer statementTranslatorContainer,
			Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(statementTranslatorContainer, expressionTranslatorContainer);
	}
	
	public void initializeTranslators() {

		statementTranslatorContainer.registerTranslator(BlockStatement.class,
				appContext.getBean(BlockStatementTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));
		statementTranslatorContainer.registerTranslator(ReturnStatement.class,
				appContext.getBean(ReturnStatementTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));
		statementTranslatorContainer.registerTranslator(FieldDeclaration.class,
				appContext.getBean(FieldDeclarationTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));
		statementTranslatorContainer.registerTranslator(WhileStatement.class,
				appContext.getBean(WhileStatementTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));
		statementTranslatorContainer.registerTranslator(SwitchStatement.class,
				appContext.getBean(SwitchStatementTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));
		statementTranslatorContainer.registerTranslator(ForStatement.class,
				appContext.getBean(ForStatementTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));
		statementTranslatorContainer.registerTranslator(BreakStatement.class,
				appContext.getBean(BreakStatementTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));
		statementTranslatorContainer.registerTranslator(ContinueStatement.class,
				appContext.getBean(ContinueStatementTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));
		statementTranslatorContainer.registerTranslator(ConditionalStatement.class,
				appContext.getBean(ConditionalStatementTranslator.class, 
						statementTranslatorContainer,
						expressionTranslatorContainer));

		expressionTranslatorContainer.registerTranslator(BinaryExpression.class,
				appContext.getBean(BinaryExpressionTranslator.class, expressionTranslatorContainer));
		expressionTranslatorContainer.registerTranslator(InstanceofExpression.class,
				appContext.getBean(InstanceofExpressionTranslator.class, expressionTranslatorContainer));
		expressionTranslatorContainer.registerTranslator(Literal.class, 
				appContext.getBean(LiteralStatementTranslator.class, expressionTranslatorContainer));
		expressionTranslatorContainer.registerTranslator(DotPrimary.class,
				appContext.getBean(DotPrimaryExpressionTranslator.class, expressionTranslatorContainer));
		expressionTranslatorContainer.registerTranslator(UnaryExpression.class,
				appContext.getBean(UnaryExpressionTranslator.class, expressionTranslatorContainer));
		expressionTranslatorContainer.registerTranslator(PlusSubExpression.class,
				appContext.getBean(PlusSubExpressionTranslator.class, expressionTranslatorContainer));
		expressionTranslatorContainer.registerTranslator(NonDetExpression.class,
				appContext.getBean(NonDetExpressionTranslator.class, expressionTranslatorContainer));
		expressionTranslatorContainer.registerTranslator(TermPrimary.class,
				(TermPrimaryExpressionTranslator)appContext.getBean("CORE_REBECA_TERM_PRIMARY", 
						expressionTranslatorContainer));
		
	}

	@Override
	public RILModel transformModel(
			Pair<RebecaModel, SymbolTable> model, 
			Set<CompilerExtension> extension, 
			CoreVersion coreVersion) {

		RILModel transformedRILModel = new RILModel();

		RebecaModel rebecaModel = model.getFirst();
		
		statementTranslatorContainer.setSymbolTable(model.getSecond());
		expressionTranslatorContainer.setSymbolTable(model.getSecond());

		List<ReactiveClassDeclaration> reactiveClassDeclarations = rebecaModel.getRebecaCode().getReactiveClassDeclaration();
		for (ReactiveClassDeclaration rcd : reactiveClassDeclarations) {
			for(MsgsrvDeclaration msgsrv : rcd.getMsgsrvs()) {
				ArrayList<InstructionBean> instructions = new ArrayList<InstructionBean>();
				String computedMethodName = RILUtilities.computeMethodName(rcd, msgsrv);
				transformedRILModel.addMethod(computedMethodName, instructions);
				statementTranslatorContainer.prepare(rcd, computedMethodName);
				statementTranslatorContainer.translate(msgsrv.getBlock(), instructions);
				instructions.add(new EndMsgSrvInstructionBean());
			}
			for(ConstructorDeclaration constructorDeclaration : rcd.getConstructors()) {
				ArrayList<InstructionBean> instructions = new ArrayList<InstructionBean>();
				String computedMethodName = RILUtilities.computeMethodName(rcd, constructorDeclaration);
				transformedRILModel.addMethod(computedMethodName, instructions);
				statementTranslatorContainer.prepare(rcd, computedMethodName);
				statementTranslatorContainer.translate(constructorDeclaration.getBlock(), instructions);
				instructions.add(new EndMsgSrvInstructionBean());
			}
			for(MethodDeclaration methodDeclaration : rcd.getSynchMethods()) {
				ArrayList<InstructionBean> instructions = new ArrayList<InstructionBean>();
				String computedMethodName = RILUtilities.computeMethodName(rcd, methodDeclaration);
				transformedRILModel.addMethod(computedMethodName, instructions);
				statementTranslatorContainer.prepare(rcd, computedMethodName);
				statementTranslatorContainer.translate(methodDeclaration.getBlock(), instructions);
				instructions.add(new EndMethodInstructionBean());
			}
			
		}
		return transformedRILModel;
	}

}
