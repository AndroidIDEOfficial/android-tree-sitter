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
import java.util.function.Consumer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import kotlin.Triple;

/**
 * @author Akash Yadav
 */
public class JNIWriter {

  private static final boolean isWindows = System.getProperty("os.name").startsWith("Windows");

  /* Signature Characters */
  private static final String SIG_VOID = "V";
  private static final String SIG_BOOLEAN = "Z";
  private static final String SIG_BYTE = "B";
  private static final String SIG_CHAR = "C";
  private static final String SIG_SHORT = "S";
  private static final String SIG_INT = "I";
  private static final String SIG_LONG = "J";
  private static final String SIG_FLOAT = "F";
  private static final String SIG_DOUBLE = "D";
  private static final String SIG_ARRAY = "[";
  private static final String SIG_CLASS = "L";

  private final StringWriter _headerWriter = new StringWriter();
  private final PrintWriter headerWriter = new PrintWriter(_headerWriter);

  private final StringWriter _sigWriter = new StringWriter();
  private final PrintWriter sigWriter = new PrintWriter(_sigWriter);


  private final Types types;
  private final TypeMirror stringType;
  private final TypeMirror throwableType;
  private final TypeMirror classType;
  private final String REGISTER_NATIVES = "registerNatives";

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
    includes(sigWriter);
    guardBegin(sigWriter, cname, "METHOD_SIGNATURES");
    writeMethodDefs(sigWriter, cname, type, sigs);
    guardEnd(sigWriter);

