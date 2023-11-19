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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;

/**
 * @author Akash Yadav
 */
public class JNIWriter {

  private final StringWriter _headerWriter = new StringWriter();
  private final PrintWriter headerWriter = new PrintWriter(_headerWriter);

  private final StringWriter _sigWriter = new StringWriter();
  private final PrintWriter sigWriter = new PrintWriter(_sigWriter);

  private static final boolean isWindows = System.getProperty("os.name").startsWith("Windows");

  private final Types types;
  private final TypeMirror stringType;
  private final TypeMirror throwableType;
  private final TypeMirror classType;

  public JNIWriter(Types types, Elements elements) {
    this.types = types;

    this.stringType = elements.getTypeElement("java.lang.String").asType();
    this.throwableType = elements.getTypeElement("java.lang.Throwable").asType();
    this.classType = elements.getTypeElement("java.lang.Class").asType();
  }

  static boolean isStatic(Element s) {
    return hasFlag(s, Modifier.STATIC);
  }

  static boolean isFinal(Element s) {
    return hasFlag(s, Modifier.FINAL);
  }

  static boolean isNative(Element s) {
    return hasFlag(s, Modifier.NATIVE);
  }

  static boolean hasFlag(Element s, Modifier m) {
    return s.getModifiers().contains(m);
  }

  static boolean isLocalType(TypeElement typeElement) {
    Element enclosingElement = typeElement.getEnclosingElement();

    while (enclosingElement != null) {
      if (enclosingElement instanceof TypeElement) {
        // Continue checking up the hierarchy
        enclosingElement = ((TypeElement) enclosingElement).getEnclosingElement();
      } else if (enclosingElement instanceof javax.lang.model.element.VariableElement ||
        enclosingElement instanceof javax.lang.model.element.ExecutableElement) {
        // Found a method or variable initializer, so the type is local
        return true;
      } else {
        // Reached a top-level element (class, package, etc.)
        break;
      }
    }

    // If we reach here, the type is not local
    return false;
  }

  static TypeElement outermostClass(TypeElement c) {
    Element sym = c;
    Element prev = null;
    while (sym.getKind() != ElementKind.PACKAGE) {
      prev = sym;
      sym = sym.getEnclosingElement();
    }
    return (TypeElement) prev;
  }

  public boolean needsHeader(TypeElement c) {
    if (isLocalType(c)) {
      return false;
    }

    return needsHeader(outermostClass(c), true);
  }

  private boolean needsHeader(TypeElement c, boolean checkNestedClasses) {
    if (isLocalType(c)) {
      return false;
    }

    for (Element sym : c.getEnclosedElements()) {
      if (sym.getKind() == ElementKind.METHOD && isNative(sym)) {
        return true;
      }
    }

    if (checkNestedClasses) {
      for (Element sym : c.getEnclosedElements()) {
        if ((sym.getKind().isClass()) && needsHeader(((TypeElement) sym), true)) {
          return true;
        }
      }
    }
    return false;
  }

  public Pair<String, String> generate(TypeElement type) {
    final var cname = encode(type.getQualifiedName(), EncoderType.CLASS);

    final var sigs = new HashMap<String, String>();

    // Write native headers file
    fileTop(headerWriter);
    includes(headerWriter);
    guardBegin(headerWriter, cname, "METHODS");
    cppGuardBegin(headerWriter);

    writeStatics(headerWriter, type);
    writeMethods(headerWriter, type, cname, sigs);

    cppGuardEnd(headerWriter);
    guardEnd(headerWriter);

    // Write method signatures header
    fileTop(sigWriter);
    guardBegin(sigWriter, cname, "METHOD_SIGNATURES");
    writeMethodDefs(sigWriter, cname, type, sigs);
    guardEnd(sigWriter);

    return new Pair<>(_headerWriter.toString(), _sigWriter.toString());
  }

  private void writeMethodDefs(PrintWriter out, String cname, TypeElement type, HashMap<String, String> sigs) {
    final var typeName = new StringBuilder();
    Element curr = type;
    while (curr != null && curr.getKind().isClass()) {
      typeName.insert(0, "_").insert(0, curr.getSimpleName());
      curr = curr.getEnclosingElement();
    }

    final var nameAndSigList = new ArrayList<Pair<String, String>>();

    for (final var entry : sigs.entrySet()) {
      final var methodName = entry.getKey();
      final var methodSig = entry.getValue();
      final var namePrefix = "TS_" + typeName.toString().toUpperCase(Locale.ROOT) + methodName.toUpperCase(Locale.ROOT);
      final var defName = namePrefix + "_NAME";
      final var defSig = namePrefix + "_SIG";

      out.println();
      methodDoc(out, cname, methodName, methodSig);

      out.print("#define ");
      out.print(defName);
      out.print(" \"");
      out.print(methodName);
      out.println("\"");

      out.print("#define ");
      out.print(defSig);
      out.print(" \"");
      out.print(methodSig);
      out.println("\"");

      if (!"registerNatives".equals(methodName)) {
        nameAndSigList.add(Pair.of(defName, defSig));
      }
    }
    out.println();

    out.print("#define TS_");
    out.print(typeName.toString().toUpperCase(Locale.ROOT));
    out.print("_METHOD_COUNT ");
    out.println(nameAndSigList.size());

    out.println();
  }

