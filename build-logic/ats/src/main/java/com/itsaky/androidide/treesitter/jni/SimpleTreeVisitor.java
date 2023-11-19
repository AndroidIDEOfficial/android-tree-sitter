/*
 *  This file is part of android-tree-sitter.
 *
 *  android-tree-sitter library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  android-tree-sitter library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with android-tree-sitter.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.itsaky.androidide.treesitter.jni;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BindingPatternTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DefaultCaseLabelTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExportsTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.GuardedPatternTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.IntersectionTypeTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.ModuleTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.OpensTree;
import com.sun.source.tree.PackageTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedPatternTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ProvidesTree;
import com.sun.source.tree.RequiresTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchExpressionTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.tree.UsesTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import com.sun.source.tree.YieldTree;
import java.util.Iterator;

/**
 * @author Akash Yadav
 */
public class SimpleTreeVisitor<R, P> implements TreeVisitor<R, P> {
  protected final R DEFAULT_VALUE;

  protected SimpleTreeVisitor() {
    this.DEFAULT_VALUE = null;
  }

  protected SimpleTreeVisitor(R defaultValue) {
    this.DEFAULT_VALUE = defaultValue;
  }

  protected R defaultAction(Tree node, P p) {
    return this.DEFAULT_VALUE;
  }

  public final R visit(Tree node, P p) {
    return node == null ? null : node.accept(this, p);
  }

  public final R visit(Iterable<? extends Tree> nodes, P p) {
    R r = null;
    Tree node;
    if (nodes != null) {
      for(Iterator var4 = nodes.iterator(); var4.hasNext(); r = this.visit(node, p)) {
        node = (Tree)var4.next();
      }
    }

    return r;
  }

  public R visitCompilationUnit(CompilationUnitTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitPackage(PackageTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitImport(ImportTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitClass(ClassTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitMethod(MethodTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitVariable(VariableTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitEmptyStatement(EmptyStatementTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitBlock(BlockTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitDoWhileLoop(DoWhileLoopTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitWhileLoop(WhileLoopTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitForLoop(ForLoopTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitEnhancedForLoop(EnhancedForLoopTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitLabeledStatement(LabeledStatementTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitSwitch(SwitchTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitSwitchExpression(SwitchExpressionTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitCase(CaseTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitSynchronized(SynchronizedTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitTry(TryTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitCatch(CatchTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitConditionalExpression(ConditionalExpressionTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitIf(IfTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitExpressionStatement(ExpressionStatementTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitBreak(BreakTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitContinue(ContinueTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitReturn(ReturnTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitThrow(ThrowTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitAssert(AssertTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitMethodInvocation(MethodInvocationTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitNewClass(NewClassTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitNewArray(NewArrayTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitLambdaExpression(LambdaExpressionTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitParenthesized(ParenthesizedTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitAssignment(AssignmentTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitCompoundAssignment(CompoundAssignmentTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitUnary(UnaryTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitBinary(BinaryTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitTypeCast(TypeCastTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitInstanceOf(InstanceOfTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitBindingPattern(BindingPatternTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitDefaultCaseLabel(DefaultCaseLabelTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitArrayAccess(ArrayAccessTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitMemberSelect(MemberSelectTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitParenthesizedPattern(ParenthesizedPatternTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitGuardedPattern(GuardedPatternTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitMemberReference(MemberReferenceTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitIdentifier(IdentifierTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitLiteral(LiteralTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitPrimitiveType(PrimitiveTypeTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitArrayType(ArrayTypeTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitParameterizedType(ParameterizedTypeTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitUnionType(UnionTypeTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitIntersectionType(IntersectionTypeTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitTypeParameter(TypeParameterTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitWildcard(WildcardTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitModifiers(ModifiersTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitAnnotation(AnnotationTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitAnnotatedType(AnnotatedTypeTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitModule(ModuleTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitExports(ExportsTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitOpens(OpensTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitProvides(ProvidesTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitRequires(RequiresTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitUses(UsesTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitErroneous(ErroneousTree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitOther(Tree node, P p) {
    return this.defaultAction(node, p);
  }

  public R visitYield(YieldTree node, P p) {
    return this.defaultAction(node, p);
  }
}