    return new Pair<>(_headerWriter.toString(), _sigWriter.toString());
  }

  private void writeMethodDefs(PrintWriter out, String cname, TypeElement type,
                               HashMap<String, String> sigs
  ) {
    final var typeName = new StringBuilder();
    Element curr = type;
    while (curr != null && curr.getKind().isClass()) {
      typeName.insert(0, "_").insert(0, curr.getSimpleName());
      curr = curr.getEnclosingElement();
    }

    final var defTypMth = typeName + "_METHODS";
    final var defTypMthCount = typeName + "_METHOD_COUNT";
    final var nameAndSigList = new ArrayList<Triple<String, String, String>>();

    var idx = 0;
    for (final var entry : sigs.entrySet()) {
      final var methodName = entry.getKey();
      final var methodSig = entry.getValue();

      if (REGISTER_NATIVES.equals(methodName)) {
        continue;
      }

      final var qualifiedMethodName = typeName + methodName;

      out.println();
      methodDoc(out, cname, methodName, methodSig);

      final var defArrIdx = qualifiedMethodName + "__ARR_IDX";

      out.print("#define ");
      out.print(defArrIdx);
      out.print(" ");
      out.println(idx++);

      // In Android, the JNINativeMethod structure has 'const' properties
      // While in JDK, it is not
      // So we disable the warning when compiling for non-Android machines
      jvmDisableWarning(out, "write-strings", o -> {
        o.print("static JNINativeMethod ");
        o.print(qualifiedMethodName);
        o.println("= {");
        o.print("    .name = \"");
        o.print(methodName);
        o.println("\",");
        o.print("    .signature = \"");
        o.print(methodSig);
        o.println("\",");
        o.println("    .fnPtr = nullptr");
        o.println("};");
      });

      nameAndSigList.add(new Triple<>(qualifiedMethodName, methodName, methodSig));
    }
    out.println();

    out.print("static JNINativeMethod ");
    out.print(defTypMth);
    out.print("[] = {");

    for (Triple<String, String, String> pair : nameAndSigList) {
      out.print(pair.getFirst());
      out.println(",");
    }

    out.println("};");

    out.print("#define ");
    out.print(defTypMthCount);
    out.print(" ");
    out.println(nameAndSigList.size());

    out.println();
    out.println("#ifndef SET_JNI_METHOD");
    out.print("#define SET_JNI_METHOD(_mth, _func) ");
    out.print(defTypMth);
    out.println("[_mth##__ARR_IDX].fnPtr = reinterpret_cast<void *>(&_func)");
    out.print("#endif");
    out.println();

    out.println();
    out.print("#define ");
    out.print(typeName);
    out.print("_RegisterNatives(_env, _class) (*_env).RegisterNatives(_class, ");
    out.print(defTypMth);
    out.print(", ");
    out.print(defTypMthCount);
    out.println(")");
    out.println();

    out.println();
  }

  private void jvmDisableWarning(PrintWriter out, String name, Consumer<PrintWriter> outConsumer) {
    out.println();
    out.println("#ifndef __ANDROID__");
    out.println();
    out.println("#ifdef __GNUC__");
    out.println("#pragma GCC diagnostic push");
    out.print("#pragma GCC diagnostic ignored \"-W");
    out.print(name);
    out.println("\"");
    out.println("#endif // __GNUC__");
    out.println();
    out.println("#ifdef __clang__");
    out.println("#pragma clang diagnostic push");
    out.print("#pragma clang diagnostic ignored \"-W");
    out.print(name);
    out.println("\"");
    out.println("#endif // __clang__");
    out.println();
    out.println("#endif // __ANDROID__");
    out.println();

    outConsumer.accept(out);

    out.println();
    out.println("#ifndef __ANDROID__");
    out.println();
    out.println("#ifdef __GNUC__");
    out.println("#pragma GCC diagnostic pop");
    out.println("#endif // __GNUC__");
    out.println();
    out.println("#ifdef __clang__");
    out.println("#pragma clang diagnostic pop");
    out.println("#endif // __clang__");
    out.println();
    out.println("#endif // __ANDROID__");
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

  void writeMethods(PrintWriter out, TypeElement sym, String cname, Map<String, String> methodSigs
  ) {
    List<? extends Element> classmethods = sym.getEnclosedElements();

    ExecutableElement regNtv = null;
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

      if (regNtv == null && REGISTER_NATIVES.contentEquals(methodName)) {
        regNtv = md;
        // validate registerNatives method
        if (!md.getModifiers().contains(Modifier.STATIC)) {
          err(REGISTER_NATIVES + " must be static, class=" + sym);
        }

        if (!md.getParameters().isEmpty()) {
          err(REGISTER_NATIVES + " must not have any parameters, class=" + sym);
        }

        if (md.getReturnType().getKind() != TypeKind.VOID) {
          err(
            REGISTER_NATIVES + " must return 'void', class=" + sym + ", typ=" + md.getReturnType() +
              ", typ.knd=" + md.getReturnType().getKind());
        }
      }

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

      final var criticalNative = md.getAnnotationMirrors()
        .stream()
        .filter(am -> ((TypeElement) am.getAnnotationType().asElement()).getQualifiedName()
          .contentEquals("dalvik.annotation.optimization.CriticalNative"))
        .findFirst();

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

    if (regNtv == null) {
      err("Class " + sym + " does not define '" + REGISTER_NATIVES +
        "' method. Please define method 'static native void " + REGISTER_NATIVES + "();'");
    }
  }

  private static void methodDoc(PrintWriter out, String cname, CharSequence methodName,
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
    var type = switch (t.getKind()) {
      case ARRAY -> switch (((ArrayType) t).getComponentType().getKind()) {
        case BOOLEAN -> "jbooleanArray";
        case BYTE -> "jbyteArray";
        case CHAR -> "jcharArray";
        case SHORT -> "jshortArray";
        case INT -> "jintArray";
        case LONG -> "jlongArray";
        case FLOAT -> "jfloatArray";
        case DOUBLE -> "jdoubleArray";
        default -> "jobjectArray";
      };
      case VOID -> "void";
      case BOOLEAN -> "jboolean";
      case BYTE -> "jbyte";
      case CHAR -> "jchar";
      case SHORT -> "jshort";
      case INT -> "jint";
      case LONG -> "jlong";
      case FLOAT -> "jfloat";
      case DOUBLE -> "jdouble";
      default -> null;
    };

    if (type != null) {
      return type;
    }

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
    String guardName = ("_Included_" + cname + "_" + type).toUpperCase(Locale.ROOT);
    out.print("#ifndef ");
    out.println(guardName);

    out.print("#define ");
    out.println(guardName);
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
        case CLASS -> {
          switch (ch) {
            case '.', '_' -> result.append("_");
            case '$' -> result.append("__");
            default -> result.append(encodeChar(ch));
          }
        }
        case JNI -> {
          switch (ch) {
            case '/', '.' -> result.append("_");
            case '_' -> result.append("_1");
            case ';' -> result.append("_2");
            case '[' -> result.append("_3");
            default -> result.append(encodeChar(ch));
          }
        }
        case SIGNATURE -> result.append(isprint(ch) ? ch : encodeChar(ch));
        case FIELDSTUB -> result.append(ch == '_' ? ch : encodeChar(ch));
        default -> result.append(encodeChar(ch));
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
    Types types;

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
        var sig = primitiveSigOrNull(t);
        if (sig != null) {
          return sig;
        }

        throw new IllegalArgumentException("unknown type: should not happen");
      }
    }

    StringBuilder getJvmSignature(TypeMirror type, boolean useFlatname) {
      StringBuilder sig = new StringBuilder();
      var s = primitiveSigOrNull(type);
      if (s != null) {
        sig.append(s);
        return sig;
      }

      TypeMirror t = types.erasure(type);
      JvmTypeVisitor jv = new JvmTypeVisitor(types);
      jv.visit(t, sig);

      return sig;
    }
  }

  static String primitiveSigOrNull(TypeMirror type) {
    return switch (type.getKind()) {
      case VOID -> SIG_VOID;
      case BOOLEAN -> SIG_BOOLEAN;
      case BYTE -> SIG_BYTE;
      case CHAR -> SIG_CHAR;
      case SHORT -> SIG_SHORT;
      case INT -> SIG_INT;
      case LONG -> SIG_LONG;
      case FLOAT -> SIG_FLOAT;
      case DOUBLE -> SIG_DOUBLE;
      default -> null;
    };
  }


  static void err(String msg) {
    throw new RuntimeException(msg);
  }
}