  protected void writeStatics(PrintWriter out, TypeElement sym) {
    List<TypeElement> clist = new ArrayList<>();
    for (TypeElement cd = sym; cd != null; cd = (TypeElement) types.asElement(cd.getSuperclass())) {
      clist.add(cd);
    }
    /*
     * list needs to be super-class, base-class1, base-class2 and so on,
     * so we reverse class hierarchy
     */
    Collections.reverse(clist);
    for (TypeElement cd : clist) {
      for (Element i : cd.getEnclosedElements()) {
        // consider only final, static and fields with ConstantExpressions
        if (isFinal(i) && isStatic(i) && i.getKind().isField()) {
          VariableElement v = (VariableElement) i;
          if (v.getConstantValue() != null) {
            Pair<TypeElement, VariableElement> p = new Pair<>(sym, v);
            printStaticDefines(out, p);
          }
        }
      }
    }
  }

  static void printStaticDefines(PrintWriter out, Pair<TypeElement, VariableElement> p) {
    TypeElement cls = p.first;
    VariableElement f = p.second;
    Object value = f.getConstantValue();
    String valueStr = null;
    switch (f.asType().getKind()) {
      case BOOLEAN:
        valueStr = (((Boolean) value) ? "1L" : "0L");
        break;
      case BYTE:
      case SHORT:
      case INT:
        valueStr = value.toString() + "L";
        break;
      case LONG:
        // Visual C++ supports the i64 suffix, not LL.
        valueStr = value.toString() + ((isWindows) ? "i64" : "LL");
        break;
      case CHAR:
        Character ch = (Character) value;
        valueStr = String.valueOf(((int) ch) & 0xffff) + "L";
        break;
      case FLOAT:
        // bug compatible
        float fv = ((Float) value).floatValue();
        valueStr = (Float.isInfinite(fv)) ? ((fv < 0) ? "-" : "") + "Inff" : value.toString() + "f";
        break;
      case DOUBLE:
        // bug compatible
        double d = ((Double) value).doubleValue();
        valueStr = (Double.isInfinite(d)) ? ((d < 0) ? "-" : "") + "InfD" : value.toString();
        break;
      default:
        valueStr = null;
    }
    if (valueStr != null) {
      out.print("#undef ");
      String cname = encode(cls.getQualifiedName(), EncoderType.CLASS);
      String fname = encode(f.getSimpleName(), EncoderType.FIELDSTUB);
      out.println(cname + "_" + fname);
      out.print("#define " + cname + "_");
      out.println(fname + " " + valueStr);
    }
  }

  void writeMethods(PrintWriter out, TypeElement sym, String cname, Map<String, String> methodSigs) {
    List<? extends Element> classmethods = sym.getEnclosedElements();

    for (Element e : classmethods) {
      if (e.getKind() != ElementKind.METHOD) {
        continue;
      }

      ExecutableElement md = ((ExecutableElement) e);

      if (!isNative(md)) {
        continue;
      }

      // validate the method

      TypeSignature newtypesig = new TypeSignature(types);
      CharSequence methodName = md.getSimpleName();
      boolean isOverloaded = false;
      for (Element md2 : classmethods) {
        if ((md2 != md) && (methodName.equals(md2.getSimpleName())) && isNative(md2)) {
          isOverloaded = true;
        }
      }

      final var methodSig = newtypesig.getSignature(md);
      methodSigs.put(methodName.toString(), methodSig.toString());

      methodDoc(out, cname, methodName, methodSig);
      out.println("JNIEXPORT " + jniType(types.erasure(md.getReturnType())) + " JNICALL " +
        encodeMethod(md, sym, isOverloaded));

      final var criticalNative = md.getAnnotationMirrors().stream().filter(am -> ((TypeElement) am.getAnnotationType()
        .asElement()).getQualifiedName().contentEquals("dalvik.annotation.optimization.CriticalNative")).findFirst();

      if (criticalNative.isPresent()) {
        // omit JNIEnv and jclass from @CriticalNative methods
        out.print("  (");
      } else {
        out.print("  (JNIEnv *env, ");
        out.print((isStatic(md)) ? "jclass clazz" : "jobject self");
      }

      for (VariableElement v : md.getParameters()) {
        var arg = types.erasure(v.asType());
        out.print(", ");
        out.print(jniType(arg));
        out.print(" ");
        out.print(v.getSimpleName());
      }
      out.println(");");
      out.println();
    }
  }

  private static void methodDoc(
    PrintWriter out,
    String cname,
    CharSequence methodName,
    CharSequence methodSig
  ) {
    out.println("/*");
    out.println(" * Class:     " + cname);
    out.println(" * Method:    " + encode(methodName, EncoderType.FIELDSTUB));
    out.println(" * Signature: " + methodSig);
    out.println(" */");
  }

  String encodeMethod(ExecutableElement msym, TypeElement clazz, boolean isOverloaded) {
    StringBuilder result = new StringBuilder(100);
    result.append("Java_");

    /* JNI */
    result.append(encode(clazz.getQualifiedName(), EncoderType.JNI));
    result.append('_');
    result.append(encode(msym.getSimpleName(), EncoderType.JNI));

    if (isOverloaded) {
      TypeSignature typeSig = new TypeSignature(types);
      StringBuilder sig = typeSig.getParameterSignature(msym, true);
      result.append("__").append(encode(sig, EncoderType.JNI));
    }
    return result.toString();
  }

  @SuppressWarnings("fallthrough")
  protected final String jniType(TypeMirror t) {
    switch (t.getKind()) {
      case ARRAY: {
        TypeMirror ct = ((ArrayType) t).getComponentType();
        switch (ct.getKind()) {
          case BOOLEAN:
            return "jbooleanArray";
          case BYTE:
            return "jbyteArray";
          case CHAR:
            return "jcharArray";
          case SHORT:
            return "jshortArray";
          case INT:
            return "jintArray";
          case LONG:
            return "jlongArray";
          case FLOAT:
            return "jfloatArray";
          case DOUBLE:
            return "jdoubleArray";
          case ARRAY:
          case DECLARED:
            return "jobjectArray";
          default:
            throw new Error(ct.toString());
        }
      }

      case VOID:
        return "void";
      case BOOLEAN:
        return "jboolean";
      case BYTE:
        return "jbyte";
      case CHAR:
        return "jchar";
      case SHORT:
        return "jshort";
      case INT:
        return "jint";
      case LONG:
        return "jlong";
      case FLOAT:
        return "jfloat";
      case DOUBLE:
        return "jdouble";
      case DECLARED: {
        if (types.isAssignable(t, stringType)) {
          return "jstring";
        } else if (types.isAssignable(t, throwableType)) {
          return "jthrowable";
        } else if (types.isAssignable(t, classType)) {
          return "jclass";
        } else {
          return "jobject";
        }
      }
    }

    throw new IllegalArgumentException("jni unknown type");
  }

  protected void fileTop(PrintWriter out) {
    out.println("/* DO NOT EDIT THIS FILE - it is machine generated */");
  }

  protected void includes(PrintWriter out) {
    out.println("#include <jni.h>");
  }

  /*
   * Deal with the C pre-processor.
   */
  protected void cppGuardBegin(PrintWriter out) {
    out.println("#ifdef __cplusplus");
    out.println("extern \"C\" {");
    out.println("#endif");
  }

  protected void cppGuardEnd(PrintWriter out) {
    out.println("#ifdef __cplusplus");
    out.println("}");
    out.println("#endif");
  }

  protected void guardBegin(PrintWriter out, String cname, String type) {
    out.println();
    out.println("#ifndef " + ("_Included_" + cname + "_" + type).toUpperCase(Locale.ROOT));
    out.println("#define " + ("_Included_" + cname + "_" + type).toUpperCase(Locale.ROOT));
  }

  protected void guardEnd(PrintWriter out) {
    out.println("#endif");
  }

  enum EncoderType {
    CLASS, FIELDSTUB, FIELD, JNI, SIGNATURE
  }

  @SuppressWarnings("fallthrough")
  static String encode(CharSequence name, EncoderType mtype) {
    StringBuilder result = new StringBuilder(100);
    int length = name.length();

    for (int i = 0; i < length; i++) {
      char ch = name.charAt(i);
      if (isalnum(ch)) {
        result.append(ch);
        continue;
      }
      switch (mtype) {
        case CLASS:
          switch (ch) {
            case '.':
            case '_':
              result.append("_");
              break;
            case '$':
              result.append("__");
              break;
            default:
              result.append(encodeChar(ch));
          }
          break;
        case JNI:
          switch (ch) {
            case '/':
            case '.':
              result.append("_");
              break;
            case '_':
              result.append("_1");
              break;
            case ';':
              result.append("_2");
              break;
            case '[':
              result.append("_3");
              break;
            default:
              result.append(encodeChar(ch));
          }
          break;
        case SIGNATURE:
          result.append(isprint(ch) ? ch : encodeChar(ch));
          break;
        case FIELDSTUB:
          result.append(ch == '_' ? ch : encodeChar(ch));
          break;
        default:
          result.append(encodeChar(ch));
      }
    }
    return result.toString();
  }

  static String encodeChar(char ch) {
    String s = Integer.toHexString(ch);
    int nzeros = 5 - s.length();
    char[] result = new char[6];
    result[0] = '_';
    for (int i = 1; i <= nzeros; i++) {
      result[i] = '0';
    }
    for (int i = nzeros + 1, j = 0; i < 6; i++, j++) {
      result[i] = s.charAt(j);
    }
    return new String(result);
  }

  /* Warning: Intentional ASCII operation. */
  private static boolean isalnum(char ch) {
    return ch <= 0x7f && /* quick test */
      ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9'));
  }

  /* Warning: Intentional ASCII operation. */
  private static boolean isprint(char ch) {
    return ch >= 32 && ch <= 126;
  }

  private static class TypeSignature {
    static class SignatureException extends Exception {
      private static final long serialVersionUID = 1L;
      SignatureException(String reason) {
        super(reason);
      }
    }

    Elements elems;
    Types    types;

    /* Signature Characters */
    private static final String SIG_VOID                   = "V";
    private static final String SIG_BOOLEAN                = "Z";
    private static final String SIG_BYTE                   = "B";
    private static final String SIG_CHAR                   = "C";
    private static final String SIG_SHORT                  = "S";
    private static final String SIG_INT                    = "I";
    private static final String SIG_LONG                   = "J";
    private static final String SIG_FLOAT                  = "F";
    private static final String SIG_DOUBLE                 = "D";
    private static final String SIG_ARRAY                  = "[";
    private static final String SIG_CLASS                  = "L";

    public TypeSignature(Types types) {
      this.types = types;
    }

    StringBuilder getParameterSignature(ExecutableElement mType, boolean useFlatname) {
      StringBuilder result = new StringBuilder();
      for (VariableElement v : mType.getParameters()) {
        result.append(getJvmSignature(v.asType(), useFlatname));
      }
      return result;
    }

    StringBuilder getReturnSignature(ExecutableElement mType) {
      return getJvmSignature(mType.getReturnType(), false);
    }

    StringBuilder getSignature(ExecutableElement mType) {
      StringBuilder sb = new StringBuilder();
      sb.append("(").append(getParameterSignature(mType, false)).append(")");
      sb.append(getReturnSignature(mType));
      return sb;
    }

    /*
     * Returns jvm internal signature.
     */
    static class JvmTypeVisitor extends SimpleTypeVisitor8<Type, StringBuilder> {

      private final Types types;

      JvmTypeVisitor(Types types) {
        super();
        this.types = types;
      }

      @Override
      public Type visitDeclared(DeclaredType t, StringBuilder stringBuilder) {
        setDeclaredType(t, stringBuilder);
        return null;
      }

      @Override
      public Type visitArray(ArrayType t, StringBuilder stringBuilder) {
        stringBuilder.append("[");
        return t.getComponentType().accept(this, stringBuilder);
      }

      @Override
      public Type visitPrimitive(PrimitiveType t, StringBuilder stringBuilder) {
        stringBuilder.append(getJvmPrimitiveSignature(t));
        return null;
      }

      private void setDeclaredType(TypeMirror t, StringBuilder s) {
        var tsym = ((TypeElement) types.asElement(t));
        String classname = tsym.getQualifiedName().toString();
        classname = classname.replace('.', '/');
        s.append("L").append(classname).append(";");
      }
      private String getJvmPrimitiveSignature(PrimitiveType t) {
        switch (t.getKind()) {
          case VOID:      return SIG_VOID;
          case BOOLEAN:   return SIG_BOOLEAN;
          case BYTE:      return SIG_BYTE;
          case CHAR:      return SIG_CHAR;
          case SHORT:     return SIG_SHORT;
          case INT:       return SIG_INT;
          case LONG:      return SIG_LONG;
          case FLOAT:     return SIG_FLOAT;
          case DOUBLE:    return SIG_DOUBLE;
          default:
            throw new IllegalArgumentException("unknown type: should not happen");
        }
      }
    }

    StringBuilder getJvmSignature(TypeMirror type, boolean useFlatname) {
      TypeMirror t = types.erasure(type);
      StringBuilder sig = new StringBuilder();
      JvmTypeVisitor jv = new JvmTypeVisitor(types);
      jv.visit(t, sig);
      return sig;
    }
  }
}
